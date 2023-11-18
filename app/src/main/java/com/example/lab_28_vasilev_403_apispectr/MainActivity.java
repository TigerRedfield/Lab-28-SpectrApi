package com.example.lab_28_vasilev_403_apispectr;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.lab_28_vasilev_403_apispectr.Model.ChemElement;
import com.example.lab_28_vasilev_403_apispectr.Model.LuminanceModel;
import com.example.lab_28_vasilev_403_apispectr.Model.RGBrange;
import com.example.lab_28_vasilev_403_apispectr.Model.SpecLine;
import com.example.lab_28_vasilev_403_apispectr.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;

    LocalDB db;

    CallApiSpectra API = ApiHelper.getApiSpectra();
    ArrayAdapter<ChemElement> adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        adp = new ArrayAdapter<ChemElement>(this, android.R.layout.simple_list_item_1);
        db = new LocalDB(this.getApplicationContext());

        API.getElements().enqueue(new Callback<List<ChemElement>>() {
                                      @Override
                                      public void onResponse(Call<List<ChemElement>> call, Response<List<ChemElement>> response) {
                                          adp.addAll(response.body());

                                          adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                          mainBinding.spinnerElement.setAdapter(adp);

                                          AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                                              @Override
                                              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                  API.getLines(position).enqueue(new Callback<List<SpecLine>>() {
                                                      @Override
                                                      public void onResponse(Call<List<SpecLine>> call, Response<List<SpecLine>> response) {
                                                          mainBinding.ButLoad.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  mainBinding.spectraView.refreshSpectre(response.body());
                                                              }
                                                          });
                                                      }

                                                      @Override
                                                      public void onFailure(Call<List<SpecLine>> call, Throwable t) {

                                                      }
                                                  });

                                                  API.getLuminance(position).enqueue(new Callback<List<LuminanceModel>>() {
                                                      @Override
                                                      public void onResponse(Call<List<LuminanceModel>> call, Response<List<LuminanceModel>> response) {
                                                          mainBinding.ButLoad.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  mainBinding.spectraView.refreshGraph(response.body());
                                                              }
                                                          });
                                                      }

                                                      @Override
                                                      public void onFailure(Call<List<LuminanceModel>> call, Throwable t) {

                                                      }
                                                  });


                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> parent) {

                                              }

                                          };
                                          mainBinding.spinnerElement.setOnItemSelectedListener(itemSelectedListener);

                                      }

                                      @Override
                                      public void onFailure(Call<List<ChemElement>> call, Throwable t) {

                                      }
                                  });

        mainBinding.spectraView.setWillNotDraw(false);
        mainBinding.spectraView.invalidate();

        mainBinding.butPlus.setOnClickListener(v -> {
            float wlen_center = (mainBinding.spectraView.wlen_max + mainBinding.spectraView.wlen_min) / 2.0f;
            float wlen_dist = wlen_center - mainBinding.spectraView.wlen_min;
            float zoom_percent = 0.1f;
            mainBinding.spectraView.wlen_min += wlen_dist * zoom_percent;
            mainBinding.spectraView.wlen_max -= wlen_dist * zoom_percent;
            mainBinding.spectraView.have_background = false;
            mainBinding.spectraView.invalidate();
        });

        mainBinding.butMinus.setOnClickListener(v -> {
            float wlen_center = (mainBinding.spectraView.wlen_max + mainBinding.spectraView.wlen_min) / 2.0f;
            float wlen_dist = wlen_center - mainBinding.spectraView.wlen_min;
            float zoom_percent = 0.1f;
            mainBinding.spectraView.wlen_min -= wlen_dist * zoom_percent;
            mainBinding.spectraView.wlen_max += wlen_dist * zoom_percent;
            mainBinding.spectraView.have_background = false;
            mainBinding.spectraView.invalidate();
        });

    }
    @Override
    protected void onPause() {
        super.onPause();

        // Сохраняем данные в базу данных при приостановке активности
        mainBinding.spectraView.saveSpectrCurrent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Восстанавливаем данные из базы данных при восстановлении активности
        mainBinding.spectraView.restoreSpectrCurrent();
    }

}