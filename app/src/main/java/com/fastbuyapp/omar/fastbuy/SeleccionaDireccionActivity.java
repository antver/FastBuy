package com.fastbuyapp.omar.fastbuy;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Operaciones.Pago_Delivery;
import com.fastbuyapp.omar.fastbuy.adaptadores.DireccionListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Operaciones;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    SharedPreferences.Editor myEditor;
    ProgressDialog progDailog = null;

    int codi=0;
    CheckBox ckRecogerEnTienda;
    LocationManager mLocManager;
    Localizacion Local;
    String radioCiudadMapa;
    String sLongitud, categoria;
    boolean recoger_entienda;
    public ArrayList<Double> listaDistancias = new ArrayList<Double>();
    String latitudCiudadMapa, longitudCiudadMapa;
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
        myEditor = myPreferences.edit();
        number = myPreferences.getString("Number_Cliente", "");
        tokencito = myPreferences.getString("tokencito", "");
        latitudCiudadMapa = myPreferences.getString("latitudCiudadMapa", "");
        longitudCiudadMapa = myPreferences.getString("longitudCiudadMapa", "");
        radioCiudadMapa = myPreferences.getString("radioCiudadMapa", "");
        recoger_entienda  = myPreferences.getBoolean("recoger_entienda", false);
        categoria = myPreferences.getString("categoria", "");
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
        ckRecogerEnTienda = (CheckBox) findViewById(R.id.ckRecogerEnTienda);
        if(categoria.equals("1") || categoria.equals("2")){
            ckRecogerEnTienda.setVisibility(View.VISIBLE);
            ckRecogerEnTienda.setChecked(recoger_entienda);
        }
        if(categoria.equals("3") || categoria.equals("4")){
            ckRecogerEnTienda.setVisibility(View.GONE);
            ckRecogerEnTienda.setChecked(false);
        }
        Activa();
        listarEtiquetas();

        String consulta = "https://apifbdelivery.fastbuych.com/Delivery/ListarDirecciones_Cliente_XTelefono?auth="+tokencito+"&telefono="+number;
        EnviarRecibirDatos(consulta);
        Etiqueta = myPreferences.getString("etiqueta_direccion", "");
        DireccionSel = myPreferences.getString("direccion_seleccionada", "");
        if(DireccionSel.equals("")){
            cmbDireccion.setText("");
        }
        else{
            cmbDireccion.setText(Etiqueta + " ("+ DireccionSel + ")");
        }
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
                myEditor.putString("etiqueta_direccion", Etiqueta);
                myEditor.putString("direccion_seleccionada", DireccionSel);
                myEditor.putString("ciudad_seleccionada", CiudadDirecSel);
                myEditor.putString("latitud_seleccionada", LatitudSel);
                myEditor.putString("longitud_seleccionada", LongitudSel);
                myEditor.commit();
                calculaDelivery();
                //motrando seleccion
                cmbDireccion.setText(Etiqueta + " ("+ DireccionSel + ")");
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

        ckRecogerEnTienda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ckRecogerEnTienda.isChecked()){
                    cmbDireccion.setEnabled(false);
                    myEditor.putBoolean("recoger_entienda", true);
                    myEditor.commit();
                }else{
                    cmbDireccion.setEnabled(true);
                    myEditor.putBoolean("recoger_entienda", false);
                    myEditor.commit();
                }
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
                Activa();
            }
        });

    }

    public void calculaDelivery(){

            //Start logica para calcular el costo de envio
            Operaciones operaciones = new Operaciones();
            listaDistancias.clear();
            Globales globales = new Globales();
            ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
            for (int i = 0; i < listapedidos.size(); i++) {
                double latitud = listapedidos.get(i).getLatitud();
                double longitud = listapedidos.get(i).getLongitud();
                String latitud_seleccionada = myPreferences.getString("latitud_seleccionada", "0");
                String longitud_seleccionada = myPreferences.getString("longitud_seleccionada", "0");
                double distancia = operaciones.calcularDistancia(Double.valueOf(latitud_seleccionada), Double.valueOf(longitud_seleccionada), latitud, longitud);
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
            String tarifabase = myPreferences.getString("precioBaseCiudadMapa", "3");
            String tarifabaseencargo = myPreferences.getString("precioExtraCiudadMapa", "20");
            double tarifa = 0;
            if(categoria.equals("1") || categoria.equals("2")){
                tarifa = Double.parseDouble(tarifabase);
            }
            if(categoria.equals("3") || categoria.equals("4")){
                tarifa = Double.parseDouble(tarifabaseencargo);
            }
            double costoEnvio = new BigDecimal(delivery.calcularCostoEnvio(numeromayor, tarifa, Double.parseDouble(distanciaBase))).setScale(1, RoundingMode.HALF_UP).doubleValue();
            myEditor.putFloat( "monto_delivery", Float.parseFloat(String.valueOf(costoEnvio)));
            myEditor.commit();
            //End logica para calcular el costo de envio
    }

    public void registrarDireccion(String a, String b, String c, String d, String e, String f) throws UnsupportedEncodingException {
        try {
            final String etiqueta = URLEncoder.encode(a, "UTF-8");
            final String direccion = URLEncoder.encode(b, "UTF-8");
            final String latitud = URLEncoder.encode(c, "UTF-8");
            final String longitud = URLEncoder.encode(d, "UTF-8");
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
                        myEditor.putString("etiqueta_direccion", "");
                        myEditor.putString("direccion_seleccionada", "");
                        myEditor.putString("ciudad_seleccionada", "");
                        myEditor.putString("latitud_seleccionada", "0");
                        myEditor.putString("longitud_seleccionada", "0");
                        myEditor.commit();
                    }
                    else {
                        if (response.length()>0){
                            try {
                                JSONObject objeto = new JSONObject(response);
                                //myEditor.putString("ciudad_seleccionada", objeto.getString("Codigo_Direc"));
                                myEditor.putString("ciudad_seleccionada", objeto.getString("Ciudad_Direc"));
                                myEditor.putString("etiqueta_direccion", etiqueta);
                                myEditor.putString("direccion_seleccionada", direccion);
                                myEditor.putString("latitud_seleccionada", latitud);
                                myEditor.putString("longitud_seleccionada", longitud);
                                myEditor.commit();
                                myEditor.commit();
                                calculaDelivery();
                                onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //myEditor.putString("ciudad_seleccionada", "");
                            myEditor.putString("ciudad_seleccionada", "");
                            myEditor.commit();
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
        listaEtiquetas.setAdapter(adapterEtiqueta);setGridViewHeightBasedOnChildren(listaEtiquetas, 1);
        //Log.v("etiquetas", "pasé");
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
    }
}
