package com.unbe1iev.creator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
@EnableFeignClients(basePackages = "com.unbe1iev")
@SpringBootApplication(scanBasePackages="com.unbe1iev", exclude = {UserDetailsServiceAutoConfiguration.class})
public class CreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreatorApplication.class, args);
	}
}
