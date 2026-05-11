package com.kaiser.messenger_server.modules.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kaiser.messenger_server.configuration.CustomPermissionEvaluator;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.modules.role.dto.RoleResponse;
import com.kaiser.messenger_server.modules.user.dto.CreateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UserResponse;
import lombok.experimental.NonFinal;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "role.user=USER", "role.admin=ADMIN" })
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomPermissionEvaluator customPermissionEvaluator;

    @MockitoBean
    private UserService userService;

    private CreateUserRequest createRequest;
    private UserResponse response;

    @NonFinal
    @Value("${role.user}")
    String USER_ROLE;

    @NonFinal
    @Value("${role.admin}")
    String ADMIN_ROLE;


    @BeforeEach
    void initData() {
        createRequest = CreateUserRequest.builder()
            .username("john")
            .email("john@gmail.com")
            .password("12345678")
            .isActive(true)
            .roleId(USER_ROLE)
            .build();

        response = UserResponse.builder()
            .id("cf0600f538b3")
            .username("john")
            .email("john@gmail.com")
            .accountType(AccountType.LOCAL.toString())
            .isActive(true)
            .role(RoleResponse.builder()
                .id(USER_ROLE)
                .description(ADMIN_ROLE)
                .isActive(true)
                .name("USER")
                .build())
            .image("default-1752056150533.png")
            .build();
    }
    
    @Test
    @WithMockUser 
    void createUser_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(response);

        Mockito.when(customPermissionEvaluator.hasPermission(
            ArgumentMatchers.any(), 
            ArgumentMatchers.any()
        )).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("cf0600f538b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.image").value("default-1752056150533.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.accountType").value(AccountType.LOCAL.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("result.role.id").value(USER_ROLE));
    }

    @Test
    void createUser_unauthorized_401() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createUser_forbidden_403() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        Mockito.when(customPermissionEvaluator.hasPermission(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
        )).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
