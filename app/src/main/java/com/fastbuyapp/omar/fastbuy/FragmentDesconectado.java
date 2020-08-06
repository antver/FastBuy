package com.fastbuyapp.omar.fastbuy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastbuyapp.omar.fastbuy.Validaciones.ValidacionDatos;

public class FragmentDesconectado extends Fragment {
    private SwipeRefreshLayout swipeLayout;
    public FragmentDesconectado() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragment_desconectado, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Aqui ejecutamos el codigo necesario para refrescar nuestra interfaz grafica.
                //Antes de ejecutarlo, indicamos al swipe layout que muestre la barra indeterminada de progreso.
                swipeLayout.setRefreshing(true);
                //Vamos a simular un refresco con un handle.
                ValidacionDatos validacion = new ValidacionDatos();
                if(validacion.hayConexiónRed(view.getContext())){
                    /*FragmentCategoriaEmpresas panelCategoriaEmpresas = new FragmentCategoriaEmpresas();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .remove(panelCategoriaEmpresas)
                            .replace(R.id.contenedorPrincipal, panelCategoriaEmpresas, panelCategoriaEmpresas.getTag())
                            .commit();*/
                    recreateActivityCompat(getActivity());
                    //getActivity().recreate();
                    //startActivity(getActivity().getIntent());
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //Se supone que aqui hemos realizado las tareas necesarias de refresco, y que ya podemos ocultar la barra de progreso
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);

            }

        });
        return view;
    }
    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }

}
