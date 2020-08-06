package com.fastbuyapp.omar.fastbuy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentDirecciones extends Fragment {
    /*****GridView gridView;
    public ArrayList<Ubicaciones> listaUbicaciones;
    UbicacionesListAdapter adapter = null;
    public FragmentDirecciones() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_direcciones, container, false);
        TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
        txtBarra.setText("FASTBUY");
        final Servidor servidor = new Servidor();
        //Toast.makeText(getActivity(),rub + "-" + ciu,Toast.LENGTH_SHORT).show();
        String consulta = "http://"+servidor.getServidor()+"/app/consultasapp/app_cliente_direcciones_xtelefono.php?telefono="+Globales.numeroTelefono;
        EnviarRecibirDatos(consulta, view);
        return view;
    }

    public void EnviarRecibirDatos(String URL, final View view){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        CargarLista(ja, view);
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

    public void CargarLista(JSONArray ja, View view) throws JSONException {
        listaUbicaciones = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject objeto = ja.getJSONObject(i);
            Ubicaciones ubicaciones = new Ubicaciones();
            ubicaciones.setCodigo(objeto.getInt("codigo"));
            ubicaciones.setCiudad(objeto.getString("ciudad"));
            ubicaciones.setDireccion(objeto.getString("direccion"));
            if (objeto.getInt("predeterminada") == 1) {
                ubicaciones.setCheck(true);
            }
            else{
                ubicaciones.setCheck(false);
            }
            //ubicaciones.setLatitud(objeto.getDouble("latitud"));
            //ubicaciones.setLongitud(objeto.getDouble("longitud"));
            listaUbicaciones.add(ubicaciones);

        }
        gridView = (GridView) view.findViewById(R.id.tablaListaUbic);
        adapter = new UbicacionesListAdapter(getActivity(), R.layout.item_direcciones, listaUbicaciones);
        gridView.setAdapter(adapter);
    }*****/
}
