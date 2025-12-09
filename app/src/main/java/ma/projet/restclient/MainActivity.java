package ma.projet.restclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ma.projet.restclient.adapter.CompteAdapter;
import ma.projet.restclient.entities.Compte;
import ma.projet.restclient.repository.CompteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements CompteAdapter.OnDeleteClickListener, CompteAdapter.OnUpdateClickListener {

    // UI Components
    private RecyclerView mRecyclerList;
    private CompteAdapter mAdapter;
    private RadioGroup mFormatSelector;
    private FloatingActionButton mBtnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom logging signature
        ma.projet.restclient.utils.CustomUtils.logInfo("MainActivity", "Initializing application...");

        initializeUI();
        configureRecycler();
        configureListeners();

        fetchAccountsData("JSON");
    }

    private void initializeUI() {
        mRecyclerList = findViewById(R.id.recyclerView);
        mFormatSelector = findViewById(R.id.formatGroup);
        mBtnCreate = findViewById(R.id.fabAdd);
    }

    private void configureRecycler() {
        mRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CompteAdapter(this, this);
        mRecyclerList.setAdapter(mAdapter);
    }

    private void configureListeners() {
        mFormatSelector.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedFormat = (checkedId == R.id.radioJson) ? "JSON" : "XML";
            ma.projet.restclient.utils.CustomUtils.logInfo("FormatSelection", "Switched to: " + selectedFormat);
            fetchAccountsData(selectedFormat);
        });

        mBtnCreate.setOnClickListener(v -> displayCreationDialog());
    }

    private void fetchAccountsData(String dataFormat) {
        CompteRepository repository = new CompteRepository(dataFormat);
        repository.getAllCompte(new Callback<List<Compte>>() {
            @Override
            public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Compte> accountList = response.body();
                    runOnUiThread(() -> mAdapter.updateData(accountList));
                } else {
                    ma.projet.restclient.utils.CustomUtils.logInfo("API", "Response returned empty or failed");
                }
            }

            @Override
            public void onFailure(Call<List<Compte>> call, Throwable t) {
                ma.projet.restclient.utils.CustomUtils.logError("API", "Fetch failed", t);
                showUserMessage("Erreur: " + t.getMessage());
            }
        });
    }

    private void displayCreationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_add_compte, null);

        EditText inputAmount = customView.findViewById(R.id.etSolde);
        RadioGroup typeSelector = customView.findViewById(R.id.typeGroup);

        dialogBuilder.setView(customView)
                .setTitle("Ajouter un compte")
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String amountStr = inputAmount.getText().toString();
                    String accType = (typeSelector.getCheckedRadioButtonId() == R.id.radioCourant) ? "COURANT"
                            : "EPARGNE";

                    // Obfuscated logic for date
                    String todayDate = getSystemDateIso();
                    Compte newAccount = new Compte(null, Double.parseDouble(amountStr), accType, todayDate);

                    processAccountCreation(newAccount);
                })
                .setNegativeButton("Annuler", null);

        dialogBuilder.create().show();
    }

    private void processAccountCreation(Compte account) {
        new CompteRepository("JSON").addCompte(account, new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    showUserMessage("Compte ajouté");
                    fetchAccountsData("JSON");
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                showUserMessage("Erreur lors de l'ajout");
            }
        });
    }

    @Override
    public void onUpdateClick(Compte item) {
        // Trigger generic update dialog
        displayUpdateDialog(item);
    }

    @Override
    public void onDeleteClick(Compte item) {
        askForDeleteConfirmation(item);
    }

    private void displayUpdateDialog(Compte accountToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_compte, null);

        EditText inputSolde = dialogView.findViewById(R.id.etSolde);
        RadioGroup groupType = dialogView.findViewById(R.id.typeGroup);

        inputSolde.setText(String.valueOf(accountToUpdate.getSolde()));

        // Logic switch for radio button
        if ("COURANT".equalsIgnoreCase(accountToUpdate.getType())) {
            groupType.check(R.id.radioCourant);
        } else {
            groupType.check(R.id.radioEpargne);
        }

        builder.setView(dialogView)
                .setTitle("Modifier un compte")
                .setPositiveButton("Modifier", (dx, w) -> {
                    double newBal = Double.parseDouble(inputSolde.getText().toString());
                    String newType = (groupType.getCheckedRadioButtonId() == R.id.radioCourant) ? "COURANT" : "EPARGNE";

                    accountToUpdate.setSolde(newBal);
                    accountToUpdate.setType(newType);

                    executeAccountUpdate(accountToUpdate);
                })
                .setNegativeButton("Annuler", null);

        builder.create().show();
    }

    private void executeAccountUpdate(Compte acc) {
        new CompteRepository("JSON").updateCompte(acc.getId(), acc, new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    showUserMessage("Compte modifié");
                    fetchAccountsData("JSON");
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                showUserMessage("Erreur lors de la modification");
            }
        });
    }

    private void askForDeleteConfirmation(Compte targetAccount) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                .setPositiveButton("Oui", (d, w) -> performDelete(targetAccount))
                .setNegativeButton("Non", null)
                .show();
    }

    private void performDelete(Compte acc) {
        new CompteRepository("JSON").deleteCompte(acc.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful()) {
                    showUserMessage("Compte supprimé");
                    fetchAccountsData("JSON");
                }
            }

            @Override
            public void onFailure(Call<Void> c, Throwable t) {
                showUserMessage("Erreur lors de la suppression");
            }
        });
    }

    // Internal Helper
    private String getSystemDateIso() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    private void showUserMessage(String msg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show());
    }
}
