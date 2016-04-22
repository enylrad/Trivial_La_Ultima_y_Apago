package es.enylrad.game.triviallaultimayapago.SubProcesos;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import es.enylrad.game.triviallaultimayapago.BDTrivial;
import es.enylrad.game.triviallaultimayapago.Fragments.MenuPrincipal;
import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.Otros.StringMD;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Proceso que comprobará si existe una versión nueva en la base de datos.
 */
public class ComprobarVersion extends AsyncTask<String, String, String> {

    private int version = -1;
    private BDTrivial db;
    private Activity context;
    private String direccion;
    private boolean conexion;
    private MenuPrincipal fragment;

    public ComprobarVersion(Activity activity, MenuPrincipal fragment) {
        this.db = ((Main) activity).getBase_de_datos_trivial();
        this.context = activity;
        this.direccion = activity.getResources().getString(R.string.comprobar_version_dir);
        this.fragment = fragment;

    }

    /**
     * Metodo para comprobar si tenemos conexiones a internet
     *
     * @param context
     * @return
     */
    public static boolean comprobarConexionesWifiMobile(Activity context) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {

            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {

                if (ni.isConnected()) {

                    haveConnectedWifi = true;

                }

            }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {

                if (ni.isConnected()) {

                    haveConnectedMobile = true;

                }

            }

        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //comrobamos si tenemos conexión a internet
        conexion = comprobarConexionesWifiMobile(context);

    }

    @Override
    protected String doInBackground(String... params) {

        //Si tenemos conexión procedemos a comprobar la versión
        if (conexion) {
            comprobarVersion();
        }
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (conexion) {
            //Si la versión del servidor es superior se realizará la bajada de preguntas
            if (db.getReadableDatabase().getVersion() < version) {

                new BajarPreguntas(context, version, fragment).execute();

            } else {

                fragment.botonesPulsables(true);
                //((Main) context).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                ((Main) context).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }
        }

    }

    /**
     * En este metodo se comprueba si la versión actual de la base de datos es inferior a la que tenemos
     * en el servidor
     */
    private void comprobarVersion() {

        String data = mostrar();

        if (!data.equalsIgnoreCase("")) {

            JSONObject json;

            try {

                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("version");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    version = jsonArrayChild.optInt("id");

                }


            } catch (JSONException e) {

                e.printStackTrace();

            }
        }

    }

    /**
     * Metodo que configura la conexión para comunicarnos con el servidor
     * se llama en el al metodo convertStreamToString
     *
     * @return
     */
    public String mostrar() {

        HttpURLConnection urlConnection = null;
        String resultado = "";

        try {

            URL url = new URL(direccion);

            String clave = "id_acceso=" + StringMD.getStringMessageDigest(context.getResources().getString(R.string.acces_BD), StringMD.SHA1);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(clave.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            out.write(clave.getBytes());
            out.flush();
            out.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            resultado = convertStreamToString(in);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        Log.d("datos", resultado);

        return resultado;

    }

    /**
     * Convierte los datos JSON a String
     *
     * @param is
     * @return
     * @throws IOException
     */
    private String convertStreamToString(InputStream is) throws IOException {

        if (is != null) {

            StringBuilder sb = new StringBuilder();
            String line;

            try {

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF8"));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }

            return sb.toString();

        } else {

            return "";

        }

    }


}
