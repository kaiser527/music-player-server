package com.kaiser.messenger_server.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.kaiser.messenger_server.entities.Permission;
import com.kaiser.messenger_server.entities.Playlist;
import com.kaiser.messenger_server.entities.Role;
import com.kaiser.messenger_server.entities.Track;
import com.kaiser.messenger_server.entities.User;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.enums.ApiMethod;
import com.kaiser.messenger_server.repositories.PermissionRepository;
import com.kaiser.messenger_server.repositories.PlaylistRepository;
import com.kaiser.messenger_server.repositories.RoleRepository;
import com.kaiser.messenger_server.repositories.TrackRepository;
import com.kaiser.messenger_server.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@ConditionalOnProperty(
    prefix = "spring",
    value = "datasource.driverClassName",
    havingValue = "com.mysql.cj.jdbc.Driver"
)
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    String baseTrackUrl = "http://localhost:3000/api/v1/tracks/song/";

    @Bean
    ApplicationRunner applicationRunner(
        UserRepository userRepository, 
        RoleRepository roleRepository, 
        PermissionRepository permissionRepository,
        PlaylistRepository playlistRepository,
        TrackRepository trackRepository
    ){
        return args -> {
            long userCount = userRepository.count();
            long roleCount = roleRepository.count();
            long permissionCount = permissionRepository.count();
            long playlistCount = playlistRepository.count();
            long trackCount = trackRepository.count();
            boolean check = 
                userCount == 0 && 
                roleCount == 0 && 
                permissionCount == 0 && 
                playlistCount == 0 &&
                trackCount == 0;

            if(check){
                //init permission
                //user
                Permission getUser = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/user")
                    .method(ApiMethod.GET)
                    .name("Get user Fetch user paginate")
                    .module("USER")
                    .createdBy("system")
                    .build());
                
                Permission createUser = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/user")
                    .method(ApiMethod.POST)
                    .name("Create user")
                    .module("USER")
                    .createdBy("system")
                    .build());

                Permission updateUser = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/user/:id")
                    .method(ApiMethod.PATCH)
                    .name("Update user")
                    .module("USER")
                    .createdBy("system")
                    .build());

                Permission deleteUser = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/user/:id")
                    .method(ApiMethod.DELETE)
                    .name("Delete user")
                    .module("USER")
                    .createdBy("system")
                    .build());

                //permission
                Permission createPermission = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/permission")
                    .method(ApiMethod.POST)
                    .name("Create permission")
                    .module("PERMISSION")
                    .createdBy("system")
                    .build());

                Permission getPermission = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/permission")
                    .method(ApiMethod.GET)
                    .name("Fetch permission paginate")
                    .module("PERMISSION")
                    .createdBy("system")
                    .build());

                Permission updatePermission = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/permission/:id")
                    .method(ApiMethod.PATCH)
                    .name("Update permission")
                    .module("PERMISSION")
                    .createdBy("system")
                    .build());
                
                Permission deletePermission = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/permission/:id")
                    .method(ApiMethod.DELETE)
                    .name("Delete permission")
                    .module("PERMISSION")
                    .createdBy("system")
                    .build());

                //role
                Permission getRole = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/role")
                    .method(ApiMethod.GET)
                    .name("Fetch role paginate")
                    .module("ROLE")
                    .createdBy("system")
                    .build());

                Permission getSingleRole = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/role/:id")
                    .method(ApiMethod.GET)
                    .name("Fetch role by id")
                    .module("ROLE")
                    .createdBy("system")
                    .build());

                Permission createRole = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/role")
                    .method(ApiMethod.POST)
                    .name("Create role")
                    .module("ROLE")
                    .createdBy("system")
                    .build());

                Permission updateRole = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/role/:id")
                    .method(ApiMethod.PATCH)
                    .name("Update role")
                    .module("ROLE")
                    .createdBy("system")
                    .build());

                Permission deleteRole = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/role/:id")
                    .method(ApiMethod.DELETE)
                    .name("Delete role")
                    .module("ROLE")
                    .createdBy("system")
                    .build());
                
                //playlist
                Permission getPlaylist = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/playlist")
                    .method(ApiMethod.GET)
                    .name("Fetch playlist paginate")
                    .module("PLAYLIST")
                    .createdBy("system")
                    .build());

                Permission createPlaylist = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/playlist")
                    .method(ApiMethod.POST)
                    .name("Create playlist")
                    .module("PLAYLIST")
                    .createdBy("system")
                    .build());

                Permission updatePlaylist = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/playlist/:id")
                    .method(ApiMethod.PATCH)
                    .name("Update playlist")
                    .module("PLAYLIST")
                    .createdBy("system")
                    .build());

                Permission deletelaylist = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/playlist/:id")
                    .method(ApiMethod.DELETE)
                    .name("Delete playlist")
                    .module("PLAYLIST")
                    .createdBy("system")
                    .build());

                //track
                Permission getTrack = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/track")
                    .method(ApiMethod.GET)
                    .name("Fetch track paginate")
                    .module("TRACK")
                    .createdBy("system")
                    .build());

                Permission getSingleTrack = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/track/:id")
                    .method(ApiMethod.GET)
                    .name("Fetch track by id")
                    .module("TRACK")
                    .createdBy("system")
                    .build());

                Permission createTrack = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/track")
                    .method(ApiMethod.POST)
                    .name("Create track")
                    .module("TRACK")
                    .createdBy("system")
                    .build());

                Permission updateTrack = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/track/:id")
                    .method(ApiMethod.PATCH)
                    .name("Update track")
                    .module("TRACK")
                    .createdBy("system")
                    .build());

                Permission deleteTrack = permissionRepository.save(Permission.builder()
                    .apiPath("/api/v1/track/:id")
                    .method(ApiMethod.DELETE)
                    .name("Delete track")
                    .module("TRACK")
                    .createdBy("system")
                    .build());

                Set<Permission> permissions = new HashSet<Permission>();
                permissions = Stream.of(
                    getUser,
                    createUser,
                    updateUser,
                    deleteUser,
                    createPermission,
                    getPermission,
                    updatePermission,
                    deletePermission,
                    getRole,
                    createRole,
                    getSingleRole,
                    updateRole,
                    deleteRole,
                    getPlaylist,
                    createPlaylist,
                    updatePlaylist,
                    deletelaylist,
                    getTrack,
                    deleteTrack,
                    updateTrack,
                    createTrack,
                    getSingleTrack
                ).collect(Collectors.toSet());
                
                //init role
                Role testerRole = roleRepository.save(Role.builder()
                    .name("TESTER")
                    .description("Tester role")
                    .isActive(true)
                    .createdBy("system")
                    .permission(permissions.stream()
                        .filter(item -> !Set.of("ROLE", "PERMISSION")
                            .contains(item.getModule()))
                        .collect(Collectors.toSet()))
                    .build());

                Role artistRole = roleRepository.save(Role.builder()
                    .name("ARTIST")
                    .description("Artist role")
                    .isActive(true)
                    .createdBy("system")
                    .permission(permissions.stream()
                        .filter(item -> item.getApiPath().contains("/api/v1/track"))
                        .collect(Collectors.toSet()))
                    .build());

                Role userRole = roleRepository.save(Role.builder()
                    .name("USER")
                    .description("User role")
                    .isActive(true)
                    .createdBy("system")
                    .build());

                Role adminRole = roleRepository.save(Role.builder()
                    .name("ADMIN")
                    .description("Admin role")
                    .isActive(true)
                    .permission(permissions)
                    .createdBy("system")
                    .build());

                //init user
                userRepository.save(User.builder()
                    .username("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(adminRole)
                    .build());

                userRepository.save(User.builder()
                    .username("User")
                    .email("user@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.FACEBOOK)
                    .role(userRole)
                    .build());

                userRepository.save(User.builder()
                    .username("Artist")
                    .email("artist@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                userRepository.save(User.builder()
                    .username("Tester")
                    .email("test@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.GOOGLE)
                    .role(testerRole)
                    .build());

                User tracktribe = userRepository.save(User.builder()
                    .username("TrackTribe")
                    .email("tracktribe@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                User neffex = userRepository.save(User.builder()
                    .username("NEFFEX")
                    .email("neffex@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                User telecasted = userRepository.save(User.builder()
                    .username("Telecasted")
                    .email("telecasted@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                User vansinjapan = userRepository.save(User.builder()
                    .username("Vans in Japan")
                    .email("vansinjapan@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                User yunglogos = userRepository.save(User.builder()
                    .username("Yung Logos")
                    .email("yunglogos@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());
                
                User annodominibeat = userRepository.save(User.builder()
                    .username("Anno Domini Beats")
                    .email("annodominibeat@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .playlist(null)
                    .build());

                User ryan = userRepository.save(User.builder()
                    .username("Ryan McCaffrey")
                    .email("ryan@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .isActive(true)
                    .image("default-1752056150533.png")
                    .createdBy("system")
                    .accountType(AccountType.LOCAL)
                    .role(artistRole)
                    .build());

                //init track
                Track gink = trackRepository.save(Track.builder()
                    .title("Guess I'll Never Know")
                    .url(baseTrackUrl + "vTRYaTEbpaYRCxiWGgL2S91mnOuMKfLw-1752396539509.mp3")
                    .artwork("a3736661212_65-1752395585339.jpg")
                    .user(tracktribe)
                    .createdBy(tracktribe.getEmail())
                    .build());

                Track axt = trackRepository.save(Track.builder()
                    .title("Anxiety")
                    .url(baseTrackUrl + "rSmGXxf0OJLipPwFRyvoFKodDOj5VuWf-1752398519072.mp3")
                    .artwork("artworks-iCqupgQNLXSjKspS-0CGreg-t500x500-1752395648901.jpg")
                    .user(neffex)
                    .createdBy(neffex.getEmail())
                    .build());

                Track ayfaw = trackRepository.save(Track.builder()
                    .title("As You Fade Away")
                    .url(baseTrackUrl + "ZLdoXNocDAcsgeq6QKtPRHyvlqslNbke-1752399140219.mp3")
                    .artwork("maxresdefault-1752399060933.jpg")
                    .user(neffex)
                    .createdBy(neffex.getEmail())
                    .build());

                Track ctl = trackRepository.save(Track.builder()
                    .title("Cattle")
                    .url(baseTrackUrl + "rZ9sshicVlki8Dnm95ps1eWhK95dYgKF-1752454924940.mp3")
                    .artwork("maxresdefault-1752454851060.jpg")
                    .user(telecasted)
                    .createdBy(telecasted.getEmail())
                    .build());

                Track dsbr = trackRepository.save(Track.builder()
                    .title("Desert Brawl")
                    .url(baseTrackUrl + "ZufGK11EtwQWXge8xYo5EQ02RuJqtr4s-1752455480866.mp3")
                    .artwork("maxresdefault-1752455408209.jpg")
                    .user(vansinjapan)
                    .createdBy(vansinjapan.getEmail())
                    .build());

                Track chg = trackRepository.save(Track.builder()
                    .title("Changing")
                    .url(baseTrackUrl + "Tn0JjUOFnQXt94p3CQCA4AkB3weF51Yf-1752456897386.mp3")
                    .artwork("artworks-ZaFhh1AQdO4hqdYb-ssYmcA-t500x500-1752456829095.jpg")
                    .user(neffex)
                    .createdBy(neffex.getEmail())
                    .build());

                Track elsct = trackRepository.save(Track.builder()
                    .title("El Secreto")
                    .url(baseTrackUrl + "yA5v0HqEX7pRLKDkjp3XeFDcksZVv7lr-1752485169070.mp3")
                    .artwork("maxresdefault-1752485122882.jpg")
                    .user(yunglogos)
                    .createdBy(yunglogos.getEmail())
                    .build());

                Track hlt = trackRepository.save(Track.builder()
                    .title("Hotlanta")
                    .url(baseTrackUrl + "nXa6f08Ojlz1V2SYJ3axYmSa7ot0hblZ-1752485491192.mp3")
                    .artwork("maxresdefault-1752485448881.jpg")
                    .user(tracktribe)
                    .createdBy(tracktribe.getEmail())
                    .build());

                Track tmb = trackRepository.save(Track.builder()
                    .title("Take Me Back")
                    .url(baseTrackUrl + "cbMVQp4JGHhSNEeCqRjvieiigYpUaE0s-1752485700210.mp3")
                    .artwork("artworks-yaXBlJOtjWvRcNnA-W6spcw-t500x500-1752485662101.jpg")
                    .user(neffex)
                    .createdBy(neffex.getEmail())
                    .build());

                Track smlo = trackRepository.save(Track.builder()
                    .title("Smokey's Lounge")
                    .url(baseTrackUrl + "K4PdyskIIfRrRotZtwF0EfHkJGjTs9Dy-1752487321004.mp3")
                    .artwork("ab67616d0000b2730efb49aab6109fe4c74d6b04-1752487270159.jpg")
                    .user(tracktribe)
                    .createdBy(tracktribe.getEmail())
                    .build());

                Track snds = trackRepository.save(Track.builder()
                    .title("Sunny Days")
                    .url(baseTrackUrl + "5MLu9yZCOGOCpf9yhdK4uitEv2CZ9fwx-1752487710543.mp3")
                    .artwork("artworks-fJ47RvWYE7weOhay-V5Qjyw-t500x500-1752487655903.jpg")
                    .user(annodominibeat)
                    .createdBy(annodominibeat.getEmail())
                    .build());
                
                Track hdfrla = trackRepository.save(Track.builder()
                    .title("Hidden Frozen Lake - Go By Ocean")
                    .url(baseTrackUrl + "bnvYr6BoqfoZjrx72rvq3hGXyE6b7Qyz-1752487833181.mp3")
                    .artwork("maxresdefault-1752487797128.jpg")
                    .user(ryan)
                    .createdBy(ryan.getEmail())
                    .build());

                Set<Track> tracks = new HashSet<>();
                tracks = Stream.of(
                    gink,
                    hdfrla,
                    snds,
                    smlo,
                    tmb,
                    axt,
                    chg,
                    ctl,
                    ayfaw,
                    elsct,
                    hlt,
                    dsbr
                ).collect(Collectors.toSet());

                //init playlist
                playlistRepository.save(Playlist.builder()
                    .name("Chill 🌱")
                    .track(tracks.stream()
                        .filter(item -> Set.of(
                            "Guess I'll Never Know", 
                            "Anxiety",
                            "Cattle",
                            "Sunny Days",
                            "El Secreto",
                            "Desert Brawl",
                            "Hidden Frozen Lake - Go By Ocean"
                        )
                            .contains(item.getTitle()))
                        .collect(Collectors.toSet()))
                    .build());

                playlistRepository.save(Playlist.builder()
                    .name("Instrumental 🎵")
                    .track(tracks.stream()
                        .filter(item -> Set.of(
                            "Anxiety",
                            "Smokey's Lounge",
                            "Hotlanta",
                            "El Secreto",
                            "Desert Brawl"
                        )
                            .contains(item.getTitle()))
                        .collect(Collectors.toSet()))
                    .build());

                playlistRepository.save(Playlist.builder()
                    .name("Rap 🎤")
                    .track(tracks.stream()
                        .filter(item -> Set.of(
                            "As You Fade Away",
                            "Changing",
                            "Take Me Back",
                            "Smokey's Lounge",
                            "Anxiety"
                        )
                            .contains(item.getTitle()))
                        .collect(Collectors.toSet()))
                    .build());
            }
        };
    }
}
