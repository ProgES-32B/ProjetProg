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

public class RecyclerViewSearch extends RecyclerView.Adapter<RecyclerViewSearch.ViewHolder> {

    private String mData;
    private Context mContext;

    public RecyclerViewSearch(Context context, String data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(mContext).inflate(R.layout.items_search, parent,false);
        ViewHolder vHolder = new ViewHolder(v);


        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rs_name.setText(mData);
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rs_name;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            rs_name = (TextView) itemView.findViewById(R.id.textView19);

            rs_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(itemView.getContext(), OptionActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }




}
