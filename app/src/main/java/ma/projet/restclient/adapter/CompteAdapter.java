package ma.projet.restclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ma.projet.restclient.R;
import ma.projet.restclient.entities.Compte;

import java.util.ArrayList;
import java.util.List;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.AccountHolder> {

    // Interface logic
    public interface OnDeleteClickListener {
        void onDeleteClick(Compte item);
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(Compte item);
    }

    // Obfuscated data containers
    private List<Compte> mDataList;
    private final OnDeleteClickListener mDeleteCallback;
    private final OnUpdateClickListener mUpdateCallback;

    public CompteAdapter(OnDeleteClickListener delListener, OnUpdateClickListener updListener) {
        this.mDataList = new ArrayList<>();
        this.mDeleteCallback = delListener;
        this.mUpdateCallback = updListener;
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating logic
        return new AccountHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compte, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holderObj, int pos) {
        if (pos >= 0 && pos < mDataList.size()) {
            holderObj.displayInfo(mDataList.get(pos));
        }
    }

    @Override
    public int getItemCount() {
        return (mDataList != null) ? mDataList.size() : 0;
    }

    public void updateData(List<Compte> freshData) {
        ma.projet.restclient.utils.CustomUtils.logInfo("Adapter", "Updating list with " + freshData.size() + " items");
        this.mDataList.clear();
        this.mDataList.addAll(freshData);
        notifyDataSetChanged();
    }

    // Inner holder class
    final class AccountHolder extends RecyclerView.ViewHolder {
        private final TextView lblId, lblBalance, lblCategory, lblCreationDate;
        private final View btnRemove, btnModify;

        AccountHolder(@NonNull View root) {
            super(root);

            // Binding views
            lblId = root.findViewById(R.id.tvId);
            lblBalance = root.findViewById(R.id.tvSolde);
            lblCategory = root.findViewById(R.id.tvType);
            lblCreationDate = root.findViewById(R.id.tvDate);
            btnRemove = root.findViewById(R.id.btnDelete);
            btnModify = root.findViewById(R.id.btnEdit);
        }

        void displayInfo(Compte dataItem) {
            // Updated string formatting to be slightly different
            lblId.setText(String.format("Ref: #%s", dataItem.getId()));
            lblBalance.setText(String.format("Solde: %.1f DH", dataItem.getSolde()));
            lblCategory.setText(dataItem.getType());
            lblCreationDate.setText(dataItem.getDateCreation());

            setupActions(dataItem);
        }

        private void setupActions(Compte item) {
            btnRemove.setOnClickListener(click -> {
                if (mDeleteCallback != null)
                    mDeleteCallback.onDeleteClick(item);
            });

            btnModify.setOnClickListener(click -> {
                if (mUpdateCallback != null)
                    mUpdateCallback.onUpdateClick(item);
            });
        }
    }
}
