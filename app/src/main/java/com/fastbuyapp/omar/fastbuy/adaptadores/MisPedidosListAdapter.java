package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.DetallesPedidoActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.entidades.MiPedido;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Luis Ysla Riojas on 20/01/2020.
 */

public class MisPedidosListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<MiPedido> miPedidoList;

    public MisPedidosListAdapter(Context context, int layout, ArrayList<MiPedido> miPedidoList) {
        this.context = context;
        this.layout = layout;
        this.miPedidoList = miPedidoList;
    }

    @Override
    public int getCount() {
        return miPedidoList.size();
    }

    @Override
    public Object getItem(int position) {
        return miPedidoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtNumOrdenPedido;
        TextView txtEstablecimiento1;
        TextView txtEstablecimiento2;
        TextView txtTotalPedido;
        TextView btnVerDetallePedido;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtNumOrdenPedido = (TextView) row.findViewById(R.id.txtNumOrdenMiPedido);
            holder.txtEstablecimiento1 = (TextView) row.findViewById(R.id.txtMiPedidoE1);
            holder.txtEstablecimiento2 = (TextView) row.findViewById(R.id.txtMiPedidoE2);
            holder.txtTotalPedido = (TextView) row.findViewById(R.id.txtTotalMiPedido);
            holder.btnVerDetallePedido = (TextView) row.findViewById(R.id.btnVerDetalleMiPedido);
            row.setTag(holder);
        }
        else{
            holder = (MisPedidosListAdapter.ViewHolder) row.getTag();
        }

        final MiPedido pedido = miPedidoList.get(position);
        final String correlativo = obtenCorrelativo(pedido.getCodigo());
        holder.txtNumOrdenPedido.setText("Orden Nº "+correlativo);
        holder.txtEstablecimiento1.setText(pedido.getEstablecimiento1());
        holder.txtEstablecimiento2.setText(pedido.getEstablecimiento2());
        if (pedido.getEstablecimiento2() != "" && pedido.getEstablecimiento2() != null)
            holder.txtEstablecimiento2.setVisibility(View.VISIBLE);
        else
            holder.txtEstablecimiento2.setVisibility(View.GONE);
        holder.txtTotalPedido.setText("S/ "+pedido.getTotal().toString().replace(",","."));

        switch (pedido.getEstado()){
            case 0:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorpendiente);
                break;
            case 1:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.coloratendido);
                break;
            case 2:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorcancelado);
                break;
            case 3:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorencamino);
                break;
            case 4:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorAceptado);
                break;
            case 7:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorEsperando);
                break;
            case 9:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.colorcancelado);
                break;
            case 10:
                cambiaColor(holder.txtNumOrdenPedido, holder.txtEstablecimiento1, holder.txtEstablecimiento1,R.color.coloratendido);
                break;
        }


        holder.btnVerDetallePedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast toast = Toast.makeText(context, "En desarrollo, mantén la calma... ;)", Toast.LENGTH_SHORT);
                View vistaToast = toast.getView();
                vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                toast.show();*/
                Intent intent = new Intent(context, DetallesPedidoActivity.class);
                intent.putExtra("NumPedido",correlativo);
                intent.putExtra("SubTotalPedido",pedido.getSubTotal().toString().replace(",","."));
                intent.putExtra("DeliveryPedido",pedido.getDelivery().toString().replace(",","."));
                intent.putExtra("CargoPedido",pedido.getCargo().toString().replace(",","."));
                intent.putExtra("DescuentoPedido",pedido.getDescuento().toString().replace(",","."));
                intent.putExtra("TotalPedido",pedido.getTotal().toString().replace(",","."));
                intent.putExtra("EstadoPedido",String.valueOf(pedido.getEstado()));
                intent.putExtra("FechaPedido",pedido.getFecha());
                context.startActivity(intent);
            }
        });

        return row;
    }

    public void cambiaColor(TextView x, TextView y, TextView z, int a){
        x.setTextColor(x.getContext().getResources().getColor(a));
        y.setTextColor(y.getContext().getResources().getColor(a));
        z.setTextColor(z.getContext().getResources().getColor(a));
    }

    public String obtenCorrelativo(int cod){
        if (cod < 10)
            return "00000"+String.valueOf(cod);
        else if(cod<100)
            return "0000"+String.valueOf(cod);
        else if(cod<1000)
            return "000"+String.valueOf(cod);
        else if(cod<10000)
            return "00"+String.valueOf(cod);
        else if(cod<100000)
            return "0"+String.valueOf(cod);
        else
            return String.valueOf(cod);
    }
}
