package com.example.lab_28_vasilev_403_apispectr.Model;

import com.google.gson.annotations.SerializedName;

public class ChemElement
{
    @SerializedName("atomic_num")
    public Integer atomic_num;

    @SerializedName("full_name")
    public String full_name;

    public ChemElement(Integer atomic_num, String full_name){
        this.atomic_num = atomic_num;
        this.full_name = full_name;
    }

    @Override
    public String toString() {
        return full_name;
    }
}
