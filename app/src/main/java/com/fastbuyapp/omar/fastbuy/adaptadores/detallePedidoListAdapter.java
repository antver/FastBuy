package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

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
        ImageView btnQuitar; //este de acá lo ocultaré porque acá no es necesario
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
            holder.btnQuitar = (ImageView) row.findViewById(R.id.btnQuitarProdPedido);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        PedidoDetalle pedidoDetalle = _listPedidoDetalle.get(position);
        int cant = pedidoDetalle.getCantidad();
        if(cant < 10)
            holder.txtCantidadProdPedido.setText("0"+String.valueOf(cant));
        else
            holder.txtCantidadProdPedido.setText(String.valueOf(cant));

        if(pedidoDetalle.isEsPromocion()){
            holder.txtNombreProdPedido.setText(pedidoDetalle.getPromocion().getDescripcion());
            Log.v("codigo de promoción: ",String.valueOf(pedidoDetalle.getPromocion().getCodigo()));
        }
        else{
            holder.txtNombreProdPedido.setText(pedidoDetalle.getProducto().getDescripcion());
            Log.v("codigo del producto: ",String.valueOf(pedidoDetalle.getProducto().getCodigo()));
        }
        holder.txtSubtotalProdPedido.setText("S/ " + String.format("%.2f",pedidoDetalle.getTotal()).toString().replace(",","."));

        holder.btnQuitar.setVisibility(View.GONE);
        return row;
    }
}
