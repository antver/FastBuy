package com.fastbuyapp.omar.fastbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import java.util.ArrayList;

/**
 * Created by OMAR on 02/04/2019.
 */

public class UbicacionesListAdapter  extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Ubicaciones> lista;

    public UbicacionesListAdapter(Context context, int layout, ArrayList<Ubicaciones> lista) {
        this.context = context;
        this.layout = layout;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        Switch aSwitch;
        TextView txtDireccion;
        TextView txtCiudad;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View getView(int position, View view, ViewGroup parent){
        View row = view;
        UbicacionesListAdapter.ViewHolder holder = new UbicacionesListAdapter.ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtDireccion = (TextView) row.findViewById(R.id.txtDireccionUbi);
            holder.txtCiudad = (TextView) row.findViewById(R.id.txtCiudadUbi);
            holder.aSwitch = (Switch) row.findViewById(R.id.swtPredeterminado);
            row.setTag(holder);
        }
        else{
            holder = (UbicacionesListAdapter.ViewHolder) row.getTag();
        }
        Ubicaciones ubicaciones = lista.get(position);
        holder.txtDireccion.setText(ubicaciones.getDireccion());
        holder.txtCiudad.setText(ubicaciones.getCiudad());
        boolean predeterminado = ubicaciones.isCheck();
        if(predeterminado){
            holder.aSwitch.setChecked(true);
        }
        else{
            holder.aSwitch.setChecked(false);
        }
        return  row;
    }
}
