package gov.sp.fatec.vocecidadao.entity;

public class DetalheSugestao {
	
	private String latitude;
	private String longitude;
	private String endereco;
	private String imagem;
	
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
}
