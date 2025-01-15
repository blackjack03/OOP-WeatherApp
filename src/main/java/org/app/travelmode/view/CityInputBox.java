package org.app.travelmode.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public interface CityInputBox {

    /**
     * @return the {@link Label} used to display the box title
     */
    Label getTitleLabel();

    /**
     * @return the {@link TextField} used to enter the city
     */
    TextField getCityTextField();
}
