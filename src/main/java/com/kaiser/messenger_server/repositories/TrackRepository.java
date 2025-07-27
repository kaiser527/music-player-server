package com.kaiser.messenger_server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, String> {
    boolean existsByTitleOrUrl(String title, String url);

    boolean existsByTitleAndUrlAndIdNot(String title, String url, String id);

    Page<Track> findByTitleContainingIgnoreCase(String title, Pageable pageable);
} 
