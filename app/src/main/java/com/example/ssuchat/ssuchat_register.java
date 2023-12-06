package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ssuchat_register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CheckBox studCheckBox;
    private CheckBox profCheckBox;

    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatRegisterBinding binding = ActivitySsuchatRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        EditText editName = binding.editName;
        EditText editEmail = binding.editEmail;
        EditText editPassword = binding.editPassword;
        EditText editStudentId = binding.editStudentId;
        studCheckBox = binding.studLogin;
        profCheckBox = binding.profLogin;

        // 초기에 학생용 체크박스는 체크되어 있도록 설정
        studCheckBox.setChecked(true);

        // 학생용 체크박스가 체크되면 교수자용 체크박스는 체크 해제
        studCheckBox.setOnClickListener(v -> profCheckBox.setChecked(false));

        // 교수자용 체크박스가 체크되면 학생용 체크박스는 체크 해제
        profCheckBox.setOnClickListener(v -> studCheckBox.setChecked(false));

        binding.buttonSignUp.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String studentId = editStudentId.getText().toString();
            String role = studCheckBox.isChecked() ? "student" : "prof";

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(studentId)) {
                Toast.makeText(ssuchat_register.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!studCheckBox.isChecked() && !profCheckBox.isChecked()) {
                Toast.makeText(ssuchat_register.this, "학생용 또는 교수용을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            signUp(name, email, password, studentId, role);
        });

        binding.buttonSignupCancel.setOnClickListener(v -> {
            // 취소 버튼을 눌렀을 때 ssuchat_login 액티비티로 이동
            Intent intent = new Intent(ssuchat_register.this, ssuchat_login.class);
            startActivity(intent);
        });

        binding.goLoginButton.setOnClickListener(v -> {
            // 취소 버튼을 눌렀을 때 ssuchat_login 액티비티로 이동
            Intent intent = new Intent(ssuchat_register.this, ssuchat_login.class);
            startActivity(intent);
        });
    }


    private void signUp(String name, String email, String password, String studentId, String role) {
        // Check if the email is already in use
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SignInMethodQueryResult result = task.getResult();
                        if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                            // Email is already in use
                            Toast.makeText(getApplicationContext(), "The account already exists for that email.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email is not in use, proceed with user registration
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Sign up success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);

                                            // Save user data to Firestore
                                            saveUserDataToFirestore(user.getUid(), email, name, studentId, role);
                                        } else {
                                            // If sign up fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                            Toast.makeText(getApplicationContext(), "해당 이메일의 계정이 이미 존재합니다.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    });
                        }
                    } else {
                        // Handle errors in fetching sign-in methods
                        Toast.makeText(getApplicationContext(), "Error checking email existence.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String email, String name, String studentId, String role) {
        // Access Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user document with UID as document ID
        DocumentReference userRef = db.collection("users").document(userId);

        // Set user data in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", role);
        userData.put("name", name);
        userData.put("studentId", studentId);
        userData.put("createdAt", FieldValue.serverTimestamp());

        // Save user data to Firestore
        userRef.set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data saved to Firestore."))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving user data to Firestore", e));
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ssuchat_login.class);
            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());
            startActivity(intent);
        }
    }
}