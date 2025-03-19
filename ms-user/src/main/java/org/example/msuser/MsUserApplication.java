package org.example.msuser;

import org.example.msuser.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class MsUserApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsUserApplication.class, args);
	}

}
