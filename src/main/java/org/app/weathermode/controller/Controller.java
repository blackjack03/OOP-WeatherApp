package org.app.weathermode.controller;

import org.app.weathermode.model.AllWeather;
import org.app.weathermode.view.AbstractApp;

/**
 * <h2>Controller (MVC contract)</h2>
 * <p>Interfaccia che definisce il <strong>contratto</strong> per il controller
 * dell’applicazione nella classica architettura <em>MVC</em> (Model‑View‑Controller).
 */
public interface Controller {

    /* ==================== ciclo di vita ==================== */

    /**
     * Punto di ingresso dopo la costruzione: inizializza modello, vista e timer
     * di refresh. Può sollevare runtime exception per bloccare l’avvio in caso
     * di errori irreversibili (es. rete assente, ID città non valido).
     */
    void start();

    /* ======================== accessor ===================== */

    /**
     * @return istanza della vista principale, utile per operazioni di test o
     *         integrazione con altri moduli.
     */
    AbstractApp getApp();

    /**
     * @return wrapper <code>AllWeather</code> con i dati meteo correnti
     *         mantenuto dal controller.
     */
    AllWeather getWeatherObj();

    /* ====================== operazioni runtime ============== */

    /**
     * Forza un refresh immediato della GUI e del modello, ignorando il timer
     * di aggiornamento automatico.
     */
    void forceRefresh();

    /**
     * Ferma in modo sicuro tutte le attività periodiche (es. {@link javafx.animation.Timeline})
     * e libera risorse; da chiamare quando la finestra principale viene chiusa.
     */
    void stop();

}
