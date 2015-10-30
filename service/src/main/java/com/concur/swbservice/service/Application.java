package com.concur.swbservice.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by mtalbot on 11/08/2015.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {HypermediaAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
