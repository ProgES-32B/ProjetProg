package com.example.wtoe;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.wtoe.adaptaters.SliderAdaptater;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OptionActivity extends AppCompatActivity {

    private ViewPager mSlideViewadaptater;
    private LinearLayout mDotLayout;
    private SliderAdaptater sliderAdaptater;

    private TextView[] mDots;
    private ArrayList<String> lCities = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make status and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        Thread thread = new Thread() {
            @Override
            public void run() {
                    FileInputStream fis = null;
                    try {
                        fis = OptionActivity.this.openFileInput("cities.txt");
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String text;
                        while ((text = br.readLine()) != null) {
                            lCities.add(text);
                        }
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };

        thread.start();

        setContentView(R.layout.activity_slide);

        mSlideViewadaptater = (ViewPager) findViewById(R.id.viewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.linLayout);
        sliderAdaptater = new SliderAdaptater(this);
        mSlideViewadaptater.setAdapter(sliderAdaptater);

        // Get current position of item from last activity, then set the dot and item position.
        int pos = getIntent().getExtras().getInt("position");
        mSlideViewadaptater.setCurrentItem(pos);
        addDotsIndicator(pos);

        mSlideViewadaptater.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[lCities.size()];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
            //System.out.println(position);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}