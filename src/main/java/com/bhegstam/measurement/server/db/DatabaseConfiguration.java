package com.bhegstam.measurement.server.db;

import lombok.Getter;

import java.util.Objects;

@Getter
public class DatabaseConfiguration {
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfiguration(String url, String user, String password) {
        this.url = Objects.requireNonNull(url);
        this.user = Objects.requireNonNull(user);
        this.password = Objects.requireNonNull(password);
    }

    public static DatabaseConfiguration fromEnv() {
        return new DatabaseConfiguration(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD")
        );
    }
}
