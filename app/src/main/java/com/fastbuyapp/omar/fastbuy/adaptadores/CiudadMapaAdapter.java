package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.fastbuyapp.omar.fastbuy.PrincipalActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;

import java.util.ArrayList;

public class CiudadMapaAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Ubicacion> listCiudadesMapa;

    public CiudadMapaAdapter(Context context, int layout, ArrayList<Ubicacion> listCiudadesMapa) {
        this.context = context;
        this.layout = layout;
        this.listCiudadesMapa = listCiudadesMapa;
    }

    @Override
    public int getCount() {
        return listCiudadesMapa.size();
    }

    @Override
    public Object getItem(int position) {
        return listCiudadesMapa.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        Button btnCiudad;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.btnCiudad = row.findViewById(R.id.btnCiudadMapa);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        holder.btnCiudad.setText("   "+listCiudadesMapa.get(position).getNombre());
        Typeface typefaceNexa = Typeface.createFromAsset(context.getAssets(), "fonts/NEXABOLD.otf");
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ciudad = myPreferences.getString("City_Cliente", "");
        holder.btnCiudad.setTypeface(typefaceNexa);
        holder.btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ciudadOrigen = listCiudadesMapa.get(position).getNombre();
                int ubicacion = listCiudadesMapa.get(position).getCodigo();
                double latitudCiudadMapa = listCiudadesMapa.get(position).getLat();
                double longitudCiudadMapa = listCiudadesMapa.get(position).getLon();
                double radioCiudadMapa = listCiudadesMapa.get(position).getRadio();
                double precioBaseCiudadMapa = listCiudadesMapa.get(position).getPreciobase();
                double precioExtraCiudadMapa = listCiudadesMapa.get(position).getPrecioextra();
                GuardarCiudad(ciudadOrigen, ubicacion, latitudCiudadMapa, longitudCiudadMapa, radioCiudadMapa, precioBaseCiudadMapa, precioExtraCiudadMapa);
                Intent intent = new Intent(context, PrincipalActivity.class);
                context.startActivity(intent);
            }
        });
        return row;
    }

    public void GuardarCiudad(String ciudad, int  ubicacion,double latitudCiudadMapa,double longitudCiudadMapa,double radioCiudadMapa, double precioBaseCiudadMapa,double precioExtraCiudadMapa){
        try {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("City_Cliente",ciudad);
            myEditor.putString("ubicacion", String.valueOf(ubicacion));
            myEditor.putString("latitudCiudadMapa", String.valueOf(latitudCiudadMapa));
            myEditor.putString("longitudCiudadMapa", String.valueOf(longitudCiudadMapa));
            myEditor.putString("radioCiudadMapa", String.valueOf(radioCiudadMapa));
            myEditor.putString("precioBaseCiudadMapa", String.valueOf(precioBaseCiudadMapa));
            myEditor.putString("precioExtraCiudadMapa", String.valueOf(precioExtraCiudadMapa));
            myEditor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
