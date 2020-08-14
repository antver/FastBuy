package com.fastbuyapp.omar.fastbuy.Operaciones;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.text.DecimalFormat;

public class Pago_Visa {

    double cargo = 0;
    double tipoCambio = 3.32;
    int codigoVisa = 0;

    public double calcularCargoVisa2(double monto){ //este era la primera función
        double costotransaccion = 0.15 * tipoCambio + (0.15 * tipoCambio * 0.18); //$ 0.15 + IGV
        double subtotal = monto - costotransaccion;
        double comision = (subtotal * 0.0399) + (subtotal * 0.0399 * 0.18); //comisión $ 3.99 + IGV
        double pagoTotal = subtotal - comision;
        double comisiónVisa = monto - pagoTotal;
        //double comisionCliente = Math.ceil(comisiónVisa);
        double comisionCliente = comisiónVisa;
        cargo = comisionCliente;
        Log.v("SUBTOTAL", String.valueOf(subtotal));
        Log.v("COMISION", String.valueOf(comision));
        Log.v("PAGO TOTAL", String.valueOf(pagoTotal));
        Log.v("COMISION VISA", String.valueOf(comisiónVisa));
        Log.v("CARGO", String.valueOf(cargo));
        return cargo;
    }

    public double calcularCargoVisa(double monto){
        if (monto < 50) cargo = 0;
        else{
            int cant = (int) monto/50;
            cargo = (double) cant*2.5;
        }
        return cargo;
    }
    /*public void CorrelativoVisa(final String URL, final double total){
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
    }*/
}
