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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorMainPageBinding;
import com.example.ssuchat.databinding.MainPageRecycleItemBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfessorMainPage extends AppCompatActivity {

    private MyAdapter myAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DrawerLayout drawer;
    private String name;
    private int documentCnt;
    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private static class MyModel {
        private final String name;
        private final String className;
        private final String classClass;
        private final String classNumber;
        private final String classBuilding;
        private final String classAddress;
        private String classWeek1;
        private String classStartHour1;
        private String classStartMinute1;
        private String classEndHour1;
        private String classEndMinute1;
        private String classWeek2;
        private String classStartHour2;
        private String classStartMinute2;
        private String classEndHour2;
        private String classEndMinute2;

        public MyModel(String name, String className, String classClass, String classNumber, String classBuilding, String classAddress,
                       String classWeek1, String classStartHour1, String classStartMinute1, String classEndHour1, String classEndMinute1,
                       String classWeek2, String classStartHour2, String classStartMinute2, String classEndHour2, String classEndMinute2) {
            this.name = name;
            this.className = className;
            this.classClass = classClass;
            this.classNumber = classNumber;
            this.classBuilding = classBuilding;
            this.classAddress = classAddress;
            this.classWeek1 = classWeek1;
            this.classStartHour1 = classStartHour1;
            this.classStartMinute1 = classStartMinute1;
            this.classEndHour1 = classEndHour1;
            this.classEndMinute1 = classEndMinute1;
            this.classWeek2 = classWeek2;
            this.classStartHour2 = classStartHour2;
            this.classStartMinute2 = classStartMinute2;
            this.classEndHour2 = classEndHour2;
            this.classEndMinute2 = classEndMinute2;
        }

        public String getName() {
            return name;
        }
        public String getClassName() {
            return className;
        }
        public String getClassClass() {
            return classClass;
        }
        public String getClassNumber() {
            return classNumber;
        }
        public String getClassBuilding() {
            return classBuilding;
        }
        public String getClassAddress() {
            return classAddress;
        }
        public String getClassWeek1() {
            return classWeek1;
        }
        public String getClassStartHour1() {
            return classStartHour1;
        }
        public String getClassStartMinute1() {
            return classStartMinute1;
        }
        public String getClassEndHour1() {
            return classEndHour1;
        }
        public String getClassEndMinute1() {
            return classEndMinute1;
        }
        public String getClassWeek2() {
            return classWeek2;
        }
        public String getClassStartHour2() {
            return classStartHour2;
        }
        public String getClassStartMinute2() {
            return classStartMinute2;
        }
        public String getClassEndHour2() {
            return classEndHour2;
        }
        public String getClassEndMinute2() {
            return classEndMinute2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorMainPageBinding binding = ActivityProfessorMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawerLayout);
        String userId = user.getUid();

        binding.menuBtn.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));

        NavigationView sideNavigationView = findViewById(R.id.navigationView);
        sideNavigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                drawer.closeDrawer(GravityCompat.END); // 네비게이션 드로어를 닫습니다.
            } else if (id == R.id.nav_gallery) {
                // Handle navigation gallery
                Toast.makeText(ProfessorMainPage.this, "NavigationDrawer...gallery..", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfessorMainPage.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is null
            Toast.makeText(ProfessorMainPage.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

//        List<String> list = new ArrayList<>();
        List<MyModel> myModelList = new ArrayList<>();

        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 사용자 문서가 존재할 경우
                    name = document.getString("name");

                    CollectionReference collectionRef = db.collection(name);

                    collectionRef.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            documentCnt = task1.getResult().size();
                            // documentCount에는 특정 컬렉션의 문서 개수가 들어 있음

                            for (int i = 0; i < documentCnt; i++) {
                                // Firestore 문서에서 데이터 가져오기 (예시로 className과 classClass 가져옴)
                                String className = task1.getResult().getDocuments().get(i).getString("className");
                                String classClass = task1.getResult().getDocuments().get(i).getString("classClass");
                                String classNumber = task1.getResult().getDocuments().get(i).getString("classNumber");
                                String classBuilding = task1.getResult().getDocuments().get(i).getString("classBuilding");
                                String classAddress = task1.getResult().getDocuments().get(i).getString("classAddress");
                                String classWeek1 = task1.getResult().getDocuments().get(i).getString("selectWeek1");
                                String classStartHour1 = task1.getResult().getDocuments().get(i).getString("selectStartHour1");
                                String classStartMinute1 = task1.getResult().getDocuments().get(i).getString("selectStartMinute1");
                                String classEndHour1 = task1.getResult().getDocuments().get(i).getString("selectEndHour1");
                                String classEndMinute1 = task1.getResult().getDocuments().get(i).getString("selectEndMinute1");
                                String classWeek2 = task1.getResult().getDocuments().get(i).getString("selectWeek2");
                                String classStartHour2 = task1.getResult().getDocuments().get(i).getString("selectStartHour2");
                                String classStartMinute2 = task1.getResult().getDocuments().get(i).getString("selectStartMinute2");
                                String classEndHour2 = task1.getResult().getDocuments().get(i).getString("selectEndHour2");
                                String classEndMinute2 = task1.getResult().getDocuments().get(i).getString("selectEndMinute2");

                                // MyModel 객체 생성
                                MyModel myModel = new MyModel(name, className, classClass, classNumber, classBuilding, classAddress,
                                        classWeek1, classStartHour1, classStartMinute1, classEndHour1, classEndMinute1,
                                        classWeek2, classStartHour2, classStartMinute2, classEndHour2, classEndMinute2);

                                // 모델을 리스트에 추가
                                myModelList.add(myModel);
                            }

                            myAdapter = new MyAdapter(myModelList);

                            myAdapter.setOnItemClickListener(pos -> {
                                Toast.makeText(getApplicationContext(), "onItemClick position : " + pos, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfessorMainPage.this, ProfessorPreChat.class);

                                String className = task1.getResult().getDocuments().get(pos).getString("className");
                                String classClass = task1.getResult().getDocuments().get(pos).getString("classClass");
                                String classNumber = task1.getResult().getDocuments().get(pos).getString("classNumber");
                                String classBuilding = task1.getResult().getDocuments().get(pos).getString("classBuilding");
                                String classAddress = task1.getResult().getDocuments().get(pos).getString("classAddress");
                                String classWeek1 = task1.getResult().getDocuments().get(pos).getString("classWeek1");
                                String classStartHour1 = task1.getResult().getDocuments().get(pos).getString("classStartHour1");
                                String classStartMinute1 = task1.getResult().getDocuments().get(pos).getString("classStartMinute1");
                                String classEndHour1 = task1.getResult().getDocuments().get(pos).getString("classEndHour1");
                                String classEndMinute1 = task1.getResult().getDocuments().get(pos).getString("classEndMinute1");
                                String classWeek2 = task1.getResult().getDocuments().get(pos).getString("classWeek2");
                                String classStartHour2 = task1.getResult().getDocuments().get(pos).getString("classStartHour2");
                                String classStartMinute2 = task1.getResult().getDocuments().get(pos).getString("classStartMinute2");
                                String classEndHour2 = task1.getResult().getDocuments().get(pos).getString("classEndHour2");
                                String classEndMinute2 = task1.getResult().getDocuments().get(pos).getString("classEndMinute2");

                                intent.putExtra("className", className);
                                intent.putExtra("classClass", classClass);
                                intent.putExtra("classNumber", classNumber);
                                intent.putExtra("classBuilding", classBuilding);
                                intent.putExtra("classAddress", classAddress);
                                intent.putExtra("classWeek1", classWeek1);
                                intent.putExtra("classStartHour1", classStartHour1);
                                intent.putExtra("classStartMinute1", classStartMinute1);
                                intent.putExtra("classEndHour1", classEndHour1);
                                intent.putExtra("classEndMinute1", classEndMinute1);
                                intent.putExtra("classWeek2", classWeek2);
                                intent.putExtra("classStartHour2", classStartHour2);
                                intent.putExtra("classStartMinute2", classStartMinute2);
                                intent.putExtra("classEndHour2", classEndHour2);
                                intent.putExtra("classEndMinute2", classEndMinute2);

                                startActivity(intent);
                            });

                            myAdapter.setOnLongItemClickListener(pos -> Toast.makeText(getApplicationContext(), "onLongItemClick position : " + pos, Toast.LENGTH_SHORT).show());

                            binding.recyclerViewMainPageProfessor.setLayoutManager(new LinearLayoutManager(ProfessorMainPage.this));
                            binding.recyclerViewMainPageProfessor.setAdapter(new MyAdapter(myModelList));

                            Log.d(TAG, "Document count: " + documentCnt);
                        } else {
                            // 작업이 실패한 경우
                            Log.w(TAG, "Error getting documents.", task1.getException());
                        }
                    });
                } else {
                    // 사용자 문서가 존재하지 않을 경우
                    Log.d(TAG, "No such document");
                }
            } else {
                // 작업이 실패한 경우
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        binding.classAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorMainPage.this, ClassAddPage.class);
            startActivity(intent);
        });

        binding.logoutGoLoginButtonProfessor.setOnClickListener(v -> logoutDialog());

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MainPageRecycleItemBinding binding;

        public MyViewHolder(MainPageRecycleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.mainPageItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (MyAdapter.onItemClickListener != null) {
                        MyAdapter.onItemClickListener.onItemClick(position);
                    }
                }
            });

            binding.mainPageItem.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (MyAdapter.onLongItemClickListener != null) {
                        MyAdapter.onLongItemClickListener.onLongItemClick(position);
                        return true;
                    }
                }
                return false;
            });
        }

        private void bind(MyModel myModel) {
            binding.classProfessor.setText(myModel.getName());
            binding.className.setText(myModel.getClassName());
            binding.classClass.setText("(" + myModel.getClassClass() + ")");
            binding.classNumber.setText("("+ myModel.getClassNumber() + ")");
            binding.classBuilding.setText(myModel.getClassBuilding());
            binding.classAddress.setText(myModel.getClassAddress());
            binding.classWeek1.setText(myModel.getClassWeek1());
            binding.classStartHour1.setText(myModel.getClassStartHour1());
            binding.classStartMinute1.setText(myModel.getClassStartMinute1());
            binding.classEndHour1.setText(myModel.getClassEndHour1());
            binding.classEndMinute1.setText(myModel.getClassEndMinute1());
            binding.classWeek2.setText(myModel.getClassWeek2());
            binding.classStartHour2.setText(myModel.getClassStartHour2());
            binding.classStartMinute2.setText(myModel.getClassStartMinute2());
            binding.classEndHour2.setText(myModel.getClassEndHour2());
            binding.classEndMinute2.setText(myModel.getClassEndMinute2());
            if (myModel.getClassWeek1() == null) {
                binding.timeB1.setVisibility(View.GONE);
                binding.timeStart1.setVisibility(View.GONE);
                binding.timeStart11.setVisibility(View.GONE);
            }
            if (myModel.getClassWeek2() == null) {
                binding.timeB2.setVisibility(View.GONE);
                binding.timeStart2.setVisibility(View.GONE);
                binding.timeStart22.setVisibility(View.GONE);
            }
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final List<MyModel> myModelList;

        private MyAdapter(List<MyModel> myModelList) {
            this.myModelList = myModelList;
        }

        public interface OnItemClickListener {
            void onItemClick(int pos);
        }

        private static OnItemClickListener onItemClickListener = null;

        public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
            onItemClickListener = listener;
        }

        public interface OnLongItemClickListener {
            void onLongItemClick(int pos);
        }

        private static OnLongItemClickListener onLongItemClickListener = null;

        public void setOnLongItemClickListener(OnLongItemClickListener listener) {
            onLongItemClickListener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainPageRecycleItemBinding binding = MainPageRecycleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MyModel myModel = myModelList.get(position);
            holder.bind(myModel);
        }

        @Override
        public int getItemCount() {
            return myModelList.size();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();    // 에러 떠도 컴파일 잘됨!!!
        // 이전 액티비티로 못돌아가게 하려고 주석처리 해놓은거니까 수정 ㄴㄴ!!!
        logoutDialog();
    }
}