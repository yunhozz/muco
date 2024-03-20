package com.muco.chatservice.global.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class UpdateChatroomRequestDTO(
    @JsonProperty("name")
    @field:NotBlank
    val name: String
)
