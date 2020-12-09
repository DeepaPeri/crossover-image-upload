package com.crossover.image;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
@EnableAutoConfiguration
public class CrossoverFeatureDevelopmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrossoverFeatureDevelopmentApplication.class, args);
    }
}
