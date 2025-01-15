package com.urlshorter.repository;


import java.util.HashMap;
import java.util.Map;

public class UrlRepository implements UrlRepositoryInterface {

    private final Map<String, String> db = new HashMap<>();

    public boolean save(String key, String url) {
        db.put(key, url);
        return true;
    }

    public String get(String key) {
        return db.get(key);
    }
}
