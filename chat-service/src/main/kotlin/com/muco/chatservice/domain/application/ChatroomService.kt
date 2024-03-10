package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
import com.muco.chatservice.domain.persistence.entity.Chatroom
import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import com.muco.chatservice.domain.persistence.repository.ChatRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomUserRepository
import com.muco.chatservice.global.dto.request.CreateChatroomRequestDTO
import com.muco.chatservice.global.dto.request.UpdateChatroomRequestDTO
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatroomService(
    private val chatRepository: ChatRepository,
    private val chatroomRepository: ChatroomRepository,
    private val chatroomUserRepository: ChatroomUserRepository
) {

    fun makeChatroom(userId: Long, dto: CreateChatroomRequestDTO): Mono<Chatroom> {
        return chatroomRepository.save(Chatroom(name = dto.name))
            .flatMap { chatroom ->
                val chatroomUser = ChatroomUser(chatroomId = chatroom.id!!, userId = userId)
                chatroomUserRepository.save(chatroomUser)
                    .thenReturn(chatroom)
            }
    }

    fun updateChatroom(chatroomId: Long, dto: UpdateChatroomRequestDTO): Mono<Chatroom> {
        return findChatroomById(chatroomId)
            .flatMap { chatroom ->
                chatroom.updateName(dto.name)
                    .then(chatroomRepository.save(chatroom))
            }
    }

    fun findAllChatroomList(): Flux<Chatroom> = chatroomRepository.findAll()

    fun findChatroomDetails(chatroomId: Long): Flux<Chat> {
        return findChatroomById(chatroomId)
            .flatMapMany { chatroom ->
                chatRepository.findAllByChatroomId(chatroom.id)
            }
    }

    fun deleteChatroom(chatroomId: Long): Mono<Void> {
        return findChatroomById(chatroomId)
            .flatMap { chatroom ->
                chatRepository.deleteAllByChatroomId(chatroomId = chatroom.id)
                    .then(chatroomRepository.deleteById(chatroom.id!!))
            }
    }

    private fun findChatroomById(id: Long): Mono<Chatroom> {
        return chatroomRepository.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("해당 채팅방을 찾을 수 없습니다. chatroom ID = $id")))
    }
}