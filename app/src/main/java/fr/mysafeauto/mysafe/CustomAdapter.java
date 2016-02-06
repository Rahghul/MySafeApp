package fr.mysafeauto.mysafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import fr.mysafeauto.mysafe.Services.Vehicle.Vehicle;

/**
 * Created by soham on 3/5/15.
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<Vehicle> textitems;

    public CustomAdapter(Context context, List<Vehicle> list) {
        super();
        this.textitems = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return textitems.size();
    }

    @Override
    public Object getItem(int position) {
        return textitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return textitems.indexOf(getItem(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder.textViewBrand = (TextView) convertView.findViewById(R.id.txt_item_brand);
            holder.textViewColor = (TextView) convertView.findViewById(R.id.txt_item_color);
            holder.textViewIMEI = (TextView) convertView.findViewById(R.id.txt_item_imei);

            holder.imageDel = (ImageView) convertView.findViewById(R.id.delete);
            holder.imageEdit = (ImageView) convertView.findViewById(R.id.edit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textViewBrand.setText(textitems.get(position).getBrand().toString());
        holder.textViewColor.setText(textitems.get(position).getColor().toString());
        holder.textViewIMEI.setText(textitems.get(position).getImei().toString());

        holder.imageDel.setImageResource(R.drawable.deleteimg);
        holder.imageEdit.setImageResource(R.drawable.editimg);
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
