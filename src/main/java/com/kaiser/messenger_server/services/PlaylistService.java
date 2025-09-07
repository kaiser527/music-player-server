package com.kaiser.messenger_server.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.playlist.PlaylistFilterRequest;
import com.kaiser.messenger_server.dto.request.playlist.PlaylistRequest;
import com.kaiser.messenger_server.dto.response.PlaylistResponse;
import com.kaiser.messenger_server.dto.response.share.PaginatedResponse;
import com.kaiser.messenger_server.entities.Playlist;
import com.kaiser.messenger_server.entities.Track;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.mapper.PlaylistMapper;
import com.kaiser.messenger_server.repositories.PlaylistRepository;
import com.kaiser.messenger_server.repositories.TrackRepository;
import com.kaiser.messenger_server.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class PlaylistService {
    PlaylistRepository playlistRepository;
    PlaylistMapper playlistMapper;
    TrackRepository trackRepository;
    UserRepository userRepository;

    public PlaylistResponse createPlaylist(PlaylistRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Playlist playlist = playlistMapper.toCreatePlaylist(request);

        playlist.setCreatedBy(authentication.getName());
        playlist.setUser(user);

        playlistRepository.save(playlist);

        return playlistMapper.toPlaylistResponse(playlist);
    }

    public PaginatedResponse<PlaylistResponse> getPlaylistPaginated(Pageable pageable, PlaylistFilterRequest filter){
        Specification<Playlist> spec = (root, query, cb) -> cb.conjunction();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")
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

        Page<Playlist> playlistPage = playlistRepository.findAll(spec, sortedPageable);

        List<PlaylistResponse> playlistReponse = playlistPage.getContent()
            .stream()
            .map(playlistMapper::toPlaylistResponse)
            .toList();

        return PaginatedResponse.<PlaylistResponse>builder()
            .pageNumber(playlistPage.getNumber() + 1)
            .pageSize(playlistPage.getSize())
            .totalPages(playlistPage.getTotalPages())
            .data(playlistReponse)
            .build();
    }

    public PlaylistResponse updatePlaylist(String id, PlaylistRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXIST));

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        request.getTrackIds().forEach(item -> {
            if(!trackRepository.existsById(item)){
                throw new AppException(ErrorCode.TRACK_NOT_EXIST);
            }
        });

        List<Track> tracks = trackRepository.findAllById(request.getTrackIds());

        playlistMapper.toUpdatePlaylist(playlist, request);

        playlist.setUpdatedBy(authentication.getName());
        playlist.setTrack(new HashSet<Track>(tracks));
        playlist.setUser(user);

        playlistRepository.save(playlist);

        return playlistMapper.toPlaylistResponse(playlist);
    }

    public PlaylistResponse deletePlaylist(String id){
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXIST));

        playlistRepository.delete(playlist);

        return playlistMapper.toPlaylistResponse(playlist);
    }

    public List<PlaylistResponse> getGlobalPlaylist(){
        List<Playlist> playlists = playlistRepository.findByUser(null);

        List<PlaylistResponse> playlistResponses = playlists.stream()
            .map(playlistMapper::toPlaylistResponse)
            .toList();

        return playlistResponses;
    }

    public PaginatedResponse<PlaylistResponse> getUserPlaylist(Pageable pageable, String name){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        List<Playlist> userPlaylists = playlistRepository.findByUser(user);
        List<Playlist> globalPlaylists = playlistRepository.findByUser(null);

        String search = name.toLowerCase();

        List<PlaylistResponse> combined = Stream.concat(userPlaylists.stream(), globalPlaylists.stream())
            .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(search))
            .map(playlistMapper::toPlaylistResponse)
            .toList();

        int total = combined.size();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<PlaylistResponse> paged = fromIndex >= total ? List.of() : combined.subList(fromIndex, toIndex);

        return PaginatedResponse.<PlaylistResponse>builder()
            .pageNumber(pageNumber + 1)
            .pageSize(pageSize)
            .totalPages((int) Math.ceil((double) total / pageSize))
            .data(paged)
            .build();
    }

    public void bulkDeletePlaylist(List<String> playlistIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        playlistIds.forEach(item -> {
            if(!playlistRepository.existsById(item)){
                throw new AppException(ErrorCode.PLAYLIST_NOT_EXIST);
            }
        });

        List<Playlist> playlists = playlistRepository.findAllById(playlistIds);
        playlists.forEach(item -> {
            if(!item.getUser().getEmail().equals(authentication.getName())){
                throw new AppException(ErrorCode.NOT_OWNED_PLAYLIST);
            }
        });

        playlistRepository.deleteAllById(playlistIds);
    }
}
