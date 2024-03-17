package com.muco.chatservice.global.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateChatroomRequestDTO(
    @field:NotNull
    val partnerId: Long?,
    @field:NotBlank
    val name: String?
)
