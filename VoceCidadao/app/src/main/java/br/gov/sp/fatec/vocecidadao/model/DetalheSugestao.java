package br.gov.sp.fatec.vocecidadao.model;

public class DetalheSugestao {
	
	private String latitude;
	private String longitude;
	private String endereco;
	private String imagem;

	public DetalheSugestao() {
	}

	public DetalheSugestao(String latitude, String longitude, String endereco, String imagem) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.endereco = endereco;
		this.imagem = imagem;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getEndereco() {
		return endereco;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public void setEndereco(String endereco) {

		this.endereco = endereco;
	}

	public void setLongitude(String longitude) {

		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
