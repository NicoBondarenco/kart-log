# Kart Log Analyzer
## Descrição do serviço
Analizar os logs de uma corrida de kart e apresentar os dados finais, tais como: Posição Chegada, Código Piloto, Nome Piloto, Quantidade Voltas Completadas e Tempo
Total de Prova, além de exibir a melhor volta da corrida.
A analise funciona utilizando apenas os dados válidos, ou seja, apenas as voltas que ocorreram até a ultima volta do primeiro colocado (4 voltas), os demais dados são descartados.
## Requisitos
- JDK 8 (para versões superiores é necessário a instalação da SDK do JavaFX - https://openjfx.io/openjfx-docs/#introduction)
- Gradle 5.6.2 ou superior
## Arquitetura e organização do projeto
O projeto foi feito utilizando Java 8 e suas tecnologias. Suas classes esta divididas entre classes de configuração, modelo, repositórios, serviços e exibição.
- Gradle: como ferramenta de gerenciamento de depêndencias e build.
- JPA/Hibernate: para acesso e gravação de entidades ao banco de dados
- H2: Como banco de dados em memória
- JavaFX: como interface gráfica
## Como executar
- Build do projeto: gradle clean build
- Localização do jar: <pasta do projeto>/build/libs/kart-log-1.0.0.jar
- execução do projeto: java -jar kart-log-1.0.0.jar

