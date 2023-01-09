package be.ehb.rebruxdef.viewmodel;

import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.app.Application;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import be.ehb.rebruxdef.models.Reports;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportsViewModel extends AndroidViewModel {

    MutableLiveData<ArrayList<Reports>> reports;
    String responseString;

    public ReportsViewModel(@NonNull Application application) {
        super(application);
        reports = new MutableLiveData<>();
        responseString = "";
    }

    public MutableLiveData<ArrayList<Reports>> getReports() {
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
                        Log.d("REPORTS", "REPORTS NULL");
                        reports = new MutableLiveData<>();
                    } else {
                        Log.d("REPORTS", "REPORTS NOT NULL");
                        ArrayList<Reports> items = new ArrayList<>();


                        JSONArray reportsRaw = new JSONArray(response);
                        int length = reportsRaw.length();

                        for (int i = 0; i < length; i++) {
                            JSONObject reportRaw = reportsRaw.getJSONObject(i);

                            Reports reportParsed = new Reports(
                                    reportRaw.getString("_id"),
                                    reportRaw.getString("creator"),
                                    reportRaw.getString("image"),
                                    reportRaw.getString("description"),
                                    reportRaw.getString("street"),
                                    reportRaw.getString("city"),
                                    reportRaw.getString("zip"),
                                    reportRaw.getString("createdAt"),
                                    reportRaw.getBoolean("cleaned"),
                                    reportRaw.getDouble("lat"),
                                    reportRaw.getDouble("lng")
                            );
                            items.add(reportParsed);
                        }
                        reports.postValue(items);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return reports;
    }

}
