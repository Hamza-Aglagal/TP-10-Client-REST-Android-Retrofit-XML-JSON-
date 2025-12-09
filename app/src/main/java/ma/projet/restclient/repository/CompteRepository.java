package ma.projet.restclient.repository;

import ma.projet.restclient.api.CompteService;
import ma.projet.restclient.entities.Compte;
import ma.projet.restclient.entities.CompteList;
import ma.projet.restclient.config.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompteRepository {
    private final CompteService mService;
    private final String mFormat;

    public CompteRepository(String formatType) {
        this.mService = RetrofitClient.getClient(formatType).create(CompteService.class);
        this.mFormat = formatType;
    }

    public void getAllCompte(Callback<List<Compte>> callback) {
        ma.projet.restclient.utils.CustomUtils.logInfo("Repo", "Fetching all accounts in " + mFormat);
        if ("JSON".equals(mFormat)) {
            mService.apiListAccountsJson().enqueue(callback);
        } else {
            mService.apiListAccountsXml().enqueue(new Callback<CompteList>() {
                @Override
                public void onResponse(Call<CompteList> call, Response<CompteList> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Compte> items = response.body().getComptes();
                        callback.onResponse(null, Response.success(items));
                    } else {
                        // Pass error downstream
                        callback.onResponse(null, Response.error(404, response.errorBody()));
                    }
                }

                @Override
                public void onFailure(Call<CompteList> call, Throwable t) {
                    callback.onFailure(null, t);
                }
            });
        }
    }

    public void getCompteById(Long id, Callback<Compte> callback) {
        mService.apiGetAccount(id).enqueue(callback);
    }

    public void addCompte(Compte compte, Callback<Compte> callback) {
        ma.projet.restclient.utils.CustomUtils.logInfo("Repo", "Adding account...");
        mService.apiCreateAccount(compte).enqueue(callback);
    }

    public void updateCompte(Long id, Compte compte, Callback<Compte> callback) {
        mService.apiUpdateAccount(id, compte).enqueue(callback);
    }

    public void deleteCompte(Long id, Callback<Void> callback) {
        ma.projet.restclient.utils.CustomUtils.logInfo("Repo", "Deleting account " + id);
        mService.apiDeleteAccount(id).enqueue(callback);
    }
}
