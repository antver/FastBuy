package com.fastbuyapp.omar.fastbuy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioDbHelper;
import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;
import com.fastbuyapp.omar.fastbuy.entidades.Usuario;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    /*****private Typeface fuente1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String fuente = "fonts/Riffic.ttf";
        this.fuente1 = Typeface.createFromAsset(getAssets(), fuente);
        TextView lblFastBuy = (TextView) findViewById(R.id.lbllogin);
        lblFastBuy.setTypeface(fuente1);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            LoginActivity.AsyncTask_load ast = new LoginActivity.AsyncTask_load();
            ast.execute();
            //locationStart();
        }

        Button btnInicar = (Button) findViewById(R.id.btnIniciarLogin);
        //ImageButton btnLocalizar = (ImageButton) findViewById(R.id.ibLocalizacion);
        btnInicar.setTypeface(fuente1);
        final EditText txtNombres = (EditText) findViewById(R.id.txtNombreLogin);
        final EditText txtNumeroTelefono = (EditText) findViewById(R.id.txtTelefonoLogin);
        //final EditText txtDireccion = (EditText) findViewById(R.id.txtDireccionLogin);
        //cboCiudad.setAdapter(new ArrayAdapter<Ubicacion>(PrincipalActivity.this, android.R.layout.simple_spinner_dropdown_item, list));
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(LoginActivity.this,R.array.ciudades,R.layout.support_simple_spinner_dropdown_item);
        final Spinner cboCiudad = (Spinner) findViewById(R.id.cboCiudadInicial);
        //cboCiudad.setAdapter(adapter);
        btnInicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNombres.getText().toString().trim().length() == 0){
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese su nombre y apellidos.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    return;
                }
                ValidacionDatos validacion = new ValidacionDatos();
                if(validacion.validarCelular(txtNumeroTelefono)==false){
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese un número de celular válido.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    return;
                }
                try {

                    UsuarioDbHelper db = new UsuarioDbHelper(getBaseContext());

                    Usuario usuario = new Usuario();
                    usuario.setNombres(txtNombres.getText().toString());
                    usuario.setNumeroTelefono(txtNumeroTelefono.getText().toString());

                    Ubicaciones ubicacion = new Ubicaciones();
                    ubicacion.setDireccion(Globales.direccion);
                    ubicacion.setCiudad(Globales.ciudadOrigen);
                    //ubicacion.setLatitud(Globales.latitudOrigen);
                    //ubicacion.setLongitud(Globales.longitudOrigen);
                    ubicacion.setCheck(true);
                    db.guardarUsuario(usuario, "", LoginActivity.this);
                    Intent intent2 = new Intent(LoginActivity.this,PrincipalActivity.class);
                    startActivity(intent2);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("punto", "Ingresa la nueva activity");
            }
        });

    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LoginActivity.Localizacion Local = new LoginActivity.Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Local);
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    int cant = DirCalle.getAddressLine(0).split(",").length;
                    Globales.direccion = DirCalle.getAddressLine(0);
                    int tam = DirCalle.getAddressLine(0).split(",")[cant - 2].length();
                    Globales.ciudadOrigen = DirCalle.getAddressLine(0).split(",")[cant - 2].substring(1, tam);
                    Log.v("ciudadOrigen", Globales.ciudadOrigen);
                    //EditText txtDireccion = (EditText) findViewById(R.id.txtDireccionLogin);
                    //txtDireccion.setText(DirCalle.getAddressLine(0).split(",")[cant - 2].substring(1, tam) +", "+DirCalle.getAddressLine(0).split(",")[cant - 1]);
                    //Toast.makeText(InicioActivity.this, DirCalle.getAddressLine(0) , Toast.LENGTH_LONG).show();
                    //direccion.setText("Mi direccion es: \n" + DirCalle.getAddressLine(0));


                    //int tam =  Globales.ciudadOrigen.length();
                    //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(LoginActivity.this,R.array.ciudades,R.layout.support_simple_spinner_dropdown_item);
                    final Spinner cboCiudad = (Spinner) findViewById(R.id.cboCiudadInicial);
                    //cboCiudad.setAdapter(adapter);
                    String ciudad = Globales.ciudadOrigen;
                    Log.v("ciudadNueva: ", Globales.ciudadOrigen);
                    if (Globales.ciudadOrigen != null) {
                        //int spinnerPosition = adapter.getPosition(ciudad);
                        //cboCiudad.setSelection(spinnerPosition);
                    }
                }
                Log.v("ciudadOrigen2", "'" + Globales.ciudadOrigen +"'");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class AsyncTask_load extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog progDailog = null;
        @Override
        protected void onPreExecute() {
            locationStart();
            //barraCargando.setAnimation(anim);

            progDailog = new ProgressDialog(LoginActivity.this);
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    AsyncTask_load.this.cancel(true);
                }
            });
            progDailog.setMessage("Buscando ubicación...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(false);
            progDailog.show();


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            while (Globales.latitudOrigen == 0.0) {

            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            progDailog.dismiss();
            isCancelled();



        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getApplication(), "CANCELADO", Toast.LENGTH_SHORT).show();
        }
    }

    public class Localizacion implements LocationListener {
        LoginActivity mainActivity;

        public LoginActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(LoginActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            Globales.latitudOrigen = loc.getLatitude();
            Globales.longitudOrigen = loc.getLongitude();
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplication(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //Toast.makeText(getApplication(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }*****/
}
