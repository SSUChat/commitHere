package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ssuchat.databinding.ActivitySsuchatMainPageBinding;
import com.example.ssuchat.databinding.MainPageRecycleItemBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ssuchat_main_page extends AppCompatActivity {

    private MyAdapter myAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatMainPageBinding binding = ActivitySsuchatMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> list = new ArrayList<>();
        for(int i  = 0; i < 10; i++) {
            list.add("Item=" + i);
        }

        myAdapter = new MyAdapter(list);

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(getApplicationContext(), "onItemClick position : " + pos, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ssuchat_main_page.this, SsuchatPreChat.class);
                startActivity(intent);
                // 여기 수정!!!!!!!!
            }
        });

        myAdapter.setOnLongItemClickListener(new MyAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int pos) {
                Toast.makeText(getApplicationContext(), "onLongItemClick position : " + pos, Toast.LENGTH_SHORT).show();
            }
        });


        binding.recyclerViewMainPage.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMainPage.setAdapter(new MyAdapter(list));

        binding.logoutGoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ssuchat_main_page.this, ssuchat_login.class);
                startActivity(intent); // dialog 넣어서 정말 뒤로가시면 로그아웃 된다는 알림 넣어야 함
            }
        });
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private MainPageRecycleItemBinding binding;

        private MyViewHolder(MainPageRecycleItemBinding binding) {
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



        private void bind(String text) {
            // binding.mainPageText.setText(text);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
        }

        public interface OnItemClickListener {
            void onItemClick(int pos);
        }

        private static OnItemClickListener onItemClickListener = null;
        public void setOnItemClickListener(OnItemClickListener listener) {
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