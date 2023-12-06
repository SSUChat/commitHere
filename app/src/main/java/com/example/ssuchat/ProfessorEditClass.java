package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorEditClassBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfessorEditClass extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }
    String className;
    String classClass;
    String classNumber;
    String classBuilding;
    String classAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorEditClassBinding binding = ActivityProfessorEditClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawer = findViewById(R.id.drawerLayout);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        binding.menuBtn.setOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.END);
        });

        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                //기능 추가하기. 일단 예시로 토스트
                Toast.makeText(ProfessorEditClass.this, "NavigationDrawer...home..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(ProfessorEditClass.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                drawer.closeDrawer(GravityCompat.END);

                MenuItem logoutItem = binding.navigationView.getMenu().findItem(R.id.nav_logout);
                logoutItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //로그아웃 기능 넣을 부분
                        FirebaseAuth.getInstance().signOut();

                        // Navigate to the login screen
                        Intent intent = new Intent(ProfessorEditClass.this, ssuchat_login.class);
                        startActivity(intent);
                        finish(); // Optional: close the current activity to prevent going back to it with the back button
                    }
                });
            }
            return false;
        });

        //Firestore에 저장된 유저 정보 가져오기
        DocumentReference user_ref = db.collection("users").document(user.getUid());
        user_ref.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if (task.getResult() != null && task.getResult().exists()) {
                    String userName = task.getResult().getString("name");
                    String userEmail = task.getResult().getString("email");
                    String userStudentId = task.getResult().getString("studentId");

                    // Set user information to TextViews
                    TextView userNameTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_name_tv);
                    TextView userEmailTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_email_tv);
                    TextView userStudentIdTextView = binding.navigationView.getHeaderView(0).findViewById(R.id.user_studentId_tv);

                    userNameTextView.setText(userName);
                    userEmailTextView.setText(userEmail);
                    userStudentIdTextView.setText(userStudentId);
                }
            } else {
                // Handle the error
                Toast.makeText(ProfessorEditClass.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
            }
        });




        Intent getIntent = getIntent();
        className = getIntent.getStringExtra("className");
        classClass = getIntent.getStringExtra("classClass");
        classNumber = getIntent.getStringExtra("classNumber");
        classBuilding = getIntent.getStringExtra("classBuilding");
        classAddress = getIntent.getStringExtra("classAddress");

        binding.textviewClassName.setText(className);
        binding.textviewClassClass.setText(classClass);
        binding.textviewClassNumber.setText(classNumber);
        binding.textviewClassBuilding.setText(classBuilding);
        binding.textviewClassAddress.setText(classAddress);

        binding.backPreChatProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorEditClass.this, ProfessorPreChat.class);
                startActivity(intent);
            }
        });

        String doc = className + classClass;
        DocumentReference userRef = db.collection("users").document(userId);;

        binding.editClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 여기서 에러
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "document : " + document);
                            if (document.exists()) {
                                Log.d(TAG, "doc = " + doc);
                                // 사용자 문서가 존재할 경우
                                String name = document.getString("name");

                                String editClassName = binding.className.getText().toString();
                                String editClassClass = binding.classClass.getText().toString();
                                String editClassNumber = binding.classNumber.getText().toString();
                                String editClassBuilding = binding.classBuilding.getText().toString();
                                String editClassAddress = binding.classAddress.getText().toString();

                                if(editClassName.isEmpty()) {
                                    editClassName = className;
                                }
                                if(editClassClass.isEmpty()) {
                                    editClassClass = classClass;
                                }
                                if(editClassNumber.isEmpty()) {
                                    editClassNumber = classNumber;
                                }
                                if(editClassBuilding.isEmpty()) {
                                    editClassBuilding = classBuilding;
                                }
                                if(editClassAddress.isEmpty()) {
                                    editClassAddress = classAddress;
                                }

                                saveClassDataToFirestore(name, editClassName, editClassClass, editClassNumber, editClassBuilding, editClassAddress);

                                Intent intent = new Intent(ProfessorEditClass.this, ProfessorPreChat.class);

                                intent.putExtra("className", className);
                                intent.putExtra("classClass", classClass);
                                intent.putExtra("classNumber", classNumber);
                                intent.putExtra("classBuilding", classBuilding);
                                intent.putExtra("classAddress", classAddress);

                                startActivity(intent);

                            } else {
                                // 사용자 문서가 존재하지 않을 경우
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            // 작업이 실패한 경우
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

    }

    private void saveClassDataToFirestore(String name, String className, String classClass, String classNumber, String classBuilding, String classAddress) {
        db = FirebaseFirestore.getInstance();
        String doc = className + classClass;
        DocumentReference userRef = db.collection(name).document(doc);
        Map<String, Object> userData = new HashMap<>();

        userData.put("className", className);
        userData.put("classClass", classClass);
        userData.put("classNumber", classNumber);
        userData.put("classBuilding", classBuilding);
        userData.put("classAddress", classAddress);

        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User data saved to Firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error saving user data to Firestore", e);
                    }
                });
    }
}