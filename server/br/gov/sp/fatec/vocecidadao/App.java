package gov.sp.fatec.vocecidadao;


import gov.sp.fatec.vocecidadao.config.CorsFilter;
import gov.sp.fatec.vocecidadao.restapi.Restapi;

public class App{

	public static void main(String[] args) {
			
		CorsFilter.apply();
		 
		Restapi.getRoutes();
		
	}

}
