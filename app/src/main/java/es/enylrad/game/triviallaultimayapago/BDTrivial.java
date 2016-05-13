package es.enylrad.game.triviallaultimayapago;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import es.enylrad.game.triviallaultimayapago.Objetos.Pregunta;

public class BDTrivial extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "trivial";

    public BDTrivial(Context context, int version) {
        super(context, DATABASE_NAME, null, version);

    }

    /**
     * CREACIÓN DE UNA BASE DE DATOS BASICA
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS categoria (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT );");

        db.execSQL("INSERT INTO categoria " +
                "VALUES " +
                "(null, 'RPG y Aventuras Gráficas'), " +
                "(null, 'Estrategia'), " +
                "(null, 'Lucha'), " +
                "(null, 'Plataformas y Aventuras'), " +
                "(null, 'Shooter'), " +
                "(null, 'Deportes'), " +
                "(null, 'Otros');");

        db.execSQL("CREATE TABLE IF NOT EXISTS dificultad (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT );");

        db.execSQL("INSERT INTO dificultad VALUES " +
                "(null, 'Fácil'), " +
                "(null, 'Normal'), " +
                "(null, 'Difícil'), " +
                "(null, 'Muy difícil');");

        db.execSQL("CREATE TABLE IF NOT EXISTS franquicia (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT );");

        db.execSQL("INSERT INTO franquicia VALUES " +
                "(null, 'Final Fantasy'), " +
                "(null, 'Warcraft'), " +
                "(null, 'Pokémon'), " +
                "(null, 'Otros'), " +
                "(null, 'Street Fighter'), " +
                "(null, 'Grand Theft Auto'), " +
                "(null, 'Mario Bross'), " +
                "(null, 'Minecraft'), " +
                "(null, 'Tomb Raider'), " +
                "(null, 'Call of Dutty'), " +
                "(null, 'Mario Kart'), " +
                "(null, 'Resident Evil'), " +
                "(null, 'FIFA'), " +
                "(null, 'Clash of Clans'), " +
                "(null, 'Dragon Quest'), " +
                "(null, 'Killzone'), " +
                "(null, 'Starcraft'), " +
                "(null, 'Metal Gear Solid'), " +
                "(null, 'Gran Turismo'), " +
                "(null, 'Star Wars'), " +
                "(null, 'MegaMan'), " +
                "(null, 'Doom'), " +
                "(null, 'Half Life'), " +
                "(null, 'Pac-Man');");

        db.execSQL("CREATE TABLE IF NOT EXISTS pregunta (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pregunta TEXT, " +
                "resp1 TEXT, " +
                "resp2 TEXT, " +
                "resp3 TEXT, " +
                "resp4 TEXT, " +
                "resp_correcta INTEGER, " +
                "id_cat INTEGER, " +
                "preguntada INTEGER, " +
                "id_franquicia INTEGER, " +
                "id_dif INTEGER, " +
                "FOREIGN KEY (id_cat) REFERENCES categoria(_id), " +
                "FOREIGN KEY (id_franquicia) REFERENCES franquicia(_id), " +
                "FOREIGN KEY (id_dif) REFERENCES dificultad(_id));");

        cargarPreguntas(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Preguntas que se cargaran inicialmente en el trivial, son una pequeña muestra
     *
     * @param db
     */
    private void cargarPreguntas(SQLiteDatabase db) {

        db.execSQL("INSERT INTO pregunta " +
                "VALUES " +
                "(null, '¿Cómo se llama la espada inicial de Cloud en Final Fantasy VII?', 'Espada Mortal', 'Arma Última', 'Ragnarok', 'Murasame', 0, 1 , 0, 1, 1)," +
                "(null, '¿Cuántos años pasaron desde el primer Starcraft al segundo?', '12 años', '10 años', '15 años', '5 años', 0, 2 , 0, 1, 1)," +
                "(null, '¿De qué nacionalidad es Juri de la saga Street Fighter?', 'Corea del Sur', 'Japón', 'China', 'E.E.U.U', 0, 3 , 0, 1, 1)," +
                "(null, 'En el juego Snake Rattle ''n'' Roll de NES, ¿Que palabra podemos ver escrita?', 'NINTENDO', 'GAME BOY', 'SNAKE RATTLE ''N'' ROLL', 'ROLL', 1, 4 , 0, 1, 1)," +
                "(null, '¿De qué saga son los quimeras?', 'Killzone', 'Resistance', 'Call of Duty', 'Battlefield', 1, 5 , 0, 1, 1)," +
                "(null, '¿Qué jugador tenía la portada de FIFA ''97 en España?', 'Erik Thorstvedt', 'Ronald de Boer', 'David Ginola y Bebeto', 'Andreas Möller', 2, 6 , 0, 1, 1)," +
                "(null, '¿Qué videojuego consiste en adivinar la mayor cantidad de juegos que se nos presentan?', 'Master Mind', 'Geek Mind', 'Game Mind', 'Mind Break', 1, 7 , 0, 1, 1);");

    }

    /**
     * Metodo que inserta una franquicia ala base de datos
     *
     * @param id
     * @param franquicia
     */
    public void insertarFranquicia(int id, String franquicia) {

        String sql = "INSERT INTO franquicia VALUES (?, ?);";

        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindLong(1, id);
        stmt.bindString(2, franquicia);

        stmt.execute();

        db.close();
    }


    /**
     * Inserta una pregunta a la base de datos
     *
     * @param p
     */
    public void insertarPregunta(Pregunta p) {

        // you can use INSERT only
        String sql = "INSERT INTO pregunta VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindLong(1, p.getId());
        stmt.bindString(2, p.getPregunta());
        stmt.bindString(3, p.getResp1());
        stmt.bindString(4, p.getResp2());
        stmt.bindString(5, p.getResp3());
        stmt.bindString(6, p.getResp4());
        stmt.bindLong(7, p.getId_resp());
        stmt.bindLong(8, p.getId_cat());
        stmt.bindLong(9, p.getPreguntada());
        stmt.bindLong(10, p.getId_franquicia());
        stmt.bindLong(11, p.getId_dif());

        stmt.execute();

        db.close();
    }

    /**
     * Metodo que busca una pregunta en la base de datos
     * Tambien se llama en ella al metodo marcarPregunta
     * En caso de que esten todas marcadas las resetea
     *
     * @param result
     * @param numcat
     * @return
     */
    public int buscarymostrarPregunta(ArrayList<String> result, int numcat) {

        boolean encontrado;
        int respcorrect = -1;
        int id = -1;

        do {

            SQLiteDatabase db = this.getReadableDatabase();

            result.removeAll(result);

            encontrado = false;

            Cursor cursor = db.rawQuery("SELECT  _id, pregunta, resp1, resp2, resp3, resp4, resp_correcta " +
                    "FROM pregunta " +
                    "WHERE id_cat = " + numcat + " " +
                    "AND preguntada = 0 " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 1;", null);

            while (cursor.moveToNext()) {

                id = cursor.getInt(0);
                result.add(cursor.getString(1));
                result.add(cursor.getString(2));
                result.add(cursor.getString(3));
                result.add(cursor.getString(4));
                result.add(cursor.getString(5));
                respcorrect = cursor.getInt(6);

                encontrado = true;

            }

            //Si no encuentra preguntas que no se hayan preguntado, resetea el marcador
            if (!encontrado) {

                resetearPreguntasCategoria(numcat);

            }

            cursor.close();
            db.close();

        } while (!encontrado);

        marcarPregunta(id);

        return respcorrect;

    }

    /**
     * Este metodo marca a la pregunta para que no se vuelva a preguntar
     *
     * @param id_preg
     */
    private void marcarPregunta(int id_preg) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Actualizamos esta base de datos para saber las preguntas que hay actualmente hechas
        db.execSQL("UPDATE pregunta " +
                "SET preguntada = 1 " +
                "WHERE _id = " + id_preg);

        db.close();
    }

    /**
     * Este metodo resetea las preguntas marcadas con marcarPregunta
     *
     * @param numcat
     */
    private void resetearPreguntasCategoria(int numcat) {

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE pregunta " +
                "SET preguntada = 0 " +
                "WHERE id_cat = " + numcat);

        db.close();

    }

    /**
     * Este metodo recibe un String y comprueba si la franquicia existe, si existe devuelve el id
     * de la franquicia.
     *
     * @param franquicia
     * @return
     */
    public int buscarFranquicia(String franquicia) {

        franquicia = franquicia.trim();

        int id_franquicia = 4;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id " +
                "FROM franquicia " +
                "WHERE nombre LIKE '%" + franquicia + "%';", null);

        while (cursor.moveToNext()) {

            id_franquicia = cursor.getInt(0);

        }

        cursor.close();
        db.close();

        return id_franquicia;

    }

}
