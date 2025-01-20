package com.urlshorter.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.urlshorter.exceptions.UrlException;
import com.urlshorter.model.UrlModel;
import com.urlshorter.repository.UrlRepositoryInterface;
import com.urlshorter.utils.KeyGenerator;

public class UrlService {

    private UrlRepositoryInterface db;

    private static final int deadlineInDays = 5;

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
        logger.debug("Tentando pegar a chave");

        UrlModel url = db.findByKey(key);

        if (url == null) {
            logger.error("Falha ao tentar resgatar conteudo da chave");
            throw new UrlException("item não encontrado", 404);
        }

        boolean isValid = isWithinDeadline(url.getCreateAt(), deadlineInDays);

        if (!isValid) {
            logger.error("Prazo de validade expirado.");
            throw new UrlException("Prazo de validade expirado.", 400);
        }

        logger.info("Url resgatada com sucesso");
        return url;
    }

    private boolean isWithinDeadline(Timestamp createdAtTimestamp, int days) {
        LocalDateTime createdAt = createdAtTimestamp.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime deadline = createdAt.plusDays(days);

        return now.isBefore(deadline) || now.isEqual(deadline);
    }

}
