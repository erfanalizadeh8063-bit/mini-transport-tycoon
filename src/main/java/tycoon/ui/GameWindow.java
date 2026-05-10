package tycoon.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import tycoon.model.*;
import tycoon.service.GameEngine;
import tycoon.service.SaveManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWindow extends Application {
    private static final int TILE_SIZE = 64;

    private Stage primaryStage;
    private Scene mainScene;

    private WorldMap worldMap;
    private GameEngine engine;
    private GameRenderer renderer;
    private AnimationTimer gameLoop;
    
    private double simulatedTime = 0;
    private Label capitalLabel;
    private Label timeLabel;
    private Label statusLabel; 
    private VBox detailPanel;
    private Canvas minimapCanvas;
    private MinimapRenderer minimapRenderer;
    private ScrollPane scrollPane;


    private Facility inspectedFacility = null; 

    private enum ToolMode {
        INSPECT, BUILD_ROAD, PLACE_STOP, BUY_VEHICLE, TRAFFIC_LIGHT, BULLDOZE
    }
    private ToolMode currentTool = ToolMode.INSPECT;
    private List<Facility> pendingRoute = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainScene = new Scene(new Pane(), 1440, 900);
        primaryStage.setTitle("Mini Transport Tycoon");
        primaryStage.setScene(mainScene);
        showMainMenu();
        primaryStage.show();
    }

    private void showMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #FFF8F0;");

        VBox mainLayout = new VBox(50);
        mainLayout.setAlignment(Pos.CENTER);

        Label gameTitle = new Label("Mini Transport Tycoon");
        gameTitle.setStyle("-fx-font-size: 56px; -fx-font-weight: bold; -fx-text-fill: #FF9AA2; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-font-family: 'Comic Sans MS', cursive, sans-serif;");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 350);
        menuBox.setPadding(new Insets(40));
        menuBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 20, 0, 0, 10);");

        Label subtitle = new Label("~ Welcome to your Tycoon ~");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #A8D8EA; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Button newGameBtn = createMenuButton("✨ New Game");
        newGameBtn.setOnAction(e -> startNewGame());

        Button loadGameBtn = createMenuButton("📁 Load Saved Game");
        loadGameBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a Saved Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tycoon Saves (*.dat)", "*.dat"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                try {
                    Object[] saveData = SaveManager.loadGameData(file.getAbsolutePath());
                    worldMap = (WorldMap) saveData[0];
                    engine = (GameEngine) saveData[1];
                    simulatedTime = (Double) saveData[2];
                    if (engine.getSimulationSpeed() == 0.0) {
                        engine.setSimulationSpeed(1.0);
                    }

                    setupGameUI(); 
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! The save file is corrupted or incompatible!");
                    alert.show();
                }
            }
        });

        Button exitBtn = createMenuButton("❌ Exit to Desktop");
        exitBtn.setOnAction(e -> Platform.exit());

        menuBox.getChildren().addAll(subtitle, newGameBtn, loadGameBtn, exitBtn);
        mainLayout.getChildren().addAll(gameTitle, menuBox);
        root.getChildren().add(mainLayout);
        mainScene.setRoot(root);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(45);
        btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-background-color: #FFDAC1; -fx-text-fill: #5D4037; -fx-cursor: hand;");
        return btn;
    }

    private void showGameOver() {
        if (gameLoop != null) gameLoop.stop();
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #FFB7B2;"); 

        VBox overBox = new VBox(30);
        overBox.setAlignment(Pos.CENTER);
        overBox.setMaxSize(500, 300);
        overBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 30; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        Label title = new Label("💸 BANKRUPT! 💸");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #E27D60;");
        Label msg = new Label("Oh no! Your transport company ran out of money.");
        msg.setStyle("-fx-font-size: 16px; -fx-text-fill: #5D4037;");

        Button closeBtn = createMenuButton("Return to Menu");
        closeBtn.setOnAction(e -> showMainMenu());

        overBox.getChildren().addAll(title, msg, closeBtn);
        root.getChildren().add(overBox);
        mainScene.setRoot(root);
    }

    private void startNewGame() {
        worldMap = new WorldMap(30, 20);
        engine = new GameEngine(worldMap);
        simulatedTime = 0; 

        City budapest = new City(new Vector2(3, 3), 0.0, worldMap, "Budapest", 1000);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                worldMap.setTile(3 + i, 3 + j, budapest);
            }
        }

        Industry lumberMill = new Industry(new Vector2(15, 2), 0.0, worldMap, "Lumber Mill", CargoType.WOOD);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) { worldMap.setTile(15 + i, 2 + j, lumberMill); }
        }

        Industry ironMine = new Industry(new Vector2(15, 12), 0.0, worldMap, "Iron Mine", CargoType.IRON_ORE);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) { worldMap.setTile(15 + i, 12 + j, ironMine); }
        }

        Industry steelMill = new Industry(new Vector2(24, 7), 0.0, worldMap, "Steel Mill", CargoType.STEEL);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) { worldMap.setTile(24 + i, 7 + j, steelMill); }
        }
        
        Random rand = new Random();
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                Tile t = worldMap.getTile(x, y);
                if (t instanceof EmptyTile && rand.nextDouble() < 0.15) {
                    worldMap.setTile(x, y, new ForestTile(
                        new Vector2(x, y), 0.0, worldMap, rand.nextInt(4) + 1));
                }
            }
        }

        setupGameUI();
    }

    private void setupGameUI() {
        renderer = new GameRenderer();
        currentTool = ToolMode.INSPECT;
        pendingRoute.clear();
        inspectedFacility = null; 

        BorderPane gameRoot = new BorderPane();
        
        VBox topArea = new VBox();
        statusLabel = new Label("✨ Welcome! Start by building roads and placing stops.");
        statusLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 8; -fx-text-fill: #5D4037;");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setBackground(new Background(new BackgroundFill(Color.web("#FFDAC1"), CornerRadii.EMPTY, Insets.EMPTY)));
        
        topArea.getChildren().addAll(createTopBar(), statusLabel);
        gameRoot.setTop(topArea);
        gameRoot.setBottom(createBottomBar());

        scrollPane = new ScrollPane();
        Canvas canvas = new Canvas(worldMap.getWidth() * TILE_SIZE, worldMap.getHeight() * TILE_SIZE);
        scrollPane.setContent(canvas);

        minimapRenderer = new MinimapRenderer();
        int mmW = worldMap.getWidth() * MinimapRenderer.cellSize();
        int mmH = worldMap.getHeight() * MinimapRenderer.cellSize();
        minimapCanvas = new Canvas(mmW, mmH);
        
        minimapCanvas.setOnMouseClicked(event -> {
            if (scrollPane.getViewportBounds() != null) {
                double mapW = worldMap.getWidth() * TILE_SIZE;
                double mapH = worldMap.getHeight() * TILE_SIZE;
                double viewW = scrollPane.getViewportBounds().getWidth();
                double viewH = scrollPane.getViewportBounds().getHeight();

                double clickXRatio = event.getX() / minimapCanvas.getWidth();
                double clickYRatio = event.getY() / minimapCanvas.getHeight();

                double newH = (clickXRatio * mapW - viewW / 2) / (mapW - viewW);
                double newV = (clickYRatio * mapH - viewH / 2) / (mapH - viewH);

                newH = Math.max(0, Math.min(1, newH));
                newV = Math.max(0, Math.min(1, newV));

                scrollPane.setHvalue(newH);
                scrollPane.setVvalue(newV);
            }
        });
        
        StackPane miniMapContainer = new StackPane(minimapCanvas);
        miniMapContainer.setStyle("-fx-background-color: #E2F0CB; -fx-border-color: #B5EAD7; -fx-border-width: 4; -fx-border-radius: 5; -fx-background-radius: 5;");

        detailPanel = new VBox(10);
        detailPanel.setPrefWidth(220); 
        detailPanel.setPadding(new Insets(15));
        detailPanel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #FFB7B2; -fx-border-width: 2px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        detailPanel.setVisible(false);

        AnchorPane floatingUI = new AnchorPane();
        floatingUI.setPickOnBounds(false);
        AnchorPane.setBottomAnchor(miniMapContainer, 20.0);
        AnchorPane.setRightAnchor(miniMapContainer, 20.0);
        floatingUI.getChildren().addAll(miniMapContainer, detailPanel);

        StackPane centerContainer = new StackPane(scrollPane, floatingUI);
        gameRoot.setCenter(centerContainer);
        mainScene.setRoot(gameRoot);

        canvas.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / TILE_SIZE);
            int y = (int) (event.getY() / TILE_SIZE);
            if (x < 0 || x >= worldMap.getWidth() || y < 0 || y >= worldMap.getHeight()) return;
            
            Tile tile = worldMap.getTile(x, y);

            switch (currentTool) {
                case INSPECT:
                    if (tile instanceof Facility) {
                        inspectedFacility = (Facility) tile; 
                        updateDetailPanel(inspectedFacility);
                        detailPanel.setLayoutX(event.getSceneX() + 20);
                        detailPanel.setLayoutY(event.getSceneY() - 50);
                        detailPanel.setVisible(true);
                        updateStatus("🔍 Inspecting: " + ((Facility) tile).getName());
                    } else if (tile instanceof RoadTile) {
                        inspectedFacility = null;
                        updateDetailPanelForTile(tile);
                        detailPanel.setLayoutX(event.getSceneX() + 20);
                        detailPanel.setLayoutY(event.getSceneY() - 50);
                        detailPanel.setVisible(true);
                        updateStatus("🚦 Inspecting Road Segment");
                    } else {
                        inspectedFacility = null; 
                        detailPanel.setVisible(false);
                        updateStatus("🌿 Looking at empty land (" + x + "," + y + ")");
                    }
                    break;

                case BUILD_ROAD:
                    if (tile instanceof EmptyTile || tile instanceof ForestTile) {
                        double cost = (tile instanceof ForestTile f) ? f.getTotalBuildCost() : 100;
                        if (tile instanceof ForestTile f) {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                            confirm.setTitle("Clear Forest");
                            confirm.setHeaderText("This tile has " + f.getTreeCount() + " tree(s)");
                            confirm.setContentText("Clearing cost: $" + (int)f.getClearingCost() + "\nTotal road cost: $" + (int)cost + "\nProceed?");
                            confirm.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    if (engine.spendMoney(cost)) {
                                        worldMap.placeRoad(x, y, 50.0);
                                        updateStatus("🌲 Forest cleared & road built! Spent $" + (int)cost);
                                    } else {
                                        updateStatus("❌ Not enough coins to clear forest. Need $" + (int)cost);
                                    }
                                }
                            });
                        } else {
                            if (engine.spendMoney(cost)) {
                                worldMap.placeRoad(x, y, 50.0);
                                updateStatus("🔨 Road built! Spent $" + (int)cost);
                            } else {
                                updateStatus("❌ Not enough coins to build a road.");
                            }
                        }
                    } else updateStatus("🌱 You can only build roads on empty land!");
                    break;

                case PLACE_STOP:
                    if (tile instanceof EmptyTile) {
                        if (engine.spendMoney(200)) {
                            RoadTile stop = new RoadTile(new Vector2(x, y), 0.0, worldMap, 50.0);
                            worldMap.setTile(x, y, stop);
                            
                            int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
                            for(int[] d : dirs) {
                                int nx = x + d[0]; int ny = y + d[1];
                                if (nx >= 0 && nx < worldMap.getWidth() && ny >= 0 && ny < worldMap.getHeight()) {
                                    if (worldMap.getTile(nx, ny) instanceof Facility f) {
                                        f.setAccessTile(stop);
                                        updateStatus("🚏 Stop placed & magically connected to " + f.getName() + "!");
                                        return;
                                    }
                                }
                            }
                            updateStatus("🚏 Stop placed, but it's a bit lonely here.");
                        } else updateStatus("❌ Oops! Not enough coins for a Stop.");
                    }
                    break;

                case TRAFFIC_LIGHT:
                    if (tile instanceof RoadTile road) {
                        int realConnections = 0;
                        if (x > 0 && worldMap.getTile(x - 1, y) instanceof RoadTile) realConnections++;
                        if (x < worldMap.getWidth() - 1 && worldMap.getTile(x + 1, y) instanceof RoadTile) realConnections++;
                        if (y > 0 && worldMap.getTile(x, y - 1) instanceof RoadTile) realConnections++;
                        if (y < worldMap.getHeight() - 1 && worldMap.getTile(x, y + 1) instanceof RoadTile) realConnections++;

                        if (road.getConnectionCount() >= 3 || realConnections >= 3) {
                            if (!road.hasJunction()) {
                                road.setJunction(new Junction()); 
                            }
                            if (road.hasJunction()) {
                                if (!road.getJunction().hasLight()) {
                                    if (engine.spendMoney(50)) {
                                        road.getJunction().install(new TrafficLight()); 
                                        updateStatus("🚦 Traffic light successfully installed!");
                                    } else {
                                        updateStatus("❌ Not enough coins for a Traffic Light.");
                                    }
                                } else {
                                    updateStatus("⚠️ This intersection already has a traffic light!");
                                }
                            }
                        } else {
                            updateStatus("❌ Traffic lights can ONLY be placed at intersections (3+ roads)!");
                        }
                    } else {
                        updateStatus("❌ Please click on a road intersection!");
                    }
                    break;

                case BULLDOZE:
                    if (tile instanceof RoadTile) {
                        if (engine.spendMoney(50)) { 
                            worldMap.setTile(x, y, new EmptyTile(new Vector2(x, y), 0.0, worldMap));
                            updateStatus("🧨 Bulldozed successfully! Spent $50");
                        } else {
                            updateStatus("❌ Not enough coins to bulldoze.");
                        }
                    } else if (tile instanceof Facility) {
                        updateStatus("🏢 You cannot bulldoze buildings!");
                    } else {
                        updateStatus("🌱 It's already empty land.");
                    }
                    break;

                case BUY_VEHICLE:
                    if (tile instanceof Facility f) {
                        if (f.getAccessTile() == null) {
                            updateStatus("❌ " + f.getName() + " needs a Stop next to it first!");
                            return;
                        }
                        if (pendingRoute.contains(f)) {
                            updateStatus("✨ " + f.getName() + " is already selected!");
                            return;
                        }
                        
                        pendingRoute.add(f);
                        if (pendingRoute.size() == 1) {
                            updateStatus("📍 Step 1: " + f.getName() + " selected. Now click the destination!");
                        } else if (pendingRoute.size() == 2) {
                            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
                            dialog.setTitle("Select Vehicle Type");
                            dialog.setHeaderText("Choose a specific vehicle for this route:");
                            
                            ButtonType btnWood = new ButtonType("🚚 Truck (Wood)");
                            ButtonType btnIron = new ButtonType("🚛 Truck (Iron)");
                            ButtonType btnSteel = new ButtonType("🚜 Truck (Steel)");
                            ButtonType btnBus = new ButtonType("🚌 City Bus (Pax)");
                            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            dialog.getButtonTypes().setAll(btnWood, btnIron, btnSteel, btnBus, btnCancel);

                            dialog.showAndWait().ifPresent(type -> {
                                Vehicle v = null;
                                String msg = "";
                                String id = "V-" + (System.currentTimeMillis()%1000);
                                
                                if (type == btnWood && engine.spendMoney(300)) {
                                    v = new SmallTruck(id, CargoType.WOOD);
                                    msg = "🚚 Wood Transporter purchased!";
                                } else if (type == btnIron && engine.spendMoney(400)) {
                                    v = new HeavyTruck(id, CargoType.IRON_ORE);
                                    msg = "🚛 Iron Ore Transporter purchased!";
                                } else if (type == btnSteel && engine.spendMoney(500)) {
                                    v = new HeavyTruck(id, CargoType.STEEL);
                                    msg = "🚜 Steel Transporter purchased!";
                                } else if (type == btnBus && engine.spendMoney(400)) {
                                    v = new CityBus(id);
                                    msg = "🚌 City Bus purchased!";
                                } else if (type != btnCancel) {
                                    updateStatus("❌ Not enough coins!");
                                    pendingRoute.clear();
                                    return;
                                }

                                if (v != null) {
                                    v.setRoute(new Route(new ArrayList<>(pendingRoute)));
                                    v.setCurrentTile(pendingRoute.get(0).getAccessTile());
                                    engine.addVehicle(v);
                                    updateStatus("🎉 " + msg);
                                }
                            });
                            
                            pendingRoute.clear(); 
                        }
                    } else {
                        updateStatus("👇 Please click on a City or Industry to set the route.");
                    }
                    break;
            }
        });

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    double dt = (now - lastUpdate) / 1_000_000_000.0;
                    if(dt > 0.05) dt = 0.05; 
                    engine.tick(dt);
                    
                    simulatedTime += dt * engine.getSimulationSpeed();
                    timeLabel.setText(String.format("☀️ Day %d", (int)simulatedTime));
                    capitalLabel.setText("💰 Capital: $" + (int)engine.getBalance());

                    if (detailPanel.isVisible() && inspectedFacility != null) {
                        updateDetailPanel(inspectedFacility);
                    }

                    renderer.render(canvas.getGraphicsContext2D(), worldMap, engine.getVehicles());
                    minimapRenderer.render(minimapCanvas.getGraphicsContext2D(), worldMap);
                    minimapRenderer.renderVehicles(minimapCanvas.getGraphicsContext2D(), engine.getVehicles());

                    if (scrollPane.getViewportBounds() != null) {
                        minimapRenderer.drawViewport(
                            minimapCanvas.getGraphicsContext2D(),
                            scrollPane.getHvalue(),
                            scrollPane.getVvalue(),
                            scrollPane.getViewportBounds().getWidth(),
                            scrollPane.getViewportBounds().getHeight(),
                            worldMap.getWidth() * TILE_SIZE,
                            worldMap.getHeight() * TILE_SIZE
                        );
                    }
                    minimapRenderer.drawLegend(minimapCanvas.getGraphicsContext2D(), 4, 4);

                    if (engine.isBankrupt()) showGameOver();
                }
                lastUpdate = now;
            }
        };
        gameLoop.start();
    }

    private void updateStatus(String text) {
        statusLabel.setText(text);
    }

    private HBox createTopBar() {
        HBox top = new HBox(40);
        top.setPadding(new Insets(15, 25, 15, 25));
        top.setStyle("-fx-background-color: #E2F0CB; -fx-border-color: #B5EAD7; -fx-border-width: 0 0 3 0;");
        top.setAlignment(Pos.CENTER_LEFT);

        Label companyName = new Label("🌱 ELTE Tycoon");
        companyName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #5D4037;");

        capitalLabel = new Label("💰 Capital: $10000");
        capitalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E27D60;");

        timeLabel = new Label("☀️ Day 0");
        timeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #5D4037;");

        HBox speedBox = new HBox(10);
        Button pause = createSmallBtn("Pause"); pause.setOnAction(e -> engine.setSimulationSpeed(0));
        Button play = createSmallBtn("1x"); play.setOnAction(e -> engine.setSimulationSpeed(1));
        Button fast2x = createSmallBtn("2x"); fast2x.setOnAction(e -> engine.setSimulationSpeed(2)); 
        Button fast4x = createSmallBtn("4x"); fast4x.setOnAction(e -> engine.setSimulationSpeed(4));
        speedBox.getChildren().addAll(pause, play, fast2x, fast4x);

        Button close = new Button("💾 Save & Quit");
        close.setStyle("-fx-background-color: #FFB7B2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
        close.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game As...");
            fileChooser.setInitialFileName("my_tycoon_save.dat");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tycoon Saves (*.dat)", "*.dat"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                SaveManager.saveGameData(worldMap, engine, simulatedTime, file.getAbsolutePath());
                if (gameLoop != null) gameLoop.stop();
                showMainMenu();
            }
        });

        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        Region s2 = new Region(); HBox.setHgrow(s2, Priority.ALWAYS);
        Region s3 = new Region(); HBox.setHgrow(s3, Priority.ALWAYS);

        top.getChildren().addAll(companyName, s1, capitalLabel, s2, timeLabel, s3, speedBox, close);
        return top;
    }

    private Button createSmallBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-radius: 50; -fx-background-color: #ffffff; -fx-border-color: #B5EAD7; -fx-border-radius: 50;");
        return b;
    }

    private HBox createBottomBar() {
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(15));
        bottom.setStyle("-fx-background-color: #A8D8EA;");
        bottom.setAlignment(Pos.CENTER);

        ToggleGroup tools = new ToggleGroup();

        ToggleButton inspectBtn = createToolBtn("🔍 Inspect\n(Info)");
        ToggleButton roadBtn = createToolBtn("🔨 Build Road\n($100, +$50/tree)");
        ToggleButton stopBtn = createToolBtn("🚏 Place Stop\n($200)");
        ToggleButton lightBtn = createToolBtn("🚦 Traffic Light\n($50)");
        ToggleButton routeBtn = createToolBtn("🚛 Buy Vehicle\n(Route)");
        ToggleButton bulldozeBtn = createToolBtn("🧨 Bulldoze\n($50)");

        inspectBtn.setToggleGroup(tools);
        roadBtn.setToggleGroup(tools);
        stopBtn.setToggleGroup(tools);
        lightBtn.setToggleGroup(tools);
        routeBtn.setToggleGroup(tools);
        bulldozeBtn.setToggleGroup(tools); 

        inspectBtn.setSelected(true);

        tools.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                inspectBtn.setSelected(true); 
            } else {
                if (newVal == inspectBtn) { currentTool = ToolMode.INSPECT; updateStatus("🔍 Mode: Inspecting tiles."); }
                else if (newVal == roadBtn) { currentTool = ToolMode.BUILD_ROAD; updateStatus("🔨 Mode: Click empty land to build roads. Forest tiles cost $50 extra per tree."); }
                else if (newVal == stopBtn) { currentTool = ToolMode.PLACE_STOP; updateStatus("🚏 Mode: Click land adjacent to a facility to place a Stop."); }
                else if (newVal == lightBtn) { currentTool = ToolMode.TRAFFIC_LIGHT; updateStatus("🚦 Mode: Click a road to install Traffic Lights."); }
                else if (newVal == routeBtn) { 
                    currentTool = ToolMode.BUY_VEHICLE; 
                    pendingRoute.clear();
                    updateStatus("🚛 Mode: Click Facility A, then Facility B to spawn a vehicle."); 
                }
                else if (newVal == bulldozeBtn) {
                    currentTool = ToolMode.BULLDOZE;
                    updateStatus("🧨 Mode: Click a road to destroy it and clear the land.");
                }
            }
        });

        bottom.getChildren().addAll(inspectBtn, roadBtn, stopBtn, lightBtn, routeBtn, bulldozeBtn);
        return bottom;
    }

    private ToggleButton createToolBtn(String text) {
        ToggleButton btn = new ToggleButton(text);
        btn.setPrefSize(130, 65);
        btn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-background-color: #ffffff; -fx-text-fill: #5D4037; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);");
        return btn;
    }

    private void updateDetailPanel(Facility f) {
        detailPanel.getChildren().clear();
        Label title = new Label("📋 Facility Info");
        title.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-border-bottom-color: #FFB7B2; -fx-border-bottom-width: 2px; -fx-padding: 0 0 5 0; -fx-text-fill: #5D4037;");
        title.setMaxWidth(Double.MAX_VALUE);
        detailPanel.getChildren().add(title);

        if (f instanceof City c) {
            detailPanel.getChildren().addAll(
                new Label("🏠 Name: " + c.getName()),
                new Label("🧑‍🤝‍🧑 Pop: " + c.getDisplayPopulation()),
                new Label("📥 Demand: Pax")
            );
        } else if (f instanceof Industry i) {
            detailPanel.getChildren().addAll(
                new Label("🏭 Name: " + i.getName()),
                new Label("🪵 Wood Stock: " + i.getStockpile(CargoType.WOOD)),
                new Label("🪨 Iron Stock: " + i.getStockpile(CargoType.IRON_ORE)),
                new Label("🏗️ Steel Stock: " + i.getStockpile(CargoType.STEEL))
            );
        }
        
        for(var node : detailPanel.getChildren()) {
            if(node instanceof Label) {
                node.setStyle(node.getStyle() + "-fx-text-fill: #5D4037; -fx-font-weight: bold;");
            }
        }
    }

    private void updateDetailPanelForTile(Tile tile) {
        detailPanel.getChildren().clear();
        Label title = new Label("🚦 Road Info");
        title.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-border-bottom-color: #FFB7B2; -fx-border-bottom-width: 2px; -fx-padding: 0 0 5 0; -fx-text-fill: #5D4037;");
        title.setMaxWidth(Double.MAX_VALUE);
        detailPanel.getChildren().add(title);

        if (tile instanceof RoadTile road) {
            detailPanel.getChildren().add(new Label("🛣️ Type: Road"));
            if (road.hasJunction() && road.getJunction().hasLight()) {
                TrafficLight light = road.getJunction().getTrafficLight();
                
                detailPanel.getChildren().add(new Label("🚦 Traffic Light:"));
                
                Label nsLabel = new Label("⬆️⬇️ NS Green: " + (int)light.getGreenNS() + "s");
                nsLabel.setStyle("-fx-text-fill: #5D4037; -fx-font-weight: bold;");
                HBox nsControls = createLightControls(light, nsLabel, true);
                
                Label ewLabel = new Label("⬅️➡️ EW Green: " + (int)light.getGreenEW() + "s");
                ewLabel.setStyle("-fx-text-fill: #5D4037; -fx-font-weight: bold;");
                HBox ewControls = createLightControls(light, ewLabel, false);

                detailPanel.getChildren().addAll(nsLabel, nsControls, ewLabel, ewControls);
            } else {
                detailPanel.getChildren().add(new Label("Status: Clear"));
            }
        }
    }

    private HBox createLightControls(TrafficLight light, Label labelToUpdate, boolean isNS) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        minusBtn.setStyle("-fx-background-color: #FFB7B2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        plusBtn.setStyle("-fx-background-color: #B5EAD7; -fx-text-fill: #5D4037; -fx-font-weight: bold; -fx-background-radius: 5;");

        minusBtn.setOnAction(e -> {
            double currentNS = light.getGreenNS();
            double currentEW = light.getGreenEW();
            if (isNS && currentNS > 2) light.setTimings(currentNS - 1, currentEW);
            if (!isNS && currentEW > 2) light.setTimings(currentNS, currentEW - 1);
            
            if (isNS) labelToUpdate.setText("⬆️⬇️ NS Green: " + (int)light.getGreenNS() + "s");
            else labelToUpdate.setText("⬅️➡️ EW Green: " + (int)light.getGreenEW() + "s");
        });

        plusBtn.setOnAction(e -> {
            double currentNS = light.getGreenNS();
            double currentEW = light.getGreenEW();
            if (isNS && currentNS < 20) light.setTimings(currentNS + 1, currentEW);
            if (!isNS && currentEW < 20) light.setTimings(currentNS, currentEW - 1);
            
            if (isNS) labelToUpdate.setText("⬆️⬇️ NS Green: " + (int)light.getGreenNS() + "s");
            else labelToUpdate.setText("⬅️➡️ EW Green: " + (int)light.getGreenEW() + "s");
        });

        box.getChildren().addAll(minusBtn, plusBtn);
        return box;
    }
}