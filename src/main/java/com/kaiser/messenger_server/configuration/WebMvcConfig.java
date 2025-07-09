package com.kaiser.messenger_server.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR ;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path rootPath = Paths.get("").toAbsolutePath();
        String imagePath = rootPath + "/" + UPLOAD_DIR + "/";
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath)
                .setCachePeriod(0); // Disable cache for development
    }
}