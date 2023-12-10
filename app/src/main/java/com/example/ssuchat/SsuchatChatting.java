package com.example.ssuchat;

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
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatChattingBinding;
import com.example.ssuchat.databinding.SsuchatChattingItemBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SsuchatChatting extends AppCompatActivity {
    private DrawerLayout drawer;
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatChattingBinding binding = ActivitySsuchatChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        if (user != null) {
            //Firestore에 저장된 유저 정보 가져오기
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (task.getResult() != null && task.getResult().exists()) {
                        String userName = task.getResult().getString("name");
                        String userEmail = task.getResult().getString("email");
                        String userStudentId = task.getResult().getString("studentId");
                        userRole = document.getString("role");

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
                    Toast.makeText(SsuchatChatting.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is null
            Toast.makeText(SsuchatChatting.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END);// 네비게이션 드로어를 닫습니다.

                if ("prof".equals(userRole)) { // 교수로 로그인 했으면 교수 메인 화면으로
                    switchToOtherActivity(ProfessorMainPage.class);
                } else { // 학생으로 로그인 했으면 학생 메인 화면으로
                    switchToOtherActivity(ssuchat_main_page.class);
                }
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(SsuchatChatting.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
            }
            return false;
        });

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("Item=" + i);
        }

        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chattingRecyclerView.setAdapter(new MyAdapter(list));

        binding.goBackPreChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(SsuchatChatting.this, SsuchatPreChat.class);
            startActivity(intent);
        });

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SsuchatChattingItemBinding binding;

        private MyViewHolder(SsuchatChattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(String text) {
            binding.chattingTime.setText(text);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SsuchatChattingItemBinding binding = SsuchatChattingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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