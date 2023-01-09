package be.ehb.rebruxdef.fragments;

import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentDetailsBinding;
import be.ehb.rebruxdef.models.Reports;
import be.ehb.rebruxdef.util.MyHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    TextView creator, street, zip, city, status, createdAt, description;
    Button btnCleaned;
    Reports report = new Reports();
    int responseStatus;
    int StatusDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        creator = binding.tvDetailsCreator;
        street = binding.tvDetailsStreet;
        zip = binding.tvDetailsZip;
        city = binding.tvDetailsCity;
        status = binding.tvDetailsStatus;
        createdAt = binding.tvDetailsCreatedAt;
        description = binding.tvDetailsDescription;
        btnCleaned = binding.btnDetailsCleaned;
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        onReportDetailsAPICall(bundle);

        btnCleaned.setOnClickListener(
                (View v) -> {
                    onReportCleanedAPICall(bundle);
                    MyHandler handler = new MyHandler(getActivity());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onReportDeleteAPICall(bundle, handler.getActivityRef().get());
                        }
                    }, 10000);
                }
        );

    }

    @SuppressLint("SetTextI18n")
    public void onReportDetailsAPICall(Bundle bundleData) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports/report/" + bundleData.getString("key"))
                            .get()
                            .build();
                    String response = client.newCall(request).execute().body().string();


                    JSONObject reportRaw = new JSONObject(response);
                    report.setId(reportRaw.getString("_id"));
                    report.setCreator(reportRaw.getString("creator"));
                    report.setImage(reportRaw.getString("image"));
                    report.setDescription(reportRaw.getString("description"));
                    report.setStreet(reportRaw.getString("street"));
                    report.setCity(reportRaw.getString("city"));
                    report.setZip(reportRaw.getString("zip"));
                    report.setCleaned(reportRaw.getBoolean("cleaned"));
                    report.setCreatedAt(reportRaw.getString("createdAt"));

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
            creator.setText(report.getCreator());
            street.setText(report.getStreet());
            zip.setText(report.getZip());
            city.setText(report.getCity());
            if (!report.getCleaned()) {
                status.setText("Not Cleaned.");
            } else {
                status.setText("Cleaned.");
            }
            createdAt.setText(report.getCreatedAt());
            description.setText(report.getDescription());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onReportCleanedAPICall(Bundle bundle) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\r\n    \"reportID\": \"" + bundle.getString("key") + "\"\r\n}");
                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports/cleaned")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    int response = client.newCall(request).execute().code();
                    responseStatus = response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            if (responseStatus == 200) {
                Toast.makeText(getActivity(), "Report will be deleted shortly.", Toast.LENGTH_SHORT).show();
                replaceFragment(new HomeFragment());
            } else {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void onReportDeleteAPICall(Bundle bunde, Activity activity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = RequestBody.create(mediaType, "");
                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports/delete/" + bunde.getString("key"))
                            .method("POST", body)
                            .build();
                    int response = client.newCall(request).execute().code();
                    StatusDelete = response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            if (StatusDelete == 200) {
                Toast.makeText(activity, "Report has been deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Something went wrong on deleting report.", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
