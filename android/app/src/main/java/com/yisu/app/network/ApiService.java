package com.yisu.app.network;

import com.yisu.app.model.Hotel;
import com.yisu.app.model.Result;
import com.yisu.app.model.RoomType;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("hotel/list")
    Call<Result<Page<Hotel>>> getHotelList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Query("name") String name, @Query("city") String city);

    @GET("hotel/detail/{id}")
    Call<Result<Map<String, Object>>> getHotelDetail(@Path("id") int id);
}
