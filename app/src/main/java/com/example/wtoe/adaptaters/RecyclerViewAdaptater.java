package com.example.wtoe.adaptaters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtoe.OptionActivity;
import com.example.wtoe.R;
import com.example.wtoe.models.City;

import java.util.List;

public class RecyclerViewAdaptater extends RecyclerView.Adapter<RecyclerViewAdaptater.MyViewHolder> {

    Context mContext;
    List<City> mData;
    Activity mActivity;

    public RecyclerViewAdaptater(Context context, List<City> data, Activity activity) {
        this.mContext = context;
        this.mData = data;
        this.mActivity = activity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = null;
        v = LayoutInflater.from(mContext).inflate(R.layout.items_list, parent,false);
        MyViewHolder vHolder = new MyViewHolder(v,mActivity);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.rs_name.setText(mData.get(position).getName());
        holder.lCityDeg.setText(mData.get(position).getDeg());
        holder.constItem.setBackgroundResource(mData.get(position).getDraw());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView rs_name;
        private ConstraintLayout constItem;
        private TextView lCityDeg;

        public MyViewHolder(@NonNull final View itemView, final Activity activity) {
            super(itemView);

            rs_name = (TextView) itemView.findViewById(R.id.textView);
            lCityDeg = (TextView) itemView.findViewById(R.id.textView4);
            constItem = (ConstraintLayout) itemView.findViewById(R.id.constItem);

            // If click on item, go to OptionActivity
            constItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), OptionActivity.class);
                    intent.putExtra("position",getLayoutPosition());
                    itemView.getContext().startActivity(intent);
                    //activity.finish();


                }
            });
        }
    }
}
