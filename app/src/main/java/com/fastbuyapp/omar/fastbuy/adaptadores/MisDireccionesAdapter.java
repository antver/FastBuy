package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.DireccionesActivity;
import com.fastbuyapp.omar.fastbuy.PagoTarjetaActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.SplashActivity;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MisDireccionesAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Ubicaciones> listDirecciones;

    public MisDireccionesAdapter(Context context, int layout, ArrayList<Ubicaciones> listDirecciones) {
        this.context = context;
        this.layout = layout;
        this.listDirecciones = listDirecciones;
    }

    @Override
    public int getCount() {
        return listDirecciones.size();
    }

    @Override
    public Object getItem(int position) {
        return listDirecciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtEtiquetaDireccion;
        TextView txtDireccion;
        TextView btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.txtEtiquetaDireccion = (TextView) row.findViewById(R.id.etiquetaDireccion);
            holder.txtDireccion = (TextView) row.findViewById(R.id.txtDirec);
            holder.btnDelete = (TextView) row.findViewById(R.id.btnDeleteDireccion);
            row.setTag(holder);
        }else{
            holder = (MisDireccionesAdapter.ViewHolder) row.getTag();
        }

        Ubicaciones direccion = listDirecciones.get(position);

        if (direccion.getEtiqueta()== "" && direccion.getEtiqueta()== null)
            holder.txtEtiquetaDireccion.setText(direccion.getDireccion());
        else
            holder.txtEtiquetaDireccion.setText(direccion.getEtiqueta());

        holder.txtDireccion.setText(direccion.getDireccion());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String mensajito = "Está a punto de eliminar la dirección "+listDirecciones.get(position).getEtiqueta()+". ¿Desea continuar?";
                builder.setMessage(mensajito);
                builder.setTitle("Eliminar Dirección");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarDireccion(String.valueOf(listDirecciones.get(position).getCodigo()));
                        listDirecciones.remove(position);
                        //actualizamos el adapter
                        notifyDataSetChanged();
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return row;
    }

    public void eliminarDireccion(String direccion){
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        String tokencito = myPreferences.getString("tokencito", "");
        String URL = "https://apifbdelivery.fastbuych.com/Delivery/DeleteDireccion?auth="+ tokencito+"&direccion="+direccion;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast toast = Toast.makeText(context,"Direccion Eliminada con éxito.",Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_success);
                toast.show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ErrorRegistroDireccion",error.getMessage().toString());
                Toast toast = Toast.makeText(context,"Error al eliminar Dirección.",Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                toast.show();
            }
        });

        queue.add(stringRequest);
    }
}
