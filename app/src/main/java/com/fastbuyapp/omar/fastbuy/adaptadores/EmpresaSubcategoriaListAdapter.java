package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by OMAR on 11/03/2019.
 */

public class EmpresaSubcategoriaListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<EmpresaSubcategoria> rubroList;

    public EmpresaSubcategoriaListAdapter(Context context, int layout, ArrayList<EmpresaSubcategoria> rubroList) {
        this.context = context;
        this.layout = layout;
        this.rubroList = rubroList;
    }

    @Override
    public int getCount() {
        return rubroList.size();
    }

    @Override
    public Object getItem(int position) {
        return rubroList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        int codigo;
        TextView descripcion;
        ImageView imagen;
    }

    @Override
    public View getView(final int i, View row, ViewGroup viewGroup) {
        EmpresaSubcategoriaListAdapter.ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new EmpresaSubcategoriaListAdapter.ViewHolder();
            holder.descripcion = (TextView) row.findViewById(R.id.txtNombreSub);
            holder.imagen = (ImageView) row.findViewById(R.id.ivimagensub);
            row.setTag(holder);
        }
        else{
            holder = (EmpresaSubcategoriaListAdapter.ViewHolder) row.getTag();
        }

        Typeface typefaceNexa = Typeface.createFromAsset(context.getAssets(), "fonts/NEXABOLD.otf");
        EmpresaSubcategoria rubro = rubroList.get(i);
        holder.descripcion.setText(rubro.getDescripcion());
        holder.codigo = rubro.getCodigo();
        holder.descripcion.setTypeface(typefaceNexa);
        String nombreImagen = rubro.getImagen();
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/empresas/subcategorias/imagenes/" + nombreImagen;

        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .override(200, 150)
                .placeholder(R.drawable.loader_img)
                .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.TOP))
                .into(holder.imagen);
        return  row;
    }
}
