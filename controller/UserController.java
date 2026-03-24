package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.UserFilter;
import com.hyundai.DMS.dto.request.UserRequest;
import com.hyundai.DMS.dto.request.UserStatusRequest;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.dto.response.UserResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> list(
            UserFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(userService.listUsers(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request,
                                                @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id,
                                                 @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(userService.getUser(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UserRequest request,
                                                @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(userService.updateUser(id, request, principal));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(@PathVariable Long id,
                                                      @Valid @RequestBody UserStatusRequest request,
                                                      @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(userService.changeStatus(id, request.getStatus(), principal));
    }
}
