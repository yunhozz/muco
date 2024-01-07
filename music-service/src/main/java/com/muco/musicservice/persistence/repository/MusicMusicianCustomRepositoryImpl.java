package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicSimpleQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
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

    @Override
    public List<MusicSimpleQueryDTO> getMusicSimpleListByKeywordAndCategory(String keyword, SearchCategory category) {
        List<MusicSimpleQueryDTO> musicList = queryFactory
                .select(new QMusicSimpleQueryDTO(
                        music.id,
                        music.name,
                        musician.nickname,
                        music.likeCount
                ))
                .from(musicMusician)
                .join(musicMusician.music, music)
                .join(musicMusician.musician, musician)
                .where(keywordContainsBySearchCategory(keyword, category))
                .fetch();

        sortByNumberOfKeywords(keyword, musicList);

        return musicList;
    }

    private BooleanExpression musicRankingGt(Integer cursorRank) {
        return cursorRank != null ? music.ranking.gt(cursorRank) : null;
    }

    private BooleanExpression musicNameContainsBy(String keyword) {
        return keyword != null ? music.name.contains(keyword) : null;
    }

    private BooleanExpression musicianNameContainsBy(String keyword) {
        return keyword != null ? musician.nickname.contains(keyword) : null;
    }

    private BooleanExpression lyricsContainsBy(String keyword) {
        return keyword != null ? music.lyrics.contains(keyword) : null;
    }

    private BooleanExpression keywordContainsBySearchCategory(String keyword, SearchCategory category) {
        BooleanExpression expression = null;
        if (category != null) {
            switch (category) {
                case MUSIC -> expression = musicNameContainsBy(keyword);
                case MUSICIAN -> expression = musicianNameContainsBy(keyword);
                case LYRICS -> expression = lyricsContainsBy(keyword);
                default -> expression =
                        musicNameContainsBy(keyword)
                                .or(musicianNameContainsBy(keyword))
                                .or(lyricsContainsBy(keyword));
            }
        }

        return expression;
    }

    private static void sortByNumberOfKeywords(String keyword, List<MusicSimpleQueryDTO> musicList) {
        musicList.sort(Comparator.comparingInt(music -> {
            String musicName = music.getMusicName();
            return musicName.length() - musicName.replace(keyword, "").length();
        }));
    }
}