package com.fastbuyapp.omar.fastbuy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.Operaciones;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.refactor.lib.colordialog.PromptDialog;
import lib.visanet.com.pe.visanetlib.VisaNet;
import lib.visanet.com.pe.visanetlib.presentation.custom.VisaNetViewAuthorizationCustom;

public class FragmentResumenEfectivo extends Fragment {
    /*******private Typeface fuente1;
    public ArrayList<PedidoDetalle> listaPedidos;
    public ArrayList<Double> listaDistancias = new ArrayList<Double>();
    private final int DURACION_SPLASH = 500;
    ProgressDialog progDailog = null;
    public FragmentResumenEfectivo() {
        // Required empty public constructor
    }

    public double calcularCostoEnvio(double distancia){
        double tarifaBase = 3;
        double distanciaBase = 2.5;
        double CostoDelivery = 0;
        double costoExtra = 0.9; //por kilometro
        if(distancia > distanciaBase){
            CostoDelivery = tarifaBase + (costoExtra * (distancia - distanciaBase));
        }
        else{
            CostoDelivery = tarifaBase;
        }
        return CostoDelivery;
    }

    public double calcularCargoVisa(double monto){
        double cargo = 0;
        double tipoCambio = 3.32;
        double costotransaccion = 0.15 * tipoCambio + (0.15 * tipoCambio * 0.18); //$ 0.15 + IGV
        double subtotal = monto - costotransaccion;
        double comision = (subtotal * 0.0399) + (subtotal * 0.0399 * 0.18); //comisión $ 3.99 + IGV
        double pagoTotal = subtotal - comision;
        double comisiónVisa = monto - pagoTotal;
        double comisionCliente = Math.ceil(comisiónVisa);
        cargo = comisionCliente;
        Log.v("SUBTOTAL", String.valueOf(subtotal));
        Log.v("COMISION", String.valueOf(comision));
        Log.v("PAGO TOTAL", String.valueOf(pagoTotal));
        Log.v("COMISION VISA", String.valueOf(comisiónVisa));
        Log.v("CARGO", String.valueOf(cargo));
        return cargo;
    }

    int codigoVisa = 0;
    public void CorrelativoVisa(final String URL, final double total){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject jo = new JSONObject(response);
                        codigoVisa = jo.getInt("codigo");
                        Log.i("VISA_SALIENTE", String.valueOf(codigoVisa));
                        DecimalFormat df = new DecimalFormat("#.00");
                        String nTotal = String.format("%.1f",total);
                        Map<String, Object> data = new HashMap<>();
                        data.put(VisaNet.VISANET_CHANNEL,
                                VisaNet.Channel.MOBILE);
                        data.put(VisaNet.VISANET_COUNTABLE, true);
                        data.put(VisaNet.VISANET_USERNAME, "diegodelucio01@gmail.com");
                        data.put(VisaNet.VISANET_PASSWORD, "@Am$168Z");
                        data.put(VisaNet.VISANET_MERCHANT, "604410301");//604410301-test-101039934
                        Log.i("VISA", String.valueOf(codigoVisa));
                        data.put(VisaNet.VISANET_PURCHASE_NUMBER, (Object) String.valueOf(codigoVisa));//codigo de compra visa
                        double prueba = 1;
                        data.put(VisaNet.VISANET_AMOUNT, (Object) String.valueOf(Globales.montoCompra).toString().replace(",","."));
                        data.put(VisaNet.VISANET_END_POINT_URL, "https://apiprod.vnforapps.com/");
                        //Personalización (Ingreso opcional)
                        VisaNetViewAuthorizationCustom custom = new VisaNetViewAuthorizationCustom();
                        custom.setLogoTextMerchant(true);
                        custom.setLogoTextMerchantText("FASTBUY");
                        custom.setLogoTextMerchantTextColor(R.color.visanet_black);
                        custom.setLogoTextMerchantTextSize(20);
                        //Personalización 2: Configuración del color del botón pagar en el formulario de pago
                        custom.setButtonColorMerchant(R.color.fastbuy);
                        custom.setInputCustom(true);
                        progDailog.dismiss();
                        try {
                            VisaNet.authorization(getActivity(), data, custom);
                        }
                        catch (Exception e) {
                            Log.i("TAG", "onClick: " + e.getMessage());
                        }

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
        final View view = inflater.inflate(R.layout.fragment_fragment_resumen_efectivo, container, false);

        String fuente = "fonts/Riffic.ttf";
        fuente1 = Typeface.createFromAsset(getActivity().getAssets(), fuente);
        final Button btnResumen = (Button) view.findViewById(R.id.btnGenerarPedido);
        btnResumen.setTypeface(fuente1);
        TextView lblFormaPago = (TextView) view.findViewById(R.id.lblFormaPago);
        ImageView imgPago = (ImageView) view.findViewById(R.id.imgPago);
        Operaciones operaciones = new Operaciones();
        Globales g = Globales.getInstance();
        listaPedidos = g.getListaPedidos();
        listaDistancias.clear();
        for (int i = 0; i < listaPedidos.size(); i++) {
            double latitud = listaPedidos.get(i).getLatitud();
            double longitud = listaPedidos.get(i).getLongitud();
            double distancia = operaciones.calcularDistancia(Globales.latitudOrigen, Globales.longitudOrigen, latitud, longitud);
            listaDistancias.add(distancia);
        }
        double numeromayor = 0;
        for (double distancia: listaDistancias){
            if(distancia > numeromayor){
                numeromayor = distancia;
            }
        }
        double sumaTotal = 0;
        for (int i = 0; i < listaPedidos.size(); i++) {
            sumaTotal += listaPedidos.get(i).getTotal();
        }
        double costoEnvio = calcularCostoEnvio(numeromayor);
        Log.v("costoEnvio", String.format("%.1f",costoEnvio));
        double cargo = calcularCargoVisa(sumaTotal + costoEnvio);

        final double total = costoEnvio + sumaTotal + cargo;
        Globales.montoDelivery = costoEnvio;
        Globales.montoCargo = cargo;
        Globales.montoCompra = sumaTotal;
        Globales.montoTotal = total;
        //
        if(Globales.formaPago.equals("Efectivo")){
            lblFormaPago.setText("Forma de pago: EFECTIVO");
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_efectivo);
            imgPago.setImageBitmap(bmp);
            Globales.montoCargo = 0;
        }
        if(Globales.formaPago.equals("Tarjeta")){
            lblFormaPago.setText("Forma de pago: TARJETA");
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_credito);
            imgPago.setImageBitmap(bmp);
            Globales.montoCargo = cargo;
        }
        if(Globales.formaPago.equals("Efectivo y Tarjeta")){
            lblFormaPago.setText("Forma de pago: EFECTIVO Y TARJETA");
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_efectivo_tarjeta);
            imgPago.setImageBitmap(bmp);
            Globales.montoCargo = cargo;
        }
        TextView lblNombreUsuario = (TextView) view.findViewById(R.id.lblNombreUsuario);
        lblNombreUsuario.setText(Globales.nombreCliente);
        TextView lblDireccionCLiente = (TextView) view.findViewById(R.id.lblDireccionCLiente);
        lblDireccionCLiente.setText(Globales.direccion2);
        TextView lblCiudadOrigen = (TextView) view.findViewById(R.id.lblCiudadOrigen);
        lblCiudadOrigen.setText(Globales.ciudadOrigen);
        TextView lblCompraMonto = (TextView) view.findViewById(R.id.lblCompraMonto);
        lblCompraMonto.setText("S/ " + String.format("%.2f",Globales.montoCompra));
        TextView lblCompraDelivery = (TextView) view.findViewById(R.id.lblCompraDelivery);
        lblCompraDelivery.setText("S/ " + String.format("%.1f",costoEnvio) + "0");
        TextView lblCompraCargo = (TextView) view.findViewById(R.id.lblCompraCargo);
        lblCompraCargo.setText("S/ " + String.format("%.1f",Globales.montoCargo) + "0");
        //double total = Globales.montoCompra + Globales.montoDelivery + Globales.montoCargo;
        TextView lblCompraTotal = (TextView) view.findViewById(R.id.lblCompraTotal);
        final double prueba = 1;
        lblCompraTotal.setText("S/ " + String.format("%.1f",total) + "0");
        btnResumen.setEnabled(true);
        btnResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.v("TOTALTOTAL", String.valueOf(total));
                //Toast.makeText(getContext(),Globales.formaPago, Toast.LENGTH_SHORT).show();
//                    data.put(VisaNet.VISANET_AMOUNT, (Object) String.valueOf(Double.parseDouble(nTotal)).toString().replace(",","."));

                if(Globales.formaPago.equals("Tarjeta")){
                    progDailog = new ProgressDialog(FragmentResumenEfectivo.this.getContext());
                    progDailog.setMessage("Cargando datos...");
                    progDailog.setIndeterminate(true);
                    progDailog.setCancelable(false);
                    progDailog.show();
                    CorrelativoVisa("http://fastbuych.com/app/consultasapp/app_visa_correlativo.php", total);

                }
                if(Globales.formaPago.equals("Efectivo")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("En breve recibirá una llamada para la confirmación de la compra. ¿Desea continuar?");
                    builder.setTitle("Nueva Compra");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.v("tam", String.valueOf(listaPedidos.size()));
                            try {
                                btnResumen.setEnabled(false);
                                progDailog = new ProgressDialog(FragmentResumenEfectivo.this.getContext());
                                progDailog.setMessage("Generando pedido...");
                                progDailog.setIndeterminate(true);
                                progDailog.setCancelable(false);
                                progDailog.show();
                                registrarPedido(Globales.nombreCliente, Globales.direccion2 + ", " + Globales.ciudadOrigen, Globales.numeroTelefono, String.valueOf(Globales.montoDelivery), String.valueOf(Globales.montoCargo), Globales.formaPago, "00:30:00", Globales.getInstance().getListaPedidos());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
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
            }
        });

        return view;
    }

    public void RegistrarPedidoBD(String URL, final ArrayList<PedidoDetalle> lista){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {

                        //TextView txtCodigoPedido = (TextView) findViewById(R.id.txtCodigoPedido);
                        JSONObject jo = new JSONObject(response);
                        Log.v("pedido", jo.getString("codigo"));
                        codigoRegistro = jo.getInt("codigo");
                        int i = 0;
                        for (PedidoDetalle pd: lista) {
                            i++;
                            String item = String.valueOf(i);
                            int promo = 0;
                            String codigoProducto = "0";
                            String cantidad = String.valueOf(pd.getCantidad());
                            String precio = String.valueOf(pd.getPreciounit());
                            String total = String.valueOf(pd.getTotal());
                            String ubicacion = String.valueOf(pd.getUbicacion());
                            String personalizado = URLEncoder.encode(pd.getPersonalizacion(), "UTF-8");
                            if(pd.isEsPromocion()){
                                promo = 1;
                                codigoProducto = String.valueOf(pd.getPromocion().getCodigo());
                                Log.v("promo", codigoProducto);
                            }
                            else{
                                promo = 0;
                                codigoProducto = String.valueOf(pd.getProducto().getCodigo());
                                Log.v("normal", codigoProducto);
                            }
                            Servidor s = new Servidor();
                            Log.v("codigo", codigoProducto);
                            Log.v("cantidad", cantidad);
                            Log.v("ubicacion", ubicacion);
                            Log.v("personalizado", personalizado);
                            Log.v("espromo", String.valueOf(pd.isEsPromocion()));
                            String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_detallepedido_guardar.php?pedido=" + String.valueOf(codigoRegistro) +"&item="+item+"&producto="+codigoProducto+"&cantidad="+cantidad+"&precio="+precio+"&total=" + total+"&ubicacion=" + ubicacion +"&personalizado=" + personalizado + "&promo=" + String.valueOf(promo);
                            //txtCodigoPedido.setText(consulta);
                            RegistrarDetallePedido(consulta);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    progDailog.dismiss();
                    new PromptDialog(getContext())
                            .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                            .setAnimationEnable(true)
                            .setTitleText("PEDIDO ENVIADO")
                            .setContentText("En breve recibirá una llamada para su confirmación.")
                            .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getView().getContext(), PrincipalActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).show();
                    lista.clear();
                    listaDistancias.clear();

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }


    public void RegistrarDetallePedido(String URL) {
        RequestQueue queue = Volley.newRequestQueue(getView().getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    public int codigoRegistro = 0;

    public void registrarPedido(String nombre, String direccion, String telefono,String delivery,String cargo, String forma, String tiempo, ArrayList<PedidoDetalle> lista) throws UnsupportedEncodingException {
        Servidor s = new Servidor();
        String a = URLEncoder.encode(nombre, "UTF-8");
        String b = URLEncoder.encode(direccion, "UTF-8");
        String c = URLEncoder.encode(telefono, "UTF-8");
        String d = URLEncoder.encode(delivery, "UTF-8");
        String e = URLEncoder.encode(cargo, "UTF-8");
        String f = URLEncoder.encode(forma, "UTF-8");
        String g = URLEncoder.encode(tiempo, "UTF-8");
        String h = URLEncoder.encode(Globales.ciudadOrigen, "UTF-8");
        String i = URLEncoder.encode(String.valueOf(Globales.latitudOrigen), "UTF-8");
        String j = URLEncoder.encode(String.valueOf(Globales.longitudOrigen), "UTF-8");
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_pedido_guardar.php?nombre="+a+"&direccion="+b+"&telefono=" + c +"&delivery=" + d +"&cargo=" + e +"&forma=" + f +"&tiempo=" + g+"&origen=" + h + "&latitud=" + i + "&longitud=" + j ;
        RegistrarPedidoBD(consulta,lista);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*******/

}

