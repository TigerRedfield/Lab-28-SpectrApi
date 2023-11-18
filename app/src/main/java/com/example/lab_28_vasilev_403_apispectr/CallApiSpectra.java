package com.example.lab_28_vasilev_403_apispectr;

import com.example.lab_28_vasilev_403_apispectr.Model.ChemElement;
import com.example.lab_28_vasilev_403_apispectr.Model.LuminanceModel;
import com.example.lab_28_vasilev_403_apispectr.Model.RGBrange;
import com.example.lab_28_vasilev_403_apispectr.Model.SpecLine;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CallApiSpectra {

    @GET("/rpc/get_lines")
    Call<List<SpecLine>> getLines(
            @Query("atomic_num") Integer atomic_num
    );

    @GET("/rpc/get_elements")
    Call <List<ChemElement>> getElements(
    );

    @GET("/rpc/get_luminance_profile")
    Call <List<LuminanceModel>> getLuminance(
            @Query("experiment_id") Integer experiment_id
    );

    @GET("/rpc/nm_to_rgb_range")
    Call<List<RGBrange>> getRGBrange(
            @Query("nm_from") Float nmFrom,
            @Query("nm_to") Float nmTo,
            @Query("steps") Integer steps
    );
}
