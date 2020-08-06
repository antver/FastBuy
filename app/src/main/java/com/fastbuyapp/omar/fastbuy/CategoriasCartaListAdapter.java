package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;

import java.util.ArrayList;

/**
 * Created by Luis Ysla on 18/12/2019.
 */

public class CategoriasCartaListAdapter extends ArrayAdapter<Categoria> {
    private Context context;
    private int layout;
    private ArrayList<Categoria> categoList;
    //private Typeface fuente1;
    private ConstraintLayout capa;
    private EditText campo;

    public CategoriasCartaListAdapter(Context context, int layout, ArrayList<Categoria> _categoList) {
        super(context, layout, _categoList);
        this.context = context;
        this.layout = layout;
        this.categoList = _categoList;
    }

    @Override
    public int getCount() {
        return categoList.size();
    }

    @Override
    public Categoria getItem(int pos) {
        return categoList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        TextView txtNameCategoriaCarta;
        TextView txtIdCategoriaCarta;
    }

    @Override
    public View getView(final int pos, View row, final ViewGroup viewGroup) {
        //View row = view;
        CategoriasCartaListAdapter.ViewHolder holder = null ;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new CategoriasCartaListAdapter.ViewHolder();
            holder.txtNameCategoriaCarta = (TextView) row.findViewById(R.id.txtNameCategoCarta);
            holder.txtIdCategoriaCarta = (TextView) row.findViewById(R.id.txtIdCategoCarta);

            row.setTag(holder);
        }
        else{
            holder = (CategoriasCartaListAdapter.ViewHolder) row.getTag();
        }

        final Categoria catego = getItem(pos);
        //Toast.makeText(context, String.valueOf(pos),Toast.LENGTH_SHORT).show();
        String fuente = "fonts/Riffic.ttf";
        holder.txtNameCategoriaCarta.setText(catego.getDescripcion());
        holder.txtIdCategoriaCarta.setText(String.valueOf(catego.getCodigo()));

        return  row;

    }
}
