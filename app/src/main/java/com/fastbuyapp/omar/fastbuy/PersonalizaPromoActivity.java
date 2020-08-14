package com.fastbuyapp.omar.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import java.util.ArrayList;

public class PersonalizaPromoActivity extends AppCompatActivity {

    private int canti = 1;
    int numero_empresas_encarrito, empresa_encarrito;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaliza_promo);

        //Start Diseño de popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        myEditor = myPreferences.edit();
        final String ubicacion = myPreferences.getString("ubicacion", "");

        numero_empresas_encarrito = myPreferences.getInt("numero_empresas_encarrito", 0);
        empresa_encarrito = myPreferences.getInt("empresa_encarrito", 0);

        int ancho = medidasVentana.widthPixels;
        final int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho*0.90), (int)(alto*0.80));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //End Diseño de popup

        //cargar la foto de la Promoción seleccionada
        final ImageView imgPromocion = (ImageView) findViewById(R.id.imgPromo);

        Servidor s = new Servidor();
        String urlImg = "https://"+s.getServidor()+"/promociones/fotos/" + Globales.PromocionPersonalizar.getImagen();
        GlideApp.with(PersonalizaPromoActivity.this)
                .load(urlImg)
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // log exception
                        Log.v("img_promo_carga", "Error loading image", e);
                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgPromocion);

        //Botones de incrementar y disminuir
        ImageButton btnAddCant = (ImageButton) findViewById(R.id.btnAumentarCant);
        ImageButton btnRemoveCant = (ImageButton) findViewById(R.id.btnReducirCant);
        final TextView txtCanti = (TextView) findViewById(R.id.txtCantidadProd);

        btnAddCant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canti+=1;
                if (canti < 10)
                    txtCanti.setText("0"+String.valueOf(canti));
                else
                    txtCanti.setText(String.valueOf(canti));
            }
        });
        btnRemoveCant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canti > 1)
                    canti-=1;
                else
                    canti = 1;

                if (canti < 10)
                    txtCanti.setText("0"+String.valueOf(canti));
                else
                    txtCanti.setText(String.valueOf(canti));
            }
        });

        //Boton Añadir al Carrito
        Button btnAgregaralCar = (Button) findViewById(R.id.btnAgregarAlCarrito);

        btnAgregaralCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean x = false; //esta variable permitira controlar el añadir el producto a la lista
                int codigoEmpresaTemp;
                //almacenamos el nombre de la empresa del producto seleccionado
                Globales globales = new Globales();
                ArrayList<PedidoDetalle> listapedidos = globales.getListaPedidosCache("lista_pedidos");
                codigoEmpresaTemp = Globales.PromocionPersonalizar.getEmpresa().getCodigo();
                if (numero_empresas_encarrito == 0){
                    myEditor.putInt("empresa_encarrito", codigoEmpresaTemp);
                    myEditor.putInt("numero_empresas_encarrito", 1);
                    myEditor.commit();
                    //Globales.codEstablecimiento1 = Globales.PromocionPersonalizar.getEmpresa().getCodigo();
                   // Globales.ubicaEstablecimiento1 = Globales.ubicacion;
                    x = true;
                }else{
                    if(empresa_encarrito == codigoEmpresaTemp)
                        x = true;
                    else
                        x = false;
                }

                if(x){

                    Boolean name = false;
                    String nameProm = Globales.PromocionPersonalizar.getDescripcion();
                    float precio = Float.valueOf(Globales.PromocionPersonalizar.getPrecio());
                    double total = 0;
                    final int size = listapedidos.size();
                    for (int i = 0; i < size; i++)
                    {
                        if(listapedidos.get(i).getPromocion() != null){
                            String nameProm2 = listapedidos.get(i).getPromocion().getDescripcion();
                            if(nameProm == nameProm2){
                                int Cant2 = listapedidos.get(i).getCantidad();
                                canti = canti+Cant2;
                                listapedidos.get(i).setCantidad(canti);
                                total = canti*precio;
                                listapedidos.get(i).setTotal(total);
                                name = true;
                            }
                            globales.setList("lista_pedidos", listapedidos);
                        }
                    }
                    if(!name){
                        PedidoDetalle detallepedido = new PedidoDetalle();
                        Promocion prom = new Promocion();
                        prom = Globales.PromocionPersonalizar;
                        detallepedido.setPromocion(prom);
                        detallepedido.setTiempo(prom.getTiempo());
                        detallepedido.setCantidad(canti);
                        detallepedido.setPreciounit(precio);
                        total = canti*precio;
                        detallepedido.setTotal(total);
                        detallepedido.setPersonalizacion("Sin Personalización");
                        detallepedido.setLatitud(Globales.PromocionPersonalizar.getEmpresa().getLatitud());
                        detallepedido.setLongitud(Globales.PromocionPersonalizar.getEmpresa().getLongitud());
                        detallepedido.setUbicacion(Integer.parseInt(ubicacion));
                        detallepedido.setEsPromocion(true);
                        globales.addListaPedidos("lista_pedidos", detallepedido);
                    }
                    canti = 1;
                    txtCanti.setText("0"+String.valueOf(canti));
                    //Intent intent = new Intent(PersonalizaPromoActivity.this, CarritoActivity.class);
                    //startActivity(intent);
                    Toast toast = Toast.makeText(PersonalizaPromoActivity.this, "Se Añadió al Carrito",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_success);
                    toast.show();
                    onBackPressed();
                }else{
                    Toast toast = Toast.makeText(PersonalizaPromoActivity.this, "No puede añadir productos de establecimientos diferentes",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                    onBackPressed();
                }
            }
        });
    }
}
