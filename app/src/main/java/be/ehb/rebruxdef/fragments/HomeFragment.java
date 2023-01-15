package be.ehb.rebruxdef.fragments;

import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentHomeBinding;
import be.ehb.rebruxdef.models.Reports;
import be.ehb.rebruxdef.viewmodel.ReportsViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GoogleMap mGoogleMap;
    ArrayList<Reports> reports = new ArrayList<>();

    public static Location currentLocation;
    FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CODE = 101;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            showReportsOnMap();
            getCurrentLocation();
//            onTestingLocation();

            final GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", marker.getTitle());
                    bundle.putString("imageTitle", marker.getSnippet());
                    replaceFragment(new DetailsFragment(), bundle);
                    return true;
                }
            };
            mGoogleMap.setOnMarkerClickListener(onMarkerClickListener);
        }

    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mapView.getMapAsync(onMapReadyCallback);
        binding.mapView.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.onDestroy();
        binding = null;
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void showReportsOnMap() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports")
                            .get()
                            .build();
                    String response = client.newCall(request).execute().body().string();

                    ArrayList<Reports> items = new ArrayList<>();

                    JSONArray reportsRaw = new JSONArray(response);
                    int length = reportsRaw.length();

                    for (int i = 0; i < length; i++) {
                        JSONObject reportRaw = reportsRaw.getJSONObject(i);
                        Log.d("CREATED", reportRaw.getString("createdAt"));

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
                    reports = items;
                    Log.d("USER", userID);
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

            for (int i = 0; i < reports.size(); i++) {
                drawAnnotations(reports.get(i).getLat(), reports.get(i).getLng(), reports.get(i).getId(), reports.get(i).getImage(), reports.get(i).getCleaned());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawAnnotations(double lat, double lng, String id, String imageTitle, Boolean isCleaned) {
        LatLng coordGroteMarkt = new LatLng(lat,lng);
        int icon;
        if (isCleaned) {
            icon = R.drawable.ic_map_green;
        } else {
            icon = R.drawable.ic_map_red;
        }

        mGoogleMap.addMarker(new MarkerOptions()
                .position(coordGroteMarkt)
                .title(id)
                .snippet(imageTitle)
                .icon(BitmapDescriptorFactory.fromResource(icon))
        );
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //LatLng coorden = new LatLng(location.getLatitude(),location.getLongitude());
                    LatLng coorden = new LatLng(50.8365808,4.308187);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coorden, 12);
                    mGoogleMap.animateCamera(cameraUpdate);
                } else {
                    Toast.makeText(getContext(), "Your current location is undefined, pls give rebrux permission.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
