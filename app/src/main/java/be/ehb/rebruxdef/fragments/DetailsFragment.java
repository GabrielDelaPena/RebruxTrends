package be.ehb.rebruxdef.fragments;

import static android.app.Activity.RESULT_OK;
import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
    ImageView imageView;
    TextView creator, street, zip, city, status, createdAt, description, time;
    Button btnCleaned;
    ConstraintLayout cleaned;
    Reports report = new Reports();
    int responseStatus;
    int StatusDelete;
    private static final int SELECT_IMAGE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        imageView = binding.imageView2;
        creator = binding.tvDetailsCreator;
        street = binding.tvDetailsStreet;
        zip = binding.tvDetailsZip;
        city = binding.tvDetailsCity;
        status = binding.tvDetailsStatus;
        createdAt = binding.tvDetailsCreatedAt;
        description = binding.tvDetailsDescription;
        btnCleaned = binding.btnDetailsCleaned;
        cleaned = binding.constraintLayout3;
        time = binding.tvDetailsTime;
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

        onGetImage(bundle);

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, SELECT_IMAGE);

    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
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
                    time.setText(reportRaw.getString("timeCreated"));

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
                cleaned.setBackgroundColor(Color.RED);
            } else {
                status.setText("Cleaned.");
                cleaned.setBackgroundColor(R.color.green);
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

    public void onGetImage(Bundle bundle) {
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[] { bundle.getString("imageTitle") + ".jpg" };

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            // If the cursor is not empty and the image was found, we can get its ID, display name, and data from the cursor
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            long id = cursor.getLong(idColumn);
            String displayName = cursor.getString(displayNameColumn);
            String data = cursor.getString(dataColumn);

            Bitmap bitmap = BitmapFactory.decodeFile(data);
            imageView.setImageBitmap(bitmap);

            // You can use the ID, display name, and data to display the image or perform other actions with it
            // ...
        } else {
            int imageResource = R.drawable.trash;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResource);
            imageView.setImageBitmap(bitmap);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
//            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
