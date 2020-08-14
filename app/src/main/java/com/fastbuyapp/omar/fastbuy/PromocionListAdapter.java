package com.fastbuyapp.omar.fastbuy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fastbuyapp.omar.fastbuy.config.GlideApp;
import com.fastbuyapp.omar.fastbuy.config.Servidor;
import com.fastbuyapp.omar.fastbuy.config.Globales;
import com.fastbuyapp.omar.fastbuy.entidades.PedidoDetalle;
import com.fastbuyapp.omar.fastbuy.entidades.Promocion;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by OMAR on 31/03/2019.
 */

public class PromocionListAdapter extends ArrayAdapter<Promocion> {
    private Context context;
    private int layout;
    private ArrayList<Promocion> promocionesList;
    private ConstraintLayout capa;
    private Button bt_main;
    private ImageView imagen;
    private EditText campo;
    SharedPreferences.Editor myEditor;
    public PromocionListAdapter(Context context, int layout, ArrayList<Promocion> _promocionesList) {
        super(context, layout, _promocionesList);
        this.context = context;
        this.layout = layout;
        this.promocionesList = _promocionesList;
    }

    @Override
    public int getCount() {
        return promocionesList.size();
    }

    @Override
    public Promocion getItem(int pos) {
        return promocionesList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtDescripcion;
        TextView txtPrecio;
        Button btnMas;
        Button btnMenos;
        Button btnAgregar;
        TextView txtCantidad;
    }

    /*public void cambiarIcono(View v){
        Log.v("Compara", "OK");
        Globales g = Globales.getInstance();
        ImageButton btnCarritoCompras = (ImageButton) v.findViewById(R.id.btnCarritoCompras);
        if(g.getListaPedidos().size() == 0){
            btnCarritoCompras.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito1);
            btnCarritoCompras.setImageBitmap(bmp);
        }
        else{
            btnCarritoCompras.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito_notif);
            btnCarritoCompras.setImageBitmap(bmp);

        }

    }*/

    @Override
    public View getView(int pos, View row, ViewGroup viewGroup) {
        //View row = view;
        PromocionListAdapter.ViewHolder holder = null;
        SharedPreferences myPreferences;
        myPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        myEditor = myPreferences.edit();
        final String ubicacion = myPreferences.getString("ubicacion", "");
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, viewGroup, false);
            holder = new PromocionListAdapter.ViewHolder();
            //holder.txtDescripcion = (TextView) row.findViewById(R.id.txtDescripcionPr);
            //holder.txtPrecio = (TextView) row.findViewById(R.id.txtPrecio);
            //holder.txtCategoria = (TextView) row.findViewById(R.id.txtCategoria);
            holder.imageView = (ImageView) row.findViewById(R.id.imgPromo);
            //holder.btnMas = (Button) row.findViewById(R.id.btnMasPr);
            //holder.btnPersonalizar = (Button) row.findViewById(R.id.btnPersonalizar);
            //holder.btnMenos = (Button) row.findViewById(R.id.btnMenosPr);
            //holder.btnAgregar = (Button) row.findViewById(R.id.btnAgregarPr);
            //holder.txtCantidad = (TextView) row.findViewById(R.id.txtCantidadPr);
            final PromocionListAdapter.ViewHolder finalHolder = holder;
            final FragmentPersonalizarProducto panelPersonalizar = new FragmentPersonalizarProducto();
            //final FragmentManager manager = ((Principal2Activity) context).getSupportFragmentManager();

            final PromocionListAdapter.ViewHolder finalHolder1 = holder;
            holder.btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtCantidad = finalHolder.txtCantidad;
                    int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                    cant++;
                    String ncant = String.valueOf(cant);
                    txtCantidad.setText(ncant);
                }
            });
            holder.btnMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtCantidad = finalHolder.txtCantidad;
                    int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                    cant--;
                    if (cant <= 0)
                        cant = 0;
                    String ncant = String.valueOf(cant);
                    txtCantidad.setText(ncant);
                }
            });
            final View finalRow = row;
            final String longitud_empresa = myPreferences.getString("longitud_empresa", "0");
            final String latitud_empresa = myPreferences.getString("latitud_empresa", "0");
            holder.btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView txtCantidad = finalHolder.txtCantidad;
                    int cant = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                    int position = (int) v.getTag();
                    final Promocion promocion = getItem(position);
                    myEditor.putString("longitud_empresa", String.valueOf(promocion.getEmpresa().getLongitud()));
                    myEditor.putString("latitud_empresa", String.valueOf(promocion.getEmpresa().getLatitud()));
                    myEditor.commit();
                    Globales globales = new Globales();
                    ArrayList<PedidoDetalle> list = globales.getListaPedidosCache("lista_pedidos");
                    //Toast.makeText(getContext(),String.valueOf(Globales.LongitudEmpresaSeleccionada), Toast.LENGTH_SHORT).show();
                    if (cant > 0) {
                        PedidoDetalle pd = new PedidoDetalle();
                        boolean existe = false;
                        int ubic = 0;
                        int cont = 0;
                        int cantActual = 0;
                        for (PedidoDetalle detalle : list) {
                            try {
                                int proCodigo = detalle.getPromocion().getCodigo();
                                int ubi = detalle.getUbicacion();

                                if (proCodigo == promocion.getCodigo() && ubi == Integer.parseInt(ubicacion)) {
                                    existe = true;
                                    ubic = cont;
                                    cantActual = detalle.getCantidad();
                                }
                                cont++;
                            }
                            catch (Exception ex){

                            }
                        }
                        if (existe) {
                            int nc = cant + cantActual;
                            pd.setPromocion(promocion);
                            pd.setCantidad(nc);
                            pd.setEsPromocion(true);
                            pd.setPersonalizacion("Sin personalización");
                            pd.setTotal((float) (Float.valueOf(promocion.getPrecio()) * nc));
                            list.set(ubic, pd);
                            globales.setList("lista_pedidos", list);
                        } else {
                            pd.setPromocion(promocion);
                            pd.setCantidad(cant);
                            pd.setEsPromocion(true);
                            pd.setTotal((float) (Float.valueOf(promocion.getPrecio()) * cant));
                            pd.setPreciounit((float) Float.valueOf(promocion.getPrecio()));
                            pd.setUbicacion(Integer.parseInt(ubicacion));
                            pd.setPersonalizacion("Sin personalización");
                            pd.setLongitud(Double.parseDouble(longitud_empresa));
                            pd.setLatitud(Double.parseDouble(latitud_empresa));
                            globales.addListaPedidos("lista_pedidos", pd);
                            /*
                            Operaciones operaciones = new Operaciones();
                            Ruta ruta = operaciones.calcularDistanciaApiMaps(context, Globales.latitudOrigen, Globales.longitudOrigen,Globales.LatitudEmpresaSeleccionada,Globales.LongitudEmpresaSeleccionada);
                            DistanciaPedidoDetalle distancia = new DistanciaPedidoDetalle();
                            distancia.setEmpresa(Globales.empresaSeleccionada);
                            distancia.setItemDetalle(g.getListaPedidos().size());
                            distancia.setDistancia(ruta.getDistancia());
                            g.agregarDistancia(distancia);

                            Toast.makeText(context, String.valueOf(Globales.LatitudEmpresaSeleccionada), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, String.valueOf(Globales.LongitudEmpresaSeleccionada), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, String.valueOf(ruta.getDistancia()), Toast.LENGTH_SHORT).show();*/

                        }
                        Toast toast = Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        TextView vText = (TextView) toast.getView().findViewById(R.id.message);
                        vText.setTextColor(Color.BLACK);
                        vistaToast.setBackgroundResource(R.drawable.toast_exito);
                        toast.show();
                        txtCantidad.setText("0");
                        Globales ga = Globales.getInstance();
                        //ImageButton btnCarrito = (ImageButton) ((Principal2Activity) context).findViewById(R.id.btnCarritoCompras);
                        if(list.size() == 0){
                            //btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito1);
                            //btnCarrito.setImageBitmap(bmp);
                        }
                        else{
                            //btnCarrito.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blanco));
                            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_carrito_notif);
                            //btnCarrito.setImageBitmap(bmp);

                        }


                    } else {
                        Toast toast = Toast.makeText(context, "Debe ingresar una cantidad válida", Toast.LENGTH_SHORT);
                        View vistaToast = toast.getView();
                        TextView vText = (TextView) toast.getView().findViewById(R.id.message);
                        vText.setTextColor(Color.BLACK);
                        vistaToast.setBackgroundResource(R.drawable.toast_alerta);
                        toast.show();
                    }
                }
            });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            row.setTag(holder);
        } else {
            holder = (PromocionListAdapter.ViewHolder) row.getTag();
        }
        final Promocion promocion = getItem(pos);
        //Toast.makeText(context, String.valueOf(pos),Toast.LENGTH_SHORT).show();
        String fuente = "fonts/Riffic.ttf";
        //this.fuente1 = Typeface.createFromAsset(context.getAssets(), fuente);

        holder.txtDescripcion.setText(promocion.getDescripcion());
        //holder.txtDescripcion.setTypeface(fuente1);
        //holder.txtCategoria.setText(promocion.getCategoria().getDescripcion());
        //holder.txtPrecio.setText("S/ "+ String.format("%.2f",promocion.getPrecio()));
        //holder.txtCantidad.setTag(R.id.txtCantidad);
        holder.btnMas.setTag(Integer.valueOf(pos));
        holder.btnMenos.setTag(Integer.valueOf(pos));
        holder.btnAgregar.setTag(Integer.valueOf(pos));
        String nombreImagen = promocion.getImagen();
        Servidor s = new Servidor();
        String url = "https://"+s.getServidor()+"/promociones/fotos/" + nombreImagen;

        GlideApp.with(context)
                .load(url)
                .centerCrop()
                .override(200, 150)
                .placeholder(R.drawable.loader_img)
                .transform(new RoundedCornersTransformation(20,0, RoundedCornersTransformation.CornerType.TOP))
                .into(holder.imageView);

        return  row;
    }
}