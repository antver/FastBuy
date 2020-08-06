package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Operaciones.Pago_Delivery;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Operaciones;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.threatmetrix.TrustDefender.internal.A;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorCompletionService;

public class SeleccionaDireccionActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    ArrayList<Ubicaciones> list;
    ArrayList<String> listEtiquetas;
    DireccionListAdapter adapter = null;
    EtiquetaListAdapter adapterEtiqueta = null;
    GridView listaDirecciones, listaEtiquetas;
    String Etiqueta,LatitudSel,LongitudSel,CiudadDirecSel,DireccionSel,Piso,Refer;
    boolean newDirec = false;
    EditText txtNewDirec, txtNumPiso, txtReferencia;
    Button btnContinuar, btnCancelar;
    TextView btnAgregarDirec;
    LinearLayout linearSeleccionaDireccion, linearAgregarDireccion;
    String tokencito, number;
    LatLng ubiOrigin, ubiCiudadMapa;
    SharedPreferences myPreferences;
    Circle circulo;

    ProgressDialog progDailog = null;

    int codi=0;

    LocationManager mLocManager;
    Localizacion Local;
    String radioCiudadMapa;
    String sLongitud;

    public ArrayList<PedidoDetalle> listaPedidos;
    public ArrayList<Double> listaDistancias = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_direccion);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*0.90), (int)(alto*0.90));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        //name = myPreferences.getString("Name_Cliente", "");
        //formaPago = myPreferences.getString("formaPago", "");
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        String latitudCiudadMapa = myPreferences.getString("latitudCiudadMapa", "");
        String longitudCiudadMapa = myPreferences.getString("longitudCiudadMapa", "");
        radioCiudadMapa = myPreferences.getString("radioCiudadMapa", "");
        //ubicacion de la ciudad seleccionada
        ubiCiudadMapa = new LatLng(Double.parseDouble(latitudCiudadMapa), Double.parseDouble(longitudCiudadMapa));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Combo box de las direcciones
        final TextView cmbDireccion = (TextView) findViewById(R.id.cmbDireccion);
        listaDirecciones = (GridView) findViewById(R.id.listaDeDirecciones);
        cmbDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaDirecciones.setVisibility(View.VISIBLE);
            }
        });

        //Combo box de las etiquetas
        final TextView cmbEtiqueta = (TextView) findViewById(R.id.cmbEtiqueta);
        listaEtiquetas = (GridView) findViewById(R.id.listaDeEtiquetas);
        cmbEtiqueta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaEtiquetas.setVisibility(View.VISIBLE);
            }
        });

        //inicializando componentes
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnAgregarDirec = (TextView) findViewById(R.id.btnAgregarDireccion);
        //Linear selecciona y agregar
        linearSeleccionaDireccion = (LinearLayout) findViewById(R.id.linearSeleccionaDireccion);
        linearAgregarDireccion = (LinearLayout) findViewById(R.id.linearAgregaDirec);
        //final EditText txtNewEtiqueta = (EditText) findViewById(R.id.txtEtiquetaDireccion);
        txtNewDirec = (EditText) findViewById(R.id.txtNameDireccion);
        txtNumPiso = (EditText) findViewById(R.id.txtNumPiso);
        txtReferencia = (EditText) findViewById(R.id.txtReferencia);

        Activa();

        listarEtiquetas();

          //Start obteniendo coordenadas actuales
        //obtenerCoordenadas();
        //fin obteniendo coordenadas actuales

        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDirecciones_Cliente_XTelefono?auth="+tokencito+"&telefono="+number;
        EnviarRecibirDatos(consulta);
        if(!newDirec){
            if (Globales.isEncargo){
                Etiqueta = Globales.EtiquetaSeleccionada_Encargo;
            }else if(Globales.isExtra){
                //Etiqueta = Globales.EtiquetaSeleccionada_Extra;
            }else {
                Etiqueta = Globales.EtiquetaSeleccionada;
            }
        }else{
            Etiqueta = "";
        }
        cmbDireccion.setText(Etiqueta);

        listaDirecciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(EstablecimientoActivity.this, "Hola", Toast.LENGTH_SHORT).show();
                int codigo =(list.get(position).getCodigo());
                Etiqueta = list.get(position).getEtiqueta();
                LatitudSel = list.get(position).getLatitud();
                LongitudSel = list.get(position).getLongitud();
                CiudadDirecSel = list.get(position).getCiudad();
                DireccionSel = list.get(position).getDireccion();
/*
                if (Globales.isEncargo){
                    Globales.CodigoDireccionSeleccionada_Encargo = codigo;
                    Globales.EtiquetaSeleccionada_Encargo = Etiqueta;
                    Globales.LatitudSeleccionada_Encargo = LatitudSel;
                    Globales.LongitudSeleccionada_Encargo = LongitudSel;
                    Globales.CiudadDireccionSeleccionada_Encargo = CiudadDirecSel;
                    Globales.DireccionSeleccionada_Encargo = DireccionSel;
                }else if(Globales.isExtra){
                    Globales.CodigoDireccionSeleccionada_Extra = codigo;
                    Globales.EtiquetaSeleccionada_Extra = Etiqueta;
                    Globales.LatitudSeleccionada_Extra = LatitudSel;
                    Globales.LongitudSeleccionada_Extra = LongitudSel;
                    Globales.CiudadDireccionSeleccionada_Extra = CiudadDirecSel;
                    Globales.DireccionSeleccionada_Extra = DireccionSel;
                }else {
                    Globales.CodigoDireccionSeleccionada = codigo;
                    Globales.EtiquetaSeleccionada = Etiqueta;
                    Globales.LatitudSeleccionada = LatitudSel;
                    Globales.LongitudSeleccionada = LongitudSel;
                    Globales.CiudadDireccionSeleccionada = CiudadDirecSel;
                    Globales.DireccionSeleccionada = DireccionSel;
                }*/

                //motrando seleccion
                cmbDireccion.setText(Etiqueta);
                //ocultando lista
                listaDirecciones.setVisibility(View.GONE);
            }
        });

        listaEtiquetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listEtiquetas.get(position).equals("Otro")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionaDireccionActivity.this);
                    builder.setTitle("Etiqueta Personalizada");

                    // Set up the input
                    final EditText input = new EditText(SeleccionaDireccionActivity.this);
                    input.setHint(R.string.text_etiqueta_personalizada);
                    //input.setBackgroundResource(R.drawable.caja_texto_input);
                    input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(200)});//200
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setGravity(Gravity.BOTTOM);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Etiqueta = input.getText().toString();
                            //motrando seleccion
                            cmbEtiqueta.setText(Etiqueta);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //motrando seleccion
                            cmbEtiqueta.setText("");
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }else{
                    Etiqueta = listEtiquetas.get(position);
                    cmbEtiqueta.setText(Etiqueta);
                }

                //ocultando lista
                listaEtiquetas.setVisibility(View.GONE);
            }
        });

        //Boton Continuar
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newDirec){
                    float[] disResultado = new float[2];

                    Location.distanceBetween( Double.valueOf(LatitudSel), Double.valueOf(LongitudSel),
                            circulo.getCenter().latitude,
                            circulo.getCenter().longitude,
                            disResultado);

                    if(disResultado[0] < circulo.getRadius()){
                        btnContinuar.setEnabled(false);
                        try {
                            if (cmbEtiqueta.getText().toString().trim().length()>0
                                    && txtNewDirec.getText().toString().trim().length()>0
                                    && txtNumPiso.getText().toString().trim().length()>0
                                    && txtReferencia.getText().toString().trim().length()>0){
                                DireccionSel = txtNewDirec.getText().toString();
                                Piso = txtNumPiso.getText().toString();
                                Refer = txtReferencia.getText().toString();

                                if (Globales.isEncargo){
                                    //Globales.CodigoDireccionSeleccionada_Encargo = codigo;
                                    Globales.EtiquetaSeleccionada_Encargo = Etiqueta;
                                    Globales.LatitudSeleccionada_Encargo = LatitudSel;
                                    Globales.LongitudSeleccionada_Encargo = LongitudSel;
                                    //Globales.CiudadDireccionSeleccionada_Encargo = CiudadDirecSel;
                                    //Globales.DireccionSeleccionada_Encargo = DireccionSel+" - "+ Piso+" - "+Refer;
                                }else if(Globales.isExtra){
                                    /*Globales.EtiquetaSeleccionada_Extra = Etiqueta;
                                    Globales.LatitudSeleccionada_Extra = LatitudSel;
                                    Globales.LongitudSeleccionada_Extra = LongitudSel;
                                    Globales.DireccionSeleccionada_Extra = DireccionSel+" - "+ Piso+" - "+Refer;*/
                                }else{
                                    Globales.EtiquetaSeleccionada = Etiqueta;
                                    Globales.LatitudSeleccionada = LatitudSel;
                                    Globales.LongitudSeleccionada = LongitudSel;
                                   // Globales.DireccionSeleccionada = DireccionSel+" - "+ Piso+" - "+Refer;
                                }

                                registrarDireccion(Etiqueta,DireccionSel,LatitudSel,LongitudSel,Piso, Refer);
                            }else {
                                btnContinuar.setEnabled(true);
                                Toast.makeText(SeleccionaDireccionActivity.this, "Por Favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                            }

                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(SeleccionaDireccionActivity.this, "Ubicación, fuera de los límites establecidos", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    btnContinuar.setEnabled(true);
                    if(Etiqueta.length()>0){
                        calculaDelivery();
                        onBackPressed();
                    }
                    else{
                        Toast.makeText(SeleccionaDireccionActivity.this, "No selecciono una dirección", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //boton agregar

        btnAgregarDirec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerCoordenadas();
                //Etiqueta limpiamos
                Etiqueta = "";
                cmbEtiqueta.setText(Etiqueta);
                Globales.EtiquetaSeleccionada_Encargo = "";
               //Globales.EtiquetaSeleccionada_Extra = "";
                Globales.EtiquetaSeleccionada = "";
                Desactiva();
            }
        });

        //boton Cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Etiqueta limpiamos
                Etiqueta = "";
                cmbDireccion.setText(Etiqueta);
                cmbEtiqueta.setText(Etiqueta);
                Globales.montoDelivery =0;
                Globales.montoDeliveryPidelo =0;
                Globales.montoDeliveryEncargo =0;
                Globales.EtiquetaSeleccionada_Encargo = "";
               // Globales.EtiquetaSeleccionada_Extra = "";
                Globales.EtiquetaSeleccionada = "";
                Activa();
            }
        });

    }

    public void calculaDelivery(){
        if (Globales.isEncargo){
            //Globales.montoDeliveryEncargo = 4.00;
            //Globales.montoDeliveryEncargo = Globales.precioExtraCiudadMapa;
        }else if(Globales.isExtra){
            //Globales.montoDeliveryPidelo = 4.00;
            //Globales.montoDeliveryPidelo = Globales.precioExtraCiudadMapa;
        }else{
            //Start logica para calcular el costo de envio
            Operaciones operaciones = new Operaciones();
            listaPedidos = Globales.listaPedidos;
            listaDistancias.clear();
            for (int i = 0; i < listaPedidos.size(); i++) {
                double latitud = listaPedidos.get(i).getLatitud();
                double longitud = listaPedidos.get(i).getLongitud();
                double distancia = operaciones.calcularDistancia(Double.valueOf(Globales.LatitudSeleccionada), Double.valueOf(Globales.LongitudSeleccionada), latitud, longitud);
                listaDistancias.add(distancia);
            }
            double numeromayor = 0;
            for (double distancia: listaDistancias){
                if(distancia > numeromayor){
                    numeromayor = distancia;
                }
            }
            Pago_Delivery delivery = new Pago_Delivery();
            //double costoEnvio = Math.round(delivery.calcularCostoEnvio(numeromayor)*Math.pow(10,2))/Math.pow(10,2);
            String distanciaBase = myPreferences.getString("distanciabase", "");
            String tarifabase = myPreferences.getString("precioBaseCiudadMapa", "");
            double costoEnvio = new BigDecimal(delivery.calcularCostoEnvio(numeromayor, Double.parseDouble(tarifabase), Double.parseDouble(distanciaBase))).setScale(1, RoundingMode.HALF_UP).doubleValue();
            Globales.montoDelivery = costoEnvio;
            Globales.deliveryTemporal = costoEnvio;

            if(Globales.recoger_en_tienda == true){
                Globales.montoDelivery = 0;
            }
            else{
                Globales.montoDelivery = Globales.deliveryTemporal;
                //costoEnvio = Globales.deliveryTemporal;
            }
            Log.v("costoEnvio", String.format("%.2f",costoEnvio));
            //End logica para calcular el costo de envio
        }
    }

    public void registrarDireccion(String a, String b, String c, String d, String e, String f) throws UnsupportedEncodingException {
        try {
            String etiqueta = URLEncoder.encode(a, "UTF-8");
            String direccion = URLEncoder.encode(b, "UTF-8");
            String latitud = URLEncoder.encode(c, "UTF-8");
            String longitud = URLEncoder.encode(d, "UTF-8");
            String numPiso = URLEncoder.encode(e, "UTF-8");
            String referen = URLEncoder.encode(f, "UTF-8");
            String URL = "https://apifbdelivery.fastbuych.com/Delivery/GuardarDireccion2?auth="+tokencito+"&numTelefono="+number+"&etiqueta="+etiqueta+"&direccion="+direccion+"&latitud="+latitud+"&longitud="+longitud+"&piso="+numPiso+"&referencia="+referen;
            Log.v("newDirec",URL);
            RequestQueue queue = Volley.newRequestQueue(SeleccionaDireccionActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    btnContinuar.setEnabled(true);
                    if(response.equals("ERROR")){
                        Toast.makeText(SeleccionaDireccionActivity.this, "Error al registrar dirección", Toast.LENGTH_SHORT).show();
                        //regDirec = false;
                        Globales.montoDelivery = 0;
                        Globales.montoDeliveryPidelo = 0;
                        Globales.montoDeliveryEncargo = 0;
                        Globales.EtiquetaSeleccionada_Encargo = "";
                        //Globales.EtiquetaSeleccionada_Extra = "";
                        Globales.EtiquetaSeleccionada = "";
                    }
                    else {
                        if (response.length()>0){
                            try {
                                JSONObject objeto = new JSONObject(response);
                                if (Globales.isEncargo){
                                    Globales.CodigoDireccionSeleccionada_Encargo = objeto.getInt("Codigo_Direc");
                                    Globales.CiudadDireccionSeleccionada_Encargo = objeto.getString("Ciudad_Direc");
                                }else if(Globales.isExtra){
                                    //Globales.CodigoDireccionSeleccionada_Extra = objeto.getInt("Codigo_Direc");
                                    //Globales.CiudadDireccionSeleccionada_Extra = objeto.getString("Ciudad_Direc");
                                }else{
                                    Globales.CodigoDireccionSeleccionada = objeto.getInt("Codigo_Direc");
                                    Globales.CiudadDireccionSeleccionada = objeto.getString("Ciudad_Direc");
                                }
                                //regDirec = true;
                                calculaDelivery();
                                onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Globales.CodigoDireccionSeleccionada_Encargo = -1;
                            //Globales.CodigoDireccionSeleccionada_Extra = -1;
                            Globales.CodigoDireccionSeleccionada = -1;
                        }
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(stringRequest);

        }catch (NullPointerException np){
            np.printStackTrace();
            onBackPressed();
        }

    }

    public void Activa(){
        newDirec = false;
        btnAgregarDirec.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.GONE);
        linearSeleccionaDireccion.setVisibility(View.VISIBLE);
        linearAgregarDireccion.setVisibility(View.GONE);
    }

    public void Desactiva(){
        newDirec = true;
        btnAgregarDirec.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.VISIBLE);
        linearSeleccionaDireccion.setVisibility(View.GONE);
        linearAgregarDireccion.setVisibility(View.VISIBLE);
    }

    public void listarEtiquetas(){
        listEtiquetas = new ArrayList<String>();
        listEtiquetas.add("Mi Casa");
        listEtiquetas.add("Mi Trabajo");
        listEtiquetas.add("Mi Pareja");
        listEtiquetas.add("Otro");

        listaEtiquetas.setNumColumns(1);
        adapterEtiqueta = new EtiquetaListAdapter(SeleccionaDireccionActivity.this, R.layout.list_direcciones_item, listEtiquetas);
        listaEtiquetas.setAdapter(adapterEtiqueta);
        Log.v("etiquetas", "pasé");
    }

    public void EnviarRecibirDatos(String URL){
        RequestQueue queue = Volley.newRequestQueue(SeleccionaDireccionActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        list = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Ubicaciones ubicacion = new Ubicaciones();
                            ubicacion.setCodigo(objeto.getInt("CD_Codigo"));
                            ubicacion.setCiudad(objeto.getString("CD_Ciudad"));
                            ubicacion.setDireccion(objeto.getString("CD_Direccion")+" - "+objeto.getString("CD_NumPiso")+" - "+objeto.getString("CD_Referencia"));
                            ubicacion.setLatitud(objeto.getString("CD_Latitud"));
                            ubicacion.setLongitud(objeto.getString("CD_Longitud"));
                            ubicacion.setEtiqueta(objeto.getString("CD_Etiqueta"));
                            list.add(ubicacion);
                        }

                        listaDirecciones.setNumColumns(1);
                        adapter = new DireccionListAdapter(SeleccionaDireccionActivity.this, R.layout.list_direcciones_item, list);
                        listaDirecciones.setAdapter(adapter);

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

    public void obtenerCoordenadas(){
        //Verifica que el gps esté activado
        if(ActivityCompat.checkSelfPermission(SeleccionaDireccionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SeleccionaDireccionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SeleccionaDireccionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            progDailog = new ProgressDialog(SeleccionaDireccionActivity.this);
            progDailog.setMessage("Obteniendo Coordenadas...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(false);
            progDailog.show();
            locationStart();
        }
    }

    private void locationStart(){
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Local = new Localizacion();
        Local.setSeleccionaDireccionActivity(SeleccionaDireccionActivity.this);
        final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(SeleccionaDireccionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SeleccionaDireccionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SeleccionaDireccionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
            return;
        }

        mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, (LocationListener) Local);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (LocationListener) Local);
        //Hasta aquí la localizacion debió de ser agregada
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(SeleccionaDireccionActivity.this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    int cant = DirCalle.getAddressLine(0).split(",").length;
                    int tam = DirCalle.getAddressLine(0).split(",")[cant - 2].length();
                    CiudadDirecSel = DirCalle.getAddressLine(0).split(",")[cant - 2].substring(1, tam);
                    txtNewDirec.setText(DirCalle.getAddressLine(0));
                    progDailog.dismiss();
                }
            } catch (IOException e) {
                progDailog.dismiss();
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        SeleccionaDireccionActivity seleccionaDireccionActivity;
        public SeleccionaDireccionActivity getSeleccionaDireccionActivity() {
            return seleccionaDireccionActivity;
        }
        public void setSeleccionaDireccionActivity(SeleccionaDireccionActivity seleccionaDireccionActivity) {
            this.seleccionaDireccionActivity = seleccionaDireccionActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String sLatitud = String.valueOf(loc.getLatitude());
            sLongitud = String.valueOf(loc.getLongitude());
            ubiOrigin = new LatLng(loc.getLatitude(),loc.getLongitude());
            LatitudSel = sLatitud;
            LongitudSel = sLongitud;

            //esta linea debería de detener la escucha del GPS
            if (sLongitud!="" && sLongitud != null )
                mLocManager.removeUpdates((LocationListener) Local);

            miUbicacion(Double.parseDouble(LatitudSel), Double.parseDouble(LongitudSel));
            if(progDailog != null)
                progDailog.dismiss();
            //this.seleccionaDireccionActivity.setLocation(loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.v("StatusDebug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.v("StatusDebug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.v("StatusDebug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            //LatLng sydney = new LatLng(Double.parseDouble(LatitudSel), Double.parseDouble(LongitudSel));
            mMap.setOnMapClickListener(this);
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {

        }
    }

    public void ubicacionOriginal(View view) {
        try {
            mMap.clear();
            miCirculo(ubiCiudadMapa);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubiOrigin, 16));
            mMap.setMaxZoomPreference(16);
            mMap.addMarker(new MarkerOptions()
                    .position(ubiOrigin)
                    .title("Ubicación Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        } catch (Exception e) {

        }
    }
    @Override public void onMapClick(LatLng puntoPulsado) {
        mMap.clear();
        miCirculo(ubiCiudadMapa);
        mMap.addMarker(new MarkerOptions().position(puntoPulsado)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        LatitudSel = String.valueOf(puntoPulsado.latitude);
        LongitudSel = String.valueOf(puntoPulsado.longitude);
        //txtNewDirec.setText(LatitudSel+"-"+LongitudSel);
    }

    public void miUbicacion(double lat, double lon){
        try {
            mMap.clear();
            miCirculo(ubiCiudadMapa);
            LatLng miUbi = new LatLng(lat,lon);
            mLocManager.removeUpdates((LocationListener) Local);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbi, 16));
            mMap.setMaxZoomPreference(16);
            mMap.addMarker(new MarkerOptions()
                    .position(miUbi)
                    .title("Ubicación Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //mMap.moveCamera(CameraUpdateFactory.zoomBy(14));
            /*mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbi));*/
        }
        catch (Exception e){

        }
    }

    public void miCirculo(LatLng x) {
        CircleOptions opcionesCirculo = new CircleOptions().center(x).radius(Double.parseDouble(radioCiudadMapa));
        circulo = mMap.addCircle(opcionesCirculo);
        circulo.setFillColor(Color.argb(35,1, 196, 164));
        circulo.setStrokeColor(Color.rgb(1, 196, 164));
        circulo.setStrokeWidth(4f);
        Log.v("TAG", "Circulo cargado en el mapa.");
    }
}
