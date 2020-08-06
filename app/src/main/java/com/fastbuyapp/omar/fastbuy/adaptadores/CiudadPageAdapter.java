package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.fastbuyapp.omar.fastbuy.PrincipalActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.SplashActivity;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;

import java.net.URLEncoder;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CiudadPageAdapter extends PagerAdapter {
    Context context;
    private ArrayList<Ubicacion> listCiudadesMapa;
    int layout;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String ciudad;
    public CiudadPageAdapter(Context context, int layout, ArrayList<Ubicacion> listCiudadesMapa) {
        this.context = context;
        this.listCiudadesMapa = listCiudadesMapa;
        this.layout=layout;
    }
    private class ViewHolder{
        LinearLayout layoutItem;
        TextView nombreciudad;
        ImageView imagenciudad;
        ImageButton btnCiudad;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ViewHolder holder = new ViewHolder();
        View row = LayoutInflater.from(context).inflate(layout, null);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.layoutItem = (LinearLayout) row.findViewById(R.id.layoutItem);
            holder.nombreciudad = (TextView) row.findViewById(R.id.nombreciudad);
            holder.imagenciudad = (ImageView)  row.findViewById(R.id.imagenciudad);
            holder.btnCiudad = (ImageButton) row.findViewById(R.id.btnCiudadMapa);

        container.addView(row);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        myEditor = myPreferences.edit();
        holder.nombreciudad.setText(listCiudadesMapa.get(position).getNombre());
        //holder.btnCiudad.setTypeface(Globales.typefaceNexa);
        String nombreImagen = listCiudadesMapa.get(position).getImagen();
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnCiudad.callOnClick();
            }
        });
        holder.btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putString("City_Cliente", listCiudadesMapa.get(position).getNombre());
                myEditor.putString("ubicacion", String.valueOf(listCiudadesMapa.get(position).getCodigo()));
                myEditor.putString("latitudCiudadMapa", String.valueOf(listCiudadesMapa.get(position).getLat()));
                myEditor.putString("longitudCiudadMapa", String.valueOf(listCiudadesMapa.get(position).getLon()));
                myEditor.putString("radioCiudadMapa", String.valueOf(listCiudadesMapa.get(position).getRadio()));
                myEditor.putString("precioBaseCiudadMapa", String.valueOf(listCiudadesMapa.get(position).getPreciobase()));
                myEditor.putString("precioExtraCiudadMapa", String.valueOf(listCiudadesMapa.get(position).getPrecioextra()));
                myEditor.putString("distanciabase", String.valueOf(listCiudadesMapa.get(position).getDistabiabase()));
                myEditor.commit();
                Intent intent = new Intent(context, PrincipalActivity.class);
               // intent.putExtra("origen","Mapa");
                context.startActivity(intent);
            }
        });
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/imagenes/ubicaciones/" + URLEncoder.encode(nombreImagen);

        GlideApp.with(context)
                .load(url)
                .fitCenter()
                .override(200, 300)
                .placeholder(R.drawable.loader_img)
                .transform(new RoundedCornersTransformation(14,0, RoundedCornersTransformation.CornerType.TOP))
                .into(holder.imagenciudad);



        return row;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listCiudadesMapa.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


}
