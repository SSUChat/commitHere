package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.example.ssuchat.databinding.ActivitySsuchatLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ssuchat_login extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER_ID = "rememberId";
    private static final String PREF_AUTO_LOGIN = "autoLogin";

    private EditText loginEmail;
    private EditText loginPassword;
    private CheckBox rememberIdCheckbox;
    private CheckBox autoLoginCheckbox;

    private FirebaseAuth mAuth;

    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssuchat_login);

        ActivitySsuchatLoginBinding binding = ActivitySsuchatLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        loginEmail = binding.loginEmail;
        loginPassword = binding.loginPassword;
        rememberIdCheckbox = binding.idRemember;
        autoLoginCheckbox = binding.autologin;

        EditText loginEmail = binding.loginEmail;
        EditText loginPassword = binding.loginPassword;


        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                signIn(email, password);
            }
        });

        binding.goRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ssuchat_login.this, ssuchat_register.class);
                startActivity(intent);
            }
        });

        binding.goFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ssuchat_login.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        // "아이디 기억하기" 랑 "자동 로그인" 선택 됐을 때
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean rememberId = preferences.getBoolean(PREF_REMEMBER_ID, false);
        boolean autoLogin = preferences.getBoolean(PREF_AUTO_LOGIN, false);

        rememberIdCheckbox.setChecked(rememberId);
        autoLoginCheckbox.setChecked(autoLogin);

        if (rememberId) {
            String savedEmail = preferences.getString(PREF_EMAIL, "");
            loginEmail.setText(savedEmail);
        }

        if (autoLogin && rememberId) {
            String savedPassword = preferences.getString(PREF_PASSWORD, "");
            loginPassword.setText(savedPassword);
            signIn(loginEmail.getText().toString(), savedPassword);
        }

        // 체크박스 리스너
        rememberIdCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // SharedPreferences 에 "ID 기억하기" 옵션 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PREF_REMEMBER_ID, isChecked);
                editor.apply();
            }
        });

        autoLoginCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // SharedPreferences 에 "자동 로그인" 옵션 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PREF_AUTO_LOGIN, isChecked);
                editor.apply();
            }
        });

    }

//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            updateUI(currentUser);
//        }
//    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Log.d(TAG, "signInWithEmail:failure");
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

        // Save email and password to SharedPreferences if "Remember ID" is checked
        if (rememberIdCheckbox.isChecked()) {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_EMAIL, email);
            editor.putString(PREF_PASSWORD, password);
            editor.apply();
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ssuchat_main_page.class);
            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());

            Toast.makeText(this, "로그인 성공!!", Toast.LENGTH_SHORT).show();

            startActivity(intent);
        } else {
            Toast.makeText(this, "아이디 또는 비밀번호가 틀렸거나 존재하지 않습니다!!", Toast.LENGTH_SHORT).show();
        }
    }
}