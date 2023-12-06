package com.example.ssuchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorPreChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                // Handle navigation home
                Toast.makeText(ProfessorPreChat.this, "NavigationDrawer...home..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ProfessorPreChat.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
//                Button logoutButton = binding.navigationView.getHeaderView(0).findViewById(R.id.nav_logout);
//
//                logoutButton.setOnClickListener(view -> {
//                    // Handle navigation logout
//                    drawer.closeDrawer(GravityCompat.END);
//
//                    // Implement logout functionality
//                    FirebaseAuth.getInstance().signOut();
//
//                    // Navigate to the login screen
//                    Intent intent = new Intent(ProfessorPreChat.this, ssuchat_login.class);
//                    startActivity(intent);
//                    finish(); // Optional: close the current activity to prevent going back to it with the back button
//                });
                MenuItem logoutItem = binding.navigationView.getMenu().findItem(R.id.nav_logout);
                logoutItem.setOnMenuItemClickListener(item -> {
                    drawer.closeDrawer(GravityCompat.END);
                    // 로그아웃 기능 넣을 부분
                    FirebaseAuth.getInstance().signOut();

                    // Navigate to the login screen
                    Intent intent = new Intent(ProfessorPreChat.this, ssuchat_login.class);
                    startActivity(intent);
//                    finish(); // Optional: close the current activity to prevent going back to it with the back button
                    return true;
                });
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
            Intent intent = new Intent(ProfessorPreChat.this, ProfessorMainPage.class);
            startActivity(intent);
        });

        binding.buttonEnterChattingProfessor.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorPreChat.this, SsuchatChatting.class);
            startActivity(intent);
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
}