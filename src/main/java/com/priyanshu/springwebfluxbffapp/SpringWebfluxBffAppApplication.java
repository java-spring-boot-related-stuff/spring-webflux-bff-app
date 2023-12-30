package com.priyanshu.springwebfluxbffapp;

import com.priyanshu.springwebfluxbffapp.proxy.model.RawGatewayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RawGatewayProperties.class)
public class SpringWebfluxBffAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxBffAppApplication.class, args);
	}

}
