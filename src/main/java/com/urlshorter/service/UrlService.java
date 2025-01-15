package com.urlshorter.service;

import java.net.URL;
import java.util.Random;

import com.urlshorter.exceptions.UrlException;
import com.urlshorter.repository.UrlRepositoryInterface;

public class UrlService {

    private UrlRepositoryInterface db;

    public UrlService(UrlRepositoryInterface repo) {
        this.db = repo;
    }

    public String saveUrl(String url) throws UrlException {
        String newKey = randomKey();

        if(!isValidUrl(url)){
            throw new UrlException("Url invalida", 400);
        }
        
        boolean created = db.save(newKey, url);

        if (!created) {
            throw new UrlException("Não foi possivel salvar", 500);
        }

        return newKey;
    }

    public String getUrl(String key) throws UrlException {

        String url = db.get(key);
        
        if (url == null) {
            throw new UrlException("item não encontrado", 404);
        }

        return url;
    }

    private static String randomKey() {
        StringBuilder result = new StringBuilder();
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM";
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            result.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return result.toString();
    }

    private static boolean isValidUrl(String url){
        if (url == null || url.isEmpty()) {
            return false; 
        }
        try{
            new URL(url).toURI();
            return true;
        } catch(Exception e){
            return false;
        }
    }

}
