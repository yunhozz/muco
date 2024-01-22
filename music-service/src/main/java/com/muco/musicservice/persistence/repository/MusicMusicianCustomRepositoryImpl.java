package com.muco.musicservice.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicianSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicianSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicianSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicianSimpleQueryDTO;
import com.muco.musicservice.global.dto.response.query.QPlaylistSearchQueryDTO;
import com.muco.musicservice.global.dto.response.query.QPlaylistSimpleQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchCondition;
import com.muco.musicservice.global.util.ListSorter;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.muco.musicservice.persistence.entity.QMusic.music;
import static com.muco.musicservice.persistence.entity.QMusicMusician.musicMusician;
import static com.muco.musicservice.persistence.entity.QMusician.musician;
import static com.muco.musicservice.persistence.entity.QUserPlaylist.userPlaylist;

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
    public List<MusicSimpleQueryDTO> getMusicSimpleListByKeyword(String keyword) {
        return queryFactory
                .select(new QMusicSimpleQueryDTO(
                        music.id,
                        music.name,
                        musician.nickname,
                        music.likeCount
                ))
                .from(musicMusician)
                .join(musicMusician.music, music)
                .join(musicMusician.musician, musician)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSIC))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) // mysql에서 NumberExpression.random().asc() 동작 x
                .limit(10)
                .fetch();
    }

    @Override
    public List<MusicianSimpleQueryDTO> getMusicianSimpleListByKeyword(String keyword) {
        return queryFactory
                .select(new QMusicianSimpleQueryDTO(
                        musician.id,
                        musician.nickname,
                        musician.likeCount,
                        musician.imageUrl
                ))
                .from(musician)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSICIAN))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) // mysql에서 NumberExpression.random().asc() 동작 x
                .limit(10)
                .fetch();
    }

    @Override
    public List<PlaylistSimpleQueryDTO> getPlaylistSimpleListByKeyword(String keyword) {
        return queryFactory
                .select(new QPlaylistSimpleQueryDTO(
                        userPlaylist.id,
                        userPlaylist.name,
                        userPlaylist.likeCount
                ))
                .from(userPlaylist)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.PLAYLIST))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) // mysql에서 NumberExpression.random().asc() 동작 x
                .limit(10)
                .fetch();
    }

    @Override
    public Page<MusicSearchQueryDTO> getMusicSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable) {
        List<MusicSearchQueryDTO> musicSearchList;
        JPAQuery<MusicSearchQueryDTO> musicSearchQuery = queryFactory
                .select(new QMusicSearchQueryDTO(
                        music.id,
                        musician.id,
                        music.name,
                        musician.nickname,
                        music.imageUrl,
                        music.playCount,
                        music.likeCount,
                        music.createdAt
                ))
                .from(musicMusician)
                .join(musicMusician.music, music)
                .join(musicMusician.musician, musician)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSIC));

        if (condition.equals(SearchCondition.ACCURACY)) {
            musicSearchList = musicSearchQuery.fetch();
            ListSorter<MusicSearchQueryDTO> musicSorter = (key, list) -> list.stream()
                    .sorted(Comparator.comparing(MusicSearchQueryDTO::musicName, (n1, n2) -> ListSorter.compareByNumberOfKeywords(n1, n2, key, false)))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            musicSearchList = musicSorter.sort(keyword, musicSearchList);
        } else {
            musicSearchList = musicSearchQuery
                    .orderBy(createMusicOrderSpecifierByCondition(condition))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        Long total = queryFactory
                .select(musicMusician.count())
                .from(musicMusician)
                .join(musicMusician.music, music)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSIC))
                .fetchOne();

        return new PageImpl<>(musicSearchList, pageable, total);
    }

    @Override
    public Page<MusicianSearchQueryDTO> getMusicianSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable) {
        List<MusicianSearchQueryDTO> musicianSearchList;
        JPAQuery<MusicianSearchQueryDTO> musicianSearchQuery = queryFactory
                .select(new QMusicianSearchQueryDTO(
                        musician.id,
                        musician.nickname,
                        musician.likeCount,
                        musician.imageUrl
                ))
                .from(musician)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSICIAN));

        if (condition.equals(SearchCondition.ACCURACY)) {
            musicianSearchList = musicianSearchQuery.fetch();
            ListSorter<MusicianSearchQueryDTO> musicianSorter = (key, list) -> list.stream()
                    .sorted(Comparator.comparing(MusicianSearchQueryDTO::name, (n1, n2) -> ListSorter.compareByNumberOfKeywords(n1, n2, key, false)))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            musicianSearchList = musicianSorter.sort(keyword, musicianSearchList);
        } else {
            musicianSearchList = musicianSearchQuery
                    .orderBy(createMusicianOrderSpecifierByCondition(condition))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        Long total = queryFactory
                .select(musician.count())
                .from(musician)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.MUSICIAN))
                .fetchOne();

        return new PageImpl<>(musicianSearchList, pageable, total);
    }

    @Override
    public Page<PlaylistSearchQueryDTO> getPlaylistSearchPageByKeywordAndConditions(String keyword, SearchCondition condition, Pageable pageable) {
        List<PlaylistSearchQueryDTO> playListSearchList;
        JPAQuery<PlaylistSearchQueryDTO> playListSearchQuery = queryFactory
                .select(new QPlaylistSearchQueryDTO(
                        userPlaylist.id,
                        userPlaylist.name,
                        userPlaylist.likeCount
                ))
                .from(userPlaylist)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.PLAYLIST));

        if (condition.equals(SearchCondition.ACCURACY)) {
            playListSearchList = playListSearchQuery.fetch();
            ListSorter<PlaylistSearchQueryDTO> playlistSorter = (key, list) -> list.stream()
                    .sorted(Comparator.comparing(PlaylistSearchQueryDTO::name, (n1, n2) -> ListSorter.compareByNumberOfKeywords(n1, n2, key, false)))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            playListSearchList = playlistSorter.sort(keyword, playListSearchList);
        } else {
            playListSearchList = playListSearchQuery
                    .orderBy(createPlayListOrderSpecifierByCondition(condition))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        Long total = queryFactory
                .select(userPlaylist.count())
                .from(userPlaylist)
                .where(keywordContainsBySearchCategory(keyword, SearchCategory.PLAYLIST))
                .fetchOne();

        return new PageImpl<>(playListSearchList, pageable, total);
    }

    private BooleanExpression keywordContainsBySearchCategory(String keyword, SearchCategory category) {
        BooleanExpression expression = null;
        if (category != null) {
            switch (category) {
                case MUSIC -> expression = musicNameContainsBy(keyword);
                case MUSICIAN -> expression = musicianNameContainsBy(keyword);
                case PLAYLIST -> expression = playlistNameContainsBy(keyword);
                default -> expression =
                        musicNameContainsBy(keyword)
                                .or(musicianNameContainsBy(keyword))
                                .or(playlistNameContainsBy(keyword));
            }
        }

        return expression;
    }

    private OrderSpecifier<?>[] createMusicOrderSpecifierByCondition(SearchCondition condition) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (condition) {
            case LATEST -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, music.createdAt));
            case ASCEND -> orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, music.name));
            case POPULARITY -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, music.likeCount));
            default -> {
                return null;
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] createMusicianOrderSpecifierByCondition(SearchCondition condition) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (condition) {
            case LATEST -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, musician.createdAt));
            case ASCEND -> orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, musician.nickname));
            case POPULARITY -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, musician.likeCount));
            default -> {
                return null;
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] createPlayListOrderSpecifierByCondition(SearchCondition condition) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (condition) {
            case LATEST -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, userPlaylist.createdAt));
            case ASCEND -> orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, userPlaylist.name));
            case POPULARITY -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, userPlaylist.likeCount));
            default -> {
                return null;
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
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

    private BooleanExpression playlistNameContainsBy(String keyword) {
        return keyword != null ? userPlaylist.name.contains(keyword) : null;
    }
}