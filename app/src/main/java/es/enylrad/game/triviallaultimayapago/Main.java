package es.enylrad.game.triviallaultimayapago;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.rey.material.widget.ProgressView;

import es.enylrad.game.triviallaultimayapago.Analytics.AnalyticsApplication;
import es.enylrad.game.triviallaultimayapago.Fragments.Desafio;
import es.enylrad.game.triviallaultimayapago.Fragments.MenuPrincipal;
import es.enylrad.game.triviallaultimayapago.Interfaces.Comunicacion;

/**
 * Clase principal y única activity de la aplicación, gestiona la mayoria de conexiones
 */
public class Main extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Comunicacion {

    //IDS APPI GOOGLE PLAY GAMES
    //LOGROS
    public final static int REQUEST_ACHIEVEMENTS = 10001;
    //PUNTUACIONES
    public final static int REQUEST_LEADERBOARD = 10002;
    public final static int RC_SIGN_IN = 9001;
    //VERSION BASE DE DATOS
    /*Version de la base de datos inicial de la APP que se creo inicialmente, se ira modificando
    con actualizaciones. Este valor no se debe tocar*/
    public final static int BBDD_VERSION = 9;
    private static final String TAG = "MainActivity";

    public static int VERSION_BD;
    private FragmentTransaction ft;
    private MenuPrincipal menu_principal_fragment;

    ///GOOGLE PLAY API
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = false;
    private boolean mSignInClicked = false;

    //BASE DE DATOS
    private BDTrivial base_de_datos_trivial;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //Habilita el control de sonido en la aplicación.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Inicializamos el Fragment del menu principal.
        menu_principal_fragment = new MenuPrincipal();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, menu_principal_fragment, MenuPrincipal.TAG_FRAGMENT)
                .commit();

        //Inicializamos APIS Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();


        //Miramos si existe una versión guardada de la base de datos
        VERSION_BD = getSharedPreferences("version", MODE_PRIVATE).getInt("version", BBDD_VERSION);

        //Carga de la base de datos
        base_de_datos_trivial = new BDTrivial(this, VERSION_BD);

    }

    public BDTrivial getBase_de_datos_trivial() {
        return base_de_datos_trivial;
    }

    public SharedPreferences getSharedpreferences() {
        return getSharedPreferences("ESTADISTICAS", 0);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        switch (requestCode) {

            case RC_SIGN_IN:

                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    // Bring up an error dialog to alert the user that sign-in
                    // failed. The R.string.signin_failure should reference an error
                    // string in your strings.xml file that tells the user they
                    // could not be signed in, such as "Unable to sign in."
                    BaseGameUtils.showActivityResultError(this,
                            requestCode, resultCode, R.string.signin_failure);
                }

                ///GAME API

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Si pulsamos el botón atras...
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Comprueba diversos eventos, si hay una animacion de inicio o final en el modo desafio no se permite volver atras
            //Si estamos en medio del desafío nos preguntará si estamos eguros
            try {

                Desafio d = (Desafio) getSupportFragmentManager().findFragmentByTag(Desafio.TAG_FRAGMENT);

                if (d.getAnimacion_inicial() != null && d.getAnimacion_inicial().getStatus() == AsyncTask.Status.RUNNING
                        || d.getAnimacion_final() != null && d.getAnimacion_final().getStatus() == AsyncTask.Status.RUNNING) {

                    return true;

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage(R.string.aviso_salida_modo_desafio)
                            .setTitle(R.string.atencion);

                    //Botón y onClick que devolvera al fragment anterior
                    builder.setPositiveButton(R.string.salir, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //Esta comprobación se vuelve a hacer para que no se produzca un error en caso de que el tiempo acabe mientras se esta mostrando el Dialog
                            try {

                                Desafio d = (Desafio) getSupportFragmentManager().findFragmentByTag(Desafio.TAG_FRAGMENT);

                                if (d.getAnimacion_inicial() != null && d.getAnimacion_inicial().getStatus() == AsyncTask.Status.RUNNING
                                        || d.getAnimacion_final() != null && d.getAnimacion_final().getStatus() == AsyncTask.Status.RUNNING) {


                                } else {


                                    getSupportFragmentManager().popBackStack();

                                }

                            } catch (Exception ignored) {

                            }
                        }
                    });

                    builder.setNegativeButton(R.string.continuar, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;

                }

            } catch (Exception ignored) {

            }

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    public void necesitasActualizar() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.necesita_actualizar))
                .setTitle(getString(R.string.titulo_necesita_actualizar))
                .setPositiveButton(getString(R.string.ok), null);
        AppCompatDialog dialog = builder.create();
        dialog.show();

    }

    ////////////////////////////////////////CONEXION GOOGLE PLAY GAMES/////////////////////////////


    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        //Habilita y deshabilita los botones dependiendo de si estamos logeados
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            try {
                menu_principal_fragment.gestionBotones(true); //Modificamos los botones

                Log.d("Datos", String.valueOf(Build.VERSION.SDK_INT));

            } catch (ClassCastException ignored) {

            }

        } else {

            try {

                menu_principal_fragment.gestionBotones(false);

            } catch (ClassCastException ignored) {

            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        // Attempt to reconnect
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, String.valueOf(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        try {
            // Put code here to display the sign-in button
            // Sign in has failed. So show the user the sign-in button.
            menu_principal_fragment.gestionBotones(false);
        } catch (ClassCastException ignored) {

        }

    }

    // Call when the sign-in button is clicked
    public void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    public void signOutclicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);

        //Inicializamos API Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }
    ////////////////////////////////////////INTERFACE//////////////////////////////////////////////

    @Override
    public ProgressView progressBar() {
        return (ProgressView) findViewById(R.id.progressBar);
    }

    @Override
    public RelativeLayout avisoActualizacion() {
        return (RelativeLayout) findViewById(R.id.actualizacion);
    }


    public Tracker getmTracker() {
        return mTracker;
    }
}