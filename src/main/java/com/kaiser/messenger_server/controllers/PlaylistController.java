package com.kaiser.messenger_server.controllers;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kaiser.messenger_server.dto.request.PlaylistRequest;
import com.kaiser.messenger_server.dto.response.ApiResponse;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.PlaylistResponse;
import com.kaiser.messenger_server.services.PlaylistService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("playlist")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class PlaylistController {
    PlaylistService playlistService;

    @PostMapping
    ApiResponse<PlaylistResponse> createPlaylist(@RequestBody @Valid PlaylistRequest request){
        PlaylistResponse result = playlistService.createPlaylist(request);

        return ApiResponse.<PlaylistResponse>builder()
            .message("Create playlist")
            .result(result)
            .build();
    }

    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/playlist', 'GET')")
    ApiResponse<PaginatedResponse<PlaylistResponse>> getPlaylistPaginated(@RequestParam int pageSize, @RequestParam int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PaginatedResponse<PlaylistResponse> result = playlistService.getPlaylistPaginated(pageable);

        return ApiResponse.<PaginatedResponse<PlaylistResponse>>builder()
            .message("Fetch playlist paginate")
            .result(result)
            .build();
    }

    @PatchMapping("/{id}")
    ApiResponse<PlaylistResponse> updatePlaylist(@PathVariable("id") String id, @RequestBody @Valid PlaylistRequest request){
        PlaylistResponse result = playlistService.updatePlaylist(id, request);

        return ApiResponse.<PlaylistResponse>builder()
            .message("Update playlist")
            .result(result)
            .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/playlist/:id', 'DELETE')")
    ApiResponse<PlaylistResponse> deletePlaylist(@PathVariable("id") String id){
        PlaylistResponse result = playlistService.deletePlaylist(id);

        return ApiResponse.<PlaylistResponse>builder()
            .message("Delete playlist")
            .result(result)
            .build();
    }

    @GetMapping("/global")
    ApiResponse<List<PlaylistResponse>> getGlobalPlaylist(){
        List<PlaylistResponse> result = playlistService.getGlobalPlaylist();
    
        return ApiResponse.<List<PlaylistResponse>>builder()
            .message("Get global playlist")
            .result(result)
            .build();
    }

    @GetMapping("/user")
    ApiResponse<PaginatedResponse<PlaylistResponse>> getUserPlaylist(@RequestParam int pageSize, @RequestParam int pageNumber, @RequestParam String name){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PaginatedResponse<PlaylistResponse> result = playlistService.getUserPlaylist(pageable, name);

        return ApiResponse.<PaginatedResponse<PlaylistResponse>>builder()
            .message("Get user playlist")
            .result(result)
            .build();
    }

    @PostMapping("/bulk-delete")
    ApiResponse<List<PlaylistResponse>> bulkDeletePlaylist(@RequestBody List<String> playlistIds){
        playlistService.bulkDeletePlaylist(playlistIds);

        return ApiResponse.<List<PlaylistResponse>>builder()
            .message("Bulk delete playlist")
            .build();
    }
}
