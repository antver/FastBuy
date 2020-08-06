package com.fastbuyapp.omar.fastbuy;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.Validaciones.CarouselEffectTransformer;
import com.fastbuyapp.omar.fastbuy.adaptadores.CiudadMapaAdapter;
import com.fastbuyapp.omar.fastbuy.adaptadores.CiudadPageAdapter;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CiudadActivity extends AppCompatActivity {

    //GridView ciudades;
    CiudadMapaAdapter adapter;
    ProgressDialog progDailog = null;
    ProgressDialog progDailog2 = null;
    LocationManager mLocManager;
    CiudadActivity.Localizacion Local;
    String sLongitud;
    LatLng ubiOrigin, ubiCiudadMapa, LatitudSel, LongitudSel;
    private ViewPager viewpagerTop;
    ArrayList<Ubicacion> listCiudades = new ArrayList<Ubicacion>();
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciudad);
        obtenerCoordenadas();
        Typeface typefaceNexa = Typeface.createFromAsset(getAssets(), "fonts/NEXABOLD.otf");
        VerificarVersion();
        TextView txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        txtMensaje.setTypeface(typefaceNexa);
        viewpagerTop = (ViewPager) findViewById(R.id.viewpagerTop);
        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(CiudadActivity.this)); // Set transformer
        String consulta2 = "https://apifbajustes.fastbuych.com/sucursal/ListarApp";
        EnviarRecibirDatosCiudades(consulta2);
    }

    public int getIndex(String nombre, ArrayList<Ubicacion> lista)
    {
        for (int i = 0; i < lista.size(); i++)
        {
            Ubicacion auction = lista.get(i);
            if (nombre.equals(auction.getNombre()))
            {
                return i;
            }
        }

        return -1;
    }
    private void setupViewPager(ArrayList<Ubicacion> lista) {
        // Set Top ViewPager Adapter
        CiudadPageAdapter adapter = new CiudadPageAdapter(CiudadActivity.this, R.layout.list_item_ciudad_mapa, lista);
        viewpagerTop.setAdapter(adapter);
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(CiudadActivity.this);
        //myEditor = myPreferences.edit();
        String ciudad = myPreferences.getString("City_Cliente", "");
        if(!ciudad.equals("")){
            int position = getIndex(ciudad, lista);
            viewpagerTop.setCurrentItem(position);
        }

        // Set Background ViewPager Adapter
        //MyPagerAdapter adapterBackground = new MyPagerAdapter(this, listItems, ADAPTER_TYPE_BOTTOM);
        //viewPagerBackground.setAdapter(adapterBackground);
        viewpagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;
            @Override
            public void onPageSelected(int position) {
                index = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //int width = viewPagerBackground.getWidth();
                //viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //viewPagerBackground.setCurrentItem(index);
                }

            }
        });
    }

    public void EnviarRecibirDatosCiudades(String URL){
        progDailog = new ProgressDialog(CiudadActivity.this);
        progDailog.setMessage("Cargando Ciudades...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);
        progDailog.show();
        RequestQueue queue = Volley.newRequestQueue(CiudadActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        listCiudades.clear();
                        JSONArray ja = new JSONArray(response);
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject objeto = ja.getJSONObject(i);
                            Ubicacion ubicacion = new Ubicacion();
                            ubicacion.setCodigo(objeto.getInt("UBI_Codigo"));
                            ubicacion.setNombre(objeto.getString("UBI_Nombre"));
                            ubicacion.setImagen(objeto.getString("UBI_Imagen"));
                            ubicacion.setEstado(objeto.getInt("UBI_Estado"));
                            ubicacion.setLat(objeto.getDouble("UBI_Latitud"));
                            ubicacion.setLon(objeto.getDouble("UBI_Longitud"));
                            ubicacion.setRadio(objeto.getInt("UBI_Radio"));
                            ubicacion.setPreciobase(objeto.getDouble("UPB_Pedidos"));
                            ubicacion.setDistabiabase(objeto.getDouble("UPB_DistanciaBase"));
                            ubicacion.setPrecioextra(objeto.getDouble("UPB_Extras"));

                            listCiudades.add(ubicacion);
                        }
                        //ciudades.setNumColumns(1);
                        setupViewPager(listCiudades);
                        Globales globales = new Globales();
                        globales.setList("lista_ciudades", listCiudades);
                        adapter = new CiudadMapaAdapter(CiudadActivity.this,R.layout.list_item_ciudad_mapa,listCiudades);
                       //ciudades.setAdapter(adapter);

                        progDailog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Intent intent = new Intent(CiudadActivity.this, ActivityDesconectado.class);
                startActivity(intent);
            }
        });
        queue.add(stringRequest);
    }



    @Override
    public void onBackPressed (){
        finish();
    }
    public void obtenerCoordenadas(){
        //Verifica que el gps esté activado
        if(ActivityCompat.checkSelfPermission(CiudadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CiudadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CiudadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
        }else{
            locationStart();
        }
    }

    private void locationStart(){
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Local = new CiudadActivity.Localizacion();
        Local.setCiudadActivity(CiudadActivity.this);
        final boolean gpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(CiudadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CiudadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CiudadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
            return;
        }

        mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, (LocationListener) Local);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (LocationListener) Local);
        //Hasta aquí la localizacion debió de ser agregada
    }

    public class Localizacion implements LocationListener {
        CiudadActivity ciudadActivity;
        public CiudadActivity getCiudadActivity() {
            return ciudadActivity;
        }
        public void setCiudadActivity(CiudadActivity ciudadActivity) {
            this.ciudadActivity = ciudadActivity;
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
            //esta linea debería de detener la escucha del GPS
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
    void VerificarVersion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            ForceUpdateAsync forced = new ForceUpdateAsync(version, CiudadActivity.this, CiudadActivity.this);
            forced.execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        private Context context;
        private Activity mActivity;

        public ForceUpdateAsync(String currentVersion, Context context, Activity activity){
            this.currentVersion = currentVersion;
            this.context = context;
            this.mActivity = activity;
        }



        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String URL = "https://play.google.com/store/apps/details?id=" + context.getPackageName()+ "&hl=en";
                latestVersion = Jsoup.connect(URL)
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.v("URL", URL);
                Log.e("latestversion","---"+latestVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //Log.v("latestVersion", latestVersion);
            if(latestVersion!=null){
                if(!currentVersion.equalsIgnoreCase(latestVersion)){
                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                    showForceUpdateDialog();
                    /*if(!(context instanceof SplashActivity)) {
                        if(!((Activity)context).isFinishing()){
                            showForceUpdateDialog();
                        }
                    }*/
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog(){
            Intent actualizar = new Intent(CiudadActivity.this, ActualizaActivity.class);
            startActivity(actualizar);
            //mActivity.finish();
        }
    }
}
