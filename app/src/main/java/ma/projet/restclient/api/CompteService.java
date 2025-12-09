package ma.projet.restclient.api;

import ma.projet.restclient.entities.Compte;
import ma.projet.restclient.entities.CompteList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CompteService {
    @GET("banque/comptes")
    @Headers({ "Accept: application/json", "X-Client-Type: Android" })
    Call<List<Compte>> apiListAccountsJson();

    @GET("banque/comptes")
    @Headers({ "Accept: application/xml", "X-Client-Type: Android" })
    Call<CompteList> apiListAccountsXml();

    @GET("banque/comptes/{id}")
    Call<Compte> apiGetAccount(@Path("id") Long id);

    @POST("banque/comptes")
    Call<Compte> apiCreateAccount(@Body Compte compte);

    @PUT("banque/comptes/{id}")
    Call<Compte> apiUpdateAccount(@Path("id") Long id, @Body Compte compte);

    @DELETE("banque/comptes/{id}")
    Call<Void> apiDeleteAccount(@Path("id") Long id);
}
