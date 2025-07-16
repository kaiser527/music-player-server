package com.kaiser.messenger_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.dto.request.TrackRequest;
import com.kaiser.messenger_server.dto.response.TrackResponse;
import com.kaiser.messenger_server.entities.Track;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "user", ignore = true)
    Track toCreateTrack(TrackRequest request);

    TrackResponse toTrackResponse(Track track);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toUpdateTrack(@MappingTarget Track track, TrackRequest request);
}
