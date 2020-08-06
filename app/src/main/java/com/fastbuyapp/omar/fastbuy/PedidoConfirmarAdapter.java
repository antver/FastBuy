package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;

public class PedidoConfirmarAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<PedidoDetalle> listDetalleConfirmar;

    public PedidoConfirmarAdapter(Context context, int layout, ArrayList<PedidoDetalle> listDetalleConfirmar) {
        this.context = context;
        this.layout = layout;
        this.listDetalleConfirmar = listDetalleConfirmar;
    }

    @Override
    public int getCount() {
        return listDetalleConfirmar.size();
    }

    @Override
    public Object getItem(int position) {
        return listDetalleConfirmar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView cantidad;
        TextView subTotal;
        TextView nombre;
        ImageView estado;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,parent,false);
            holder = new ViewHolder();
            holder.cantidad = row.findViewById(R.id.txtCantidadProdPedidoConfirmar);
            holder.nombre = row.findViewById(R.id.txtNombreProdPedidoConfirmar);
            holder.estado = row.findViewById(R.id.txtEstadoProdPedidoConfirmar);
            holder.subTotal = row.findViewById(R.id.txtSubtotalProdPedidoConfirmar);
            row.setTag(holder);
        }else {
            holder = (ViewHolder) row.getTag();
        }

        final PedidoDetalle pedDetalle = listDetalleConfirmar.get(position);
        int cant = pedDetalle.getCantidad();
        if(cant < 10)
            holder.cantidad.setText("0"+String.valueOf(cant));
        else
            holder.cantidad.setText(String.valueOf(cant));

        final String nameProdOrProm;
        if(pedDetalle.isEsPromocion() == true){
            nameProdOrProm = pedDetalle.getPromocion().getDescripcion();
            Log.v("codigo de promoción: ",String.valueOf(pedDetalle.getPromocion().getCodigo()));
        }
        else{
            nameProdOrProm = pedDetalle.getProducto().getDescripcion();
            Log.v("codigo del producto: ",String.valueOf(pedDetalle.getProducto().getCodigo()));
        }
        holder.nombre.setText(nameProdOrProm);
        holder.subTotal.setText("S/ " + String.format("%.2f",pedDetalle.getTotal()).toString().replace(",","."));

        int state = pedDetalle.getEstado();

        if (state == 2){
            holder.estado.setBackgroundResource(R.drawable.ic_check_cancelado);
        }else{
            holder.estado.setBackgroundResource(R.drawable.ic_check_existe);
        }
        return row;
    }
}
