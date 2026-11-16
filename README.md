# Cadastro de personagens Fortnite

Aplicação Java Swing com inclusão, pesquisa por ID, edição e exclusão em MySQL. Guarda nome, vida, escudo e duas imagens independentes.

## O que este projeto demonstra

- Corrigida quantidade e ordem de todos os parâmetros SQL.
- Imagens independentes, preservadas ao editar e recuperadas do banco.
- Vida e escudo usam os valores das barras.
- Conexões e consultas fechadas automaticamente.
- Configuração externa, script de criação do banco e chamadas ao banco fora da thread da interface.

## Executar no Windows

1. Para usar o JAR já gerado nesta cópia local, tenha Java 8 ou superior e abra `executar.bat`.
2. Para recompilar, use JDK 17 ou superior e abra `compilar.bat`. O script também procura o JDK incluído no Eclipse.
3. Para compilar e rodar as verificações, abra `verificar.bat`.

O JAR fica em `dist/cadastro-fortnite-java.jar`. Os scripts não instalam programas nem alteram o Java do sistema.

## Abrir na IDE ou usar Maven

Abra a pasta pelo `pom.xml` no Eclipse, IntelliJ IDEA ou outra IDE com suporte a Maven.
Com JDK 17+ e Maven instalados:

```sh
mvn package
mvn exec:java -Dexec.mainClass=view.JFFornite
```

O Maven produz o JAR em `target/`; os scripts do Windows produzem em `dist/`.
O comando Maven não roda os testes de contrato automaticamente. Use `verificar.bat`, ou:

```sh
mvn test-compile exec:java -Dexec.mainClass=testes.Testes -Dexec.classpathScope=test
```

## Estrutura

- `src/main/java`: código-fonte.
- `src/main/resources`: recursos visuais, quando houver.
- `src/test/java`: verificações sem dependências de teste externas.
- `ORIGEM.md`: fontes usadas e histórico das correções.
- `.gitignore`: evita versionar arquivos gerados e configurações locais.

## Publicar no GitHub

Use esta pasta como um repositório independente. Faça o primeiro commit com os fontes, README e configuração de build.
`build/`, `dist/` e `target/` são gerados localmente e estão no `.gitignore`.
O arquivo `db.properties`, quando existir, deve continuar apenas no seu computador.
Não foi escolhida uma licença nesta organização dos materiais; confirme a autoria e os créditos ao publicar.


## Configurar o banco

1. Tenha um servidor MySQL 8.0 ou superior disponível.
2. Execute manualmente `database/schema.sql` no MySQL.
3. Configure um usuário com acesso ao banco `fortnite`.
4. Copie `db.properties.example` para `db.properties` e preencha URL, usuário e senha.
5. Instale o driver em `lib/` seguindo `lib/LEIA-ME.md`.
6. Use `executar.bat`. As imagens são opcionais e cada arquivo pode ter até 5 MB.

O código do projeto compila sem o driver, mas as operações no MySQL exigem o driver e o banco configurados.
Os testes JDBC usam objetos simulados: não validam um servidor MySQL real nem alteram dados.

Referência do driver: https://dev.mysql.com/doc/relnotes/connector-j/en/news-8-4-0.html

