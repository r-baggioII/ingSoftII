package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImagenConfiguration implements WebMvcConfigurer {

    @Value("${app.images.base-path:${user.home}/Videojuegos/imagenes}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        WebMvcConfigurer.super.addResourceHandlers(registry);
        // Normalizar la ruta para que funcione en Windows y Linux
        String normalizedPath = basePath.replace("\\", "/");
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + normalizedPath);
    }

}