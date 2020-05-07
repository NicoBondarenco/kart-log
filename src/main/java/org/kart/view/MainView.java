package org.kart.view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.kart.model.vo.KartLogVO;
import org.kart.model.vo.PilotRaceSummaryVO;
import org.kart.model.vo.RaceSummaryVO;
import org.kart.service.KartLogService;
import org.kart.view.component.DurationTableCell;
import org.kart.view.component.SpeedTableCell;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainView extends VBox {

    private final KartLogService service;

    private TextField field;
    private Button button;
    private FileChooser chooser;
    private ComboBox<KartLogVO> logsSelect;
    private ObservableList<KartLogVO> logsList;
    private ObservableList<PilotRaceSummaryVO> detailsList;
    private TableView<PilotRaceSummaryVO> detailsTable;
    private Label bestLapLabel;

    public MainView(KartLogService service) {
        super(10);
        this.service = service;
        init();
    }

    private void init() {
        initComponents();
        setPadding(new Insets(10, 50, 10, 50));
        getChildren().addAll(
                createTitle(),
                createFiledChooserContainer(),
                logsSelect,
                bestLapLabel,
                detailsTable
        );
    }

    private void initComponents() {
        field = createFileChooserField();
        button = createFileChooserButton();
        chooser = createFileChooser();
        logsSelect = createLogsSelect();
        detailsTable = createDetailsTable();
        bestLapLabel = createBestLapLabel();
        clearBestLapLabel();
    }

    private Label createTitle() {
        Label title = new Label("- Kart Log Analyzer -");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        title.setFont(Font.font(36));
        return title;
    }

    private HBox createFiledChooserContainer() {
        HBox container = new HBox(10);
        container.getChildren().addAll(
                field,
                button
        );
        HBox.setHgrow(field, Priority.ALWAYS);
        return container;
    }

    private TextField createFileChooserField() {
        TextField field = new TextField();
        field.setEditable(false);
        return field;
    }

    private Button createFileChooserButton() {
        Button button = new Button("Selecione...");
        button.setOnAction(event -> {
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                processFile(file);
            }
        });
        return button;
    }

    private void processFile(File file) {
        field.setText(file.getAbsolutePath());
        button.setDisable(true);
        try {
            List<KartLogVO> all = service.process(file);
            logsList.clear();
            detailsList.clear();
            clearBestLapLabel();
            logsList.addAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            field.setText("");
        } finally {
            button.setDisable(false);
        }
    }

    private FileChooser createFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecione o arquivo de logs da Corrida");
        FileChooser.ExtensionFilter logFilter = new FileChooser.ExtensionFilter("Kart log files (*.log | *.txt)", "*.log", "*.txt");
        chooser.getExtensionFilters().add(logFilter);
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return chooser;
    }

    private ComboBox<KartLogVO> createLogsSelect() {
        logsList = FXCollections.observableArrayList();
        ComboBox<KartLogVO> select = new ComboBox<>(logsList);
        select.setMaxWidth(Double.MAX_VALUE);
        select.setOnAction(event -> {
            detailsList.clear();
            ofNullable(select.getValue()).ifPresent(value -> {
                RaceSummaryVO details = service.getKartLogDetails(value.getId());
                setBestLapLabel(details.getPilot(), details.getBestLap(), details.getBestDuration());
                detailsList.addAll(details.getItems());
            });
        });
        return select;
    }

    private Label createBestLapLabel() {
        Label label = new Label();
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(24));
        return label;
    }

    private void clearBestLapLabel(){
        bestLapLabel.setText(formatBestLap("--------", 0, Duration.ZERO));
    }

    private void setBestLapLabel(String pilot, Integer lap, Duration duration){
        bestLapLabel.setText(formatBestLap(pilot, lap, duration));
    }

    private String formatBestLap(String pilot, Integer lap, Duration duration) {
        Long millis = duration.toMillis();
        String time = String.format("%02d:%02d.%03d",
                MILLISECONDS.toMinutes(millis) % HOURS.toMinutes(1),
                MILLISECONDS.toSeconds(millis) % MINUTES.toSeconds(1),
                (millis % 1000)
        );
        return format("Melhor volta: {0} - Tempo: {1}({2})", pilot, lap, time);
    }

    private TableView<PilotRaceSummaryVO> createDetailsTable() {

        detailsList = FXCollections.observableArrayList();

        TableColumn<PilotRaceSummaryVO, Integer> positionColumn = new TableColumn<>("Posi\u00E7\u00E3o");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        TableColumn<PilotRaceSummaryVO, String> codeColumn = new TableColumn<>("C\u00F3digo");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        TableColumn<PilotRaceSummaryVO, String> pilotColumn = new TableColumn<>("Piloto");
        pilotColumn.setCellValueFactory(new PropertyValueFactory<>("pilot"));
        TableColumn<PilotRaceSummaryVO, Integer> lapsColumn = new TableColumn<>("Voltas");
        lapsColumn.setCellValueFactory(new PropertyValueFactory<>("laps"));
        TableColumn<PilotRaceSummaryVO, Duration> durationColumn = new TableColumn<>("Dura\u00E7\u00E3o");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.setCellFactory(column -> new DurationTableCell<>());
        TableColumn<PilotRaceSummaryVO, Integer> bestLapColumn = new TableColumn<>("Melhor Volta");
        bestLapColumn.setCellValueFactory(new PropertyValueFactory<>("bestLap"));
        TableColumn<PilotRaceSummaryVO, Duration> bestDurationColumn = new TableColumn<>("Dur. Melhor Volta");
        bestDurationColumn.setCellValueFactory(new PropertyValueFactory<>("bestDuration"));
        bestDurationColumn.setCellFactory(column -> new DurationTableCell<>());
        TableColumn<PilotRaceSummaryVO, BigDecimal> avarageSpeedColumn = new TableColumn<>("Velocidade M\u00E9dia");
        avarageSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("avarageSpeed"));
        avarageSpeedColumn.setCellFactory(column -> new SpeedTableCell<>());
        TableColumn<PilotRaceSummaryVO, Duration> afterFirstColumn = new TableColumn<>("Tempo");
        afterFirstColumn.setCellValueFactory(new PropertyValueFactory<>("afterFirst"));
        afterFirstColumn.setCellFactory(column -> new DurationTableCell<>());

        TableView<PilotRaceSummaryVO> table = new TableView<>(detailsList);
        table.getColumns().addAll(
                positionColumn,
                codeColumn,
                pilotColumn,
                lapsColumn,
                durationColumn,
                bestLapColumn,
                bestDurationColumn,
                avarageSpeedColumn,
                afterFirstColumn
        );

        return table;
    }
}
