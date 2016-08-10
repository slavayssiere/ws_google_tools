package org.crf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("org.crf"))              
          .paths(PathSelectors.ant("/api/**/*"))                          
          .build()
          .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        @SuppressWarnings("deprecation")
		ApiInfo apiInfo = new ApiInfo(
          "CRF7511 WS GOOGLE",
          "Custom WS for access Google WS",
          WsGoogleToolsApplication.class.getClass().getPackage().getImplementationVersion(),
          "Use for CRF only",
          "sebastien.lavayssiere@gmail.com",
          "Use for CRF only",
          "API license URL: none for now but I think about");
        return apiInfo;
    }
}