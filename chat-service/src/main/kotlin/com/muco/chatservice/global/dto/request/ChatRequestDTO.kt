package com.muco.chatservice.global.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class ChatRequestDTO(
    @JsonProperty("content")
    @field:NotNull
    val content: String
)
