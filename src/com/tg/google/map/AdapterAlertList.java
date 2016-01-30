package com.tg.google.map;

import java.util.Date;
import java.util.List;

import com.tg.google.map.common.ModelTripInfo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterAlertList extends ArrayAdapter<ModelTripInfo>{

    Context context; 
    int layoutResourceId;    
    List<ModelTripInfo> data = null;
    
    public AdapterAlertList(Context context, int layoutResourceId,List<ModelTripInfo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TripHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new TripHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.textViewTitle);
            holder.txtDesc =  (TextView)row.findViewById(R.id.textViewDescription);
            row.setTag(holder);
        }
        else
        {
            holder = (TripHolder)row.getTag();
        }
        
        ModelTripInfo item = data.get(position);
        Date date = new Date(item.getDate());
        holder.txtTitle.setText(date.toLocaleString());
        
        holder.txtDesc.setText(item.getMessage());
        //holder.imgIcon.setImageResource(weather.icon);
        
        return row;
    }
    
    static class TripHolder
    {
    	TextView txtDesc;
        TextView txtTitle;
    }
}