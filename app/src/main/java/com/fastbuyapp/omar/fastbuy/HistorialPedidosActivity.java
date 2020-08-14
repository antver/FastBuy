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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.MisPedidosListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.MiExtra;
import com.fastbuyapp.omar.fastbuy.entidades.MiPedido;
import com.fastbuyapp.omar.fastbuy.entidades.Pedido;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoHist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class HistorialPedidosActivity extends AppCompatActivity {

    ProgressDialog progDailog = null;
    //ArrayList<MiPedido> list;
    //ArrayList<MiExtra> listExtra;
    MisPedidosListAdapter adapter = null;
    MiExtraListAdapter adapterExtra = null;
    ArrayList<PedidoHist> pedidosFinalizados;
    ArrayList<PedidoHist> pedidosCancelados;
    ArrayList<PedidoHist> pedidosPendientes;
    GridView gridView, gridViewFinalizados, gridViewCancelados;
    int numerito = 100;
    String type;
    //Button btnCargarMas;

    ImageButton btnCarrito;
    SharedPreferences myPreferences;
    String nombre, numero, tokencito;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
        //listaHistorialPedidos(String.valueOf(numerito));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos);

        type = getIntent().getStringExtra("tipo");

        //inicializando componentes
        gridView = (GridView) findViewById(R.id.dtgvHistorialPedidos);
        gridViewFinalizados = (GridView) findViewById(R.id.dtgvHistorialPedidosAtendidos);
        gridViewCancelados = (GridView) findViewById(R.id.dtgvHistorialPedidosCancelados);
        //btnCargarMas = (Button) findViewById(R.id.btnCargarMas);
       // TextView txtNombreUser = (TextView) findViewById(R.id.txtNameUserHistorial);
        
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        nombre = myPreferences.getString("Name_Cliente", "");
        numero = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        pedidosCancelados = new ArrayList<>();
        pedidosFinalizados = new ArrayList<>();
        pedidosPendientes = new ArrayList<>();
       // txtNombreUser.setText(nombre);
        //txtNumberUser.setText(numero);

        /*btnCargarMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numerito += 5;
                listaHistorialPedidos(String.valueOf(numerito));
                gridView.setStackFromBottom(true);
            }
        });*/

        listaHistorialPedidos(String.valueOf(numerito));

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
        final TextView txtEnCurso = (TextView) findViewById(R.id.txtEnCurso);
        final TextView txtFinalizados = (TextView) findViewById(R.id.txtFinalizados);
        final TextView txtCancelados = (TextView) findViewById(R.id.txtCancelados);
        txtEnCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridView.getVisibility() == View.VISIBLE) {
                    gridView.setVisibility(View.GONE);
                    txtEnCurso.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_left_black_24dp, 0);

                }
                else {
                    gridView.setVisibility(View.VISIBLE);
                    txtEnCurso.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                }
            }
        });

        txtFinalizados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridViewFinalizados.getVisibility() == View.VISIBLE) {
                    gridViewFinalizados.setVisibility(View.GONE);
                    txtFinalizados.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_left_black_24dp, 0);
                }
                else {
                    gridViewFinalizados.setVisibility(View.VISIBLE);
                    txtFinalizados.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                }
            }
        });

        txtCancelados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridViewCancelados.getVisibility() == View.VISIBLE) {
                    gridViewCancelados.setVisibility(View.GONE);
                    txtCancelados.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_left_black_24dp, 0);
                } else {
                    gridViewCancelados.setVisibility(View.VISIBLE);
                    txtCancelados.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                }
            }
        });
    }

    /*public void muestraListas(){
        if (type.equals("pedido")){
            listaHistorialPedidos(String.valueOf(numerito));
        }else{
            listaHistorialPedidosExtras(String.valueOf(numerito));
        }
    }*/

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
                    //btnCargarMas.setVisibility(View.VISIBLE);
                    try {
                        JSONArray ja = new JSONArray(response);
                        //list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            PedidoHist pedido = new PedidoHist();
                            pedido.setCodigo(objeto.getInt("PED_Codigo"));
                            Empresa empresa = new Empresa();
                            empresa.setNombreComercial(objeto.getString("Establecimiento"));
                            empresa.setImagen(objeto.getString("Logo"));
                            /*if (objeto.getString("PED_Establecimiento1")== "null")
                                establecimientos = objeto.getString("PED_Establecimiento2");
                            else if (objeto.getString("PED_Establecimiento2")== "null")
                                establecimientos = objeto.getString("PED_Establecimiento1");
                            else
                                establecimientos = objeto.getString("PED_Establecimiento1")+" - "+objeto.getString("PED_Establecimiento2");
                            */
                            /*if (objeto.getString("PED_CodEstablecimiento1")!= "null")
                                pedido.setCodigoEmpresa(objeto.getString("PED_CodEstablecimiento1"));
                            else if (objeto.getString("PED_CodEstablecimiento2")!= "null")
                                pedido.setCodigoEmpresa(objeto.getString("PED_CodEstablecimiento2"));*/

                            //pedido.setEstablecimiento1(establecimientos.toString());
                            //pedido.setEstablecimiento2("");
                            pedido.setEmpresa(empresa);
                            pedido.setFecha(objeto.getString("PED_FechaPedido"));

                            pedido.setSubTotal(objeto.getString("Total"));
                            pedido.setDelivery(objeto.getString("PED_MontoDelivery"));
                            pedido.setCargo(objeto.getString("PED_MontoCargo"));
                            pedido.setDescuento(objeto.getString("PED_Descuento"));
                            double sub = objeto.getDouble("Total");
                            double del = objeto.getDouble("PED_MontoDelivery");
                            double car = objeto.getDouble("PED_MontoCargo");
                            double des = objeto.getDouble("PED_Descuento");
                            double total = sub + del + car - des;
                            pedido.setTotal(String.format("%.2f",total).toString().replace(",","."));
                            //list.add(pedido);
                            int numestado = objeto.getInt("PED_Atendido");
                            String estado = "";
                            if(numestado == 1) {
                                estado = "Se entregó";
                                pedido.setEstado(estado);
                                pedidosFinalizados.add(pedido);
                            }
                            else if(numestado == 2) {
                                estado = "Cancelado";
                                pedido.setEstado(estado);
                                pedidosCancelados.add(pedido);
                            }
                            else {
                                estado = "En curso";
                                pedido.setEstado(estado);
                                pedidosPendientes.add(pedido);
                            }


                        }


                        gridView.setNumColumns(1);
                        adapter = new MisPedidosListAdapter(HistorialPedidosActivity.this, R.layout.list_item_mi_pedido, pedidosPendientes);
                        gridView.setAdapter(adapter);
                        gridView.setVerticalScrollBarEnabled(false);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //String state = pedidosPendientes.get(position).getEstado();
                                //Toast.makeText(HistorialPedidosActivity.this,"codigo empresa "+list.get(position).getCodigoEmpresa(),Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(HistorialPedidosActivity.this, SiguiendoPedidoActivity.class);
                                Intent intent = new Intent(HistorialPedidosActivity.this, DetallesPedidoActivity.class);
                                //intent.putExtra("state",String.valueOf(state));
                                //intent.putExtra("empresa",pedidosPendientes.get(position).getEmpresa().get);
                                intent.putExtra("pedido",String.valueOf(pedidosPendientes.get(position).getCodigo()));
                                //intent.putExtra("cantidadRespuestas","1");//cambiar a cero "1"
                                startActivity(intent);
                            }
                        });
                        gridViewFinalizados.setNumColumns(1);
                        adapter = new MisPedidosListAdapter(HistorialPedidosActivity.this, R.layout.list_item_mi_pedido, pedidosFinalizados);
                        gridViewFinalizados.setAdapter(adapter);
                        gridViewFinalizados.setVerticalScrollBarEnabled(false);
                        gridViewFinalizados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(HistorialPedidosActivity.this, DetallesPedidoActivity.class);
                                intent.putExtra("pedido",String.valueOf(pedidosFinalizados.get(position).getCodigo()));
                                startActivity(intent);
                            }
                        });
                        gridViewCancelados.setNumColumns(1);
                        adapter = new MisPedidosListAdapter(HistorialPedidosActivity.this, R.layout.list_item_mi_pedido, pedidosCancelados);
                        gridViewCancelados.setAdapter(adapter);
                        gridViewCancelados.setVerticalScrollBarEnabled(false);
                        gridViewCancelados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(HistorialPedidosActivity.this, DetallesPedidoActivity.class);
                                intent.putExtra("pedido",String.valueOf(pedidosCancelados.get(position).getCodigo()));
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
                    //btnCargarMas.setVisibility(View.VISIBLE);
                    progDailog.dismiss();
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
    }

    /*public void listaHistorialPedidosExtras(String cantidad)
    {
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarExtras_XCliente_XLimite?auth="+tokencito+"&cliente="+numero+"&limite="+cantidad;
        progDailog = new ProgressDialog(HistorialPedidosActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        EnviarRecibirDatosExtras(consulta);
    }*/

    /*public void EnviarRecibirDatosExtras(String URL){
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
    }*/

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
