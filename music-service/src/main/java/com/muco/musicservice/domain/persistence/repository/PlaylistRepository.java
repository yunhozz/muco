package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
