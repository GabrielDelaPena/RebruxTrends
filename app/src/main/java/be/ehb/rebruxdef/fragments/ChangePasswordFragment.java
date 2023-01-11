package be.ehb.rebruxdef.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentChangePasswordBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    EditText password, confirmPassword;
    int status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        password = binding.etNewPassword;
        confirmPassword = binding.etConfirmPassword;
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        binding.btnUpdateSave.setOnClickListener(
                (View v) -> {
                    if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        Toast.makeText(getActivity(), "Both password are not the same, pls try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        onUpdatePasswordCallAPI(bundle.getString("email"), password.getText().toString());
                    }
                }
        );
    }

    public void onUpdatePasswordCallAPI(String email, String password) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"email\": \"" + email +
                        "\",\r\n    \"password\": \"" + password + "\"\r\n}");
                Request request = new Request.Builder()
                        .url("https://rebrux-backend-node-side.onrender.com/api/users/password/update")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
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
                NavHostFragment.findNavController(ChangePasswordFragment.this)
                        .navigate(R.id.action_changePasswordFragment_to_loginFragment);
                Toast.makeText(getActivity(), email + " updated password.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Something went wrong, pls try again later.", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
