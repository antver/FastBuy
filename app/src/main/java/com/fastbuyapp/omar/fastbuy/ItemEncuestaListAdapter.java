package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.Encuesta;
import com.fastbuyapp.omar.fastbuy.entidades.ItemEncuesta;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;

import java.util.ArrayList;

public class ItemEncuestaListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Encuesta> itemEncuestaList;

    public ItemEncuestaListAdapter(Context context, int layout, ArrayList<Encuesta> itemEncuestaList) {
        this.context = context;
        this.layout = layout;
        this.itemEncuestaList = itemEncuestaList;
    }

    @Override
    public int getCount() {
        return itemEncuestaList.size();
    }

    @Override
    public Encuesta getItem(int position) {
        return itemEncuestaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView chbxItemEncuesta;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.chbxItemEncuesta = (TextView) row.findViewById(R.id.chbxPresentacion);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        Encuesta encues = itemEncuestaList.get(position);
        ItemEncuesta ite = encues.getListItems();

        holder.chbxItemEncuesta.setText(" "+ite.getRespuesta());


        if (ite.isCheck())
            holder.chbxItemEncuesta.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.checkbox_on,0,0,0);
        else
            holder.chbxItemEncuesta.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.checkbox_off,0,0,0);

        return  row;
    }
}
