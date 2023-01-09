package be.ehb.rebruxdef.fragments;

import static be.ehb.rebruxdef.fragments.LoginFragment.userID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentEditProfileBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    EditText username, email, phone;
    int status;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        username = binding.profileEditUsername;
        email = binding.profileEditEmail;
        phone = binding.profileEditPhone;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        username.setText(bundle.getString("username"));
        email.setText(bundle.getString("email"));
        phone.setText(bundle.getString("phone"));

        binding.btnEditProfileSave.setOnClickListener(
                (View v) -> {
                    onEditCall(username.getText().toString(), email.getText().toString(), phone.getText().toString(), userID);
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Own Code

    public int onEditCall(String username, String email, String phone, String userID) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\r\n    \"username\": \"" + username +
                            "\",\r\n    \"email\": \"" + email +
                            "\",\r\n    \"phone\": \"" + phone + "\"\r\n}");
                    Request request = new Request.Builder()
                            .url("https://rebrux-backend-node-side.onrender.com/api/users/edit/" + userID)
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    int response = client.newCall(request).execute().code();
                    status = response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            if (status == 200) {
                replaceFragment(new HomeFragment());
                Toast.makeText(this.getContext(), "User successfully updated.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getContext(), "Some of the inputs are invalid.", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
