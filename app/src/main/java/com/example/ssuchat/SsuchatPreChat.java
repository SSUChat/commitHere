package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatPreChatBinding;
import com.example.ssuchat.databinding.SsuchatLiveMemberItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SsuchatPreChat extends AppCompatActivity {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatPreChatBinding binding = ActivitySsuchatPreChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                //기능 추가하기. 일단 예시로 토스트
                Toast.makeText(SsuchatPreChat.this, "NavigationDrawer...home..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(SsuchatPreChat.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                drawer.closeDrawer(GravityCompat.END);

                MenuItem logoutItem = binding.navigationView.getMenu().findItem(R.id.nav_logout);
                logoutItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //로그아웃 기능 넣을 부분
                        FirebaseAuth.getInstance().signOut();

                        // Navigate to the login screen
                        Intent intent = new Intent(SsuchatPreChat.this, ssuchat_login.class);
                        startActivity(intent);
                        finish(); // Optional: close the current activity to prevent going back to it with the back button
                    }
                });
            }
            return false;
        });


        //Firestore에 저장된 유저 정보 가져오기
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.get().addOnCompleteListener(task -> {
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
                Toast.makeText(SsuchatPreChat.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
            }
        });

        List<String> list = new ArrayList<>();
        for(int i  = 0; i < 20; i++) {
            list.add("Item=" + i);
        }

        binding.liveMemberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.liveMemberRecyclerView.setAdapter(new MyAdapter(list));

        binding.backMainPage.setOnClickListener(v -> {
            Intent intent = new Intent(SsuchatPreChat.this, ssuchat_main_page.class);
            startActivity(intent);
        });

        binding.buttonEnterChatting.setOnClickListener(v -> {
            Intent intent = new Intent(SsuchatPreChat.this, SsuchatChatting.class);
            startActivity(intent);
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

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SsuchatLiveMemberItemBinding binding = SsuchatLiveMemberItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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

}