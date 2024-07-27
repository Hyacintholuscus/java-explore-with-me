package ru.practicum.ewmmain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EwmApplication {
    public static void main(String[] args) {
        SpringApplication.run(EwmApplication.class, args);
    }
}
