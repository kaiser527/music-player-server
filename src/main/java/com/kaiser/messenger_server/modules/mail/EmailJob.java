package com.kaiser.messenger_server.modules.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailJob {
    private String email;
    private String activationCode;
    private String subject;
}