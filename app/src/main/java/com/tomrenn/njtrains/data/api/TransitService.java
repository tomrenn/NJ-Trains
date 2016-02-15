package com.tomrenn.njtrains.data.api;

import com.tomrenn.njtrains.data.api.models.TransitInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;


public interface TransitService {
    @GET("/")
    Call<Map<String, TransitInfo>> getTransitInfo();

}
