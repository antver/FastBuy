package com.fastbuyapp.omar.fastbuy.entidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioContract;
import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioDbHelper;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by OMAR on 24/03/2019.
 */

public class Operaciones {
    public double calcularDistancia(double latitudOrigen , double longitudOrigen, double latitudDestino , double longitudDestino){
        Location location = new Location("localizacion 1");
        location.setLatitude(latitudOrigen);  //latitud
        location.setLongitude(longitudOrigen); //longitud
        Location location2 = new Location("localizacion 2");
        location2.setLatitude(latitudDestino);  //latitud
        location2.setLongitude(longitudDestino); //longitud
        double distancia = location.distanceTo(location2);

        return  (distancia / 1000) * 1.18;
    }

    public double calcularDistanciaEqui(double longitudOrigen, double latitudOrigen, double longitudDestino, double latitudDestino){
        int R = 6371; // km
        double dLat = toRadians(latitudDestino-latitudOrigen);
        double dLon = toRadians(longitudDestino-longitudOrigen);
        double lat1 = toRadians(latitudOrigen);
        double lat2 = toRadians(latitudDestino);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //return R * c;

       // int R = 6371; // km
        //double x = (longitudDestino - longitudOrigen) * Math.cos((latitudOrigen + latitudDestino) / 2);
        //double y = (latitudDestino - latitudOrigen);
        double distance = R * c; //Math.sqrt(x * x + y * y) * R;
        return distance;

    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }

    public Ruta calcularDistanciaApiMaps(final Context context, double latitudOrigen, double longitudOrigen, double latitudDestino, double longitudDestino){
        final Ruta ruta = new Ruta();
        //String consulta = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=-7.240313509751132,-79.46285303119015&destinations=-7.226482341369703,-79.4305043907571&key=AIzaSyCOsnfR9NuuaynSCgVAMM2d9NwMF3rv-PE";
        String consulta = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+String.valueOf(latitudOrigen)+","+String.valueOf(longitudOrigen)+"&destinations="+String.valueOf(latitudDestino)+","+longitudDestino+"&key="+ Globales.apiKey;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, consulta, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0){
                    try {
                        JSONObject jo = new JSONObject(response);
                        JSONArray jsonRows = jo.getJSONArray("rows");
                        for (int i = 0; i < jsonRows.length(); i++) {
                            JSONObject jsonRowsItem = jsonRows.getJSONObject(i);
                            JSONArray jsonElement = jsonRowsItem.getJSONArray("elements");
                            for (int j = 0; j < jsonElement.length(); j++) {
                                JSONObject jsonElementItem = jsonElement.getJSONObject(j);
                                JSONObject jsonDistance = jsonElementItem.getJSONObject("distance");
                                ruta.setDistancia(jsonDistance.getInt("value"));
                                JSONObject jsonDuration = jsonElementItem.getJSONObject("duration");
                                ruta.setDuracionSegundos(jsonDuration.getInt("value"));
                                ruta.setDuracionTexto(jsonDuration.getString("text"));
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ruta.setMensaje("NADA");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                ruta.setMensaje("POR AQUI NO");
            }
        });
        queue.add(stringRequest);
        return ruta;
    }


}
