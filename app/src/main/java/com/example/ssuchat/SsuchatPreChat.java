package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.material.navigation.NavigationView;
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

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                // Handle navigation home
                Toast.makeText(SsuchatPreChat.this, "NavigationDrawer...home..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(SsuchatPreChat.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
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
        builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (drawer.isDrawerOpen(GravityCompat.END)) // 네비게이션 드로어 열려있으면
                    drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.

                // 로그아웃 기능을 수행합니다.
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동합니다.
                switchToOtherActivity(ssuchat_login.class);
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼을 눌렀을 때의 동작
                dialog.dismiss(); // 다이얼로그 닫기
            }
        });
        builder.show();
    }
}