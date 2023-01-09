package be.ehb.rebruxdef.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import be.ehb.rebruxdef.R;

public class ForgotpasswordFragment extends Fragment implements View.OnClickListener {

    public ForgotpasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotpasswordFragment newInstance() {
        ForgotpasswordFragment fragment = new ForgotpasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_forgotpassword, container, false);

        Button buttonRegistertoLogin = mView.findViewById(R.id.btn_register_to_login);
        buttonRegistertoLogin.setOnClickListener(this);
        Button buttonLoginRegister = mView.findViewById(R.id.btn_login_to_register);
        buttonLoginRegister.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_to_login:
                Navigation.findNavController(v).navigate(R.id.action_forgotpassword_to_login);
                break;
            case R.id.btn_login_to_register:
                Navigation.findNavController(v).navigate(R.id.action_forgotpassword_to_register);
                break;
        }
    }
}