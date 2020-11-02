package com.fastbuyapp.omar.fastbuy.entidades;

import java.util.ArrayList;

/**
 * Created by OMAR on 05/09/2018.
 */

public class PedidoDetalle {
    private int numero;
    private Producto producto;
    private boolean esPromocion;
    private int cantidad;
    private double total;
    private int empresa;
    private int ubicacion;
    private float preciounit;
    private Promocion promocion;
    private double longitud;
    private double latitud;
    private String personalizacion;
    private int tiempo;
    private int estado;
    private String presentacion;
    private ArrayList<ProductoAgregados> agregados;


    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public float getPreciounit() {
        return preciounit;
    }

    public void setPreciounit(float preciounit) {
        this.preciounit = preciounit;
    }

    public int getUbicacion() {
        return ubicacion;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public void setUbicacion(int ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isEsPromocion() {
        return esPromocion;
    }

    public void setEsPromocion(boolean esPromocion) {
        this.esPromocion = esPromocion;
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

    public String getPersonalizacion() {
        return personalizacion;
    }

    public void setPersonalizacion(String personalizacion) {
        this.personalizacion = personalizacion;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getEmpresa() {
        return empresa;
    }

    public void setEmpresa(int empresa) {
        this.empresa = empresa;
    }

    //esto es para cuando los productos del pedido no estan disponibles en el establecimiento
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public ArrayList<ProductoAgregados> getAgregados() {
        return agregados;
    }

    public void setAgregados(ArrayList<ProductoAgregados> agregados) {
        this.agregados = agregados;
    }
}
