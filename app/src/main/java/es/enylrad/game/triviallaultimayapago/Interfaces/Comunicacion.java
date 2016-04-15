package es.enylrad.game.triviallaultimayapago.Interfaces;

import android.widget.RelativeLayout;

import com.rey.material.widget.ProgressView;

/**
 * Created by enylr on 08/04/2016.
 */
public interface Comunicacion {

    ProgressView progressBar();

    RelativeLayout avisoActualizacion();

    void MenuPrincipalPulsable(boolean click);

}


