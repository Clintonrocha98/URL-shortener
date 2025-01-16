package com.urlshorter.factory;

import com.urlshorter.controller.UrlController;
import com.urlshorter.repository.UrlRepository;
import com.urlshorter.service.UrlService;

public class UrlControllerFactory {

    public static UrlController createController() {
        UrlRepository repo = new UrlRepository();
        UrlService service = new UrlService(repo);
        return new UrlController(service);
    }
}
