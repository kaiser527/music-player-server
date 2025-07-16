package com.kaiser.messenger_server.controllers;

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
import com.kaiser.messenger_server.dto.request.TrackRequest;
import com.kaiser.messenger_server.dto.response.ApiResponse;
import com.kaiser.messenger_server.dto.response.PaginatedResponse;
import com.kaiser.messenger_server.dto.response.TrackResponse;
import com.kaiser.messenger_server.services.TrackService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("track")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class TrackController {
    TrackService trackService;

    @GetMapping
    ApiResponse<PaginatedResponse<TrackResponse>> getTrackPaginated(@RequestParam int pageSize, @RequestParam int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PaginatedResponse<TrackResponse> result = trackService.getTrackPaginated(pageable);

        return ApiResponse.<PaginatedResponse<TrackResponse>>builder()
            .message("Fetch track paginate")
            .result(result)
            .build();
    }

    @PostMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/track', 'POST')")
    ApiResponse<TrackResponse> createTrack(@RequestBody @Valid TrackRequest request){
        TrackResponse result = trackService.createTrack(request);
            
        return ApiResponse.<TrackResponse>builder()
            .message("Create track")
            .result(result)
            .build();
    }

    @GetMapping("/{id}")
    ApiResponse<TrackResponse> getTrackById(@PathVariable("id") String id){
        TrackResponse result = trackService.getTrackById(id);

        return ApiResponse.<TrackResponse>builder()
            .message("Get track by id")
            .result(result)
            .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/track/:id', 'PATCH')")
    ApiResponse<TrackResponse> updateTrack(@PathVariable("id") String id, @RequestBody @Valid TrackRequest request){
        TrackResponse result = trackService.updateTrack(id, request);

        return ApiResponse.<TrackResponse>builder()
            .message("Update track")
            .result(result)
            .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission('/api/v1/track/:id', 'DELETE')")
    ApiResponse<TrackResponse> deleteTrack(@PathVariable("id") String id){
        TrackResponse result = trackService.deleteTrack(id);

        return ApiResponse.<TrackResponse>builder()
            .message("Delete track")
            .result(result)
            .build();
    }
}
