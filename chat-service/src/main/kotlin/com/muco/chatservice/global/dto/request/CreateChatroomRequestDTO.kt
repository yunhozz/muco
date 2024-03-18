package com.muco.chatservice.global.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateChatroomRequestDTO(
    @JsonProperty("partnerId")
    @field:NotNull
    val partnerId: Long?,

    @JsonProperty("name")
    @field:NotBlank
    val name: String?
)
