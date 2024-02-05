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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/music/manage")
@RequiredArgsConstructor
public class MusicManagementController {

    private final MusicManagementService musicManagementService;

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createMusic(@Valid @ModelAttribute CreateMusicSimpleRequestDTO dto) throws JsonProcessingException {
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
                .musicName(dto.name())
                .genres(dto.genres())
                .lyrics(dto.lyrics())
                .music(dto.music())
                .image(dto.image())
                .build();

        Long musicId = musicManagementService.registerMusic(createMusicRequestDTO);

        return ResponseDTO.of("음악을 성공적으로 등록하였습니다.", musicId, Long.class);
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