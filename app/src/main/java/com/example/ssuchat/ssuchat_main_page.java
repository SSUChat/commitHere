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

import com.example.ssuchat.databinding.ActivitySsuchatMainPageBinding;
import com.example.ssuchat.databinding.MainPageRecycleItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ssuchat_main_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatMainPageBinding binding = ActivitySsuchatMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> list = new ArrayList<>();
        for(int i  = 0; i < 10; i++) {
            list.add("Item=" + i);
        }

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

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private MainPageRecycleItemBinding binding;

        private MyViewHolder(MainPageRecycleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(String text) {
            binding.mainPageText.setText(text);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<String> list;

        private MyAdapter(List<String> list) {
            this.list = list;
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