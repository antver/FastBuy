package com.fastbuyapp.omar.fastbuy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FragmentPromociones extends Fragment {
    /*******GridView gridView;
    public String codigo;
    public String nombreComercial;
    public String categoria;
    ArrayList<Promocion> list;
    PromocionListAdapter adapter = null;

    public FragmentPromociones() {
        // Required empty public constructor
    }

    public void listarPromociones(String filtro, View v) throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String c = URLEncoder.encode(filtro, "UTF-8");
        //Toast.makeText(v.getContext(), String.valueOf(Globales.ubicacion), Toast.LENGTH_SHORT).show();
        //https://fastbuych.com/app/consultasapp/app_promocion_xubicacion.php?ubicacion=1&filtro=&accion=omarveravasq2005
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_promocion_xubicacion.php?&ubicacion="+ Globales.ubicacion+"&filtro="+c;
        EnviarRecibirDatos(consulta, v);
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
                        //Toast.makeText(getApplicationContext(),"a: ", Toast.LENGTH_LONG);
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_promociones, container, false);
        try {
            listarPromociones("", view);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        EditText txtDescripcion = (EditText) view.findViewById(R.id.txtFiltroPromos);
        final String[] descrip = {""};
        txtDescripcion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    descrip[0] = URLEncoder.encode(s.toString().toUpperCase(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    listarPromociones(descrip[0], view);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    public void CargarLista(JSONArray ja, View view) throws JSONException {
        list = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject objeto = ja.getJSONObject(i);
            Promocion promocion = new Promocion();
            promocion.setCodigo(objeto.getInt("codigo"));
            promocion.setDescripcion(objeto.getString("descripcion"));
            promocion.setPrecio(objeto.getString("precio"));
            promocion.setImagen(objeto.getString("imagen"));
            promocion.setEstado(objeto.getInt("estado"));

            JSONObject objeto2 = objeto.getJSONObject("categoria");
            Categoria categoria = new Categoria();
            categoria.setDescripcion(objeto2.getString("descripcion"));
            promocion.setCategoria(categoria);

            JSONObject objeto3 = objeto.getJSONObject("empresa");
            Empresa empresa = new Empresa();
            empresa.setCodigo(objeto3.getInt("codigo"));
            empresa.setNombreComercial(objeto3.getString("nombreComercial"));
            empresa.setLongitud(objeto3.getDouble("longitud"));
            empresa.setLatitud(objeto3.getDouble("latitud"));
            promocion.setEmpresa(empresa);
            list.add(promocion);
        }
        try {
            gridView = (GridView) view.findViewById(R.id.tablaListaPromos);
            adapter = new PromocionListAdapter(getActivity(), R.layout.promociones_item, list);
            gridView.setAdapter(adapter);
        } catch (Exception ex) {

        }
    }*******/
}
