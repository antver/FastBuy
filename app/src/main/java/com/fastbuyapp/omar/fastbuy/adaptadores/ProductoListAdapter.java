package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fastbuyapp.omar.fastbuy.PersonalizaPedidoActivity;
import com.fastbuyapp.omar.fastbuy.ProductoCategoriaActivity;
import com.fastbuyapp.omar.fastbuy.ProductosActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;

import java.net.URLEncoder;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProductoListAdapter extends RecyclerView.Adapter<ProductoListAdapter.MyViewHolder>  {

    private ArrayList<Producto> listaProductos;
    private Context context;
    public ProductoListAdapter(ArrayList<Producto> listaProductos, Context context)
    {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtNombre;
        public TextView txtDescripcion;
        public TextView txtPrecio;
        public ImageView imagen, ivIconoFavorito;
        public Button btnAgregarAlCarrito;

        public MyViewHolder(View v) {
            super(v);
            txtNombre = (TextView) v.findViewById(R.id.txtNombre);
            txtDescripcion = (TextView) v.findViewById(R.id.txtDescripcion);
            txtPrecio = (TextView) v.findViewById(R.id.txtPrecio);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            ivIconoFavorito = (ImageView) v.findViewById(R.id.ivIconoFavorito);
            btnAgregarAlCarrito = (Button) v.findViewById(R.id.btnAgregarAlCarrito);

        }
    }

    @NonNull
    @Override
    public ProductoListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_dato, parent, false);
        ProductoListAdapter.MyViewHolder pvh = new ProductoListAdapter.MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoListAdapter.MyViewHolder holder, final int position) {
        holder.txtNombre.setText(listaProductos.get(position).getDescripcion());
        holder.txtDescripcion.setText(listaProductos.get(position).getDescripcion2());
        holder.txtPrecio.setText("S/ "+listaProductos.get(position).getPrecio());

        if(listaProductos.get(position).getFavorito().equals("no")){
            holder.ivIconoFavorito.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            //holder.ivIconoFavorito.setColorFilter(ContextCompat.getColor(context, R.color.fastbuy), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            holder.ivIconoFavorito.setImageResource(R.drawable.ic_favorite_black_24dp);
            //holder.ivIconoFavorito.setColorFilter(ContextCompat.getColor(context, R.color.fastbuy), android.graphics.PorterDuff.Mode.SRC_IN);

        }
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/productos/fotos/" + listaProductos.get(position).getImagen();
        Log.v("imagn",url);
        GlideApp.with(context)
                .load(url)
                .override(200, 200)
                .placeholder(R.drawable.loader_img)
                .into(holder.imagen);
        final SharedPreferences myPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor myEditor;
        myEditor = myPreferences.edit();
        final boolean tiendaCerrada = myPreferences.getBoolean("tiendaCerrada", false);
        final SharedPreferences.Editor finalMyEditor = myEditor;
        holder.btnAgregarAlCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!tiendaCerrada){
                    int codigo =(listaProductos.get(position).getCodigo());
                    Producto prod = new Producto();
                    prod.setCodigo(codigo);
                    prod.setDescripcion(listaProductos.get(position).getDescripcion());
                    prod.setDescripcion2(listaProductos.get(position).getDescripcion2());
                    prod.setPrecio(listaProductos.get(position).getPrecio());
                    prod.setImagen(listaProductos.get(position).getImagen());
                    prod.setEstado(listaProductos.get(position).getEstado());

                    Categoria categoria = new Categoria();
                    categoria.setDescripcion(listaProductos.get(position).getCategoria().getDescripcion());
                    prod.setCategoria(categoria);

                    Empresa empresa = new Empresa();
                    empresa.setCodigo(listaProductos.get(position).getEmpresa().getCodigo());
                    empresa.setNombreComercial(listaProductos.get(position).getEmpresa().getNombreComercial());
                    empresa.setLongitud(listaProductos.get(position).getEmpresa().getLongitud());
                    empresa.setLatitud(listaProductos.get(position).getEmpresa().getLatitud());
                    prod.setEmpresa(empresa);
                    prod.setTiempo(listaProductos.get(position).getTiempo());
                    Globales.getInstance().setProductoPersonalizar(prod);
                    finalMyEditor.putBoolean("producto_recarga", false);
                    finalMyEditor.commit();
                    Intent intent = new Intent(context, PersonalizaPedidoActivity.class);
                    context.startActivity(intent);
                //}else {
                // Toast toast = Toast.makeText(context, "El establecimiento seleccionado se encuentra cerrado...", Toast.LENGTH_LONG);
                // toast.show();
                //}
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }
}
