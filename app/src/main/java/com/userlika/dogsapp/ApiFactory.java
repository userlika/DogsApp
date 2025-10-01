package com.userlika.dogsapp;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    // Базовая часть URL должна заканчиваться косой чертой, все остальное указывать в endpoint
    private static final String BASE_URL = "https://dog.ceo/api/breeds/";
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // URL, на который будем отправлять запросы
                    .addConverterFactory(GsonConverterFactory.create()) // Преобразует JSON объекты в экземпляры нашего класса
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // Чтобы Retrofit возвращал объект Single
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }


}
