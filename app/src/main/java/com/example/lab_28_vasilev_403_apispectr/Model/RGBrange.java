package com.example.lab_28_vasilev_403_apispectr.Model;

import android.graphics.Paint;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class RGBrange {

    @SerializedName("wavelength")
    public Float wavelength;

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
