package org.snowcorp.app.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.snowcorp.app.R;
import org.snowcorp.app.presenter.SignupPresenter;
import org.snowcorp.app.utils.GlobalUtils;
import org.snowcorp.app.view.SignupListener;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignupActivity extends AppCompatActivity implements SignupListener {

    SignupPresenter presenter;

    @InjectView(R.id.input_name)
    EditText _nameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        presenter = new SignupPresenter(this, this);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");


        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        onLoading();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        presenter.dataSave(name, email, password);

                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        progressDialog.dismiss();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        startActivity(new Intent(this, Dashboard.class));
        finish();

    }

    public void onSignupFailed() {
        progressDialog.dismiss();

        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    @Override
    public void onLoading() {
        progressDialog.show();
    }

    @Override
    public void onSuccess(String message, String name, String email) {
        progressDialog.dismiss();
        GlobalUtils.savePref("name", name, this);
        GlobalUtils.savePref("email", email, this);
        GlobalUtils.saveBoolean("status", true, this);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        onSignupSuccess();
    }

    @Override
    public void onFailed(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}