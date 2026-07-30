package com.northstar.crm.dto;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import java.time.LocalDateTime;

public final class CustomerMapper {

    private CustomerMapper() {}

    public static Customer toEntity(CustomerRequestDTO dto) {
        return new Customer(
                dto.getCustomerId(),
                dto.getFullName(),
                dto.getEmail(),
                null,                                  // phone not on the DTO
                CustomerStatus.valueOf(dto.getStatus()),
                LocalDateTime.now()
        );
    }

    public static CustomerResponseDTO toResponse(Customer entity) {
        return CustomerResponseDTO.of(
                entity.getCustomerId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus().name(),
                entity.getCreatedAt()
        );
    }
}