package com.muco.musicservice.domain.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.domain.application.MusicManagementService;
import com.muco.musicservice.domain.interfaces.dto.CreateMusicSimpleRequestDTO;
import com.muco.musicservice.domain.interfaces.dto.ResponseDTO;
import com.muco.musicservice.domain.interfaces.dto.UserInfoClientDTO;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.response.FileResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/music/manage")
@RequiredArgsConstructor
public class MusicManagementController {

    private final MusicManagementService musicManagementService;

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseDTO> createMusic(@Valid @ModelAttribute CreateMusicSimpleRequestDTO dto, HttpServletRequest request) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .defaultHeader(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION)
                .build();

        return webClient.get()
                .uri("/api/users/{id}", request.getHeader("sub"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ResponseDTO.class)
                .flatMap(responseDTO -> {
                    UserInfoClientDTO userInfoClientDTO = new ObjectMapper().convertValue(responseDTO.data(), UserInfoClientDTO.class);
                    CreateMusicRequestDTO createMusicRequestDTO = CreateMusicRequestDTO.builder()
                            .userId(Long.parseLong(userInfoClientDTO.id()))
                            .email(userInfoClientDTO.email())
                            .age(Integer.parseInt(userInfoClientDTO.age()))
                            .nickname(userInfoClientDTO.nickname())
                            .userImageUrl(userInfoClientDTO.imageUrl())
                            .musicName(dto.name())
                            .genres(dto.genres())
                            .lyrics(dto.lyrics())
                            .music(dto.music())
                            .image(dto.image())
                            .build();
                    Long result = musicManagementService.registerMusic(createMusicRequestDTO);
                    return Mono.just(ResponseDTO.of("음악을 성공적으로 등록하였습니다.", result, Long.class));
                });
    }

    @GetMapping("/{id}/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadMusic(@PathVariable String id) {
        FileResponseDTO fileResponseDTO = musicManagementService.downloadMusic(Long.parseLong(id));
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(UriUtils.encode(fileResponseDTO.fileName(), StandardCharsets.UTF_8))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);
        headers.setContentType(MediaType.valueOf(fileResponseDTO.contentType()));

        return new ResponseEntity<>(fileResponseDTO.resource(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> displayMusicImage(@PathVariable String id) {
        FileResponseDTO fileResponseDTO = musicManagementService.displayMusicImage(Long.parseLong(id));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(fileResponseDTO.contentType()));

        return new ResponseEntity<>(fileResponseDTO.resource(), headers, HttpStatus.OK);
    }
}