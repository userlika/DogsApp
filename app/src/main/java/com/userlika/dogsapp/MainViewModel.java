package com.userlika.dogsapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    private static final String TAG = "MainViewModel";

    private MutableLiveData<DogImage> dogImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    // Возвращаем родительский объект, чтобы в него не были установлены данные снаружи
    public LiveData<DogImage> getDogImage() {
        return dogImage;
    }

    public void loadDogImage(){
        Disposable disposable = loadImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() { // Callback для реакции на состояние (Подписка)
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        // Код внутри метода accept будет выполнен в момент подписки
                        isError.setValue(false);
                        isLoading.setValue(true);
                    }
                })
                .doAfterTerminate(new Action() { // Callback для реакции на состояние (После завершения)
                    @Override
                    public void run() throws Throwable {
                        isLoading.setValue(false);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isError.setValue(true);
                    }
                })
                .subscribe(new Consumer<DogImage>() {
                    @Override
                    public void accept(DogImage image) throws Throwable {
                        dogImage.setValue(image);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "Error: " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<DogImage> loadImageRx(){
        return Single.fromCallable(new Callable<DogImage>() {
            @Override
            public DogImage call() throws Exception {

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

                JSONObject jsonObject = new JSONObject(data.toString());
                String message = jsonObject.getString(KEY_MESSAGE);
                String status = jsonObject.getString(KEY_STATUS);

                return new DogImage(message, status);
                }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
