package io.github.hello09x.fakeplayer.core.repository.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    private final DataSource dataSource;

    public JdbcTemplate(@NotNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(@NotNull String sql) {
        try (var connection = this.dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new JdbcException(sql, e);
        }
    }

    public int update(@NotNull String sql, @Nullable Object... args) {
        try (var connection = this.dataSource.getConnection(); var statement = prepare(connection, sql, args)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException(sql, e);
        }
    }

    public <T> @NotNull List<T> query(@NotNull String sql, @NotNull RowMapper<T> mapper, @Nullable Object... args) {
        try (var connection = this.dataSource.getConnection(); var statement = prepare(connection, sql, args)) {
            try (var rs = statement.executeQuery()) {
                var rows = new ArrayList<T>();
                var rowNum = 0;
                while (rs.next()) {
                    rows.add(mapper.mapRow(rs, rowNum++));
                }
                return rows;
            }
        } catch (SQLException e) {
            throw new JdbcException(sql, e);
        }
    }

    public <T> @Nullable T queryForObject(@NotNull String sql, @NotNull RowMapper<T> mapper, @Nullable Object... args) {
        try (var connection = this.dataSource.getConnection(); var statement = prepare(connection, sql, args)) {
            try (var rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapper.mapRow(rs, 0);
            }
        } catch (SQLException e) {
            throw new JdbcException(sql, e);
        }
    }

    private static @NotNull PreparedStatement prepare(
            @NotNull Connection connection,
            @NotNull String sql,
            @Nullable Object... args
    ) throws SQLException {
        var statement = connection.prepareStatement(sql);
        if (args != null) {
            for (var i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
        }
        return statement;
    }

}
