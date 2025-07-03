package org.app.weathermode.view;

import java.util.Map;

import org.app.weathermode.model.LocationSelector;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * <h2>AbstractApp</h2>
 * <p>Contratto di alto livello per la <strong>vista</strong> principale
 * dell’applicazione. L’interfaccia è pensata per favorire <em>dependency
 * injection</em> e testabilità, permettendo di sostituire l’implementazione
 * concreta (JavaFX, Swing, web) senza modificare il controller.</p>
 */
public interface AbstractApp {

    /* ================= configurazione iniziale ================ */

    /**
     * Inietta un {@link LocationSelector} che la vista potrà utilizzare —
     * ad esempio — in finestre di ricerca o impostazioni.
     *
     * @param ls il {@link LocationSelector} da iniettare nella vista
     */
    void setLocationSelector(LocationSelector ls);

    /* ================= accessori runtime ===================== */

    /** @return mappa dei label principali, indicizzata per chiave simbolica. */
    Map<String, Label> getLabels();
    /** @return contenitore delle voci orarie (riempito dal controller). */
    VBox getHourlyEntries();
    /** @return strip dei 7 giorni di previsioni. */
    HBox getForecastStrip();
    /** @return <code>ImageView</code> dell’icona meteo odierna. */
    ImageView getTodayIcon();
    /** @return nodo radice da passare a <code>Scene</code>. */
    Parent getRoot();

}
