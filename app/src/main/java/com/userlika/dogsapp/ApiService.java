package com.userlika.dogsapp;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("image/random") // Аннотация для получения данных по сети (сюда передается endpoint - конечная часть URL
    Single<DogImage> loadDogImage();
}
