package es.enylrad.game.triviallaultimayapago.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.enylrad.game.triviallaultimayapago.Otros.MetodosEstaticos;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Esta ventana se mostrará para mostrar la puntuación de la partida
 */
public class PuntuacionDesafio extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "PUNTUACIONES";

    public PuntuacionDesafio() {

    }

    public static PuntuacionDesafio newInstance(Bundle arguments) {
        PuntuacionDesafio p = new PuntuacionDesafio();
        if (arguments != null) {
            p.setArguments(arguments);
        }
        return p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View vista = inflater.inflate(R.layout.puntuacion_fragment, container, false);

        //Botones interfaz
        vista.findViewById(R.id.aceptar).setOnClickListener(this);

        TextView aciertos = (TextView) vista.findViewById(R.id.txtnumaciertos);
        TextView fallos = (TextView) vista.findViewById(R.id.txtnumfallos);
        TextView tseguidas = (TextView) vista.findViewById(R.id.txtnumconsecutivos);
        TextView tsuma = (TextView) vista.findViewById(R.id.txtsuma);
        TextView tresta = (TextView) vista.findViewById(R.id.txtrestados);
        TextView total = (TextView) vista.findViewById(R.id.txttotal);

        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_aciertos"), 1000, aciertos);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_fallos"), 1000, fallos);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_aciertos_seguidos"), 1000, tseguidas);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_puntos_sumados"), 1000, tsuma);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_puntos_restados"), 1000, tresta);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_puntuacion"), 1000, total);

        return vista;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.aceptar) {

            Fragment fragment = getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            if (fragment != null) {

                FragmentTransaction ft;

                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_puntuaciones_back_in, R.anim.slide_puntuaciones_back_out);
                ft.remove(fragment);
                ft.commit();

            }
        }
    }

}

