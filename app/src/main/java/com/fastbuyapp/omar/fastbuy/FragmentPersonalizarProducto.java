package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;

public class FragmentPersonalizarProducto extends Fragment {
/*********
    public FragmentPersonalizarProducto() {
        // Required empty public constructor
    }
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_personalizar_producto, container, false);

        final TextView txtDescripcion = (TextView) view.findViewById(R.id.txtPersDescripcion);
        final TextView txtCategoria = (TextView) view.findViewById(R.id.txtPersCategoria);
        TextView txtPrecio = (TextView) view.findViewById(R.id.txtPersPrecio);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgPersImagen);
        final TextView txtCantidad = (EditText) view.findViewById(R.id.txtPersCantidad);
        txtDescripcion.setText(Globales.personalizarDescripcion);
        txtCategoria.setText(Globales.personalizarCategoria);
        txtPrecio.setText(Globales.personalizarPrecio);
        txtCantidad.setText(Globales.personalizarCantidad);

        Servidor s = new Servidor();
        String url = "http://"+s.getServidor()+"/productos/fotos/" + Globales.personalizarImagen;

        Button btnMas = (Button) view.findViewById(R.id.btnPersMas);
        Button btnMenos = (Button) view.findViewById(R.id.btnPersMenos);
        Button btnListo = (Button) view.findViewById(R.id.btnListo);

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                cant++;
                String ncant = String.valueOf(cant);
                txtCantidad.setText(ncant);
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                cant--;
                if(cant<=0)
                    cant = 0;
                String ncant = String.valueOf(cant);
                txtCantidad.setText(ncant);
            }
        });

        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                if(Globales.tiendaCerrada == false){
                    TextView txtPersonalizacion = (TextView) view.findViewById(R.id.txtPersonalizacion);

                    if (cant > 0) {

                        PedidoDetalle pd = new PedidoDetalle();
                        Globales g = Globales.getInstance();
                        boolean existe = false;
                        int ubic = 0;
                        int cont = 0;
                        int cantActual = 0;
                        for (PedidoDetalle detalle : g.getListaPedidos()) {
                            try {
                                int proCodigo = detalle.getProducto().getCodigo();
                                int ubi = detalle.getUbicacion();
                                if (proCodigo == Globales.productoPersonalizar.getCodigo() && ubi == Globales.ubicacion) {
                                    existe = true;
                                    ubic = cont;
                                    cantActual = detalle.getCantidad();
                                }
                                cont++;
                            } catch (Exception ex) {

                            }
                        }
                        if (existe) {
                            int nc = cant + cantActual;
                            pd.setProducto(Globales.productoPersonalizar);
                            pd.setPersonalizacion(txtPersonalizacion.getText().toString());
                            pd.setCantidad(nc);
                            pd.setEsPromocion(false);
                            pd.setTotal(Double.parseDouble(Globales.productoPersonalizar.getPrecio()) * nc);
                        } else {
                            pd.setProducto(Globales.productoPersonalizar);
                            pd.setCantidad(cant);
                            pd.setEsPromocion(false);
                            pd.setTotal(Double.parseDouble(Globales.productoPersonalizar.getPrecio()) * cant);
                            pd.setPreciounit((float) Double.parseDouble(Globales.productoPersonalizar.getPrecio()));
                            pd.setUbicacion(Globales.ubicacion);
                            pd.setLongitud(Globales.LongitudEmpresaSeleccionada);
                            pd.setPersonalizacion(txtPersonalizacion.getText().toString());
                            pd.setLatitud(Globales.LatitudEmpresaSeleccionada);
                            g.agregarPedido(pd);
                        }
                        Globales ga = Globales.getInstance();
                        ImageButton btnCarrito = (ImageButton) getActivity().findViewById(R.id.btnCarritoCompras);
                        if(ga.getListaPedidos().size() == 0){
                            btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito1);
                            btnCarrito.setImageBitmap(bmp);
                        }
                        else{
                            //btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito_notif);
                            btnCarrito.setImageBitmap(bmp);

                        }
                        Toast toast = Toast.makeText(getContext(), "Añadido al carrito", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_exito);
                        toast.show();
                        txtCantidad.setText("0");
                        txtPersonalizacion.setText("");
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Debe ingresar una cantidad válida", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        vistaToast.setBackgroundResource(R.drawable.toast_warning);
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getContext(),"Tienda cerrada", Toast.LENGTH_SHORT);
                    View vistaToast = toast.getView();
                    vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                    toast.show();
                }
            }
        });

        return view;
    }
*********/
}
