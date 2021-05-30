package com.example.wtoe.adaptaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtoe.OptionActivity;
import com.example.wtoe.R;
import com.example.wtoe.models.HourlyWeather;

import java.util.List;

public class RecyclerViewWeatherAdaptater extends RecyclerView.Adapter<RecyclerViewWeatherAdaptater.ViewHolder> {

    private List<HourlyWeather> mData;
    private Context mContext;

    public RecyclerViewWeatherAdaptater(Context context, List<HourlyWeather> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext).inflate(R.layout.recview_hourly_weather, parent,false);
        ViewHolder vHolder = new ViewHolder(v);


        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rs_name.setText(mData.get(position).getmHour());
        holder.textView3.setText(mData.get(position).getmTemp());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rs_name;
        private TextView textView3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rs_name = (TextView) itemView.findViewById(R.id.textView);
            textView3 = (TextView) itemView.findViewById(R.id.textView3);
        }
    }




}
