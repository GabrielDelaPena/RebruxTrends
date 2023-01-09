package be.ehb.rebruxdef.util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.fragments.DetailsFragment;
import be.ehb.rebruxdef.models.Reports;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    protected class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView date, street, city;
        Button button;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_report_date);
            street = itemView.findViewById(R.id.tv_report_street);
            city = itemView.findViewById(R.id.tv_report_city);
            button = itemView.findViewById(R.id.btn_report_row_details);
        }
    }

    private ArrayList<Reports> items;
    FragmentActivity activity;

    public ReportAdapter(FragmentActivity r) {
        this.activity = r;
        this.items = new ArrayList<>();
    }

    public void setItems(ArrayList<Reports> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_report_row, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Reports current = items.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("key", current.getId());
        bundle.putString("imageTitle", current.getImage());
        holder.date.setText(current.getCreatedAt());
        holder.street.setText(current.getStreet());
        holder.city.setText(current.getCity());
        holder.button.setOnClickListener(
                (View v) -> {
                    replaceFragment(new DetailsFragment(), bundle);
                }
        );
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
