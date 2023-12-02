package com.example.ssuchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivityProfessorMainPageBinding;
import com.example.ssuchat.databinding.MainPageRecycleItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfessorMainPage extends AppCompatActivity {

    private MyAdapter myAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String name;
    private int documentCnt;
    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private class MyModel {
        private String name;
        private String className;
        private String classClass;
        private String classNumber;
        private String classBuilding;
        private String classAddress;

        public MyModel(String name, String className, String classClass, String classNumber, String classBuilding, String classAddress) {
            this.name = name;
            this.className = className;
            this.classClass = classClass;
            this.classNumber = classNumber;
            this.classBuilding = classBuilding;
            this.classAddress = classAddress;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfessorMainPageBinding binding = ActivityProfessorMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
//        List<String> list = new ArrayList<>();
        List<MyModel> myModelList = new ArrayList<>();

        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 사용자 문서가 존재할 경우
                        name = document.getString("name");

                        CollectionReference collectionRef = db.collection(name);

                        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    documentCnt = task.getResult().size();
                                    // documentCount에는 특정 컬렉션의 문서 개수가 들어 있음

//                                    Log.d(TAG, "Document count1: " + documentCnt);
//                                    for(int i  = 0; i < documentCnt; i++) {
//                                        list.add("Item=" + i);
//                                    }

                                    for (int i = 0; i < documentCnt; i++) {
                                        // Firestore 문서에서 데이터 가져오기 (예시로 className과 classClass 가져옴)
                                        String className = task.getResult().getDocuments().get(i).getString("className");
                                        String classClass = task.getResult().getDocuments().get(i).getString("classClass");
                                        String classNumber = task.getResult().getDocuments().get(i).getString("classNumber");
                                        String classBuilding = task.getResult().getDocuments().get(i).getString("classBuilding");
                                        String classAddress = task.getResult().getDocuments().get(i).getString("classAddress");

                                        // MyModel 객체 생성
                                        MyModel myModel = new MyModel(name, className, classClass, classNumber, classBuilding, classAddress);

                                        // 모델을 리스트에 추가
                                        myModelList.add(myModel);
                                    }

                                    myAdapter = new MyAdapter(myModelList);

                                    myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int pos) {
                                            Toast.makeText(getApplicationContext(), "onItemClick position : " + pos, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProfessorMainPage.this, ProfessorPreChat.class);
                                            startActivity(intent);
                                        }
                                    });

                                    myAdapter.setOnLongItemClickListener(new MyAdapter.OnLongItemClickListener() {
                                        @Override
                                        public void onLongItemClick(int pos) {
                                            Toast.makeText(getApplicationContext(), "onLongItemClick position : " + pos, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    binding.recyclerViewMainPageProfessor.setLayoutManager(new LinearLayoutManager(ProfessorMainPage.this));
                                    binding.recyclerViewMainPageProfessor.setAdapter(new MyAdapter(myModelList));

                                    Log.d(TAG, "Document count: " + documentCnt);
                                } else {
                                    // 작업이 실패한 경우
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
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
            }
        });

        binding.classAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainPage.this, ClassAddPage.class);
                startActivity(intent);
            }
        });

        binding.logoutGoLoginButtonProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainPage.this, ssuchat_login.class);
                startActivity(intent);
            }
        });

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private MainPageRecycleItemBinding binding;

        public MyViewHolder(MainPageRecycleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.mainPageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (MyAdapter.onItemClickListener != null) {
                            MyAdapter.onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            binding.mainPageItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (MyAdapter.onLongItemClickListener != null) {
                            MyAdapter.onLongItemClickListener.onLongItemClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        private void bind(MyModel myModel) {
            binding.classProfessor.setText(myModel.getName());
            binding.className.setText(myModel.getClassName());
            binding.classClass.setText("(" + myModel.getClassClass() + ")");
            binding.classNumber.setText("("+ myModel.getClassNumber() + ")");
            binding.classBuilding.setText(myModel.getClassBuilding());
            binding.classAddress.setText(myModel.getClassAddress());
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<MyModel> myModelList;

        private MyAdapter(List<MyModel> myModelList) {
            this.myModelList = myModelList;
        }

        public interface OnItemClickListener {
            void onItemClick(int pos);
        }

        private static OnItemClickListener onItemClickListener = null;

        public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        public interface OnLongItemClickListener {
            void onLongItemClick(int pos);
        }

        private static OnLongItemClickListener onLongItemClickListener = null;

        public void setOnLongItemClickListener(OnLongItemClickListener listener) {
            this.onLongItemClickListener = listener;
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
}