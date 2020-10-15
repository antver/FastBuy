package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.adaptadores.detallePedidoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class DetallesPedidoActivity extends AppCompatActivity {

    String codigoPedido, SubTotal, Delivery, Cargo, Descuento, Total, estado, fecha;
    ProgressDialog progDailog = null;
    ArrayList<PedidoDetalle> list;
    GridView gridView;
    detallePedidoListAdapter adapter = null;
    String tokencito;
    String fotoRepartidor = "";
    String logoEmpresa = "";
    int estadoPedidoReal = -1;
    int codigoEmpresa = 0;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);

        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        tokencito = myPreferences.getString("tokencito", "");
        codigoPedido = getIntent().getStringExtra("pedido");
        //listaDetallePedido(codigoPedido);
        //cargar_datos(tokencito, codigoPedido);
        //recibiendo codigo de Pedido

        /*SubTotal = getIntent().getStringExtra("SubTotalPedido");
        Delivery = getIntent().getStringExtra("DeliveryPedido");
        Cargo = getIntent().getStringExtra("CargoPedido");
        Descuento = getIntent().getStringExtra("DescuentoPedido");
        Total = getIntent().getStringExtra("TotalPedido");
        estado = getIntent().getStringExtra("EstadoPedido");
        fecha = getIntent().getStringExtra("FechaPedido");*/

        //Inicializamos Componentes
        final TextView txtNroPedido = (TextView) findViewById(R.id.txtNroPedido);
        final TextView txtEstablecimiento = (TextView) findViewById(R.id.txtEstablecimiento);
        final TextView txtTotal = (TextView) findViewById(R.id.txtTotal);
        final TextView txtEstado = (TextView) findViewById(R.id.txtEstado);

        final TextView txtNombreRepartidor = (TextView) findViewById(R.id.txtNombreRepartidor);
        final ImageView imgLogo = (ImageView) findViewById(R.id.imgLogo);
        final ImageView imgRepartidor = (ImageView) findViewById(R.id.imgRepartidor);

        final TextView txtFecha = (TextView) findViewById(R.id.txtFechaPedido);
        final TextView txtSubTotalPedido = (TextView) findViewById(R.id.txtSubTotalPedidoConfirmar);
        final TextView txtDeliveryPedido = (TextView) findViewById(R.id.txtDeliveryPedido);
        final TextView txtCargoPedido = (TextView) findViewById(R.id.txtCargoPedido);
        final TextView txtDescuentoPedido = (TextView) findViewById(R.id.txtDescuentoPedido);
        final TextView txtTotalPedido = (TextView) findViewById(R.id.txtTotalPedido);
        final TextView txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        final TextView mensajerepartidor = (TextView) findViewById(R.id.mensajerepartidor);
        final LinearLayout layoutRepartidor = (LinearLayout) findViewById(R.id.layoutRepartidor);
        gridView = (GridView) findViewById(R.id.dtgvItemsPedido);

        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDetallePedido_XCodigoNuevo?auth="+ tokencito+"&codigo="+codigoPedido;
        progDailog = new ProgressDialog(DetallesPedidoActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(DetallesPedidoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            txtNroPedido.setText("Orden Nº " + objeto.getString("PED_Codigo"));
                            txtFecha.setText(objeto.getString("PED_FechaPedido"));

                            fotoRepartidor = objeto.getString("PR_Foto");
                            logoEmpresa = objeto.getString("Logo");
                            if(objeto.getString("PR_Nombres") != null){
                                txtNombreRepartidor.setText(objeto.getString("PR_Nombres") + " " + objeto.getString("PR_Apellidos"));
                                if(objeto.getString("PED_Atendido").equals("1")){
                                    mensajerepartidor.setText("Entregó tu pedido.");
                                }
                                else {
                                    mensajerepartidor.setText("Tu driver asignado.");
                                }
                                if(objeto.getString("PED_Atendido").equals("2")){
                                    txtNombreRepartidor.setText("No tiene un repartidor asignado.");
                                    mensajerepartidor.setText("");
                                }
                            }
                            else{
                                txtNombreRepartidor.setText("No tiene un repartidor asignado.");
                                mensajerepartidor.setText("");
                            }

                            /*if(objeto.getString("PED_Atendido").equals("1") || objeto.getString("PED_Atendido").equals("3") || objeto.getString("PED_Atendido").equals("10")){
                                layoutRepartidor.setVisibility(View.VISIBLE);
                                if(objeto.getString("PR_Nombres") == null){
                                    txtNombreRepartidor.setText("No tiene un repartidor asignado.");
                                }
                            }
                            else{
                                layoutRepartidor.setVisibility(View.GONE);
                            }*/
                            int numestado = objeto.getInt("PED_Atendido");
                            estadoPedidoReal = objeto.getInt("PED_Atendido");
                            String estado = "";
                            if(numestado == 1) {
                                estado = "Se entregó";
                            }
                            else if(numestado == 2) {
                                estado = "Cancelado";
                            }
                            else {
                                estado = "En curso";
                            }
                            txtEstado.setText(estado);
                            txtEstablecimiento.setText(objeto.getString("Establecimiento"));
                            codigoEmpresa = objeto.getInt("CodigoEmpresa");
                            txtTotal.setText(objeto.getString("Total"));
                            txtDireccion.setText(objeto.getString("PED_Direccion"));
                            txtSubTotalPedido.setText("s/" + objeto.getString("Total"));
                            txtDeliveryPedido.setText("s/" + objeto.getString("PED_MontoDelivery"));
                            txtCargoPedido.setText("s/" + objeto.getString("PED_MontoCargo"));
                            txtDescuentoPedido.setText("s/-" + objeto.getString("PED_Descuento"));
                            double st = objeto.getDouble("Total");
                            double md = objeto.getDouble("PED_MontoDelivery");
                            double mc = objeto.getDouble("PED_MontoCargo");
                            double de = objeto.getDouble("PED_Descuento");
                            double suma= (st + md + mc - de);

                            txtTotalPedido.setText("s/" + String.format("%.2f",suma).toString().replace(",","."));

                            PedidoDetalle detalle = new PedidoDetalle();
                            if (objeto.getInt("PRO_Codigo") == 0) {
                                detalle.setEsPromocion(true);
                                Promocion prom = new Promocion();
                                prom.setCodigo(objeto.getInt("PRO_Codigo"));
                                prom.setDescripcion(objeto.getString("Producto"));
                                prom.setImagen(objeto.getString("ImagenProducto"));
                                detalle.setPromocion(prom);
                            }else {
                                detalle.setEsPromocion(false);
                                Producto prod = new Producto();
                                prod.setCodigo(objeto.getInt("PRO_Codigo"));
                                prod.setDescripcion(objeto.getString("Producto"));
                                prod.setImagen(objeto.getString("ImagenProducto"));
                                detalle.setProducto(prod);
                            }
                            detalle.setPresentacion(objeto.getString("Presentacion"));
                            detalle.setCantidad(objeto.getInt("PD_Cantidad"));
                            detalle.setTotal(objeto.getDouble("PD_Total"));
                            list.add(detalle);
                        }
                        String urlLogo = "https://fastbuych.com/empresas/logos/" + URLEncoder.encode(logoEmpresa);
                        GlideApp.with(DetallesPedidoActivity.this)
                                .load(urlLogo)
                                .centerCrop()
                                .placeholder(R.drawable.restaurante)
                                .into(imgLogo);

                        String urlRepartidor = "https://fastbuych.com/imagenes/agentesreparto/" + URLEncoder.encode(fotoRepartidor);
                        GlideApp.with(DetallesPedidoActivity.this)
                                .load(urlRepartidor)
                                .centerCrop()
                                .placeholder(R.drawable.ic_usuario)
                                .into(imgRepartidor);
                        gridView.setNumColumns(1);
                        adapter = new detallePedidoListAdapter(DetallesPedidoActivity.this, R.layout.item_detalle_pedido, list);
                        gridView.setAdapter(adapter);
                        gridView.setVerticalScrollBarEnabled(false);
                        setGridViewHeightBasedOnChildren(gridView, 1);
                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                    catch (IllegalArgumentException i){
                        i.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

        gridView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }

        });

        layoutRepartidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtEstado.getText().equals("En curso"))
                {
                    Intent intentSeguimiento = new Intent(DetallesPedidoActivity.this, SiguiendoPedidoActivity.class);
                    intentSeguimiento.putExtra("state", estadoPedidoReal);
                    intentSeguimiento.putExtra("empresa", codigoEmpresa);
                    intentSeguimiento.putExtra("pedido", Integer.parseInt(codigoPedido));
                    startActivity(intentSeguimiento);
                }
            }
        });
        //Menu

    }

    public void muestraEstado(TextView txt, int cod){
        switch (cod){
            case 0:
                muestratxt(txt,R.color.colorpendiente);
                txt.setText("Pedido Pendiente");
                break;
            case 1:
                muestratxt(txt,R.color.coloratendido);
                txt.setText("Pedido Entregado");
                break;
            case 2:
                muestratxt(txt,R.color.colorcancelado);
                txt.setText("Pedido Cancelado");
                break;
            case 3:
                muestratxt(txt,R.color.colorencamino);
                txt.setText("Pedido En Camino");
                break;
            case 4:
                muestratxt(txt,R.color.colorAceptado);
                txt.setText("Pedido Aceptado");
                break;
            default:
                txt.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void muestratxt(TextView txt,int color){
        txt.setVisibility(View.VISIBLE);
        txt.setBackgroundResource(color);
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


}
