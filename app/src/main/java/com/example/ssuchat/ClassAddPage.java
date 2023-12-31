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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityClassAddPageBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassAddPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClassAddPageBinding binding = ActivityClassAddPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);
        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
                switchToOtherActivity(ProfessorMainPage.class);
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ClassAddPage.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
            }
            return false;
        });


        if (user != null) {
            //Firestore에 저장된 유저 정보 가져오기
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
                    Toast.makeText(ClassAddPage.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is null
            Toast.makeText(ClassAddPage.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

//        String className = binding.className.getText().toString();
//        String classClass = binding.classClass.getText().toString();
//        String classNumber = binding.classNumber.getText().toString();
//        String classBuilding = binding.classBuilding.getText().toString();
//        String classAddress = binding.classAddress.getText().toString();
//        String selectWeek1 = String.valueOf(binding.selectWeek1);
//        String selectWeek2 = String.valueOf(binding.selectWeek2);
//        String selectWeek3 = String.valueOf(binding.selectWeek3);
//        String selectStartHour1 = String.valueOf(binding.selectStartHour1);
//        String selectStartHour2 = String.valueOf(binding.selectStartHour2);
//        String selectStartHour3 = String.valueOf(binding.selectStartHour3);
//        String selectStartMinute1 = String.valueOf(binding.selectStartMinute1);
//        String selectStartMinute2 = String.valueOf(binding.selectStartMinute2);
//        String selectStartMinute3 = String.valueOf(binding.selectStartMinute3);
//        String selectEndHour1 = String.valueOf(binding.selectEndHour1);
//        String selectEndHour2 = String.valueOf(binding.selectEndHour2);
//        String selectEndHour3 = String.valueOf(binding.selectEndHour3);
//        String selectEndMinute1 = String.valueOf(binding.selectEndMinute1);
//        String selectEndMinute2 = String.valueOf(binding.selectEndMinute2);
//        String selectEndMinute3 = String.valueOf(binding.selectEndMinute3);

//        int SelectStartHour1 = Integer.parseInt(String.valueOf(selectStartHour1));
//        int SelectStartHour2 = Integer.parseInt(String.valueOf(selectStartHour2));
//        int SelectStartHour3 = Integer.parseInt(String.valueOf(selectStartHour3));
//        int SelectStartMinute1 = Integer.parseInt(String.valueOf(selectStartMinute1));
//        int SelectStartMinute2 = Integer.parseInt(String.valueOf(selectStartMinute2));
//        int SelectStartMinute3 = Integer.parseInt(String.valueOf(selectStartMinute3));
//        int SelectEndHour1 = Integer.parseInt(String.valueOf(selectEndHour1));
//        int SelectEndHour2 = Integer.parseInt(String.valueOf(selectEndHour2));
//        int SelectEndHour3 = Integer.parseInt(String.valueOf(selectEndHour3));
//        int SelectEndMinute1 = Integer.parseInt(String.valueOf(selectEndMinute1));
//        int SelectEndMinute2 = Integer.parseInt(String.valueOf(selectEndMinute2));
//        int SelectEndMinute3 = Integer.parseInt(String.valueOf(selectEndMinute3));

        binding.addClassButton.setOnClickListener(v -> userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d(TAG, "add_page_document : " + document);
                if (document.exists()) {
                    // 사용자 문서가 존재할 경우
                    String name = document.getString("name");

                    String className = binding.className.getText().toString();
                    String classClass = binding.classClass.getText().toString();
                    String classNumber = binding.classNumber.getText().toString();
                    String classBuilding = binding.classBuilding.getText().toString();
                    String classAddress = binding.classAddress.getText().toString();
                    String selectWeek1 = String.valueOf(binding.selectWeek1);
                    String selectWeek2 = String.valueOf(binding.selectWeek2);
                    String selectWeek3 = String.valueOf(binding.selectWeek3);
                    String selectStartHour1 = String.valueOf(binding.selectStartHour1);
                    String selectStartHour2 = String.valueOf(binding.selectStartHour2);
                    String selectStartHour3 = String.valueOf(binding.selectStartHour3);
                    String selectStartMinute1 = String.valueOf(binding.selectStartMinute1);
                    String selectStartMinute2 = String.valueOf(binding.selectStartMinute2);
                    String selectStartMinute3 = String.valueOf(binding.selectStartMinute3);
                    String selectEndHour1 = String.valueOf(binding.selectEndHour1);
                    String selectEndHour2 = String.valueOf(binding.selectEndHour2);
                    String selectEndHour3 = String.valueOf(binding.selectEndHour3);
                    String selectEndMinute1 = String.valueOf(binding.selectEndMinute1);
                    String selectEndMinute2 = String.valueOf(binding.selectEndMinute2);
                    String selectEndMinute3 = String.valueOf(binding.selectEndMinute3);

                    saveClassDataToFirestore(userId, name, className, classClass, classNumber, classBuilding, classAddress,
                            selectWeek1, selectStartHour1, selectStartMinute1, selectEndHour1, selectEndMinute1,
                            selectWeek2, selectStartHour2, selectStartMinute2, selectEndHour2, selectEndMinute2,
                            selectWeek3, selectStartHour3, selectStartMinute3, selectEndHour3, selectEndMinute3);

                    Toast.makeText(ClassAddPage.this, "강의가 추가되었습니다!!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ClassAddPage.this, ProfessorMainPage.class);
                    startActivity(intent);

                    // 이제 'name' 변수에 문서에서 가져온 이름이 들어 있음
                    // saveClassDataToFirestore 메소드 호출할 때 'name'을 전달하면 됨
                } else {
                    // 사용자 문서가 존재하지 않을 경우
                    Log.d(TAG, "No such document");
                }
            } else {
                // 작업이 실패한 경우
                Log.d(TAG, "get failed with ", task.getException());
            }
        }));

        binding.goBackMainPageProfessor.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.addClassTimeButton.setOnClickListener(v -> {
            if (binding.selectClassTime2.getVisibility() == View.GONE) {
                binding.selectClassTime2.setVisibility(View.VISIBLE);
            } else {
                binding.selectClassTime3.setVisibility(View.VISIBLE);
            }
        });

    }

    private void saveClassDataToFirestore(String userId, String name, String className, String classClass, String classNumber,
                                          String classBuilding, String classAddress,
                                          String selectWeek1, String selectStartHour1, String selectStartMinute1, String selectEndHour1, String selectEndMinute1,
                                          String selectWeek2, String selectStartHour2, String selectStartMinute2, String selectEndHour2, String selectEndMinute2,
                                          String selectWeek3, String selectStartHour3, String selectStartMinute3, String selectEndHour3, String selectEndMinute3) {

        db = FirebaseFirestore.getInstance();
        String doc = className + classClass;
        DocumentReference userRef = db.collection(name).document(doc);
        DocumentReference classRef = db.collection("class").document(doc);
        Map<String, Object> userData = new HashMap<>();

//        int SelectStartHour1 = Integer.parseInt(selectStartHour1);
//        int SelectStartMinute1 = Integer.parseInt(selectStartMinute1);
//        int SelectEndHour1 = Integer.parseInt(selectEndHour1);
//        int SelectEndMinute1 = Integer.parseInt(selectEndMinute1);
//        int SelectStartHour2 = Integer.parseInt(selectStartHour2);
//        int SelectStartMinute2 = Integer.parseInt(selectStartMinute2);
//        int SelectEndHour2 = Integer.parseInt(selectEndHour2);
//        int SelectEndMinute2 = Integer.parseInt(selectEndMinute2);
//        int SelectStartHour3 = Integer.parseInt(selectStartHour3);
//        int SelectStartMinute3 = Integer.parseInt(selectStartMinute3);
//        int SelectEndHour3 = Integer.parseInt(selectEndHour3);
//        int SelectEndMinute3 = Integer.parseInt(selectEndMinute3);

        userData.put("name", name);
        userData.put("className", className);
        userData.put("classClass", classClass);
        userData.put("classNumber", classNumber);
        userData.put("classBuilding", classBuilding);
        userData.put("classAddress", classAddress);
        userData.put("selectWeek1", selectWeek1);
        userData.put("selectStartHour1", selectStartHour1);
        userData.put("selectStartMinute1", selectStartMinute1);
        userData.put("selectEndHour1", selectEndHour1);
        userData.put("selectEndMinute1", selectEndMinute1);
        userData.put("selectWeek2", selectWeek2);
        userData.put("selectStartHour2", selectStartHour2);
        userData.put("selectStartMinute2", selectStartMinute2);
        userData.put("selectEndHour2", selectEndHour2);
        userData.put("selectEndMinute2", selectEndMinute2);
        userData.put("selectWeek3", selectWeek3);
        userData.put("selectStartHour3", selectStartHour3);
        userData.put("selectStartMinute3", selectStartMinute3);
        userData.put("selectEndHour3", selectEndHour3);
        userData.put("selectEndMinute3", selectEndMinute3);
        userData.put("enrolledStudents", new ArrayList<String>());
        userData.put("createdAt", FieldValue.serverTimestamp());

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data saved to Firestore."))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving user data to Firestore", e));

        classRef.set(userData)
                .addOnCompleteListener(task -> Log.d(TAG, "User data saved to Class Firestore."))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving user data to Class Firestore", e));
    }

    private void switchToOtherActivity(Class<?> destinationActivity) {
        // 현재 액티비티의 컨텍스트를 가져옵니다.
        Context context = this;

        // Intent를 생성하고, 전환할 액티비티로 설정합니다.
        Intent intent = new Intent(context, destinationActivity);

        // 다른 액티비티로 전환합니다.
        startActivity(intent);
    }

    private void logoutDialog() {
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