package es.enylrad.game.triviallaultimayapago.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import es.enylrad.game.triviallaultimayapago.BDTrivial;
import es.enylrad.game.triviallaultimayapago.Main;
import es.enylrad.game.triviallaultimayapago.Otros.StringMD;
import es.enylrad.game.triviallaultimayapago.R;

/**
 * Clase que contiene un formulario donde el usuario podrá rellenar para posteriormente enviarlo
 * a la base de datos del desarrollador
 */
public class EnviarPregunta extends Fragment implements View.OnClickListener {

    public final static String TAG_FRAGMENT = "ENVIAR_PREGUNTA";

    //UI
    private TextView pregM;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    private EditText preg;
    private EditText resp1;
    private EditText resp2;
    private EditText resp3;
    private EditText resp4;
    private EditText juego;

    private Spinner categ;
    private Spinner dific;

    private ArrayAdapter spinner_categoria;
    private ArrayAdapter spinner_dificultad;

    //Variables
    private int respC = -1;
    private int id_cat = 1;
    private int id_dif = 1;

    private View vista;
    private Main context;
    private BDTrivial BDTrivial;

    public EnviarPregunta() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        try {
            context = (Main) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        vista = inflater.inflate(R.layout.enviarpregunta_fragment, container, false);

        pregM = (TextView) vista.findViewById(R.id.pregM);
        btn1 = (Button) vista.findViewById(R.id.boton_resp_1);
        btn2 = (Button) vista.findViewById(R.id.boton_resp_2);
        btn3 = (Button) vista.findViewById(R.id.boton_resp_3);
        btn4 = (Button) vista.findViewById(R.id.boton_resp_4);

        categ = (Spinner) vista.findViewById(R.id.spinCat);
        dific = (Spinner) vista.findViewById(R.id.spinDif);

        preg = (EditText) vista.findViewById(R.id.editpreg);
        resp1 = (EditText) vista.findViewById(R.id.editresp1);
        resp2 = (EditText) vista.findViewById(R.id.editresp2);
        resp3 = (EditText) vista.findViewById(R.id.editresp3);
        resp4 = (EditText) vista.findViewById(R.id.editresp4);
        juego = (EditText) vista.findViewById(R.id.editjuego);

        spinner_categoria = ArrayAdapter.createFromResource(context, R.array.categorias, android.R.layout.simple_spinner_item);
        spinner_dificultad = ArrayAdapter.createFromResource(context, R.array.dificultad, android.R.layout.simple_spinner_item);

        return vista;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Botones interfaz
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        vista.findViewById(R.id.enviar_preg).setOnClickListener(this);

        preg.addTextChangedListener(new MultipleTextWatcher(preg));
        resp1.addTextChangedListener(new MultipleTextWatcher(resp1));
        resp2.addTextChangedListener(new MultipleTextWatcher(resp2));
        resp3.addTextChangedListener(new MultipleTextWatcher(resp3));
        resp4.addTextChangedListener(new MultipleTextWatcher(resp4));

        spinner_categoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categ.setAdapter(spinner_categoria);

        spinner_dificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dific.setAdapter(spinner_dificultad);

        categ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_cat = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dific.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_dif = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BDTrivial = context.getBase_de_datos_trivial();

        context.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.boton_resp_1:

                btn1.setBackgroundColor(getResources().getColor(R.color.verde));
                btn2.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn3.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn4.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                respC = 0;
                break;

            case R.id.boton_resp_2:

                btn2.setBackgroundColor(getResources().getColor(R.color.verde));
                btn1.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn3.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn4.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                respC = 1;
                break;

            case R.id.boton_resp_3:

                btn3.setBackgroundColor(getResources().getColor(R.color.verde));
                btn1.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn2.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn4.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                respC = 2;
                break;

            case R.id.boton_resp_4:

                btn4.setBackgroundColor(getResources().getColor(R.color.verde));
                btn1.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn2.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                btn3.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                respC = 3;
                break;

            case R.id.enviar_preg:

                enviarPregunta();
                break;


        }
    }

    public void enviarPregunta() {

        AlertDialog aviso;

        //Comprobamos que el formulario este completado correctamente
        if (preg.getText().toString().equals("") || resp1.getText().toString().equals("") || resp2.getText().toString().equals("")
                || resp3.getText().toString().equals("") || resp4.getText().toString().equals("") || juego.getText().toString().equals("")) {

            aviso = new AlertDialog.Builder(context).create();
            aviso.setMessage(getResources().getString(R.string.faltan_resp));
            aviso.show();

            //Comprobamos que el usuario a seleccionado alguna respuesta como correcta.
        } else if (respC == -1) {

            aviso = new AlertDialog.Builder(context).create();
            aviso.setMessage(getResources().getString(R.string.falta_correcta));
            aviso.show();

        } else {

            new Consulta_EnviarPregunta(context).execute();

        }

    }

    /**
     * Metodo que nos permite que el texto que el usuario intruduzca posteriormente aparezca en el
     * apartado y botones correspondientes.
     */
    class MultipleTextWatcher implements TextWatcher {

        View view;

        public MultipleTextWatcher(View view) {

            this.view = view;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = s.toString();

            switch (view.getId()) {

                case R.id.editpreg:
                    pregM.setText(text);
                    break;

                case R.id.editresp1:
                    btn1.setText(text);
                    break;

                case R.id.editresp2:
                    btn2.setText(text);
                    break;

                case R.id.editresp3:
                    btn3.setText(text);
                    break;

                case R.id.editresp4:
                    btn4.setText(text);
                    break;
            }

        }
    }

    /**
     * Proceso que enviará el formulario
     */
    class Consulta_EnviarPregunta extends AsyncTask<String, String, String> {

        private AppCompatActivity context;

        Consulta_EnviarPregunta(AppCompatActivity context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            //Se enviará la pregunta, si es correcto el envio se pondran los valores a cero
            if (enviarPregunta()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.envio_correcto), Toast.LENGTH_LONG).show();
                        preg.setText("");
                        resp1.setText("");
                        resp2.setText("");
                        resp3.setText("");
                        resp4.setText("");
                        juego.setText("");
                        respC = -1;
                        btn1.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                        btn2.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                        btn3.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                        btn4.setBackgroundColor(getResources().getColor(R.color.rojogoogleP));
                    }
                });

                //Sino se notificará de que el proceso ha fallado
            } else {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.envio_fallido), Toast.LENGTH_LONG).show();
                    }
                });

            }
            return null;
        }

        /**
         * Metodo que enviará la pregunta al servidor
         *
         * @return Si es correcto será true, si ha surgido algun error será false
         */
        private boolean enviarPregunta() {

            String direccion = context.getResources().getString(R.string.enviar_pregunta);

            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(direccion);

                String clave = StringMD.getStringMessageDigest(context.getResources().getString(R.string.acces_BD), StringMD.SHA1);

                String charset = "UTF-8";
                String datos = "pregunta=" + URLEncoder.encode(preg.getText().toString().trim(), charset);
                datos += "&respuesta1=" + URLEncoder.encode(resp1.getText().toString().trim(), charset);
                datos += "&respuesta2=" + URLEncoder.encode(resp2.getText().toString().trim(), charset);
                datos += "&respuesta3=" + URLEncoder.encode(resp3.getText().toString().trim(), charset);
                datos += "&respuesta4=" + URLEncoder.encode(resp4.getText().toString().trim(), charset);
                datos += "&correcta=" + URLEncoder.encode((String.valueOf(respC)).trim(), charset);
                datos += "&id_cat=" + URLEncoder.encode((String.valueOf(id_cat)).trim(), charset);
                datos += "&preguntada=" + URLEncoder.encode((String.valueOf(0)).trim(), charset);
                datos += "&id_franquicia=" + URLEncoder.encode((String.valueOf(BDTrivial.buscarFranquicia(juego.getText().toString()))).trim(), charset);
                datos += "&id_dif=" + URLEncoder.encode((String.valueOf(id_dif)).trim(), charset);
                datos += "&id_acceso=" + URLEncoder.encode(clave, charset);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(datos.getBytes().length);

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(datos);
                writer.flush();
                writer.close();
                Log.d("datos", datos);

                return true;


            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

        }

    }
}
