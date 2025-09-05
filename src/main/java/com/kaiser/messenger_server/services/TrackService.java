package com.kaiser.messenger_server.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.TrackFilterRequest;
import com.kaiser.messenger_server.dto.request.TrackRequest;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.TrackResponse;
import com.kaiser.messenger_server.entities.Track;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.TrackMapper;
import com.kaiser.messenger_server.repositories.TrackRepository;
import com.kaiser.messenger_server.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class TrackService {
    TrackRepository trackRepository;
    TrackMapper trackMapper;
    UserRepository userRepository;

    public PaginatedResponse<TrackResponse> getTrackPaginated(Pageable pageable, TrackFilterRequest filter){
        Specification<Track> spec = (root, query, cb) -> cb.conjunction();
        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%")
            );
        }
        if (filter.getUser() != null && !filter.getUser().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("user").get("username")), "%" + filter.getUser().toLowerCase() + "%")
            );
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            LocalDateTime start = filter.getStartDate().atStartOfDay();             
            LocalDateTime end = filter.getEndDate().atTime(LocalTime.MAX);

            spec = spec.and((root, q, cb) ->
                cb.between(root.get("createdAt"), start, end)
            );
        }

        Sort sort = Sort.unsorted();
        if (filter.getSortByCreatedAt() != null) {
            sort = sort.and(Sort.by(filter.getSortByCreatedAt() ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt"));
        }
        if (filter.getSortByUpdatedAt() != null) {
            sort = sort.and(Sort.by(filter.getSortByUpdatedAt() ? Sort.Direction.ASC : Sort.Direction.DESC, "updatedAt"));
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Track> trackPage = trackRepository.findAll(spec, sortedPageable);

        List<TrackResponse> playlistReponse = trackPage.getContent()
            .stream()
            .map(trackMapper::toTrackResponse)
            .toList();

        return PaginatedResponse.<TrackResponse>builder()
            .pageNumber(trackPage.getNumber() + 1)
            .pageSize(trackPage.getSize())
            .totalPages(trackPage.getTotalPages())
            .data(playlistReponse)
            .build();
    }

    public TrackResponse createTrack(TrackRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if(trackRepository.existsByTitleOrUrl(request.getTitle(), request.getUrl())){
            throw new AppException(ErrorCode.TRACK_EXIST);
        }

        Track track = trackMapper.toCreateTrack(request);

        track.setUser(user);
        track.setCreatedBy(authentication.getName());

        trackRepository.save(track);

        return trackMapper.toTrackResponse(track);
    }

    public TrackResponse updateTrack(String id, TrackRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Track track = trackRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TRACK_NOT_EXIST));

        if(trackRepository.existsByTitleOrUrlAndIdNot(request.getTitle(), request.getUrl(), id)){
            throw new AppException(ErrorCode.TRACK_EXIST);
        }

        trackMapper.toUpdateTrack(track, request);

        track.setUser(user);
        track.setUpdatedBy(authentication.getName());

        trackRepository.save(track);

        return trackMapper.toTrackResponse(track);
    }

    public TrackResponse getTrackById(String id){
        Track track = trackRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TRACK_NOT_EXIST));

        return trackMapper.toTrackResponse(track);
    }

    public TrackResponse deleteTrack(String id){
        Track track = trackRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TRACK_NOT_EXIST));

        trackRepository.delete(track);

        return trackMapper.toTrackResponse(track);
    }
}
