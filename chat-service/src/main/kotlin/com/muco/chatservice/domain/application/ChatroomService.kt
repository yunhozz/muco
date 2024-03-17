package com.muco.chatservice.domain.application

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

    fun makeChatroom(userId: Long, dto: CreateChatroomRequestDTO): Mono<Chatroom> =
        chatroomRepository.save(Chatroom(name = dto.name!!))
            .flatMap { chatroom ->
                val chatroomId = chatroom.id!!
                val me = ChatroomUser(chatroomId = chatroomId, userId = userId)
                val partner = ChatroomUser(chatroomId = chatroomId, userId = dto.partnerId!!)

                chatroomUserRepository.saveAll(listOf(me, partner))
                    .then(Mono.just(chatroom))
            }

    fun updateChatroom(chatroomId: Long, dto: UpdateChatroomRequestDTO): Mono<Chatroom> =
        findChatroomById(chatroomId)
            .flatMap { chatroom ->
                chatroom.updateName(dto.name)
                    .then(chatroomRepository.save(chatroom))
            }

    fun findAllChatroomList(): Flux<Chatroom> = chatroomRepository.findAll()

    fun deleteChatroom(chatroomId: Long): Mono<Void> =
        findChatroomById(chatroomId)
            .flatMap { chatroom ->
                chatroomUserRepository.findAllByChatroomId(chatroomId = chatroom.id!!)
                    .flatMap { chatroomUser ->
                        chatRepository.deleteAllByChatroomId(chatroomId = chatroomUser.chatroomId)
                    }
                    .then(chatroomRepository.deleteById(chatroom.id))
            }

    private fun findChatroomById(id: Long): Mono<Chatroom> =
        chatroomRepository.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("해당 채팅방을 찾을 수 없습니다. chatroom ID = $id")))
}