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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private CheckBox studCheckBox;
    private CheckBox profCheckBox;

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
        studCheckBox = binding.studLogin;
        profCheckBox = binding.profLogin;

        EditText loginEmail = binding.loginEmail;
        EditText loginPassword = binding.loginPassword;

        // 초기에 학생용 체크박스는 체크되어 있도록 설정
        studCheckBox.setChecked(true);

        // 학생용 체크박스가 체크되면 교수자용 체크박스는 체크 해제
        studCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profCheckBox.setChecked(false);
            }
        });

        // 교수자용 체크박스가 체크되면 학생용 체크박스는 체크 해제
        profCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studCheckBox.setChecked(false);
            }
        });

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
                            checkUserInFirestore(user);
                        } else {
                            // If sign in fails, display a message to the user.
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

    private void checkUserInFirestore(FirebaseUser user) {
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User data found in Firestore
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            // Check user role
                            String role = document.getString("role");
                            if (role != null) {
                                // Check if the user role matches the desired role for login
                                if ((studCheckBox.isChecked() && role.equals("student")) ||
                                        (profCheckBox.isChecked() && role.equals("prof"))) {
                                    // User role is allowed, update UI
                                    updateUI(user);
                                } else {
                                    // User role is not allowed, display an error message
                                    Toast.makeText(getApplicationContext(), "Invalid user role for login.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            } else {
                                // User role not found, display an error message
                                Toast.makeText(getApplicationContext(), "User role not found.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        } else {
                            // User data not found in Firestore
                            Log.d(TAG, "No such document");
                            Toast.makeText(getApplicationContext(), "User data not found.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getApplicationContext(), "Error fetching user data.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ssuchat_main_page.class);
            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());

            Toast.makeText(this, "로그인 성공!!", Toast.LENGTH_SHORT).show();

            startActivity(intent);
        } else {
            Toast.makeText(this, "로그인 정보가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}