package io.github.hello09x.fakeplayer.core.repository.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanRowMapper implements RowMapper<Boolean> {

    @Override
    public @NotNull Boolean mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return rs.getBoolean(1);
    }

}
