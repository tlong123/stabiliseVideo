package com.stablevideo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StablevideoApplication {

	public static void main(String[] args) {
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(StablevideoApplication.class);
        builder.headless(false).run(args);
        
	}

}
