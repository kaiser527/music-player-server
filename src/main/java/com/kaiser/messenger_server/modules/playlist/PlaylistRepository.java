package com.kaiser.messenger_server.modules.playlist;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.kaiser.messenger_server.modules.playlist.entity.Playlist;
import com.kaiser.messenger_server.modules.user.entity.User;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String>, JpaSpecificationExecutor<Playlist> {
    List<Playlist> findByUser(User user);
}