package com.urlshorter.factory;

import com.urlshorter.controller.UrlController;
import com.urlshorter.repository.UrlRepository;
import com.urlshorter.service.UrlService;

public class UrlFactory {

    public static UrlController createController() {
        UrlRepository repo = new UrlRepository();
        UrlService service = new UrlService(repo);
        UrlController controller = new UrlController(service);
        return controller;
    }
}
