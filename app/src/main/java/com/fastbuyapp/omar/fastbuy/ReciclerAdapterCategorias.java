package com.fastbuyapp.omar.fastbuy;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fastbuyapp.omar.fastbuy.entidades.Categoria;
import com.fastbuyapp.omar.fastbuy.config.Globales;

import java.util.ArrayList;

/**
 * Created by OMAR on 12/03/2019.
 */

public class ReciclerAdapterCategorias extends RecyclerView.Adapter<ReciclerAdapterCategorias.MyViewHolder>{
    private ArrayList<Categoria> categoriaList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button btn;
        public MyViewHolder(View v) {
            super(v);
            btn = (Button) v.findViewById(R.id.btnCategoria);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReciclerAdapterCategorias(ArrayList<Categoria> lista) {
        categoriaList = lista;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReciclerAdapterCategorias.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_categoria_item, parent, false);
        return new MyViewHolder(v);
    }
    int selectedPosition=-1;
    boolean clickado = false;
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String texto = categoriaList.get(position).getDescripcion();
        holder.btn.setText(texto);
        holder.btn.setTag(categoriaList.get(position).getCodigo());
        holder.btn.setBackgroundResource(R.drawable.botoncategoria);
        //Toast.makeText(holder.btn.getContext(), String.valueOf(categoriaList.get(position).getCodigo()), Toast.LENGTH_SHORT).show();
        //
        // holder.btn.setBackgroundColor(ContextCompat.getColor(holder.btn.getContext(),R.color.fastbuy));

        /*if(categoriaList.get(position).getCodigo()==Globales.catProductoSeleccionado)
            holder.btn.setBackgroundColor(ContextCompat.getColor(holder.btn.getContext(),R.color.amarillo_patito));
        else{
            holder.btn.setBackgroundColor(ContextCompat.getColor(holder.btn.getContext(),R.color.fastbuy));
        }*/

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnCategoria:
                /*for(int i = 0; i < getItemCount(); i++){
                    Button otrosBtn = (Button) v.findViewById(R.id.btnCategoria); ;
                    otrosBtn.setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.fastbuy));
                    otrosBtn.setTextColor(ContextCompat.getColor(v.getContext(),R.color.blanco));
                    otrosBtn.setPadding(5,0,5,0);
                }*/

                        //notifyDataSetChanged();
                        //v.setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.amarillo_patito));
                        //v.setTextColor(ContextCompat.getColor(v.getContext(),R.color.gris));
                        //v.setPadding(5,0,5,0);*/
                        Globales.catProductoSeleccionado = Integer.parseInt(v.getTag().toString());
                        //final FragmentListaProductos panelListaProductos = new FragmentListaProductos();
                        final FragmentManager manager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                        manager.beginTransaction()
                                //.replace(R.id.PanelProductosLista, panelListaProductos, panelListaProductos.getTag())
                                .commit();
                        break;
                }
            }
        });
       //
        if (clickado == false) {
            holder.btn.performClick();
            clickado = true;
        }
        else{

        }
        //Toast.makeText(holder.btn.getContext(), Globales.catProductoSeleccionado, Toast.LENGTH_SHORT).show();
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

}
