package com.example.lab_28_vasilev_403_apispectr.Model;

import com.google.gson.annotations.SerializedName;

public class LuminanceModel {


    @SerializedName("nm")
    Float nanometers;

    @SerializedName("lum")
    Float luminance;

    public Float getNanometers() {
        return nanometers;
    }

    public Float getLuminance() {
        return luminance;
    }
}
