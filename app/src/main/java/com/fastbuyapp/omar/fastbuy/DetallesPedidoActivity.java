package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.adaptadores.detallePedidoListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Producto;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetallesPedidoActivity extends AppCompatActivity {

    String codigoPedido, SubTotal, Delivery, Cargo, Descuento, Total, estado, fecha;
    ProgressDialog progDailog = null;
    ArrayList<PedidoDetalle> list;
    GridView gridView;
    detallePedidoListAdapter adapter = null;
    String tokencito;
    ImageButton btnCarrito;

    @Override
    protected void onResume() {
        super.onResume();
        Globales.valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        tokencito = myPreferences.getString("tokencito", "");

        //recibiendo codigo de Pedido
        codigoPedido = getIntent().getStringExtra("NumPedido");
        SubTotal = getIntent().getStringExtra("SubTotalPedido");
        Delivery = getIntent().getStringExtra("DeliveryPedido");
        Cargo = getIntent().getStringExtra("CargoPedido");
        Descuento = getIntent().getStringExtra("DescuentoPedido");
        Total = getIntent().getStringExtra("TotalPedido");
        estado = getIntent().getStringExtra("EstadoPedido");
        fecha = getIntent().getStringExtra("FechaPedido");

        //Inicializamos Componentes
        TextView txtNumPedido = (TextView) findViewById(R.id.txtNroPedido);
        TextView txtFecha = (TextView) findViewById(R.id.txtFechaPedido);
        TextView txtSubTotalPedido = (TextView) findViewById(R.id.txtSubTotalPedidoConfirmar);
        TextView txtDeliveryPedido = (TextView) findViewById(R.id.txtDeliveryPedido);
        TextView txtCargoPedido = (TextView) findViewById(R.id.txtCargoPedido);
        TextView txtDescuentoPedido = (TextView) findViewById(R.id.txtDescuentoPedido);
        TextView txtTotalPedido = (TextView) findViewById(R.id.txtTotalPedido);
        TextView txtEstado = (TextView) findViewById(R.id.txtEstadoPedido);
        gridView = (GridView) findViewById(R.id.dtgvItemsPedido);

        txtNumPedido.setText("Pedido Nº "+codigoPedido);
        txtFecha.setText(fecha);
        txtSubTotalPedido.setText("s/"+SubTotal);
        txtDeliveryPedido.setText("s/"+Delivery);
        txtCargoPedido.setText("s/"+Cargo);
        txtDescuentoPedido.setText("s/-"+Descuento);
        txtTotalPedido.setText("s/"+Total);

        Log.v("estadoPedido",estado);
        muestraEstado(txtEstado, Integer.valueOf(estado));

        listaDetallePedido(codigoPedido);

        //Menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesPedidoActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesPedidoActivity.this, FavoritosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesPedidoActivity.this, CarritoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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

    public void listaDetallePedido(String codigo)
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDetallePedido_XCodigo?auth="+ tokencito+"&codigo="+codigo;
        progDailog = new ProgressDialog(DetallesPedidoActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){

        RequestQueue queue = Volley.newRequestQueue(DetallesPedidoActivity.this);
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
                            list.add(detalle);
                        }

                        gridView.setNumColumns(1);
                        adapter = new detallePedidoListAdapter(DetallesPedidoActivity.this, R.layout.list_producto_pedido, list);
                        gridView.setAdapter(adapter);
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

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detalle_pedido, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
