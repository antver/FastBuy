package com.fastbuyapp.omar.fastbuy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaCategoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FragmentCategoriaEmpresas extends Fragment {
/*

    GridView gridView;
    ArrayList<EmpresaCategoria> list;
    CategoriaEmpresaListAdapter adapter = null;
    public FragmentCategoriaEmpresas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_categorias_empresas, container, false);
        TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
        txtBarra.setText("FASTBUY");

        try {

            listarCategorias(String.valueOf(Globales.ubicacion));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final FragmentSubCategoriaEmpresas panelSubCategoriaEmpresas = new FragmentSubCategoriaEmpresas();
        final FragmentManager manager = getActivity().getSupportFragmentManager();

        GridView gv = (GridView) view.findViewById(R.id.tablaCategorias);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int codigo = (list.get(i).getCodigo());
                String descripcion = list.get(i).getDescripcion();
                String imagen = list.get(i).getImagen();
                Globales.categoria = codigo;
                manager.beginTransaction()
                        .replace(R.id.contenedorPrincipal, panelSubCategoriaEmpresas, panelSubCategoriaEmpresas.getTag())
                        .addToBackStack(null)
                        .commit();

            }
        });

        return view;

    }

    public void listarCategorias(String ubicacion) throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_empresa_categoria_xubicacion.php?ubicacion="+ubicacion;
        EnviarRecibirDatos(consulta);
        Toast.makeText(getContext(),String.valueOf(codigo),Toast.LENGTH_SHORT).show();

    }

    public void EnviarRecibirDatos(String URL){

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        CargarLista(ja);
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

    public void CargarLista(JSONArray ja) throws JSONException {
        try {
            list = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject objeto = ja.getJSONObject(i);
                EmpresaCategoria ec = new EmpresaCategoria();
                ec.setCodigo(objeto.getInt("codigo"));
                ec.setDescripcion(objeto.getString("descripcion"));
                ec.setImagen(objeto.getString("imagen"));
                ec.setEstado(objeto.getString("estado"));
                list.add(ec);
            }

            gridView = (GridView) getActivity().findViewById(R.id.tablaCategorias);
            adapter = new CategoriaEmpresaListAdapter(getActivity(), R.layout.list_categoria_empresa_item, list);
            gridView.setAdapter(adapter);
        }
        catch(Exception ex) {}
    }*/

}
