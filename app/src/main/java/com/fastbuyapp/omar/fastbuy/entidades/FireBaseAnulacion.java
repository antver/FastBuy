package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseAnulacion implements Serializable {
    private String empresa;
    private String pedido;

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }
}
