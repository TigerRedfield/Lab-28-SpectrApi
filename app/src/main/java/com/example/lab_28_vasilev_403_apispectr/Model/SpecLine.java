package com.example.lab_28_vasilev_403_apispectr.Model;

import android.graphics.Paint;
import android.graphics.PathMeasure;

import com.google.gson.annotations.SerializedName;

public class SpecLine {

    @SerializedName("wavelength")
    public Float wavelength;

    @SerializedName("rel_intensity")
    Integer  rel_intensity;

    @SerializedName("red")
    double red;

    @SerializedName("green")
    double green;

    @SerializedName("blue")
    double blue;

    public Paint getPaint() {
        Paint p = new Paint();
        p.setARGB(255, (int) (red * 255.0), (int) (green * 255.0), (int) (blue * 255.0));
        return p;
    }

}
