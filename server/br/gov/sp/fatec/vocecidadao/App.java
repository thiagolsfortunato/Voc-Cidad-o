package gov.sp.fatec.vocecidadao;

import java.util.LinkedList;
import java.util.List;

import gov.sp.fatec.vocecidadao.config.CorsFilter;
import gov.sp.fatec.vocecidadao.entity.DetalheSugestao;
import gov.sp.fatec.vocecidadao.repository.SugestaoRepository;
import gov.sp.fatec.vocecidadao.restapi.Restapi;

public class App{

	public static void main(String[] args) {
			
		//CorsFilter.apply();
		 
		//Restapi.getRoutes();
		
		SugestaoRepository repository = new SugestaoRepository();
		List<DetalheSugestao> sugestoes = new LinkedList();
		sugestoes = repository.todasSugestoes();
		for(DetalheSugestao det:sugestoes){
			System.out.println("Latitude: "+det.getLatitude());
			System.out.println("Longitue: "+det.getLongitude());
			System.out.println("Endereço: "+det.getEndereco());
			System.out.println("Imagem: "+det.getImagem());
		}
		
	}

}
