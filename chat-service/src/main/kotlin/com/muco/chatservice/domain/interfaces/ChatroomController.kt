package com.muco.chatservice.domain.interfaces

import com.muco.chatservice.domain.application.ChatroomService
import com.muco.chatservice.domain.interfaces.dto.ResponseDTO
import com.muco.chatservice.global.dto.request.CreateChatroomRequestDTO
import com.muco.chatservice.global.dto.request.UpdateChatroomRequestDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/chatroom")
class ChatroomController(
    private val chatroomService: ChatroomService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun makeChatroom(@RequestHeader sub: String, @Valid @RequestBody dto: CreateChatroomRequestDTO): Mono<ResponseDTO> =
        chatroomService.makeChatroom(sub.toLong(), dto)
            .flatMap { chatroom ->
                ResponseDTO.of("채팅방 생성이 완료되었습니다.", chatroom)
            }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    fun getChatroomList(@RequestHeader sub: String): Mono<ResponseDTO> =
        chatroomService.findChatroomListByUserId(sub.toLong())
            .collectList()
            .flatMap { chatroom ->
                ResponseDTO.of("채팅방 목록 조회 결과입니다.", chatroom)
            }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun changeChatroomInfo(@RequestParam id: String, @Valid @RequestBody dto: UpdateChatroomRequestDTO): Mono<ResponseDTO> =
        chatroomService.updateChatroom(id.toLong(), dto)
            .flatMap { chatroom->
                ResponseDTO.of("채팅방 수정이 완료되었습니다.", chatroom)
            }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteChatroom(@PathVariable id: String): Mono<ResponseDTO> =
        chatroomService.deleteChatroom(id.toLong())
            .then(Mono.defer {
                ResponseDTO.of("해당 채팅방을 성공적으로 삭제하였습니다.")
            })
}