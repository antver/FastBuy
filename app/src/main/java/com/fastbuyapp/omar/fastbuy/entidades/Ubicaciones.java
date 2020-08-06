package com.fastbuyapp.omar.fastbuy.entidades;

import android.content.ContentValues;

import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioContract;

/**
 * Created by OMAR on 25/03/2019.
 * Edited by Luis Ysla on 23/12/2019.
 */


public class Ubicaciones {
    private String direccion;
    private String etiqueta; //sirve para mostrarlo en el combobox
    private int codigo;
    private String ciudad;
    private String latitud;
    private String longitud;

    private boolean check; //sirve para ... ¿?


    public Ubicaciones(){
        this.etiqueta = "";
        this.direccion = "";
        this.ciudad = "";
        this.latitud = "";
        this.longitud = "";
    }

    public boolean isCheck() {
        return check;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

}
