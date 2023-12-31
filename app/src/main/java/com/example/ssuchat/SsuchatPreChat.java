package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatPreChatBinding;
import com.example.ssuchat.databinding.SsuchatLiveMemberItemBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsuchatPreChat extends AppCompatActivity {
    private DrawerLayout drawer;
    private String className;
    private String classClass;
    private String classNumber;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatPreChatBinding binding = ActivitySsuchatPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);

        Intent getIntent = getIntent();
        if (getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");

            binding.className.setText(className);
        }

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
                switchToOtherActivity(ssuchat_main_page.class);
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(SsuchatPreChat.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SsuchatPreChat.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is null
            Toast.makeText(SsuchatPreChat.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

        String documentId = className + classClass;

        DocumentReference classRef = db.collection("class").document(documentId);

        classRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> enrolledStudents = (List<String>) document.get("enrolledStudents");

                    // Set enrolled students in RecyclerView
                    binding.liveMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    binding.liveMemberRecyclerView.setAdapter(new SsuchatPreChat.MyAdapter(enrolledStudents));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        binding.backMainPage.setOnClickListener(v -> {
            finish();
        });

        binding.buttonEnterChatting.setOnClickListener(v -> {
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
                        Toast.makeText(SsuchatPreChat.this, "채팅방 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }


    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SsuchatLiveMemberItemBinding binding;

        private MyViewHolder(SsuchatLiveMemberItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(String text) {
            binding.memberName.setText(text);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<SsuchatPreChat.MyViewHolder> {

        private final List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public SsuchatPreChat.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SsuchatLiveMemberItemBinding binding = SsuchatLiveMemberItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new SsuchatPreChat.MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SsuchatPreChat.MyViewHolder holder, int position) {
            String text = list.get(position);
            holder.bind(text);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }

    private void enterChatRoom(String chatroomId, String chatroomTitle) {
        // 채팅방으로 이동하는 로직
        Intent intent = new Intent(SsuchatPreChat.this, SsuchatChatting.class);
        intent.putExtra("classNumber", chatroomId);
        intent.putExtra("chatRoomTitle", chatroomTitle);
        intent.putExtra("name", "user_name"); // replace with the actual user name
        intent.putExtra("UID", "user_uid"); // replace with the actual user UID
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
                    Toast.makeText(SsuchatPreChat.this, "채팅방이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    enterChatRoom(chatroomId, chatroomId); // 생성된 채팅방으로 이동
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SsuchatPreChat.this, "채팅방 생성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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