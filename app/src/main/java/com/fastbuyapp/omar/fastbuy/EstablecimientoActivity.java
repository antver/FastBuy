package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapter;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapterMosaico;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EstablecimientoActivity extends AppCompatActivity {
    GridView gridView;
    ArrayList<Empresa> list;
    EmpresaListAdapter adapter = null;
    EmpresaListAdapterMosaico adapterMosaico = null;
    String tipoVista = "MOSAICO";
    ProgressDialog progDailog = null;
    String tokencito, ubicacion, categoria, subcategoria;
    ImageButton btnCarrito;
    SharedPreferences.Editor myEditor;
    boolean tiendaCerrada;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establecimiento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inicializando estado del establecimiento a seleccionar
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        tokencito = myPreferences.getString("tokencito", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        categoria = myPreferences.getString("categoria", "");
        subcategoria = myPreferences.getString("subcategoria", "");
        tiendaCerrada = myPreferences.getBoolean("tiendaCerrada", true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        final String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarEmpresasXUbiXCatXSubCatXDescrip?auth="+tokencito+"&catego="+categoria+"&subcatego="+subcategoria+"&ubica="+ubicacion+"&descrip=";

        final ImageButton btnLista = (ImageButton) findViewById(R.id.btnLista);
        final ImageButton btnMosaico = (ImageButton) findViewById(R.id.btnMosaico);
        gridView = (GridView) findViewById(R.id.gvLista);

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progDailog = new ProgressDialog(EstablecimientoActivity.this);
                progDailog.setMessage("Cargando...");
                progDailog.setIndeterminate(true);
                progDailog.setCancelable(false);
                progDailog.show();

                tipoVista = "LISTA";
                EnviarRecibirDatos(consulta);
                btnLista.setColorFilter(Color.parseColor("#01C4A4")); // White Tint
                btnMosaico.setColorFilter(Color.parseColor("#E6E6E6"));
            }
        });
        btnMosaico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progDailog = new ProgressDialog(EstablecimientoActivity.this);
                progDailog.setMessage("Cargando...");
                progDailog.setIndeterminate(true);
                progDailog.setCancelable(false);
                progDailog.show();

                tipoVista = "MOSAICO";
                EnviarRecibirDatos(consulta);
                btnLista.setColorFilter(Color.parseColor("#E6E6E6"));
                btnMosaico.setColorFilter(Color.parseColor("#01C4A4"));
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(EstablecimientoActivity.this, "Hola", Toast.LENGTH_SHORT).show();
                int codigo =(list.get(position).getCodigo());
                myEditor.putString("codigo_empresa", String.valueOf(codigo));
                myEditor.putString("nombre_empresa", list.get(position).getNombreComercial());
                myEditor.putString("longitud_empresa", String.valueOf(list.get(position).getLongitud()));
                myEditor.putString("latitud_empresa", String.valueOf(list.get(position).getLatitud()));
                myEditor.putString("logo_empresa", list.get(position).getImagen());
                myEditor.putString("portada_empresa", list.get(position).getImagenFondo());
                myEditor.putString("taper_empresa", list.get(position).getTaper());
                myEditor.putString("costo_taper", String.valueOf(list.get(position).getCostoTaper()));
                myEditor.putInt("categoria_producto", 0);
                //String nombreComercial = list.get(position).getNombreComercial();
                /*Globales.nombreEmpresaSeleccionada = nombreComercial;
                Globales.LongitudEmpresaSeleccionada = list.get(position).getLongitud();
                Globales.LatitudEmpresaSeleccionada =  list.get(position).getLatitud();
                Globales.imagenEmpresa = list.get(position).getImagen();
                Globales.imagenFondoEmpresa = list.get(position).getImagenFondo();
                Globales.taperEmpresaSel = list.get(position).getTaper();
                Globales.costoTaperEmpresaSel = list.get(position).getCostoTaper();*/

                if(list.get(position).getEstadoAbierto().equals("Abierto")){
                    myEditor.putBoolean("tiendaCerrada", false);
                }
                else{
                    myEditor.putBoolean("tiendaCerrada", true);
                }
                myEditor.commit();
                Intent intent = new Intent(EstablecimientoActivity.this, ProductosActivity.class);
                //Intent intent = new Intent(EstablecimientoActivity.this, CartaActivity.class);
                startActivity(intent);
            }
        });
        btnMosaico.performClick();

        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EstablecimientoActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EstablecimientoActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EstablecimientoActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EstablecimientoActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }
    int tiendas = 0;

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(EstablecimientoActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        tiendas = 0;
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Empresa empresa = new Empresa();
                            empresa.setCodigo(objeto.getInt("EMP_Codigo"));
                            empresa.setNombreComercial(objeto.getString("EMP_NombreComercial"));
                            empresa.setRazonSocial(objeto.getString("EMP_RazonSocial"));
                            empresa.setDireccion(objeto.getString("EMP_Direccion"));
                            empresa.setImagen(objeto.getString("EMP_Imagen"));
                            empresa.setTelefonos(objeto.getString("EMP_Telefonos"));
                            empresa.setEstado(objeto.getInt("EMP_Estado"));
                            empresa.setEstadoAbierto(objeto.getString("EstadoAbierto"));
                            empresa.setLatitud(objeto.getDouble("EU_Latitud"));
                            empresa.setLongitud(objeto.getDouble("EU_Longitud"));
                            empresa.setTaper(objeto.getString("EMP_CobraTaper"));
                            empresa.setCostoTaper(objeto.getDouble("EMP_CostoTaper"));
                            empresa.setImagenFondo(objeto.getString("EMP_Portada"));
                            list.add(empresa);
                        }
                        //TextView txt = (TextView) findViewById(R.id.textView2);
                        //if(tiendas!= 1)
                            //txt.setText(String.valueOf(tiendas) + " tiendas abiertas");
                        //else
                            //txt.setText(String.valueOf(tiendas) + " tienda abierta");

                        if(tipoVista == "LISTA") {
                            gridView.setNumColumns(1);
                            adapter = new EmpresaListAdapter(EstablecimientoActivity.this, R.layout.list_item, list);
                            gridView.setAdapter(adapter);
                            progDailog.dismiss();
                        }
                        if(tipoVista == "MOSAICO"){
                            gridView.setNumColumns(2);
                            adapterMosaico = new EmpresaListAdapterMosaico(EstablecimientoActivity.this, R.layout.item_list_mosaico, list);
                            gridView.setAdapter(adapterMosaico);
                            progDailog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intentdes = new Intent(EstablecimientoActivity.this, ActivityDesconectado.class);
                startActivity(intentdes);
            }
        });

        queue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuestablecimiento, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    /*@Override
    public void onBackPressed(){
        Intent intent = new Intent(EstablecimientoActivity.this, DeliveryActivity.class);
        startActivity(intent);
    }*/
}
