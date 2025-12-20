package com.example.comicreaderapp.api;

import com.example.comicreaderapp.model.Conversation;
import com.example.comicreaderapp.model.Message;
import com.example.comicreaderapp.model.MiniProfileUser;
import com.example.comicreaderapp.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApi {

    @GET("chatroom/api.php?action=get_conversations")
    Call<List<Conversation>> getConversations(
            @Query("user_id") String userId
    );

    @GET("chatroom/api.php?action=get_messages")
    Call<List<Message>> getMessages(
            @Query("conversation_id") int conversationId
    );

    @POST("chatroom/api.php?action=send_message")
    Call<Map<String, Object>> sendMessage(@Body SendMessageBody body);

    @GET("chatroom/api.php?action=get_public_profile")
    Call<MiniProfileUser> getUserProfile(@Query("user_id") String userId);

    @GET("chatroom/api.php?action=get_or_create_dm")
    Call<Map<String, Object>> getOrCreateDM(
            @Query("user_a") String userA,
            @Query("user_b") String userB
    );




}
