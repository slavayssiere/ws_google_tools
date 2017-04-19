package org.crf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@Configuration
@EnableScheduling
public class WsGoogleToolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsGoogleToolsApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {			
				listApi(registry, "http://ul-management.s3-website-eu-west-1.amazonaws.com");
				listApi(registry, "http://localhost:8080");
				listApi(registry, "http://localhost:3000");
			}

			public void listApi(CorsRegistry registry, String url) {
				registry.addMapping("/info").allowedOrigins(url).allowedMethods("GET", "OPTIONS");
				registry.addMapping("/api/auth/google").allowedOrigins(url);
				registry.addMapping("/api/files").allowedOrigins(url);
				registry.addMapping("/api/sheets/state").allowedOrigins(url);
				registry.addMapping("/api/sheets/create").allowedOrigins(url);
				registry.addMapping("/api/sheets/launchscript").allowedOrigins(url);
				registry.addMapping("/api/sheets/getemails").allowedOrigins(url);
				registry.addMapping("/api/sheets/inscription/{sheetid}/{row}").allowedOrigins(url);
				registry.addMapping("/api/sheets/complete").allowedOrigins(url);
				registry.addMapping("/api/sheets/getemails/{row}").allowedOrigins(url).allowedMethods("DELETE");
				registry.addMapping("/api/sheets/{sheetid}/sendinscrits").allowedOrigins(url).allowedMethods("PUT");
				registry.addMapping("/api/sheets/draft").allowedOrigins(url).allowedMethods("POST");
			}
		};
	}
}
