package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.example.ssuchat.databinding.ActivitySsuchatLoginBinding;
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
    private static final String PREF_STUDENT_CHECKED = "isStudentChecked";
    private static final String PREF_PROF_CHECKED = "isProfChecked";

    private EditText loginEmail;
    private EditText loginPassword;
    private CheckBox rememberIdCheckbox;
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
        ActivitySsuchatLoginBinding binding = ActivitySsuchatLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        String callingActivity = getIntent().getStringExtra("callingActivity");

        // Binding
        loginEmail = binding.loginEmail;
        loginPassword = binding.loginPassword;
        rememberIdCheckbox = binding.idRemember;
        CheckBox autoLoginCheckbox = binding.autologin;
        studCheckBox = binding.studLogin;
        profCheckBox = binding.profLogin;

        binding.buttonSignIn.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            // Check if email, password, and role (student or prof) are provided
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(ssuchat_login.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!studCheckBox.isChecked() && !profCheckBox.isChecked()) {
                Toast.makeText(ssuchat_login.this, "학생용 또는 교수용을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            signIn(email, password);
        });

        binding.goRegisterButton.setOnClickListener(v -> startActivity(new Intent(this, ssuchat_register.class)));
        binding.goFindPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgetPassword.class)));

        // "아이디 기억하기" 랑 "자동 로그인" 선택 됐을 때
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        rememberIdCheckbox.setChecked(preferences.getBoolean(PREF_REMEMBER_ID, false));
        autoLoginCheckbox.setChecked(preferences.getBoolean(PREF_AUTO_LOGIN, false));
        studCheckBox.setChecked(preferences.getBoolean(PREF_STUDENT_CHECKED, false));
        profCheckBox.setChecked(preferences.getBoolean(PREF_PROF_CHECKED, false));

        if (rememberIdCheckbox.isChecked()) {
            loginEmail.setText(preferences.getString(PREF_EMAIL, ""));
        }

        if (autoLoginCheckbox.isChecked() && rememberIdCheckbox.isChecked()) {
            loginPassword.setText(preferences.getString(PREF_PASSWORD, ""));
            if (callingActivity != null && callingActivity.equals("MainActivity")) {
                signIn(loginEmail.getText().toString(), loginPassword.getText().toString());
            }
        }

        rememberIdCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference(PREF_REMEMBER_ID, isChecked));
        autoLoginCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference(PREF_AUTO_LOGIN, isChecked);
            if (isChecked) {
                savePreference(PREF_STUDENT_CHECKED, studCheckBox.isChecked());
                savePreference(PREF_PROF_CHECKED, profCheckBox.isChecked());
                if (!rememberIdCheckbox.isChecked()) {
                    rememberIdCheckbox.setChecked(true);
                    savePreference(PREF_REMEMBER_ID, true);
                }
            }
        });

        studCheckBox.setOnClickListener(v -> {
            profCheckBox.setChecked(false);
            savePreference(PREF_STUDENT_CHECKED, true);
            savePreference(PREF_PROF_CHECKED, false);
        });

        profCheckBox.setOnClickListener(v -> {
            studCheckBox.setChecked(false);
            savePreference(PREF_STUDENT_CHECKED, false);
            savePreference(PREF_PROF_CHECKED, true);
        });
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, value).apply();
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInFirestore(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "signInWithEmail:failure");
                        Toast.makeText(this, "로그인 정보가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                        updateUI(null);
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

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String role = document.getString("role");
                        if ((studCheckBox.isChecked() && "student".equals(role)) ||
                                (profCheckBox.isChecked() && "prof".equals(role))) {
                            updateUI(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid user role for login.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            });
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ssuchat_main_page.class);
            Intent intent2 = new Intent(this, ProfessorMainPage.class);
            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());
            intent2.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());
            Toast.makeText(this, "로그인 성공!!", Toast.LENGTH_SHORT).show();
            startActivity(studCheckBox.isChecked() ? intent : intent2);
        }
    }
}