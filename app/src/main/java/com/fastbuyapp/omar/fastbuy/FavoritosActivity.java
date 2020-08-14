package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapter;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapterMosaico;
import com.fastbuyapp.omar.fastbuy.adaptadores.RecyclerAdapterPromociones;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity {

    RecyclerView rvPromociones;
    GridView gridView;
    ArrayList<Empresa> list;
    ArrayList<Promocion> listProm;
    EmpresaListAdapterMosaico adapterMosaico = null;
    ProgressDialog progDailog = null,progDailog1=null;
    ImageButton btnCarrito;
    LinearLayout LinearSinFav, LinearConFav, LinearPromFav;
    SharedPreferences.Editor myEditor;
    String tokencito, ubicacion, numero;
    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        tokencito = myPreferences.getString("tokencito", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        numero  = myPreferences.getString("Number_Cliente", "");
        //inicializar componentes
        gridView = (GridView) findViewById(R.id.gvListaFav);
        LinearConFav = (LinearLayout) findViewById(R.id.linearContenidoFavoritos);
        LinearSinFav = (LinearLayout) findViewById(R.id.linearFavoritoVacio);
        LinearPromFav = findViewById(R.id.linearPromocionesFav);
        rvPromociones = findViewById(R.id.rvPromocionesFav);

        listarPromocionesFav();
        EnviarRecibirDatosFav();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int codigo =(list.get(position).getCodigo());
                myEditor.putString("codigo_empresa", String.valueOf(codigo));
                myEditor.putString("nombre_empresa", list.get(position).getNombreComercial());
                myEditor.putString("longitud_empresa", String.valueOf(list.get(position).getLongitud()));
                myEditor.putString("latitud_empresa", String.valueOf(list.get(position).getLatitud()));
                myEditor.putString("logo_empresa", list.get(position).getImagen());
                myEditor.putString("portada_empresa", list.get(position).getImagenFondo());
                myEditor.putString("taper_empresa", list.get(position).getTaper());
                myEditor.putString("costo_taper", String.valueOf(list.get(position).getCostoTaper()));
                String categoria = String.valueOf(list.get(position).getCategoria());
                myEditor.putString("categoria", categoria);
                if(list.get(position).getEstadoAbierto().equals("Abierto")){
                    myEditor.putBoolean("tiendaCerrada", false);
                }
                else{
                    myEditor.putBoolean("tiendaCerrada", true);
                }
                myEditor.commit();

                Intent intent = new Intent(FavoritosActivity.this, ProductosActivity.class);
                startActivity(intent);
            }
        });

        //MENU PRINCIPAL
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritosActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritosActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritosActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritosActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

    }

    public void activaLinear(){
        LinearSinFav.setVisibility(View.GONE);
        LinearConFav.setVisibility(View.VISIBLE);
    }
    public void desactivaLinear(){
        LinearSinFav.setVisibility(View.VISIBLE);
        LinearConFav.setVisibility(View.GONE);
    }

    public void EnviarRecibirDatosFav(){
        progDailog = new ProgressDialog(FavoritosActivity.this);
        progDailog.setMessage("Cargando...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        String miURL = "https://apifbdelivery.fastbuych.com/Delivery/ListarFavoritosXCliente?auth="+tokencito+"&ubicacion="+ubicacion+"&telefono="+ numero;
        Log.v("miURlFavorito",miURL);
        RequestQueue queue = Volley.newRequestQueue(FavoritosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, miURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("nulo")){
                    activaLinear();
                    if (response.length()>0){
                        activaLinear();
                        try {
                            JSONArray ja = new JSONArray(response);
                            list = new ArrayList<>();
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
                                empresa.setCategoria(objeto.getInt("categoria"));
                                list.add(empresa);
                            }

                            gridView.setNumColumns(2);
                            adapterMosaico = new EmpresaListAdapterMosaico(FavoritosActivity.this, R.layout.item_list_mosaico, list);
                            gridView.setAdapter(adapterMosaico);
                            progDailog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progDailog.dismiss();
                            desactivaLinear();
                        }
                    }
                    else {
                        progDailog.dismiss();
                        desactivaLinear();
                    }
                }
                else {
                    progDailog.dismiss();
                    desactivaLinear();
                }
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                desactivaLinear();
            }
        });

        queue.add(stringRequest);

    }

    public void listarPromocionesFav(){
        progDailog1 = new ProgressDialog(FavoritosActivity.this);
        progDailog1.setMessage("Cargando Promociones...");
        progDailog1.setIndeterminate(true);
        progDailog1.setCancelable(false);
        progDailog1.show();

        String URL = "https://apifbdelivery.fastbuych.com/Delivery/PromocionesXUbicacionXFavoritoXCliente?auth="+tokencito+"&ubica="+ubicacion+"&telefono="+numero;
        RequestQueue queue = Volley.newRequestQueue(FavoritosActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    LinearPromFav.setVisibility(View.VISIBLE);
                    try {
                        JSONArray ja = new JSONArray(response);
                        listProm = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Promocion promocion = new Promocion();
                            promocion.setCodigo(objeto.getInt("PRM_Codigo"));
                            promocion.setDescripcion(objeto.getString("PRM_Descripcion"));
                            promocion.setPrecio(objeto.getString("PRM_Precio"));
                            promocion.setImagen(objeto.getString("PRM_Imagen"));
                            promocion.setEstado(objeto.getInt("PRM_Estado"));
                            Categoria categoria = new Categoria();
                            categoria.setDescripcion(objeto.getString("CAT_Codigo"));
                            promocion.setCategoria(categoria);
                            Empresa empresa = new Empresa();
                            empresa.setCodigo(objeto.getInt("EMP_Codigo"));
                            empresa.setNombreComercial(objeto.getString("EMP_NombreComercial"));
                            empresa.setLongitud(objeto.getDouble("EU_Longitud"));
                            empresa.setLatitud(objeto.getDouble("EU_Latitud"));
                            promocion.setEmpresa(empresa);
                            Calcular_Minutos calcula = new Calcular_Minutos();
                            promocion.setTiempo(calcula.ObtenMinutos(objeto.getString("PRM_Preparacion")));
                            listProm.add(promocion);
                        }
                        try {
                            rvPromociones.setLayoutManager(new LinearLayoutManager(FavoritosActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rvPromociones.setAdapter(new RecyclerAdapterPromociones(FavoritosActivity.this, listProm));
                            final GestureDetector mGestureDetector = new GestureDetector(FavoritosActivity.this, new GestureDetector.SimpleOnGestureListener(){
                                @Override
                                public boolean onSingleTapUp(MotionEvent e){
                                    return true;
                                }
                            });
                            rvPromociones.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                                @Override
                                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                    try {
                                        View child = rvPromociones.findChildViewUnder(e.getX(),e.getY());
                                        if(child != null && mGestureDetector.onTouchEvent(e)){
                                            int position = rvPromociones.getChildAdapterPosition(child);
                                            Globales.PromocionPersonalizar = listProm.get(position);
                                            Intent intent = new Intent(FavoritosActivity.this,PersonalizaPromoActivity.class);
                                            startActivity(intent);
                                            return true;
                                        }
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                    return false;
                                }

                                @Override
                                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                                }

                                @Override
                                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                                }
                            });
                            progDailog1.dismiss();
                        } catch (Exception ex) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog1.dismiss();
                    }
                }
                else {
                    progDailog1.dismiss();
                    LinearPromFav.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog1.dismiss();
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menufavoritos, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(FavoritosActivity.this,PrincipalActivity.class);
        startActivity(intent);
    }
}
