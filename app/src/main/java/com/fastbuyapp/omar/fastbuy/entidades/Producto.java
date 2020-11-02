package com.fastbuyapp.omar.fastbuy.entidades;

/**
 * Created by OMAR on 23/08/2018.
 */

public class Producto {
    private int codigo;
    private String descripcion;
    private String descripcion2;
    private String imagen;
    private Categoria categoria;
    private Empresa empresa;
    private String precio;
    private int estado;
    private String presentacion;
    private int tiempo;
    private String favorito;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcion2() {
        return descripcion2;
    }

    public void setDescripcion2(String descripcion2) {
        this.descripcion2 = descripcion2;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getPresentacion() {return presentacion;}

    public void setPresentacion(String presentacion) { this.presentacion = presentacion;}

    public int getTiempo() {return tiempo;}

    public void setTiempo(int tiempo) {this.tiempo = tiempo;}

    public String getFavorito() {
        return favorito;
    }

    public void setFavorito(String favorito) {
        this.favorito = favorito;
    }
}
