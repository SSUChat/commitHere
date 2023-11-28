package com.example.ssuchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ssuchat.databinding.ActivitySsuchatChattingBinding;
import com.example.ssuchat.databinding.MainPageRecycleItemBinding;
import com.example.ssuchat.databinding.SsuchatChattingItemBinding;

import java.util.ArrayList;
import java.util.List;

public class SsuchatChatting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySsuchatChattingBinding binding = ActivitySsuchatChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> list = new ArrayList<>();
        for(int i  = 0; i < 10; i++) {
            list.add("Item=" + i);
        }

        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chattingRecyclerView.setAdapter(new MyAdapter(list));

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private SsuchatChattingItemBinding binding;

        private MyViewHolder(SsuchatChattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(String text) {
            binding.chattingTime.setText(text);
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
}