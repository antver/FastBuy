package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;

import java.net.URLEncoder;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by OMAR on 1/12/2019.
 */

/**
 * Update by Luis Ysla on 27/01/2020.
 */

public class EmpresaListAdapterMosaico extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Empresa> empresaList;

    public EmpresaListAdapterMosaico(Context context, int layout, ArrayList<Empresa> empresaList) {
        this.context = context;
        this.layout = layout;
        this.empresaList = empresaList;
    }

    @Override
    public int getCount() {
        return empresaList.size();
    }

    @Override
    public Object getItem(int pos) {
        return empresaList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        ImageView imageViewPortada;
        TextView txtNombreEmpresa;
        TextView txtDescripcion;
        TextView txtEstadoAbierto;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View row = view;
        EmpresaListAdapterMosaico.ViewHolder holder = new EmpresaListAdapterMosaico.ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtNombreEmpresa = (TextView) row.findViewById(R.id.txtNombreEmpresa);
            holder.imageView = (ImageView) row.findViewById(R.id.imgEmpresas2);
            holder.imageViewPortada = (ImageView) row.findViewById(R.id.imageViewPortada);
            holder.txtDescripcion = (TextView) row.findViewById(R.id.txtDescripcion);
            holder.txtEstadoAbierto = (TextView) row.findViewById(R.id.lblEstadoAbierto);
            row.setTag(holder);
        }
        else{
            holder = (EmpresaListAdapterMosaico.ViewHolder) row.getTag();
        }
        String fuente = "fonts/Riffic.ttf";
        Typeface typefaceRiffic = Typeface.createFromAsset(context.getAssets(), fuente);
        Empresa empresa = empresaList.get(i);
        holder.txtNombreEmpresa.setText(empresa.getNombreComercial());
        holder.txtNombreEmpresa.setTypeface(typefaceRiffic);
        holder.txtDescripcion.setText(empresa.getRazonSocial());

        holder.txtEstadoAbierto.setText(empresa.getEstadoAbierto());
        String estadoabierto = empresa.getEstadoAbierto();
        //Toast.makeText(context,"'"+estadoabierto+"'",Toast.LENGTH_SHORT).show();
        if(estadoabierto.equals("Abierto")){
            holder.txtEstadoAbierto.setBackgroundColor(ContextCompat.getColor(context, R.color.etiqueta));
            holder.txtEstadoAbierto.setTextColor(ContextCompat.getColor(context, R.color.fastbuy));
        }else{
            holder.txtEstadoAbierto.setBackgroundColor(ContextCompat.getColor(context, R.color.etiquetaRoja));
            holder.txtEstadoAbierto.setTextColor(ContextCompat.getColor(context, R.color.alert));
        }

        String nombreImagen = empresa.getImagen();
        String nombreImagenPortada;
        Servidor s = new Servidor();

        //urlPortada = "http://"+s.getServidor()+"/empresas/subcategorias/imagenes/" + nombreImagenPortadaSubCatego;
        if (empresa.getImagenFondo() == "null"){
            nombreImagenPortada = "default.jpg";

        }else {
            nombreImagenPortada = empresa.getImagenFondo();
        }
        String urlPortada = "https://"+s.getServidor()+"/empresas/portadas/" + URLEncoder.encode(nombreImagenPortada);

        //Log.v("portada", urlPortada);
        GlideApp.with(context)
                .load(urlPortada)
                .centerCrop()
                .override(300, 150)
                .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.TOP))
                .into(holder.imageViewPortada);

        String url = "https://"+s.getServidor()+"/empresas/logos/" + URLEncoder.encode(nombreImagen);

        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .override(100, 100)
                .placeholder(R.drawable.loader_img)
                .transform(new CircleCrop())
                .into(holder.imageView);

        return  row;
    }

}
