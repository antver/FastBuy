package com.fastbuyapp.omar.fastbuy.HelperEsquema;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicacion;
import com.fastbuyapp.omar.fastbuy.entidades.Ubicaciones;
import com.fastbuyapp.omar.fastbuy.entidades.Usuario;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by OMAR on 25/03/2019.
 */

public class UsuarioDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "usuario.db";

    public UsuarioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UsuarioContract.UsuarioEntry.TABLE_NAME + " ("
                + UsuarioContract.UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsuarioContract.UsuarioEntry.CODIGO + " TEXT,"
                + UsuarioContract.UsuarioEntry.NOMBRES + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.TELEFONO + " TEXT NOT NULL,"
                + UsuarioContract.UsuarioEntry.FECHANACIMIENTO + " TEXT,"
                + UsuarioContract.UsuarioEntry.EMAIL + " TEXT,"
                + UsuarioContract.UsuarioEntry.DNI + " TEXT,"
                + UsuarioContract.UsuarioEntry.RUC + " TEXT,"
                + "UNIQUE (" + UsuarioContract.UsuarioEntry._ID + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long guardarUsuario(Usuario usuario, String ciudad, Activity activity) throws UnsupportedEncodingException {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Servidor s = new Servidor();
        String nombre = URLEncoder.encode(usuario.getNombres().toString(), "UTF-8");
        String telefono = URLEncoder.encode(usuario.getNumeroTelefono().toString(), "UTF-8");
        String email = URLEncoder.encode(usuario.getEmail().toString(), "UTF-8");
        String ciudadurl = URLEncoder.encode(ciudad, "UTF-8");
        /*String direccion = URLEncoder.encode(ubicaciones.getDireccion().toString(), "UTF-8");
        String longitud = URLEncoder.encode(String.valueOf(ubicaciones.getLongitud()), "UTF-8");
        String latitud = URLEncoder.encode(String.valueOf(ubicaciones.getLatitud()), "UTF-8");*/

        //String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_cliente_registrar.php?nombre="+nombre+"&telefono="+telefono+"&email="+ email+"&direccion=&ciudad="+ ciudad+"&longitud=&latitud=";
        String consulta = "http://"+s.getServidor()+"/app/consultasapp/app_cliente_registrar_v2.php?nombre="+nombre+"&telefono="+telefono+"&email="+ email+"&ciudad="+ ciudadurl;

        RegistrarUsuario(consulta, activity);

        return sqLiteDatabase.insert(
                UsuarioContract.UsuarioEntry.TABLE_NAME,
                null,
                usuario.toContentValues());

    }

    public void RegistrarUsuario(String URL, Activity activity){
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("REGISTRO USUARIO","Éxito");
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("REGISTRO USUARIO","Falló");
            }
        });
        queue.add(stringRequest);
    }

    public Cursor consultarPorCodigo(int codigo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "select * from " + UsuarioContract.UsuarioEntry.TABLE_NAME + " WHERE _id=?";
        Cursor c = sqLiteDatabase.rawQuery(query, new String[]{"1"});
        return c;
    }

}
