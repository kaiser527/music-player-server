package com.kaiser.messenger_server.modules.mail;

import java.util.HashMap;
import java.util.Map;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class EmailService {
    JavaMailSender javaMailSender;
    Handlebars handlebars;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        TemplateLoader loader = new ClassPathTemplateLoader("/templates/", ".hbs");
        this.handlebars = new Handlebars(loader);
    }

    public void sendTemplateEmail(String name, String activationCode, String subject){
        try{
            Template template = handlebars.compile("email-content");

            Map<String, String> context = new HashMap<>();

            context.put("name", name);
            context.put("activationCode", activationCode);

            String htmlBody = template.apply(context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(name);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
        }catch(Exception e){
            log.info(e.getMessage());
            throw new AppException(ErrorCode.SEND_MAIL_FAILED);
        }
    }
}
