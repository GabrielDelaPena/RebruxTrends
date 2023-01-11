package be.ehb.rebruxdef.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import be.ehb.rebruxdef.R;
import be.ehb.rebruxdef.databinding.FragmentForgotpasswordBinding;

public class ForgotpasswordFragment extends Fragment {

    private FragmentForgotpasswordBinding binding;
    EditText email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotpasswordBinding.inflate(inflater, container, false);
        email = binding.inputEmailForgotpassword;
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();

        binding.btnSend.setOnClickListener(
                (View v) -> {
                    bundle.putString("email", email.getText().toString());
                    NavHostFragment.findNavController(ForgotpasswordFragment.this)
                            .navigate(R.id.action_forgotpasswordFragment_to_changePasswordFragment, bundle);
                }
        );

    }

}