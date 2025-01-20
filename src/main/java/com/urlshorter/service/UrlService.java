package com.urlshorter.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.urlshorter.exceptions.UrlException;
import com.urlshorter.model.UrlModel;
import com.urlshorter.repository.UrlRepositoryInterface;
import com.urlshorter.utils.KeyGenerator;

public class UrlService {

    private UrlRepositoryInterface db;

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    public UrlService(UrlRepositoryInterface repo) {
        this.db = repo;
    }

    public String saveUrl(String url) throws UrlException, SQLException {
        logger.debug("Tentando salvar URL");

        UrlModel urlModel = new UrlModel();
        String key = KeyGenerator.generate();

        urlModel.setKeyUrl(key);
        urlModel.setOriginalUrl(url);

        boolean created = db.save(urlModel);

        if (!created) {
            logger.error("Falha ao salvar URL");
            throw new UrlException("Falha ao salvar URL", 500);
        }

        logger.info("URL salva com sucesso");

        return key;
    }

    public UrlModel getUrl(String key) throws UrlException, SQLException {
        logger.debug("Tentando pegar a chave: {}", key);

        UrlModel url = db.findByKey(key);

        if (url == null) {
            logger.error("Falha ao tentar resgatar conteudo da chave: {}", key);
            throw new UrlException("item não encontrado", 404);
        }
        logger.info("Url resgatada com sucesso");
        return url;
    }

}
