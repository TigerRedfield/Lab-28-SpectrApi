package com.example.lab_28_vasilev_403_apispectr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.example.lab_28_vasilev_403_apispectr.Model.LuminanceModel;
import com.example.lab_28_vasilev_403_apispectr.Model.RGBrange;
import com.example.lab_28_vasilev_403_apispectr.Model.SpecLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpectraView extends SurfaceView {

    private LocalDB localDB;
    private SQLiteDatabase db;


    ArrayList<SpecLine> specLines = new ArrayList<>();

     ArrayList<LuminanceModel> luminance = new ArrayList<>();

     CallApiSpectra API = ApiHelper.getApiSpectra();


    boolean have_background = false;

    float wlen_min = 380.0f;
    float wlen_max = 780.0f;


    List<Integer> del = Arrays.asList(380, 440, 490, 510, 580, 645, 780);

    List<RGBrange> rgBranges = new ArrayList<>();


    public SpectraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        localDB = new LocalDB(context);
        db = localDB.getWritableDatabase();
            setWillNotDraw(false);

    }

        public void saveSpectrCurrent(){
            if (db != null) {
                // Удалить старую запись
                db.delete("table_spectr", null, null);

                // Вставить новую запись с текущими данными карты
                db.insert("table_spectr", null, LocalDB.getContentValues(this));
            }
        }

        public void restoreSpectrCurrent (){
            if (db != null) {
                Cursor cursor = db.query(
                        "table_spectr",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    LocalDB.CursorFromLoad(this, cursor);
                }
            }
    }

        private void download_background(int steps) {
            API.getRGBrange(wlen_min, wlen_max, steps).enqueue(new Callback<List<RGBrange>>() {
                @Override
                public void onResponse(Call<List<RGBrange>> call, Response<List<RGBrange>> response) {
                    rgBranges = response.body();
                    have_background = true;
                    invalidate();
                }

                @Override
                public void onFailure(Call<List<RGBrange>> call, Throwable t) {

                }
            });
        }

    float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    float unlerp(float x, float x0, float x1) {
        return (x - x0) / (x1 - x0);
    }

    float map(float x, float x0, float x1, float a, float b) {
        float t = unlerp(x, x0, x1);
        return lerp(a, b, t);
    }

    float last_x = 0.0f;
    float new_x = 0.0f;
    float delta_x = 0.0f;
    float delta_nm = 0.0f;
    float nm_per_pixel = 0.0f;
    int img_w;
    boolean moving = false;

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last_x = event.getX();
                moving = true;
                return true;
            case MotionEvent.ACTION_UP:
                moving = false;
                have_background = false;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                new_x = event.getX();
                delta_x = new_x - last_x;
                delta_nm = wlen_max - wlen_min;
                nm_per_pixel = delta_nm / img_w;
                wlen_min -= delta_x * nm_per_pixel;
                wlen_max -= delta_x * nm_per_pixel;
                last_x = event.getX();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas){
            int w = canvas.getWidth();
            int h = canvas.getHeight();

            img_w = w;

            int luminanceNum = 1;
            int lastLum = 0;

            if(!luminance.isEmpty() && rgBranges.isEmpty()){
                for (int j = 0; j < luminance.size(); j++) {
                    if (rgBranges.get(0).wavelength.intValue() < luminance.get(j).getNanometers().intValue()) {
                        luminanceNum = j;
                        break;
                    }
                }
            }


        canvas.drawColor(Color.BLACK);
        if (!have_background) {
            download_background(w);
        } else {
            if (moving == false)
                for (int i = 0; i < rgBranges.size(); i++) {

                    canvas.drawLine(i, 0, i, h, rgBranges.get(i).getPaint());

                    Paint markPaint = new Paint();
                    markPaint.setTextSize(20);
                    markPaint.setColor(Color.WHITE);
                    if (del.contains(rgBranges.get(i).wavelength.intValue())) {
                        canvas.drawText(
                                String.valueOf(rgBranges.get(i).wavelength.intValue()),
                                i,
                                h - 20,
                                markPaint
                        );
                    }

                    Paint lumPaint = new Paint();
                    lumPaint.setColor(Color.BLACK);
                    lumPaint.setStrokeWidth(100);

                    if (luminanceNum < luminance.size()) {
                        Log.d("LUMINANCE", i + " --- " + rgBranges.get(i).wavelength.intValue() + " --- " + luminance.get(luminanceNum).getNanometers().intValue());
                        if (rgBranges.get(i).wavelength.intValue() == luminance.get(luminanceNum).getNanometers().intValue()) {
                            canvas.drawLine(lastLum, h * luminance.get(luminanceNum - 1).getLuminance(), i, h * luminance.get(luminanceNum).getLuminance(), lumPaint);

                            lastLum = i;
                            luminanceNum++;
                        }
                    }

                }

        }

        for (SpecLine line : specLines ) {
            float x = map(line.wavelength, wlen_min, wlen_max, 0, w - 1);
            canvas.drawLine(x, 0, x, h, line.getPaint());
        }
    }


    public void refreshSpectre(List<SpecLine> newSpectre) {
        specLines.clear();
        specLines.addAll(newSpectre);
        invalidate();
    }

    public void refreshGraph(List<LuminanceModel> newLuminance) {
        luminance.clear();
        luminance.addAll(newLuminance);
        invalidate();
    }



}
