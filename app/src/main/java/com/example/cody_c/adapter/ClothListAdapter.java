package com.example.cody_c.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cody_c.R;
import com.example.cody_c.data.ClothNode;

import java.util.ArrayList;

public class ClothListAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ClothNode> sample;

    public ClothListAdapter(Context context,ArrayList<ClothNode> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ClothNode getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_custom, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.style);
        TextView grade = (TextView)view.findViewById(R.id.grade);

        imageView.setImageResource(sample.get(position).getStyle());
        grade.setText(sample.get(position).getGrade());

        return view;
    }
}
