package com.hyundai.DMS.service;

import com.hyundai.DMS.domain.entity.Dealership;
import com.hyundai.DMS.dto.request.DealershipFilter;
import com.hyundai.DMS.dto.request.DealershipRequest;
import com.hyundai.DMS.dto.response.DealershipResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.exception.ConflictException;
import com.hyundai.DMS.exception.ResourceNotFoundException;
import com.hyundai.DMS.repository.DealershipRepository;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.specification.DealershipSpecification;
import com.hyundai.DMS.util.HtmlSanitizer;
import com.hyundai.DMS.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DealershipService {

    private final DealershipRepository dealershipRepository;
    private final AuditService auditService;
    private final HtmlSanitizer htmlSanitizer;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Transactional
    public DealershipResponse createDealership(DealershipRequest request, DmsUserDetails principal) {
        if (dealershipRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Dealership with this email already exists");
        }
        if (dealershipRepository.existsByLicenseNo(request.getLicenseNo())) {
            throw new ConflictException("Dealership with this license number already exists");
        }

        Dealership dealership = Dealership.builder()
                .name(htmlSanitizer.sanitize(request.getName()))
                .email(request.getEmail().toLowerCase())
                .phone(request.getPhone())
                .address(htmlSanitizer.sanitize(request.getAddress()))
                .city(htmlSanitizer.sanitize(request.getCity()))
                .state(htmlSanitizer.sanitize(request.getState()))
                .licenseNo(request.getLicenseNo())
                .isActive(true)
                .build();

        dealership = dealershipRepository.save(dealership);
        auditService.log(principal.getUserId(), dealership.getId(), "CREATE_DEALERSHIP",
                "dealerships", dealership.getId(), null, null, null, null);

        return DealershipResponse.from(dealership);
    }

    @Cacheable(value = "dealershipById", key = "#id")
    public DealershipResponse getDealership(Long id) {
        return DealershipResponse.from(findById(id));
    }

    public PagedResponse<DealershipResponse> listDealerships(DealershipFilter filter, Pageable pageable) {
        Specification<Dealership> spec = Specification
                .where(DealershipSpecification.hasName(filter.getName()))
                .and(DealershipSpecification.hasCity(filter.getCity()))
                .and(DealershipSpecification.hasState(filter.getState()))
                .and(DealershipSpecification.isActive(filter.getIsActive()));

        return PaginationUtils.toPagedResponse(
                dealershipRepository.findAll(spec, pageable).map(DealershipResponse::from)
        );
    }

    @CacheEvict(value = "dealershipById", key = "#id")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Transactional
    public DealershipResponse updateDealership(Long id, DealershipRequest request, DmsUserDetails principal) {
        Dealership dealership = findById(id);

        if (!dealership.getEmail().equalsIgnoreCase(request.getEmail())
                && dealershipRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Dealership with this email already exists");
        }
        if (!dealership.getLicenseNo().equals(request.getLicenseNo())
                && dealershipRepository.existsByLicenseNo(request.getLicenseNo())) {
            throw new ConflictException("Dealership with this license number already exists");
        }

        dealership.setName(htmlSanitizer.sanitize(request.getName()));
        dealership.setEmail(request.getEmail().toLowerCase());
        dealership.setPhone(request.getPhone());
        dealership.setAddress(htmlSanitizer.sanitize(request.getAddress()));
        dealership.setCity(htmlSanitizer.sanitize(request.getCity()));
        dealership.setState(htmlSanitizer.sanitize(request.getState()));
        dealership.setLicenseNo(request.getLicenseNo());

        dealership = dealershipRepository.save(dealership);
        auditService.log(principal.getUserId(), dealership.getId(), "UPDATE_DEALERSHIP",
                "dealerships", dealership.getId(), null, null, null, null);

        return DealershipResponse.from(dealership);
    }

    @CacheEvict(value = "dealershipById", key = "#id")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Transactional
    public void deleteDealership(Long id, DmsUserDetails principal) {
        Dealership dealership = findById(id);
        dealership.setIsActive(false);
        dealershipRepository.save(dealership);
        auditService.log(principal.getUserId(), dealership.getId(), "DELETE_DEALERSHIP",
                "dealerships", dealership.getId(), null, null, null, null);
    }

    private Dealership findById(Long id) {
        return dealershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealership not found with id: " + id));
    }
}
