package com.fastbuyapp.omar.fastbuy.entidades;

public class Establecimiento {
    private String nombre;
    private int cantidadProductos=0;

    public void setCantidad(int numero){
        this.cantidadProductos = numero ;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadProductos() {
        return cantidadProductos;
    }
}
