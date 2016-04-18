package es.enylrad.game.triviallaultimayapago.Objetos;

import java.io.Serializable;

/**
 * Created by enylr on 10/04/2016.
 */
public class Estadisticas implements Serializable {

    //Variables estadisticas
    private double respondidas = 0;
    private double acertadas = 0;
    private double rpg_aventuras = 0;
    private double estrategia = 0;
    private double lucha = 0;
    private double plataf_avent = 0;
    private double shooter = 0;
    private double deportes = 0;
    private double otros = 0;
    private double rpg_aventuras_acertada = 0;
    private double estrategia_acertada = 0;
    private double lucha_acertada = 0;
    private double plataf_avent_acertada = 0;
    private double shooter_acertada = 0;
    private double deportes_acertada = 0;
    private double otros_acertada = 0;

    public Estadisticas() {

    }

    public Estadisticas(double acertadas, double deportes, double deportes_acertada, double estrategia, double estrategia_acertada, double lucha, double lucha_acertada, double otros, double otros_acertada, double plataf_avent, double plataf_avent_acertada, double respondidas, double rpg_aventuras, double rpg_aventuras_acertada, double shooter, double shooter_acertada) {
        this.acertadas = acertadas;
        this.deportes = deportes;
        this.deportes_acertada = deportes_acertada;
        this.estrategia = estrategia;
        this.estrategia_acertada = estrategia_acertada;
        this.lucha = lucha;
        this.lucha_acertada = lucha_acertada;
        this.otros = otros;
        this.otros_acertada = otros_acertada;
        this.plataf_avent = plataf_avent;
        this.plataf_avent_acertada = plataf_avent_acertada;
        this.respondidas = respondidas;
        this.rpg_aventuras = rpg_aventuras;
        this.rpg_aventuras_acertada = rpg_aventuras_acertada;
        this.shooter = shooter;
        this.shooter_acertada = shooter_acertada;
    }

    public double getShooter_acertada() {
        return shooter_acertada;
    }

    public void setShooter_acertada(double shooter_acertada) {
        this.shooter_acertada = shooter_acertada;
    }

    public double getAcertadas() {
        return acertadas;
    }

    public void setAcertadas(double acertadas) {
        this.acertadas = acertadas;
    }

    public double getDeportes() {
        return deportes;
    }

    public void setDeportes(double deportes) {
        this.deportes = deportes;
    }

    public double getDeportes_acertada() {
        return deportes_acertada;
    }

    public void setDeportes_acertada(double deportes_acertada) {
        this.deportes_acertada = deportes_acertada;
    }

    public double getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(double estrategia) {
        this.estrategia = estrategia;
    }

    public double getEstrategia_acertada() {
        return estrategia_acertada;
    }

    public void setEstrategia_acertada(double estrategia_acertada) {
        this.estrategia_acertada = estrategia_acertada;
    }

    public double getLucha() {
        return lucha;
    }

    public void setLucha(double lucha) {
        this.lucha = lucha;
    }

    public double getLucha_acertada() {
        return lucha_acertada;
    }

    public void setLucha_acertada(double lucha_acertada) {
        this.lucha_acertada = lucha_acertada;
    }

    public double getOtros() {
        return otros;
    }

    public void setOtros(double otros) {
        this.otros = otros;
    }

    public double getOtros_acertada() {
        return otros_acertada;
    }

    public void setOtros_acertada(int otros_acertada) {
        this.otros_acertada = otros_acertada;
    }

    public void setOtros_acertada(double otros_acertada) {
        this.otros_acertada = otros_acertada;
    }

    public double getPlataf_avent() {
        return plataf_avent;
    }

    public void setPlataf_avent(double plataf_avent) {
        this.plataf_avent = plataf_avent;
    }

    public double getPlataf_avent_acertada() {
        return plataf_avent_acertada;
    }

    public void setPlataf_avent_acertada(double plataf_avent_acertada) {
        this.plataf_avent_acertada = plataf_avent_acertada;
    }

    public double getRespondidas() {
        return respondidas;
    }

    public void setRespondidas(double respondidas) {
        this.respondidas = respondidas;
    }

    public double getRpg_aventuras() {
        return rpg_aventuras;
    }

    public void setRpg_aventuras(double rpg_aventuras) {
        this.rpg_aventuras = rpg_aventuras;
    }

    public double getRpg_aventuras_acertada() {
        return rpg_aventuras_acertada;
    }

    public void setRpg_aventuras_acertada(double rpg_aventuras_acertada) {
        this.rpg_aventuras_acertada = rpg_aventuras_acertada;
    }

    public double getShooter() {
        return shooter;
    }

    public void setShooter(double shooter) {
        this.shooter = shooter;
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
