package com.kaiser.messenger_server.modules.playlist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.annotations.IgnoreAuditableFields;
import com.kaiser.messenger_server.modules.playlist.dto.PlaylistRequest;
import com.kaiser.messenger_server.modules.playlist.dto.PlaylistResponse;
import com.kaiser.messenger_server.modules.playlist.entity.Playlist;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @IgnoreAuditableFields
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "user", ignore = true)
    Playlist toCreatePlaylist(PlaylistRequest request);

    PlaylistResponse toPlaylistResponse(Playlist playlist);

    @IgnoreAuditableFields
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toUpdatePlaylist(@MappingTarget Playlist playlist, PlaylistRequest request);
}
