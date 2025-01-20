package com.urlshorter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.urlshorter.exceptions.UrlException;
import com.urlshorter.model.UrlModel;
import com.urlshorter.repository.UrlRepositoryInterface;
import com.urlshorter.utils.KeyGenerator;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
  @Mock
  private UrlRepositoryInterface repositoryMock;

  @InjectMocks
  private UrlService service;

  @Test
  @DisplayName("Deve salvar uma URL e retornar a chave gerada")
  void testSaveUrl() throws SQLException, UrlException {

    String url = "http://example.com";
    String generatedKey = "abc1234567";

    when(repositoryMock.save(any(UrlModel.class))).thenReturn(true);
    mockStatic(KeyGenerator.class);
    when(KeyGenerator.generate()).thenReturn(generatedKey);

    String result = service.saveUrl(url);

    assertEquals(generatedKey, result);
    verify(repositoryMock).save(any(UrlModel.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao falhar ao salvar a URL")
  void testSaveUrlFails() throws SQLException {

    String url = "http://example.com";

    when(repositoryMock.save(any(UrlModel.class))).thenReturn(false);

    UrlException exception = assertThrows(UrlException.class, () -> service.saveUrl(url));
    assertEquals("Falha ao salvar URL", exception.getMessage());
    assertEquals(500, exception.getStatusCode());
    verify(repositoryMock).save(any(UrlModel.class));
  }

  @Test
  @DisplayName("Deve retornar o modelo de URL ao encontrar a chave")
  void testGetUrl() throws SQLException, UrlException {

    String key = "abc1234567";
    UrlModel urlModel = new UrlModel();
    urlModel.setKeyUrl(key);
    urlModel.setOriginalUrl("http://example.com");

    when(repositoryMock.findByKey(key)).thenReturn(urlModel);

    UrlModel result = service.getUrl(key);

    assertNotNull(result);
    assertEquals("http://example.com", result.getOriginalUrl());
    assertEquals(key, result.getKeyUrl());
    verify(repositoryMock).findByKey(key);
  }

  @Test
  @DisplayName("Deve lançar exceção ao não encontrar a chave")
  void testGetUrlNotFound() throws SQLException {

    String key = "nonexistent";
    when(repositoryMock.findByKey(key)).thenReturn(null);

    UrlException exception = assertThrows(UrlException.class, () -> service.getUrl(key));
    assertEquals("item não encontrado", exception.getMessage());
    assertEquals(404, exception.getStatusCode());
    verify(repositoryMock).findByKey(key);
  }
}
