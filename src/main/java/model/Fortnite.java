package model;

public class Fortnite {

	private int id;
	private int vida;
	private int escudo;
	private byte[] skin;
	private byte[] picareta;
	private String nome;

	public Fortnite (int id, byte[] picareta, int vida, int escudo, byte[] skin, String nome) {
		super();
		this.id = id;
		this.skin = skin;
		this.nome = nome;
		this.vida = vida;
		this.escudo = escudo;
		this.picareta = picareta;
	}

	public Fortnite() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getSkin() {
		return skin;
	}

	public void setSkin(byte[] skin) {
		this.skin = skin;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public byte[] getPicareta() {
		return picareta;
	}

	public void setPicareta(byte[] picareta) {
		this.picareta = picareta;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

	public int getEscudo() {
		return escudo;
	}

	public void setEscudo(int escudo) {
		this.escudo = escudo;
	}


    public void validar() {
        if (nome == null || nome.trim().isEmpty() || nome.trim().length() > 100)
            throw new IllegalArgumentException("Informe um nome com até 100 caracteres.");
        if (vida < 0 || vida > 100 || escudo < 0 || escudo > 100)
            throw new IllegalArgumentException("Vida e escudo devem estar entre 0 e 100.");
        if ((skin != null && skin.length > 5 * 1024 * 1024) || (picareta != null && picareta.length > 5 * 1024 * 1024))
            throw new IllegalArgumentException("Cada imagem pode ter até 5 MB.");
        nome = nome.trim();
    }
}
