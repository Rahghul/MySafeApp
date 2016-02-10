package fr.mysafeauto.mysafe.Services.Vehicle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.mysafeauto.mysafe.R;

/**
 * Created by soham on 3/5/15.
 */
public class CustomAdapterRight extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<Vehicle> vehicleList;

    public CustomAdapterRight(Context context, List<Vehicle> vehicleList) {
        super();
        this.vehicleList = vehicleList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return vehicleList.size();
    }

    @Override
    public Object getItem(int position) {
        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return vehicleList.indexOf(getItem(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.one_item_list_vehicle, null);
            holder.textViewBrand = (TextView) convertView.findViewById(R.id.txt_item_brand);
            holder.textViewColor = (TextView) convertView.findViewById(R.id.txt_item_color);
            holder.textViewIMEI = (TextView) convertView.findViewById(R.id.txt_item_imei);
            if(position == 0)
                convertView.setBackgroundColor(Color.parseColor("#678FBA"));

            holder.imageDel = (ImageView) convertView.findViewById(R.id.delete);
            holder.imageEdit = (ImageView) convertView.findViewById(R.id.edit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textViewBrand.setText(vehicleList.get(position).getBrand().toString());
        holder.textViewColor.setText(vehicleList.get(position).getColor().toString());
        holder.textViewIMEI.setText(vehicleList.get(position).getImei().toString());

        holder.imageDel.setImageResource(R.drawable.ic_delete_black);
        holder.imageEdit.setImageResource(R.drawable.ic_build_black);
        holder.textViewBrand.setTag(String.valueOf(position));
        holder.textViewColor.setTag(String.valueOf(position));
        holder.textViewIMEI.setTag(String.valueOf(position));
        return convertView;
    }

    class ViewHolder {
        TextView textViewBrand;
        TextView textViewColor;
        TextView textViewIMEI;
        ImageView imageDel;
        ImageView imageEdit;
    }
}
