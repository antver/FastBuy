package com.fastbuyapp.omar.fastbuy.entidades;

public class MiPedido {
    private int codigo;
    private String fecha;
    private String establecimiento1;
    private String establecimiento2;
    private String total;
    private String subTotal;
    private String delivery;
    private String cargo;
    private String descuento;
    private String codigoEmpresa;
    private int estado;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstablecimiento1() {
        return establecimiento1;
    }

    public void setEstablecimiento1(String establecimiento1) {
        this.establecimiento1 = establecimiento1;
    }

    public String getEstablecimiento2() {
        return establecimiento2;
    }

    public void setEstablecimiento2(String establecimiento2) {
        this.establecimiento2 = establecimiento2;
    }

    public String getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(String codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
