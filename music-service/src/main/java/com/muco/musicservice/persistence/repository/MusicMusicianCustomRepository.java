package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.LyricsSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicianSimpleQueryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MusicMusicianCustomRepository {

    Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable);
}