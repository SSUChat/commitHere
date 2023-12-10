package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorEditClassBinding;
import com.google.android.material.navigation.NavigationView;
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

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
                switchToOtherActivity(ssuchat_main_page.class);
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ProfessorEditClass.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
            }
            return false;
        });

        if (user != null) {
            //Firestore에 저장된 유저 정보 가져오기
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
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
        } else {
            // Handle the case where the user is null
            Toast.makeText(ProfessorEditClass.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

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

        binding.backPreChatProfessor.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorEditClass.this, ProfessorPreChat.class);
            startActivity(intent);
        });

        String doc = className + classClass;
        DocumentReference userRef = db.collection("users").document(userId);

        binding.editClassButton.setOnClickListener(v -> userRef.get().addOnCompleteListener(task -> {
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
        }));

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
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data saved to Firestore."))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving user data to Firestore", e));
    }

    private void switchToOtherActivity(Class<?> destinationActivity) {
        // 현재 액티비티의 컨텍스트를 가져옵니다.
        Context context = this;

        // Intent를 생성하고, 전환할 액티비티로 설정합니다.
        Intent intent = new Intent(context, destinationActivity);

        // 다른 액티비티로 전환합니다.
        startActivity(intent);
    }

    private void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그아웃");
        builder.setMessage("정말 로그아웃 하시겠습니까?");
        builder.setPositiveButton("로그아웃", (dialog, which) -> {

            if (drawer.isDrawerOpen(GravityCompat.END)) // 네비게이션 드로어 열려있으면
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.

            // 로그아웃 기능을 수행합니다.
            FirebaseAuth.getInstance().signOut();

            // 로그인 화면으로 이동합니다.
            switchToOtherActivity(ssuchat_login.class);
            finish();
        });
        builder.setNegativeButton("취소", (dialog, which) -> {
            // 취소 버튼을 눌렀을 때의 동작
            dialog.dismiss(); // 다이얼로그 닫기
        });
        builder.show();
    }
}