package com.example.whack_a_mole;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://android.mythovac.com/"; // 替换为你的基础 URL
    private static Retrofit retrofit;
    private ApiService apiService;

    public RetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        apiService = retrofit.create(ApiService.class);
    }

    public void insertData(ActInfo actInfo, final DataCallback<ActInfo> callback) {
        apiService.insertData(actInfo).enqueue(new Callback<ActInfo>() {
            @Override
            public void onResponse(Call<ActInfo> call, Response<ActInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 调用成功的回调
                    callback.onSuccess(response.body());
                } else {
                    // 处理非成功响应
                    callback.onError(new Throwable("插入失败，响应代码: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ActInfo> call, Throwable t) {
                // 处理失败情况
                callback.onError(t);
            }
        });
    }

    public void getByUsername(String username, final DataCallback<List<ActInfo>> callback) {
        apiService.getByUsername(username).enqueue(new Callback<List<ActInfo>>() {
            @Override
            public void onResponse(Call<List<ActInfo>> call, Response<List<ActInfo>> response) {
                // 调用成功的回调
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<ActInfo>> call, Throwable t) {
                // 处理失败情况
                callback.onError(t);
            }
        });
    }

    public void getAll(final DataCallback<List<ActInfo>> callback) {
        apiService.getAll().enqueue(new Callback<List<ActInfo>>() {
            @Override
            public void onResponse(Call<List<ActInfo>> call, Response<List<ActInfo>> response) {
//                // 调用成功的回调
//                callback.onSuccess(response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "响应: " + new Gson().toJson(response.body()));
                    callback.onSuccess(response.body());
                } else {
                    Log.e("API Error", "错误代码: " + response.code() + ", 错误信息: " + response.message());
                    Log.e("API Error", "响应体为 null");
                    callback.onError(new Throwable("响应体为 null"));
                }
            }

            @Override
            public void onFailure(Call<List<ActInfo>> call, Throwable t) {
                // 处理失败情况
                callback.onError(t);
            }
        });
    }

    // 自定义回调接口
    public interface DataCallback<T> {
        void onSuccess(T data);       // 成功时的回调
        void onError(Throwable throwable); // 失败时的回调
    }

}
