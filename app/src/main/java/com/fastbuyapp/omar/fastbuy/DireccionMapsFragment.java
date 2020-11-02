package com.fastbuyapp.omar.fastbuy;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DireccionMapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DireccionMapsFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    Localizacion Local;
    SharedPreferences myPreferences;
    LocationManager mLocManager;
    private GoogleMap mMap;
    String Etiqueta;
    String LatitudSel;
    String LongitudSel;
    String CiudadDirecSel;
    String DireccionSel;
    String Piso;
    String Refer;
    SharedPreferences.Editor myEditor;
    ProgressDialog progDailog = null;
    String sLongitud;
    LatLng ubiOrigin, ubiCiudadMapa;
    String latitudCiudadMapa, longitudCiudadMapa, ciudad_seleccionada, tokencito, number;
    EditText txtNewDirec, txtNumPiso, txtReferencia;
    String codigoZona;
    ImageView btnAtras;
    public DireccionMapsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DireccionMapsFragment newInstance() {
        DireccionMapsFragment fragment = new DireccionMapsFragment();
         
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_direccionmaps);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*1), (int)(alto*1));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();

        tokencito = myPreferences.getString("tokencito", "");
        latitudCiudadMapa = myPreferences.getString("latitudCiudadMapa", "");
        longitudCiudadMapa = myPreferences.getString("longitudCiudadMapa", "");
        number = myPreferences.getString("Number_Cliente", "");
        txtNewDirec = (EditText) findViewById(R.id.txtNameDireccion);
        txtNumPiso = (EditText) findViewById(R.id.txtNumPiso);
        txtReferencia = (EditText) findViewById(R.id.txtReferencia);
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        obtenerCoordenadas();
        //ubiCiudadMapa = new LatLng(Double.parseDouble(latitudCiudadMapa), Double.parseDouble(longitudCiudadMapa));
        Button btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                try {
                    if (txtNewDirec.getText().toString().trim().length()>0
                            && txtNumPiso.getText().toString().trim().length()>0
                            && txtReferencia.getText().toString().trim().length()>0){
                        DireccionSel = txtNewDirec.getText().toString();
                        Piso = txtNumPiso.getText().toString();
                        Refer = txtReferencia.getText().toString();

                        registrarDireccion("",DireccionSel,LatitudSel,LongitudSel,Piso, Refer);
                    }else {
                        Toast.makeText(DireccionMapsFragment.this, "Por Favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                    }

                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }

        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String direccion = myPreferences.getString("direccion_seleccionada", "");
                if(!direccion.equals("codigoZona_usuario"))
                    onBackPressed();
                else
                    Toast.makeText(DireccionMapsFragment.this, "Seleccione una ubicación donde FastBuy brinde cobertura", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrarDireccion(final String a, final String b, final String c, final String d, String e, String f) throws UnsupportedEncodingException {
        try {
            final String etiqueta = URLEncoder.encode(a, "UTF-8");
            final String direccion = URLEncoder.encode(b, "UTF-8");
            final String latitud = URLEncoder.encode(c, "UTF-8");
            final String longitud = URLEncoder.encode(d, "UTF-8");
            //final String ciudad = URLEncoder.encode(g, "UTF-8");
            String numPiso = URLEncoder.encode(e, "UTF-8");
            String referen = URLEncoder.encode(f, "UTF-8");
            String URL = "https://apifbdelivery.fastbuych.com/Delivery/GuardarDireccion3?auth="+tokencito+"&numTelefono="+number+"&etiqueta=a&direccion="+direccion+"&latitud="+latitud+"&longitud="+longitud+"&piso="+numPiso+"&referencia="+referen+"&ciudad=1";
            Log.v("newDirec",URL);
            RequestQueue queue = Volley.newRequestQueue(DireccionMapsFragment.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.equals("ERROR")){
                        Toast.makeText(DireccionMapsFragment.this, "Error al registrar dirección", Toast.LENGTH_SHORT).show();
                        myEditor.putString("etiqueta_direccion", "");

                        //myEditor.putString("ciudad_seleccionada", "");
                        myEditor.putString("latitud_seleccionada", "0");
                        myEditor.putString("longitud_seleccionada", "0");
                        myEditor.commit();
                    }
                    else {
                        if (response.length()>0){
                            try {
                                JSONObject objeto = new JSONObject(response);
                                //myEditor.putString("ciudad_seleccionada", objeto.getString("Codigo_Direc"));
                                //myEditor.putString("ciudad_seleccionada", objeto.getString("Ciudad_Direc"));
                                myEditor.putString("codigoZona_usuario", codigoZona);
                                myEditor.putString("etiqueta_direccion", a);
                                myEditor.putString("direccion_seleccionada", b);
                                myEditor.putString("latitud_seleccionada", c);
                                myEditor.putString("longitud_seleccionada", d);
                                myEditor.commit();
                                onBackPressed();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //myEditor.putString("ciudad_seleccionada", "");
                            //myEditor.putString("ciudad_seleccionada", "");
                            //myEditor.commit();
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
    @Override
    public void onMapClick(LatLng puntoPulsado) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(puntoPulsado).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        LatitudSel = String.valueOf(puntoPulsado.latitude);
        LongitudSel = String.valueOf(puntoPulsado.longitude);
        miUbicacion(Double.parseDouble(LatitudSel), Double.parseDouble(LongitudSel));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            //LatLng sydney = new LatLng(Double.parseDouble(LatitudSel), Double.parseDouble(LongitudSel));
            mMap.setOnMapClickListener(this);
            if (ActivityCompat.checkSelfPermission(DireccionMapsFragment.this,
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
           // miCirculo(ubiCiudadMapa);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubiOrigin, 19));
            mMap.setMaxZoomPreference(19);
            mMap.addMarker(new MarkerOptions()
                    .position(ubiOrigin)
                    .title("Ubicación Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        } catch (Exception e) {

        }
    }
    public void obtenerCoordenadas(){
        //Verifica que el gps esté activado
        if(ActivityCompat.checkSelfPermission(DireccionMapsFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DireccionMapsFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DireccionMapsFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            progDailog = new ProgressDialog(DireccionMapsFragment.this);
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
        Local.setDireccionMapsFragment(DireccionMapsFragment.this);
        final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(DireccionMapsFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DireccionMapsFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DireccionMapsFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
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

    public void miUbicacion(final double lat, final double lon){
        try {
            mMap.clear();
            LatLng miUbi = new LatLng(lat,lon);
            mLocManager.removeUpdates((LocationListener) Local);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbi, 19));
            mMap.setMaxZoomPreference(19);
            mMap.addMarker(new MarkerOptions()
                    .position(miUbi)
                    .title("Ubicación Actual")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //mMap.moveCamera(CameraUpdateFactory.zoomBy(14));
            /*mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbi));*/

            String verzona = "https://apifbdelivery.fastbuych.com/Delivery/ConsultarZona?latitud=" + String.valueOf(lat)+ "&longitud=" + String.valueOf(lon);
            Log.v("newDirec",verzona);
            RequestQueue queue = Volley.newRequestQueue(DireccionMapsFragment.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, verzona, new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    if (response.length()>0){
                        try {
                            JSONObject objeto = new JSONObject(response);
                            if (objeto.getString("respuesta").equals("ENCONTRADO")){
                                codigoZona = objeto.getString("codigo");
                                String direccion = objeto.getString("zona");

                                txtNewDirec.setText(direccion);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }
        catch (Exception e){

        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        DireccionMapsFragment direccionMapsFragment;
        public DireccionMapsFragment getDireccionMapsFragment() {
            return direccionMapsFragment;
        }
        public void setDireccionMapsFragment(DireccionMapsFragment direccionMapsFragment) {
            this.direccionMapsFragment = direccionMapsFragment;
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

}
