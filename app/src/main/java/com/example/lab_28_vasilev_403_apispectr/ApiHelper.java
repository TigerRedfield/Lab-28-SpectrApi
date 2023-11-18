package com.example.lab_28_vasilev_403_apispectr;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {
        static final String BASE_URL= "http://194.87.68.149:5003/"; //Базовый адрес для конечных точек API

        public static CallApiSpectra getApiSpectra() {
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder() // создаём экземпляр библиотеки OkHTTP,
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // чтобы реализовать связь по HTTP
                        .build();

                Retrofit retrofit = new Retrofit.Builder() // создаём экземпляр библиотеки Retrofit
                        .baseUrl(BASE_URL) // чтобы работать с REST сервисами
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                return retrofit.create(CallApiSpectra.class);
        }
}
