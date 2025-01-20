package com.urlshorter.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.urlshorter.model.UrlModel;

public class UrlRepository implements UrlRepositoryInterface {

    private final Connection connection;

    public UrlRepository(Connection connection, boolean initializeTable) {
        this.connection = connection;
        if (initializeTable) {
            initializeTable();
        }
    }

    private void initializeTable() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS urls (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    url_key CHAR(10) NOT NULL,
                    original_url VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de URLs", e);
        }
    }

    public boolean save(UrlModel url) throws SQLException {
        String sql = "INSERT INTO urls (original_url, url_key) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getOriginalUrl());
            stmt.setString(2, url.getKeyUrl());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    public UrlModel findByKey(String key) throws SQLException {
        String sql = "SELECT * FROM urls WHERE url_key = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, key);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UrlModel url = new UrlModel();
                    url.setOriginalUrl(rs.getString("original_url"));
                    url.setKeyUrl(rs.getString("url_key"));
                    url.setCreateAt(rs.getTimestamp("create_at"));
                    return url;
                }
            }
        }
        return null;
    }

}
