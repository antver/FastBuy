package com.fastbuyapp.omar.fastbuy.entidades;

import java.util.ArrayList;

/**
 * Created by OMAR on 22/08/2018.
 */

public class Empresa {
    private int codigo;
    private String nombreComercial;
    private String razonSocial;
    private String direccion;
    private String telefonos;
    private String imagen;
    private String imagenFondo;
    private int estado;
    private String estadoAbierto;
    private double precio;
    private double longitud;
    private double latitud;
    private String taper;
    private double costoTaper;
    private int categoria;
    private String valoracion;
    private String tiempo;

    private int ubicacion;

    private ArrayList<Producto> productos;

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstadoAbierto() {
        return estadoAbierto;
    }

    public void setEstadoAbierto(String estadoAbierto) {
        this.estadoAbierto = estadoAbierto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getTaper() {
        return taper;
    }

    public void setTaper(String taper) {
        this.taper = taper;
    }

    public double getCostoTaper() {
        return costoTaper;
    }

    public void setCostoTaper(double costoTaper) {
        this.costoTaper = costoTaper;
    }

    public String getImagenFondo() {return imagenFondo;}

    public void setImagenFondo(String imagenFondo) { this.imagenFondo = imagenFondo; }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public int getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(int ubicacion) {
        this.ubicacion = ubicacion;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }
}
