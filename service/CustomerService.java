package com.hyundai.DMS.service;

import com.hyundai.DMS.domain.entity.Customer;
import com.hyundai.DMS.domain.entity.Dealership;
import com.hyundai.DMS.domain.entity.User;
import com.hyundai.DMS.domain.enums.CustomerStatus;
import com.hyundai.DMS.domain.enums.Role;
import com.hyundai.DMS.dto.request.CustomerFilter;
import com.hyundai.DMS.dto.request.CustomerRequest;
import com.hyundai.DMS.dto.response.CustomerResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.exception.ForbiddenException;
import com.hyundai.DMS.exception.ResourceNotFoundException;
import com.hyundai.DMS.repository.CustomerRepository;
import com.hyundai.DMS.repository.DealershipRepository;
import com.hyundai.DMS.repository.UserRepository;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.specification.CustomerSpecification;
import com.hyundai.DMS.util.HtmlSanitizer;
import com.hyundai.DMS.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final DealershipRepository dealershipRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final HtmlSanitizer htmlSanitizer;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, DmsUserDetails principal) {
        Dealership dealership = dealershipRepository.findById(request.getDealershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Dealership not found"));

        if (!principal.isSuperAdmin() && !dealership.getId().equals(principal.getDealershipId())) {
            throw new ForbiddenException("Cannot create customer in another dealership");
        }

        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
        }

        Customer customer = Customer.builder()
                .dealership(dealership)
                .firstName(htmlSanitizer.sanitize(request.getFirstName()))
                .lastName(htmlSanitizer.sanitize(request.getLastName()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(request.getStatus() != null ? request.getStatus() : CustomerStatus.LEAD)
                .source(htmlSanitizer.sanitize(request.getSource()))
                .assignedTo(assignedTo)
                .notes(htmlSanitizer.sanitize(request.getNotes()))
                .build();

        customer = customerRepository.save(customer);
        auditService.log(principal.getUserId(), dealership.getId(), "CREATE_CUSTOMER",
                "customers", customer.getId(), null, null, null, null);

        return CustomerResponse.from(customer);
    }

    public PagedResponse<CustomerResponse> listCustomers(CustomerFilter filter, Pageable pageable, DmsUserDetails principal) {
        // Enforce dealership scope
        if (!principal.isSuperAdmin()) {
            filter.setDealershipId(principal.getDealershipId());
        }

        Specification<Customer> spec = Specification
                .where(CustomerSpecification.hasDealership(filter.getDealershipId()))
                .and(CustomerSpecification.hasStatus(filter.getStatus()))
                .and(CustomerSpecification.hasSource(filter.getSource()))
                .and(CustomerSpecification.search(filter.getSearch()));

        // SALESPERSON sees only own + unassigned customers
        if (principal.getRole() == Role.SALESPERSON) {
            spec = spec.and(CustomerSpecification.assignedToOrUnassigned(principal.getUserId()));
        } else if (filter.getAssignedToUserId() != null) {
            spec = spec.and(CustomerSpecification.assignedTo(filter.getAssignedToUserId()));
        }

        return PaginationUtils.toPagedResponse(
                customerRepository.findAll(spec, pageable).map(CustomerResponse::from)
        );
    }

    public CustomerResponse getCustomer(Long id, DmsUserDetails principal) {
        Customer customer = findById(id);
        enforceScope(customer, principal);
        return CustomerResponse.from(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request, DmsUserDetails principal) {
        Customer customer = findById(id);
        enforceScope(customer, principal);

        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
        }

        customer.setFirstName(htmlSanitizer.sanitize(request.getFirstName()));
        customer.setLastName(htmlSanitizer.sanitize(request.getLastName()));
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        if (request.getStatus() != null) customer.setStatus(request.getStatus());
        customer.setSource(htmlSanitizer.sanitize(request.getSource()));
        customer.setAssignedTo(assignedTo);
        customer.setNotes(htmlSanitizer.sanitize(request.getNotes()));

        customer = customerRepository.save(customer);
        auditService.log(principal.getUserId(), customer.getDealership().getId(), "UPDATE_CUSTOMER",
                "customers", customer.getId(), null, null, null, null);

        return CustomerResponse.from(customer);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    @Transactional
    public void deleteCustomer(Long id, DmsUserDetails principal) {
        Customer customer = findById(id);
        enforceScope(customer, principal);
        customerRepository.delete(customer);
        auditService.log(principal.getUserId(), customer.getDealership().getId(), "DELETE_CUSTOMER",
                "customers", customer.getId(), null, null, null, null);
    }

    private void enforceScope(Customer customer, DmsUserDetails principal) {
        if (principal.isSuperAdmin()) return;
        if (!customer.getDealership().getId().equals(principal.getDealershipId())) {
            throw new ForbiddenException("Access denied to customer in another dealership");
        }
    }

    private Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
