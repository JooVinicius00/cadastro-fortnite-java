package testes;

import controller.FortniteDados;
import model.Fortnite;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

/** Testa o contrato JDBC sem conectar ou modificar um banco real. */
public class Testes {
    private static int verificacoes;
    private static void conferir(boolean valor) {
        verificacoes++;
        if (!valor) throw new AssertionError("Falha JDBC " + verificacoes);
    }
    private static class BancoFalso {
        String sql;
        Map<Integer, Object> parametros = new HashMap<>();
        int conexoesFechadas;
        int comandosFechados;
        int resultadosFechados;
        boolean encontrado = true;
        boolean falhar;
        byte[] skin = {1, 2};
        byte[] picareta = {3, 4};

        Connection abrir() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class}, (proxy, metodo, args) -> {
                if (metodo.getName().equals("prepareStatement")) { sql = (String) args[0]; parametros.clear(); return comando(); }
                if (metodo.getName().equals("close")) { conexoesFechadas++; return null; }
                return padrao(metodo.getReturnType());
            });
        }
        PreparedStatement comando() {
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{PreparedStatement.class}, (proxy, metodo, args) -> {
                String nome = metodo.getName();
                if (nome.equals("setString") || nome.equals("setInt") || nome.equals("setBytes")) {
                    parametros.put((Integer) args[0], args[1]); return null;
                }
                if (nome.equals("executeUpdate") || nome.equals("executeQuery")) {
                    long quantidade = sql.chars().filter(c -> c == '?').count();
                    conferir(parametros.size() == quantidade);
                    for (int i = 1; i <= quantidade; i++) conferir(parametros.containsKey(i));
                    if (falhar) throw new SQLException("Falha simulada");
                    return nome.equals("executeUpdate") ? Integer.valueOf(1) : resultado(false);
                }
                if (nome.equals("getGeneratedKeys")) return resultado(true);
                if (nome.equals("close")) { comandosFechados++; return null; }
                return padrao(metodo.getReturnType());
            });
        }
        ResultSet resultado(boolean chave) {
            final boolean[] primeiro = {true};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class}, (proxy, metodo, args) -> {
                if (metodo.getName().equals("next")) { boolean tem = primeiro[0] && encontrado; primeiro[0] = false; return tem; }
                if (metodo.getName().equals("getString")) return "Teste";
                if (metodo.getName().equals("getInt")) {
                    if (chave || args[0].equals("id")) return 7;
                    return args[0].equals("vida") ? 65 : 30;
                }
                if (metodo.getName().equals("getBytes")) return args[0].equals("skin") ? skin : picareta;
                if (metodo.getName().equals("close")) { resultadosFechados++; return null; }
                return padrao(metodo.getReturnType());
            });
        }
        Object padrao(Class<?> tipo) {
            if (tipo == boolean.class) return false;
            if (tipo == int.class) return 0;
            if (tipo == long.class) return 0L;
            return null;
        }
    }
    public static void main(String[] args) throws Exception {
        BancoFalso banco = new BancoFalso();
        FortniteDados dados = new FortniteDados(banco::abrir);
        Fortnite personagem = new Fortnite(7, banco.picareta, 65, 30, banco.skin, "Teste");
        conferir(dados.incluir(personagem));
        conferir(banco.parametros.get(1).equals("Teste"));
        conferir(banco.parametros.get(2).equals(65)); conferir(banco.parametros.get(3).equals(30));
        conferir(Arrays.equals((byte[]) banco.parametros.get(4), banco.picareta));
        conferir(Arrays.equals((byte[]) banco.parametros.get(5), banco.skin));
        conferir(dados.editar(personagem)); conferir(banco.parametros.get(6).equals(7));
        Fortnite encontrado = dados.pesquisar(7);
        conferir(encontrado.getId() == 7 && encontrado.getVida() == 65 && encontrado.getEscudo() == 30);
        conferir(Arrays.equals(encontrado.getSkin(), banco.skin));
        conferir(Arrays.equals(encontrado.getPicareta(), banco.picareta));
        banco.encontrado = false; conferir(dados.pesquisar(999) == null);
        conferir(dados.excluir(7)); conferir(banco.parametros.get(1).equals(7));
        conferir(banco.conexoesFechadas == 5 && banco.comandosFechados == 5);
        banco.falhar = true;
        try { dados.excluir(7); throw new AssertionError("Falha de banco ignorada"); }
        catch (SQLException esperada) { conferir(banco.conexoesFechadas == 6 && banco.comandosFechados == 6); }
        personagem.setVida(101);
        try { dados.incluir(personagem); throw new AssertionError("Vida inválida aceita"); }
        catch (IllegalArgumentException esperada) { conferir(true); }
        System.out.println("OK: " + verificacoes + " verificações JDBC simuladas; MySQL real não acessado.");
    }
}
