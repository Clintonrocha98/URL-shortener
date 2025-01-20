package com.urlshorter.repository;

import java.sql.SQLException;

import com.urlshorter.model.UrlModel;

public interface UrlRepositoryInterface {
    public boolean save(UrlModel url) throws SQLException;

    public UrlModel findByKey(String key) throws SQLException;
}
