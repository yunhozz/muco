package com.muco.chatservice.domain.persistence.repository;

import com.muco.chatservice.domain.persistence.entity.Chat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChatRepository extends R2dbcRepository<Chat, Long> {
}
