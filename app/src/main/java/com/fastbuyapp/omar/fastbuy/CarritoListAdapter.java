package com.fastbuyapp.omar.fastbuy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Total;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OMAR on 23/08/2018.
 */

public class CarritoListAdapter extends ArrayAdapter<PedidoDetalle> {
    private Context context;
    private int layout;
    private ArrayList<PedidoDetalle> pedidoList;
    private Typeface fuente1;
    private TextView txtTotal;

    public CarritoListAdapter(Context context, int layout, ArrayList<PedidoDetalle> _productosList, TextView txtTotal) {
        super(context, layout, _productosList);
        this.context = context;
        this.layout = layout;
        this.pedidoList = _productosList;
        this.txtTotal = txtTotal;
    }

    @Override
    public int getCount() {
        return pedidoList.size();
    }

    @Override
    public PedidoDetalle getItem(int pos) {
        return pedidoList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        TextView txtCantidadProdPedido;
        TextView txtNombreProdPedido;
        TextView txtSubtotalProdPedido;
        ImageView btnQuitar;
    }

    @Override
    public View getView(final int pos, View row, ViewGroup viewGroup) {
        //View row = view;
        ViewHolder holder = null ;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,viewGroup,false);
            holder = new ViewHolder();
            holder.txtCantidadProdPedido = (TextView) row.findViewById(R.id.txtCantidadProdPedido);
            holder.txtSubtotalProdPedido = (TextView) row.findViewById(R.id.txtSubtotalProdPedido);
            holder.txtNombreProdPedido = (TextView) row.findViewById(R.id.txtNombreProdPedido);
            holder.btnQuitar = (ImageView) row.findViewById(R.id.btnQuitarProdPedido);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }
        final PedidoDetalle pedidoDetalle = getItem(pos);
        int cant = pedidoDetalle.getCantidad();
        if(cant < 10)
            holder.txtCantidadProdPedido.setText("0"+String.valueOf(cant));
        else
            holder.txtCantidadProdPedido.setText(String.valueOf(cant));

        final String nameProdOrProm;
        if(pedidoDetalle.isEsPromocion() == true){
            nameProdOrProm = pedidoDetalle.getPromocion().getDescripcion();
        }
        else{
            nameProdOrProm = pedidoDetalle.getProducto().getDescripcion();
        }
        holder.txtNombreProdPedido.setText(nameProdOrProm);
        holder.txtSubtotalProdPedido.setText("S/ " + String.format("%.2f",pedidoDetalle.getTotal()).toString().replace(",","."));

        final Calcular_Total calcula = new Calcular_Total();
        /*if(pedidoList.size()== (pos+1)){
            calcula.muestraTotal(txtTotal);
        }*/

        holder.btnQuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String mensajito = "Está a punto de quitar "+nameProdOrProm+" del carrito. ¿Desea continuar?";
                builder.setMessage(mensajito);
                builder.setTitle("Quitar Producto");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pedidoList.remove(pos);
                        double total = 0;
                        for (int ix = 0; ix < pedidoList.size(); ix++){
                            total += pedidoList.get(ix).getTotal();
                        }
                        txtTotal.setText(String.format("%.2f", total));
                        //Actualiza el Adapter
                        notifyDataSetChanged();
                        Globales globales =  new Globales();
                        globales.setList("lista_pedidos", pedidoList);
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
        /*
        if(pedidoDetalle.isEsPromocion()){
            holder.txtCantidadPedido.setText(String.valueOf(pedidoDetalle.getCantidad()));
            holder.txtNombreProducto.setText(pedidoDetalle.getPromocion().getDescripcion());
            holder.txtPrecio.setText("S/ " + String.format("%.2f",pedidoDetalle.getTotal()));
        }
        else{
            holder.txtCantidadPedido.setText(String.valueOf(pedidoDetalle.getCantidad()));
            holder.txtNombreProducto.setText(pedidoDetalle.getProducto().getDescripcion());
            holder.txtPrecio.setText("S/ " + String.format("%.2f",pedidoDetalle.getTotal()));

        }*/
        return  row;
    }
}
