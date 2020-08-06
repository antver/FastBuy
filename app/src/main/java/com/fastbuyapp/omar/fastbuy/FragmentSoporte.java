package com.fastbuyapp.omar.fastbuy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FragmentSoporte extends Fragment {
    /******
    public FragmentSoporte() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_soporte, container, false);
        final EditText txtDirección = (EditText) view.findViewById(R.id.txtDireccionSoporte);
        final EditText txtTelefono = (EditText) view.findViewById(R.id.txtTelefonoSoporte);
        final EditText txtLugar = (EditText) view.findViewById(R.id.txtTiendaSoporte);
        final EditText txtPedido = (EditText) view.findViewById(R.id.txtPedidoSoporte);
        txtTelefono.setText(Globales.numeroTelefono);
        Button btnListo = (Button) view.findViewById(R.id.btnListoSoporte);
        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDirección.getText().toString().trim().length() > 0){
                    if(txtTelefono.getText().toString().trim().length() > 0){
                        if(txtLugar.getText().toString().trim().length() > 0){
                            if(txtPedido.getText().toString().trim().length() > 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("En breve recibirá una llamada para la confirmación del pedido. ¿Desea continuar?");
                                builder.setTitle("Nueva Solicitud");
                                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            registrarPedidoSoporte(Globales.nombreCliente, txtDirección.getText().toString() + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, txtTelefono.getText().toString(), txtLugar.getText().toString(), txtPedido.getText().toString());
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        Toast toast = Toast.makeText(view.getContext(), "Solicitud enviada.", Toast.LENGTH_SHORT);
                                        View vistaToast = toast.getView();
                                        vistaToast.setBackgroundResource(R.drawable.toast_exito);
                                        toast.show();

                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                txtDirección.setText("");
                                                txtTelefono.setText("");
                                                txtLugar.setText("");
                                                txtPedido.setText("");
                                                Intent intent = new Intent(view.getContext(), PrincipalActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }

                                            ;
                                        }, 500);
                                    }

                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else{
                                Toast toast = Toast.makeText(getContext(), "Describe lo que necesitas.", Toast.LENGTH_SHORT);
                                View vistaToast = toast.getView();
                                vistaToast.setBackgroundResource(R.drawable.toast_warning);
                                toast.show();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(getContext(), "Ingrese nombre de la tienda.", Toast.LENGTH_SHORT);
                            View vistaToast = toast.getView();
                            vistaToast.setBackgroundResource(R.drawable.toast_warning);
                            toast.show();
                        }
                    }
                    else{
                        Toast toast = Toast.makeText(getContext(), "Ingrese número de teléfono.", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_warning);
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getContext(), "Ingrese dirección.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                }
            }
        });

        return view;
    }

    public void registrarPedidoSoporte(String nombre, String direccion, String telefono1, String telefono2, String lugar, String pedido) throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String a = URLEncoder.encode(nombre, "UTF-8");
        String b = URLEncoder.encode(direccion, "UTF-8");
        String c = URLEncoder.encode(telefono1, "UTF-8");
        String i = URLEncoder.encode(telefono2, "UTF-8");
        String d = URLEncoder.encode(lugar, "UTF-8");
        String e = URLEncoder.encode(pedido, "UTF-8");
        String f = URLEncoder.encode(Globales.ciudadOrigen, "UTF-8");
        String g = URLEncoder.encode(String.valueOf(Globales.latitudOrigen), "UTF-8");
        String h = URLEncoder.encode(String.valueOf(Globales.longitudOrigen), "UTF-8");
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_pedidoextra_registrar.php?nombre="+a+"&direccion="+b+"&telefono1=" + c +"&telefono2="+ i +"&lugar=" + d +"&pedido=" +  e + "&origen=" +  f +"&latitud=" + g + "&longitud=" + h ;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }******/
}
