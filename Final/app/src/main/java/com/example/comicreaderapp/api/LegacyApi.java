package com.example.comicreaderapp.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface LegacyApi {

    // Generic GET for /getData/request.php?r=...
    @GET("getData/request.php")
    Call<ResponseBody> getData(@Query("r") String r, @QueryMap Map<String, String> options);

    // Convenience overload when no extra params are needed
    @GET("getData/request.php")
    Call<ResponseBody> getData(@Query("r") String r);

    // Generic POST to /getData/request.php?r=... with JSON body
    @POST("getData/request.php")
    Call<ResponseBody> postData(@Query("r") String r, @Body RequestBody body);
}