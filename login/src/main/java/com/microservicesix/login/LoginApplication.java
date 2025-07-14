package com.microservicesix.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LoginApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.load();

			if(dotenv!=null){
				dotenv.entries().forEach(entry -> {
					if(System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey())==null){
						System.setProperty(entry.getKey(), entry.getValue());
					}
				});
			}
		SpringApplication.run(LoginApplication.class, args);
	}
}
