package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorPreChatBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfessorPreChat extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    private DrawerLayout drawer;
    String className;
    String classClass;
    String classNumber;
    String classBuilding;
    String classAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorPreChatBinding binding = ActivityProfessorPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
                switchToOtherActivity(ProfessorMainPage.class);
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ProfessorPreChat.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
            }
            return false;
        });



        if (user != null) {
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
                    Toast.makeText(ProfessorPreChat.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is null
            Toast.makeText(ProfessorPreChat.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }


        Intent getIntent = getIntent();
        if(getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");
            classBuilding = getIntent.getStringExtra("classBuilding");
            classAddress = getIntent.getStringExtra("classAddress");

            binding.className.setText(className);
        }

        binding.backMainPageProfessor.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfessorPreChat.this, ProfessorMainPage.class);
//            startActivity(intent);
            finish();
        });

        binding.buttonEnterChattingProfessor.setOnClickListener(v -> {
            String chatroomId = classNumber;

            DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);
            chatroomRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot chatroomDoc = task.getResult();
                    if (chatroomDoc != null && chatroomDoc.exists()) {
                        // 채팅방 존재 시, 해당 채팅방으로 이동
                        enterChatRoom(chatroomId, chatroomDoc.getString("title"));
                    } else {
                        // 채팅방 존재하지 않을 경우, 새로운 채팅방 생성
                        createNewChatRoom(chatroomId);
                    }
                } else {
                    // 에러 처리
                    Toast.makeText(ProfessorPreChat.this, "채팅방 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.subjectInformationProfessor.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorPreChat.this, ProfessorEditClass.class);

            intent.putExtra("className", className);
            intent.putExtra("classClass", classClass);
            intent.putExtra("classNumber", classNumber);
            intent.putExtra("classBuilding", classBuilding);
            intent.putExtra("classAddress", classAddress);

            startActivity(intent);
        });

        binding.attendanceCheckButtonProfessor.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorPreChat.this, ProfessorClassMember.class);

            intent.putExtra("className", className);
            intent.putExtra("classClass", classClass);
            intent.putExtra("classNumber", classNumber);
            intent.putExtra("classBuilding", classBuilding);
            intent.putExtra("classAddress", classAddress);

            startActivity(intent);
        });

    }

    private void enterChatRoom(String chatroomId, String chatroomTitle) {
        // 채팅방으로 이동하는 로직
        Intent intent = new Intent(ProfessorPreChat.this, SsuchatChatting.class);
        intent.putExtra("classNumber", chatroomId);
        intent.putExtra("chatRoomTitle", chatroomTitle);
        startActivity(intent);
    }

    private void createNewChatRoom(String chatroomId) {
        // 새로운 채팅방 생성 로직
        Map<String, Object> chatroomData = new HashMap<>();
        chatroomData.put("title", chatroomId); // 이 예시에서는 chatroomId를 제목으로 사용
        // 다른 필요한 데이터 설정

        db.collection("chatrooms").document(chatroomId)
                .set(chatroomData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfessorPreChat.this, "채팅방이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    enterChatRoom(chatroomId, chatroomId); // 생성된 채팅방으로 이동
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfessorPreChat.this, "채팅방 생성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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