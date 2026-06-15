package ru.aston.hometask5.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
public record MailTemplatesProperties(
        Templates templates,
        Variables variables
) {
    public record Templates(Template create, Template delete) {}
    public record Template(String subject, String text) {}
    public record Variables(String siteName) {}
}
