package com.muco.authservice.persistence.repo;

import com.muco.authservice.persistence.query.QUserInfoQueryDTO;
import com.muco.authservice.persistence.query.UserInfoQueryDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.muco.authservice.persistence.entity.QUser.user;
import static com.muco.authservice.persistence.entity.QUserProfile.userProfile;

@Repository
@RequiredArgsConstructor
public class UserProfileCustomRepositoryImpl implements UserProfileCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserInfoQueryDTO> findUserInfoById(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .select(new QUserInfoQueryDTO(
                                user.id,
                                userProfile.email,
                                userProfile.age,
                                userProfile.nickname,
                                userProfile.imageUrl
                        ))
                        .from(userProfile)
                        .join(userProfile.user, user)
                        .where(user.id.eq(id))
                        .fetchOne()
        );
    }
}