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
            	listApi(registry, "http://localhost:8080");
            	listApi(registry, "http://ul-management.s3-website-eu-west-1.amazonaws.com");
            }
            
            public void listApi(CorsRegistry registry, String url){
                registry.addMapping("/").allowedOrigins(url);
            	registry.addMapping("/api/auth/google").allowedOrigins(url);
                registry.addMapping("/api/files").allowedOrigins(url);
                registry.addMapping("/api/sheets/state").allowedOrigins(url);
                registry.addMapping("/api/sheets/create").allowedOrigins(url);
                registry.addMapping("/api/sheets/launchscript").allowedOrigins(url);
            }
        };
    }

}
