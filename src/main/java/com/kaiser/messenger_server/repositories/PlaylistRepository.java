package com.kaiser.messenger_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

}