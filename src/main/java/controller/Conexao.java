package controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Conexao {
    private Conexao() { }
    public static Connection abrir() throws SQLException {
        Properties arquivo = new Properties();
        Path caminho = Paths.get("db.properties");
        if (!Files.isRegularFile(caminho))
            throw new SQLException("Copie db.properties.example para db.properties e configure o MySQL.");
        try (InputStream entrada = Files.newInputStream(caminho)) {
            arquivo.load(entrada);
        } catch (java.io.IOException erro) {
            throw new SQLException("Não foi possível ler db.properties.", erro);
        }
        String url = arquivo.getProperty("db.url", "jdbc:mysql://localhost:3306/fortnite").trim();
        Properties acesso = new Properties();
        acesso.setProperty("user", arquivo.getProperty("db.user", "fortnite_app"));
        acesso.setProperty("password", arquivo.getProperty("db.password", ""));
        acesso.setProperty("connectTimeout", "5000");
        acesso.setProperty("socketTimeout", "10000");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException erro) {
            throw new SQLException("Driver MySQL ausente. Consulte o README para instalar o Connector/J.", erro);
        }
        return DriverManager.getConnection(url, acesso);
    }
}
