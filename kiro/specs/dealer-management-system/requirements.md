# Requirements Document

## Introduction

The Dealer Management System (DMS) is a production-grade Spring Boot (Java) backend for a multi-dealership automotive business. It covers authentication and authorization, vehicle inventory, customer lifecycle management, sales orders, payments, test drives, service operations, notifications, and audit logging. The system is designed to be secure, scalable, and observable, with a React TypeScript frontend consuming its REST API.

---

## Glossary

- **DMS**: Dealer Management System — the backend application described in this document.
- **Dealership**: A registered automotive dealership entity in the system.
- **User**: An authenticated system actor with one of the defined roles (super_admin, admin, manager, salesperson, technician, receptionist).
- **Customer**: A person tracked by the dealership as a lead, prospect, active customer, or lost contact.
- **Inventory**: A vehicle record held by a dealership, identified by a unique VIN.
- **Order**: A sales transaction linking a customer, salesperson, and one or more order items.
- **Order_Item**: A line item within an order (vehicle, accessory, insurance, warranty, fee, or other).
- **Payment**: A financial transaction recorded against an order.
- **Test_Drive**: A scheduled or completed vehicle test drive event.
- **Service_Ticket**: A service or repair job opened for a customer's vehicle.
- **Service_Item**: A part, labor entry, consumable, or external charge within a service ticket.
- **Audit_Log**: An immutable record of system actions (CRUD, auth events, password changes).
- **Inventory_History**: A field-level change log for inventory records.
- **Notification**: An in-app message delivered to a specific user.
- **Refresh_Token**: A long-lived opaque token stored as a SHA-256 hash, used to obtain new access tokens.
- **Access_Token**: A short-lived (15-minute) stateless JWT carrying user identity and role claims.
- **JWT**: JSON Web Token used for stateless authentication.
- **RBAC**: Role-Based Access Control — the authorization model used by the DMS.
- **HikariCP**: The JDBC connection pool used by Spring Boot for database connections.
- **BCrypt**: The password hashing algorithm (cost ≥ 12) used to store user passwords.
- **Pageable**: A Spring Data abstraction for paginated, sorted query results.

---

## Requirements

---

### Requirement 1: User Authentication

**User Story:** As a user, I want to log in with my email and password, so that I can securely access the DMS.

#### Acceptance Criteria

1. WHEN a login request is received with a valid email and password, THE DMS SHALL return a signed JWT access token (15-minute expiry) and a refresh token (7-day expiry).
2. WHEN a login request is received, THE DMS SHALL verify the submitted password against the BCrypt hash stored in `users.password_hash` with a cost factor of at least 12.
3. WHEN a login request is received for a user whose `locked_until` timestamp is in the future, THE DMS SHALL reject the request with HTTP 423 and a message indicating the lockout expiry time.
4. WHEN a login attempt fails due to incorrect credentials, THE DMS SHALL increment `users.failed_login_attempts` by 1.
5. WHEN `users.failed_login_attempts` reaches 5, THE DMS SHALL set `users.locked_until` to the current timestamp plus 15 minutes.
6. WHEN a login attempt succeeds, THE DMS SHALL reset `users.failed_login_attempts` to 0 and update `users.last_login` to the current timestamp.
7. WHEN any login attempt occurs (success or failure), THE DMS SHALL write a record to `audit_logs` with action `LOGIN` or `LOGIN_FAILED`, the user's IP address, and user-agent string.
8. WHEN a login request is received for a user whose `status` is `inactive` or `suspended`, THE DMS SHALL reject the request with HTTP 403.

---

### Requirement 2: JWT Token Management

**User Story:** As a user, I want my session to be refreshed automatically, so that I do not have to log in repeatedly during active use.

#### Acceptance Criteria

1. THE DMS SHALL store only the SHA-256 hex hash of each refresh token in `refresh_tokens.token_hash`; the raw token SHALL NOT be persisted.
2. WHEN a token-refresh request is received with a valid, non-revoked, non-expired refresh token, THE DMS SHALL issue a new access token and a new refresh token, and SHALL revoke the old refresh token by setting `is_revoked = 1` and `revoked_at` to the current timestamp.
3. WHEN a token-refresh request is received with a revoked or expired refresh token, THE DMS SHALL reject the request with HTTP 401.
4. WHEN a logout request is received, THE DMS SHALL revoke all active refresh tokens for the requesting user.
5. THE DMS SHALL embed `user_id`, `role`, and `dealership_id` as claims in every issued access token.
6. WHEN a protected endpoint is called without a valid access token, THE DMS SHALL return HTTP 401.

---

### Requirement 3: Role-Based Access Control

**User Story:** As a system administrator, I want each role to have clearly defined permissions, so that users can only perform actions appropriate to their role.

#### Acceptance Criteria

1. THE DMS SHALL enforce the following role hierarchy for access control: `super_admin` > `admin` > `manager` > `salesperson` / `technician` / `receptionist`.
2. WHEN a `salesperson`, `technician`, or `receptionist` attempts to access another dealership's data, THE DMS SHALL reject the request with HTTP 403.
3. WHEN a `manager` attempts to access data outside their assigned dealership, THE DMS SHALL reject the request with HTTP 403.
4. THE DMS SHALL permit only `super_admin` and `admin` roles to create, update, or deactivate dealership records.
5. THE DMS SHALL permit only `super_admin`, `admin`, and `manager` roles to create or modify user accounts.
6. THE DMS SHALL permit `salesperson` and above to create and update customer records within their dealership.
7. THE DMS SHALL permit only `admin`, `manager`, and `super_admin` to view audit logs.
8. WHEN a user attempts an action not permitted by their role, THE DMS SHALL return HTTP 403 with a descriptive error message.

---

### Requirement 4: Password Management

**User Story:** As a user, I want to change my password securely, so that I can maintain account security.

#### Acceptance Criteria

1. WHEN a password-change request is received with a valid current password, THE DMS SHALL hash the new password with BCrypt (cost ≥ 12) and update `users.password_hash` and `users.password_changed_at`.
2. WHEN a password-change request is received with an incorrect current password, THE DMS SHALL reject the request with HTTP 400.
3. WHEN a password is changed, THE DMS SHALL revoke all existing refresh tokens for that user.
4. WHEN a password is changed, THE DMS SHALL write a record to `audit_logs` with action `PASSWORD_CHANGE`.
5. THE DMS SHALL reject passwords shorter than 8 characters or lacking at least one uppercase letter, one digit, and one special character, returning HTTP 400 with a descriptive validation message.

---

### Requirement 5: Dealership Management

**User Story:** As an admin, I want to manage dealership records, so that the system accurately reflects the organization's branch structure.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and soft-delete (set `is_active = 0`) dealership records.
2. WHEN a dealership creation request is received with a duplicate `email` or `license_no`, THE DMS SHALL reject the request with HTTP 409 and identify the conflicting field.
3. WHEN a dealership record is created or updated, THE DMS SHALL validate that `email` is a well-formed email address and `phone` matches a 10–15 digit pattern.
4. THE DMS SHALL support searching dealerships by `name`, `city`, `state`, and `is_active` status.
5. THE DMS SHALL return dealership list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
6. WHEN a dealership update or delete is performed, THE DMS SHALL write a record to `audit_logs` with the old and new field values as JSON.

---

### Requirement 6: User Management

**User Story:** As a manager, I want to manage user accounts within my dealership, so that staff can access the system with appropriate permissions.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and change the `status` of user accounts.
2. WHEN a user creation request is received, THE DMS SHALL hash the provided password with BCrypt (cost ≥ 12) before persisting.
3. WHEN a user creation request is received with a duplicate `email`, THE DMS SHALL reject the request with HTTP 409.
4. THE DMS SHALL support filtering users by `dealership_id`, `role`, `status`, and searching by `first_name`, `last_name`, or `email`.
5. THE DMS SHALL return user list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
6. THE DMS SHALL never include `password_hash` or `failed_login_attempts` in any API response body.
7. WHEN a user's `status` is changed to `suspended`, THE DMS SHALL revoke all active refresh tokens for that user.
8. WHEN a user record is created, updated, or status-changed, THE DMS SHALL write a record to `audit_logs`.

---

### Requirement 7: Vehicle Inventory Management

**User Story:** As a salesperson, I want to manage vehicle inventory, so that I can track available vehicles and their details.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and delete inventory records within a dealership.
2. WHEN an inventory record is created with a duplicate `vin`, THE DMS SHALL reject the request with HTTP 409.
3. WHEN an inventory record is created or updated, THE DMS SHALL validate that `selling_price` is greater than 0 and `year` is between 1900 and the current calendar year plus 1.
4. THE DMS SHALL support filtering inventory by `make`, `model`, `year`, `fuel_type`, `transmission`, `body_type`, `condition_type`, `status`, and price range (`cost_price`, `selling_price`).
5. THE DMS SHALL support full-text searching of inventory by `make`, `model`, and `variant`.
6. THE DMS SHALL support sorting inventory by `selling_price`, `year`, `mileage`, and `intake_date`.
7. THE DMS SHALL return inventory list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
8. WHEN an inventory field is updated, THE DMS SHALL write a record to `inventory_history` capturing `field_name`, `old_value`, `new_value`, `changed_by`, and `reason`.
9. WHEN an inventory record's `status` is set to `sold`, THE DMS SHALL prevent further status changes except by `admin` or `manager` roles.
10. THE DMS SHALL cache frequently accessed inventory list queries using Spring Cache with a TTL of 5 minutes, and SHALL evict the cache on any inventory write operation.

---

### Requirement 8: Customer Management

**User Story:** As a salesperson, I want to manage customer records, so that I can track leads and maintain customer relationships.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and delete customer records within a dealership.
2. WHEN a customer record is created or updated, THE DMS SHALL validate that `phone` is present and matches a 10–15 digit pattern.
3. THE DMS SHALL support filtering customers by `status` (lead, prospect, customer, lost), `source`, `assigned_to`, and `dealership_id`.
4. THE DMS SHALL support searching customers by `first_name`, `last_name`, `email`, and `phone`.
5. THE DMS SHALL support sorting customers by `created_at`, `last_name`, and `status`.
6. THE DMS SHALL return customer list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
7. WHEN a `salesperson` requests the customer list, THE DMS SHALL return only customers assigned to that salesperson or unassigned customers within their dealership.
8. WHEN a customer record is created, updated, or deleted, THE DMS SHALL write a record to `audit_logs`.

---

### Requirement 9: Test Drive Management

**User Story:** As a receptionist or salesperson, I want to schedule and track test drives, so that customers can evaluate vehicles before purchase.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and cancel test drive records.
2. WHEN a test drive is scheduled for an inventory item whose `status` is not `available` or `test_drive`, THE DMS SHALL reject the request with HTTP 409.
3. WHEN a test drive is scheduled, THE DMS SHALL set the inventory item's `status` to `test_drive`.
4. WHEN a test drive `status` is set to `completed` or `cancelled`, THE DMS SHALL set the inventory item's `status` back to `available`.
5. WHEN a test drive `status` is set to `completed`, THE DMS SHALL require `odometer_after` to be greater than or equal to `odometer_before`.
6. THE DMS SHALL support filtering test drives by `status`, `customer_id`, `inventory_id`, `staff_id`, and `scheduled_at` date range.
7. THE DMS SHALL return test drive list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
8. WHEN a test drive is created or its status changes, THE DMS SHALL create a `notification` record for the assigned staff member.

---

### Requirement 10: Order Management

**User Story:** As a salesperson, I want to create and manage sales orders, so that vehicle purchases are tracked from draft to delivery.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and cancel orders.
2. WHEN an order is created, THE DMS SHALL generate a unique `order_number` using a deterministic format (e.g., `ORD-{YYYYMMDD}-{sequence}`).
3. WHEN an order is created or updated, THE DMS SHALL compute `tax_amount` as `(subtotal - discount_amount) * (tax_rate / 100)` and `total_amount` as `subtotal - discount_amount + tax_amount - trade_in_value`.
4. WHEN an order `status` is set to `confirmed`, THE DMS SHALL set the `status` of each linked vehicle inventory item to `reserved`.
5. WHEN an order `status` is set to `delivered`, THE DMS SHALL set the `status` of each linked vehicle inventory item to `sold`.
6. WHEN an order `status` is set to `cancelled`, THE DMS SHALL set the `status` of each linked vehicle inventory item back to `available` and require a `cancellation_reason`.
7. THE DMS SHALL support filtering orders by `status`, `customer_id`, `salesperson_id`, `dealership_id`, and `order_date` range.
8. THE DMS SHALL support sorting orders by `order_date`, `total_amount`, and `status`.
9. THE DMS SHALL return order list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
10. WHEN an order is created, updated, or cancelled, THE DMS SHALL write a record to `audit_logs`.

---

### Requirement 11: Order Item Management

**User Story:** As a salesperson, I want to add line items to an order, so that all products and services in a sale are captured.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to add, update, and remove order items from an order in `draft` status.
2. WHEN an order item is added or updated, THE DMS SHALL compute `line_total` as `(unit_price * quantity) - discount`.
3. WHEN an order item is added or removed, THE DMS SHALL recompute the parent order's `subtotal`, `tax_amount`, and `total_amount`.
4. WHEN an order item of type `vehicle` is added, THE DMS SHALL validate that the referenced `inventory_id` belongs to the same dealership as the order.
5. WHEN an attempt is made to modify order items on an order whose `status` is not `draft`, THE DMS SHALL reject the request with HTTP 409.

---

### Requirement 12: Payment Management

**User Story:** As a manager, I want to record and track payments against orders, so that the financial status of each sale is accurate.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, and update payment records linked to an order.
2. WHEN a payment record is created, THE DMS SHALL validate that `amount` is greater than 0.
3. THE DMS SHALL support filtering payments by `order_id`, `status`, and `method`.
4. THE DMS SHALL return payment list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
5. WHEN a payment `status` is set to `completed`, THE DMS SHALL write a record to `audit_logs` with the payment amount and method.
6. WHEN the sum of completed payments for an order equals or exceeds `orders.total_amount`, THE DMS SHALL update the order `status` to `processing` if it is currently `confirmed`.

---

### Requirement 13: Service Ticket Management

**User Story:** As a technician or receptionist, I want to manage service tickets, so that vehicle repairs and maintenance are tracked end-to-end.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to create, read, update, and cancel service tickets.
2. WHEN a service ticket is created, THE DMS SHALL generate a unique `ticket_number` using a deterministic format (e.g., `SVC-{YYYYMMDD}-{sequence}`).
3. WHEN a service ticket `status` is set to `in_progress`, THE DMS SHALL require `diagnosis` to be non-empty.
4. WHEN a service ticket `status` is set to `completed`, THE DMS SHALL require `completed_at` to be set and `customer_approval` to be `1`.
5. WHEN a service ticket `status` is set to `delivered`, THE DMS SHALL require `delivered_at` to be set and `odometer_out` to be greater than or equal to `odometer_in`.
6. THE DMS SHALL support filtering service tickets by `status`, `priority`, `service_type`, `assigned_to`, `customer_id`, and `dealership_id`.
7. THE DMS SHALL support sorting service tickets by `drop_off_at`, `promised_at`, `priority`, and `total_cost`.
8. THE DMS SHALL return service ticket list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
9. WHEN a service ticket is created or its `status` changes, THE DMS SHALL create a `notification` record for the assigned technician.
10. WHEN a service ticket is created, updated, or cancelled, THE DMS SHALL write a record to `audit_logs`.

---

### Requirement 14: Service Item Management

**User Story:** As a technician, I want to add parts and labor entries to a service ticket, so that the cost of each job is accurately captured.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to add, update, and remove service items from a service ticket.
2. WHEN a service item is added or updated, THE DMS SHALL compute `line_total` as `unit_cost * quantity`.
3. WHEN a service item is added, updated, or removed, THE DMS SHALL recompute the parent ticket's `total_parts` (sum of part/consumable/external items), `total_labor` (sum of labor items), and `total_cost`.
4. WHEN an attempt is made to modify service items on a ticket whose `status` is `completed`, `cancelled`, or `delivered`, THE DMS SHALL reject the request with HTTP 409.

---

### Requirement 15: Audit Log Management

**User Story:** As an admin, I want to query audit logs, so that I can investigate security events and data changes.

#### Acceptance Criteria

1. THE DMS SHALL provide a read-only endpoint to query `audit_logs`.
2. THE DMS SHALL support filtering audit logs by `user_id`, `dealership_id`, `action`, `table_name`, `record_id`, and `created_at` date range.
3. THE DMS SHALL support sorting audit logs by `created_at` descending by default.
4. THE DMS SHALL return audit log responses as paginated results with configurable `page`, `size`, and `sort` parameters.
5. THE DMS SHALL restrict audit log access to users with `admin` or `super_admin` roles.
6. THE DMS SHALL write audit log entries asynchronously to avoid adding latency to the originating request.

---

### Requirement 16: Notification Management

**User Story:** As a user, I want to receive and manage in-app notifications, so that I am informed of relevant events without leaving the system.

#### Acceptance Criteria

1. THE DMS SHALL provide endpoints to list, mark as read, and delete notifications for the authenticated user.
2. WHEN a notification list is requested, THE DMS SHALL support filtering by `is_read` status.
3. THE DMS SHALL return notification list responses as paginated results with configurable `page`, `size`, and `sort` parameters.
4. WHEN a mark-as-read request is received, THE DMS SHALL set `is_read = 1` and `read_at` to the current timestamp for the specified notification.
5. THE DMS SHALL only allow a user to access, mark, or delete their own notifications.

---

### Requirement 17: Inventory History

**User Story:** As a manager, I want to view the change history of an inventory record, so that I can audit price changes and status transitions.

#### Acceptance Criteria

1. THE DMS SHALL provide a read-only endpoint to list `inventory_history` records for a given `inventory_id`.
2. THE DMS SHALL return inventory history responses as paginated results sorted by `created_at` descending.
3. THE DMS SHALL restrict inventory history access to `manager`, `admin`, and `super_admin` roles.

---

### Requirement 18: Validation and Error Handling

**User Story:** As an API consumer, I want consistent, descriptive error responses, so that I can handle failures gracefully in the frontend.

#### Acceptance Criteria

1. WHEN a request body fails Bean Validation constraints, THE DMS SHALL return HTTP 400 with a JSON body listing each invalid field and its violation message.
2. WHEN a requested resource is not found, THE DMS SHALL return HTTP 404 with a JSON body containing `status`, `error`, `message`, and `timestamp` fields.
3. WHEN a database constraint violation occurs (duplicate key, foreign key), THE DMS SHALL return HTTP 409 with a descriptive message and SHALL NOT expose raw SQL error text.
4. WHEN an unhandled exception occurs, THE DMS SHALL return HTTP 500 with a generic error message and SHALL log the full stack trace at ERROR level.
5. THE DMS SHALL use a global `@ControllerAdvice` exception handler to ensure all error responses follow a uniform JSON structure.
6. IF a path variable or request parameter cannot be parsed to its expected type, THEN THE DMS SHALL return HTTP 400 with a descriptive message.

---

### Requirement 19: Pagination, Sorting, and Filtering

**User Story:** As an API consumer, I want all list endpoints to support pagination, sorting, and filtering, so that large datasets are navigable efficiently.

#### Acceptance Criteria

1. THE DMS SHALL accept `page` (0-based), `size` (default 20, max 100), and `sort` (field,direction) query parameters on all list endpoints.
2. WHEN `size` exceeds 100, THE DMS SHALL cap it at 100 and return the capped result without an error.
3. THE DMS SHALL return paginated responses in a uniform envelope containing `content`, `page`, `size`, `totalElements`, `totalPages`, and `last` fields.
4. WHEN an invalid `sort` field is provided, THE DMS SHALL return HTTP 400 with a message identifying the unsupported field.
5. THE DMS SHALL apply all active filters as AND conditions unless otherwise specified.

---

### Requirement 20: Caching

**User Story:** As a system operator, I want frequently read data to be cached, so that database load is reduced and response times are improved.

#### Acceptance Criteria

1. THE DMS SHALL use Spring Cache (backed by Caffeine or Redis) to cache inventory list query results with a TTL of 5 minutes.
2. WHEN an inventory record is created, updated, or deleted, THE DMS SHALL evict all affected inventory cache entries.
3. THE DMS SHALL set HTTP `Cache-Control: max-age=300, public` headers on inventory list responses to enable browser/CDN caching.
4. THE DMS SHALL cache dealership lookup results (by ID) with a TTL of 10 minutes and evict on update.
5. WHERE Redis is configured as the cache provider, THE DMS SHALL use Redis as the cache backend; WHERE Redis is not configured, THE DMS SHALL fall back to Caffeine in-process cache.

---

### Requirement 21: Logging and Observability

**User Story:** As a system operator, I want structured, leveled application logs, so that I can diagnose issues in production.

#### Acceptance Criteria

1. THE DMS SHALL log all inbound HTTP requests at INFO level, including method, URI, response status, and duration in milliseconds.
2. THE DMS SHALL log all unhandled exceptions at ERROR level with the full stack trace.
3. THE DMS SHALL include a correlation ID (UUID) in every log line and propagate it in the `X-Correlation-Id` response header.
4. THE DMS SHALL use SLF4J with Logback and output logs in JSON format when the `prod` Spring profile is active.
5. THE DMS SHALL never log `password_hash`, raw tokens, or any field named `password` at any log level.
6. THE DMS SHALL log cache hit and miss events at DEBUG level.

---

### Requirement 22: Performance and Scalability

**User Story:** As a system operator, I want the API to perform reliably under load, so that dealership staff experience consistent response times.

#### Acceptance Criteria

1. WHEN handling a paginated list request with default page size, THE DMS SHALL return a response within 500 ms under a load of 50 concurrent users.
2. THE DMS SHALL use HikariCP with a maximum pool size of 10 connections (configurable via `application.properties`).
3. THE DMS SHALL use Spring Data JPA with indexed columns for all filter and sort fields to avoid full-table scans.
4. THE DMS SHALL use `@Async` for audit log writes and notification creation to avoid blocking the request thread.
5. THE DMS SHALL be stateless (no server-side HTTP session) so that multiple instances can run behind a load balancer without sticky sessions.

---

### Requirement 23: Security Hardening

**User Story:** As a security-conscious operator, I want the API to follow security best practices, so that the system is resistant to common attacks.

#### Acceptance Criteria

1. THE DMS SHALL set the following HTTP security headers on all responses: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Strict-Transport-Security: max-age=31536000`.
2. THE DMS SHALL configure CORS to allow only explicitly whitelisted origins.
3. THE DMS SHALL disable Spring Security's default CSRF protection for stateless JWT-authenticated endpoints.
4. WHEN a JWT access token's `exp` claim is in the past, THE DMS SHALL reject the request with HTTP 401.
5. THE DMS SHALL sanitize all user-supplied string inputs to prevent stored XSS by stripping HTML tags before persistence.
6. THE DMS SHALL enforce HTTPS in production by rejecting plain HTTP requests (configurable via profile).
