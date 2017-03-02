package com.funcxy.oj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
* The entry point of the application.
*/

@SpringBootApplication
@EnableConfigurationProperties()
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
