package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MusicMusicianCustomRepository {

    Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable);
    List<MusicSimpleQueryDTO> getMusicSimpleListByMusicName(String keyword);
    List<MusicSimpleQueryDTO> getMusicSimpleListByMusicianName(String keyword);
}