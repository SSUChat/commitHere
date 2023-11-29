package com.example.ssuchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuchat.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityForgetPasswordBinding binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.forgetPasswordBackLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, ssuchat_login.class);
                startActivity(intent);
            }
        });

        binding.goLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, ssuchat_login.class);
                startActivity(intent);
            }
        });

        binding.buttonFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = binding.editName;
                EditText editId = binding.editId;
                EditText editStudentId = binding.editStudentId;

                String name = editName.getText().toString();
                String id = editId.getText().toString();
                String studentId = editStudentId.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id) || TextUtils.isEmpty(studentId)) {
                    Toast.makeText(ForgetPassword.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

//                // 사용자 재인증
//                reauthenticateUser(id, "password1234"); // 비밀번호는 가상의 값으로 수정해야 합니다.

                // Firebase에서 해당 계정 찾아서 이메일 전송
                sendPasswordResetEmail(id);
            }
        });
    }

//    private void reauthenticateUser(String id, String password) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String userEmail = id; // 이메일 형식을 수정해야 합니다.
//
//        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, password);
//
//        mAuth.getCurrentUser().reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            // 사용자 재인증 성공
//                            Toast.makeText(ForgetPassword.this, "사용자 재인증 성공", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 사용자 재인증 실패
//                            Toast.makeText(ForgetPassword.this, "사용자 재인증 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void sendPasswordResetEmail(String id) {
        String email = id;
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPassword.this, "비밀번호 재설정 이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("PasswordReset", "Error sending reset email: " + task.getException());
                            Toast.makeText(ForgetPassword.this, "이메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
