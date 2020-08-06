package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseConfirmacion implements Serializable {
    private String empresa;
    private String estado;
    private String pedido;

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }
}
