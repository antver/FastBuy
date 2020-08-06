package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;

import java.util.ArrayList;

/**
 * Created by OMAR on 15/09/2018.
 */

public class CategoriaListAdapter extends BaseAdapter{
    private Typeface fuente1;
    private Context context;
    private int layout;
    private ArrayList<Categoria> categoriaList;
    private Empresa empresa;

    public CategoriaListAdapter(Context context, int layout, ArrayList<Categoria> categoriaList) {
        this.context = context;
        this.layout = layout;
        this.categoriaList = categoriaList;
    }

    @Override
    public int getCount() {
        return categoriaList.size();
    }

    @Override
    public Object getItem(int pos) {
        return categoriaList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        int codigo;
        String descripcion;
        Button btnCategoria;
    }

    @Override
    public View getView(final int i, View row, ViewGroup viewGroup) {
        //View row = view;
        ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new CategoriaListAdapter.ViewHolder();
            holder.btnCategoria = (Button) row.findViewById(R.id.btnCategoria);
            final ViewHolder finalHolder = holder;
            holder.btnCategoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        String fuente = "fonts/Riffic.ttf";
        this.fuente1 = Typeface.createFromAsset(context.getAssets(), fuente);
        Categoria categoria = categoriaList.get(i);
        holder.btnCategoria.setText(categoria.getDescripcion());
        holder.descripcion = categoria.getDescripcion();
        holder.codigo = categoria.getCodigo();
        holder.btnCategoria.setTypeface(fuente1);
        return  row;
    }
}
