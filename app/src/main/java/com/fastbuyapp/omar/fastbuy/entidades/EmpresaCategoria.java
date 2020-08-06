package com.fastbuyapp.omar.fastbuy.entidades;

/**
 * Created by OMAR on 11/03/2019.
 */

public class EmpresaCategoria {
    private int codigo;
    private String descripcion;
    private String imagen;
    private String estado;
    private String ubicacion;

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

}
