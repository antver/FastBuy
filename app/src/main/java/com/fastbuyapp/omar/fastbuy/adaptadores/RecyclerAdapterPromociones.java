package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by OMAR on 26/11/2019.
 */

public class RecyclerAdapterPromociones extends RecyclerView.Adapter<RecyclerAdapterPromociones.MyViewHolder> {
    private ArrayList<Promocion> promocionesList;
    private Context context;

    public RecyclerAdapterPromociones(Context context, ArrayList<Promocion> lista) {
        this.context=context;
        promocionesList = lista;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.promociones_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Servidor s = new Servidor();
        Promocion promocion = promocionesList.get(position);
        String nombreImagen = promocion.getImagen();
        String url = "https://"+s.getServidor()+"/promociones/fotos/" + nombreImagen;

        Log.v("imagen_promo",url);
        GlideApp.with(context)
                .load(url)
                .fitCenter()
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
                .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.ALL))
                .into(holder.imagen);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return promocionesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagen;
        public MyViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imgPromo);

        }
    }
}
