package gov.sp.fatec.vocecidadao.repository;

import java.util.LinkedList;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import gov.sp.fatec.vocecidadao.entity.DetalheSugestao;

public class SugestaoRepository {
	
	ObjectContainer sugestoes = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "br/gov/sp/fatec/vocecidadao/banco/sugestoes.db4o");
	

	public ObjectContainer getClientes() {
		return sugestoes;
	}

	public boolean addSugestao(DetalheSugestao sugestao){
		
		if(sugestao != null ){
			sugestoes.store(sugestao);
			sugestoes.commit();
			return true;
		}
		return false;
		
	}
	
	
	public List<DetalheSugestao> todasSugestoes(){
		List buscas = new LinkedList();
		Query q = sugestoes.query();
		q.constrain(DetalheSugestao.class);
    	ObjectSet<DetalheSugestao> result = q.execute();
		for(DetalheSugestao user:result){
			buscas.add(user);
		}
		return buscas;
	}
	

}
