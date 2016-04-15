package es.enylrad.game.triviallaultimayapago.Objetos;

import java.io.Serializable;

/**
 * Created by enylr on 10/04/2016.
 */
public class Estadisticas implements Serializable {

    //Variables estadisticas
    private int respondidas = 0;
    private int acertadas = 0;
    private int rpg_aventuras = 0;
    private int estrategia = 0;
    private int lucha = 0;
    private int plataf_avent = 0;
    private int shooter = 0;
    private int deportes = 0;
    private int otros = 0;
    private int rpg_aventuras_acertada = 0;
    private int estrategia_acertada = 0;
    private int lucha_acertada = 0;
    private int plataf_avent_acertada = 0;
    private int shooter_acertada = 0;
    private int deportes_acertada = 0;
    private int otros_acertada = 0;

    public Estadisticas() {

    }

    public Estadisticas(int respondidas, int acertadas, int rpg_aventuras, int estrategia, int lucha, int plataf_avent, int shooter, int deportes, int otros, int rpg_aventuras_acertada, int estrategia_acertada, int lucha_acertada, int plataf_avent_acertada, int shooter_acertada, int deportes_acertada, int otros_acertada) {
        this.respondidas = respondidas;
        this.acertadas = acertadas;
        this.rpg_aventuras = rpg_aventuras;
        this.estrategia = estrategia;
        this.lucha = lucha;
        this.plataf_avent = plataf_avent;
        this.shooter = shooter;
        this.deportes = deportes;
        this.otros = otros;
        this.rpg_aventuras_acertada = rpg_aventuras_acertada;
        this.estrategia_acertada = estrategia_acertada;
        this.lucha_acertada = lucha_acertada;
        this.plataf_avent_acertada = plataf_avent_acertada;
        this.shooter_acertada = shooter_acertada;
        this.deportes_acertada = deportes_acertada;
        this.otros_acertada = otros_acertada;
    }

    public int getRespondidas() {
        return respondidas;
    }

    public void setRespondidas(int respondidas) {
        this.respondidas = respondidas;
    }

    public int getAcertadas() {
        return acertadas;
    }

    public void setAcertadas(int acertadas) {
        this.acertadas = acertadas;
    }

    public int getRpg_aventuras() {
        return rpg_aventuras;
    }

    public void setRpg_aventuras(int rpg_aventuras) {
        this.rpg_aventuras = rpg_aventuras;
    }

    public int getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(int estrategia) {
        this.estrategia = estrategia;
    }

    public int getLucha() {
        return lucha;
    }

    public void setLucha(int lucha) {
        this.lucha = lucha;
    }

    public int getPlataf_avent() {
        return plataf_avent;
    }

    public void setPlataf_avent(int plataf_avent) {
        this.plataf_avent = plataf_avent;
    }

    public int getShooter() {
        return shooter;
    }

    public void setShooter(int shooter) {
        this.shooter = shooter;
    }

    public int getDeportes() {
        return deportes;
    }

    public void setDeportes(int deportes) {
        this.deportes = deportes;
    }

    public int getOtros() {
        return otros;
    }

    public void setOtros(int otros) {
        this.otros = otros;
    }

    public int getRpg_aventuras_acertada() {
        return rpg_aventuras_acertada;
    }

    public void setRpg_aventuras_acertada(int rpg_aventuras_acertada) {
        this.rpg_aventuras_acertada = rpg_aventuras_acertada;
    }

    public int getEstrategia_acertada() {
        return estrategia_acertada;
    }

    public void setEstrategia_acertada(int estrategia_acertada) {
        this.estrategia_acertada = estrategia_acertada;
    }

    public int getLucha_acertada() {
        return lucha_acertada;
    }

    public void setLucha_acertada(int lucha_acertada) {
        this.lucha_acertada = lucha_acertada;
    }

    public int getPlataf_avent_acertada() {
        return plataf_avent_acertada;
    }

    public void setPlataf_avent_acertada(int plataf_avent_acertada) {
        this.plataf_avent_acertada = plataf_avent_acertada;
    }

    public int getShooter_acertada() {
        return shooter_acertada;
    }

    public void setShooter_acertada(int shooter_acertada) {
        this.shooter_acertada = shooter_acertada;
    }

    public int getDeportes_acertada() {
        return deportes_acertada;
    }

    public void setDeportes_acertada(int deportes_acertada) {
        this.deportes_acertada = deportes_acertada;
    }

    public int getOtros_acertada() {
        return otros_acertada;
    }

    public void setOtros_acertada(int otros_acertada) {
        this.otros_acertada = otros_acertada;
    }

    public void incrementarRespondidas(boolean acertada) {

        if (acertada)
            this.acertadas++;

        this.respondidas++;
    }


    public void incrementarRPGAventurasRespondidas(boolean acertada) {
        if (acertada)
            this.rpg_aventuras_acertada++;

        this.rpg_aventuras++;
    }

    public void incrementarEstrategiaRespondidas(boolean acertada) {
        if (acertada)
            this.estrategia_acertada++;

        this.estrategia++;
    }

    public void incrementarLuchaRespondidas(boolean acertada) {
        if (acertada)
            this.lucha_acertada++;

        this.lucha++;
    }

    public void incrementarPlataformasAventuraRespondidas(boolean acertada) {
        if (acertada)
            this.plataf_avent_acertada++;

        this.plataf_avent++;
    }

    public void incrementarShooterRespondidas(boolean acertada) {
        if (acertada)
            this.shooter_acertada++;

        this.shooter++;
    }

    public void incrementarDeportesRespondidas(boolean acertada) {
        if (acertada)
            this.deportes_acertada++;

        this.deportes++;
    }


    public void incrementarOtrosRespondidas(boolean acertada) {
        if (acertada)
            this.otros_acertada++;

        this.otros++;
    }

}
