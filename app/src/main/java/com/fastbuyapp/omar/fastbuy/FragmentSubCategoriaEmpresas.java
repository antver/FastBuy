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
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaSubcategoriaListAdapter;
import com.fastbuyapp.omar.fastbuy.entidades.EmpresaSubcategoria;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FragmentSubCategoriaEmpresas extends Fragment {
    /*****GridView gridView;
    ArrayList<EmpresaSubcategoria> list;
    EmpresaSubcategoriaListAdapter adapter = null;
    public FragmentSubCategoriaEmpresas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_subcategoria_empresas, container, false);
        TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
        txtBarra.setText("FASTBUY");
        try {
            listarRubros(String.valueOf(1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final FragmentListaEmpresas panelListaEmpresas = new FragmentListaEmpresas();
        final FragmentManager manager = getActivity().getSupportFragmentManager();

        GridView gv = (GridView) view.findViewById(R.id.tablaRubros);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int codigo = (list.get(i).getCodigo());
                String descripcion = list.get(i).getDescripcion();
                String imagen = list.get(i).getImagen();
                Globales.imagenSubcategoria = list.get(i).getImagen();
                Globales.Subcategoria = codigo;
                manager.beginTransaction()
                        .replace(R.id.contenedorPrincipal, panelListaEmpresas, panelListaEmpresas.getTag())
                        .addToBackStack(null)
                        .commit();

            }
        });
        return view;

    }

    public void listarRubros(String ciudad) throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_empresa_subcategoria_xcategoria_xubicacion.php?categoria="+Globales.categoria+"&ubicacion="+Globales.ubicacion+"&descripcion="+"";
        EnviarRecibirDatos(consulta);

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
                EmpresaSubcategoria rubro = new EmpresaSubcategoria();
                rubro.setCodigo(objeto.getInt("codigo"));
                rubro.setDescripcion(objeto.getString("descripcion"));
                rubro.setImagen(objeto.getString("imagen"));
                rubro.setEstado(objeto.getString("estado"));
                list.add(rubro);

            }

            gridView = (GridView) getActivity().findViewById(R.id.tablaRubros);
            adapter = new EmpresaSubcategoriaListAdapter(getActivity(), R.layout.list_subcategoria_empresa_item, list);
            gridView.setAdapter(adapter);
        }
        catch(Exception ex) {}
    }*****/
}
