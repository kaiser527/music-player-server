package com.kaiser.messenger_server.modules.track;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.annotations.IgnoreAuditableFields;
import com.kaiser.messenger_server.modules.track.dto.TrackRequest;
import com.kaiser.messenger_server.modules.track.dto.TrackResponse;
import com.kaiser.messenger_server.modules.track.entity.Track;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    @IgnoreAuditableFields
    @Mapping(target = "user", ignore = true)
    Track toCreateTrack(TrackRequest request);

    TrackResponse toTrackResponse(Track track);

    @IgnoreAuditableFields
    @Mapping(target = "user", ignore = true)
    void toUpdateTrack(@MappingTarget Track track, TrackRequest request);
}
