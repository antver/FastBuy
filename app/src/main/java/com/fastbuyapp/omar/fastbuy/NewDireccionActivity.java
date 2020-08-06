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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewDireccionActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    SharedPreferences myPreferences;
    String Latitud,Longitud;
    EditText txtNewDirec, txtNumPiso, txtReferencia;

    ArrayList<String> listEtiquetas;
    EtiquetaListAdapter adapterEtiqueta = null;

    GridView listaEtiquetas;
    String Etiqueta,LatitudSel,LongitudSel,DireccionSel,Piso,Refer, tokencito, celular, radioCiudadMapa;

    ProgressDialog progDailog = null;
    Button btnRegistrar,btnCancelar;
    LocationManager mLocManager;
    NewDireccionActivity.Localizacion Local;

    Circle circulo;

    LatLng ubiOrigin, ubiCiudadMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_direccion);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*0.90), (int)(alto*0.90));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        //ubicacion de la ciudad seleccionada
        String latitudCiudadMapa = myPreferences.getString("latitudCiudadMapa", "");
        String longitudCiudadMapa = myPreferences.getString("latitudCiudadMapa", "");
        tokencito = myPreferences.getString("tokencito", "");
        celular = myPreferences.getString("Number_Cliente", "");
        radioCiudadMapa = myPreferences.getString("radioCiudadMapa", "");
        ubiCiudadMapa = new LatLng(Double.parseDouble(latitudCiudadMapa), Double.parseDouble(longitudCiudadMapa));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtNewDirec = (EditText) findViewById(R.id.txtNameDireccion);
        //Combo box de las etiquetas
        final TextView cmbEtiqueta = (TextView) findViewById(R.id.cmbEtiqueta);
        listaEtiquetas = (GridView) findViewById(R.id.listaDeEtiquetas);
        cmbEtiqueta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaEtiquetas.setVisibility(View.VISIBLE);
            }
        });

        listarEtiquetas();

        txtNumPiso = (EditText) findViewById(R.id.txtNumPiso);
        txtReferencia = (EditText) findViewById(R.id.txtReferencia);

        btnRegistrar = (Button) findViewById(R.id.btnRegistrarDirec);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listaEtiquetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listEtiquetas.get(position).equals("Otro")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewDireccionActivity.this);
                    builder.setTitle("Etiqueta Personalizada");

                    // Set up the input
                    final EditText input = new EditText(NewDireccionActivity.this);
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

        obtenerCoordenadas();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] disResultado = new float[2];

                Location.distanceBetween( Double.valueOf(LatitudSel), Double.valueOf(LongitudSel),
                        circulo.getCenter().latitude,
                        circulo.getCenter().longitude,
                        disResultado);

                if(disResultado[0] < circulo.getRadius()){
                    btnRegistrar.setEnabled(false);
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
                            btnRegistrar.setEnabled(true);
                            Toast.makeText(NewDireccionActivity.this, "Por Favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                        }

                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(NewDireccionActivity.this, "Ubicación, fuera de los límites establecidos", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void registrarDireccion(String a, String b, String c, String d, String e, String f) throws UnsupportedEncodingException {
        try {
            String etiqueta = URLEncoder.encode(a, "UTF-8");
            String direccion = URLEncoder.encode(b, "UTF-8");
            String latitud = URLEncoder.encode(c, "UTF-8");
            String longitud = URLEncoder.encode(d, "UTF-8");
            String numPiso = URLEncoder.encode(e, "UTF-8");
            String referen = URLEncoder.encode(f, "UTF-8");
            String URL = "https://apifbdelivery.fastbuych.com/Delivery/GuardarDireccion2?auth="+tokencito+"&numTelefono="+celular+"&etiqueta="+etiqueta+"&direccion="+direccion+"&latitud="+latitud+"&longitud="+longitud+"&piso="+numPiso+"&referencia="+referen;
            Log.v("newDirec",URL);
            RequestQueue queue = Volley.newRequestQueue(NewDireccionActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    btnRegistrar.setEnabled(true);
                    if(response.equals("ERROR")){
                        Toast.makeText(NewDireccionActivity.this, "Error al registrar dirección", Toast.LENGTH_SHORT).show();
                    }else {
                        if (response.length()>0){
                            try {
                                JSONObject objeto = new JSONObject(response);
                                int CodigoDireccion = objeto.getInt("Codigo_Direc");
                                String CiudadDireccion = objeto.getString("Ciudad_Direc");
                                onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public void listarEtiquetas(){
        listEtiquetas = new ArrayList<String>();
        listEtiquetas.add("Mi Casa");
        listEtiquetas.add("Mi Trabajo");
        listEtiquetas.add("Mi Pareja");
        listEtiquetas.add("Otro");

        listaEtiquetas.setNumColumns(1);
        adapterEtiqueta = new EtiquetaListAdapter(NewDireccionActivity.this, R.layout.list_direcciones_item, listEtiquetas);
        listaEtiquetas.setAdapter(adapterEtiqueta);
    }

    public void obtenerCoordenadas(){
        //Verifica que el gps esté activado
        if(ActivityCompat.checkSelfPermission(NewDireccionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(NewDireccionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NewDireccionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            progDailog = new ProgressDialog(NewDireccionActivity.this);
            progDailog.setMessage("Obteniendo Coordenadas...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(false);
            progDailog.show();
            locationStart();
        }
    }

    private void locationStart(){
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Local = new NewDireccionActivity.Localizacion();
        Local.setNewDireccionActivity(NewDireccionActivity.this);
        final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(NewDireccionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(NewDireccionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NewDireccionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
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

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        NewDireccionActivity newDireccionActivity;
        public NewDireccionActivity getNewDireccionActivity() {
            return newDireccionActivity;
        }
        public void setNewDireccionActivity(NewDireccionActivity newDireccionActivity) {
            this.newDireccionActivity = newDireccionActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            Latitud = String.valueOf(loc.getLatitude());
            Longitud = String.valueOf(loc.getLongitude());

            ubiOrigin = new LatLng(loc.getLatitude(),loc.getLongitude());
            LatitudSel = Latitud;
            LongitudSel = Longitud;

            //esta linea debería de detener la escucha del GPS
            if (Longitud!="" && Longitud != null )
                mLocManager.removeUpdates((LocationListener) Local);

            miUbicacion(Double.parseDouble(LatitudSel), Double.parseDouble(LongitudSel));
            progDailog.dismiss();
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
            // Este metodo se ejecuta cuando el GPS es desactivado
            /*Toast toast = Toast.makeText(SeleccionaDireccionActivity.this,"POR FAVOR ACTIVE EL GPS", Toast.LENGTH_SHORT);
            View vistaToast =toast.getView();
            vistaToast.setBackgroundResource(R.drawable.toast_yellow);
            toast.show();*/
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMyLocationEnabled(false);
        }
    }

    public void ubicacionOriginal(View view) {
        mMap.clear();
        miCirculo(ubiCiudadMapa);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubiOrigin, 18));
        mMap.addMarker(new MarkerOptions()
                .position(ubiOrigin)
                .title("Ubicación Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override public void onMapClick(LatLng puntoPulsado) {
        mMap.clear();
        miCirculo(ubiCiudadMapa);
        mMap.addMarker(new MarkerOptions().position(puntoPulsado)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        LatitudSel = String.valueOf(puntoPulsado.latitude);
        LongitudSel = String.valueOf(puntoPulsado.longitude);
    }

    public void miUbicacion(double lat, double lon){
        mMap.clear();
        miCirculo(ubiCiudadMapa);
        LatLng miUbi = new LatLng(lat,lon);
        mLocManager.removeUpdates((LocationListener) Local);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbi, 18));
        mMap.addMarker(new MarkerOptions()
                .position(miUbi)
                .title("Ubicación Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
