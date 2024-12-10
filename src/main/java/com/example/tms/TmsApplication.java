package com.example.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class TmsApplication {

    public static void main( String[] args) {
        SpringApplication.run(TmsApplication.class, args);
    }

}
