package com.example.whack_a_mole;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

//    // GET 请求
//    @GET("data/{id}")
//    Call<ResponseModel> getData(@Path("id") int id);
//
//    // POST 请求
//    @POST("data")
//    Call<ResponseModel> postData(@Body RequestModel requestModel);

    @POST("database/actInfos/insert_one")
    Call<ActInfo> insertData(@Body ActInfo actInfo);

    @GET("database/actInfos/queryByUserName")
    Call<List<ActInfo>> getByUsername(@Query("userName") String username);

    @GET("database/actInfos/findAll")
    Call<List<ActInfo>> getAll();
}
