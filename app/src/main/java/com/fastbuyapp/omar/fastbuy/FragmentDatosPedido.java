package com.fastbuyapp.omar.fastbuy;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import java.util.List;
import java.util.Locale;

import lib.visanet.com.pe.visanetlib.VisaNet;

import static android.app.Activity.RESULT_OK;

public class FragmentDatosPedido extends Fragment {
    /*****private Typeface fuente1;
    public FragmentDatosPedido() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VisaNet.VISANET_AUTHORIZATION) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    String JSONString = data.getExtras().getString("keySuccess");
                    Toast toast1 = Toast.makeText(getContext(), JSONString, Toast.LENGTH_LONG);
                    toast1.show();
                } else {
                    String JSONString = data.getExtras().getString("keyError");
                    JSONString = JSONString != null ? JSONString : "";
                    Toast toast1 = Toast.makeText(getContext(), JSONString, Toast.LENGTH_LONG);
                    toast1.show();
                }
            }
            else {
                Toast toast1 = Toast.makeText(getContext(), "Cancel...", Toast.LENGTH_LONG);
                toast1.show();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_fragment_datos_pedido, container, false);
        String fuente = "fonts/Riffic.ttf";
        fuente1 = Typeface.createFromAsset(getActivity().getAssets(), fuente);
        Button btnResumen = (Button) row.findViewById(R.id.btnResumen);
        btnResumen.setTypeface(fuente1);

        final Spinner cboCiudad = (Spinner) row.findViewById(R.id.cboCiudadOrigen);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(row.getContext(),R.array.ciudades,R.layout.support_simple_spinner_dropdown_item);
        cboCiudad.setAdapter(adapter);
        if (Globales.ciudadOrigen != null) {
            int spinnerPosition = adapter.getPosition(Globales.ciudadOrigen);
            cboCiudad.setSelection(spinnerPosition);
        }
        final Spinner cboFormaPago = (Spinner) row.findViewById(R.id.cboFormaPago);
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(row.getContext(),R.array.formaPago,R.layout.support_simple_spinner_dropdown_item);
        cboFormaPago.setAdapter(adapter2);

        cboFormaPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int spinnerPosition2 = cboFormaPago.getSelectedItemPosition();
                cboFormaPago.setSelection(spinnerPosition2);
                Globales.formaPago = cboFormaPago.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //final TextView txtNombreCliente = (TextView) row.findViewById(R.id.txtNombreCliente);
        final TextView txtDireccionCliente = (TextView) row.findViewById(R.id.txtDireccionCliente);
        final TextView txtDireccionCliente2 = (TextView) row.findViewById(R.id.txtDireccionCliente2);
        //final TextView txtTelefonoCliente = (TextView) row.findViewById(R.id.txtTelefonoCliente);
        //txtNombreCliente.setText(Globales.nombreCliente);
        txtDireccionCliente.setText(Globales.ciudadOrigen);
        //txtTelefonoCliente.setText(Globales.numeroTelefono);
        Button btnMiUbicacion = (Button) row.findViewById(R.id.btnMiUbicacion);
        btnMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                    } else {
                        FragmentDatosPedido.AsyncTask_load ast = new FragmentDatosPedido.AsyncTask_load();
                        ast.execute();
                        //locationStart();
                    }
                }
                catch (Exception ex){

                }
            }
        });
        btnMiUbicacion.performClick();
        btnResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDireccionCliente2.getText().toString().trim().length() == 0){
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese su dirección actual.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                    return;
                }
                ValidacionDatos validacionDatos = new ValidacionDatos();
                //Globales.ciudadOrigen = String.valueOf(cboCiudad.getSelectedItem());
                Globales.direccion2 = txtDireccionCliente2.getText().toString();
                final FragmentResumenEfectivo panelResumenEfectivo = new FragmentResumenEfectivo();
                final FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.contenedorPrincipal, panelResumenEfectivo, panelResumenEfectivo.getTag())
                        .commit();
                If(validacionDatos.validarCelular(txtTelefonoCliente)){
                    TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
                    txtBarra.setText("RESUMEN");
                    Globales.nombreCliente = String.valueOf(txtNombreCliente.getText());
                    Globales.direccion = String.valueOf(txtDireccionCliente.getText());
                    Globales.numeroTelefono = String.valueOf(txtTelefonoCliente.getText());
                    //Globales.formaPago = String.valueOf(cboFormaPago.getSelectedItem());
                    Globales.formaPago = String.valueOf("Efectivo");
                    //Toast.makeText(v.getContext(), String.valueOf(spinnerPosition2),Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese un número de teléfono válido.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                }
            }
        });
        return row;
    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        FragmentDatosPedido.Localizacion Local = new FragmentDatosPedido.Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Local);
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                EditText txtDireccion = (EditText) getView().findViewById(R.id.txtDireccionCliente);
                txtDireccion.setText("");
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    int cant = DirCalle.getAddressLine(0).split(",").length;
                    Globales.direccion = DirCalle.getAddressLine(0);
                    int tam = DirCalle.getAddressLine(0).split(",")[cant - 2].length();
                    Globales.ciudadOrigen = DirCalle.getAddressLine(0).split(",")[cant - 2].substring(1, tam);
                    Log.v("ciudadOrigen", Globales.ciudadOrigen);

                    txtDireccion.setText(DirCalle.getAddressLine(0).toString());
                    //Toast.makeText(getContext(), DirCalle.getAddressLine(0) , Toast.LENGTH_LONG).show();

                    //direccion.setText("Mi direccion es: \n" + DirCalle.getAddressLine(0));
                }
                Log.v("ciudadOrigen2", "'" + Globales.ciudadOrigen +"'");

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public class AsyncTask_load extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog progDailog = null;
        @Override
        protected void onPreExecute() {
            locationStart();
            //barraCargando.setAnimation(anim);
            Activity activity = getActivity();
            progDailog = new ProgressDialog(getActivity());
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FragmentDatosPedido.AsyncTask_load.this.cancel(true);
                }
            });
            progDailog.setMessage("Buscando ubicación...");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(true);
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
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    // Cuando pasen 1 segundo, pasamos a la actividad principal de la aplicación
                    Intent i = new Intent(InicioActivity.this, PortadaActivity.class );
                    i.putExtra("ubicacion", Globales.ciudad);
                    InicioActivity.this.startActivity(i);
                    finish();
                };
            }, 1000);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getContext(), "CANCELADO", Toast.LENGTH_SHORT).show();
        }
    }

    public class Localizacion implements LocationListener {
        FragmentDatosPedido mainActivity;

        public FragmentDatosPedido getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(FragmentDatosPedido mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            Globales.latitudOrigen = loc.getLatitude();
            Globales.longitudOrigen = loc.getLongitude();
            View view = getView();
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
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
    }******/

}
