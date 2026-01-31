package com.yisu.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.yisu.app.R;

public class MainActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etSearchKeyword.getText().toString();
                Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });
        
        // Handle Banner Click (Simulated)
        findViewById(R.id.bannerImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Could jump to a specific hotel
                // Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
                // intent.putExtra("hotelId", 1);
                // startActivity(intent);
            }
        });
    }
}
