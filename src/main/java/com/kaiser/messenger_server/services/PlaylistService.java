package com.kaiser.messenger_server.services;

import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kaiser.messenger_server.dto.request.PlaylistRequest;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.PlaylistResponse;
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

    public PaginatedResponse<PlaylistResponse> getPlaylistPaginated(Pageable pageable){
        Page<Playlist> playlistPage = playlistRepository.findAll(pageable);

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
}
