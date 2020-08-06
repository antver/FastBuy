package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;

import java.util.ArrayList;

public class CiudadListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Ubicacion> ciudadesList;

    public CiudadListAdapter(Context context, int layout, ArrayList<Ubicacion> ciudadesList) {
        this.context = context;
        this.layout = layout;
        this.ciudadesList = ciudadesList;
    }

    @Override
    public int getCount() {
        return ciudadesList.size();
    }

    @Override
    public Ubicacion getItem(int position) {
        return ciudadesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtEtiqueta;
        TextView txtCodEtiqueta;
    }

    @Override
    public View getView(int position, View row, ViewGroup viewGroup) {
        CiudadListAdapter.ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, viewGroup, false);
            holder = new CiudadListAdapter.ViewHolder();
            holder.txtEtiqueta = (TextView) row.findViewById(R.id.txtEtiquetaCiudad);
            holder.txtCodEtiqueta = (TextView) row.findViewById(R.id.txtCodEtiquetaCiudad);
            row.setTag(holder);
        }else {
            holder = (CiudadListAdapter.ViewHolder) row.getTag();
        }

        Ubicacion ciudad = getItem(position);
        String fuente = "fonts/Riffic.ttf";
        holder.txtEtiqueta.setText(ciudad.getNombre());
        holder.txtCodEtiqueta.setText(String.valueOf(ciudad.getCodigo()));

        return row;
    }
}
