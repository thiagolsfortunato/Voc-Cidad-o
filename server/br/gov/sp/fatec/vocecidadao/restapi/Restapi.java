package gov.sp.fatec.vocecidadao.restapi;

import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;

import java.util.List;

import com.google.gson.Gson;

import gov.sp.fatec.vocecidadao.config.CorsFilter;
import gov.sp.fatec.vocecidadao.entity.DetalheSugestao;
import gov.sp.fatec.vocecidadao.repository.SugestaoRepository;
import gov.sp.fatec.vocecidadao.util.JsonUtil; 
public class Restapi {
	public static void getRoutes() {
		
		Gson gson = new Gson();
		SugestaoRepository repository = new SugestaoRepository();
		
		options("/map/*", (req, res) -> {
			res.status(200);
			return CorsFilter.getCorsheaders();
		});
		
		
		post("/map/sugestao/", (req, res) -> {
			String competenceData = req.body();
			
			byte ptext[] = competenceData.getBytes("ISO-8859-1"); 
			String value = new String(ptext, "UTF-8"); 
			
			DetalheSugestao sugestao = gson.fromJson(value, DetalheSugestao.class);
			boolean operation = false;
			try{
				operation = repository.addSugestao(sugestao);
				if(operation){
					res.status(200);
					return "SUCESS";
				}else{
					res.status(600);
					return "FAIL";
				}
			}catch(Exception e){
				e.printStackTrace();
				return "ops, an error with inserting, check the fields!";
			}
		}, JsonUtil.json());
		
		get("/map/all", (req, res) -> {
			List<DetalheSugestao> detalhes = null;
			try{
				detalhes = repository.todasSugestoes();
				if(detalhes.size() > 0){
					res.status(200);
					return detalhes;
				}else{
					res.status(600);
					return "FAIL";
				}
			}catch(NullPointerException e){
				e.printStackTrace();
				return "FAIL";
			}
		} , JsonUtil.json());
		
	}
}
