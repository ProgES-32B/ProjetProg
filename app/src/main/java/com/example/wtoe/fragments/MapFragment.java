package com.example.wtoe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtoe.R;
import com.example.wtoe.models.City;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    RecyclerView myrecyclerview;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        List<City> lstCity;
        lstCity = new ArrayList<>();

        myrecyclerview = (androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.recView);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //myrecyclerview.setAdapter(new RecyclerViewAdaptater(getContext(),lstCity),getActivity());
    }
}