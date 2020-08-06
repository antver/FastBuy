package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.adaptadores.detallePedidoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class DetallePedidoConfirmar extends AppCompatActivity {

    TextView txtNumPedidoConfi, txtFechaPedidoConfi, txtSubTotalPedidoConfi, txtDeliveryPedidoConfi, txtDescuentoPedidoConfi, txtTotalPedidoConfi;
    Button btnCancelarPedidoConfi, btnContinuarPedidoConfi;
    GridView gvListPedidosConfirmar;
    String CodigoPedido, CodigoEmpresaPedido, FechaPedido, HoraPedido, DeliveryPedido,DescuentoPedido;
    ArrayList<PedidoDetalle> list;
    PedidoConfirmarAdapter adapter = null;
    ProgressDialog progDailog = null;
    SharedPreferences myPreferences;
    double xSubTotal = 0;
    double xTotal = 0;
    String tokencito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido_confirmar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        CodigoPedido = getIntent().getStringExtra("codigo");
        CodigoEmpresaPedido = getIntent().getStringExtra("empresa");
        FechaPedido = getIntent().getStringExtra("Fecha");
        HoraPedido = getIntent().getStringExtra("Hora");
        DeliveryPedido = getIntent().getStringExtra("Delivery");
        DescuentoPedido = getIntent().getStringExtra("Descuento");

        //Inicializando variables
        txtNumPedidoConfi = findViewById(R.id.txtNroPedidoConfirmar);
        txtFechaPedidoConfi = findViewById(R.id.txtFechaPedidoConfirmar);
        txtSubTotalPedidoConfi = findViewById(R.id.txtSubTotalPedidoConfirmar);
        txtDeliveryPedidoConfi = findViewById(R.id.txtDeliveryPedidoConfi);
        txtDescuentoPedidoConfi = findViewById(R.id.txtDescuentoPedidoConfirmar);
        txtTotalPedidoConfi = findViewById(R.id.txtTotalPedidoConfirmar);
        btnCancelarPedidoConfi = findViewById(R.id.btnCancelarConfirmacion);
        btnContinuarPedidoConfi = findViewById(R.id.btnContinuarConfirmacion);
        gvListPedidosConfirmar = findViewById(R.id.dtgvItemsPedidoConfirmar);

        txtNumPedidoConfi.setText("Pedido Nº "+CodigoPedido);
        txtFechaPedidoConfi.setText(FechaPedido + " " +HoraPedido);
        txtDeliveryPedidoConfi.setText("S/ " +DeliveryPedido.replace(",","."));
        txtDescuentoPedidoConfi.setText("S/ " +DescuentoPedido.replace(",","."));
        listaDetallePedidoConfirmar(CodigoPedido);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        tokencito = myPreferences.getString("tokencito", "");

        btnContinuarPedidoConfi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePedidoConfirmar.this, SiguiendoPedidoActivity.class);

                String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ActualizaDetallePedido_XCodigo?auth="+ tokencito+"&codigo="+CodigoPedido+"&empresa="+CodigoEmpresaPedido+"&fecha="+FechaPedido+"&hora="+HoraPedido+"&monto="+String.valueOf(xSubTotal);
                continuarPedido(consulta, intent);
            }
        });

        btnCancelarPedidoConfi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetallePedidoConfirmar.this);
                builder.setMessage("Está a punto de cancelar el pedido. ¿Desea continuar?");
                builder.setTitle("Cancelar Pedido");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DetallePedidoConfirmar.this, SiguiendoPedidoActivity.class);
                        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/CancelarPedido?auth="+ tokencito+"&codigo="+CodigoPedido+"&empresa="+CodigoEmpresaPedido;
                        CancelandoPedido(consulta,intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog a = builder.create();
                a.show();
            }
        });
    }

    public  void continuarPedido(String URL,final Intent intent){
        progDailog = new ProgressDialog(DetallePedidoConfirmar.this);
        progDailog.setMessage("Enviando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        RequestQueue queue = Volley.newRequestQueue(DetallePedidoConfirmar.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    progDailog.dismiss();
                    intent.putExtra("state",String.valueOf(0));
                    intent.putExtra("empresa",CodigoEmpresaPedido);
                    intent.putExtra("pedido",CodigoPedido);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void listaDetallePedidoConfirmar(String codigo)
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDetallePedido_XCodigo?auth="+ tokencito +"&codigo="+codigo;
        progDailog = new ProgressDialog(DetallePedidoConfirmar.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(DetallePedidoConfirmar.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            PedidoDetalle detalle = new PedidoDetalle();
                            if (objeto.getInt("Codigo_PROD") == 0) {
                                detalle.setEsPromocion(true);
                                Promocion prom = new Promocion();
                                prom.setCodigo(objeto.getInt("Codigo_PROM"));
                                prom.setDescripcion(objeto.getString("Nombre_PROM"));
                                detalle.setPromocion(prom);
                            } else {
                                detalle.setEsPromocion(false);
                                Producto prod = new Producto();
                                prod.setCodigo(objeto.getInt("Codigo_PROD"));
                                prod.setDescripcion(objeto.getString("Nombre_PROD"));
                                detalle.setProducto(prod);
                            }
                            detalle.setCantidad(objeto.getInt("Cantidad"));
                            detalle.setTotal(objeto.getDouble("Total"));
                            detalle.setEstado(objeto.getInt("Estado_PROD"));
                            int estadito = objeto.getInt("Estado_PROD");
                            if (estadito != 2){
                                Log.v("detalle_estado",String.valueOf(estadito));
                                xSubTotal+= (double) objeto.getDouble("Total");
                            }
                            list.add(detalle);
                        }

                        gvListPedidosConfirmar.setNumColumns(1);
                        adapter = new PedidoConfirmarAdapter(DetallePedidoConfirmar.this, R.layout.list_item_pedido_confirmar, list);
                        gvListPedidosConfirmar.setAdapter(adapter);

                        txtSubTotalPedidoConfi.setText("S/ " +String.format("%.2f",xSubTotal).toString().replace(",","."));
                        xTotal =(double) (xSubTotal + Double.valueOf(DeliveryPedido) - Double.valueOf(DescuentoPedido));
                        txtTotalPedidoConfi.setText("S/ " +String.format("%.2f",xTotal).toString().replace(",","."));
                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void CancelandoPedido(String URL,final Intent intent){
        progDailog = new ProgressDialog(DetallePedidoConfirmar.this);
        progDailog.setMessage("Cancelando pedido...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        RequestQueue queue = Volley.newRequestQueue(DetallePedidoConfirmar.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progDailog.dismiss();
                intent.putExtra("state",String.valueOf(2));
                intent.putExtra("empresa",CodigoEmpresaPedido);
                intent.putExtra("pedido",CodigoPedido);
                startActivity(intent);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detalle_pedido_confirmar, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
