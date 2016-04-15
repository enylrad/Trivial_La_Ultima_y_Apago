package es.enylrad.game.triviallaultimayapago;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.rey.material.widget.ProgressView;

import java.io.InputStream;

import es.enylrad.game.triviallaultimayapago.Fragments.Desafio;
import es.enylrad.game.triviallaultimayapago.Fragments.EnviarPregunta;
import es.enylrad.game.triviallaultimayapago.Fragments.MenuPrincipal;
import es.enylrad.game.triviallaultimayapago.Fragments.Registros;
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
    // Logcat tag
    private static final String TAG = "MainActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    public static int version;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    protected FragmentTransaction ft;
    private MenuPrincipal menu_principal_fragment;

    ///GOOGLE PLAY API
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = false;
    private boolean mSignInClicked = false;

    //BASE DE DATOS
    private BDTrivial base_de_datos_trivial;

    //MENU LATERAL
    private DrawerLayout mDrawerLayout;

    private RelativeLayout btn_estadisticas;
    private RelativeLayout btn_enviar_preg;
    private RelativeLayout btn_about;

    //PROFILE INFORMATION
    private ImageView imgProfilePic;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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
        version = getSharedPreferences("version", MODE_PRIVATE).getInt("version", BBDD_VERSION);

        //Carga de la base de datos
        base_de_datos_trivial = new BDTrivial(this, version);

        configurarDrawerMenu();

    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public BDTrivial getBase_de_datos_trivial() {
        return base_de_datos_trivial;
    }

    public SharedPreferences getSharedpreferences() {
        return getSharedPreferences("ESTADISTICAS", 0);
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Si pulsamos el botón atras...
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Controla que si el menu lateral esta abierto, al dar al boton atras lo cierre
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                closeDrawers();
                return true;
            }

            //Comprueba diversos eventos, si hay una animacion de inicio o final en el modo desafio no se permite volver atras
            //Si estamos en medio del desafío nos preguntará si estamos eguros
            try {

                Desafio d = (Desafio) getSupportFragmentManager().findFragmentByTag(Desafio.TAG_FRAGMENT);

                if (d.getAnim_ini() != null && d.getAnim_ini().getStatus() == AsyncTask.Status.RUNNING
                        || d.getAnim_fin() != null && d.getAnim_fin().getStatus() == AsyncTask.Status.RUNNING) {

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

                                if (d.getAnim_ini() != null && d.getAnim_ini().getStatus() == AsyncTask.Status.RUNNING
                                        || d.getAnim_fin() != null && d.getAnim_fin().getStatus() == AsyncTask.Status.RUNNING) {


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
    public void onConnected(Bundle connectionHint) {

        //Habilita y deshabilita los botones dependiendo de si estamos logeados
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            try {
                if (Build.VERSION.SDK_INT == 23) {
                    insertDummyContactWrapper();
                } else {
                    getProfileInformation();
                }
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

    //////////////////////////////////////////////MENU LATERAL//////////////////////////////////////

    /**
     * Configuración Menu lateral
     */
    private void configurarDrawerMenu() {

        AppCompatDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);

        btn_estadisticas = (RelativeLayout) findViewById(R.id.boton_estadisticas);
        btn_enviar_preg = (RelativeLayout) findViewById(R.id.boton_enviar_preg);
        btn_about = (RelativeLayout) findViewById(R.id.boton_about);
        imgProfilePic = (ImageView) findViewById(R.id.imagen_usuario);
        txtName = (TextView) findViewById(R.id.nombre_usuario);

        btn_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_go_in, R.anim.slide_go_out, R.anim.slide_back_in, R.anim.slide_back_out)
                        .replace(R.id.container, new Registros(), Registros.TAG_FRAGMENT)
                        .addToBackStack(MenuPrincipal.TAG_FRAGMENT);
                ft.commit();
            }
        });

        btn_enviar_preg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_go_in, R.anim.slide_go_out, R.anim.slide_back_in, R.anim.slide_back_out)
                        .replace(R.id.container, new EnviarPregunta(), EnviarPregunta.TAG_FRAGMENT)
                        .addToBackStack(MenuPrincipal.TAG_FRAGMENT);
                ft.commit();

            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://laultimayapago.wordpress.com/"));
                    startActivity(i);

                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();

                }

            }
        });

    }

    ////////////////////////////////////////PROFILE INFORMATION///////////////////////////////////

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void MenuPrincipalPulsable(boolean click) {
        menu_principal_fragment.botonesPulsables(click);
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

    /**
     * Metodo para pedir permisos
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
                    REQUEST_CODE_ASK_PERMISSIONS);

            return;
        } else {

            getProfileInformation();
        }
    }


    ////////////////////////////////////////PERMISOS///////////////////////////////////////////////

    /**
     * Metodo para recoger el resultado de la decision en los permisos
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    getProfileInformation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "No se mostrará ni foto, ni nombre el menu lateral :(", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}