package fr.mysafeauto.mysafe.Services.Coordinate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import fr.mysafeauto.mysafe.R;

/**
 * Created by Rahghul on 08/02/2016.
 */
public class CustomAdapterLeft extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Coordinate> coordinateList;

    public CustomAdapterLeft(Context context, List<Coordinate> coordinateList) {
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
            convertView = layoutInflater.inflate(R.layout.one_item_list_left, null);
            holder.textViewLat = (TextView) convertView.findViewById(R.id.txt_item_lat);
            holder.textViewLon = (TextView) convertView.findViewById(R.id.txt_item_lon);
            holder.textViewSpeed = (TextView) convertView.findViewById(R.id.txt_item_speed);
            holder.textViewBatt = (TextView) convertView.findViewById(R.id.txt_item_batt);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.txt_item_date);

            holder.imageViewBatt = (ImageView) convertView.findViewById(R.id.img_item_batt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewLat.setText(coordinateList.get(position).getLatitude().toString());
        holder.textViewLon.setText(coordinateList.get(position).getLongitude().toString());
        holder.textViewSpeed.setText(coordinateList.get(position).getSpeed().toString());
        holder.textViewBatt.setText(coordinateList.get(position).getBattery().toString());
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
