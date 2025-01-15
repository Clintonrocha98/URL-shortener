# Encurtador de URLs

Este projeto implementa um serviço de encurtamento de URLs utilizando o `HttpServer` do Java. A aplicação permite encurtar URLs longas e redirecionar para as URLs originais a partir de um link encurtado.

## Desafio

O objetivo do desafio é criar um encurtador de URLs, que recebe uma URL longa como parâmetro e retorna uma versão encurtada. A URL encurtada será salva em um banco de dados com um prazo de validade, e, ao acessar a URL encurtada, o usuário será redirecionado para a URL original salva no banco de dados. Caso a URL não seja encontrada, o servidor retorna um erro HTTP 404.

## Funcionalidades

- **Encurtamento de URL**: Enviar uma URL longa para o endpoint `POST /shorten-url` e receber uma URL encurtada.
- **Redirecionamento de URL**: Acessar uma URL encurtada para ser redirecionado à URL original.
- **Validação de URLs**: Se uma URL encurtada não for encontrada no banco de dados, o sistema retornará um erro 404.

## Arquitetura

**SimpleHttpServer:** Esta é a classe principal que configura e inicializa o servidor HTTP, ouvindo na porta 8000. Ela cria e configura o roteador para lidar com as requisições.

**Router:** Responsável por encaminhar as requisições HTTP para o controlador adequado. Ele verifica a URL e o método HTTP (POST para encurtar URLs e GET para redirecionar).

**UrlController:** O controlador gerencia a lógica das requisições, interagindo com o serviço que manipula o banco de dados. Ele contém os métodos para salvar uma URL (`saveUrl`) e para redirecionar para a URL original (`getUrl`).

**UrlService**: Contém a lógica de negócios. Ele lida com a geração de uma chave aleatória para cada URL encurtada e com a interação com o repositório para salvar e recuperar URLs.

**UrlRepository**: O repositório simula um banco de dados, armazenando as URLs encurtadas em um mapa em memória. Ele contém métodos para salvar e recuperar as URLs.

## Como usar

1. Faça uma requisição `POST` para o endpoint `/shorten-url` com um corpo JSON contendo a URL longa. Exemplo:
```json
{
    "url": "https://exemplo.com"
}
```

2. O serviço responderá com a URL encurtada:
```json
{
    "data": "http://localhost:8000/get-url/DXB6V"
}
```

3. Para acessar a URL original, basta fazer uma requisição `GET` para o endpoint `GET /get-url/{key}`, onde `{key}` é a chave gerada para a URL encurtada.