package es.enylrad.game.triviallaultimayapago.SubProcesos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.widget.ProgressView;

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
import es.enylrad.game.triviallaultimayapago.Interfaces.Comunicacion;
import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.Objetos.Pregunta;
import es.enylrad.game.triviallaultimayapago.Otros.StringMD;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Proceso que se encarga de la bajada de las preguntas cuando existe una versión nueva
 */
public class BajarPreguntas extends AsyncTask<Void, Float, Void> {

    private int version = -1;
    private BDTrivial db;
    private Activity activity;
    private String direccion;
    private Comunicacion callback_main;
    private MenuPrincipal fragment;

    private RelativeLayout avisoActualizacion;
    private ProgressView mProgressDialog;

    public BajarPreguntas(Activity activity, int version, MenuPrincipal fragment) {

        this.activity = activity;
        this.version = version;
        this.fragment = fragment;
        this.callback_main = (Main) activity;
        this.db = ((Main) activity).getBase_de_datos_trivial();
        this.direccion = activity.getResources().getString(R.string.bajar_preguntas_bd);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //Se inicia el aviso de que esta actualizando
        avisoActualizacion = callback_main.avisoActualizacion();
        mProgressDialog = callback_main.progressBar();
        avisoActualizacion.setVisibility(View.VISIBLE);

    }

    @Override
    protected Void doInBackground(Void... params) {

        descargarDatos();

        return null;
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        float progreso = (values[0] + 1) / values[1];
        mProgressDialog.setProgress(progreso);
        Log.d("progreso", values[0] + " - " + values[1] + " - " + progreso);

    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);

        //Configuraciones para que vuelva ha funcionar con normalidad
        mProgressDialog.setVisibility(View.GONE);
        avisoActualizacion.setVisibility(View.GONE);
        fragment.botonesPulsables(true);
        ((Main) activity).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Este metodo borra toda la base de datos
     */
    private void borradoBD() {

        db.getWritableDatabase().execSQL("DROP TABLE IF EXISTS franquicia;");

        db.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS franquicia (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT );");

        db.getWritableDatabase().execSQL("DROP TABLE IF EXISTS pregunta;");

        db.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS pregunta (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pregunta TEXT, " +
                "resp1 TEXT, " +
                "resp2 TEXT, " +
                "resp3 TEXT, " +
                "resp4 TEXT, " +
                "resp_correcta INTEGER, " +
                "id_cat INTEGER, " +
                "preguntada INTEGER, " +
                "id_franquicia TEXT, " +
                "id_dif INTEGER, " +
                "FOREIGN KEY (id_cat) REFERENCES categoria(_id), " +
                "FOREIGN KEY (id_franquicia) REFERENCES franquicia(_id), " +
                "FOREIGN KEY (id_dif) REFERENCES dificultad(_id));");

    }

    /**
     * Este metodo descarga los datos del WebService
     */
    private void descargarDatos() {

        String data = mostrar();

        borradoBD();

        if (!data.equalsIgnoreCase("")) {
            JSONObject json;
            try {

                int id;
                String nombre;
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("franquicia");

                Pregunta pregunta;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    id = jsonArrayChild.optInt("id");
                    nombre = jsonArrayChild.optString("nombre");

                    db.insertarFranquicia(id, nombre);

                }

                json = new JSONObject(data);
                jsonArray = json.optJSONArray("preguntas");
                for (int i = 0; i < jsonArray.length(); i++) {

                    pregunta = new Pregunta();
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    pregunta.setId(jsonArrayChild.optInt("id"));
                    pregunta.setPregunta(jsonArrayChild.optString("pregunta"));
                    pregunta.setResp1(jsonArrayChild.optString("respuesta1"));
                    pregunta.setResp2(jsonArrayChild.optString("respuesta2"));
                    pregunta.setResp3(jsonArrayChild.optString("respuesta3"));
                    pregunta.setResp4(jsonArrayChild.optString("respuesta4"));
                    pregunta.setId_resp(jsonArrayChild.optInt("correcta"));
                    pregunta.setId_cat(jsonArrayChild.optInt("id_cat"));
                    pregunta.setPreguntada(jsonArrayChild.optInt("preguntada"));
                    pregunta.setId_franquicia(jsonArrayChild.optInt("id_franquicia"));
                    pregunta.setId_dif(jsonArrayChild.optInt("id_dif"));

                    publishProgress((float) i, (float) jsonArray.length());

                    db.insertarPregunta(pregunta);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            SharedPreferences ver = activity.getSharedPreferences("version", Context.MODE_PRIVATE);
            SharedPreferences.Editor editver = ver.edit();
            editver.putInt("version", version);
            editver.apply();

            Main.VERSION_BD = version;

            db.close();

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

            String clave = "id_acceso=" + StringMD.getStringMessageDigest(activity.getResources().getString(R.string.acces_BD), StringMD.SHA1);
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

        return resultado;

    }

    /**
     * Convierte los datos JSON a String
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
