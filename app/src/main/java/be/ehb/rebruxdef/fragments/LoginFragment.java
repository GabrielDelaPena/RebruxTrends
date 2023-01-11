package be.ehb.rebruxdef.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentLoginBinding;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    EditText password, email;
    public static String userID;
    int status;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        password = binding.inputPassswordLogin;
        email = binding.inputEmailLogin;
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.btnLoginToRegister.setOnClickListener(
//                (View v) -> {
//                    NavHostFragment.findNavController(LoginFragment.this)
//                            .navigate(R.id.action_login_to_register);
//                }
//        );
        if (userID == null) {
            Log.d("CURRENT_USER", "USER IS NULL");
        }

        binding.btnLoginToForgotpassword.setOnClickListener(
                (View v) -> {
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_login_to_forgotpassword);
                }
        );

        binding.btnLoginToRegister.setOnClickListener(
                (View v) -> {
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_login_to_register);
                }
        );

        binding.btnLogin.setOnClickListener(
            (View v) -> {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("application/json");
                                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"password\": \"" + password.getText().toString() +
                                        "\",\r\n    \"email\": \"" + email.getText().toString() + "\"\r\n}");
                                Request request = new Request.Builder()
                                        .url("https://rebrux-backend-node-side.onrender.com/api/users/login")
                                        .method("POST", body)
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                String responseString = client.newCall(request).execute().body().string();
                                int response = client.newCall(request).execute().code();
                                userID = responseString;
                                status = response;
                                Log.d("LOGIN", String.valueOf(response));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                 thread.start();

                 try {
                     thread.join();
                     if (status == 400) {
                         Toast.makeText(this.getContext(), "Email or Password is invalid.", Toast.LENGTH_LONG).show();
                     } else if (status == 500) {
                         Toast.makeText(this.getContext(), "An error occurred in the server.", Toast.LENGTH_LONG).show();
                     } else if (status == 200) {
                         Toast.makeText(this.getContext(), "User successfully logged in.", Toast.LENGTH_LONG).show();
                         NavHostFragment.findNavController(LoginFragment.this)
                                 .navigate(R.id.action_loginFragment_to_startFragment);
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