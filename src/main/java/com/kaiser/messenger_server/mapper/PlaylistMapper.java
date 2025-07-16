package com.kaiser.messenger_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.dto.request.PlaylistRequest;
import com.kaiser.messenger_server.dto.response.PlaylistResponse;
import com.kaiser.messenger_server.entities.Playlist;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "user", ignore = true)
    Playlist toCreatePlaylist(PlaylistRequest request);

    PlaylistResponse toPlaylistResponse(Playlist playlist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toUpdatePlaylist(@MappingTarget Playlist playlist, PlaylistRequest request);
}
