package com.kaiser.messenger_server.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.entities.Playlist;
import com.kaiser.messenger_server.entities.User;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String>, JpaSpecificationExecutor<Playlist> {
    List<Playlist> findByUser(User user);
}