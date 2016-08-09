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
            	listApi(registry, "http://ul-management.s3-website-eu-west-1.amazonaws.com", "http://localhost:8080");
            }
            
            public void listApi(CorsRegistry registry, String url, String url2){
                registry.addMapping("/info").allowedOrigins(url, url2);
            	registry.addMapping("/api/auth/google").allowedOrigins(url, url2);
                registry.addMapping("/api/files").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/state").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/create").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/launchscript").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/getemails").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/inscription/{sheetid}/{row}").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/complete").allowedOrigins(url, url2);
                registry.addMapping("/api/sheets/getemails/{row}").allowedOrigins(url, url2).allowedMethods("DELETE");
                registry.addMapping("/api/sheets/{sheetid}/sendinscrits").allowedOrigins(url, url2).allowedMethods("PUT");
            }
        };
    }

}
