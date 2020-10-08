/*
* SFC project 2015, Ant Colony Optimization.
* ViewController with ACO algorithm.
* @author Mark, Birger (xbirge00@stud.fit.vutbr.cz)
*/

package aco;

// libraries for JavaFX GUI
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.*;
import java.net.URL;

public class MainViewController implements Initializable  {

    // import GUI elements from FXML
    // basic elemnts
    @FXML
    private Canvas canvas;
    @FXML
    private Button start;
    @FXML
    private Button edit;
    @FXML
    private Button wipe;
    @FXML
    private ChoiceBox method;
    // ant cycle common parameters
    @FXML
    private Slider option_a;
    @FXML
    private Slider option_m;
    @FXML
    private Slider option_alpha;
    @FXML
    private Slider option_beta;
    @FXML
    private Slider option_Q;
    @FXML
    private Slider option_ro;
    @FXML
    private Slider option_Cmax;
    // extensions options
    @FXML
    private Slider option_min;
    @FXML
    private Slider option_max;
    @FXML
    private Slider option_w;
    @FXML
    private Slider option_q0;
    // labels for options above
    // really, JavaFX hasn't default label for Slider
    @FXML
    private Label value_a;
    @FXML
    private Label value_m;
    @FXML
    private Label value_alpha;
    @FXML
    private Label value_beta;
    @FXML
    private Label value_Q;
    @FXML
    private Label value_ro;
    @FXML
    private Label value_Cmax;
    // extensions options
    @FXML
    private Label value_min;
    @FXML
    private Label value_max;
    @FXML
    private Label value_w;
    @FXML
    private Label value_q0;
    // evaluation labels
    @FXML
    private Label timing;
    @FXML
    private Label length;
    @FXML
    private Label length_delta;
    @FXML
    private Label nodes_count;
    @FXML
    private Button defaults;
    // extra modes
    @FXML
    private CheckBox onupdate;
    @FXML
    private CheckBox stepbystep;
    @FXML
    private Label iteration;
    @FXML
    private ChoiceBox visualization;
    boolean in_step_mode = false;
    @FXML
    private Button next;
    // mode 0 - editing
    // mode 1 - result/step-by-step
    private int mode = 0;
    // for options comparison
    private double latest_distance = 0.0;
    // nodes map loading
    @FXML
    private Button load_map;
    @FXML
    private ChoiceBox map_select;
    // array of nodes
    ArrayList<Point2D> nodes = new ArrayList<Point2D>();

    // this method runs at the start
    // initialization, binding creation
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        // initialize input parameters
        method.getItems().addAll("ant-cycle", "ant-density", "ant-quantity",
                "elitist-strategy", "max-min-as", "rank-based-as", "acs");
        method.getSelectionModel().selectFirst();
        start.setDisable(true);
        edit.setDisable(true);
        next.setDisable(true);
        value_a.setText(String.format("%.2f", option_a.getValue()));
        value_m.setText(String.format("%d", (int) option_m.getValue()));
        value_alpha.setText(String.format("%.2f", option_alpha.getValue()));
        value_beta.setText(String.format("%.2f", option_beta.getValue()));
        value_Q.setText(String.format("%.2f", option_Q.getValue()));
        value_ro.setText(String.format("%.2f", option_ro.getValue()));
        value_Cmax.setText(String.format("%d", (int) option_Cmax.getValue()));
        value_min.setText(String.format("*%.2f", option_min.getValue()*100));
        value_max.setText(String.format("*%.2f", option_max.getValue()*100));
        value_w.setText(String.format("%d", (int) option_w.getValue()));
        value_q0.setText(String.format("%.2f", option_q0.getValue()));
        visualization.getItems().addAll("ants", "pheromones");
        visualization.getSelectionModel().selectFirst();
        map_select.getItems().addAll("circle", "random20", "random100", "smile", "czech");
        map_select.getSelectionModel().selectFirst();

        //initialize canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // mouse click on canvas handler
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if (mode == 0) { // only in edit mode
                    Point2D position = new Point2D(event.getX(), event.getY());
                    if (nodes.contains(position))
                        return;
                    if (event.isPrimaryButtonDown() && event.isControlDown() && (nodes.size()>0)) {
                        nodes.set(get_nearest_node(position), position);
                    } else if (event.isPrimaryButtonDown()) {
                        nodes.add(position);
                    } else if (event.isSecondaryButtonDown() && (nodes.size()>0)) {
                        nodes.remove(get_nearest_node(position));
                    }
                    if (nodes.size() >= 2) {
                        start.setDisable(false);
                    } else {
                        start.setDisable(true);
                    }
                    draw_nodes();
                }
            }
        });

        // start button
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                run();
            }
        });

        // edit mode button
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mode = 0;
                edit.setDisable(true);
                wipe.setDisable(false);
                load_map.setDisable(false);
                in_step_mode = false;
                next.setDisable(true);
                draw_nodes();
            }
        });

        // wipe all nodes button
        wipe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                nodes.clear();
                start.setDisable(true);
                draw_nodes();
            }
        });

        // option handlers
        // updates labels during a dragging
        // runs algorithm in on-options-update mode
        option_a.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_a.setText(String.format("%.2f", new_val));
            }
        });
        option_a.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_m.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_m.setText(String.format("%d", new_val.intValue()));
            }
        });
        option_m.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_alpha.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_alpha.setText(String.format("%.2f", new_val));
            }
        });
        option_alpha.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_beta.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_beta.setText(String.format("%.2f", new_val));
            }
        });
        option_beta.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_Q.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_Q.setText(String.format("%.2f", new_val));
            }
        });
        option_Q.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_ro.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_ro.setText(String.format("%.2f", new_val));
            }
        });
        option_ro.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_Cmax.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_Cmax.setText(String.format("%d", new_val.intValue()));
            }
        });
        option_Cmax.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_min.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_min.setText(String.format("*%.2f", ((double) new_val)*100));
            }
        });
        option_min.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_max.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_max.setText(String.format("*%.2f", ((double) new_val)*100));
            }
        });
        option_max.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_w.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_w.setText(String.format("%d", new_val.intValue()));
            }
        });
        option_w.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });
        option_q0.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                value_q0.setText(String.format("%.2f", new_val));
            }
        });
        option_q0.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (! isNowChanging)
                    runonupdate();
            }
        });

        // special modes (on-update, step-by-step)
        onupdate.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    stepbystep.setSelected(false);
            }
        });
        stepbystep.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    onupdate.setSelected(false);
            }
        });
        // set default options values
        defaults.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                option_a.setValue(2.0);
                option_m.setValue(20);
                option_alpha.setValue(2.0);
                option_beta.setValue(2.0);
                option_Q.setValue(50.0);
                option_ro.setValue(0.5);
                option_Cmax.setValue(10.0);
                option_min.setValue(0.05/100);
                option_max.setValue(35.00/100);
                option_w.setValue(10);
                option_q0.setValue(0.5);
                runonupdate();
            }
        });

        // next iteration button
        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                interprete_step2();
            }
        });

        // change visualization mode choicebox handler
        visualization.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (in_step_mode && (mode == 1)) {
                    // show ants routes
                    if (visualization.getItems().get((Integer) number2) == "ants") {
                        clear_canvas();
                        double grade = 360/m;
                        double offset_step = 45/m;
                        for (int k = 0; k < m; k++) {
                            ArrayList tabu_k = tabu.get(k);
                            Color color = Color.hsb((int) grade*k, 1.0, 0.8);
                            draw_road(tabu_k, (int) (k*offset_step), color, true, 3);
                        }
                    // show pheromones
                    } else if (visualization.getItems().get((Integer) number2) == "pheromones") {
                        clear_canvas();
                        draw_tau();
                    }
                }
            }
        });

        // load map button
        // maps is hardcoded, due to random generation and
        // java list and point2d defintion method
        load_map.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //System.out.println(nodes);
                ArrayList<Point2D> new_nodes = new ArrayList<Point2D>();
                double sizex = canvas.getWidth();
                double sizey = canvas.getHeight();
                if (map_select.getValue() == "circle") {
                    // circle dots
                    new_nodes.add(new Point2D(253.0, 79.0));
                    new_nodes.add(new Point2D(105.0, 216.0));
                    new_nodes.add(new Point2D(397.0, 218.0));
                    new_nodes.add(new Point2D(258.0, 349.0));
                    new_nodes.add(new Point2D(349.0, 309.0));
                    new_nodes.add(new Point2D(154.0, 119.0));
                    new_nodes.add(new Point2D(357.0, 118.0));
                    new_nodes.add(new Point2D(161.0, 310.0));
                    new_nodes.add(new Point2D(125.0, 268.0));
                    new_nodes.add(new Point2D(206.0, 335.0));
                    new_nodes.add(new Point2D(310.0, 338.0));
                    new_nodes.add(new Point2D(381.0, 270.0));
                    new_nodes.add(new Point2D(383.0, 164.0));
                    new_nodes.add(new Point2D(313.0, 91.0));
                    new_nodes.add(new Point2D(199.0, 91.0));
                    new_nodes.add(new Point2D(116.0, 164.0));
                } else if (map_select.getValue() == "random20") {
                    for (int x = 0; x < 20; x++) {
                        new_nodes.add(new Point2D(Math.random() * sizex, Math.random() * sizey));
                    }
                } else if (map_select.getValue() == "random100") {
                    for (int x = 0; x < 100; x++) {
                        new_nodes.add(new Point2D(Math.random() * sizex, Math.random() * sizey));
                    }
                } else if (map_select.getValue() == "smile") {
                    // smile symbol nodes
                    new_nodes.add(new Point2D(121.0, 184.0));
                    new_nodes.add(new Point2D(136.0, 162.0));
                    new_nodes.add(new Point2D(161.0, 151.0));
                    new_nodes.add(new Point2D(182.0, 156.0));
                    new_nodes.add(new Point2D(198.0, 168.0));
                    new_nodes.add(new Point2D(207.0, 178.0));
                    new_nodes.add(new Point2D(276.0, 178.0));
                    new_nodes.add(new Point2D(289.0, 169.0));
                    new_nodes.add(new Point2D(307.0, 159.0));
                    new_nodes.add(new Point2D(334.0, 158.0));
                    new_nodes.add(new Point2D(324.0, 157.0));
                    new_nodes.add(new Point2D(349.0, 165.0));
                    new_nodes.add(new Point2D(361.0, 179.0));
                    new_nodes.add(new Point2D(119.0, 275.0));
                    new_nodes.add(new Point2D(134.0, 288.0));
                    new_nodes.add(new Point2D(155.0, 299.0));
                    new_nodes.add(new Point2D(174.0, 306.0));
                    new_nodes.add(new Point2D(193.0, 311.0));
                    new_nodes.add(new Point2D(213.0, 314.0));
                    new_nodes.add(new Point2D(236.0, 314.0));
                    new_nodes.add(new Point2D(262.0, 311.0));
                    new_nodes.add(new Point2D(282.0, 311.0));
                    new_nodes.add(new Point2D(297.0, 307.0));
                    new_nodes.add(new Point2D(319.0, 302.0));
                    new_nodes.add(new Point2D(335.0, 292.0));
                    new_nodes.add(new Point2D(142.0, 276.0));
                    new_nodes.add(new Point2D(165.0, 276.0));
                    new_nodes.add(new Point2D(189.0, 276.0));
                    new_nodes.add(new Point2D(215.0, 277.0));
                    new_nodes.add(new Point2D(233.0, 278.0));
                    new_nodes.add(new Point2D(257.0, 278.0));
                    new_nodes.add(new Point2D(282.0, 279.0));
                    new_nodes.add(new Point2D(310.0, 279.0));
                    new_nodes.add(new Point2D(334.0, 279.0));
                    new_nodes.add(new Point2D(355.0, 281.0));
                    new_nodes.add(new Point2D(56.0, 234.0));
                    new_nodes.add(new Point2D(59.0, 205.0));
                    new_nodes.add(new Point2D(66.0, 184.0));
                    new_nodes.add(new Point2D(77.0, 159.0));
                    new_nodes.add(new Point2D(95.0, 141.0));
                    new_nodes.add(new Point2D(113.0, 126.0));
                    new_nodes.add(new Point2D(136.0, 116.0));
                    new_nodes.add(new Point2D(165.0, 108.0));
                    new_nodes.add(new Point2D(190.0, 104.0));
                    new_nodes.add(new Point2D(217.0, 102.0));
                    new_nodes.add(new Point2D(244.0, 101.0));
                    new_nodes.add(new Point2D(266.0, 101.0));
                    new_nodes.add(new Point2D(288.0, 104.0));
                    new_nodes.add(new Point2D(310.0, 108.0));
                    new_nodes.add(new Point2D(329.0, 114.0));
                    new_nodes.add(new Point2D(344.0, 119.0));
                    new_nodes.add(new Point2D(366.0, 127.0));
                    new_nodes.add(new Point2D(381.0, 136.0));
                    new_nodes.add(new Point2D(398.0, 155.0));
                    new_nodes.add(new Point2D(408.0, 170.0));
                    new_nodes.add(new Point2D(413.0, 188.0));
                    new_nodes.add(new Point2D(416.0, 207.0));
                    new_nodes.add(new Point2D(420.0, 230.0));
                    new_nodes.add(new Point2D(420.0, 253.0));
                    new_nodes.add(new Point2D(417.0, 274.0));
                    new_nodes.add(new Point2D(408.0, 291.0));
                    new_nodes.add(new Point2D(400.0, 307.0));
                    new_nodes.add(new Point2D(388.0, 322.0));
                    new_nodes.add(new Point2D(376.0, 334.0));
                    new_nodes.add(new Point2D(342.0, 350.0));
                    new_nodes.add(new Point2D(324.0, 355.0));
                    new_nodes.add(new Point2D(306.0, 362.0));
                    new_nodes.add(new Point2D(57.0, 253.0));
                    new_nodes.add(new Point2D(63.0, 274.0));
                    new_nodes.add(new Point2D(69.0, 287.0));
                    new_nodes.add(new Point2D(78.0, 301.0));
                    new_nodes.add(new Point2D(92.0, 318.0));
                    new_nodes.add(new Point2D(108.0, 328.0));
                    new_nodes.add(new Point2D(162.0, 359.0));
                    new_nodes.add(new Point2D(181.0, 363.0));
                    new_nodes.add(new Point2D(204.0, 366.0));
                    new_nodes.add(new Point2D(227.0, 365.0));
                    new_nodes.add(new Point2D(249.0, 367.0));
                    new_nodes.add(new Point2D(284.0, 365.0));
                    new_nodes.add(new Point2D(268.0, 367.0));
                    new_nodes.add(new Point2D(125.0, 340.0));
                    new_nodes.add(new Point2D(143.0, 351.0));
                    new_nodes.add(new Point2D(357.0, 343.0));
                } else if (map_select.getValue() == "czech") {
                    // czech map nodes
                    new_nodes.add(new Point2D(144.0, 195.0));
                    new_nodes.add(new Point2D(249.0, 168.0));
                    new_nodes.add(new Point2D(366.0, 288.0));
                    new_nodes.add(new Point2D(413.0, 223.0));
                    new_nodes.add(new Point2D(487.0, 180.0));
                    new_nodes.add(new Point2D(461.0, 273.0));
                    new_nodes.add(new Point2D(80.0, 253.0));
                    new_nodes.add(new Point2D(34.0, 187.0));
                    new_nodes.add(new Point2D(169.0, 91.0));
                    new_nodes.add(new Point2D(192.0, 313.0));
                }
                nodes = new_nodes;
                if (nodes.size() >= 2) {
                    start.setDisable(false);
                } else {
                    start.setDisable(true);
                }
                draw_nodes();
            }
        });
    }

    // common function, called from slider handlers
    public void runonupdate() {
        if (onupdate.isSelected()) {
            run();
        }
    }

    // common run algorithm function
    public void run() {
        if (nodes.size() >= 2) {
            mode = 1;
            edit.setDisable(false);
            wipe.setDisable(true);
            load_map.setDisable(true);
            interprete_step1();
        }
    }

    // Algorithm is separated to 3 parts
    // due to asynchronious JavaFX interpretation
    // it's impossible to loop iteration until Next button click
    // so most of ACO data structures are shared between async methods
    // Next button runs interprete_2 step, which is basically one iteration
    // in normal (not step-by-step) mode, interprete_2 calls recursively
    // this allow to interprete full algorithm at once

    long time_delta = 0; // time counting
    int n; // number of nodes
    int m; // number of ants
    double shortest_length; // best road length
    ArrayList<Integer> shortest; // beast road nodes
    double Q; // pheromones value by 1 ant
    double ro; // disappearance quotient
    int C_max; // maximum iterations number
    int c; // iteration index
    double[][] tau; // pheromones value
    List<ArrayList<Integer>> tabu; // list of road at current iteration

    // initialize algorithm
    // equals to lectures step 1
    // also read some sliders values for future steps
    // also initalizes best road values
    public void interprete_step1() {
        time_delta = 0;
        long start_time = System.currentTimeMillis();
        in_step_mode = false;
        next.setDisable(true);
        start_time = System.currentTimeMillis();
        n = nodes.size();
        m = (int) option_m.getValue();
        shortest_length = Double.POSITIVE_INFINITY;
        shortest = new ArrayList<Integer>(m);
        Q = option_Q.getValue(); // step 9
        ro = option_ro.getValue(); // step 10
        C_max = (int) option_Cmax.getValue(); // step 12
        c = 0;
        tau = new double[n][n];
        for (double[] row : tau) {
            Arrays.fill(row, option_a.getValue());
        }
        time_delta += (System.currentTimeMillis() - start_time);
        interprete_step2();
    }

    // iteration step
    // equals to lectures step 5,6,7,8,9,10,11,12
    public void interprete_step2() {
        long start_time = System.currentTimeMillis();
        in_step_mode = true;
        next.setDisable(false);
        // while iteration is allowed
        if (c<=C_max) {
            // show pheromones in step-by-step mode
            if (stepbystep.isSelected()) {
                if (visualization.getValue() == "pheromones") {
                    clear_canvas();
                    draw_tau();
                }
            }
            // step 2, delta tau initialization
            double[][] delta_tau = new double[n][n];
            // step 3 setup random ants positions
            int s = 0;
            tabu = new ArrayList<ArrayList<Integer>>(m);
            for (int k = 0; k < m; k++) {
                ArrayList<Integer> tabu_k = new ArrayList<Integer>(n);
                int rp1 = (int) (Math.random() * n);
                Integer rp = new Integer(rp1);
                tabu_k.add(rp);
                tabu.add(tabu_k);
            }
            do {
                // step 4, increment s
                s += 1;
                // step 5, calculate moves
                double alpha = option_alpha.getValue();
                double beta = option_beta.getValue();
                for (List tabu_k : tabu) {
                    // ant colony system extension
                    // agrmax defines exploration/exploitation strategy
                    boolean argmax = (method.getValue() == "acs") && (Math.random() <= option_q0.getValue());
                    double fake_alpha = alpha; // fake alpha for ACS
                    if (argmax) {
                        fake_alpha = 1.0;
                    }
                    // i is latest position
                    int i = (int) tabu_k.get(s - 1);
                    // p is probability of moves
                    double[] p = new double[n];
                    // probability calculation
                    // p contains TOP part of the equation
                    for (int j = 0; j < n; j++) {
                        Integer j_integer = (Integer) j;
                        if (!tabu_k.contains(j_integer)) {
                            double nu = 1 / nodes.get(i).distance(nodes.get(j));
                            p[j] = Math.pow(tau[i][j], fake_alpha) * Math.pow(nu, beta);
                        }
                    }
                    // if strategy is exploitation, select max
                    if (argmax) {
                        double max = 0.0;
                        int max_idx = 0;
                        for (int j = 0; j < n; j++) {
                            if (p[j] >= max) {
                                max = p[j];
                                max_idx = j;
                            }
                        }
                        tabu_k.add(max_idx);
                    // if strategy is exploration
                    } else {
                        // normalize probability to <0,1>
                        double sum = 0.0;
                        for (double p_j : p)
                            sum += p_j;
                        for (int x = 0; x < p.length; x++) {
                            p[x] = p[x] / sum;
                        }
                        // setup intervals
                        sum = 0.0;
                        for (int x = 0; x < p.length; x++) {
                            p[x] = p[x] + sum; //intervals
                            sum = p[x];
                        }
                        // generate random value
                        double random_value = Math.random();
                        int j = 0;
                        // select interval
                        for (int x = 0; x < p.length; x++) {
                            if (random_value < p[x]) {
                                j = x;
                                break;
                            }
                        }
                        tabu_k.add(j);
                    }
                }
            } while (s < (n-1)); // step 6, for each node
            // step 7, count roads length
            List<Double> L = new ArrayList<Double>(m);
            for (int k = 0; k < m; k++) { // for each ant
                double d = 0.0;
                ArrayList tabu_k = tabu.get(k);
                for (int x = 1; x < n; x++) {
                    int i = (int) tabu_k.get(x - 1);
                    int j = (int) tabu_k.get(x);
                    d += nodes.get(i).distance(nodes.get(j));
                }
                // also add length to inital point
                int i = (int) tabu_k.get(1);
                int j = (int) tabu_k.get(tabu_k.size() - 1);
                d += nodes.get(i).distance(nodes.get(j));
                Double d_double = (Double) d;
                L.add(d_double);
            }
            // step 8, select shortest road
            double iteration_shortest_length = Collections.min(L);
            int best_ant_index = L.indexOf(iteration_shortest_length);
            if (iteration_shortest_length < shortest_length) {
                shortest_length = iteration_shortest_length;
                shortest = tabu.get(best_ant_index);
            }
            // step 9
            // fake number of ants, tabus and L for
            // Max-min extenstion, for Rank-based extension
            int fake_m = m;
            List<ArrayList<Integer>> fake_tabu = tabu;
            List<Double> fake_L = L;
            int w = (int) option_w.getValue();
            if (w > m)
                w = m;
            if (method.getValue() == "max-min-as") {
                fake_m = 1;
                fake_tabu = new ArrayList<ArrayList<Integer>>(1);
                fake_tabu.add(tabu.get(best_ant_index));
                fake_L = new ArrayList<Double>(1);
                fake_L.add(L.get(best_ant_index));
            } else if (method.getValue() == "rank-based-as") {
                fake_m = w;
                List<Double> sorted_L = L;
                Collections.sort(sorted_L);
                Collections.reverse(sorted_L);
                fake_tabu = new ArrayList<ArrayList<Integer>>(w);
                fake_L = new ArrayList<Double>(w);
                for (int x = 0; x < w; x++) {
                    int idx = L.indexOf(sorted_L.get(x));
                    fake_tabu.add(tabu.get(idx));
                    fake_L.add(L.get(idx));
                }
            }
            for (int k = 0; k < fake_m; k++) {
                ArrayList<Integer> tabu_k = fake_tabu.get(k);
                Double L_k = fake_L.get(k);
                for (int x = 1; x < n; x++) {
                    int i = (int) tabu_k.get(x - 1);
                    int j = (int) tabu_k.get(x);
                    double dij = nodes.get(i).distance(nodes.get(j));
                    // ant density, changed delta formula
                    if (method.getValue() == "ant-density") {
                        delta_tau[i][j] += Q;
                    // ant quantity, changed delta formula
                    } else if (method.getValue() == "ant-quantity") {
                        delta_tau[i][j] += Q / dij;
                    // rank-based, changed delta formula, fake number of ants (sorted, w)
                    } else if (method.getValue() == "rank-based-as") {
                        delta_tau[i][j] += Q / L_k * (w-k);
                    } else {
                        // all other extensions, including max-min with fake m=1
                        delta_tau[i][j] += Q / L_k;
                    }
                }
            }
            // step 10, pheromones update
            double min = option_min.getValue();
            double max = option_max.getValue();
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    // elitist strategy extension
                    if (method.getValue() == "elitist-strategy") {
                        // only for best road edges
                        if (edge_at_road(shortest,x,y)) {
                            int e = 0;
                            // count number of ants for edge
                            for (ArrayList tabu_k : tabu) {
                                if (edge_at_road(tabu_k, x, y)) {
                                    e += 1;
                                }
                            }
                            tau[x][y] = (1 - ro) * tau[x][y] + delta_tau[x][y] + e*Q/shortest_length;
                        } else {
                            // elitist-strategy, normal edge
                            tau[x][y] = (1 - ro) * tau[x][y] + delta_tau[x][y];
                        }
                    } else {
                        // normal edge update
                        tau[x][y] = (1 - ro) * tau[x][y] + delta_tau[x][y];
                    }
                    // max-min extension, fix pheromones values
                    if (method.getValue() == "max-min-as") {
                        if (tau[x][y] < min) {
                            tau[x][y] = min;
                        }
                        if (tau[x][y] > max) {
                            tau[x][y] = max;
                        }
                    }
                }
            }
            // step 11, update countee, iteration label
            String iteration_label_text = String.format(Locale.US, "%d/%d", c, C_max);
            iteration.setText(iteration_label_text);
            c += 1;
            time_delta += (System.currentTimeMillis() - start_time);
            // show ant routes in step-by-step mode
            if (stepbystep.isSelected()) {
                if (visualization.getValue() == "ants") {
                    clear_canvas();
                    double grade = 360/m;
                    double offset_step = 45/m;
                    for (int k = 0; k < m; k++) {
                        ArrayList tabu_k = tabu.get(k);
                        Color color = Color.hsb((int) grade*k, 1.0, 0.8);
                        draw_road(tabu_k, (int) (k*offset_step), color, true, 3);
                    }
                }
            } else {
                // automatically run next iteration in normal mode
                interprete_step2();
            }
        } else {
            // if iterations counter is high enough, run iterations_step3
            interprete_step3();
        }
    }

    // finish algorithm
    // equals to lectures step 13
    public void interprete_step3() {
        in_step_mode = false;
        next.setDisable(true);
        // update time spent
        double seconds_spent = time_delta / 1000.0;
        String spent = String.format(Locale.US, "%.2fs", seconds_spent);
        timing.setText(spent);
        // update length
        String road_length = String.format(Locale.US, "%.2f", shortest_length);
        length.setText(road_length);
        // update distance delta (for example between options change)
        double distance_delta = latest_distance - shortest_length;
        String distance_delta_text = String.format(Locale.US, "%.2f", distance_delta);
        length_delta.setText(distance_delta_text);
        if (distance_delta >= 0) {
            length_delta.setTextFill(Color.GREEN);
        } else {
            length_delta.setTextFill(Color.RED);
        }
        latest_distance = shortest_length;

        clear_canvas();
        draw_road(shortest, 0, Color.ORANGE, true, 1);
        draw_road_nodes(shortest);
    }

    // helper method for elitist-strategy extension
    // returns is edge is part of the road
    public boolean edge_at_road(ArrayList<Integer> road, int node1, int node2) {
        int n = nodes.size();
        for (int x = 1; x < n; x++) {
            int from = road.get(x - 1);
            int to = road.get(x);
            if ((from == node1 && to == node2) || (from == node2 && to == node1)) {
                return true;
            }
        }
        return false;
    }

    // method, clears canvas with white color
    public void clear_canvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // ants roads visualization method for step-by-step mode
    // input: road of nodes, offset (which is calculated by ant index)
    // output: modified Points2D road
    // method moves each point the way, selected by the edge way
    public ArrayList<Point2D> modify_road(ArrayList<Integer> road, int offset) {
        ArrayList<Point2D> modified_road = new ArrayList<Point2D>(m);
        Point2D previous_node = nodes.get(road.get(0));
        Point2D current_node;
        for (int x = 1; x < (n+1); x++) {
            if (x==n) {
                current_node = nodes.get(road.get(0));
            } else{
                current_node = nodes.get(road.get(x));
            }
            double delta_x = Math.abs(previous_node.getX() - current_node.getX());
            double delta_y = Math.abs(previous_node.getY() - current_node.getY());
            Point2D new_node;
            if (delta_x < delta_y) {
                new_node = new Point2D(previous_node.getX()-offset, previous_node.getY());
            } else {
                new_node = new Point2D(previous_node.getX(), previous_node.getY()-offset);
            }
            modified_road.add(new_node);
            previous_node = current_node;
        }
        return modified_road;
    }

    // helper method, draws road
    public void draw_road(ArrayList<Integer> road, int offset, Color color, boolean loop, int width) {
        ArrayList<Point2D> points_road = modify_road(road, offset);
        int n = nodes.size();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        gc.setLineWidth(width);
        for (int x = 1; x < n; x++) {
            Point2D from = points_road.get(x - 1);
            Point2D to = points_road.get(x);
            gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
        }
        if (loop) {
            Point2D from = points_road.get(0);
            Point2D to = points_road.get(points_road.size() - 1);
            gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
        }
    }

    // helper method, draws pheromone intensity map
    public void draw_tau() {
        int n = nodes.size();
        double maxValue = 0.0;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (tau[x][y] > maxValue) {
                    maxValue = tau[x][y];
                }
            }
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                Point2D from = nodes.get(x);
                Point2D to = nodes.get(y);
                double opacity = 0.5*tau[x][y]/maxValue;
                Color c = Color.rgb(0, 0, 0, opacity);
                gc.setStroke(c);
                gc.setLineWidth(2);
                gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
            }
        }
    }

    // helper method, draws pink dots at the finite road
    public void draw_road_nodes(ArrayList<Integer> road) {
        int n = nodes.size();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double grade = 255/n;
        for (int x = 0; x < n; x++) {
            Color c = Color.rgb(255,(int) grade*x,(int) grade*x);
            int node_idx = (int) road.get(x);
            Point2D node = nodes.get(node_idx);
            gc.setStroke(c);
            gc.setLineWidth(3);
            gc.strokeOval(node.getX(), node.getY(), 3, 3);
        }
    }

    // helper method, draws nodes at the edit mode, update nodes count label
    public void draw_nodes() {
        int n = nodes.size();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int x = 0; x < n; x++) {
            Point2D node = nodes.get(x);
            Float radius = 4.0f;
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(1);
            gc.strokeOval(node.getX(), node.getY(), radius, radius);
        }
        String nodes_count_text = String.format(Locale.US, "%d", nodes.size());
        nodes_count.setText(nodes_count_text);
    }

    // helper method for editing mode, selects nearest node for click position
    public int get_nearest_node(Point2D position) {
        int n = nodes.size();
        double min = Double.POSITIVE_INFINITY;
        int min_idx = 0;
        for (int x = 0; x < n; x++) {
            double d = nodes.get(x).distance(position);
            if (d < min) {
                min = d;
                min_idx = x;
            }
        }
        return min_idx;
    }

}