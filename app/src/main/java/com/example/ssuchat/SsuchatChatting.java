package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatChattingBinding;
import com.example.ssuchat.databinding.SsuchatChattingItemBinding;
import com.example.ssuchat.model.ChatMessageModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SsuchatChatting extends AppCompatActivity {
    private DrawerLayout drawer;
    private String chatroomId;
    private String name;
    private String UID;
    String userRole;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ChatMessageModel> messageList = new ArrayList<>();
    private MyAdapter adapter;
    private ActivitySsuchatChattingBinding binding;
    private String className;
    private String classClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySsuchatChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);
        Intent intent = getIntent();
        chatroomId = intent.getStringExtra("classNumber");
        name = intent.getStringExtra("name");
        UID = intent.getStringExtra("UID");
        adapter = new MyAdapter(messageList, UID);
        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chattingRecyclerView.setAdapter(adapter);

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        Intent getIntent = getIntent();
        if (getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");

            binding.className.setText(className + "(" + classClass + ")");
        }

        if (user != null) {
            UID = user.getUid();
            setupRecyclerView();
            binding.chattingRecyclerView.setAdapter(adapter);
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
        binding.chattingRecyclerView.setAdapter(new MyAdapter(messageList, UID));

        binding.goBackPreChatButton.setOnClickListener(v -> {
            finish();
        });

        binding.transmitMessageButton.setOnClickListener(v -> {
            String messageText = binding.messageEditText.getText().toString();
            sendMessage(messageText);
            binding.messageEditText.setText(""); // 입력창 초기화
        });
        setupMessageListener();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private SsuchatChattingItemBinding binding;

        // 레이아웃의 참조를 가져옵니다.
        public MyViewHolder(SsuchatChattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatMessageModel message, String currentUserId) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(message.getTimestamp().toDate());
            if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
                // 현재 사용자의 메시지일 경우 오른쪽에 표시
                binding.rightChatLayout.setVisibility(View.VISIBLE);
                binding.leftChatLayout.setVisibility(View.GONE);
                binding.rightChatTextview.setText(message.getMessage());
                binding.rightChatName.setText(message.getSenderName()); // 사용자 이름 설정
                binding.rightChatTime.setText(formattedDate); // 시간 설정
            } else {
                // 다른 사용자의 메시지일 경우 왼쪽에 표시
                binding.rightChatLayout.setVisibility(View.GONE);
                binding.leftChatLayout.setVisibility(View.VISIBLE);
                binding.leftChatTextview.setText(message.getMessage());
                binding.leftChatName.setText(message.getSenderName()); // 사용자 이름 설정
                binding.leftChatTime.setText(formattedDate); // 시간 설정
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<ChatMessageModel> messageList;
        private String currentUserId;

        private MyAdapter(List<ChatMessageModel> messageList, String currentUserId) {
            this.messageList = messageList;
            this.currentUserId = currentUserId;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SsuchatChattingItemBinding binding = SsuchatChattingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ChatMessageModel message = messageList.get(position);
            holder.bind(message, currentUserId);
        }

        @Override
        public int getItemCount() {
            return messageList.size();
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

    private void setupMessageListener() {
        db.collection("chatrooms").document(chatroomId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Chat", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // 새 메시지를 리스트에 추가
                                ChatMessageModel message = dc.getDocument().toObject(ChatMessageModel.class);
                                messageList.add(message);
                            }
                        }

                        // 데이터가 변경되었음을 어댑터에 알리고, 리스트를 스크롤하여 최신 메시지 표시
                        adapter.notifyDataSetChanged();

                        // 메시지 리스트가 비어있지 않은 경우에만 스크롤
                        if (!messageList.isEmpty()) {
                            binding.chattingRecyclerView.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                });
    }

    private void setupRecyclerView() {
        // 현재 사용자 ID를 가져옵니다.
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 어댑터에 현재 사용자 ID를 전달합니다.
        adapter = new MyAdapter(messageList, currentUserId);
        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chattingRecyclerView.setAdapter(adapter);
    }

    private void sendMessage(String messageText) {
        if (user != null && !messageText.isEmpty()) {
            // Firestore에서 현재 사용자의 이름을 가져옵니다.
            db.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String senderName = task.getResult().getString("name");
                    String senderId = user.getUid(); // 현재 사용자의 UID를 가져옵니다.

                    if (senderName != null) {
                        // 메시지 모델 생성 및 전송
                        ChatMessageModel chatMessage = new ChatMessageModel(messageText, senderId, senderName, Timestamp.now());
                        db.collection("chatrooms").document(chatroomId)
                                .collection("messages")
                                .add(chatMessage)
                                .addOnSuccessListener(documentReference -> {
                                    // 메시지 전송 성공 처리
                                    Toast.makeText(this, "메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // 메시지 전송 실패 처리
                                    Toast.makeText(this, "메시지 전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // 사용자 이름이 Firestore에 없는 경우
                        Toast.makeText(this, "사용자 이름을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Firestore에서 사용자 정보를 가져오는 데 실패한 경우
                    Toast.makeText(this, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}