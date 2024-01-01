package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.query.QMusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.LyricsSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicianSimpleQueryDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.muco.musicservice.persistence.entity.QMusic.music;
import static com.muco.musicservice.persistence.entity.QMusicMusician.musicMusician;
import static com.muco.musicservice.persistence.entity.QMusician.musician;

@Repository
@RequiredArgsConstructor
public class MusicMusicianCustomRepositoryImpl implements MusicMusicianCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<MusicChartQueryDTO> musicList = queryFactory
                .select(new QMusicChartQueryDTO(
                        music.id,
                        musician.id,
                        music.name,
                        musician.nickname,
                        music.ranking,
                        music.likeCount,
                        music.imageUrl
                ))
                .from(musicMusician)
                .join(musicMusician.music, music)
                .join(musicMusician.musician, musician)
                .where(musicRankingGt(cursorRank))
                .orderBy(music.ranking.asc())
                .limit(pageSize + 1)
                .limit(100)
                .fetch();

        boolean hasNext = false;
        if (musicList.size() > pageSize) {
            musicList.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(musicList, pageable, hasNext);
    }

    private BooleanExpression musicRankingGt(Integer cursorRank) {
        return cursorRank != null ? music.ranking.gt(cursorRank) : null;
    }
}