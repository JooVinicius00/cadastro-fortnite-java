package view;

import controller.FortniteDados;
import model.Fortnite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Interface do cadastro original, com persistência e imagens corrigidas. */
public class JFFornite extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JTextField tfId = new JTextField(7);
    private final JTextField tfNome = new JTextField(24);
    private final JProgressBar vida = new JProgressBar(0, 100);
    private final JProgressBar escudo = new JProgressBar(0, 100);
    private final JLabel lblSkin = imagemLabel("Skin");
    private final JLabel lblPicareta = imagemLabel("Picareta");
    private final JLabel status = new JLabel("Novo cadastro. Preencha o nome e escolha as imagens opcionais.");
    private final List<JComponent> controles = new ArrayList<>();
    private final FortniteDados dados = new FortniteDados();
    private Fortnite atual = new Fortnite();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JFFornite().setVisible(true));
    }
    public JFFornite() {
        super("Cadastro Fortnite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(raiz);
        JPanel campos = new JPanel();
        campos.add(new JLabel("ID:")); campos.add(tfId);
        campos.add(botao("Pesquisar", e -> pesquisar()));
        campos.add(new JLabel("Nome:")); campos.add(tfNome);
        controles.add(tfId); controles.add(tfNome);
        raiz.add(campos, BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(1, 2, 12, 0));
        centro.add(painelImagem(lblSkin, "Selecionar skin", true));
        centro.add(painelImagem(lblPicareta, "Selecionar picareta", false));
        raiz.add(centro, BorderLayout.CENTER);
        JPanel inferior = new JPanel(new GridLayout(4, 1, 4, 4));
        inferior.add(painelBarra("Vida", vida));
        inferior.add(painelBarra("Escudo", escudo));
        JPanel acoes = new JPanel();
        acoes.add(botao("Novo / limpar", e -> limpar()));
        acoes.add(botao("Salvar", e -> salvar()));
        acoes.add(botao("Excluir", e -> excluir()));
        acoes.add(botao("Sair", e -> dispose()));
        inferior.add(acoes); inferior.add(status);
        raiz.add(inferior, BorderLayout.SOUTH);
        limpar();
        pack();
        setMinimumSize(new Dimension(780, 560));
        setLocationRelativeTo(null);
    }
    private static JLabel imagemLabel(String titulo) {
        JLabel label = new JLabel("Sem imagem", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(300, 230));
        label.setBorder(BorderFactory.createTitledBorder(titulo));
        return label;
    }
    private JButton botao(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(acao);
        controles.add(botao);
        return botao;
    }
    private JPanel painelImagem(JLabel label, String texto, boolean skin) {
        JPanel painel = new JPanel(new BorderLayout(4, 4));
        painel.add(label, BorderLayout.CENTER);
        painel.add(botao(texto, e -> selecionarImagem(skin)), BorderLayout.SOUTH);
        return painel;
    }
    private JPanel painelBarra(String texto, JProgressBar barra) {
        JPanel painel = new JPanel(new BorderLayout(8, 0));
        barra.setStringPainted(true);
        painel.add(new JLabel(texto), BorderLayout.WEST);
        painel.add(barra, BorderLayout.CENTER);
        JPanel acoes = new JPanel();
        acoes.add(botao("-5", e -> barra.setValue(barra.getValue() - 5)));
        acoes.add(botao("+5", e -> barra.setValue(barra.getValue() + 5)));
        painel.add(acoes, BorderLayout.EAST);
        return painel;
    }
    private void limpar() {
        atual = new Fortnite();
        tfId.setText(""); tfNome.setText("");
        vida.setValue(100); escudo.setValue(100);
        mostrarImagem(lblSkin, null); mostrarImagem(lblPicareta, null);
        status.setText("Novo cadastro. As imagens são opcionais; pesquise um ID para editar.");
        tfNome.requestFocusInWindow();
    }
    private void selecionarImagem(boolean skin) {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(new FileNameExtensionFilter("Imagens PNG e JPEG", "png", "jpg", "jpeg"));
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            if (Files.size(seletor.getSelectedFile().toPath()) > 5L * 1024 * 1024)
                throw new IOException("A imagem deve ter até 5 MB.");
            byte[] bytes = Files.readAllBytes(seletor.getSelectedFile().toPath());
            if (ImageIO.read(new ByteArrayInputStream(bytes)) == null) throw new IOException("Arquivo de imagem inválido.");
            if (skin) { atual.setSkin(bytes); mostrarImagem(lblSkin, bytes); }
            else { atual.setPicareta(bytes); mostrarImagem(lblPicareta, bytes); }
        } catch (IOException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage());
        }
    }
    private void mostrarImagem(JLabel label, byte[] bytes) {
        label.setIcon(null); label.setText("Sem imagem");
        if (bytes == null || bytes.length == 0) return;
        try {
            java.awt.image.BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(bytes));
            if (imagem == null) { label.setText("Imagem inválida"); return; }
            double escala = Math.min(280.0 / imagem.getWidth(), 210.0 / imagem.getHeight());
            Image redimensionada = imagem.getScaledInstance(Math.max(1, (int) (imagem.getWidth() * escala)),
                Math.max(1, (int) (imagem.getHeight() * escala)), Image.SCALE_SMOOTH);
            label.setText(""); label.setIcon(new ImageIcon(redimensionada));
        } catch (IOException erro) { label.setText("Imagem inválida"); }
    }
    private int lerId() {
        int id = Integer.parseInt(tfId.getText().trim());
        if (id <= 0) throw new IllegalArgumentException("Informe um ID positivo.");
        return id;
    }
    private void pesquisar() {
        try {
            int id = lerId();
            tarefa(() -> dados.pesquisar(id), resultado -> {
                if (resultado == null) { limpar(); status.setText("Registro não encontrado."); return; }
                atual = resultado;
                tfId.setText(String.valueOf(atual.getId())); tfNome.setText(atual.getNome());
                vida.setValue(atual.getVida()); escudo.setValue(atual.getEscudo());
                mostrarImagem(lblSkin, atual.getSkin()); mostrarImagem(lblPicareta, atual.getPicareta());
                status.setText("Editando ID " + atual.getId() + ". As imagens atuais serão preservadas ao salvar.");
            });
        } catch (IllegalArgumentException erro) { JOptionPane.showMessageDialog(this, "Informe um ID positivo para pesquisar."); }
    }
    private void salvar() {
        try {
            boolean editar = atual.getId() > 0;
            if ((editar && lerId() != atual.getId()) || (!editar && !tfId.getText().trim().isEmpty()))
                throw new IllegalArgumentException("Pesquise o ID antes de editar ou use Novo para cadastrar.");
            atual.setNome(tfNome.getText()); atual.setVida(vida.getValue()); atual.setEscudo(escudo.getValue());
            atual.validar();
            Fortnite registro = atual;
            tarefa(() -> editar ? dados.editar(registro) : dados.incluir(registro), sucesso -> {
                if (sucesso) {
                    atual = registro;
                    tfId.setText(String.valueOf(atual.getId()));
                    status.setText("Registro salvo. ID: " + atual.getId());
                }
                else status.setText("Nenhum registro foi alterado. Pesquise novamente.");
            });
        } catch (IllegalArgumentException erro) { JOptionPane.showMessageDialog(this, erro.getMessage()); }
    }
    private void excluir() {
        try {
            int id = lerId();
            if (JOptionPane.showConfirmDialog(this, "Excluir o registro " + id + "?", "Excluir", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            tarefa(() -> dados.excluir(id), sucesso -> {
                limpar(); status.setText(sucesso ? "Registro excluído." : "Registro não encontrado.");
            });
        } catch (IllegalArgumentException erro) { JOptionPane.showMessageDialog(this, "Informe um ID positivo para excluir."); }
    }
    private <T> void tarefa(Callable<T> trabalho, Consumer<T> concluido) {
        for (JComponent controle : controles) controle.setEnabled(false);
        status.setText("Acessando o banco de dados...");
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return trabalho.call(); }
            @Override protected void done() {
                try { concluido.accept(get()); }
                catch (Exception erro) {
                    Throwable causa = erro.getCause() == null ? erro : erro.getCause();
                    status.setText("Operação não concluída.");
                    JOptionPane.showMessageDialog(JFFornite.this, causa.getMessage(), "Banco de dados", JOptionPane.ERROR_MESSAGE);
                } finally { for (JComponent controle : controles) controle.setEnabled(true); }
            }
        }.execute();
    }
}
