package com.muco.chatservice.domain.persistence.repository

import com.muco.chatservice.domain.persistence.entity.Chat
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ChatRepository : R2dbcRepository<Chat, Long>
