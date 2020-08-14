package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import java.util.ArrayList;

public class DireccionListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Ubicaciones> direccionesList;

    public DireccionListAdapter(Context context, int layout, ArrayList<Ubicaciones> _direccionesList) {
        //super(context, layout, _direccionesList);
        this.context = context;
        this.layout = layout;
        this.direccionesList = _direccionesList;
    }

    @Override
    public int getCount() {
        return direccionesList.size();
    }

    @Override
    public Ubicaciones getItem(int pos) {
        return direccionesList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        TextView txtEtiqueta;
    }

    @Override
    public View getView(final int pos, View row, final ViewGroup viewGroup) {
        DireccionListAdapter.ViewHolder holder = null ;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new DireccionListAdapter.ViewHolder();
            holder.txtEtiqueta = (TextView) row.findViewById(R.id.txtEtiqueta);
            row.setTag(holder);
        }
        else{
            holder = (DireccionListAdapter.ViewHolder) row.getTag();
        }

        final Ubicaciones ubicacion = getItem(pos);
        String fuente = "fonts/Riffic.ttf";
        if (ubicacion.getEtiqueta() != "" && ubicacion.getEtiqueta() != null)
            holder.txtEtiqueta.setText(ubicacion.getEtiqueta() + "(" + ubicacion.getDireccion() + ")");
        else
            holder.txtEtiqueta.setText(ubicacion.getDireccion());

        return  row;
    }
}
