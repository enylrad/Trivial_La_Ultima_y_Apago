package es.enylrad.game.triviallaultimayapago.Interfaces;

import android.widget.RelativeLayout;

import com.rey.material.widget.ProgressView;

/**
 * Interfaz para comunnicar el Main con los fragment y procesos
 */
public interface Comunicacion {

    ProgressView progressBar();

    RelativeLayout avisoActualizacion();

}


