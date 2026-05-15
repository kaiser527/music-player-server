package com.kaiser.messenger_server.modules.user.mbt.generator;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.modules.user.dto.CreateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UpdateUserRequest;
import com.kaiser.messenger_server.modules.user.mbt.model.ModelState;

public class TestDataGenerator {
    public static String randomUserId() {
        return UUID.randomUUID().toString();
    }

    public static CreateUserRequest randomCreateRequest(String roleId, ModelState model) {
        boolean reuseEmail = ThreadLocalRandom.current().nextInt(100) < 40;

        String email;

        String existingEmail = model.randomEmail();

        if (reuseEmail && existingEmail != null) {
            email = existingEmail;
        } else {
            String rand = String.valueOf(ThreadLocalRandom.current().nextInt(1000));
            email = "user_" + rand + "@mail.com";
        }

        int usernameRand = ThreadLocalRandom.current().nextInt(1000);

        String username = "username_" + usernameRand;

        return CreateUserRequest.builder()
                .username(username)
                .email(email)
                .password("pass")
                .isActive(true)
                .roleId(roleId)
                .build();
    }

    public static UpdateUserRequest randomUpdateRequest(String roleId) {
        String rand = String.valueOf(ThreadLocalRandom.current().nextInt(1000));

        return UpdateUserRequest.builder()
                .username("updated_" + rand)
                .image("img_" + rand + ".png")
                .accountType(
                    ThreadLocalRandom.current().nextInt(100) < 80 
                        ? AccountType.GOOGLE 
                        : AccountType.LOCAL
                )
                .isActive(true)
                .roleId(roleId)
                .build();
    }
}
 