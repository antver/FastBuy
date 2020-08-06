package com.fastbuyapp.omar.fastbuy;

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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaCategoria;

import java.util.ArrayList;

/**
 * Created by OMAR on 11/03/2019.
 */

public class CategoriaEmpresaListAdapter extends BaseAdapter {
    private Typeface fuente1;
    private Context context;
    private int layout;
    private ArrayList<EmpresaCategoria> rubroList;

    public CategoriaEmpresaListAdapter(Context context, int layout, ArrayList<EmpresaCategoria> rubroList) {
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
        CategoriaEmpresaListAdapter.ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new CategoriaEmpresaListAdapter.ViewHolder();
            holder.descripcion = (TextView) row.findViewById(R.id.txtNombreCategoria);
            holder.imagen = (ImageView) row.findViewById(R.id.ivimagencategoria);
            row.setTag(holder);
        }
        else{
            holder = (CategoriaEmpresaListAdapter.ViewHolder) row.getTag();
        }

        String fuente = "fonts/Riffic.ttf";
        this.fuente1 = Typeface.createFromAsset(context.getAssets(), fuente);
        EmpresaCategoria rubro = rubroList.get(i);
        holder.descripcion.setText(rubro.getDescripcion());
        holder.codigo = rubro.getCodigo();
        holder.descripcion.setTypeface(fuente1);
        String nombreImagen = rubro.getImagen();
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/empresas/categorias/imagenes/" + nombreImagen;
        GlideApp.with(context)
                .load(url)
                //.error(R.drawable.logo_fastbuy_2)
                .fitCenter()
                .transform(new CircleCrop())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // log exception
                        Log.v("img_promo_carga", "Error loading image", e);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imagen);
        return  row;
    }
}
