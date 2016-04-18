package es.enylrad.game.triviallaultimayapago.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.games.Games;

import es.enylrad.game.triviallaultimayapago.Analytics.AnalyticsApplication;
import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.R;
import es.enylrad.game.triviallaultimayapago.SubProcesos.ComprobarVersion;

public class MenuPrincipal extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "MENU_PRINCIPAL";

    private FragmentTransaction ft;

    //Botones
    private Button desafio;
    private Button podcast;

    //Botones con Imagenes
    private ImageButton logros;
    private ImageButton marcad;
    private ImageButton mostrar_menu_lateral;

    //Imagen inicio aplicacion
    private FrameLayout presentacion;

    //Mensaje enviarpregunta_fragment
    private ImageView flecha_envia;
    private TextView texto_flecha_envia;

    //Animaciones
    private Animation anim_presentacion;
    private Animation anim_envia_preg;

    private AlertDialog.Builder builder;

    //variables
    private boolean primer_inicio = true;       //variable para controlar que que la app arranca de 0

    private View view;
    private Main activity;
    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.activity = (Main) getActivity();
        this.view = inflater.inflate(R.layout.menu_fragment, container, false);

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) activity.getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        activity.sendScreenImageName(getClass().getName());

        configurarReferencias();

        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configListener();

        builder = new AlertDialog.Builder(activity);

        configurarAnimaciones();

        if (primer_inicio) presentacion.startAnimation(anim_presentacion);

    }

    private void configurarReferencias() {

        desafio = (Button) view.findViewById(R.id.desafio);
        mostrar_menu_lateral = (ImageButton) view.findViewById(R.id.mostrar_menu_lateral);
        logros = (ImageButton) view.findViewById(R.id.logros);
        marcad = (ImageButton) view.findViewById(R.id.marcad);
        podcast = (Button) view.findViewById(R.id.podcast);

        presentacion = (FrameLayout) view.findViewById(R.id.presentacion);
        flecha_envia = (ImageView) view.findViewById(R.id.flecha_envia_preg);

        texto_flecha_envia = (TextView) view.findViewById(R.id.texto_flecha_envia);

        anim_presentacion = AnimationUtils.loadAnimation(activity, R.anim.anim_presentacion);
        anim_envia_preg = AnimationUtils.loadAnimation(activity, R.anim.anim_flecha_envia);

    }

    private void configListener() {

        //Botones interfaz
        desafio.setOnClickListener(this);
        logros.setOnClickListener(this);
        marcad.setOnClickListener(this);
        podcast.setOnClickListener(this);
        mostrar_menu_lateral.setOnClickListener(this);

        //Botones para el login de google+
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //Botón de Desafio
            case R.id.desafio:

                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_back_in, R.anim.slide_back_out, R.anim.slide_go_in, R.anim.slide_go_out);
                ft.replace(R.id.container, new Desafio(), Desafio.TAG_FRAGMENT);
                ft.addToBackStack(TAG_FRAGMENT);
                ft.commit();

                break;

            case R.id.logros:

                if (activity.getmGoogleApiClient().isConnected()) {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(activity.getmGoogleApiClient()), Main.REQUEST_ACHIEVEMENTS);
                } else {
                    builder.setMessage(getString(R.string.idemensaglogro))
                            .setTitle(R.string.identificate)
                            .setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                }
                break;

            case R.id.marcad:
                if (activity.getmGoogleApiClient().isConnected()) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(activity.getmGoogleApiClient()), Main.REQUEST_LEADERBOARD);

                } else {
                    builder.setMessage(getString(R.string.idemensagmarca))
                            .setTitle(R.string.identificate)
                            .setPositiveButton(getString(R.string.ok), null);
                    builder.show();
                }
                break;


            case R.id.podcast:

                try {

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.ivoox.com/podcast-podcast-la-ultima-apago_sq_f1103936_1.html"));
                    startActivity(i);

                } catch (ActivityNotFoundException e) {

                    Toast.makeText(activity, "Ninguna aplicación puede atender esta petición,"
                            + " Porfavor instala un navegador web.", Toast.LENGTH_LONG).show();

                    e.printStackTrace();

                }

                break;

            //Boton de inicio de sesión
            case R.id.sign_in_button:
                activity.signInClicked();

                break;

            //Boton de cierre de sesión
            case R.id.sign_out_button:
                activity.signOutclicked();

                // show sign-in button, hide the sign-out button
                gestionBotones(false);
                break;

            case R.id.mostrar_menu_lateral:

                activity.openDrawer();

                break;
        }
    }

    /**
     * Este metodo gestiona los botones para la interfaz, si esta logeado o no
     *
     * @param signin
     */
    public void gestionBotones(boolean signin) {

        view.findViewById(R.id.sign_out_button).setVisibility(signin ? View.VISIBLE : View.GONE);

        logros.setBackground(signin ? getResources().getDrawable(R.drawable.boton) : getResources().getDrawable(R.color.gris));
        marcad.setBackground(signin ? getResources().getDrawable(R.drawable.boton) : getResources().getDrawable(R.color.gris));

        view.findViewById(R.id.sign_in_button).setVisibility(signin ? View.GONE : View.VISIBLE);

    }


    /**
     * Metodo para hacer o no pulsables los botones
     *
     * @param pulsable
     */
    public void botonesPulsables(boolean pulsable) {

        desafio.setClickable(pulsable);
        logros.setClickable(pulsable);
        marcad.setClickable(pulsable);
        podcast.setClickable(pulsable);
        mostrar_menu_lateral.setClickable(pulsable);

        view.findViewById(R.id.sign_in_button).setClickable(pulsable);
        view.findViewById(R.id.sign_out_button).setClickable(pulsable);
    }

    /**
     * Configuración de las animaciones de la aplicación
     */
    public void configurarAnimaciones() {

        //Animacion de inicio de la aplicación
        anim_presentacion.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                presentacion.setVisibility(View.VISIBLE);

                botonesPulsables(false);

                activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                presentacion.setVisibility(View.GONE);

                primer_inicio = false;

                texto_flecha_envia.startAnimation(anim_envia_preg);
                flecha_envia.startAnimation(anim_envia_preg);

                new ComprobarVersion(activity, MenuPrincipal.this).execute();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Animacion de la flecha enviar pregunta
        anim_envia_preg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flecha_envia.setVisibility(View.VISIBLE);
                texto_flecha_envia.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flecha_envia.setVisibility(View.GONE);
                texto_flecha_envia.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (activity.getmGoogleApiClient().isConnected()) {
            gestionBotones(true);
        } else {
            gestionBotones(false);
        }

    }

}
