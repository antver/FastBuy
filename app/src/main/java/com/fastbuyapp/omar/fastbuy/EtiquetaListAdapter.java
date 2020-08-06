package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class EtiquetaListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<String> etiquetasList;

    public EtiquetaListAdapter(Context context, int layout, ArrayList<String> etiquetasList) {
        this.context = context;
        this.layout = layout;
        this.etiquetasList = etiquetasList;
    }

    @Override
    public int getCount() {
        return etiquetasList.size();
    }

    @Override
    public String getItem(int pos) {
        return etiquetasList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        TextView txtNewEtiqueta;
    }

    @Override
    public View getView(final int pos, View row, final ViewGroup viewGroup) {

        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(layout,viewGroup,false);
        holder = new EtiquetaListAdapter.ViewHolder();
        holder.txtNewEtiqueta = (TextView) row.findViewById(R.id.txtEtiqueta);
        row.setTag(holder);

        final String itemEtiqueta = getItem(pos);
        holder.txtNewEtiqueta.setText(itemEtiqueta);

        return  row;
    }
}
