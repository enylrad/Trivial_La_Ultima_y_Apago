package es.enylrad.game.triviallaultimayapago.Fragments;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.games.Games;

import java.util.ArrayList;

import es.enylrad.game.triviallaultimayapago.BDTrivial;
import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.Objetos.Estadisticas;
import es.enylrad.game.triviallaultimayapago.Otros.MetodosEstaticos;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Clase donde se gestiona  el Modo Desafío del juego.
 */
public class Desafio extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "DESAFIO";

    //////////////////////////////////////PROCESOS/////////////////////////////////////////////////
    private final int puntuacion_minima = 0;            //Puntuacion minima permitida

    ////////////////////////////////////////VARIABLES//////////////////////////////////////////////
    private final int tiempo_inicial_desafio = 60 * 10;       //Tiempo inicial de la partida
    private AsyncTask animacion_inicial;
    private AsyncTask animacion_final;
    //Hilo animacion carga tiempo
    private Thread cargaTiempo_inicio_thread;
    private Handler cargaTimmer_inicio_handler;
    //Hilo tiempo desafio
    private Thread tiempo_partida_thread;
    private Handler tiempo_partida_handler;

    //INTERFAZ
    private ProgressBar barra_progreso_tiempo;        //Variable para la progressbar de la interfaz

    //Textos
    private TextView txt_segundos_progreso;
    private TextView txt_puntuacion;
    private TextView txt_puntuacion_anyadida;             //Variables para los diferentes textos comunes
    private TextView txt_resultado;
    private TextView txt_segundos_anyadidos;
    private TextView txt_pregunta;
    private TextView txt_cuenta_atras;
    private TextView txt_modo;

    //////////////////////////////////////////TABLERO//////////////////////////////////////////////
    //Imagenes
    private ImageView amarillo;
    private ImageView azul_c;
    private ImageView azul_o;
    private ImageView naranja;
    private ImageView verde;
    private ImageView rojo;
    private ImageView rosa;
    private ImageView ic_tema;
    private ImageView fondo;

    //todo hacer una clase
    //////////////////////////////////////////RESPEUSTAS//////////////////////////////////////////
    //RelativeLayout
    private RelativeLayout respuesta1;
    private RelativeLayout respuesta2;
    private RelativeLayout respuesta3;
    private RelativeLayout respuesta4;
    //Imagenes Respeustas
    private ImageView icono_respuesta1;
    private ImageView icono_respuesta2;
    private ImageView icono_respuesta3;
    private ImageView icono_respuesta4;
    private TextView txt_respuesta1;
    private TextView txt_respuesta2;
    private TextView txt_respuesta3;
    private TextView txt_respuesta4;

    ////////////////////////////////////////ANIMACIONES////////////////////////////////////////////
    private AnimatorSet anim_preparacion_texto;
    private AnimatorSet anim_fin_texto;
    private Animation anim_respuestaIcono;
    private Animation anim_mostrarTema;
    private Animation anim_desaparecerTema;
    private Animation anim_preparacion_fondo;
    private Animation anim_fin_fondo;

    ////////////////////////////////////////SONIDOS////////////////////////////////////////////////
    private MediaPlayer ding;
    private MediaPlayer gameover;
    private MediaPlayer success;
    private MediaPlayer failure;
    private MediaPlayer countdown;
    private MediaPlayer time_over;
    private int segundos_actuales;                //Numero de segundos
    private int puntuacion_actual = 0;                   //Puntuacion total conseguida
    private int valor_acierto = 150;                     //Puntuacion inicial que se suma por cada respuesta correcta
    private int aciertos_consecutivos = 0;              //Total de respuestas seguidas

    private int total_aciertos = 0;                     //Aciertos totales en la partida
    private int total_fallos = 0;                       //Fallos totales en la partida
    private int max_aciertos_seguidos = 0;              //Maximos aciertos seguidos en una partida
    private int max_puntos_sumados = 0;                 //Puntos totales que se han sumando durante la partida
    private int max_puntos_restados = 0;                //Puntos totales restados durante una partida

    //Variables Pregunta
    private int posicion_respuesta_correcta;            //Respuesta correcta que la averiguaremos apartir de la base de datos
    private int id_categoria;                           //ID de la categoria de la pregunta

    private boolean pausado = false;

    //Variables segundos
    private String segundos;

    //Datos de la pregunta
    private ArrayList<String> pregunta;

    private Estadisticas estadisticas;

    ///////////////////////////////////////////FRAGMENT///////////////////////////////////////////
    private Main context;
    private View view;
    private BDTrivial bdTrivial;
    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.context = (Main) getActivity();
        this.view = inflater.inflate(R.layout.desafio_fragment, container, false);
        this.mTracker = context.getmTracker();

        // [END shared_tracker]
        mTracker.setScreenName(getClass().getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder()
                .build());

        this.bdTrivial = context.getBase_de_datos_trivial();
        context.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        configurarReferencias();
        iniciarObjetos();
        cargaTiempoDesafioInicio();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configurarListener();
        configurarAnimaciones();

        txt_puntuacion.setText("0");

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Desafio")
                .setAction("Partida nueva")
                .build());
        realizarPreguntaNueva();

        animacion_inicial = new animacionInicioDesafio().execute();

    }

    /**
     * Proceso para buscar pregunta nueva
     */
    private void realizarPreguntaNueva() {

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Desafio")
                .setAction("Pregunta Realizada")
                .build());

        elegirCategoria();
        gestionarImagenesTablero();

        posicion_respuesta_correcta = bdTrivial.buscarymostrarPregunta(pregunta, id_categoria);

        txt_pregunta.setText(pregunta.get(0));

        //provisional, que fueran aleatorias no estaba en mente

        int orden_respuestas[] = generarNumerosAleatoriosArray();

        txt_respuesta1.setText(pregunta.get(orden_respuestas[0]));
        txt_respuesta2.setText(pregunta.get(orden_respuestas[1]));
        txt_respuesta3.setText(pregunta.get(orden_respuestas[2]));
        txt_respuesta4.setText(pregunta.get(orden_respuestas[3]));

        for (int k = 0; k < orden_respuestas.length; k++) {

            if (posicion_respuesta_correcta == (orden_respuestas[k] - 1)) {
                posicion_respuesta_correcta = k;
                break;
            }

        }

    }

    /////////////////////////////////////LISTENERS/////////////////////////////////////////////////

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.respuesta_1:

                if (posicion_respuesta_correcta == 0) {

                    respuestaCorrecta(0);

                } else {

                    respuestaIncorrecta(0);

                }

                break;

            case R.id.respuesta_2:

                if (posicion_respuesta_correcta == 1) {

                    respuestaCorrecta(1);

                } else {

                    respuestaIncorrecta(1);

                }

                break;

            case R.id.respuesta_3:

                if (posicion_respuesta_correcta == 2) {

                    respuestaCorrecta(2);

                } else {

                    respuestaIncorrecta(2);

                }

                break;

            case R.id.respuesta_4:

                if (posicion_respuesta_correcta == 3) {

                    respuestaCorrecta(3);

                } else {

                    respuestaIncorrecta(3);

                }

                break;

        }
    }

    ///////////////////////////////////////LOGICA///////////////////////////////////////////////////

    /**
     * Si la respuesta es correcta se incrementara la puntuación y tiempo además se
     * guardaran las estadisticas pertinentes
     *
     * @param pulsado
     */
    private void respuestaCorrecta(int pulsado) {

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Desafio")
                .setAction("Pregunta Correcta")
                .build());

        int puntuacion_actual_temp = puntuacion_actual;

        //Incrementamos variables de control
        aciertos_consecutivos++;
        //Comprobamos el valor que se le va a sumar
        puntuacionRespuestasConsecutivas();

        max_puntos_sumados = max_puntos_sumados + valor_acierto;

        //Incrementamos estaditiscas
        total_aciertos++;

        //Actualizamos puntuación actual
        puntuacion_actual += valor_acierto;
        MetodosEstaticos.iniciarAnimacionContar(puntuacion_actual_temp, puntuacion_actual, 400, txt_puntuacion);

        //Actualizamos tiempo
        segundos_actuales += 60;

        //Aplicamos al texto
        txt_puntuacion_anyadida.setText("+" + valor_acierto);
        txt_segundos_anyadidos.setText("+" + 6 + "seg");
        txt_resultado.setText(getResources().getString(R.string.correcto));

        //Aplicamos colores
        txt_puntuacion_anyadida.setTextColor(getResources().getColor(R.color.verde));
        txt_segundos_anyadidos.setTextColor(getResources().getColor(R.color.verde));
        txt_resultado.setTextColor(getResources().getColor(R.color.verde));

        //Modificamos estadisticas de Registro
        estadisticas.incrementarRespondidas(true);

        incrementoEstadisticasCategoria(true);
        animacionVariacionPuntuacion(true);

        //Iniciamos el sonido
        success.start();

        //Controlamos que el tiempo no supero los 60 segundos
        if (segundos_actuales > 600) segundos_actuales = 600;

        //Iniciamos la animación
        new animacionResultadoPregunta(pulsado, true).execute();

    }


    /**
     * Si la respuesta es correcta se disminuira la puntuación y tiempo además se
     * guardaran las estadisticas pertinentes
     *
     * @param pulsado
     */
    private void respuestaIncorrecta(int pulsado) {

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Desafio")
                .setAction("Pregunta Incorrecta")
                .build());

        int puntos_restados_error = 150;
        int puntuacion_actual_temp = puntuacion_actual;

        //Comprobamos el valor que se le va a sumar
        puntuacionRespuestasConsecutivas();

        //Comprobamos y asignamos los maximo aciertos seguidos
        if (max_aciertos_seguidos < aciertos_consecutivos)
            max_aciertos_seguidos = aciertos_consecutivos;

        //Incrementamos variables de control
        aciertos_consecutivos = 0;

        //Incrementamos estadisticas
        total_fallos++;

        max_puntos_restados = max_puntos_restados - puntos_restados_error;

        //Comprobamos que no sea la puntuacion minima
        if (puntuacion_actual != puntuacion_minima) {
            puntuacion_actual -= puntos_restados_error;
            MetodosEstaticos.iniciarAnimacionContar(puntuacion_actual_temp, puntuacion_actual, 400, txt_puntuacion);
        }

        //Restamos los segundos por fallar
        segundos_actuales -= 80;

        //Aplicamos texto
        txt_puntuacion_anyadida.setText("-" + puntos_restados_error);
        txt_segundos_anyadidos.setText("-" + 8 + "seg");
        txt_resultado.setText(getResources().getString(R.string.error));

        //Aplicamos color
        txt_puntuacion_anyadida.setTextColor(getResources().getColor(R.color.firebrick));
        txt_segundos_anyadidos.setTextColor(getResources().getColor(R.color.firebrick));
        txt_resultado.setTextColor(getResources().getColor(R.color.firebrick));

        estadisticas.incrementarRespondidas(false);

        incrementoEstadisticasCategoria(false);
        animacionVariacionPuntuacion(false);

        //Iniciamos sonido
        failure.start();

        new animacionResultadoPregunta(pulsado, false).execute();

    }

    /**
     * Puntuaciones que se sumaran dependiendo de los aciertos
     */
    private void puntuacionRespuestasConsecutivas() {

        if (aciertos_consecutivos == 0) {
            valor_acierto = 150;
        } else if (aciertos_consecutivos >= 20) {
            valor_acierto = 2000;
        } else if (aciertos_consecutivos >= 10) {
            valor_acierto = 1000;
        } else if (aciertos_consecutivos >= 6) {
            valor_acierto = 600;
        } else if (aciertos_consecutivos >= 3) {
            valor_acierto = 300;
        }

    }

    private void incrementoEstadisticasCategoria(boolean acierto) {

        switch (id_categoria) {

            case 1:

                estadisticas.incrementarRPGAventurasRespondidas(acierto);

                break;

            case 2:

                estadisticas.incrementarEstrategiaRespondidas(acierto);

                break;

            case 3:

                estadisticas.incrementarLuchaRespondidas(acierto);

                break;

            case 4:

                estadisticas.incrementarPlataformasAventuraRespondidas(acierto);

                break;

            case 5:

                estadisticas.incrementarShooterRespondidas(acierto);

                break;

            case 6:

                estadisticas.incrementarDeportesRespondidas(acierto);

                break;

            case 7:

                estadisticas.incrementarOtrosRespondidas(acierto);

                break;

        }

    }

    private void ejecutarFinPartida() {


        botonesActivos(false);
        barra_progreso_tiempo.setProgress(0);
        txt_segundos_progreso.setText("0.0seg");

        animacion_final = new animacionFinPartida().execute();

    }

    ///////////////////////////////////////ANIMACIONES/////////////////////////////////////////////

    /**
     * Metodo que gestiaona la animacion de cuando una respuesta es correcta o incorrecta
     *
     * @param positivo
     */
    private void animacionVariacionPuntuacion(boolean positivo) {

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.animacion_puntuacion);
        Animation anim_min = AnimationUtils.loadAnimation(context, R.anim.animacion_resta);
        Animation anim_plus = AnimationUtils.loadAnimation(context, R.anim.animacion_suma);

        txt_puntuacion_anyadida.setVisibility(View.VISIBLE);
        txt_resultado.setVisibility(View.VISIBLE);
        txt_segundos_anyadidos.setVisibility(View.VISIBLE);

        if (positivo) {

            txt_puntuacion_anyadida.startAnimation(anim_plus);
            txt_segundos_anyadidos.startAnimation(anim_plus);

        } else {

            txt_puntuacion_anyadida.startAnimation(anim_min);
            txt_segundos_anyadidos.startAnimation(anim_min);

        }

        txt_resultado.startAnimation(anim);

        txt_puntuacion_anyadida.setVisibility(View.INVISIBLE);
        txt_resultado.setVisibility(View.INVISIBLE);
        txt_segundos_anyadidos.setVisibility(View.INVISIBLE);

    }

    ///////////////////////////////////////CONTROL Y GUARDADO /////////////////////////////////////
    private void guardarPuntuaciones() {

        if (context != null) {

            guardarEstadisticasSharedpreferences();
            actualizarLogrosyMarcadores();

        }

    }

    private void guardarEstadisticasSharedpreferences() {

        //Recogemos las estadisticas y sumamos a las actuales
        estadisticas.setRespondidas(context.getSharedpreferences().getInt("respondidas", Main.MODE_PRIVATE) + estadisticas.getRespondidas());
        estadisticas.setAcertadas(context.getSharedpreferences().getInt("acertadas", Main.MODE_PRIVATE) + estadisticas.getAcertadas());

        estadisticas.setRpg_aventuras(context.getSharedpreferences().getInt("rpg_aventuras", Main.MODE_PRIVATE) + estadisticas.getRpg_aventuras());
        estadisticas.setEstrategia(context.getSharedpreferences().getInt("estrategia", Main.MODE_PRIVATE) + estadisticas.getEstrategia());
        estadisticas.setLucha(context.getSharedpreferences().getInt("lucha", Main.MODE_PRIVATE) + estadisticas.getLucha());
        estadisticas.setPlataf_avent(context.getSharedpreferences().getInt("platf_avent", Main.MODE_PRIVATE) + estadisticas.getPlataf_avent());
        estadisticas.setShooter(context.getSharedpreferences().getInt("shooter", Main.MODE_PRIVATE) + estadisticas.getShooter());
        estadisticas.setDeportes(context.getSharedpreferences().getInt("deportes", Main.MODE_PRIVATE) + estadisticas.getDeportes());
        estadisticas.setOtros(context.getSharedpreferences().getInt("otros", Main.MODE_PRIVATE) + estadisticas.getOtros());

        estadisticas.setRpg_aventuras_acertada(context.getSharedpreferences().getInt("rpg_aventuras_acertada", Main.MODE_PRIVATE) + estadisticas.getRpg_aventuras_acertada());
        estadisticas.setEstrategia_acertada(context.getSharedpreferences().getInt("estrategia_acertada", Main.MODE_PRIVATE) + estadisticas.getEstrategia_acertada());
        estadisticas.setLucha_acertada(context.getSharedpreferences().getInt("lucha_acertada", Main.MODE_PRIVATE) + estadisticas.getLucha_acertada());
        estadisticas.setPlataf_avent_acertada(context.getSharedpreferences().getInt("platf_avent_acertada", Main.MODE_PRIVATE) + estadisticas.getPlataf_avent_acertada());
        estadisticas.setShooter_acertada(context.getSharedpreferences().getInt("shooter_acertada", Main.MODE_PRIVATE) + estadisticas.getShooter_acertada());
        estadisticas.setDeportes_acertada(context.getSharedpreferences().getInt("deportes_acertada", Main.MODE_PRIVATE) + estadisticas.getDeportes_acertada());
        estadisticas.setOtros_acertada(context.getSharedpreferences().getInt("otros_acertada", Main.MODE_PRIVATE) + estadisticas.getOtros_acertada());
        //Asignamos los resultados
        SharedPreferences.Editor estadisticas_share = context.getSharedpreferences().edit();
        estadisticas_share.putInt("respondidas", (int) estadisticas.getRespondidas());
        estadisticas_share.putInt("acertadas", (int) estadisticas.getAcertadas());

        estadisticas_share.putInt("rpg_aventuras", (int) estadisticas.getRpg_aventuras());
        estadisticas_share.putInt("estrategia", (int) estadisticas.getEstrategia());
        estadisticas_share.putInt("lucha", (int) estadisticas.getLucha());
        estadisticas_share.putInt("platf_avent", (int) estadisticas.getPlataf_avent());
        estadisticas_share.putInt("shooter", (int) estadisticas.getShooter());
        estadisticas_share.putInt("deportes", (int) estadisticas.getDeportes());
        estadisticas_share.putInt("otros", (int) estadisticas.getOtros());

        estadisticas_share.putInt("rpg_aventuras_acertada", (int) estadisticas.getRpg_aventuras_acertada());
        estadisticas_share.putInt("estrategia_acertada", (int) estadisticas.getEstrategia_acertada());
        estadisticas_share.putInt("lucha_acertada", (int) estadisticas.getLucha_acertada());
        estadisticas_share.putInt("platf_avent_acertada", (int) estadisticas.getPlataf_avent_acertada());
        estadisticas_share.putInt("shooter_acertada", (int) estadisticas.getShooter_acertada());
        estadisticas_share.putInt("deportes_acertada", (int) estadisticas.getDeportes_acertada());
        estadisticas_share.putInt("otros_acertada", (int) estadisticas.getOtros_acertada());

        estadisticas_share.apply();

    }

    private void actualizarLogrosyMarcadores() {

        if (Main.VERSION_BD > 25) {

            if (context.getmGoogleApiClient().isConnected()) {
                //Logros de puntuación
                if (puntuacion_actual >= 160000) {           //Logro de 160000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_brutal));
                }

                if (puntuacion_actual >= 80000) {           //Logro de 80000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_increible));
                }

                if (puntuacion_actual >= 40000) {           //Logro de 40000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_hardcore));
                }

                if (puntuacion_actual >= 20000) {      //Logro de 20000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_avanzado));
                }

                if (puntuacion_actual >= 10000) {      //Logro de 10000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_medio));
                }

                if (puntuacion_actual >= 5000) {      //Logro de 5000 o mas puntos en una partida
                    Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_amateur));

                }

                Games.Achievements.unlock(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_primera_partida));

                //Logro de partidas completadas con 2000 o más puntos
                if (puntuacion_actual >= 2000) {

                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_jugn), 1);
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_vicio), 1);
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_vicio_mximo), 1);

                }

                if (estadisticas.getRpg_aventuras_acertada() > 0) {
                    //RPG y Aventura
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_rpg_y_aventuras_grficas_1), (int) estadisticas.getRpg_aventuras_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_rpg_y_aventuras_grficas_2), (int) estadisticas.getRpg_aventuras_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_rpg_y_aventuras_grficas_3), (int) estadisticas.getRpg_aventuras_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_rpg_y_aventuras_grficas_4), (int) estadisticas.getRpg_aventuras_acertada());
                }

                if (estadisticas.getEstrategia_acertada() > 0) {
                    //Estrategia
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_estrategia_y_moba_1), (int) estadisticas.getEstrategia_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_estrategia_y_moba_2), (int) estadisticas.getEstrategia_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_estrategia_y_moba_3), (int) estadisticas.getEstrategia_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_estrategia_y_moba_4), (int) estadisticas.getEstrategia_acertada());
                }

                if (estadisticas.getLucha_acertada() > 0) {
                    //Lucha
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_lucha_1), (int) estadisticas.getLucha_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_lucha_2), (int) estadisticas.getLucha_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_lucha_3), (int) estadisticas.getLucha_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_lucha_4), (int) estadisticas.getLucha_acertada());
                }

                if (estadisticas.getPlataf_avent_acertada() > 0) {
                    //Platafomras y Aventura
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_plataformas_y_aventuras_1), (int) estadisticas.getPlataf_avent_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_plataformas_y_aventuras_2), (int) estadisticas.getPlataf_avent_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_plataformas_y_aventuras_3), (int) estadisticas.getPlataf_avent_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_plataformas_y_aventuras_4), (int) estadisticas.getPlataf_avent_acertada());
                }

                if (estadisticas.getShooter_acertada() > 0) {
                    //Shooter
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_shooter_1), (int) estadisticas.getShooter_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_shooter_2), (int) estadisticas.getShooter_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_shooter_3), (int) estadisticas.getShooter_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_shooter_4), (int) estadisticas.getShooter_acertada());
                }

                if (estadisticas.getDeportes_acertada() > 0) {
                    //Deportes
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_deportes_1), (int) estadisticas.getDeportes_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_deportes_2), (int) estadisticas.getDeportes_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_deportes_3), (int) estadisticas.getDeportes_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_deportes_4), (int) estadisticas.getDeportes_acertada());
                }

                if (estadisticas.getOtros_acertada() > 0) {
                    //Otros
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_otros_1), (int) estadisticas.getOtros_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_otros_2), (int) estadisticas.getOtros_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_otros_3), (int) estadisticas.getOtros_acertada());
                    Games.Achievements.increment(context.getmGoogleApiClient(), getResources().getString(R.string.achievement_otros_4), (int) estadisticas.getOtros_acertada());

                }

                //Actualizar Puntuaciones de puntuacion máximas y preguntas correctas
                Games.Leaderboards.submitScore(context.getmGoogleApiClient(), getResources().getString(R.string.leaderboard_desafo), puntuacion_actual);

                if (max_aciertos_seguidos > 1) {
                    Games.Leaderboards.submitScore(context.getmGoogleApiClient(), getResources().getString(R.string.leaderboard_racha_de_aciertos), max_aciertos_seguidos);
                }

            }

        } else {

            context.necesitasActualizar();
        }

    }

    //////////////////////////////////////////SUBPROCESOS//////////////////////////////////////////

    /**
     * Este proceso es para controlar el tiempo durante el modo desafío, una vez llegue ha cero finalizará
     * el Modo desafío
     */
    private void tiempoDesafio() {

        segundos_actuales = tiempo_inicial_desafio;

        botonesActivos(true);

        tiempo_partida_thread = new Thread(new Runnable() {

            @Override
            public void run() {

                int tiempo = 0;

                while (segundos_actuales > 0) {

                    tiempo += 1;

                    segundos_actuales -= 1;

                    tiempo_partida_handler.post(new Runnable() {
                        public void run() {

                            try {

                                segundos = String.valueOf(segundos_actuales);

                                if (segundos.length() >= 2) {
                                    segundos = segundos.substring(0, segundos.length() - 1) + "." + segundos.substring(segundos.length() - 1, segundos.length()) + "seg";
                                } else {
                                    segundos = "0." + segundos.substring(segundos.length() - 1, segundos.length()) + "seg";
                                }

                                barra_progreso_tiempo.setProgress(segundos_actuales);
                                txt_segundos_progreso.setText(segundos);

                                //Color de la barra
                                if (segundos_actuales >= 400) {
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_green));
                                } else if (segundos_actuales < 400 && segundos_actuales >= 200) {
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_yellow));
                                    if (time_over.isPlaying()) {
                                        time_over.pause();
                                    }
                                } else if (segundos_actuales < 150) {
                                    if (!time_over.isPlaying() && !pausado) {
                                        time_over.start();
                                    }
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_red));
                                }

                            } catch (IllegalStateException ignored) {
                            }

                        }
                    });


                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }

                }

                if (time_over.isPlaying()) {
                    time_over.stop();
                }

                // Build and send a timing hit.
                mTracker.send(new HitBuilders.TimingBuilder()
                        .setCategory("Desafio")
                        .setValue(tiempo * 100)
                        .setVariable("Tiempo partida")
                        .build());

                Log.d("tiempo", String.valueOf(tiempo * 100));

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        ejecutarFinPartida();
                    }
                });


            }
        });

        tiempo_partida_thread.start();

    }

    /**
     * Este proceso es para mostrar como el tiempo va subiendo durante la animación del inicio del
     * Modo desafío
     */
    private void cargaTiempoDesafioInicio() {

        cargaTiempo_inicio_thread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (segundos_actuales < tiempo_inicial_desafio) {

                    segundos_actuales += 1;

                    cargaTimmer_inicio_handler.post(new Runnable() {
                        public void run() {

                            try {

                                segundos = String.valueOf(segundos_actuales);
                                if (segundos.length() >= 2) {
                                    segundos = segundos.substring(0, segundos.length() - 1) + "." + segundos.substring(segundos.length() - 1, segundos.length()) + "seg";
                                } else {
                                    segundos = "0." + segundos.substring(segundos.length() - 1, segundos.length()) + "seg";
                                }

                                barra_progreso_tiempo.setProgress(segundos_actuales);
                                txt_segundos_progreso.setText(segundos);

                                if (segundos_actuales >= 400) {
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_green));
                                } else if (segundos_actuales < 400 && segundos_actuales >= 200) {
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_yellow));
                                } else if (segundos_actuales < 150) {
                                    barra_progreso_tiempo.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_color_red));
                                }

                            } catch (IllegalStateException ignored) {
                            }

                        }
                    });

                    SystemClock.sleep(10);

                }

            }
        });

        cargaTiempo_inicio_thread.start();

    }

    private void configurarReferencias() {

        //RelativeLayout Button
        respuesta1 = (RelativeLayout) view.findViewById(R.id.respuesta_1);
        respuesta2 = (RelativeLayout) view.findViewById(R.id.respuesta_2);
        respuesta3 = (RelativeLayout) view.findViewById(R.id.respuesta_3);
        respuesta4 = (RelativeLayout) view.findViewById(R.id.respuesta_4);

        //TextView
        txt_puntuacion_anyadida = (TextView) view.findViewById(R.id.punt_suma);
        txt_resultado = (TextView) view.findViewById(R.id.textoresultado);
        txt_segundos_progreso = (TextView) view.findViewById(R.id.segundos);
        txt_segundos_anyadidos = (TextView) view.findViewById(R.id.segundosV);
        txt_puntuacion = (TextView) view.findViewById(R.id.punt_total);
        txt_pregunta = (TextView) view.findViewById(R.id.pregunta);
        txt_cuenta_atras = (TextView) view.findViewById(R.id.cuenta_atras);
        txt_modo = (TextView) view.findViewById(R.id.modo);

        txt_respuesta1 = (TextView) respuesta1.findViewById(R.id.texto_respuesta);
        txt_respuesta2 = (TextView) respuesta2.findViewById(R.id.texto_respuesta);
        txt_respuesta3 = (TextView) respuesta3.findViewById(R.id.texto_respuesta);
        txt_respuesta4 = (TextView) respuesta4.findViewById(R.id.texto_respuesta);

        fondo = (ImageView) view.findViewById(R.id.fondo);

        //Imagenes flechas tema
        amarillo = (ImageView) view.findViewById(R.id.amarillo);
        azul_c = (ImageView) view.findViewById(R.id.azul_c);
        azul_o = (ImageView) view.findViewById(R.id.azul_o);
        naranja = (ImageView) view.findViewById(R.id.naranja);
        verde = (ImageView) view.findViewById(R.id.verde);
        rojo = (ImageView) view.findViewById(R.id.rojo);
        rosa = (ImageView) view.findViewById(R.id.rosa);
        ic_tema = (ImageView) view.findViewById(R.id.ic_tema);

        icono_respuesta1 = (ImageView) respuesta1.findViewById(R.id.ic_respuesta);
        icono_respuesta2 = (ImageView) respuesta2.findViewById(R.id.ic_respuesta);
        icono_respuesta3 = (ImageView) respuesta3.findViewById(R.id.ic_respuesta);
        icono_respuesta4 = (ImageView) respuesta4.findViewById(R.id.ic_respuesta);

        //Animaciones
        anim_preparacion_texto = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.anim_preparacion_texto);
        anim_fin_texto = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.anim_fin_texto);

        anim_respuestaIcono = AnimationUtils.loadAnimation(context, R.anim.anim_ic_aparecer_desaparecer);
        anim_mostrarTema = AnimationUtils.loadAnimation(context, R.anim.anim_tema_mostrar);
        anim_desaparecerTema = AnimationUtils.loadAnimation(context, R.anim.anim_tema_desaparecer);
        anim_preparacion_fondo = AnimationUtils.loadAnimation(context, R.anim.anim_preparacion_fondo);
        anim_fin_fondo = AnimationUtils.loadAnimation(context, R.anim.anim_fin_fondo);

        //Sonidos
        ding = MediaPlayer.create(context, R.raw.sound_ding_inicio);
        gameover = MediaPlayer.create(context, R.raw.sound_game_over);
        countdown = MediaPlayer.create(context, R.raw.sound_countdown);
        success = MediaPlayer.create(context, R.raw.sound_success);
        failure = MediaPlayer.create(context, R.raw.sound_failure);
        time_over = MediaPlayer.create(context, R.raw.sound_timeover);

        barra_progreso_tiempo = (ProgressBar) view.findViewById(R.id.progressBar);

    }

    private void iniciarObjetos() {

        estadisticas = new Estadisticas();
        tiempo_partida_handler = new Handler();
        cargaTimmer_inicio_handler = new Handler();
        pregunta = new ArrayList<>();

    }

    private void configurarListener() {

        respuesta1.setOnClickListener(this);
        respuesta2.setOnClickListener(this);
        respuesta3.setOnClickListener(this);
        respuesta4.setOnClickListener(this);

    }

    /////////////////////////////////////CONFIGURACION INICIAL//////////////////////////////////////

    /**
     * Este metodo configurará las animaciones
     */
    private void configurarAnimaciones() {

        //Animación que visualizará el fondo rojo y el nombre del modo
        anim_preparacion_fondo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //Se hacen visibles las imagenes, tanto el fondo como el titulo del modo
                fondo.setVisibility(View.VISIBLE);
                txt_modo.setVisibility(View.VISIBLE);
                txt_cuenta_atras.setVisibility(View.VISIBLE);

                //Se deshabilitan los botones para que no sean interactivos durante la animación
                botonesActivos(false);

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //Se retira la imagen y la cuenta atrás cuando la animación finalice
                fondo.setVisibility(View.GONE);
                txt_cuenta_atras.setVisibility(View.GONE);
                txt_cuenta_atras.setVisibility(View.GONE);

                if (!pausado)
                    ding.start();

                //Cuando finalice la animación se iniciará el tiempo del modo desafío
                tiempoDesafio();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Animación visual que se iniciará cuando finalice el modo desafío
        anim_fin_fondo.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                //Hacemos visibles el fondo y las letras del modo
                fondo.setVisibility(View.VISIBLE);
                txt_modo.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        pausado = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        //Controlamos diferentes situaciones.
        if (ding.isPlaying())
            ding.pause();

        if (gameover.isPlaying())
            gameover.pause();

        if (countdown.isPlaying())
            countdown.pause();

        if (time_over.isPlaying())
            time_over.pause();

        if (cargaTiempo_inicio_thread != null) {
            cargaTiempo_inicio_thread.interrupt();
        }

        if (tiempo_partida_thread != null) {

            tiempo_partida_thread.interrupt();

            getFragmentManager().popBackStack();

        }

        pausado = true;

    }

    /**
     * Pone las respuestas en un orden aleatorio
     * todo esto se puede mejorar
     *
     * @return
     */
    private int[] generarNumerosAleatoriosArray() {

        int numeros[] = new int[4];

        numeros[0] = (int) (Math.random() * 4) + 1;

        for (int i = 1; i < 4; i++) {

            numeros[i] = (int) (Math.random() * 4) + 1;

            for (int j = 0; j < i; j++) {
                if (numeros[i] == numeros[j]) {
                    i--;
                }
            }
        }

        return numeros;
    }

    //////////////////////////////////////////////ESTADOS///////////////////////////////////////////

    /**
     * Este metodo deshabilitará los botones del modo desafío
     *
     * @param accion
     */
    private void botonesActivos(boolean accion) {

        respuesta1.setClickable(accion);
        respuesta2.setClickable(accion);
        respuesta3.setClickable(accion);
        respuesta4.setClickable(accion);

    }

    public AsyncTask getAnimacion_inicial() {
        return animacion_inicial;
    }

    ///////////////////////////////////GESTIONES SENCILLAS/////////////////////////////////////////

    public AsyncTask getAnimacion_final() {
        return animacion_final;
    }

    /**
     * Este metodo elige una categoría aleatoria de la base de datos y selecciona la imagen a mostrar
     * segun la categoría
     */
    private void elegirCategoria() {

        id_categoria = ((int) (Math.random() * 7) + 1);

        switch (id_categoria) {

            case 1:

                ic_tema.setImageResource(R.drawable.ic_aventuras);

                break;

            case 2:

                ic_tema.setImageResource(R.drawable.ic_estrategia);

                break;

            case 3:

                ic_tema.setImageResource(R.drawable.ic_lucha);

                break;

            case 4:

                ic_tema.setImageResource(R.drawable.ic_accion);

                break;

            case 5:

                ic_tema.setImageResource(R.drawable.ic_shooter);

                break;

            case 6:

                ic_tema.setImageResource(R.drawable.ic_deportes);

                break;

            case 7:

                ic_tema.setImageResource(R.drawable.ic_otros);

                break;

        }

        ic_tema.setVisibility(View.VISIBLE);
        ic_tema.startAnimation(anim_mostrarTema);

    }

    /**
     * Esto gestiona las imagenes que aparecen en el tablero cuando se seleccionan preguntas
     */
    private void gestionarImagenesTablero() {

        switch (id_categoria) {

            case 1:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura_iluminada);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            case 2:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja_iluminada);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            case 3:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja_iluminada);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            case 4:

                amarillo.setImageResource(R.drawable.img_flecha_amarilla_iluminada);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            case 5:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde_iluminada);

                break;

            case 6:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro_iluminada);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            case 7:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa_iluminada);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

            default:

                amarillo.setImageResource(R.drawable.img_flecha_amarrilla);
                azul_c.setImageResource(R.drawable.img_flecha_azul_claro);
                azul_o.setImageResource(R.drawable.img_flecha_azul_oscura);
                naranja.setImageResource(R.drawable.img_flecha_naranja);
                rojo.setImageResource(R.drawable.img_flecha_roja);
                rosa.setImageResource(R.drawable.img_flecha_rosa);
                verde.setImageResource(R.drawable.img_flecha_verde);

                break;

        }
    }

    /**
     * Este metodo es para la animación inicial del Modo Desafío donde se mostrará la cuenta atrás
     * para su inicio
     */
    class animacionInicioDesafio extends AsyncTask<Void, Integer, Void> {

        int cuenta_atras_time;
        String seg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            cuenta_atras_time = 50;

            fondo.startAnimation(anim_preparacion_fondo);

        }

        @Override
        protected Void doInBackground(Void... params) {

            SystemClock.sleep(3000);

            publishProgress(cuenta_atras_time);

            SystemClock.sleep(1000);

            while (cuenta_atras_time >= 0) {

                cuenta_atras_time -= 1;
                publishProgress(cuenta_atras_time);
                SystemClock.sleep(100);

                if (isCancelled())
                    break;


            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            seg = String.valueOf(values[0] / 10);

            if (values[0] == 50) {

                txt_modo.setVisibility(View.GONE);
                txt_cuenta_atras.setVisibility(View.VISIBLE);

                if (!pausado)
                    countdown.start();

                if (Build.VERSION.SDK_INT >= 21) {

                    anim_preparacion_texto.setTarget(txt_cuenta_atras);
                    anim_preparacion_texto.start();

                }

                txt_cuenta_atras.setText(seg);

            } else if (values[0] < 10) {

                txt_cuenta_atras.setText("GO!");

            } else if (values[0] >= 10) {

                txt_cuenta_atras.setText(seg);

            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }


    }

    /**
     * Este metodo será para controlar las animaciones de cuando el usuario responde a las preguntas
     */
    class animacionResultadoPregunta extends AsyncTask<Void, Void, Void> {

        boolean correcta;
        int pulsado;

        public animacionResultadoPregunta(int pulsado, boolean correcta) {

            this.correcta = correcta;
            this.pulsado = pulsado;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            botonesActivos(false);

            ic_tema.startAnimation(anim_desaparecerTema);

            //Inicia animación para ver si es correcta
            animacionElementoPulsado();

            //Inicia animacion en la respuesta correcta
            animacionElementoCorrecto();

        }

        @Override
        protected Void doInBackground(Void... params) {

            SystemClock.sleep(900);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            icono_respuesta1.setVisibility(View.GONE);
            icono_respuesta2.setVisibility(View.GONE);
            icono_respuesta3.setVisibility(View.GONE);
            icono_respuesta4.setVisibility(View.GONE);

            if (barra_progreso_tiempo.getProgress() != 0) { //Para evitar conflictos con el fin de partidaque
                botonesActivos(true);
            }

            //hacemos desaparecer el tema
            ic_tema.setVisibility(View.INVISIBLE);

            realizarPreguntaNueva();

        }

        public void elegirIcono(ImageView img) {

            if (correcta) {

                img.setImageResource(R.drawable.ic_correcto);

            } else {

                img.setImageResource(R.drawable.ic_incorrecto);

            }

        }

        public void animacionElementoPulsado() {

            switch (pulsado) {

                case 0:

                    elegirIcono(icono_respuesta1);
                    icono_respuesta1.setVisibility(View.VISIBLE);
                    icono_respuesta1.startAnimation(anim_respuestaIcono);

                    break;

                case 1:

                    elegirIcono(icono_respuesta2);
                    icono_respuesta2.setVisibility(View.VISIBLE);
                    icono_respuesta2.startAnimation(anim_respuestaIcono);

                    break;

                case 2:

                    elegirIcono(icono_respuesta3);
                    icono_respuesta3.setVisibility(View.VISIBLE);
                    icono_respuesta3.startAnimation(anim_respuestaIcono);

                    break;

                case 3:

                    elegirIcono(icono_respuesta4);
                    icono_respuesta4.setVisibility(View.VISIBLE);
                    icono_respuesta4.startAnimation(anim_respuestaIcono);

                    break;


            }

        }

        public void animacionElementoCorrecto() {


            TransitionDrawable colorAnim;

            switch (posicion_respuesta_correcta) {

                case 0:

                    respuesta1.setBackground(getResources().getDrawable(R.drawable.bg_respuesta_correcta));
                    colorAnim = (TransitionDrawable) respuesta1.getBackground();
                    colorAnim.startTransition(300);
                    colorAnim.reverseTransition(1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            respuesta1.setBackground(getResources().getDrawable(R.drawable.selector_boton_preg));
                        }
                    }, 1300);

                    break;

                case 1:

                    respuesta2.setBackground(getResources().getDrawable(R.drawable.bg_respuesta_correcta));
                    colorAnim = (TransitionDrawable) respuesta2.getBackground();
                    colorAnim.startTransition(300);
                    colorAnim.reverseTransition(1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            respuesta2.setBackground(getResources().getDrawable(R.drawable.selector_boton_preg));
                        }
                    }, 1300);

                    break;

                case 2:

                    respuesta3.setBackground(getResources().getDrawable(R.drawable.bg_respuesta_correcta));
                    colorAnim = (TransitionDrawable) respuesta3.getBackground();
                    colorAnim.startTransition(300);
                    colorAnim.reverseTransition(1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            respuesta3.setBackground(getResources().getDrawable(R.drawable.selector_boton_preg));
                        }
                    }, 1300);

                    break;

                case 3:

                    respuesta4.setBackground(getResources().getDrawable(R.drawable.bg_respuesta_correcta));
                    colorAnim = (TransitionDrawable) respuesta4.getBackground();
                    colorAnim.startTransition(300);
                    colorAnim.reverseTransition(1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            respuesta4.setBackground(getResources().getDrawable(R.drawable.selector_boton_preg));
                        }
                    }, 1300);

                    break;


            }

        }
    }

    /**
     * Este metodo controlará la animación que se mostrará al finalizar el modo desafío.
     */
    class animacionFinPartida extends AsyncTask<Void, Void, Void> {

        int contador = 60;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            guardarPuntuaciones();

            botonesActivos(false);

            txt_modo.setText(getString(R.string.gameover));

            fondo.startAnimation(anim_fin_fondo);

            if (Build.VERSION.SDK_INT >= 21) {
                anim_fin_texto.setTarget(txt_modo);
                anim_fin_texto.start();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            while (contador >= 0) {

                contador -= 1;

                if (contador == 30)
                    if (!pausado)
                        gameover.start();

                SystemClock.sleep(100);

                if (isCancelled())
                    break;


            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Bundle arguments = new Bundle();
            arguments.putInt("total_aciertos", total_aciertos);
            arguments.putInt("total_fallos", total_fallos);
            arguments.putInt("max_aciertos_seguidos", max_aciertos_seguidos);
            arguments.putInt("max_puntos_sumados", max_puntos_sumados);
            arguments.putInt("max_puntos_restados", max_puntos_restados);
            arguments.putInt("total_puntuacion", puntuacion_actual);

            try {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Desafio")
                        .setAction("Partida Finalizada")
                        .build());

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_puntuaciones_go_in, R.anim.slide_puntuaciones_go_out);
                ft.replace(R.id.puntuaciones, new PuntuacionDesafio().newInstance(arguments), PuntuacionDesafio.TAG_FRAGMENT);
                ft.commit();

                getFragmentManager().popBackStack();

            } catch (NullPointerException ignored) {

            } catch (IllegalStateException ignored) {

            }
        }

    }

}