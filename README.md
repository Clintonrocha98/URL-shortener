# Encurtador de URLs

Este projeto implementa um serviço de encurtamento de URLs. A aplicação permite encurtar URLs longas e redirecionar para as URLs originais a partir de um link encurtado.

## Requisitos do desafio

[Repositório do desafio original](https://github.com/backend-br/desafios/blob/master/url-shortener/PROBLEM.md)

- O encurtador de URLs recebe uma URL longa como parâmetro inicial.

- O encurtamento será composto por um mínimo de 05 e um máximo de 10 caracteres.

- Apenas letras e números são permitidos no encurtamento.

- A URL encurtada será salva no banco de dados com um prazo de validade (você pode escolher a duração desejada).

- Ao receber uma chamada para a URL encurtada `{{host}}/DXB6aV2tg2`, você deve fazer o redirecionamento para a URL original salva no banco de dados. Caso a URL não seja encontrada no banco, retorne o código de status HTTP 404 (Not Found).

## Dedpendências

- h2
- junit
- mockito
- slf4j
- logback
- jackson
- java-dotenv

## Arquitetura

**Main.java**: Esta é a classe principal que configura e inicializa o servidor HTTP, ouvindo na porta informada no arquivo `.env` ou na porta padrão `8080`. Ela cria e configura o roteador para lidar com as requisições.

**Routes**: Responsável por encaminhar as requisições HTTP para o controlador adequado. Ele verifica a URL e o método HTTP (POST para encurtar URLs e GET para redirecionar).

**Controller**: O controlador gerencia a lógica das requisições, interagindo com o serviço que manipula o banco de dados. Ele contém os métodos para salvar uma URL (saveUrl) e para redirecionar para a URL original (getUrl).

**Service**: Contém a lógica de negócios. Ele lida com a geração de uma chave aleatória para cada URL encurtada e com a interação com o repositório para salvar e recuperar URLs.

**Repository**: O repositório lida com as interações com o banco de dados, ele contém métodos para salvar e recuperar as URLs.

## Configuração do Banco de Dados

O repositório contém um arquivo `.env.example.` Para configurar o projeto, renomeie este arquivo para `.env` e adicione as informações de conexão com o banco de dados H2.

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
    "message": "success",
    "data": "{{host}}/DXB6aV2tg2"
}
```

3. Para acessar a URL original, basta fazer uma requisição `GET` para o endpoint `GET /{key}`, onde `{key}` é a chave gerada para a URL encurtada.

