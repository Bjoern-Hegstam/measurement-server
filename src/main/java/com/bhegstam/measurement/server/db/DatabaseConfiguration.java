package com.bhegstam.measurement.server.db;

import java.util.Objects;

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

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
