package com.fastbuyapp.omar.fastbuy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.adaptadores.EmpresaListAdapter;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Empresa;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FragmentListaEmpresas extends Fragment {
    /*****GridView gridView;
    ArrayList<Empresa> list;
    EmpresaListAdapter adapter = null;

    public FragmentListaEmpresas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_lista_empresas, container, false);
        TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
        txtBarra.setText("FASTBUY");
        final Servidor servidor = new Servidor();
        String rubro = String.valueOf(Globales.Subcategoria);
        String ciudad = String.valueOf(1);
        final String[] descrip = {""};
        EditText txtDescripcion = (EditText) view.findViewById(R.id.editText2);

        final String[] ciu = {null};
        try {
            descrip[0] = URLEncoder.encode(txtDescripcion.getText().toString(), "UTF-8");
            ciu[0] = URLEncoder.encode(ciudad, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getActivity(),rub + "-" + ciu,Toast.LENGTH_SHORT).show();
        String consulta = "http://"+servidor.getServidor()+"/app/consultasapp/app_empresas_xubicacion_xcategoria_xsubcategoria_xdescripcion.php?ubicacion="+Globales.ubicacion+"&categoria="+Globales.categoria+"&subcategoria="+Globales.Subcategoria+"&descripcion=" + descrip[0];
        EnviarRecibirDatos(consulta, view);
        GridView gv = (GridView) view.findViewById(R.id.tablaListaEmpresas);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int codigo =(list.get(i).getCodigo());
                Globales.empresaSeleccionada = codigo;
                String nombreComercial = list.get(i).getNombreComercial();
                Globales.LongitudEmpresaSeleccionada = list.get(i).getLongitud();
                Globales.LatitudEmpresaSeleccionada =  list.get(i).getLatitud();
                Globales.ubicacionEmpresaSeleccionada = Globales.ubicacion;
                Globales.imagenEmpresa = list.get(i).getImagen();
                if(list.get(i).getEstadoAbierto().equals("Abierto")){
                    Globales.tiendaCerrada = false;
                }
                else{
                    Globales.tiendaCerrada = true;
                }
                //String descripcion = list.get(i).getRazonSocial();
                //Toast.makeText(view.getContext(), String.valueOf(Globales.LatitudEmpresaSeleccionada), Toast.LENGTH_SHORT).show();
                //Toast.makeText(view.getContext(), String.valueOf(Globales.LongitudEmpresaSeleccionada), Toast.LENGTH_SHORT).show();

                //String precio = String.format("S/ %.2f",list.get(i).getPrecio());
                //Globales.precio = list.get(i).getPrecio();
                //String estadoAbierto = list.get(i).getEstadoAbierto();
                //String imagem = list.get(i).getImagen();
                TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
                txtBarra.setText(String.valueOf(nombreComercial));

                FragmentVistaProductos panelListaProductos = new FragmentVistaProductos();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.contenedorPrincipal, panelListaProductos, panelListaProductos.getTag())
                        .addToBackStack(null)
                        .commit();
                //abrirVistaCategoria(view,codigo,nombreComercial,estadoAbierto,descripcion, imagem);
            }
        });

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
                String consulta = "http://"+ servidor.getServidor()+"/app/consultasapp/app_empresas_xubicacion_xcategoria_xsubcategoria_xdescripcion.php?ubicacion="+Globales.ubicacion+"&categoria="+Globales.categoria+"&subcategoria="+Globales.Subcategoria+"&descripcion=" + descrip[0];
                EnviarRecibirDatos(consulta, view);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
    int tiendas = 0;
    public void CargarLista(JSONArray ja, View view) throws JSONException {
        list = new ArrayList<>();
        tiendas = 0;
        for (int i = 0; i < ja.length(); i++) {
            JSONObject objeto = ja.getJSONObject(i);
            Empresa empresa = new Empresa();
            empresa.setCodigo(objeto.getInt("codigo"));
            empresa.setNombreComercial(objeto.getString("nombreComercial"));
            empresa.setRazonSocial(objeto.getString("razonSocial"));
            empresa.setDireccion(objeto.getString("direccion"));
            empresa.setImagen(objeto.getString("foto"));
            empresa.setTelefonos(objeto.getString("telefonos"));
            empresa.setEstado(objeto.getInt("estado"));
            empresa.setEstadoAbierto(objeto.getString("estadoAbierto"));
            empresa.setLatitud(objeto.getDouble("latitud"));
            empresa.setLongitud(objeto.getDouble("longitud"));
            list.add(empresa);
            if(objeto.getString("estadoAbierto").equals("Abierto")){
                tiendas ++;
            }
        }
        TextView txt = (TextView) view.findViewById(R.id.textView2);
        if(tiendas!= 1)
            txt.setText(String.valueOf(tiendas) + " tiendas abiertas");
        else
            txt.setText(String.valueOf(tiendas) + " tienda abierta");
        gridView = (GridView) view.findViewById(R.id.tablaListaEmpresas);
        adapter = new EmpresaListAdapter(getActivity(), R.layout.list_item, list);
        gridView.setAdapter(adapter);
    }*****/
}
