package be.ehb.rebruxdef.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentRegisterBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    EditText username, password, email;
    int status;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        username = binding.inputUsernameRegister;
        password = binding.inputPassswordRegister;
        email = binding.inputEmailRegister;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegisterToLogin.setOnClickListener(
                (View v) -> {
                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_register_to_login);
                }
        );

        binding.btnSingup.setOnClickListener(
                (View v) -> {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"username\": \"" + username.getText().toString() +
                                        "\",\r\n    \"password\": \"" + password.getText().toString() +
                                        "\",\r\n    \"email\": \"" + email.getText().toString() + "\"\r\n}");
                                Request request = new Request.Builder()
                                        .url("https://rebrux-backend-node-side.onrender.com/api/users/register")
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
                        if (status == 400) {
                            Toast.makeText(this.getContext(), "Email already exist.", Toast.LENGTH_LONG).show();
                        } else if (status == 500) {
                            Toast.makeText(this.getContext(), "An error occurred in the server.", Toast.LENGTH_LONG).show();
                        } else if (status == 200) {
                            Toast.makeText(this.getContext(), "User successfully registered.", Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(RegisterFragment.this)
                                    .navigate(R.id.action_register_to_login);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}