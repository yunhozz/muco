package com.muco.musicservice.domain.interfaces;

import com.muco.musicservice.domain.application.PlaylistService;
import com.muco.musicservice.domain.interfaces.dto.ResponseDTO;
import com.muco.musicservice.global.dto.request.CreatePlaylistRequestDTO;
import com.muco.musicservice.global.dto.request.UpdatePlaylistRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseDTO createPlaylist(@RequestHeader String sub, @Valid @RequestBody CreatePlaylistRequestDTO dto) {
        Long playlistId = playlistService.createPlaylist(sub, dto);
        return ResponseDTO.of("플레이리스트 생성이 완료되었습니다.", playlistId, Long.class);
    }

    @PutMapping
    public ResponseDTO addMusicOnPlaylist(@RequestParam String id, @RequestParam String... musicIds) {
        playlistService.addMusicOnPlaylist(id, musicIds);
        return ResponseDTO.of("음원을 성공적으로 추가하였습니다.");
    }

    @PatchMapping
    public ResponseDTO updatePlaylist(@RequestParam String playlistId, @Valid @RequestBody UpdatePlaylistRequestDTO dto) {
        playlistService.updatePlaylist(playlistId, dto);
        return ResponseDTO.of("해당 플레이리스트에 대한 업데이트를 완료했습니다.");
    }

    @DeleteMapping("/music")
    public ResponseDTO deleteMusicFromPlaylist(@RequestParam String playlistId, @RequestParam String... musicIds) {
        playlistService.deleteMusicFromPlaylist(playlistId, musicIds);
        return ResponseDTO.of("음원을 성공적으로 제거하였습니다.");
    }

    @DeleteMapping
    public ResponseDTO deletePlaylist(@RequestParam String id) {
        playlistService.deletePlaylist(id);
        return ResponseDTO.of("해당 플레이리스트를 성공적으로 제거하였습니다.");
    }
}
