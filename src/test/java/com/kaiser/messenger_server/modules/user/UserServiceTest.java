package com.kaiser.messenger_server.modules.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.assertj.core.api.Assertions;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import com.kaiser.messenger_server.modules.role.RoleRepository;
import com.kaiser.messenger_server.modules.role.entity.Role;
import com.kaiser.messenger_server.modules.user.dto.CreateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UpdateUserRequest;
import com.kaiser.messenger_server.modules.user.entity.User;
import com.kaiser.messenger_server.modules.user.mbt.generator.TestDataGenerator;
import com.kaiser.messenger_server.modules.user.mbt.generator.TestGenerator;
import com.kaiser.messenger_server.modules.user.mbt.model.ModelState;
import com.kaiser.messenger_server.modules.user.mbt.model.UserModel;
import com.kaiser.messenger_server.modules.user.mbt.state.ActionState;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = { "role.user=USER", "role.admin=ADMIN" })
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private UserRepository userRepository;
    
    @NonFinal
    @Value("${role.user}")
    String USER_ROLE;

    @NonFinal
    @Value("${role.admin}")
    String ADMIN_ROLE;

    @BeforeEach
    void initData() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.save(any()))
            .thenAnswer(inv -> {
                User u = inv.getArgument(0);
                if (u.getId() == null) {
                    u.setId(TestDataGenerator.randomUserId());
                }
                return u;
        });
    }

    User buildUser(String id, String email, Role role) {
        return User.builder()
            .id(id)
            .username(email)
            .email(email)
            .password("12345678")
            .accountType(AccountType.LOCAL)
            .image("default.png")
            .isActive(true)
            .role(role)
            .build();
    }

    void printSection(String title) {
        log.info("""

            ==================================================
            {}
            ==================================================
            """, title);
    }

    @Test
    void modelBasedTest_demo() {
        // ---------- SETUP ----------
        Role userRole = Role.builder()
            .id(USER_ROLE)
            .name("USER")
            .isActive(true)
            .description("User role")
            .build();

        Role adminRole = Role.builder()
            .id(ADMIN_ROLE)
            .name("ADMIN")
            .isActive(true)
            .description("Admin role")
            .build();

        when(roleRepository.findById(USER_ROLE))
            .thenReturn(Optional.of(userRole));

        when(roleRepository.findById(ADMIN_ROLE))
            .thenReturn(Optional.of(adminRole));

        // ---------- AUTOMATED TEST GENERATION ----------
        for (int run = 1; run <= 5; run++) {
            ModelState generatorModel = new ModelState();

            List<ActionState> sequence = TestGenerator.generateSequence(10, generatorModel);

            ModelState model = new ModelState();

            log.info("\n=== TEST RUN {} ===", run);
            log.info("Generated sequence: {}", sequence);

            // ---------- EXECUTION ----------
            for (ActionState action : sequence) {
                printSection("EXECUTING " + action);

                log.info("""
                    
                    CURRENT MODEL STATE
                    -------------------
                    User Count : {}
                    Users      : {}
                    
                    """,
                    model.size(),
                    model.prettyUsers()
                );

                switch (action) {
                    case CREATE_USER:
                        CreateUserRequest createRequest = TestDataGenerator.randomCreateRequest(USER_ROLE, model);

                        when(userRepository.existsByEmail(anyString()))
                            .thenAnswer(inv -> {
                                String email = inv.getArgument(0);
                                return model.emailExists(email);
                            });

                        boolean existed = model.emailExists(createRequest.getEmail());

                        log.info("""
                            
                            CREATE REQUEST
                            --------------
                            Email    : {}
                            Username : {}
                            
                            """,
                            createRequest.getEmail(),
                            createRequest.getUsername()
                        );

                        if (!existed) {
                            var response = userService.createUser(createRequest);

                            Assertions.assertThat(response).isNotNull();
                            Assertions.assertThat(response.getEmail()).isEqualTo(createRequest.getEmail());
                            Assertions.assertThat(response.getUsername()).isEqualTo(createRequest.getUsername());
                            Assertions.assertThat(response.getImage()).isEqualTo(createRequest.getImage());
                            Assertions.assertThat(response.getAccountType().toString()).isEqualTo(AccountType.LOCAL.toString());
                            Assertions.assertThat(response.getRole().getId()).isEqualTo(USER_ROLE);

                            UserModel createdUser = UserModel.builder()
                                .username(response.getUsername())
                                .email(response.getEmail())
                                .image(response.getImage())
                                .accountType(AccountType.valueOf(response.getAccountType()))
                                .active(response.getIsActive())
                                .roleId(response.getRole().getId())
                                .build();

                            model.addUser(response.getId(), createdUser);

                            log.info("""
        
                                RESULT
                                ------
                                SUCCESS : USER CREATED
                                
                                """);
                        } else {
                            AppException ex = assertThrows(AppException.class,
                                () -> userService.createUser(createRequest));

                            Assertions.assertThat(ex.getErrorCode())
                                .isEqualTo(ErrorCode.USER_EXISTED);

                            log.info("""
    
                                RESULT
                                ------
                                FAILED : USER EXISTED
                                
                                """);
                        }
                        break;

                    case UPDATE_USER:
                        UpdateUserRequest updateRequest = TestDataGenerator.randomUpdateRequest(USER_ROLE);

                        String existingId = model.size() == 0 ? null : model.randomUser();
                        String idUpdate =
                            existingId != null && ThreadLocalRandom.current().nextInt(100) < 70
                                ? existingId
                                : "invalid-" + TestDataGenerator.randomUserId();

                        boolean existsUpdate = model.userExists(idUpdate);

                        when(userRepository.findById(idUpdate))
                            .thenAnswer(inv -> existsUpdate
                                ? Optional.of(buildUser(idUpdate, model.getEmail(idUpdate), userRole))
                                : Optional.empty()
                            );

                        log.info("""
    
                            UPDATE REQUEST
                            --------------
                            Target Id : {}
                            Exists    : {}
                            Request   : {}
                            
                            """,
                            idUpdate,
                            existsUpdate,
                            updateRequest
                        );

                        if (existsUpdate) {
                            var response = userService.updateUser(idUpdate, updateRequest);

                            Assertions.assertThat(response).isNotNull();
                            Assertions.assertThat(response.getUsername()).isEqualTo(updateRequest.getUsername());
                            Assertions.assertThat(response.getRole().getId()).isEqualTo(updateRequest.getRoleId());
                            Assertions.assertThat(response.getImage()).isEqualTo(updateRequest.getImage());
                            Assertions.assertThat(response.getAccountType().toString()).isEqualTo(updateRequest.getAccountType().toString());

                            UserModel updatedUser = UserModel.builder()
                                .username(response.getUsername())
                                .email(response.getEmail())
                                .image(response.getImage())
                                .accountType(AccountType.valueOf(response.getAccountType()))
                                .active(response.getIsActive())
                                .roleId(response.getRole().getId())
                                .build();

                            model.updateUser(idUpdate, updatedUser);

                            log.info("""
        
                                RESULT
                                ------
                                SUCCESS : USER UPDATED
                                
                                """);

                        } else {
                            AppException ex = assertThrows(AppException.class,
                                () -> userService.updateUser(idUpdate, updateRequest));

                            Assertions.assertThat(ex.getErrorCode())
                                .isEqualTo(ErrorCode.USER_NOT_EXIST);

                            log.info("""
    
                                RESULT
                                ------
                                FAILED : USER NOT FOUND
                                
                                """);
                        }
                        break;

                    case DELETE_USER:
                        String deletingId = model.size() == 0 ? null : model.randomUser();
                        String idDelete =
                            deletingId != null && ThreadLocalRandom.current().nextInt(100) < 70
                                ? deletingId
                                : "invalid-" + TestDataGenerator.randomUserId();

                        boolean existsDelete = model.userExists(idDelete);

                        when(userRepository.findById(idDelete))
                            .thenAnswer(inv -> existsDelete
                                ? Optional.of(buildUser(idDelete, model.getEmail(idDelete), userRole))
                                : Optional.empty()
                            );

                        log.info("""
    
                            DELETE REQUEST
                            --------------
                            Target Id : {}
                            Exists    : {}
                            
                            """,
                            idDelete,
                            existsDelete
                        );

                        if (existsDelete) {
                            var response = userService.deleteUser(idDelete);

                            Assertions.assertThat(response).isNotNull();
                            Assertions.assertThat(response.getId()).isEqualTo(idDelete);

                            model.removeUser(idDelete);

                            log.info("""
        
                                RESULT
                                ------
                                SUCCESS : USER DELETED
                                
                                """);
                        } else {
                            AppException ex = assertThrows(AppException.class,
                                () -> userService.deleteUser(idDelete));

                            Assertions.assertThat(ex.getErrorCode())
                                .isEqualTo(ErrorCode.USER_NOT_EXIST);

                            log.info("""
    
                                RESULT
                                ------
                                FAILED : USER NOT FOUND
                                
                                """);
                        }
                        break;
                }
            }
            
            printSection("FINAL STATE OF TEST RUN " + run);

            log.info("""

                FINAL MODEL STATE
                -----------------
                Total Users : {}
                Users Map   : {}

                """,
                model.size(),
                model.prettyUsers()
            );

            log.info("""
                
                TEST RUN {} FINISHED
                
                ================================================
                
                """,
                run
            );
        }
    }
}
