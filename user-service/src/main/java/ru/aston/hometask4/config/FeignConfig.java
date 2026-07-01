package ru.aston.hometask4.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.aston.hometask4.clients")
public class FeignConfig {
}
