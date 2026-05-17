package com.group2.web_tmdt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("\n========== 🕵️ BẮT ĐẦU DEBUG TRUY XUẤT ẢNH ==========");
        
        // 1. Xem Java đang lấy mốc ở đâu
        String userDir = System.getProperty("user.dir");
        System.out.println("[1] Thư mục gốc (user.dir): " + userDir);

        // 2. Tìm thư mục uploads
        File uploadDir = new File(userDir, "uploads");
        if (!uploadDir.exists() && new File(userDir, "web_tmdt/uploads").exists()) {
            uploadDir = new File(userDir, "web_tmdt/uploads");
        }
        
        System.out.println("[2] Đường dẫn uploads vật lý đang trỏ tới: " + uploadDir.getAbsolutePath());
        System.out.println("[3] Thư mục uploads này có TỒN TẠI thật không?: " + (uploadDir.exists() ? "CÓ" : "KHÔNG ❌"));

        // 3. Kiểm tra thẳng mặt file 1_main.jpg
        File testFile = new File(uploadDir, "products/1_main.jpg");
        System.out.println("[4] Kiểm tra file 1_main.jpg tại: " + testFile.getAbsolutePath());
        System.out.println("[5] File 1_main.jpg có TỒN TẠI trên ổ cứng không?: " + (testFile.exists() ? "CÓ" : "KHÔNG ❌"));

        // 4. Cấu hình vào Spring Boot
        String location = uploadDir.toURI().toString();
        System.out.println("[6] Chuỗi URL nạp vào Spring Boot: " + location);
        
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations(location);
                
        System.out.println("========================================================\n");
    }
}