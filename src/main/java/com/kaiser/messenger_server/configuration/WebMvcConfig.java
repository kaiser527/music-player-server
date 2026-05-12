package com.kaiser.messenger_server.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
    @Value("${file.image-upload-dir}")
    private String UPLOAD_DIR_IMAGE ;

    @Value("${file.track-upload-dir}")
    private String UPLOAD_DIR_TRACK ;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path rootPath = Paths.get("").toAbsolutePath();
        String imagePath = rootPath + "/" + UPLOAD_DIR_IMAGE + "/";
        String trackPath = rootPath + "/" + UPLOAD_DIR_TRACK + "/";
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath)
                .setCachePeriod(0);

        registry.addResourceHandler("/tracks/**")
                .addResourceLocations("file:" + trackPath)
                .setCachePeriod(0);
    }
}