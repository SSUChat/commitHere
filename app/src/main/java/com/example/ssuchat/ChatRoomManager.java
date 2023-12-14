package com.example.ssuchat;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class ChatRoomManager {
    private FirebaseFirestore firebaseFirestore;

    public ChatRoomManager(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public ChatRoom createChatRoom(List<String> memberIds) {

        // 채팅방 ID를 생성합니다.
        String roomId = UUID.randomUUID().toString();

        // 채팅방 데이터 모델을 생성합니다.
        ChatRoom chatRoom = new ChatRoom(roomId, memberIds);

        // 채팅방 데이터 모델을 데이터베이스에 저장합니다.
        DocumentReference documentReference = firebaseFirestore.collection("chatRooms").document(roomId);
        documentReference.set(chatRoom);

        return chatRoom;
    }



}