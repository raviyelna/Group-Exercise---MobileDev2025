package com.example.comicreaderapp.api;

public class SendMessageBody {
    public String conversation_id;
    public String sender_id;
    public String content;

    public SendMessageBody(String conversation_id, String sender_id, String content) {
        this.conversation_id = conversation_id;
        this.sender_id = sender_id;
        this.content = content;
    }
}
