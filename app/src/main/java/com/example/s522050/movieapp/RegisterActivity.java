package com.example.s522050.movieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;
    private Toolbar mToolbar;
    private DatabaseReference mDatabase;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Setting toolbar
        mToolbar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Progress Bar
        mRegProgress = new ProgressDialog(this);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(display_name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Please fill out all your information to register!", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "You should fill out all information!", Toast.LENGTH_SHORT).show();
                }
                else if (!TextUtils.isEmpty(display_name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){

                    if (!email.contains("@")){
                        Toast.makeText(RegisterActivity.this, "Email should contain @ character.", Toast.LENGTH_SHORT).show();
                    }
                    else if (password.length() < 4 ){
                        Toast.makeText(RegisterActivity.this, "Password should be as least 4 characters!", Toast.LENGTH_SHORT).show();
                    }else{
                        mRegProgress.setTitle("Registering User");
                        mRegProgress.setMessage("Please wait while we create your account!");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();

                        registerUser(display_name,email,password);
                    }
                }
            }
        });
    }

    private void registerUser(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    //Setting where to save each user
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",display_name);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                }else{
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot sign in. Please check form again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
