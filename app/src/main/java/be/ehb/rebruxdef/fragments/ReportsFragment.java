package be.ehb.rebruxdef.fragments;

import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentReportsBinding;
import be.ehb.rebruxdef.models.Reports;
import be.ehb.rebruxdef.util.ReportAdapter;
import be.ehb.rebruxdef.viewmodel.ReportsViewModel;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReportAdapter reportAdapter = new ReportAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.rvReports.setAdapter(reportAdapter);
        binding.rvReports.setLayoutManager(layoutManager);

        ReportsViewModel model = new ViewModelProvider(getActivity()).get(ReportsViewModel.class);
        model.getReports().observe(getViewLifecycleOwner(), new Observer<ArrayList<Reports>>() {
            @Override
            public void onChanged(ArrayList<Reports> reports) {
                Log.d("SIZE", String.valueOf(reports.size()));
                reportAdapter.setItems(reports);
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
