package com.muco.chatservice.global.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateChatroomRequestDTO(
    @NotNull
    val partnerId: Long,
    @NotBlank
    val name: String
)
