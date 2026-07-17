package io.github.hello09x.fakeplayer.core.repository.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

    @Nullable T mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException;

}
