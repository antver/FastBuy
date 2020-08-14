package com.fastbuyapp.omar.fastbuy;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.icu.text.Replaceable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.Operaciones.Calcular_Total;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;

public class CarritoActivity extends AppCompatActivity {

    CarritoListAdapter adapter1 = null;
    ArrayList<PedidoDetalle> list;

    GridView dtgvPedidos;
    TextView txtTotal;

    Calcular_Total calculaTotal = new Calcular_Total();

    @Override
    protected void onResume() {
        super.onResume();
        listaDetallePedido(dtgvPedidos,txtTotal);
        //total del pedido
        Globales globales = new Globales();
        list = globales.getListaPedidosCache("lista_pedidos");
        double total = 0;
        for (int i = 0; i < list.size(); i++){
            total += list.get(i).getTotal();
        }
         txtTotal.setText(String.format("%.2f", total).replace(",","."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigate_before));

        txtTotal = (TextView) findViewById(R.id.txtTotalPedido);

        //Lista de pedidos
        dtgvPedidos = (GridView) findViewById(R.id.dtgvListaPedido);

        //Boton comprar
        Button btnCompra = (Button) findViewById(R.id.btnComprar);
        btnCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globales globales = new Globales();
                list = globales.getListaPedidosCache("lista_pedidos");
                if (list.size() > 0){
                    Intent intent = new Intent(CarritoActivity.this, MetodoDePagoActivity.class);
                    startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(CarritoActivity.this,"¡Carrito Vacio!, añada productos a comprar",Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_yellow);
                    toast.show();
                }
            }
        });

        //botones del menu
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHome);
        ImageButton btnFavoritos = (ImageButton) findViewById(R.id.btnFavoritos);
        ImageButton btnUsuario = (ImageButton) findViewById(R.id.btnUsuario);
        ImageButton btnCarrito = (ImageButton) findViewById(R.id.btnCarrito);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }
        });

        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, FavoritosActivity.class);
                startActivity(intent);
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(CarritoActivity.this, CarritoActivity.class);
                startActivity(intent);*/
            }
        });

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void listaDetallePedido(GridView gridViewX, TextView txtMiTotal){
        list = new ArrayList<PedidoDetalle>();
        try {
            Globales globales = new Globales();
            list = globales.getListaPedidosCache("lista_pedidos");
            adapter1 = new CarritoListAdapter(CarritoActivity.this, R.layout.list_producto_pedido, list, txtMiTotal);
            gridViewX.setAdapter(adapter1);
        }
        catch (Exception ex){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucarrito, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        /*Intent intent = new Intent(CarritoActivity.this,ProductosActivity.class);
        startActivity(intent);*/
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(CarritoActivity.this,PrincipalActivity.class);
        startActivity(intent);
    }
}
