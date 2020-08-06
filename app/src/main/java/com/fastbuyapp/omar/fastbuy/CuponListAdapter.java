package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.Cupon;

import java.util.ArrayList;

public class CuponListAdapter extends BaseAdapter {
    private Context context;
    private  int layout;
    private ArrayList<Cupon> listCupones;

    public CuponListAdapter(Context context, int layout, ArrayList<Cupon> listCupones) {
        this.context = context;
        this.layout = layout;
        this.listCupones = listCupones;
    }

    @Override
    public int getCount() {
        return listCupones.size();
    }

    @Override
    public Object getItem(int position) {
        return listCupones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtCodigo;
        TextView txtStock;
        TextView txtDescripcion;
        TextView txtFecha;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CuponListAdapter.ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, parent, false);
            holder = new CuponListAdapter.ViewHolder();
            holder.txtCodigo = row.findViewById(R.id.txtCodigoCupon);
            holder.txtStock = row.findViewById(R.id.txtStockCupon);
            holder.txtDescripcion = row.findViewById(R.id.txtDescripcionCupon);
            holder.txtFecha = row.findViewById(R.id.txtFechaCaducidadCupon);
            row.setTag(holder);
        }else {
            holder = (CuponListAdapter.ViewHolder) row.getTag();
        }


        if (listCupones.get(position).getEstado()!= 1){
            holder.txtCodigo.setText(listCupones.get(position).getCodigo());
            holder.txtDescripcion.setText(listCupones.get(position).getDescripcion());
            holder.txtStock.setText(String.valueOf(listCupones.get(position).getCantidad()));
            holder.txtFecha.setText(listCupones.get(position).getFecha());
        }else{
            if (listCupones.get(position).getPromo().equals("COMPARTIDO")){
                holder.txtCodigo.setText(listCupones.get(position).getCodigo());
                holder.txtDescripcion.setText(listCupones.get(position).getPromo()+" - "+listCupones.get(position).getDescripcion());
                holder.txtStock.setText(String.valueOf(listCupones.get(position).getCantidad()));
                holder.txtFecha.setText(listCupones.get(position).getFecha());
            }
        }
        return row;
    }
}
