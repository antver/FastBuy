package com.fastbuyapp.omar.fastbuy.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fastbuyapp.omar.fastbuy.DetallesPedidoActivity;
import com.fastbuyapp.omar.fastbuy.R;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.entidades.MiPedido;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoHist;

import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Luis Ysla Riojas on 20/01/2020.
 */

public class MisPedidosListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<PedidoHist> miPedidoList;

    public MisPedidosListAdapter(Context context, int layout, ArrayList<PedidoHist> miPedidoList) {
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
        ImageView imgLogo;
        TextView txFecha;
        TextView txtEstado;
    }

    String formatearfecha(String fecha){
        String dia = fecha.split("-")[2];
        String mes = fecha.split("-")[1];
        String year = fecha.split("-")[0];
        return dia + "/" + mes + "/" + year;
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
            holder.txtEstado = (TextView) row.findViewById(R.id.txtEstado);
            holder.txtTotalPedido = (TextView) row.findViewById(R.id.txtTotalMiPedido);
            //holder.btnVerDetallePedido = (TextView) row.findViewById(R.id.btnVerDetalleMiPedido);
            holder.txFecha = (TextView) row.findViewById(R.id.txFecha);
            holder.imgLogo = (ImageView) row.findViewById(R.id.imglogo);
            row.setTag(holder);
        }
        else{
            holder = (MisPedidosListAdapter.ViewHolder) row.getTag();
        }

        final PedidoHist pedido = miPedidoList.get(position);
        final String correlativo = obtenCorrelativo(pedido.getCodigo());
        holder.txtNumOrdenPedido.setText("Orden Nº "+correlativo);
        holder.txtEstablecimiento1.setText(pedido.getEmpresa().getNombreComercial());
        String fecha = pedido.getFecha();
        holder.txFecha.setText(formatearfecha(fecha));
        holder.txtEstado.setText(pedido.getEstado());
        /*if (pedido.getEstablecimiento2() != "" && pedido.getEstablecimiento2() != null)
            holder.txtEstablecimiento2.setVisibility(View.VISIBLE);
        else
            holder.txtEstablecimiento2.setVisibility(View.GONE);*/
        holder.txtTotalPedido.setText("S/ "+pedido.getTotal().toString().replace(",","."));
        String url = "https://fastbuych.com/empresas/logos/" + URLEncoder.encode(pedido.getEmpresa().getImagen());

        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.restaurante)
                .into(holder.imgLogo);
        /*switch (pedido.getEstado()){
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
        }*/


        /*holder.btnVerDetallePedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });*/

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
