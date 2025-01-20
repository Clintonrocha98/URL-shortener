package com.urlshorter.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {

  private static final Dotenv dotenv = Dotenv.load();

  private static final String URL = dotenv.get("DB_URL", "jdbc:h2:mem:testdb");
  private static final String USER = dotenv.get("DB_USER", "sa");
  private static final String PASSWORD = dotenv.get("DB_PASSWORD", "");

  public static Connection getConnection() throws SQLException {
    if (URL == null || USER == null || PASSWORD == null) {
      throw new IllegalStateException("Variáveis de ambiente para conexão com o banco não configuradas corretamente.");
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
