package main.java.org.htwg.konstanz.metaproject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/Metaproject/app/**") //
                .allowedOrigins( //
                        "https://metaproject.in.htwg-konstanz.de", //
                        "https://kp2.in.htwg-konstanz.de", //
                        "http://localhost:4200", //
                        "http://metaproject-tst.in.fhkn.de:4200" //
                ) //
                .allowCredentials(true) //
                .exposedHeaders("Location") //
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "token") //
                .maxAge(3600) //
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH");
    }
}
