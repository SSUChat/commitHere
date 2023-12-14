package com.example.ssuchat;

import java.util.List;

public class ChatRoom {

    private String roomId;
    private List<String> memberIds;

    public ChatRoom(String roomId, List<String> memberIds) {
        this.roomId = roomId;
        this.memberIds = memberIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}