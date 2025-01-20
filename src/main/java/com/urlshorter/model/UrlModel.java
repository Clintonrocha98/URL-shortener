package com.urlshorter.model;

import java.sql.Timestamp;

public class UrlModel {
  private int id;
  private String keyUrl;

  private String originalUrl;
  private Timestamp createAt;

  public UrlModel() {
  }

  public UrlModel(String originalUrl) {

    this.originalUrl = originalUrl;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public Timestamp getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Timestamp createAt) {
    this.createAt = createAt;
  }

  public String getKeyUrl() {
    return keyUrl;
  }

  public void setKeyUrl(String keyUrl) {
    this.keyUrl = keyUrl;
  }

}
