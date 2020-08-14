package com.fastbuyapp.omar.fastbuy;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.entidades.DistanciaPedidoDetalle;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

import java.util.ArrayList;


public class FragmentCarrito extends Fragment {
    /********private Typeface fuente1;
    public ArrayList<PedidoDetalle> listaPedidos;
    public ArrayList<DistanciaPedidoDetalle> listaDistancias;
    GridView gridView;
    CarritoListAdapter adapter = null;

    public FragmentCarrito() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_carrito, container, false);
        String fuente = "fonts/Riffic.ttf";
        fuente1 = Typeface.createFromAsset(getActivity().getAssets(), fuente);
        listarTablaPedidos(view);
        Button btnContinuarCarrito = (Button) view.findViewById(R.id.btnContinuarCarrito);
        btnContinuarCarrito.setTypeface(fuente1);
        Button btnCancelarCarrito = (Button) view.findViewById(R.id.btnCancelarCarrito);
        btnCancelarCarrito.setTypeface(fuente1);
        TextView lblSolicitud = (TextView) view.findViewById(R.id.lblSolicitudCompras);
        lblSolicitud.setTypeface(fuente1);
        TextView lblText2 = (TextView) view.findViewById(R.id.lblText2);
        //lblText2.setTypeface(fuente1);
        //TextView lblTextCostoEnvio = (TextView) view.findViewById(R.id.lblTextCostoEnvio);
        //lblTextCostoEnvio.setTypeface(fuente1);
        //TextView lblTextCostoTotal = (TextView) view.findViewById(R.id.lblTextCostoTotal);
        //lblTextCostoTotal.setTypeface(fuente1);
        btnCancelarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("¿Desea cancelar su pedido?");
                builder.setTitle("Cancelar");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listaPedidos.clear();
                        listaDistancias.clear();
                        listarTablaPedidos(view);
                        Globales ga = Globales.getInstance();
                        //ImageButton btnCarrito = (ImageButton) ((Principal2Activity) getContext()).findViewById(R.id.btnCarritoCompras);
                        if(ga.getListaPedidos().size() == 0){
                            //btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito1);
                            //btnCarrito.setImageBitmap(bmp);
                        }
                        else{
                            //btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito_notif);
                            //btnCarrito.setImageBitmap(bmp);

                        }
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        return view;
    }


    public void listarTablaPedidos(final View view){
        Globales g = Globales.getInstance();
        listaPedidos = g.getListaPedidos();
        listaDistancias = g.getListaDistancias();
        gridView = (GridView) view.findViewById(R.id.gvTablaProductosPedidos);
        //adapter = new CarritoListAdapter(getActivity(), R.layout.list_carrito_item, listaPedidos);
        //gridView.setAdapter(adapter);
        float sumaTotal = 0;
        for (int i = 0; i < listaPedidos.size(); i++) {
            sumaTotal += listaPedidos.get(i).getTotal();
        }

        //TextView lblTextCostoTotal = (TextView) view.findViewById(R.id.lblTextCostoTotal);
        TextView lblTextCostoPedido = (TextView) view.findViewById(R.id.lblTextCostoPedido);
        //TextView lblTextCostoEnvio = (TextView) view.findViewById(R.id.lblTextCostoEnvio);


        Globales.montoCompra = sumaTotal;

        //Globales.montoTotal = total;

        lblTextCostoPedido.setText("S/ " + String.format("%.2f",sumaTotal));
        //lblTextCostoTotal.setText("S/ " + String.format("%.2f",total));

        Button btnContinuar = (Button) view.findViewById(R.id.btnContinuarCarrito);
        Button btnCancelar= (Button) view.findViewById(R.id.btnContinuarCarrito);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaPedidos.size() > 0) {
                    TextView txtBarra = (TextView) getActivity().findViewById(R.id.txtBarra);
                    txtBarra.setText("DATOS");

                    final FragmentDatosPedido panelDatosPedido = new FragmentDatosPedido();
                    final FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.contenedorPrincipal, panelDatosPedido, panelDatosPedido.getTag())
                            .commit();
                }
                else{
                    Toast toast = Toast.makeText(v.getContext(), "Ingrese un item al carrito.", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_warning);
                    toast.show();
                }
            }
        });


    }*******/
}
