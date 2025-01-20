package com.urlshorter.factory;

import java.sql.SQLException;

import com.urlshorter.config.DatabaseConfig;
import com.urlshorter.controller.UrlController;
import com.urlshorter.repository.UrlRepository;
import com.urlshorter.service.UrlService;

public class UrlControllerFactory {

    public UrlController createController() throws SQLException {
        UrlRepository repo = new UrlRepository(DatabaseConfig.getConnection(), true);
        UrlService service = new UrlService(repo);
        return new UrlController(service);
    }
}
