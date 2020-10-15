package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.fastbuyapp.omar.fastbuy.entidades.MensajeChat;

import java.util.ArrayList;

public class MensajesChatAdapter extends BaseAdapter {
    private Context context;
    private int layout1; //este es para el usuario
    private int layout2; //este es para el repartidor
    private ArrayList<MensajeChat> mensajesList;
    private int idRepartidor;

    public MensajesChatAdapter(Context context, int layout1, int layout2, ArrayList<MensajeChat> mensajesList, int idRepartidor) {
        this.context = context;
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.mensajesList = mensajesList;
        this.idRepartidor = idRepartidor;
    }

    @Override
    public int getCount() {
        return mensajesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mensajesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView fecha;
        TextView mensaje;
        TextView hora;
        ImageView foto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if(mensajesList.get(position).getOrigen().equals("REPARTIDOR")){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout2, null);
        }else if(mensajesList.get(position).getOrigen().equals("USUARIO")){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout1, null);
        }
        holder.fecha = row.findViewById(R.id.txtFechaChat);
        holder.hora = row.findViewById(R.id.txtHoraChat);
        holder.mensaje = row.findViewById(R.id.txtMensajeChat);
        holder.foto = row.findViewById(R.id.imgChat);
        row.setTag(holder);


        MensajeChat mensajito = mensajesList.get(position);
        holder.fecha.setText(mensajito.getFecha());
        if (position == 0){
            holder.fecha.setVisibility(View.VISIBLE);
        }else {
            if (!mensajito.getFecha().equals(mensajesList.get(position-1).getFecha())){
                holder.fecha.setVisibility(View.VISIBLE);
            }else {
                holder.fecha.setVisibility(View.GONE);
            }
        }

        holder.mensaje.setText(mensajito.getMensaje());
        holder.hora.setText(mensajito.getHora().replace("p. m.", "PM").replace("a. m.", "AM").toUpperCase());

        String photoUser;
        if (mensajesList.get(position).getOrigen().equals("USUARIO")){
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            photoUser = myPreferences.getString("Photo_Cliente", "R.drawable.ic_usuario");
        }else{
            Servidor s = new Servidor();
            photoUser = "https://"+s.getServidor()+"/imagenes/agentesreparto/r" + idRepartidor+".jpg";

        }

        GlideApp.with(context)
                .load(photoUser)
                .error(R.drawable.ic_usuario)
                .fitCenter()
                .transform(new CircleCrop())
                .into(holder.foto);


        if (position< mensajesList.size()-1){
            if (mensajesList.get(position).getOrigen().equals(mensajesList.get(position+1).getOrigen())){
                holder.foto.setVisibility(View.INVISIBLE);
            }else {
                holder.foto.setVisibility(View.VISIBLE);
            }
        }else{
            holder.foto.setVisibility(View.VISIBLE);
        }


        return row;
    }
}
