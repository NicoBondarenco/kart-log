package org.kart.view.component;


import javafx.scene.control.TableCell;

import java.math.BigDecimal;

public class SpeedTableCell<T> extends TableCell<T, BigDecimal> {

    @Override
    protected void updateItem(BigDecimal value, boolean empty) {
        if (empty) {
            setText(null);
        } else {
            setText(value.toString() + "km");
        }
    }

}
