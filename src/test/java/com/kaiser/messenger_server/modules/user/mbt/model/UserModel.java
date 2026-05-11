package com.kaiser.messenger_server.modules.user.mbt.model;

import com.kaiser.messenger_server.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String username;

    private String email;

    private String image;

    private AccountType accountType;

    private boolean active;

    private String roleId;
}