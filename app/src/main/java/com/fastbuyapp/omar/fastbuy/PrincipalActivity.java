package com.fastbuyapp.omar.fastbuy;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Minutos;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.adaptadores.RecyclerAdapterPromociones;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaCategoria;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PrincipalActivity extends AppCompatActivity {

    RecyclerView rvPromociones;
    Spinner ciudades;
    public String codigo;
    public String categoria;
    ArrayList<Promocion> list;
    ArrayList<EmpresaCategoria> listCategorias;
    PromocionListAdapter adapter = null;
    ProgressDialog progDailog = null;
    TextView txtPromociones;
    String elOrigen;
    ImageView imvRestaurants;
    ImageView imvMarkets;
    //Intent myService;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    ImageButton btnCarrito;
    String ciudad; //nombre de la ciudad
    String ubicacion; // codigo de la ciudad
    String tokencito;
    List<Ubicacion> listCiudades;
    FrameLayout fondopromociones;

    @Override
    protected void onResume() {
        super.onResume();
        ValidacionDatos valida = new ValidacionDatos();
        valida.validarCarritoVacio(btnCarrito);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        ciudad = myPreferences.getString("City_Cliente", "");
        ubicacion = myPreferences.getString("ubicacion", "");
        tokencito = myPreferences.getString("tokencito", "");
        fondopromociones = (FrameLayout) findViewById(R.id.fondopromociones);

        //elOrigen = getIntent().getStringExtra("origen");//para evitar que el servicio se inicie

        //inicio 2º plano
        if (Globales.myService == null){
            Globales.myService = new Intent(getApplicationContext(), BackgroundChat.class);
            startService(Globales.myService);
        }

        /*if (!elOrigen.equals("Notificacion")){
            if (myService == null){
                myService = new Intent(getApplicationContext(), BackgroundChat.class);
                startService(myService);
            }
        }*/
        //fin 2º plano

        /*Asignación de controles a variables*/
        txtPromociones = (TextView) findViewById(R.id.txtPromociones);
        imvRestaurants = (ImageView)findViewById(R.id.imageView5);
        imvMarkets = (ImageView)findViewById(R.id.imageView8);
        ciudades = (Spinner) findViewById(R.id.spinnerCiudad);

        //estableciendo en true el inicio de sesion
        rvPromociones = (RecyclerView) findViewById(R.id.rvPromociones);

        //Asignando valores a las variables globales ciudadEncargo y codigo
       // Globales.CiudadEncargoSeleccionada = Globales.ciudadOrigen;
        //Globales.CodigoCiudadEncargoSeleccionada = Globales.ubicacion;

        //try {
            listarPromociones(Integer.parseInt(ubicacion));
            listaCiudades();
            //listarCategorias();
        //} catch (UnsupportedEncodingException e) {
        //e.printStackTrace();
        //}

        ciudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ciudadOrigen = ciudades.getSelectedItem().toString();
                myEditor.putString("City_Cliente", ciudadOrigen);
                int ubicacionnueva = obtenerPosicionCiudadSeleccionada(ciudades.getSelectedItem().toString());
                myEditor.putString("ubicacion", String.valueOf(ubicacionnueva));
                myEditor.commit();
                listarPromociones(ubicacionnueva);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //End Spinner de ciudades

        LinearLayout llRestaurantes = (LinearLayout) findViewById(R.id.llRestaurantes);
        llRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putString("categoria", "1");
                myEditor.commit();
                Intent intent = new Intent(PrincipalActivity.this, DeliveryActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout llMarket = (LinearLayout) findViewById(R.id.llMarket);
        llMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putString("categoria", "2");
                myEditor.commit();
                Intent intent = new Intent(PrincipalActivity.this, DeliveryActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout llEncargo = (LinearLayout) findViewById(R.id.llEncargos);
        llEncargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putString("categoria", "3");
                myEditor.commit();
                Intent intent = new Intent(PrincipalActivity.this, EncargoActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout llTransporte = (LinearLayout) findViewById(R.id.llTransporte);
        llTransporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, TransporteActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout llHoteleria = (LinearLayout) findViewById(R.id.llHoteleria);
        llHoteleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, HoteleriaActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout llPidelo = (LinearLayout) findViewById(R.id.llPidelo);
        llPidelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditor.putString("categoria", "4");
                myEditor.commit();
                Intent intent = new Intent(PrincipalActivity.this, PideloActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public int obtenerPosicionItem(Spinner spinner, String texto) {
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(texto)) {
                posicion = i;
            }
        }
        return posicion;
    }

    public int obtenerPosicionCiudadSeleccionada(String texto) {
        int posicion = 0;
        for (int i = 0; i < listCiudades.size(); i++) {
            if (texto.equals(listCiudades.get(i).getNombre())){
                posicion = listCiudades.get(i).getCodigo();

                //double latitudCiudadMapa = Globales.listCiudades.get(i).getLat();
                //Globales.longitudCiudadMapa = ;
                //Globales.radioCiudadMapa = Globales.listCiudades.get(i).getRadio();
               // Globales.precioBaseCiudadMapa = Globales.listCiudades.get(i).getPreciobase();
               // Globales.precioExtraCiudadMapa = Globales.listCiudades.get(i).getPrecioextra();

                myEditor.putString("latitudCiudadMapa", String.valueOf(listCiudades.get(i).getLat()));
                myEditor.putString("longitudCiudadMapa", String.valueOf(listCiudades.get(i).getLon()));
                myEditor.putString("radioCiudadMapa", String.valueOf(listCiudades.get(i).getRadio()));
                myEditor.putString("precioBaseCiudadMapa", String.valueOf(listCiudades.get(i).getPreciobase()));
                myEditor.putString("precioExtraCiudadMapa", String.valueOf(listCiudades.get(i).getPrecioextra()));
                myEditor.commit();
            }
        }
        //Devuelve un valor entero (si encontro una coincidencia devuelve la
        // posición 0 o N, de lo contrario devuelve 0 = posición inicial)
        return posicion;
    }

    public void listarPromociones(int codUbi){
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/PromocionesXUbicacion?auth="+tokencito+"&ubica="+String.valueOf(codUbi);
        EnviarRecibirDatosPromociones(consulta);
    }

    public void listaCiudades(){
        ArrayList<String> nameCiudades = new ArrayList<>();
        Globales globales = new Globales();
        listCiudades = globales.getDataFromSharedPreferences("lista_ciudades");

        for (int i=0; i<listCiudades.size();i++){
            nameCiudades.add(listCiudades.get(i).getNombre());
        }
        ArrayAdapter<String> adap = new ArrayAdapter<String>(PrincipalActivity.this,R.layout.spinner_vera, nameCiudades);
        adap.setDropDownViewResource(R.layout.spinner_vera);
        ciudades.setAdapter(adap);
        //mostrar la ciudad seleccionada en el Mapa
        ciudades.setSelection(obtenerPosicionItem(ciudades, ciudad));
        progDailog.dismiss();
    }

    public void EnviarRecibirDatosPromociones(String URL){
        progDailog = new ProgressDialog(PrincipalActivity.this);
        progDailog.setMessage("Cargando Promociones...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(PrincipalActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    txtPromociones.setVisibility(View.VISIBLE);
                    rvPromociones.setVisibility(View.VISIBLE);
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
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
                            list.add(promocion);
                        }
                        try {
                            rvPromociones.setLayoutManager(new LinearLayoutManager(PrincipalActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rvPromociones.setAdapter(new RecyclerAdapterPromociones(PrincipalActivity.this, list));
                            final GestureDetector mGestureDetector = new GestureDetector(PrincipalActivity.this, new GestureDetector.SimpleOnGestureListener(){
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
                                            Globales.PromocionPersonalizar = list.get(position);
                                            Intent intent = new Intent(PrincipalActivity.this,PersonalizaPromoActivity.class);
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
                            progDailog.dismiss();
                        } catch (Exception ex) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progDailog.dismiss();
                    }
                }
                else {
                    progDailog.dismiss();
                    txtPromociones.setVisibility(View.INVISIBLE);
                    rvPromociones.setVisibility(View.GONE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                    fondopromociones.setLayoutParams(lp);
                }
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
    public void onBackPressed (){
        PrincipalActivity.this.finishAffinity();
        startActivity(new Intent(getBaseContext(), CiudadActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    public void listarCategorias() throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarCategoriasEmpresas?auth=" + tokencito;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        try {
                            listCategorias = new ArrayList<>();
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject objeto = ja.getJSONObject(i);
                                EmpresaCategoria ec = new EmpresaCategoria();
                                ec.setCodigo(objeto.getInt("codigo"));
                                ec.setDescripcion(objeto.getString("descripcion"));
                                ec.setImagen(objeto.getString("imagen"));
                                ec.setEstado(objeto.getString("estado"));
                                listCategorias.add(ec);
                                if(ec.getDescripcion().equals("Restaurantes")){
                                    String urlRestaurantes = "https://fastbuych.com/empresas/categorias/imagenes/" + listCategorias.get(0).getImagen();
                                    CargarImagenCategoria(urlRestaurantes, imvRestaurants);
                                }
                                if(ec.getDescripcion().equals("Tiendas")){
                                    String urlMarket = "https://fastbuych.com/empresas/categorias/imagenes/" + ec.getImagen();
                                    CargarImagenCategoria(urlMarket, imvMarkets);
                                }
                            }
                        }
                        catch(Exception ex) {}
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public void CargarImagenCategoria(String urlImagen, ImageView imageView){
        Uri uri = Uri.parse(urlImagen);
        GlideApp.with(getApplicationContext())
            .load(uri)
                .centerCrop()
            .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.TOP))
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false; // important to return false so the error placeholder can be placed
                }
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            })
            .into(imageView);
    }
}
