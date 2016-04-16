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

    private TextView aciertos;
    private TextView fallos;
    private TextView tseguidas;
    private TextView tsuma;
    private TextView tresta;
    private TextView total;

    private View view;

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

        this.view = inflater.inflate(R.layout.puntuacion_fragment, container, false);

        configurarReferencias();

        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_aciertos"), 1000, aciertos);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_fallos"), 1000, fallos);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_aciertos_seguidos"), 1000, tseguidas);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_puntos_sumados"), 1000, tsuma);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("max_puntos_restados"), 1000, tresta);
        MetodosEstaticos.iniciarAnimacionContar(0, getArguments().getInt("total_puntuacion"), 1000, total);

        return view;
    }

    private void configurarReferencias() {

        //Botones interfaz
        aciertos = (TextView) view.findViewById(R.id.txtnumaciertos);
        fallos = (TextView) view.findViewById(R.id.txtnumfallos);
        tseguidas = (TextView) view.findViewById(R.id.txtnumconsecutivos);
        tsuma = (TextView) view.findViewById(R.id.txtsuma);
        tresta = (TextView) view.findViewById(R.id.txtrestados);
        total = (TextView) view.findViewById(R.id.txttotal);

        view.findViewById(R.id.aceptar).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.aceptar:

                FragmentTransaction ft;
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_puntuaciones_back_in, R.anim.slide_puntuaciones_back_out);
                ft.remove(getFragmentManager().findFragmentByTag(TAG_FRAGMENT));
                ft.commit();

                break;

        }
    }

}

