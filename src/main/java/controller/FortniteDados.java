package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Fortnite;

public class FortniteDados {
    @FunctionalInterface
    public interface FonteConexao { Connection abrir() throws SQLException; }
    private final FonteConexao fonte;
    public FortniteDados() { this(Conexao::abrir); }
    public FortniteDados(FonteConexao fonte) { this.fonte = fonte; }

    private static void idValido(int id) {
        if (id <= 0) throw new IllegalArgumentException("O ID deve ser positivo.");
    }
    private static void preencher(PreparedStatement ps, Fortnite personagem) throws SQLException {
        ps.setString(1, personagem.getNome());
        ps.setInt(2, personagem.getVida());
        ps.setInt(3, personagem.getEscudo());
        ps.setBytes(4, personagem.getPicareta());
        ps.setBytes(5, personagem.getSkin());
    }
    public boolean incluir(Fortnite personagem) throws SQLException {
        personagem.validar();
        String sql = "INSERT INTO usuario (nome, vida, escudo, picareta, skin) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = fonte.abrir(); PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, personagem);
            if (ps.executeUpdate() != 1) return false;
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) personagem.setId(chaves.getInt(1));
            }
            return true;
        }
    }
    public Fortnite pesquisar(int id) throws SQLException {
        idValido(id);
        try (Connection con = fonte.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT id, nome, vida, escudo, picareta, skin FROM usuario WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Fortnite(rs.getInt("id"), rs.getBytes("picareta"), rs.getInt("vida"),
                    rs.getInt("escudo"), rs.getBytes("skin"), rs.getString("nome"));
            }
        }
    }
    public boolean editar(Fortnite personagem) throws SQLException {
        personagem.validar();
        idValido(personagem.getId());
        String sql = "UPDATE usuario SET nome = ?, vida = ?, escudo = ?, picareta = ?, skin = ? WHERE id = ?";
        try (Connection con = fonte.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, personagem);
            ps.setInt(6, personagem.getId());
            return ps.executeUpdate() == 1;
        }
    }
    public boolean excluir(int id) throws SQLException {
        idValido(id);
        try (Connection con = fonte.abrir(); PreparedStatement ps = con.prepareStatement("DELETE FROM usuario WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}
