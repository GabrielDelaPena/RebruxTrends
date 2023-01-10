package be.ehb.rebruxdef.fragments;
import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentProfileBinding;
import be.ehb.rebruxdef.models.Reports;
import be.ehb.rebruxdef.models.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    TextView tv_username, tv_email, tv_phone, tv_points;
    User user = new User();
    int reportsLength;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        tv_username = binding.tvProfileUsername;
        tv_email = binding.tvProfileEmail;
        tv_phone = binding.tvProfileNumber;
        tv_points = binding.tvProfilePoints;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getReports();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/users/" + userID)
                            .get()
                            .build();
                    String response = client.newCall(request).execute().body().string();
                    JSONObject reportRaw = new JSONObject(response);
                    user.setUsername(reportRaw.getString("username"));
                    user.setEmail(reportRaw.getString("email"));
                    user.setPhone(reportRaw.getString("phone"));
                    user.setPoints(reportRaw.getInt("points"));


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            tv_username.setText(user.getUsername());
            tv_email.setText(user.getEmail());
            tv_phone.setText(user.getPhone());
            tv_points.setText(String.valueOf(user.getPoints()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        binding.btnProfileEdit.setOnClickListener(
                (View v) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", user.getUsername());
                    bundle.putString("email", user.getEmail());
                    bundle.putString("phone", user.getPhone());
                    replaceFragmentWithBundle(new EditProfileFragment(), bundle);
                }
        );

        binding.btnProfileReports.setOnClickListener(
                (View v) -> {
                    Log.d("REPORTS LENGTH", String.valueOf(reportsLength));
                    if (reportsLength > 0) {
                        replaceFragment(new ReportsFragment(), "ReportsFragment");
                    } else {
                        Toast.makeText(getActivity(), "User has no reports yet, pls add one", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        binding.btnProfileLogout.setOnClickListener(
                (View v) -> {
                    userID = null;
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_startFragment_to_loginFragment);
                }
        );
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.commit();
    }

    private void replaceFragmentWithBundle(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
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

    public int getReports() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports/" + userID)
                            .get()
                            .build();
                    String response = client.newCall(request).execute().body().string();

                    if (response.equals("No reports for this user.")) {
                        Log.d("GETREPORT", "HAS NO REPORTS");
                    } else {
                        Log.d("GETREPORT", "HAS REPORTS");
                        JSONArray reportsRaw = new JSONArray(response);
                        reportsLength = reportsRaw.length();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return reportsLength;
    }

}
