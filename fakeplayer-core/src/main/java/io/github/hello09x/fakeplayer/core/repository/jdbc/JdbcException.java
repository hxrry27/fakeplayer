package io.github.hello09x.fakeplayer.core.repository.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class JdbcException extends RuntimeException {

    public JdbcException(@NotNull String sql, @NotNull SQLException cause) {
        super("Failed to execute: " + sql, cause);
    }

}
