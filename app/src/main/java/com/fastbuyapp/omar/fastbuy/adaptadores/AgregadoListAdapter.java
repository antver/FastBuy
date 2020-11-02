package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.Interfaces.OnItemChange;
import com.fastbuyapp.omar.fastbuy.Interfaces.OnListItemClick;
import com.fastbuyapp.omar.fastbuy.PresentacionProdListAdapter;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoAgregados;
import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;

import java.util.ArrayList;

public class AgregadoListAdapter  extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ProductoAgregados> lista;
    private OnItemChange onItemChange;

    public AgregadoListAdapter(Context context, int layout, ArrayList<ProductoAgregados> lista) {
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
        Switch swAgregado;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        AgregadoListAdapter.ViewHolder holder = new AgregadoListAdapter.ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.swAgregado = (Switch) row.findViewById(R.id.swAgregado);
            holder.swAgregado.setChecked(false);
            row.setTag(holder);
        }
        else{
            holder = (AgregadoListAdapter.ViewHolder) row.getTag();
        }
        String fuente = "fonts/Riffic.ttf";
        //this.fuente1 = Typeface.createFromAsset( context.getAssets(), fuente);
        ProductoAgregados productoAgregado = lista.get(position);
        holder.swAgregado.setText(productoAgregado.getNombre());
        holder.swAgregado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onItemChange.onChange(convertView, position, isChecked);
                notifyDataSetChanged();

            }
        });
        return  row;
    }

    public void setOnItemChange (OnItemChange context) {
        this.onItemChange = context;
    }
}
