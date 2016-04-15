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

import com.google.android.gms.games.Games;

import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.R;
import es.enylrad.game.triviallaultimayapago.SubProcesos.ComprobarVersion;

public class MenuPrincipal extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "MENU_PRINCIPAL";

    protected FragmentTransaction ft;
    private View vista;
    private Main context;

    private Button desafio;
    private Button podcast;

    private ImageButton logrosA;
    private ImageButton logrosD;
    private ImageButton marcadA;
    private ImageButton marcadD;
    private ImageButton mostrar_menu_lateral;

    private FrameLayout presentacion;
    private ImageView fondo_presentacion;
    private ImageView logo_presentacion;
    private ImageView flecha_envia;

    private TextView texto_flecha_envia;

    private Animation anim_presentacion;
    private Animation anim_envia_preg;

    private AlertDialog.Builder builder;

    private boolean primer_inicio = true;

    public MenuPrincipal() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        context = (Main) getActivity();

        this.vista = inflater.inflate(R.layout.menu_fragment, container, false);

        configurarReferencias();

        context.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        return vista;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Botones interfaz
        desafio.setOnClickListener(this);
        logrosA.setOnClickListener(this);
        logrosD.setOnClickListener(this);
        marcadA.setOnClickListener(this);
        marcadD.setOnClickListener(this);
        podcast.setOnClickListener(this);
        mostrar_menu_lateral.setOnClickListener(this);

        //Botones para el login de google+
        vista.findViewById(R.id.sign_in_button).setOnClickListener(this);
        vista.findViewById(R.id.sign_out_button).setOnClickListener(this);

        builder = new AlertDialog.Builder(context);

        configurarAnimaciones();

        //context.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (primer_inicio) {

            presentacion.startAnimation(anim_presentacion);

        }
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

            case R.id.logrosA:

                startActivityForResult(Games.Achievements.getAchievementsIntent(context.getmGoogleApiClient()), Main.REQUEST_ACHIEVEMENTS);

                break;
            //Botón de Logros (Deshabilido)
            case R.id.logrosD:

                builder.setMessage(getString(R.string.idemensaglogro))
                        .setTitle(R.string.identificate)
                        .setPositiveButton(getString(R.string.ok), null);
                builder.show();

                break;

            //Boton de Marcadores (Habilitado)
            case R.id.marcadA:

                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(context.getmGoogleApiClient()), Main.REQUEST_LEADERBOARD);

                break;
            //Boton de Marcadores (Deshabilido)
            case R.id.marcadD:

                builder.setMessage(getString(R.string.idemensagmarca))
                        .setTitle(R.string.identificate)
                        .setPositiveButton(getString(R.string.ok), null);
                builder.show();

                break;

            case R.id.podcast:

                try {

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.ivoox.com/podcast-podcast-la-ultima-apago_sq_f1103936_1.html"));
                    startActivity(i);

                } catch (ActivityNotFoundException e) {

                    Toast.makeText(context, "Ninguna aplicación puede atender esta petición,"
                            + " Porfavor instala un navegador web.", Toast.LENGTH_LONG).show();

                    e.printStackTrace();

                }

                break;

            //Boton de inicio de sesión
            case R.id.sign_in_button:
                context.signInClicked();

                break;

            //Boton de cierre de sesión
            case R.id.sign_out_button:
                context.signOutclicked();

                // show sign-in button, hide the sign-out button
                gestionBotones(false);
                break;

            case R.id.mostrar_menu_lateral:

                context.openDrawer();

                break;
        }
    }

    public void gestionBotones(Boolean signin) {


        vista.findViewById(R.id.sign_out_button).setVisibility(signin ? View.VISIBLE : View.GONE);

        logrosA.setVisibility(signin ? View.VISIBLE : View.GONE);
        marcadA.setVisibility(signin ? View.VISIBLE : View.GONE);

        vista.findViewById(R.id.sign_in_button).setVisibility(signin ? View.GONE : View.VISIBLE);

        logrosD.setVisibility(signin ? View.GONE : View.VISIBLE);
        marcadD.setVisibility(signin ? View.GONE : View.VISIBLE);

    }

    public void configurarReferencias() {

        desafio = (Button) vista.findViewById(R.id.desafio);
        mostrar_menu_lateral = (ImageButton) vista.findViewById(R.id.mostrar_menu_lateral);
        logrosA = (ImageButton) vista.findViewById(R.id.logrosA);
        logrosD = (ImageButton) vista.findViewById(R.id.logrosD);
        marcadA = (ImageButton) vista.findViewById(R.id.marcadA);
        marcadD = (ImageButton) vista.findViewById(R.id.marcadD);
        podcast = (Button) vista.findViewById(R.id.podcast);

        presentacion = (FrameLayout) vista.findViewById(R.id.presentacion);
        logo_presentacion = (ImageView) vista.findViewById(R.id.logo_presentacion);
        flecha_envia = (ImageView) vista.findViewById(R.id.flecha_envia_preg);

        texto_flecha_envia = (TextView) vista.findViewById(R.id.texto_flecha_envia);

        anim_presentacion = AnimationUtils.loadAnimation(context, R.anim.anim_presentacion);
        anim_envia_preg = AnimationUtils.loadAnimation(context, R.anim.anim_flecha_envia);

    }

    public void botonesPulsables(boolean pulsable) {

        desafio.setClickable(pulsable);
        logrosA.setClickable(pulsable);
        logrosD.setClickable(pulsable);
        marcadA.setClickable(pulsable);
        marcadD.setClickable(pulsable);
        podcast.setClickable(pulsable);
        mostrar_menu_lateral.setClickable(pulsable);

        vista.findViewById(R.id.sign_in_button).setClickable(pulsable);
        vista.findViewById(R.id.sign_out_button).setClickable(pulsable);
    }

    public void configurarAnimaciones() {

        anim_presentacion.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                presentacion.setVisibility(View.VISIBLE);

                botonesPulsables(false);

                context.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                presentacion.setVisibility(View.GONE);

                primer_inicio = false;

                texto_flecha_envia.startAnimation(anim_envia_preg);
                flecha_envia.startAnimation(anim_envia_preg);

                new ComprobarVersion(context.getBase_de_datos_trivial(), context).execute();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

        if (context.getmGoogleApiClient().isConnected()) {
            gestionBotones(true);
        } else {
            gestionBotones(false);
        }

    }
}
