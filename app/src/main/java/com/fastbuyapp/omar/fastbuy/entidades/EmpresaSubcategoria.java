package com.fastbuyapp.omar.fastbuy.entidades;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by OMAR on 22/03/2019.
 */

public class EmpresaSubcategoria {
    private int codigo;
    private String descripcion;
    private String imagen;
    private String estado;
    private ArrayList<Empresa> listEmpresas;

    private int posicion;

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(ArrayList<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
