package com.urlshorter.repository;



public interface UrlRepositoryInterface {
    public boolean save(String key, String url);
    public String get(String key);
}
