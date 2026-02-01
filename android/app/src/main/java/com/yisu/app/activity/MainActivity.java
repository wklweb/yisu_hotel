package com.yisu.app.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yisu.app.R;
import com.yisu.app.adapter.BannerAdapter;
import com.yisu.app.model.Banner;
import com.yisu.app.model.Result;
import com.yisu.app.network.ApiService;
import com.yisu.app.network.RetrofitClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private Button btnSearch;
    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutBanner;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList = new ArrayList<>();
    private ApiService apiService;
    
    private TextView tvCheckInDate, tvCheckOutDate;
    private ChipGroup chipGroupTags;
    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat dateFormat;
    private String selectedStarRating = null;
    private String selectedPriceRange = null;
    private String selectedTag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check login status
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            // Not logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();

        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        btnSearch = findViewById(R.id.btnSearch);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabLayoutBanner = findViewById(R.id.tabLayoutBanner);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        chipGroupTags = findViewById(R.id.chipGroupTags);

        // Initialize date format and calendars
        dateFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
        
        // Set initial dates
        updateDateDisplay();

        // Setup banner
        setupBanner();

        // Date picker listeners
        tvCheckInDate.setOnClickListener(v -> showDatePicker(true));
        tvCheckOutDate.setOnClickListener(v -> showDatePicker(false));

        // Chip group listener
        chipGroupTags.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedStarRating = null;
            selectedPriceRange = null;
            selectedTag = null;
            
            for (int id : checkedIds) {
                Chip chip = findViewById(id);
                if (chip != null) {
                    String text = chip.getText().toString();
                    if (text.contains("星")) {
                        selectedStarRating = text.replace("星", "星");
                    } else if (text.equals("经济型") || text.equals("舒适型")) {
                        selectedPriceRange = text;
                    } else {
                        selectedTag = text;
                    }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etSearchKeyword.getText().toString();
                Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("checkInDate", checkInCalendar.getTimeInMillis());
                intent.putExtra("checkOutDate", checkOutCalendar.getTimeInMillis());
                intent.putExtra("starRating", selectedStarRating);
                intent.putExtra("priceRange", selectedPriceRange);
                intent.putExtra("tag", selectedTag);
                startActivity(intent);
            }
        });

        // Load banners
        loadBanners();
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                if (isCheckIn) {
                    // Ensure check-out is after check-in
                    if (checkOutCalendar.before(checkInCalendar) || 
                        checkOutCalendar.equals(checkInCalendar)) {
                        checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
                        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                } else {
                    // Ensure check-out is after check-in
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
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvCheckInDate.setText(dateFormat.format(checkInCalendar.getTime()));
        tvCheckOutDate.setText(dateFormat.format(checkOutCalendar.getTime()));
        
        // Calculate nights
        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        long nights = diff / (24 * 60 * 60 * 1000);
        // Update nights display if needed
    }

    private void setupBanner() {
        bannerAdapter = new BannerAdapter(bannerList, new BannerAdapter.OnBannerClickListener() {
            @Override
            public void onBannerClick(Banner banner) {
                // Jump to hotel detail
                Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
                intent.putExtra("hotelId", banner.hotelId);
                startActivity(intent);
            }
        });
        viewPagerBanner.setAdapter(bannerAdapter);

        // Auto scroll
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayoutBanner, viewPagerBanner, (tab, position) -> {
            // Tab indicator dots
        }).attach();
    }

    private void loadBanners() {
        Call<Result<List<Banner>>> call = apiService.getBannerList();
        call.enqueue(new Callback<Result<List<Banner>>>() {
            @Override
            public void onResponse(Call<Result<List<Banner>>> call, Response<Result<List<Banner>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<Banner>> result = response.body();
                    if ("200".equals(result.code) && result.data != null && !result.data.isEmpty()) {
                        bannerList.clear();
                        bannerList.addAll(result.data);
                        bannerAdapter.notifyDataSetChanged();
                        // Update tab indicators
                        if (bannerList.size() > 1) {
                            tabLayoutBanner.setVisibility(View.VISIBLE);
                        } else {
                            tabLayoutBanner.setVisibility(View.GONE);
                        }
                    } else {
                        // No banners, show default
                        showDefaultBanner();
                    }
                } else {
                    showDefaultBanner();
                }
            }

            @Override
            public void onFailure(Call<Result<List<Banner>>> call, Throwable t) {
                showDefaultBanner();
            }
        });
    }

    private void showDefaultBanner() {
        // Show default banner if no banners available
        bannerList.clear();
        Banner defaultBanner = new Banner();
        defaultBanner.imageUrl = "";
        defaultBanner.hotelId = 0;
        bannerList.add(defaultBanner);
        bannerAdapter.notifyDataSetChanged();
        tabLayoutBanner.setVisibility(View.GONE);
    }
}
