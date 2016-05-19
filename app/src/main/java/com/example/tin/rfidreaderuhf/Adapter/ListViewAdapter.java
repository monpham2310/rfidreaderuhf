package com.example.tin.rfidreaderuhf.Adapter;

import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIRST_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.SECOND_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.THIRD_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FOURTH_COLUMN;
import static com.example.tin.rfidreaderuhf.Adapter.Constants.FIFTH_COLUMN;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.app.Activity;
import android.widget.TextView;

import com.example.tin.rfidreaderuhf.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Mon on 19/05/2016.
 */
public class ListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtStt;
    TextView txtName;
    TextView txtType;
    TextView txtAmount;
    TextView txtPrice;

    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = activity.getLayoutInflater();
        if(view == null){
            view = inflater.inflate(R.layout.column_row, null);

            txtStt = (TextView)view.findViewById(R.id.txtStt);
            txtName = (TextView)view.findViewById(R.id.txtName);
            txtType = (TextView)view.findViewById(R.id.txtType);
            txtAmount = (TextView)view.findViewById(R.id.txtAmount);
            txtPrice = (TextView)view.findViewById(R.id.txtPrice);
        }

        HashMap<String, String> map = list.get(position);
        txtStt.setText(map.get(FIRST_COLUMN));
        txtName.setText(map.get(SECOND_COLUMN));
        txtType.setText(map.get(THIRD_COLUMN));
        txtAmount.setText(map.get(FOURTH_COLUMN));
        txtPrice.setText(map.get(FIFTH_COLUMN));

        return view;
    }
}
