package com.yisu.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yisu.app.R;
import com.yisu.app.adapter.HotelAdapter;
import com.yisu.app.model.Hotel;
import com.yisu.app.model.Page;
import com.yisu.app.model.Result;
import com.yisu.app.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private List<Hotel> hotelList = new ArrayList<>();
    private EditText etListSearch;
    private Button btnListSearch;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        keyword = getIntent().getStringExtra("keyword");
        
        etListSearch = findViewById(R.id.etListSearch);
        if(keyword != null) {
            etListSearch.setText(keyword);
        }
        btnListSearch = findViewById(R.id.btnListSearch);
        
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotelAdapter(hotelList);
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(new HotelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Hotel hotel) {
                Intent intent = new Intent(HotelListActivity.this, HotelDetailActivity.class);
                intent.putExtra("hotelId", hotel.id);
                startActivity(intent);
            }
        });

        btnListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(etListSearch.getText().toString());
            }
        });

        loadData(keyword);
    }

    private void loadData(String name) {
        RetrofitClient.getApiService().getHotelList(1, 20, name, null).enqueue(new Callback<Result<Page<Hotel>>>() {
            @Override
            public void onResponse(Call<Result<Page<Hotel>>> call, Response<Result<Page<Hotel>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    hotelList.clear();
                    hotelList.addAll(response.body().data.records);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HotelListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Page<Hotel>>> call, Throwable t) {
                Toast.makeText(HotelListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
