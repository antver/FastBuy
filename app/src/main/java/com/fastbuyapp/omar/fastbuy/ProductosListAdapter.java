package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;

import java.net.URLEncoder;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by OMAR on 23/08/2018.
 */

public class ProductosListAdapter extends ArrayAdapter<Producto> {
    private Context context;
    private int layout;
    private ArrayList<Producto> productosList;
    //private Typeface fuente1;
    private ConstraintLayout capa;
    private Button bt_main;
    private ImageView imagen;

    public ProductosListAdapter(Context context, int layout, ArrayList<Producto> _productosList) {
        super(context, layout, _productosList);
        this.context = context;
        this.layout = layout;
        this.productosList = _productosList;
    }

    @Override
    public int getCount() {
        return productosList.size();
    }

    @Override
    public Producto getItem(int pos) {
        return productosList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtDescripcion;
        TextView txtCategoria;
        TextView txtPrecio;
        TextView txtNombreImagen;
        TextView txtTiempo;
    }

    @Override
    public View getView(final int pos, View row, final ViewGroup viewGroup) {
        //View row = view;
        ViewHolder holder = null ;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new ViewHolder();
            holder.txtDescripcion = (TextView) row.findViewById(R.id.txtDescripcion);
            holder.txtPrecio = (TextView) row.findViewById(R.id.txtPrecio);
            holder.txtCategoria = (TextView) row.findViewById(R.id.txtCategoria);
            holder.imageView = (ImageView) row.findViewById(R.id.imgProductos);
            holder.txtTiempo = (TextView) row.findViewById(R.id.txtTimePreparacion);
            holder.txtNombreImagen = (TextView) row.findViewById(R.id.txtNombreImagen);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        final Producto producto = getItem(pos);
        String fuente = "fonts/Riffic.ttf";
        holder.txtDescripcion.setText(producto.getDescripcion());
        holder.txtCategoria.setText(producto.getCategoria().getDescripcion());
        holder.txtPrecio.setText("S/ "+ producto.getPrecio());
        String nombreImagen = producto.getImagen();
        holder.txtNombreImagen.setText(nombreImagen);
        holder.txtTiempo.setText(" "+String.valueOf(producto.getTiempo())+" min.");
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/productos/fotos/" + URLEncoder.encode(nombreImagen);
        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .override(200, 180)
                .placeholder(R.drawable.loader_img)
                .transform(new RoundedCornersTransformation(20,0))
                .into(holder.imageView);
        return  row;


    }
}
