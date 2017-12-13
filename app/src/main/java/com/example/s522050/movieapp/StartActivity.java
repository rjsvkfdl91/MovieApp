package com.example.s522050.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mRegBtn;
    private Button mLoginBtn;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;

    private SignInButton googleSignInBtn;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Google Sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Set Authentication
        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    if (!email.contains("@")){
                        Toast.makeText(StartActivity.this, "Please check your email form!", Toast.LENGTH_SHORT).show();
                    }
                    else if (password.length() < 4 ){
                        Toast.makeText(StartActivity.this, "Password should be as least 4 characters!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mLoginProgress.setTitle("Logging in");
                        mLoginProgress.setMessage("Please wait while we check your account...");
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        loginUser(email, password);
                        mLoginProgress.show();

                    }
                }
                else{
                    if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                        Toast.makeText(StartActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(password) && !TextUtils.isEmpty(email))
                        Toast.makeText(StartActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
                        Toast.makeText(StartActivity.this, "Email and Password cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegBtn = findViewById(R.id.reg_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mLoginProgress.dismiss();
                    Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                    // To not go back to start page. If you click back button, app will be closed
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // ~~~~~~>
                    startActivity(mainIntent);
                    finish();
                } else {
                    mLoginProgress.hide();
                    Toast.makeText(StartActivity.this, "Cannot Sign In. Please check your account and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
