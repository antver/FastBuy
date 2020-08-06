package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastbuyapp.omar.fastbuy.entidades.MiExtra;

import java.util.ArrayList;

public class MiExtraListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<MiExtra> miPedidoList;

    public MiExtraListAdapter(Context context, int layout, ArrayList<MiExtra> miPedidoList) {
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
        TextView txtTotalPedido;
        TextView btnVerDetallePedido;
        TextView btnOcultarDetallePedido;
        TextView txtDetallePedido;
        TextView txtInicio;
        TextView txtFin;
        TextView txtFecha;
        LinearLayout linearDetalleExtra;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MiExtraListAdapter.ViewHolder holder = new MiExtraListAdapter.ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtNumOrdenPedido = (TextView) row.findViewById(R.id.txtNumOrdenMiPedidoExtra);
            holder.txtTotalPedido = (TextView) row.findViewById(R.id.txtTotalMiPedidoExtra);
            holder.txtFecha = (TextView) row.findViewById(R.id.txtFechaExtra);
            holder.txtInicio = (TextView) row.findViewById(R.id.txtLugarInicio);
            holder.txtFin = (TextView) row.findViewById(R.id.txtLugarFin);
            holder.txtDetallePedido = (TextView) row.findViewById(R.id.txtDetalleMiPedidoExtra);
            holder.btnVerDetallePedido = (TextView) row.findViewById(R.id.btnVerDetalleMiPedidoExtra);
            holder.btnOcultarDetallePedido = (TextView) row.findViewById(R.id.btnOcultarDetalleMiPedidoExtra);
            holder.linearDetalleExtra = row.findViewById(R.id.linearDetalleExtra);
            row.setTag(holder);
        }
        else{
            holder = (MiExtraListAdapter.ViewHolder) row.getTag();
        }

        final MiExtra pedido = miPedidoList.get(position);
        final String correlativo = obtenCorrelativo(pedido.getCodigo());
        holder.txtNumOrdenPedido.setText("Orden Nº "+correlativo);
        holder.txtDetallePedido.setText(pedido.getDetalle());
        holder.txtFecha.setText(pedido.getFecha());
        holder.txtInicio.setText(pedido.getInicio());
        holder.txtFin.setText(pedido.getFin());
        holder.txtTotalPedido.setText("S/ "+pedido.getTotal().replace(",","."));

        switch (pedido.getEstado()){
            case 0:
                cambiaColor(holder.txtNumOrdenPedido, R.color.colorpendiente);
                break;
            case 1:
                cambiaColor(holder.txtNumOrdenPedido, R.color.coloratendido);
                break;
            case 2:
                cambiaColor(holder.txtNumOrdenPedido, R.color.colorcancelado);
                break;
            case 3:
                cambiaColor(holder.txtNumOrdenPedido, R.color.colorencamino);
                break;
            case 4:
                cambiaColor(holder.txtNumOrdenPedido, R.color.colorAceptado);
                break;
        }


        final ViewHolder finalHolder = holder;
        holder.btnVerDetallePedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.btnVerDetallePedido.setVisibility(View.GONE);
                finalHolder.btnOcultarDetallePedido.setVisibility(View.VISIBLE);
                finalHolder.linearDetalleExtra.setVisibility(View.VISIBLE);
            }
        });

        holder.btnOcultarDetallePedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.btnVerDetallePedido.setVisibility(View.VISIBLE);
                finalHolder.btnOcultarDetallePedido.setVisibility(View.GONE);
                finalHolder.linearDetalleExtra.setVisibility(View.GONE);
            }
        });

        return row;
    }

    public void cambiaColor(TextView x, int a){
        x.setTextColor(x.getContext().getResources().getColor(a));
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
