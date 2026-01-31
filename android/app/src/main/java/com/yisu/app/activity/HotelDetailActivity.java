package com.yisu.app.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yisu.app.R;
import com.yisu.app.model.Result;
import com.yisu.app.network.RetrofitClient;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelDetailActivity extends AppCompatActivity {

    private int hotelId;
    private TextView tvName, tvStar, tvAddress, tvDesc;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);
        
        // Hide default Action Bar as we use CollapsingToolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        hotelId = getIntent().getIntExtra("hotelId", 0);
        
        tvName = findViewById(R.id.tvDetailName);
        tvStar = findViewById(R.id.tvDetailStar);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvDesc = findViewById(R.id.tvDetailDesc);
        ivImage = findViewById(R.id.ivDetailImage);
        
        loadDetail();
    }

    private void loadDetail() {
        RetrofitClient.getApiService().getHotelDetail(hotelId).enqueue(new Callback<Result<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<Result<Map<String, Object>>> call, Response<Result<Map<String, Object>>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body().data;
                    if(data != null && data.containsKey("hotel")) {
                        Object hotelObj = data.get("hotel");
                        if(hotelObj instanceof Map) {
                             Map hotel = (Map) hotelObj;
                             tvName.setText((String)hotel.get("name"));
                             tvStar.setText((String)hotel.get("starRating"));
                             tvAddress.setText((String)hotel.get("address"));
                             tvDesc.setText((String)hotel.get("description"));
                             // Glide.with(HotelDetailActivity.this).load(hotel.get("images")).into(ivImage);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(HotelDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
