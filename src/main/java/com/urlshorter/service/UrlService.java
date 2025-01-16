package com.urlshorter.service;

import java.net.URL;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.urlshorter.exceptions.UrlException;
import com.urlshorter.repository.UrlRepositoryInterface;

public class UrlService {

    private UrlRepositoryInterface db;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private static final String VALID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM";
    private static final int KEY_LENGTH = 10;

    public UrlService(UrlRepositoryInterface repo) {
        this.db = repo;
    }

    public String saveUrl(String url) throws UrlException {
        logger.debug("Tentando salvar URL: {}", url);

        if (!isValidUrl(url)) {
            logger.error("URL invalida: {}", url);
            throw new UrlException("Url invalida", 400);
        }
        String newKey = randomKey();

        boolean created = db.save(newKey, url);

        if (!created) {
            logger.error("Falha ao salvar URL com chave: {}", newKey);
            throw new UrlException("Não foi possivel salvar", 500);
        }

        logger.info("URL com chave salvo com sucesso: {}", newKey);
        return newKey;
    }

    public String getUrl(String key) throws UrlException {
        logger.debug("Tentando pegar a chave: {}", key);

        String url = db.get(key);

        if (url == null) {
            logger.error("Falha ao tentar resgatar conteudo da chave: {}", key);
            throw new UrlException("item não encontrado", 404);
        }
        logger.info("Conteudo da chave resgatada com sucesso: {}", key);
        return url;
    }

    private static String randomKey() {
        StringBuilder result = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < KEY_LENGTH; i++) {
            result.append(VALID_CHARS.charAt(rand.nextInt(VALID_CHARS.length())));
        }
        return result.toString();
    }

    private static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        try {

            if (!url.matches("^(http|https)://.*$")) {
                url = "http://" + url;
            }
            new URL(url).toURI();

            return true;
        } catch (Exception e) {

            return false;
        }
    }

}
