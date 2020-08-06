package com.fastbuyapp.omar.fastbuy.entidades;

/**
 * Created by OMAR on 26/03/2019.
 */

public class Ubicacion {
    private int codigo;
    private String nombre;
    private String imagen;
    private int estado;
    private double lat;
    private double lon;
    private int radio;
    private double preciobase;
    private double precioextra;
    private double distabiabase;
    public Ubicacion(){
        
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public double getPreciobase() {
        return preciobase;
    }

    public void setPreciobase(double preciobase) {
        this.preciobase = preciobase;
    }

    public double getPrecioextra() {
        return precioextra;
    }

    public void setPrecioextra(double precioextra) {
        this.precioextra = precioextra;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getDistabiabase() {
        return distabiabase;
    }

    public void setDistabiabase(double distabiabase) {
        this.distabiabase = distabiabase;
    }
}
