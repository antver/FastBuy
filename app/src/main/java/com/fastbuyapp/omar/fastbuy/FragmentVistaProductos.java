package com.fastbuyapp.omar.fastbuy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentVistaProductos extends Fragment {

    public FragmentVistaProductos() {
        // Required empty public constructor
    }
    boolean menuAbierto = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_vista_productos, container, false);
        //LinearLayout layout2 = view.findViewById(R.id.PanelProductosLista);
        //layout2.setLayoutParams(params);

        final FragmentListaCategoriasProductos panelCategoriasProductos = new FragmentListaCategoriasProductos();
        final FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.PanelCategoriasGeneral, panelCategoriasProductos, panelCategoriasProductos.getTag())
                .commit();
        /*
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout2 = view.findViewById(R.id.PanelProductosLista);
                //mostrar(view);

                if(menuAbierto == false) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 50
                    );
                    layout2.setLayoutParams(params);
                    menuAbierto = true;
                    //mostrar(view);
                }
                else{

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT - 5
                    );
                    layout2.setLayoutParams(params);
                    menuAbierto = false;
                }


            }
        });*/
        return view;
    }


}
