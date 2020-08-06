package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by OMAR on 22/08/2018.
 */

public class EmpresaListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Empresa> empresaList;

    public EmpresaListAdapter(Context context, int layout, ArrayList<Empresa> empresaList) {
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
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtNombreEmpresa;
        TextView txtDescripcion;
        TextView txtEstadoAbierto;
        TextView btnRecomendar;
    }

    private Typeface fuente1;

    @Override
    public View getView(int i, final View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtNombreEmpresa = (TextView) row.findViewById(R.id.txtNombreEmpresa);
            holder.imageView = (ImageView) row.findViewById(R.id.imgEmpresas2);
            holder.txtDescripcion = (TextView) row.findViewById(R.id.txtDescripcion);
            holder.txtEstadoAbierto = (TextView) row.findViewById(R.id.lblEstadoAbierto);
            holder.btnRecomendar = (TextView) row.findViewById(R.id.btnRecomendarEstablecimiento);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }
        String fuente = "fonts/Riffic.ttf";
        this.fuente1 = Typeface.createFromAsset( context.getAssets(), fuente);
        final Empresa empresa = empresaList.get(i);
        holder.txtNombreEmpresa.setText(empresa.getNombreComercial());
        holder.txtNombreEmpresa.setTypeface(fuente1);
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

        holder.btnRecomendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent();
                send.setAction(Intent.ACTION_SEND);
                send.putExtra(Intent.EXTRA_TEXT, "Encuentra a "+empresa.getNombreComercial()+" en FAST BUY! Descarga esta increible App y disfruta! https://play.google.com/store/apps/details?id=com.fastbuyapp.omar.fastbuy");
                send.setType("text/plain");
                context.startActivity(send);
            }
        });
        String nombreImagen = empresa.getImagen();
        Log.v("mi imagen",nombreImagen);
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/empresas/logos/" + URLEncoder.encode(nombreImagen);

        GlideApp.with(context)
                .load(url)
                .fitCenter()
                .override(150, 150)
                .placeholder(R.drawable.loader_img)
                .transform(new CircleCrop())
                .into(holder.imageView);

        return  row;
    }
}
