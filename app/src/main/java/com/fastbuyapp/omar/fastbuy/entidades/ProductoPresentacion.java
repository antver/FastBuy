package com.fastbuyapp.omar.fastbuy.entidades;

public class ProductoPresentacion {
    private int codigo;
    private String descripcion;
    private int estado;
    private String precio;
    private boolean check;

    public int getCodigo() { return codigo; }

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

    public boolean isCheck() { return check; }

    public void setCheck(boolean check) { this.check = check; }

    public String getPrecio() { return precio; }

    public void setPrecio(String precio) { this.precio = precio; }
}
