package com.userlika.dogsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadDogImage();
    }

    private void loadDogImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // При работе с интернетом адрес используется не в виде строки, а в виде объекта URL
                // Ctrl+Alt+T для добавления try catch
                try {
                    URL url = new URL(BASE_URL);
                    // openConnection() возвращает родительский тип URLConnection,
                    // HttpURLConnection - дочерний класс, поэтому нужно явное преобразование
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    // Объект InputStream используется для считывания данных из инета или файлов
                    // В таком случае ответ считывается побайтово
                    InputStream inputStream = urlConnection.getInputStream();

                    // Считывание посимвольно
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    // Для считывания целой строки
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder data = new StringBuilder();
                    String result;

                    do {
                        result = bufferedReader.readLine();
                        if (result != null) {
                            data.append(result);
                        }

                    } while (result != null);

                    Log.d("MainActivity", "Result: " + data);
                } catch (Exception e) {
                    Log.d("MainActivity", "Error: " + e.toString());
                }
            }
        }).start();

    }
}