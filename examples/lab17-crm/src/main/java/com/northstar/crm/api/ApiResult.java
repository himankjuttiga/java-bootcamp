package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerResponseDTO;
import com.northstar.crm.exception.ErrorResponse;

/**
 * Facade channel: a call returns Ok with a response DTO or Fail with an
 * ErrorResponse, never both, and never lets an exception escape to the caller
 * (Module 16 lab convention).
 */
public sealed interface ApiResult permits ApiResult.Ok, ApiResult.Fail {

    record Ok(CustomerResponseDTO body) implements ApiResult {}

    record Fail(ErrorResponse error) implements ApiResult {}

    default boolean isOk() {
        return this instanceof Ok;
    }
}
