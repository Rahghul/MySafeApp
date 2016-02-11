package fr.mysafeauto.mysafe.Services.Coordinate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.mysafeauto.mysafe.R;


public class CustomAdapterCoordinate extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Coordinate> coordinateList = new ArrayList<>();

    public CustomAdapterCoordinate(Context context, List<Coordinate> coordinateList) {
        super();
        this.context = context;
        this.coordinateList = coordinateList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return coordinateList.size();
    }

    @Override
    public Object getItem(int position) {
        return coordinateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return coordinateList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.one_item_list_coord, null);
            holder.textViewLat = (TextView) convertView.findViewById(R.id.txt_item_lat);
            holder.textViewLon = (TextView) convertView.findViewById(R.id.txt_item_lon);
            holder.textViewSpeed = (TextView) convertView.findViewById(R.id.txt_item_speed);
            holder.textViewBatt = (TextView) convertView.findViewById(R.id.txt_item_batt);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.txt_item_date);
           // if(position == 0)
            //    convertView.setBackgroundColor(Color.parseColor("#678FBA"));
            
            holder.imageViewBatt = (ImageView) convertView.findViewById(R.id.img_item_batt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewLat.setText(coordinateList.get(position).getLatitude());
        holder.textViewLon.setText(coordinateList.get(position).getLongitude());
        holder.textViewSpeed.setText(coordinateList.get(position).getSpeed());
        holder.textViewBatt.setText(coordinateList.get(position).getBattery());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        holder.textViewDate.setText(sfd.format(coordinateList.get(position).getDateTime()));

        holder.textViewLat.setTag(String.valueOf(position));
        holder.textViewLon.setTag(String.valueOf(position));
        holder.textViewDate.setTag(String.valueOf(position));
        holder.textViewBatt.setTag(String.valueOf(position));
        holder.textViewSpeed.setTag(String.valueOf(position));
        if(Integer.parseInt(coordinateList.get(position).getBattery()) < 20){
            holder.imageViewBatt.setImageResource(R.drawable.ic_battery_alert_black_24dp);
        }
        else{
            holder.imageViewBatt.setImageResource(R.drawable.ic_battery_full_black_24dp);
        }


        return convertView;
    }

    class ViewHolder {
        TextView textViewLat;
        TextView textViewLon;
        TextView textViewDate;
        TextView textViewSpeed;
        TextView textViewBatt;
        ImageView imageViewBatt;
    }


}
