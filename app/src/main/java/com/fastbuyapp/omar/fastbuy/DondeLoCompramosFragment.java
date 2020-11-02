package com.fastbuyapp.omar.fastbuy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DondeLoCompramosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DondeLoCompramosFragment  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
    // TODO: Rename parameter arguments, choose names that match
    DondeLoCompramosFragment.Localizacion Local;
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
    EditText txtNewDirec, txtNumPiso;
    String codigoZona;
    ImageView btnAtras;
    public DondeLoCompramosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DondeLoCompramosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DondeLoCompramosFragment newInstance(String param1, String param2) {
        DondeLoCompramosFragment fragment = new DondeLoCompramosFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_donde_lo_compramos);
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
        //txtReferencia = (EditText) findViewById(R.id.txtReferencia);
        obtenerCoordenadas();
        //ubiCiudadMapa = new LatLng(Double.parseDouble(latitudCiudadMapa), Double.parseDouble(longitudCiudadMapa));
        Button btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNewDirec.getText().toString().trim().length()>0 && txtNumPiso.getText().toString().trim().length()>0){
                    DireccionSel = txtNewDirec.getText().toString();
                    Piso = txtNumPiso.getText().toString();
                    myEditor.putString("LugarRecogerEncargo", Piso + " ("  + DireccionSel + ")");
                    myEditor.putString("latitud_pidelo", LatitudSel);
                    myEditor.putString("longitud_pudelo",LongitudSel);
                    myEditor.commit();
                }else {
                    Toast.makeText(DondeLoCompramosFragment.this, "Por Favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }

        });
        btnAtras = (ImageView) findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void obtenerCoordenadas(){
        //Verifica que el gps esté activado
        if(ActivityCompat.checkSelfPermission(DondeLoCompramosFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DondeLoCompramosFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DondeLoCompramosFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            progDailog = new ProgressDialog(DondeLoCompramosFragment.this);
            progDailog.setMessage("Obteniendo Coordenadas...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(false);
            progDailog.show();
            locationStart();
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
            if (ActivityCompat.checkSelfPermission(DondeLoCompramosFragment.this,
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

    private void locationStart(){
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Local = new DondeLoCompramosFragment.Localizacion();
        Local.setDireccionMapsFragment(DondeLoCompramosFragment.this);
        final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(DondeLoCompramosFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DondeLoCompramosFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DondeLoCompramosFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
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

        }
        catch (Exception e){

        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        DondeLoCompramosFragment direccionMapsFragment;
        public DondeLoCompramosFragment getDireccionMapsFragment() {
            return direccionMapsFragment;
        }
        public void setDireccionMapsFragment(DondeLoCompramosFragment direccionMapsFragment) {
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
