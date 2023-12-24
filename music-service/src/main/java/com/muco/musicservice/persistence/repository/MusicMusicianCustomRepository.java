package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.query.MusicChartQueryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MusicMusicianCustomRepository {

    Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable);
}