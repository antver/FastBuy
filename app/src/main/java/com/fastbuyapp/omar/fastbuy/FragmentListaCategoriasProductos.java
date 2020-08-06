package com.fastbuyapp.omar.fastbuy;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FragmentListaCategoriasProductos extends Fragment {
    /*****ArrayList<Categoria> list;
    CategoriaListAdapter adapter = null;
    RecyclerView recicler;
    public void listarCategorias(String empresa, String ubicacion,View view) throws UnsupportedEncodingException {
        Servidor s = new Servidor();

        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_productos_categoria_xempresa_xubicacion.php?empresa="+empresa+ "&ubicacion="+ubicacion;
        EnviarRecibirDatos(consulta, view);
    }
    private Typeface fuente1;

    public FragmentListaCategoriasProductos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_lista_categorias_productos, container, false);
        try {
            listarCategorias(String.valueOf(Globales.empresaSeleccionada),String.valueOf(Globales.ubicacion), view);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


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
        String[] mDataset;
        list = new ArrayList<>();
        for(int i = 0; i < ja.length(); i++) {
            JSONObject objeto = ja.getJSONObject(i);
            Categoria categoria = new Categoria();
            categoria.setCodigo(objeto.getInt("codigo"));
            categoria.setDescripcion(objeto.getString("descripcion"));
            list.add(categoria);
        }
        //Empresa empresa = new Empresa();
        //empresa.setCodigo(Integer.parseInt(codigo));
        //empresa.setNombreComercial(nombreComercial);

        ReciclerAdapterCategorias adapter = new ReciclerAdapterCategorias(list);
        recicler = (RecyclerView) view.findViewById(R.id.rcvListaCategoriasProductos);
        recicler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        //adapter = new CategoriaListAdapter(getActivity(), R.layout.list_categoria_item, list);
        recicler.setAdapter(new ReciclerAdapterCategorias(list));
    }*****/
}
