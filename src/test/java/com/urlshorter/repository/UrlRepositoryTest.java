package com.urlshorter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.Connection;

import com.urlshorter.model.UrlModel;

public class UrlRepositoryTest {

  private Connection connectionMock;
  private PreparedStatement preparedStatementMock;
  private ResultSet resultSetMock;
  private UrlRepository repository;

  @BeforeEach
  void setUp() {
    connectionMock = mock(Connection.class);
    preparedStatementMock = mock(PreparedStatement.class);
    resultSetMock = mock(ResultSet.class);
    repository = new UrlRepository(connectionMock, false);
  }

  @Test
  @DisplayName("Deve ser possivel salvar url")
  void testSaveSuccess() throws SQLException {

    when(connectionMock.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
        .thenReturn(preparedStatementMock);
    when(preparedStatementMock.executeUpdate()).thenReturn(1);

    UrlModel url = new UrlModel();
    url.setOriginalUrl("http://example.com");
    url.setKeyUrl("abc1234567");

    boolean result = repository.save(url);

    assertTrue(result);
    verify(preparedStatementMock).setString(1, "http://example.com");
    verify(preparedStatementMock).setString(2, "abc1234567");
    verify(preparedStatementMock).executeUpdate();
  }

  @Test
  @DisplayName("não deve ser possivel salvar a url")
  void testSaveFailure() throws SQLException {
    when(connectionMock.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
        .thenReturn(preparedStatementMock);
    when(preparedStatementMock.executeUpdate()).thenReturn(0);

    UrlModel url = new UrlModel();
    url.setOriginalUrl("http://example.com");
    url.setKeyUrl("abc1234567");

    boolean result = repository.save(url);

    assertFalse(result);
    verify(preparedStatementMock).executeUpdate();
  }

  @Test
  @DisplayName("Deve ser possivel encontrar a url")
  void testFindByKeySuccess() throws SQLException {

    when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
    when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
    when(resultSetMock.next()).thenReturn(true);
    when(resultSetMock.getString("original_url")).thenReturn("http://example.com");
    when(resultSetMock.getString("url_key")).thenReturn("abc123");

    UrlModel result = repository.findByKey("abc123");

    assertNotNull(result);
    assertEquals("http://example.com", result.getOriginalUrl());
    assertEquals("abc123", result.getKeyUrl());
    verify(preparedStatementMock).setString(1, "abc123");
  }

  @Test
  @DisplayName("Não deve ser possivel encontrar a url usando a key")
  void testFindByKeyNotFound() throws SQLException {
    when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
    when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
    when(resultSetMock.next()).thenReturn(false);

    UrlModel result = repository.findByKey("nonexistent");

    assertNull(result);
    verify(preparedStatementMock).setString(1, "nonexistent");
  }
}
