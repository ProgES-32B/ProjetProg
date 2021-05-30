package com.example.wtoe.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtoe.R;
import com.example.wtoe.Utils;
import com.example.wtoe.http.ClientHTTP;
import com.example.wtoe.models.City;
import com.example.wtoe.adaptaters.RecyclerViewAdaptater;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeFragment extends Fragment {

    View v;
    RecyclerView myrecyclerview;
    List<City> lstCity;
    ClientHTTP socket;
    RecyclerViewAdaptater recyclerViewAdaptater;

    Boolean saveButtonClicked = true;
    final Handler refreshHandler2 = new Handler();
    Runnable runnable;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        socket = new ClientHTTP(getContext());
        lstCity = new ArrayList<>();

        // Button to add a city
        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySearchMenu();
            }
        });

        return v;
    }

    private void refreshData() {
        ArrayList<String> lCities = getCities();
        for(int i = 0; i < lCities.size(); i++) {
            socket.requestCityWeather(lCities.get(i).toString());
        }
        System.out.println("Debug: Refreshing Data...");
    }

    private void refreshFragment() {
        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    // Refresh fragment and data
                    refreshData();
                    getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
                    System.out.println("Debug: Refreshed fragment.");
                    refreshHandler.postDelayed(this, 120 * 1000);
                }
                catch (Exception error){
                    System.out.println("Debug: Fragment refreshed failed. Error: " + error);
                }
            }
        };
        refreshHandler.postDelayed(runnable, 120 * 1000);


    }

    private void refreshFragmentFromDialog(final Dialog dialog) {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (saveButtonClicked) {
                    try {
                        getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
                        dialog.cancel();
                        refreshHandler2.postDelayed(this, 1 * 1000);
                    } catch (Exception error) {
                        System.out.println("Debug: Fragment refreshed failed. Error: " + error);
                        refreshHandler2.removeCallbacks(this);
                    }
                }
                else {
                    refreshHandler2.removeCallbacks(this);
                }
            }
        };
        refreshHandler2.postDelayed(runnable, 5 * 1000);


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final ArrayList<String> cCities = getCities();
        for(int i = 0; i < cCities.size(); i++) {
            socket.requestCityWeather(cCities.get(i).toString());
        }

        Utils lUtils = new Utils(getContext());
        try {

            for(Integer i = 0; i < cCities.size(); i++) {
                JSONObject objWeather = lUtils.getCityInfoWeather(cCities.get(i).toString());
                Integer temp = lUtils.convertKelvinToCelcius(objWeather.getJSONObject("main").getDouble("temp"));
                String weat_stat =   objWeather.getJSONArray("weather").getJSONObject(0).getString("main");
                Integer background = 0;
                switch(weat_stat) {
                    case "Clouds":
                        background = R.drawable.clouds_light;
                        break;
                    case "Clear":
                        background = R.drawable.cielbleusansnuagesoleil2;
                        break;
                }

                Long sunset =   objWeather.getJSONObject("sys").getLong("sunset");
                Long tsLong = System.currentTimeMillis()/1000;
                if(tsLong >=  sunset) {
                    background = R.drawable.night_small;
                }

                lstCity.add(new City(cCities.get(i).toString(), temp.toString() + "°", background));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Swipe Delete Code
        recyclerViewAdaptater =  new RecyclerViewAdaptater(getContext(),lstCity, getActivity());
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                saveButtonClicked = false;
                refreshHandler2.removeCallbacks(runnable);
                refreshHandler2.postDelayed(runnable, 100000);
                return true;
            }

            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRED))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                refreshHandler2.removeCallbacks(runnable);
                refreshHandler2.postDelayed(runnable, 100000);


                // Delete file in cache
                ArrayList<String> lCit = getCities();
                File fdelete = new File(getContext().getFilesDir() + "/" + lCit.get(position).toString() + ".txt");
                fdelete.delete();

                lCit.remove(position);

                // Writing
                String lstCities = "";
                try {
                    for (int i = 0; i < lCit.size() - 1; i++) {
                        lstCities += lCit.get(i).toString() + "\n";
                    }
                    lstCities += lCit.get(lCit.size() - 1).toString();
                }
                catch (Exception e) {

                }
                FileOutputStream fos = null;
                try {
                    fos = getContext().openFileOutput("cities.txt", getContext().MODE_PRIVATE);
                    fos.write(lstCities.toString().getBytes(), 0 ,lstCities.toString().length());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();

                recyclerViewAdaptater.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        myrecyclerview = (androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.item_list);
        itemTouchHelper.attachToRecyclerView(myrecyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerViewAdaptater);


    }


    private ArrayList<String> getCities() {
        FileInputStream fis = null;
        ArrayList<String> finalString = new ArrayList<String>();
        try {
            fis = getContext().openFileInput("cities.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                finalString.add(text);
            }
            fis.close();
            return finalString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalString;
    }

    private void displaySearchMenu() {
        // Search Menu
        final ConstraintLayout wayPointSettings = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialog_search_bar, null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.myFullscreenAlertDialogStyle)
                .setTitle("")
                .setView(wayPointSettings)
                .show();

        final TextView dynamicText = (TextView) wayPointSettings.findViewById(R.id.textView2);

        SearchView searchView = (SearchView) wayPointSettings.findViewById(R.id.searchView);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setAttributes(wlp);

        // Dynamic resolution of input text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //socket.isCityValid(dynamicText.getText().toString(),dialog,HomeFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                dynamicText.setText(newText);
                return false;
            }
        });

        // Exit dialog page.
        Button button = (Button) wayPointSettings.findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
                dialog.cancel();
            }
        });

        // Saving City
        dynamicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if city is valid, if yes write the city in an array in a specified file.
                socket.isCityValid(dynamicText.getText().toString(),dialog,HomeFragment.this);
                saveButtonClicked = true;
                Toasty.info(getContext(),"Validating city...",Toasty.LENGTH_LONG).show();
                refreshFragmentFromDialog(dialog);
            }
        });


    }

}
