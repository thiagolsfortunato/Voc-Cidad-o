package br.gov.sp.fatec.vocecidadao.service;

import java.util.List;

import br.gov.sp.fatec.vocecidadao.model.DetalheSugestao;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Di Nizo on 08/11/2016.
 */

public interface SugestaoService {

    //https://api.github.com/
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://172.16.55.142:4567/map/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @GET("/all")
    Call<List<DetalheSugestao>> todasSugestoes();

    @POST("/map/sugestao")
    Call<DetalheSugestao> inserirSugestao(@Body DetalheSugestao detalheSugestao);


}
