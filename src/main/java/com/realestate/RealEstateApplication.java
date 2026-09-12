package com.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Real Estate Management System backend.
 *
 * Run with:  mvn spring-boot:run
 * Or build a jar:  mvn clean package  ->  java -jar target/real-estate-management-system.jar
 */
@SpringBootApplication
public class RealEstateApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealEstateApplication.class, args);
    }
}
