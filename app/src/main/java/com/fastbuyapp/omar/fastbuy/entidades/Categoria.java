package com.fastbuyapp.omar.fastbuy.entidades;

/**
 * Created by OMAR on 23/08/2018.
 */

public class Categoria {
    private int codigo;
    private String descripcion;
    private int estado;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
