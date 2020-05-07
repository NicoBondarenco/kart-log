package org.kart.view.component;


import javafx.scene.control.TableCell;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class DurationTableCell<T> extends TableCell<T, Duration> {

    @Override
    protected void updateItem(Duration value, boolean empty) {
        if (empty) {
            setText(null);
        } else {
            Long millis = value.toMillis();
            String text = String.format("%02d:%02d.%03d",
                    MILLISECONDS.toMinutes(millis) % HOURS.toMinutes(1),
                    MILLISECONDS.toSeconds(millis) % MINUTES.toSeconds(1),
                    (millis % 1000)
            );
            setText(text);
        }
    }

}
