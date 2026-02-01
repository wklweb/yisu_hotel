package com.yisu.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yisu.app.R;
import com.yisu.app.model.LoginResult;
import com.yisu.app.model.Result;
import com.yisu.app.model.User;
import com.yisu.app.network.ApiService;
import com.yisu.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.getApiService();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Check if already logged in
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token != null) {
            // Already logged in, go to main
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(username, password);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login(String username, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("登录中...");

        User user = new User();
        user.username = username;
        user.password = password;

        Call<Result<LoginResult>> call = apiService.login(user);
        call.enqueue(new Callback<Result<LoginResult>>() {
            @Override
            public void onResponse(Call<Result<LoginResult>> call, Response<Result<LoginResult>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("登录");

                if (response.isSuccessful() && response.body() != null) {
                    Result<LoginResult> result = response.body();
                    if ("200".equals(result.code) && result.data != null) {
                        // Save token and user info
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", result.data.token);
                        editor.putInt("userId", result.data.user.id);
                        editor.putString("username", result.data.user.username);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, result.msg != null ? result.msg : "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<LoginResult>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("登录");
                Toast.makeText(LoginActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
