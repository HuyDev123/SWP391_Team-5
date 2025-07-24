package com.genx.adnmanagement;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sử dụng đường dẫn tuyệt đối để serving files
        String baseDir = System.getProperty("user.dir") + File.separator;

        registry.addResourceHandler("/uploads/results/**")
                .addResourceLocations("file:" + baseDir + "uploads" + File.separator + "results" + File.separator);
        registry.addResourceHandler("/uploads/receipts/**")
                .addResourceLocations("file:" + baseDir + "uploads" + File.separator + "receipts" + File.separator);
    }
}
