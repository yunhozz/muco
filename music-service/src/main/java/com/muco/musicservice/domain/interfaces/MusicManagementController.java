package com.muco.musicservice.domain.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muco.musicservice.domain.application.MusicManagementService;
import com.muco.musicservice.domain.interfaces.dto.CreateMusicSimpleRequestDTO;
import com.muco.musicservice.domain.interfaces.dto.ResponseDTO;
import com.muco.musicservice.domain.interfaces.dto.UserInfoClientDTO;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.response.FileResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/music/manage")
@RequiredArgsConstructor
public class MusicManagementController {

    private final MusicManagementService musicManagementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createMusic(@Valid @RequestBody CreateMusicSimpleRequestDTO dto, MultipartHttpServletRequest request) throws JsonProcessingException {
        String userInfoRequestUri = UriComponentsBuilder
                .fromUriString("http://localhost:8000/api/users/{id}")
                .build()
                .expand(dto.userId())
                .encode().toString();

        ResponseEntity<String> response = new RestTemplate().getForEntity(userInfoRequestUri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        UserInfoClientDTO userInfo = objectMapper.convertValue(responseDTO.data(), UserInfoClientDTO.class);

        CreateMusicRequestDTO createMusicRequestDTO = CreateMusicRequestDTO.builder()
                .userId(Long.parseLong(userInfo.id()))
                .email(userInfo.email())
                .age(Integer.parseInt(userInfo.age()))
                .nickname(userInfo.nickname())
                .userImageUrl(userInfo.imageUrl())
                .genres(dto.genres())
                .lyrics(dto.lyrics())
                .build();

        Long musicId = musicManagementService.registerMusic(createMusicRequestDTO, request.getFile("music"));

        return ResponseDTO.of("음악을 성공적으로 등록하였습니다.", musicId, Long.class);
    }

    @GetMapping("/{id}/download")
    public ResponseDTO downloadMusic(@PathVariable String id, HttpServletResponse response) {
        FileResponseDTO fileResponseDTO = musicManagementService.downloadMusic(Long.parseLong(id));
        String contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileResponseDTO.fileName(), StandardCharsets.UTF_8)
                .build().toString();

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        response.setHeader(HttpHeaders.CONTENT_TYPE, fileResponseDTO.contentType());

        return ResponseDTO.of("해당 음원을 다운로드합니다.", fileResponseDTO.resource(), Resource.class);
    }
}