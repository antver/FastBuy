package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.ProductoPresentacion;

import java.util.ArrayList;

public class PresentacionProdListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ProductoPresentacion> presentaProdList;

    private Typeface fuente1;

    public PresentacionProdListAdapter(Context context, int layout, ArrayList<ProductoPresentacion> presentaProdList) {
        this.context = context;
        this.layout = layout;
        this.presentaProdList = presentaProdList;
    }

    @Override
    public int getCount() {
        return presentaProdList.size();
    }

    @Override
    public Object getItem(int pos) {
        return presentaProdList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        TextView chbxPresentacionProd;
    }

    @Override
    public View getView(int i, final View view, ViewGroup viewGroup) {
        View row = view;
        PresentacionProdListAdapter.ViewHolder holder = new PresentacionProdListAdapter.ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.chbxPresentacionProd = (TextView) row.findViewById(R.id.chbxPresentacion);
            row.setTag(holder);
        }
        else{
            holder = (PresentacionProdListAdapter.ViewHolder) row.getTag();
        }
        String fuente = "fonts/Riffic.ttf";
        this.fuente1 = Typeface.createFromAsset( context.getAssets(), fuente);
        ProductoPresentacion prodPresentacion = presentaProdList.get(i);

        int estado = prodPresentacion.getEstado();
        if(estado == 0){
            holder.chbxPresentacionProd.setText(" "+prodPresentacion.getDescripcion());
            //holder.chbxPresentacionProd.setTypeface(fuente1);
            if (prodPresentacion.isCheck())
                holder.chbxPresentacionProd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.checkbox_on,0,0,0);
            else
                holder.chbxPresentacionProd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.checkbox_off,0,0,0);
        }

        return  row;
    }
}
