package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.MisPedidosListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.MiExtra;
import com.fastbuyapp.omar.fastbuy.entidades.MiPedido;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialPedidosActivity extends AppCompatActivity {

    ProgressDialog progDailog = null;
    ArrayList<MiPedido> list;
    ArrayList<MiExtra> listExtra;
    MisPedidosListAdapter adapter = null;
    MiExtraListAdapter adapterExtra = null;
    GridView gridView;
    int numerito = 5;
    String type;
    Button btnCargarMas;

    ImageButton btnCarrito;
    SharedPreferences myPreferences;
    String nombre, numero, tokencito;

    @Override
    protected void onResume() {
        super.onResume();
        Globales.valida.validarCarritoVacio(btnCarrito);
        muestraListas();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        type = getIntent().getStringExtra("tipo");

        //inicializando componentes
        gridView = (GridView) findViewById(R.id.dtgvHistorialPedidos);
        btnCargarMas = (Button) findViewById(R.id.btnCargarMas);
        TextView txtNombreUser = (TextView) findViewById(R.id.txtNameUserHistorial);
        TextView txtNumberUser = (TextView) findViewById(R.id.txtNumberUserHistorial);

        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        nombre = myPreferences.getString("Name_Cliente", "");
        numero = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        txtNombreUser.setText(nombre);
        txtNumberUser.setText(numero);

        btnCargarMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numerito += 5;
                muestraListas();
                gridView.setStackFromBottom(true);
            }
        });

        //listaHistorialPedidos(String.valueOf(numerito));

        //Menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialPedidosActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialPedidosActivity.this, FavoritosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialPedidosActivity.this, CarritoActivity.class);
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

    public void muestraListas(){
        if (type.equals("pedido")){
            listaHistorialPedidos(String.valueOf(numerito));
        }else{
            listaHistorialPedidosExtras(String.valueOf(numerito));
        }
    }

    public void listaHistorialPedidos(String cantidad)
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarPedidos_XCliente_XLimite?auth="+tokencito+"&cliente="+numero+"&limite="+cantidad;
        progDailog = new ProgressDialog(HistorialPedidosActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatos(consulta);
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(HistorialPedidosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    btnCargarMas.setVisibility(View.VISIBLE);
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            MiPedido pedido = new MiPedido();
                            pedido.setCodigo(objeto.getInt("PED_Codigo"));
                            String establecimientos;
                            if (objeto.getString("PED_Establecimiento1")== "null")
                                establecimientos = objeto.getString("PED_Establecimiento2");
                            else if (objeto.getString("PED_Establecimiento2")== "null")
                                establecimientos = objeto.getString("PED_Establecimiento1");
                            else
                                establecimientos = objeto.getString("PED_Establecimiento1")+" - "+objeto.getString("PED_Establecimiento2");

                            if (objeto.getString("PED_CodEstablecimiento1")!= "null")
                                pedido.setCodigoEmpresa(objeto.getString("PED_CodEstablecimiento1"));
                            else if (objeto.getString("PED_CodEstablecimiento2")!= "null")
                                pedido.setCodigoEmpresa(objeto.getString("PED_CodEstablecimiento2"));

                            pedido.setEstablecimiento1(establecimientos.toString());
                            pedido.setEstablecimiento2("");
                            pedido.setEstado(objeto.getInt("Estado"));
                            pedido.setFecha(objeto.getString("PED_Fecha"));
                            pedido.setTotal(objeto.getString("PED_Total"));
                            pedido.setSubTotal(objeto.getString("PED_SubTotal"));
                            pedido.setDelivery(objeto.getString("PED_Delivery"));
                            pedido.setCargo(objeto.getString("PED_Cargo"));
                            pedido.setDescuento(objeto.getString("PED_Descuento"));
                            list.add(pedido);
                        }

                        gridView.setNumColumns(1);
                        adapter = new MisPedidosListAdapter(HistorialPedidosActivity.this, R.layout.list_item_mi_pedido, list);
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                int state = list.get(position).getEstado();
                                //Toast.makeText(HistorialPedidosActivity.this,"codigo empresa "+list.get(position).getCodigoEmpresa(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HistorialPedidosActivity.this, SiguiendoPedidoActivity.class);
                                intent.putExtra("state",String.valueOf(state));
                                intent.putExtra("empresa",list.get(position).getCodigoEmpresa());
                                intent.putExtra("pedido",String.valueOf(list.get(position).getCodigo()));
                                intent.putExtra("cantidadRespuestas","1");//cambiar a cero "1"
                                startActivity(intent);
                            }
                        });
                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }else {
                    Toast toast = Toast.makeText(HistorialPedidosActivity.this, "Lista Vacía",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    btnCargarMas.setVisibility(View.VISIBLE);
                    progDailog.dismiss();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void listaHistorialPedidosExtras(String cantidad)
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarExtras_XCliente_XLimite?auth="+tokencito+"&cliente="+numero+"&limite="+cantidad;
        progDailog = new ProgressDialog(HistorialPedidosActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatosExtras(consulta);
    }

    public void EnviarRecibirDatosExtras(String URL){
        RequestQueue queue = Volley.newRequestQueue(HistorialPedidosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    btnCargarMas.setVisibility(View.VISIBLE);
                    try {
                        JSONArray ja = new JSONArray(response);
                        listExtra = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            MiExtra pedido = new MiExtra();
                            pedido.setCodigo(objeto.getInt("EXT_Codigo"));
                            pedido.setDetalle(objeto.getString("EXT_Pedido"));
                            pedido.setInicio(objeto.getString("EXT_Tienda"));
                            pedido.setFin(objeto.getString("EXT_Direccion"));
                            pedido.setEstado(objeto.getInt("EXT_Estado"));
                            pedido.setFecha(objeto.getString("EXT_FechaRegistro")+" "+objeto.getString("EXT_HoraRegistro"));
                            pedido.setTotal(objeto.getString("EXT_Total"));
                            listExtra.add(pedido);
                        }

                        gridView.setNumColumns(1);
                        adapterExtra = new MiExtraListAdapter(HistorialPedidosActivity.this, R.layout.list_item_extra, listExtra);
                        gridView.setAdapter(adapterExtra);
                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
                else {
                    Toast toast = Toast.makeText(HistorialPedidosActivity.this, "Lista Vacía",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    btnCargarMas.setVisibility(View.GONE);
                    progDailog.dismiss();
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
        if (type.equals("pedido"))
            inflater.inflate(R.menu.menu_historial_pedidos, menu);
        else
            inflater.inflate(R.menu.menu_historial_extras, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HistorialPedidosActivity.this,UserActivity.class);
        startActivity(intent);
    }
}
