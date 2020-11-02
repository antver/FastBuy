package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fastbuyapp.omar.fastbuy.EstablecimientoActivity;
import com.fastbuyapp.omar.fastbuy.ProductoCategoriaActivity;
import com.fastbuyapp.omar.fastbuy.ProductosActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EmpresaListAdapterRV  extends RecyclerView.Adapter<EmpresaListAdapterRV.MyViewHolder>  {

    private ArrayList<Empresa> listaEmpresas;
    private Context context;
    public EmpresaListAdapterRV(ArrayList<Empresa> listaEmpresas, Context context)
    {
        this.context = context;
        this.listaEmpresas = listaEmpresas;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtNombre;
        public TextView txtDescripcion;
        public TextView txtValoracion;
        public TextView txtTiempo;
        public ImageView imagen;
        public FrameLayout layout_empresa;
        public MyViewHolder(View v) {
            super(v);
            txtNombre = (TextView) v.findViewById(R.id.txtNombre);
            txtDescripcion = (TextView) v.findViewById(R.id.txtDescripcion);
            txtValoracion = (TextView) v.findViewById(R.id.txtValoracion);
            txtTiempo = (TextView) v.findViewById(R.id.txtTiempo);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            layout_empresa = (FrameLayout) v.findViewById(R.id.layout_empresa);
        }
    }

    @NonNull
    @Override
    public EmpresaListAdapterRV.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empresa, parent, false);
        EmpresaListAdapterRV.MyViewHolder pvh = new EmpresaListAdapterRV.MyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaListAdapterRV.MyViewHolder holder, final int position) {
        holder.txtNombre.setText(listaEmpresas.get(position).getNombreComercial());
        holder.txtDescripcion.setText(listaEmpresas.get(position).getRazonSocial());
        holder.txtValoracion.setText(listaEmpresas.get(position).getValoracion());
        holder.txtTiempo.setText(listaEmpresas.get(position).getTiempo());

        String estadoabierto = listaEmpresas.get(position).getEstadoAbierto();
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/empresas/portadas/" + listaEmpresas.get(position).getImagenFondo();

        if(estadoabierto.equals("Abierto")){
            GlideApp.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.loader_img)
                    .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.LEFT))
                    .into(holder.imagen);
        }else{


            GlideApp.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.loader_img)
                    .into(holder.imagen);

            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.imagen.setColorFilter(filter);
        }

        holder.layout_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences myPreferences;
                myPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor myEditor = myPreferences.edit();
                int codigo =(listaEmpresas.get(position).getCodigo());
                myEditor.putString("codigo_empresa", String.valueOf(codigo));
                myEditor.putString("nombre_empresa", listaEmpresas.get(position).getNombreComercial());
                myEditor.putString("longitud_empresa", String.valueOf(listaEmpresas.get(position).getLongitud()));
                myEditor.putString("latitud_empresa", String.valueOf(listaEmpresas.get(position).getLatitud()));
                myEditor.putString("logo_empresa", listaEmpresas.get(position).getImagen());
                myEditor.putString("portada_empresa", listaEmpresas.get(position).getImagenFondo());
                myEditor.putString("taper_empresa", listaEmpresas.get(position).getTaper());
                myEditor.putString("costo_taper", String.valueOf(listaEmpresas.get(position).getCostoTaper()));
                myEditor.putString("valoracion_empresa", String.valueOf(listaEmpresas.get(position).getValoracion()));
                myEditor.putString("tiempo_empresa", String.valueOf(listaEmpresas.get(position).getTiempo()));
                myEditor.putString("ubicacion", String.valueOf(listaEmpresas.get(position).getUbicacion()));

                myEditor.putInt("categoria_producto", 0);
                //String nombreComercial = list.get(position).getNombreComercial();
                /*Globales.nombreEmpresaSeleccionada = nombreComercial;
                Globales.LongitudEmpresaSeleccionada = list.get(position).getLongitud();
                Globales.LatitudEmpresaSeleccionada =  list.get(position).getLatitud();
                Globales.imagenEmpresa = list.get(position).getImagen();
                Globales.imagenFondoEmpresa = list.get(position).getImagenFondo();
                Globales.taperEmpresaSel = list.get(position).getTaper();
                Globales.costoTaperEmpresaSel = list.get(position).getCostoTaper();*/

                if(listaEmpresas.get(position).getEstadoAbierto().equals("Abierto")){
                    myEditor.putBoolean("tiendaCerrada", false);
                }
                else{
                    myEditor.putBoolean("tiendaCerrada", true);
                }
                myEditor.commit();
                Intent intent = new Intent(context, ProductoCategoriaActivity.class);
                //Intent intent = new Intent(EstablecimientoActivity.this, CartaActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaEmpresas.size();
    }


}
