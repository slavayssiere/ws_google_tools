package org.crf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class WsGoogleToolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsGoogleToolsApplication.class, args);
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/auth/google").allowedOrigins("http://localhost:8080");
                registry.addMapping("/api/files").allowedOrigins("http://localhost:8080");
                registry.addMapping("/api/sheets/state").allowedOrigins("http://localhost:8080");
                registry.addMapping("/api/sheets/create").allowedOrigins("http://localhost:8080");
            }
        };
    }

}
