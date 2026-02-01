package com.yisu.app.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yisu.app.R;
import com.yisu.app.adapter.HotelImageAdapter;
import com.yisu.app.adapter.RoomTypeAdapter;
import com.yisu.app.model.Hotel;
import com.yisu.app.model.Result;
import com.yisu.app.model.RoomType;
import com.yisu.app.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelDetailActivity extends AppCompatActivity {

    private int hotelId;
    private TextView tvName, tvStar, tvAddress, tvDesc, tvDetailCheckIn, tvDetailCheckOut, tvDetailNights;
    private ViewPager2 viewPagerImages;
    private TabLayout tabLayoutImages;
    private RecyclerView rvRooms;
    private Toolbar toolbar;
    
    private HotelImageAdapter imageAdapter;
    private RoomTypeAdapter roomAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private List<RoomType> roomTypes = new ArrayList<>();
    
    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);
        
        hotelId = getIntent().getIntExtra("hotelId", 0);
        long checkInMillis = getIntent().getLongExtra("checkInDate", 0);
        long checkOutMillis = getIntent().getLongExtra("checkOutDate", 0);
        
        // Initialize calendars
        dateFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
        
        if (checkInMillis > 0) {
            checkInCalendar.setTimeInMillis(checkInMillis);
        }
        if (checkOutMillis > 0) {
            checkOutCalendar.setTimeInMillis(checkOutMillis);
        }
        
        initViews();
        setupToolbar();
        setupImagePager();
        setupRoomList();
        updateDateDisplay();
        
        loadDetail();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvDetailName);
        tvStar = findViewById(R.id.tvDetailStar);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvDesc = findViewById(R.id.tvDetailDesc);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        tabLayoutImages = findViewById(R.id.tabLayoutImages);
        rvRooms = findViewById(R.id.rvRooms);
        tvDetailCheckIn = findViewById(R.id.tvDetailCheckIn);
        tvDetailCheckOut = findViewById(R.id.tvDetailCheckOut);
        tvDetailNights = findViewById(R.id.tvDetailNights);
        
        tvDetailCheckIn.setOnClickListener(v -> showDatePicker(true));
        tvDetailCheckOut.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupImagePager() {
        imageAdapter = new HotelImageAdapter(imageUrls);
        viewPagerImages.setAdapter(imageAdapter);
        
        if (imageUrls.size() > 1) {
            new TabLayoutMediator(tabLayoutImages, viewPagerImages, (tab, position) -> {
            }).attach();
        } else {
            tabLayoutImages.setVisibility(android.view.View.GONE);
        }
    }

    private void setupRoomList() {
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        roomAdapter = new RoomTypeAdapter(roomTypes);
        rvRooms.setAdapter(roomAdapter);
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                if (isCheckIn) {
                    if (checkOutCalendar.before(checkInCalendar) || 
                        checkOutCalendar.equals(checkInCalendar)) {
                        checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
                        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                } else {
                    if (checkOutCalendar.before(checkInCalendar) || 
                        checkOutCalendar.equals(checkInCalendar)) {
                        checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
                        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
                updateDateDisplay();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvDetailCheckIn.setText(dateFormat.format(checkInCalendar.getTime()));
        tvDetailCheckOut.setText(dateFormat.format(checkOutCalendar.getTime()));
        
        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        long nights = diff / (24 * 60 * 60 * 1000);
        tvDetailNights.setText(nights + "晚");
    }

    private void loadDetail() {
        RetrofitClient.getApiService().getHotelDetail(hotelId).enqueue(new Callback<Result<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<Result<Map<String, Object>>> call, Response<Result<Map<String, Object>>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Result<Map<String, Object>> result = response.body();
                    if("200".equals(result.code) && result.data != null) {
                        Map<String, Object> data = result.data;
                        
                        // Parse hotel
                        if(data.containsKey("hotel")) {
                            Object hotelObj = data.get("hotel");
                            if(hotelObj instanceof Map) {
                                Map hotel = (Map) hotelObj;
                                tvName.setText((String)hotel.get("name"));
                                tvStar.setText((String)hotel.get("starRating"));
                                tvAddress.setText((String)hotel.get("address"));
                                tvDesc.setText((String)hotel.get("description"));
                                
                                // Parse images
                                Object imagesObj = hotel.get("images");
                                if(imagesObj != null) {
                                    parseImages(imagesObj.toString());
                                }
                                
                                // Set toolbar title
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().setTitle((String)hotel.get("name"));
                                }
                            }
                        }
                        
                        // Parse rooms
                        if(data.containsKey("rooms")) {
                            Object roomsObj = data.get("rooms");
                            if(roomsObj instanceof List) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<RoomType>>(){}.getType();
                                roomTypes = gson.fromJson(gson.toJson(roomsObj), listType);
                                roomAdapter = new RoomTypeAdapter(roomTypes);
                                rvRooms.setAdapter(roomAdapter);
                            }
                        }
                    }
                } else {
                    Toast.makeText(HotelDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(HotelDetailActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseImages(String imagesStr) {
        imageUrls.clear();
        try {
            if(imagesStr.startsWith("[")) {
                // JSON array
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>(){}.getType();
                imageUrls = gson.fromJson(imagesStr, listType);
            } else if(imagesStr.contains(",")) {
                // Comma separated
                String[] urls = imagesStr.split(",");
                for(String url : urls) {
                    imageUrls.add(url.trim());
                }
            } else {
                // Single image
                imageUrls.add(imagesStr);
            }
        } catch (Exception e) {
            // Fallback: use cover image or default
            imageUrls.add("");
        }
        
        if(imageUrls.isEmpty()) {
            imageUrls.add("");
        }
        
        imageAdapter = new HotelImageAdapter(imageUrls);
        viewPagerImages.setAdapter(imageAdapter);
        
        if(imageUrls.size() > 1) {
            tabLayoutImages.setVisibility(android.view.View.VISIBLE);
            new TabLayoutMediator(tabLayoutImages, viewPagerImages, (tab, position) -> {
            }).attach();
        } else {
            tabLayoutImages.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
