package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fastbuyapp.omar.fastbuy.Interfaces.OnListItemClick;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SubcategoriaListAdapter extends RecyclerView.Adapter<SubcategoriaListAdapter.MyViewHolder> {
    int row_index = 0;
    private ArrayList<EmpresaSubcategoria> lista;
    private Context context;
    private OnListItemClick onListItemClick;

    public SubcategoriaListAdapter(ArrayList<EmpresaSubcategoria> listamarcas, Context context)
    {
        this.context = context;
        this.lista = listamarcas;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imagen;
        public LinearLayout row_layout;
        public LinearLayout row_layout1;
        public MyViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.nombresubcategoria);
            imagen = (ImageView) v.findViewById(R.id.imgsubcategoria);
            row_layout = (LinearLayout) v.findViewById(R.id.row_layout);
            row_layout1 = (LinearLayout) v.findViewById(R.id.row_layout1);
        }
    }
    @NonNull
    @Override
    public SubcategoriaListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategoria, parent, false);
        SubcategoriaListAdapter.MyViewHolder pvh = new SubcategoriaListAdapter.MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoriaListAdapter.MyViewHolder holder, final int position) {
        holder.textView.setText(lista.get(position).getDescripcion());
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/empresas/subcategorias/imagenes/" + lista.get(position).getImagen();
        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.loader_img)
                .into(holder.imagen);

        holder.row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                onListItemClick.onClick(v, position);
                notifyDataSetChanged();
            }
        });

        /*if(lista.get(position).getPosicion() == 0){
            Toast.makeText(context, String.valueOf(lista.get(position).getPosicion()), Toast.LENGTH_SHORT).show();
            holder.row_layout1.setBackgroundResource(R.drawable.shadow_selected);
        }*/
         if(row_index == position){
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.fastbuy));
        }else{
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.negro));
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setClickListener(OnListItemClick context) {
        this.onListItemClick = context;
    }

}
