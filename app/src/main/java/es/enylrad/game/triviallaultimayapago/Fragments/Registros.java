package es.enylrad.game.triviallaultimayapago.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.Objetos.Estadisticas;
import es.enylrad.game.triviallaultimayapago.Otros.MetodosEstaticos;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Este metodo mostrará las puntuaciones en porcentajes que se han ido almacenando en el dispositivo
 */
public class Registros extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "ESTADISTICAS";

    //Textos
    private TextView respondidas;
    private TextView acertadas;
    private TextView rpg_aventuras;
    private TextView estrategia;
    private TextView lucha;
    private TextView plataf_avent;
    private TextView shooter;
    private TextView deportes;
    private TextView otros;

    //variables estadisticas
    private Estadisticas estadisticas;

    private Main activity;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.estadisticas = new Estadisticas();
        this.activity = (Main) getActivity();
        this.view = inflater.inflate(R.layout.estadisticas_fragment, container, false);

        configurarRegistro();

        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        return view;
    }

    private void configurarRegistro() {

        respondidas = (TextView) view.findViewById(R.id.respondidas);
        acertadas = (TextView) view.findViewById(R.id.acertadas);
        rpg_aventuras = (TextView) view.findViewById(R.id.rpg_aventras_graficas);
        estrategia = (TextView) view.findViewById(R.id.estrategia);
        lucha = (TextView) view.findViewById(R.id.lucha);
        plataf_avent = (TextView) view.findViewById(R.id.plataformas_aventuras);
        shooter = (TextView) view.findViewById(R.id.shooter);
        deportes = (TextView) view.findViewById(R.id.deportes);
        otros = (TextView) view.findViewById(R.id.otros);

        view.findViewById(R.id.aceptar).setOnClickListener(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //todo serializar todo esto

        estadisticas.setRespondidas(activity.getSharedpreferences().getInt("respondidas", Context.MODE_PRIVATE));
        estadisticas.setAcertadas(activity.getSharedpreferences().getInt("acertadas", Context.MODE_PRIVATE));

        estadisticas.setRpg_aventuras(activity.getSharedpreferences().getInt("rpg_aventuras", Context.MODE_PRIVATE));
        estadisticas.setEstrategia(activity.getSharedpreferences().getInt("estrategia", Context.MODE_PRIVATE));
        estadisticas.setLucha(activity.getSharedpreferences().getInt("lucha", Context.MODE_PRIVATE));
        estadisticas.setPlataf_avent(activity.getSharedpreferences().getInt("platf_avent", Context.MODE_PRIVATE));
        estadisticas.setShooter(activity.getSharedpreferences().getInt("shooter", Context.MODE_PRIVATE));
        estadisticas.setDeportes(activity.getSharedpreferences().getInt("deportes", Context.MODE_PRIVATE));
        estadisticas.setOtros(activity.getSharedpreferences().getInt("otros", Context.MODE_PRIVATE));

        estadisticas.setRpg_aventuras_acertada(activity.getSharedpreferences().getInt("rpg_aventuras_acertada", Context.MODE_PRIVATE));
        estadisticas.setEstrategia_acertada(activity.getSharedpreferences().getInt("estrategia_acertada", Context.MODE_PRIVATE));
        estadisticas.setLucha_acertada(activity.getSharedpreferences().getInt("lucha_acertada", Context.MODE_PRIVATE));
        estadisticas.setPlataf_avent_acertada(activity.getSharedpreferences().getInt("platf_avent_acertada", Context.MODE_PRIVATE));
        estadisticas.setShooter_acertada(activity.getSharedpreferences().getInt("shooter_acertada", Context.MODE_PRIVATE));
        estadisticas.setDeportes_acertada(activity.getSharedpreferences().getInt("deportes_acertada", Context.MODE_PRIVATE));
        estadisticas.setOtros_acertada(activity.getSharedpreferences().getInt("otros_acertada", Context.MODE_PRIVATE));

        MetodosEstaticos.iniciarAnimacionContar(0, estadisticas.getRespondidas(), 1000, respondidas);

        int dato;

        try {
            dato = (estadisticas.getAcertadas() / estadisticas.getRespondidas()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, acertadas, "%");
        } catch (ArithmeticException e) {
            acertadas.setText("0%");
        }

        try {
            dato = (estadisticas.getRpg_aventuras_acertada() / estadisticas.getRpg_aventuras()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, rpg_aventuras, "%");
        } catch (ArithmeticException e) {
            rpg_aventuras.setText("0%");
        }

        try {
            dato = (estadisticas.getEstrategia_acertada() / estadisticas.getEstrategia()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, estrategia, "%");
        } catch (ArithmeticException e) {
            estrategia.setText("0%");
        }

        try {
            dato = (estadisticas.getLucha_acertada() / estadisticas.getLucha()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, lucha, "%");
        } catch (ArithmeticException e) {
            lucha.setText("0%");
        }

        try {
            dato = (estadisticas.getPlataf_avent_acertada() / estadisticas.getPlataf_avent()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, plataf_avent, "%");
        } catch (ArithmeticException e) {
            plataf_avent.setText("0%");
        }

        try {
            dato = (estadisticas.getShooter_acertada() / estadisticas.getShooter()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, shooter, "%");
        } catch (ArithmeticException e) {
            shooter.setText("0%");
        }

        try {
            dato = (estadisticas.getDeportes_acertada() / estadisticas.getDeportes()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, deportes, "%");
        } catch (ArithmeticException e) {
            deportes.setText("0%");
        }

        try {
            dato = (estadisticas.getOtros_acertada() / estadisticas.getOtros()) * 100;
            MetodosEstaticos.iniciarAnimacionContar(0, dato, 1000, otros, "%");
        } catch (ArithmeticException e) {
            otros.setText("0%");
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.aceptar:

                getFragmentManager().popBackStack();
                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                break;

        }

    }
}