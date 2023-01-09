package be.ehb.rebruxdef.fragments;
import static be.ehb.rebruxdef.fragments.HomeFragment.currentLocation;
import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentAddBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;
    Button save;
    EditText image, description;
    String street, number, zip, city;
    TextView tv_street, tv_zip, tv_city, tv_lat, tv_lon, tv_userID;
    File imageFile;
    int status;
    Bitmap imageBitmap;
    long timeInSeconds = System.currentTimeMillis() / 1000;
    String timeInSecondsString = Long.toString(timeInSeconds);
    String capturedImage;

    // u zus 3

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        save = binding.btnAddSave;
        image = binding.etAddImage;
        description = binding.etAddDescription;

        tv_street = binding.tvAddStreet;
        tv_zip = binding.tvAddZip;
        tv_city = binding.tvAddCity;

        tv_lat = binding.tvAddLat;
        tv_lon = binding.tvAddLon;
        tv_userID = binding.tvAddUserid;
        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onGetLocation();

        tv_street.setText(street + " " + number);
        tv_zip.setText(zip);
        tv_city.setText(city);

        tv_lat.setText(String.valueOf(currentLocation.getLatitude()));
        tv_lon.setText(String.valueOf(currentLocation.getLongitude()));
        tv_userID.setText(userID);

        save.setOnClickListener(
                (View v) -> {
                    onAddReportCall();
                    //onTestingImage();
                    //onSaveImageGallery(imageBitmap, timeInSecondsString);
                }
        );

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }

        binding.btnAddOpencamera.setOnClickListener(
                (View v) -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 101);
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onAddReportCall() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType,
                            "{\r\n    \"creator\": \"" + userID +
                            "\",\r\n    \"image\": \"" + timeInSecondsString +
                            "\",\r\n    \"description\": \"" + description.getText().toString() +
                            "\",\r\n    \"street\": \"" + street + " " + number +
                            "\",\r\n    \"city\": \"" + city +
                            "\",\r\n    \"zip\": \"" + zip +
                            "\",\r\n    \"lat\": \"" + currentLocation.getLatitude() +
                            "\",\r\n    \"lng\": \"" + currentLocation.getLongitude() + "\"\r\n}");
                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/reports/new")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    int response = client.newCall(request).execute().code();
                    status = response;
                    onSaveImageGallery(imageBitmap, timeInSecondsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            if (status == 200) {
                Toast.makeText(getActivity(), "New report added successfully added.", Toast.LENGTH_LONG).show();
                replaceFragment(new HomeFragment());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onGetLocation() {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                street = address.getThoroughfare();
                number = address.getSubThoroughfare();
                zip = address.getPostalCode();
                city = address.getLocality();
                Toast.makeText(getActivity(), address.getAddressLine(0), Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void getBottomNav() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ReportsFragment reportsFragment = (ReportsFragment) fragmentManager.findFragmentByTag("ReportsFragment");
        if (reportsFragment != null) {
            Toast.makeText(getActivity(), "Start exist", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Start does not exist", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageBitmap = bitmap;
                File outputFile = new File(getActivity().getExternalFilesDir(null), "image.jpg");
                OutputStream outputStream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                imageFile = outputFile;
                binding.imageViewPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getActivity(), String.valueOf(imageFile), Toast.LENGTH_SHORT).show();
    }

    public void onSaveImageGallery(Bitmap dataBitmap, String imageTitle) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageTitle);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Rebrux");
        values.put(MediaStore.Images.Media.IS_PENDING, true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                try (OutputStream out = getActivity().getContentResolver().openOutputStream(uri)) {
                    dataBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getActivity().getContentResolver().update(uri, values, null, null);
            }
        });
        thread.start();
    }

    public void onTestingImage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/image/63b9e92f184e315342251b3b")
                            .get()
                            .build();
                    String response = client.newCall(request).execute().body().string();
                    JSONObject reportRaw = new JSONObject(response).getJSONObject("data");
//                    Response response = client.newCall(request).execute();
//                    ResponseBody responseBody = response.body();
//                    Buffer buffer = responseBody.source().buffer();
                    Log.d("BUFFER", reportRaw.getString("data"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
