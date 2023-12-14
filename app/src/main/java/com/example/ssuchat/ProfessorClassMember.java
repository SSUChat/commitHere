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

import com.example.ssuchat.databinding.ActivityProfessorClassMemberBinding;
import com.example.ssuchat.databinding.SsuchatLiveMemberItemBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorClassMember extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;
    private ActivityProfessorClassMemberBinding binding;

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
        binding = ActivityProfessorClassMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawer = findViewById(R.id.drawerLayout);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);


        Intent getIntent = getIntent();
        if (getIntent != null) {
            className = getIntent.getStringExtra("className");
            classClass = getIntent.getStringExtra("classClass");
            classNumber = getIntent.getStringExtra("classNumber");
            classBuilding = getIntent.getStringExtra("classBuilding");
            classAddress = getIntent.getStringExtra("classAddress");
        }

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
                switchToOtherActivity(ProfessorMainPage.class);
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ProfessorClassMember.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                logoutDialog();
            }
            return false;
        });

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
                Toast.makeText(ProfessorClassMember.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
            }
        });
        binding.backPreChatProfessor.setOnClickListener(v -> finish());

        binding.addClassMemberProfessor.setOnClickListener(v -> {
            String addClassMember = binding.addClassMember.getText().toString();
            if (addClassMember.isEmpty()) {
                Toast.makeText(ProfessorClassMember.this, "필드를 채워주세요", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Check if the student exists in the "users" collection
                db.collection("users")
                        .whereEqualTo("studentId", addClassMember)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Student exists, proceed to add to the class
                                    DocumentReference classRef = db.collection("class").document(className + classClass);

                                    // Check if the student is already enrolled
                                    classRef.get().addOnCompleteListener(classTask -> {
                                        if (classTask.isSuccessful()) {
                                            DocumentSnapshot document = classTask.getResult();
                                            if (document.exists()) {
                                                List<String> enrolledStudentsList = (List<String>) document.get("enrolledStudents");

                                                if (enrolledStudentsList == null || enrolledStudentsList.contains(addClassMember)) {
                                                    Toast.makeText(ProfessorClassMember.this, "이미 등록된 학생입니다.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                // Update the enrolled students list
                                                enrolledStudentsList.add(addClassMember);

                                                Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("enrolledStudents", enrolledStudentsList);

                                                // Update Firestore with the new enrolled students list
                                                classRef.update(updateData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.d("TAG", "Enrolled students list updated successfully");
                                                            // Update RecyclerView with the new list
                                                            updateRecyclerView(enrolledStudentsList);
                                                            Toast.makeText(ProfessorClassMember.this, "성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                            // Clear the addClassMember TextView
                                                            binding.addClassMember.getText().clear();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.w("TAG", "Error updating enrolled students list", e);
                                                            Toast.makeText(ProfessorClassMember.this, "추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", classTask.getException());
                                        }
                                    });
                                } else {
                                    // Student does not exist in the "users" collection
                                    Toast.makeText(ProfessorClassMember.this, "해당 학번은 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });
            }
        });



        binding.removeClassMemberProfessor.setOnClickListener(v -> {
            String removeClassMember = binding.removeClassMember.getText().toString();
            if (removeClassMember.isEmpty()) {
                Toast.makeText(ProfessorClassMember.this, "필드를 채워주세요", Toast.LENGTH_SHORT).show();
                return;
            } else {
                DocumentReference classRef = db.collection("class").document(className + classClass);

                // Get the current enrolled students list
                classRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> enrolledStudentsList = (List<String>) document.get("enrolledStudents");

                            if (enrolledStudentsList == null || !enrolledStudentsList.contains(removeClassMember)) {
                                Toast.makeText(ProfessorClassMember.this, "해당 학생은 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Remove the student from the enrolled students list
                            enrolledStudentsList.remove(removeClassMember);

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("enrolledStudents", enrolledStudentsList);

                            // Update Firestore with the updated enrolled students list
                            classRef.update(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("TAG", "Enrolled students list updated successfully");
                                        // Update RecyclerView with the updated list
                                        updateRecyclerView(enrolledStudentsList);
                                        Toast.makeText(ProfessorClassMember.this, "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        // Clear the addClassMember TextView
                                        binding.removeClassMember.getText().clear();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("TAG", "Error updating enrolled students list", e);
                                        Toast.makeText(ProfessorClassMember.this, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            }
        });


        String documentId = className + classClass;

        DocumentReference classRef = db.collection("class").document(documentId);

        classRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> enrolledStudents = (List<String>) document.get("enrolledStudents");

                    // Set enrolled students in RecyclerView
                    binding.classMemberRecyclerViewProfessor.setLayoutManager(new LinearLayoutManager(this));
                    binding.classMemberRecyclerViewProfessor.setAdapter(new ProfessorClassMember.MyAdapter(enrolledStudents));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void updateRecyclerView(List<String> enrolledStudentsList) {
        ProfessorClassMember.MyAdapter adapter = new ProfessorClassMember.MyAdapter(enrolledStudentsList);
        binding.classMemberRecyclerViewProfessor.setAdapter(adapter);
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

    private static class MyAdapter extends RecyclerView.Adapter<ProfessorClassMember.MyViewHolder> {

        private final List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ProfessorClassMember.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SsuchatLiveMemberItemBinding binding = SsuchatLiveMemberItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new ProfessorClassMember.MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfessorClassMember.MyViewHolder holder, int position) {
            String text = list.get(position);
            holder.bind(text);
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
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