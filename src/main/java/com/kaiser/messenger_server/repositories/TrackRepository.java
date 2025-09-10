package com.kaiser.messenger_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, String>, JpaSpecificationExecutor<Track> {
    boolean existsByTitleOrUrl(String title, String url);
    
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Track t " +
           "WHERE (t.title = :title OR t.url = :url) " +
           "AND t.id <> :id")
    boolean existsByTitleOrUrlAndIdNot(String title, String url, String id);

    boolean existsByArtwork(String artwork);

    boolean existsByUrl(String url);
} 
