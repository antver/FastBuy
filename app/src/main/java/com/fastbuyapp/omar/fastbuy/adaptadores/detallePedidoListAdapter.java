package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.DetallesPedidoActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.net.URLEncoder;
import java.util.ArrayList;

public class detallePedidoListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<PedidoDetalle>  _listPedidoDetalle;

    public detallePedidoListAdapter(Context context, int layout, ArrayList<PedidoDetalle> _listPedidoDetalle) {
        this.context = context;
        this.layout = layout;
        this._listPedidoDetalle = _listPedidoDetalle;
    }

    @Override
    public int getCount() {
        return _listPedidoDetalle.size();
    }

    @Override
    public Object getItem(int position) {
        return _listPedidoDetalle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtCantidadProdPedido;
        TextView txtNombreProdPedido;
        TextView txtSubtotalProdPedido;
        ImageView imgproducto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,parent,false);
            holder = new ViewHolder();
            holder.txtCantidadProdPedido = (TextView) row.findViewById(R.id.txtCantidadProdPedido);
            holder.txtNombreProdPedido = (TextView) row.findViewById(R.id.txtNombreProdPedido);
            holder.txtSubtotalProdPedido = (TextView) row.findViewById(R.id.txtSubtotalProdPedido);
            holder.imgproducto = (ImageView) row.findViewById(R.id.imgproducto);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }
        String nombreImagen = "";
        PedidoDetalle pedidoDetalle = _listPedidoDetalle.get(position);
        int cant = pedidoDetalle.getCantidad();
        if(cant < 10)
            holder.txtCantidadProdPedido.setText("0" +String.valueOf(cant) + " (" + _listPedidoDetalle.get(position).getPresentacion()+ ")");
        else
            holder.txtCantidadProdPedido.setText(String.valueOf(cant) + " (" + _listPedidoDetalle.get(position).getPresentacion()+ ")");

        if(pedidoDetalle.isEsPromocion()){
            holder.txtNombreProdPedido.setText(pedidoDetalle.getPromocion().getDescripcion());
            nombreImagen = pedidoDetalle.getPromocion().getImagen();
        }
        else{
            holder.txtNombreProdPedido.setText(pedidoDetalle.getProducto().getDescripcion());
            nombreImagen = pedidoDetalle.getProducto().getImagen();
        }
        holder.txtSubtotalProdPedido.setText("S/ " + String.format("%.2f",pedidoDetalle.getTotal()).toString().replace(",","."));
        Servidor s = new Servidor();

        String url = "https://"+s.getServidor()+"/productos/fotos/" + URLEncoder.encode(nombreImagen);
        Log.v("urlimagen", url);
        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.restaurante)
                .into(holder.imgproducto);
        return row;
    }
}
