package es.enylrad.game.triviallaultimayapago.Objetos;

public class Pregunta {

    private int id;
    private String pregunta;
    private String resp1;
    private String resp2;
    private String resp3;
    private String resp4;
    private int id_resp;
    private int id_cat;
    private int preguntada;
    private int id_franquicia;
    private int id_dif;

    public Pregunta() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_resp() {
        return id_resp;
    }

    public void setId_resp(int id_resp) {
        this.id_resp = id_resp;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getResp1() {
        return resp1;
    }

    public void setResp1(String resp1) {
        this.resp1 = resp1;
    }

    public String getResp2() {
        return resp2;
    }

    public void setResp2(String resp2) {
        this.resp2 = resp2;
    }

    public String getResp3() {
        return resp3;
    }

    public void setResp3(String resp3) {
        this.resp3 = resp3;
    }

    public String getResp4() {
        return resp4;
    }

    public void setResp4(String resp4) {
        this.resp4 = resp4;
    }

    public int getId_cat() {
        return id_cat;
    }

    public void setId_cat(int id_cat) {
        this.id_cat = id_cat;
    }

    public int getPreguntada() {
        return preguntada;
    }

    public void setPreguntada(int preguntada) {
        this.preguntada = preguntada;
    }

    public int getId_franquicia() {
        return id_franquicia;
    }

    public void setId_franquicia(int id_franquicia) {
        this.id_franquicia = id_franquicia;
    }

    public int getId_dif() {
        return id_dif;
    }

    public void setId_dif(int id_dif) {
        this.id_dif = id_dif;
    }

}
