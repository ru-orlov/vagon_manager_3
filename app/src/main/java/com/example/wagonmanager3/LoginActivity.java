package com.example.wagonmanager3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Инициализация UI элементов
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);

        // Обработчик нажатия кнопки входа
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем readable базу данных
        database = dbHelper.getReadableDatabase();

        // Проверяем пользователя в базе данных (в реальном приложении это должно быть в фоновом потоке!)
        User user = dbHelper.getUserByUsername(username);

        if (user != null && validatePassword(user, password)) {
            // Успешный вход
            startMainActivity(user);
        } else {
            // Неверные учетные данные
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePassword(User user, String inputPassword) {
        // В реальном приложении используйте хеширование паролей!
        // Это пример - НЕ используйте такое в продакшене
        return user.getPasswordHash().equals(inputPassword);
    }

    private void startMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_role", user.getRole());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем соединения с базой данных
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}