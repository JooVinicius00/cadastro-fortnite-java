# Driver MySQL

O código compila usando apenas o JDK, mas o acesso ao banco precisa do MySQL Connector/J.

Opção Maven, na raiz do projeto:

```sh
mvn package dependency:copy-dependencies -DoutputDirectory=lib
```

Ou baixe o Connector/J 8.4.0 em https://dev.mysql.com/downloads/connector/j/8.4.html,
extraia o pacote e coloque `mysql-connector-j-8.4.0.jar` nesta pasta.
O arquivo do driver não é versionado. Consulte o README para configurar o banco.
