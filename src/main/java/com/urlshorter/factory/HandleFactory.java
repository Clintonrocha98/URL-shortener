package com.urlshorter.factory;

import java.sql.SQLException;

import com.urlshorter.config.DatabaseConfig;
import com.urlshorter.controller.UrlController;
import com.urlshorter.exceptions.FactoryCreationException;
import com.urlshorter.repository.UrlRepository;
import com.urlshorter.routes.Router;
import com.urlshorter.service.UrlService;
import java.sql.Connection;

public class HandleFactory {

    public Router create() {
        try (Connection connection = new DatabaseConfig().getConnection()) {
            UrlRepository repository = new UrlRepository(connection, true);
            UrlService service = new UrlService(repository);
            UrlController controller = new UrlController(service);
            return new Router(controller);

        } catch (SQLException e) {
            throw new FactoryCreationException("Erro ao criar o Router", e);
        }
    }
}
