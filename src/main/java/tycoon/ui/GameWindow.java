package tycoon.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tycoon.model.*;
import tycoon.service.GameEngine;
import java.util.Random;

public class GameWindow extends Application {
    private static final int TILE_SIZE = 64;

    private Stage primaryStage;
    private Scene mainScene;

    private WorldMap worldMap;
    private GameEngine engine;
    private GameRenderer renderer;
    private AnimationTimer gameLoop;
    
    private Label capitalLabel;
    private Label timeLabel;
    private VBox detailPanel;
    private boolean isBuildMode = false;
    private double simulatedTime = 0;
    private Canvas minimapCanvas;
    private MinimapRenderer minimapRenderer;
    private ScrollPane scrollPane;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        mainScene = new Scene(new Pane(), 1440, 900);
        primaryStage.setTitle("Mini Transport Tycoon");
        primaryStage.setScene(mainScene);
        
        showMainMenu();
        primaryStage.show();
    }

    // ==========================================
    // 1. Main Menu
    // ==========================================
    private void showMainMenu() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #cccccc;");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 300);
        menuBox.setStyle("-fx-background-color: #d9d9d9; -fx-border-color: black; -fx-border-width: 1px;");

        Label title = new Label("Main menu");
        title.setStyle("-fx-font-size: 20px; -fx-padding: 0 0 20 0;");

        Button newGameBtn = new Button("New Game");
        newGameBtn.setPrefWidth(200);
        newGameBtn.setStyle(
                "-fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: black; -fx-background-color: transparent;");
        newGameBtn.setOnAction(e -> startNewGame());

        Button loadGameBtn = new Button("Load Saved Game");
        loadGameBtn.setPrefWidth(200);
        loadGameBtn.setStyle(
                "-fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: black; -fx-background-color: transparent;");

        Button exitBtn = new Button("Exit to the desktop");
        exitBtn.setPrefWidth(200);
        exitBtn.setStyle(
                "-fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: black; -fx-background-color: transparent;");
        exitBtn.setOnAction(e -> Platform.exit());

        menuBox.getChildren().addAll(title, newGameBtn, loadGameBtn, exitBtn);
        root.getChildren().add(menuBox);

        mainScene.setRoot(root);
    }

    // ==========================================
    // 2. Game Over
    // ==========================================
    private void showGameOver() {
        if (gameLoop != null) gameLoop.stop();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #cccccc;");

        VBox overBox = new VBox(30);
        overBox.setAlignment(Pos.CENTER);
        overBox.setMaxSize(500, 300);
        overBox.setStyle("-fx-background-color: #d9d9d9; -fx-border-color: black; -fx-border-width: 1px;");

        Label title = new Label("Game over");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label msg = new Label("BANKRUPT! Your company has run out of capital.");
        msg.setStyle("-fx-font-size: 14px;");

        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(100);
        closeBtn.setStyle("-fx-border-color: black; -fx-background-color: transparent;");
        closeBtn.setOnAction(e -> showMainMenu());

        overBox.getChildren().addAll(title, msg, closeBtn);
        root.getChildren().add(overBox);

        mainScene.setRoot(root);
    }

    // ==========================================
    // 3. Game UI
    // ==========================================
    private void startNewGame() {
        worldMap = new WorldMap(30, 20);
        engine = new GameEngine(worldMap);
        renderer = new GameRenderer();
        simulatedTime = 0;
        isBuildMode = false;

        // [Fix 1] City logically occupies a 3x3 area
        City budapest = new City(new Vector2(5, 5), 0.0, worldMap, "Budapest", 1000);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (5 + i < worldMap.getWidth() && 5 + j < worldMap.getHeight()) {
                    worldMap.setTile(5 + i, 5 + j, budapest);
                }
            }
        }

        // [Fix 1] Industry logically occupies a 2x2 area
        Industry lumberMill = new Industry(new Vector2(15, 5), 0.0, worldMap, "Lumber Mill", CargoType.GOODS_A);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (15 + i < worldMap.getWidth() && 5 + j < worldMap.getHeight()) {
                    worldMap.setTile(15 + i, 5 + j, lumberMill);
                }
            }
        }
        
        // [New Fix] Randomly generate forests (Forests Sub-task)
        java.util.Random rand = new java.util.Random();
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                Tile t = worldMap.getTile(x, y);
                // Ensure trees only spawn on empty tiles, not on cities or factories
                if (t instanceof EmptyTile) {
                    if (rand.nextDouble() < 0.15) { // 15% chance to spawn a forest on an empty tile
                        int trees = rand.nextInt(4) + 1; // Generates 1 to 4 trees
                        ((EmptyTile) t).setTreeCount(trees); 
                    }
                }
            }
        }

        Random rng = new Random(42);
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                if (worldMap.getTile(x, y) instanceof EmptyTile && rng.nextDouble() < 0.15) {
                    worldMap.setTile(x, y, new ForestTile(new Vector2(x, y), 0.0, worldMap, rng.nextInt(4) + 1));
                }
            }
        }

        RoadTile trafficLightRoad = new RoadTile(new Vector2(10, 5), 0.0, worldMap, 50.0);
        Junction junction = new Junction();
        junction.installTrafficLight(new TrafficLight());
        trafficLightRoad.setJunction(junction);
        worldMap.setTile(10, 5, trafficLightRoad);

        BorderPane gameRoot = new BorderPane();
        gameRoot.setTop(createTopBar());
        gameRoot.setBottom(createBottomBar());

        StackPane centerContainer = new StackPane();
        scrollPane = new ScrollPane();
        Canvas canvas = new Canvas(worldMap.getWidth() * TILE_SIZE, worldMap.getHeight() * TILE_SIZE);
        scrollPane.setContent(canvas);

        minimapRenderer = new MinimapRenderer();
        int mmW = worldMap.getWidth() * MinimapRenderer.cellSize();
        int mmH = worldMap.getHeight() * MinimapRenderer.cellSize();
        minimapCanvas = new Canvas(mmW, mmH);
        minimapCanvas.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 4, 0, 0, 2);");

        minimapCanvas.setOnMouseClicked(e -> {
            double fx = e.getX() / mmW;
            double fy = e.getY() / mmH;
            scrollPane.setHvalue(fx);
            scrollPane.setVvalue(fy);
        });

        StackPane miniMapContainer = new StackPane(minimapCanvas);
        miniMapContainer.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 1;");

        AnchorPane floatingUI = new AnchorPane();
        floatingUI.setPickOnBounds(false);

        AnchorPane.setBottomAnchor(miniMapContainer, 20.0);
        AnchorPane.setRightAnchor(miniMapContainer, 20.0);

        detailPanel = new VBox(10);
        detailPanel.setPrefWidth(180);
        detailPanel.setPadding(new Insets(10));
        detailPanel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-border-color: black; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        detailPanel.setVisible(false);

        floatingUI.getChildren().addAll(miniMapContainer, detailPanel);
        centerContainer.getChildren().addAll(scrollPane, floatingUI);
        gameRoot.setCenter(centerContainer);

        mainScene.setRoot(gameRoot);

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    double dt = (now - lastUpdate) / 1_000_000_000.0;
                    engine.tick(dt);
                    renderer.render(canvas.getGraphicsContext2D(), worldMap, engine.getVehicles());
                    minimapRenderer.render(minimapCanvas.getGraphicsContext2D(), worldMap);

                    if (engine.getBalance() < 0) {
                        showGameOver();
                    }
                }
                lastUpdate = now;
            }
        };
        gameLoop.start();

        canvas.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / TILE_SIZE);
            int y = (int) (event.getY() / TILE_SIZE);
            Tile tile = worldMap.getTile(x, y);

            if (isBuildMode && tile instanceof EmptyTile) {
                EmptyTile emptyTile = (EmptyTile) tile;
                
                // [Fix 2] Forests sub-task logic: Costs $200 for a forest tile, $100 for normal grass.
                double cost = (emptyTile.getTreeCount() > 0) ? 200.0 : 100.0; 

                if (engine.spendMoney(cost)) {
                    worldMap.setTile(x, y, new RoadTile(new Vector2(x, y), 0.0, worldMap, 50.0));
                    detailPanel.setVisible(false);
                    // Update the UI capital immediately
                    capitalLabel.setText("Capital: $" + (int)engine.getBalance());
                } else {
                    System.out.println("Not enough money to build a road!");
                }
            } else {
                if (tile instanceof Facility) {
                    updateDetailPanel((Facility) tile);
                    detailPanel.setLayoutX(event.getSceneX() + 20);
                    detailPanel.setLayoutY(event.getSceneY() - 50);
                    detailPanel.setVisible(true);
                } else {
                    detailPanel.setVisible(false);
                }
            }
        });
    }

    private HBox createTopBar() {
        HBox top = new HBox(40);
        top.setPadding(new Insets(15));
        top.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #999;");

        Label companyName = new Label("Company: ELTE Tycoon");
        companyName.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-padding: 5;");

        capitalLabel = new Label("Capital: $10000");
        capitalLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-padding: 5;");

        timeLabel = new Label("Time: Day 0");
        timeLabel.setStyle("-fx-font-size: 16px; -fx-border-color: black; -fx-padding: 5;");

        HBox speedBox = new HBox(5);
        Button pause = new Button("||");
        pause.setOnAction(e -> engine.setSimulationSpeed(0));
        Button play = new Button(">");
        play.setOnAction(e -> engine.setSimulationSpeed(1));
        Button fast = new Button(">>>");
        fast.setOnAction(e -> engine.setSimulationSpeed(4));

        Button close = new Button("X");
        close.setOnAction(e -> showMainMenu());

        speedBox.getChildren().addAll(pause, play, fast, close);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        top.getChildren().addAll(companyName, spacer1, capitalLabel, spacer2, timeLabel, speedBox);
        return top;
    }

    private HBox createBottomBar() {
        HBox bottom = new HBox(15);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #999;");

        Button buildBtn = new Button("Build\nRoad");
        buildBtn.setStyle("-fx-border-color: black;");
        buildBtn.setOnAction(e -> isBuildMode = !isBuildMode);

        Button stopBtn = new Button("Place\nStop");
        
        // [Fix 3] Vehicle Purchase UI & Logic (Vertical Prototyping - Dynamic Pathfinding)
        Button vehicleBtn = new Button("Buy\nVehicle");
        vehicleBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Buy Vehicle");
            alert.setHeaderText("Purchase a new delivery truck?");
            alert.setContentText("Cost: $500");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (engine.spendMoney(500)) {
                        
                        RoadTile startTile = null;
                        RoadTile targetTile = null;

                        for (int x = 0; x < worldMap.getWidth(); x++) {
                            for (int y = 0; y < worldMap.getHeight(); y++) {
                                Tile t = worldMap.getTile(x, y);
                                if (t instanceof RoadTile) {
                                    if (startTile == null) {
                                        startTile = (RoadTile) t; 
                                    } else {
                                        targetTile = (RoadTile) t; 
                                    }
                                }
                            }
                        }

                        if (startTile != null) {
                            if (targetTile == null) targetTile = startTile; 

                            Vehicle newCar = new Vehicle("TRUCK-001", 1.5, 100) {}; 
                            newCar.setCurrentTile(startTile);
                            newCar.setTargetTile(targetTile);
        
                            engine.addVehicle(newCar);
                            System.out.println("Vehicle successfully purchased and placed on road!");
                            
                        } else {

                            System.out.println("No road found. Purchase cancelled.");
                            Alert warn = new Alert(Alert.AlertType.WARNING, "Please build a road first!");
                            warn.show();
                            engine.earn(500); 
                        }

                        capitalLabel.setText("Capital: $" + (int)engine.getBalance());

                    } else {
                        System.out.println("Not enough capital!");
                    }
                }
            });
        });

        Button routeBtn = new Button("Route\nEditor");
        Button lightBtn = new Button("Traffic\nLights");
        // to change the color of the traffic light

        lightBtn.setOnAction(e -> {
            Tile tile = worldMap.getTile(5, 10);
            if (tile instanceof RoadTile roadTile && roadTile.hasJunction() && roadTile.getJunction().hasLight()) {

                roadTile.getJunction().getTrafficLight().switchState();
            }
        });

        bottom.getChildren().addAll(buildBtn, stopBtn, vehicleBtn, routeBtn, lightBtn);
        return bottom;
    }

    private void updateDetailPanel(Facility f) {
        detailPanel.getChildren().clear();
        Label title = new Label("detail");
        title.setStyle("-fx-alignment: center; -fx-border-bottom-color: black; -fx-border-bottom-width: 1px;");
        title.setMaxWidth(Double.MAX_VALUE);
        detailPanel.getChildren().add(title);

        if (f instanceof City) {
            City c = (City) f;
            detailPanel.getChildren().addAll(
                    new Label("Demand : Passengers"),
                    new Label("City Growth : " + c.getDisplayPopulation()));
        } else if (f instanceof Industry) {
            detailPanel.getChildren().addAll(
                    new Label("Facility : " + f.getName()),
                    new Label("Stockpile : "),
                    new Label("    Goods_A : " + f.getStockpile(CargoType.GOODS_A)));
        }
    }
}