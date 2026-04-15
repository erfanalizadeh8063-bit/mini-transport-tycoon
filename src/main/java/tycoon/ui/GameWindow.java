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
import javafx.scene.paint.Color;
import tycoon.model.*;
import tycoon.service.GameEngine;
import tycoon.service.SaveManager;

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
    private boolean isTrafficLightMode = false;

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
            WorldMap loadedMap = null;
            try { loadedMap = SaveManager.loadGame("savegame.dat"); } catch (Exception ex) {}
            
            if (loadedMap != null) {
                worldMap = loadedMap;
                engine = new GameEngine(worldMap);
                setupGameUI(); 
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! No saved game found!");
                alert.show();
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

        // 【核心修改】：地图初始化只保留城市和工厂，不自动生成任何马路！
        City budapest = new City(new Vector2(5, 5), 0.0, worldMap, "Budapest", 1000);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                worldMap.setTile(5 + i, 5 + j, budapest);
            }
        }

        Industry lumberMill = new Industry(new Vector2(15, 5), 0.0, worldMap, "Lumber Mill", CargoType.GOODS_A);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                worldMap.setTile(15 + i, 5 + j, lumberMill);
            }
        }
        
        Random rand = new Random();
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                Tile t = worldMap.getTile(x, y);
                if (t instanceof EmptyTile && rand.nextDouble() < 0.15) {
                    ((EmptyTile) t).setTreeCount(rand.nextInt(4) + 1); 
                }
            }
        }

        setupGameUI();
    }

    private void setupGameUI() {
        renderer = new GameRenderer();
        simulatedTime = 0;
        currentTool = ToolMode.INSPECT;
        pendingRoute.clear();
        inspectedFacility = null; // 重置

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
        
        StackPane miniMapContainer = new StackPane(minimapCanvas);
        miniMapContainer.setStyle("-fx-background-color: #E2F0CB; -fx-border-color: #B5EAD7; -fx-border-width: 4; -fx-border-radius: 5; -fx-background-radius: 5;");

        detailPanel = new VBox(10);
        detailPanel.setPrefWidth(180);
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
                        inspectedFacility = (Facility) tile; // 【记录】
                        updateDetailPanel(inspectedFacility);
                        detailPanel.setLayoutX(event.getSceneX() + 20);
                        detailPanel.setLayoutY(event.getSceneY() - 50);
                        detailPanel.setVisible(true);
                        updateStatus("🔍 Inspecting: " + ((Facility) tile).getName());
                    } else {
                        inspectedFacility = null; // 【清空】
                        detailPanel.setVisible(false);
                        updateStatus("🌿 Looking at tile (" + x + "," + y + ")");
                    }
                    break;

                case BUILD_ROAD:
                    if (tile instanceof EmptyTile) {
                        double cost = (((EmptyTile) tile).getTreeCount() > 0) ? 200 : 100;
                        if (engine.spendMoney(cost)) {
                            worldMap.setTile(x, y, new RoadTile(new Vector2(x, y), 0.0, worldMap, 50.0));
                            updateStatus("🔨 Road built! Spent $" + (int)cost);
                        } else updateStatus("❌ Oops! Not enough coins to build a road.");
                    } else updateStatus("🌱 You can only build roads on empty land!");
                    break;

                case PLACE_STOP:
                    if (tile instanceof EmptyTile) {
                        if (engine.spendMoney(200)) {
                            RoadTile stop = new RoadTile(new Vector2(x, y), 0.0, worldMap, 50.0);
                            worldMap.setTile(x, y, stop);
                            
                            // 检查四周有没有建筑，有的话就绑定大门！
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
                    if (tile instanceof RoadTile) {
                        if (engine.spendMoney(50)) updateStatus("🚦 Traffic light installed!");
                        else updateStatus("❌ Not enough coins for a Traffic Light.");
                    }
                    break;

                case BULLDOZE:
                    // 【新增】：推土机逻辑
                    if (tile instanceof RoadTile) {
                        if (engine.spendMoney(50)) { // 拆迁费 50 块
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
                            dialog.setHeaderText("Choose a cute vehicle for this route:");
                            
                            ButtonType btnTruck = new ButtonType("🚛 Truck ($500)");
                            ButtonType btnBus = new ButtonType("🚌 Bus ($400)");
                            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            dialog.getButtonTypes().setAll(btnTruck, btnBus, btnCancel);

                            dialog.showAndWait().ifPresent(type -> {
                                if (type == btnTruck) {
                                    if (engine.spendMoney(500)) {
                                        Vehicle v = new Truck("TRUCK-" + (System.currentTimeMillis()%1000));
                                        v.setRoute(new Route(new ArrayList<>(pendingRoute)));
                                        v.setCurrentTile(pendingRoute.get(0).getAccessTile());
                                        engine.addVehicle(v);
                                        updateStatus("🎉 Yay! A new Truck joined your fleet!");
                                    } else updateStatus("❌ Not enough coins for a Truck.");
                                } else if (type == btnBus) {
                                    if (engine.spendMoney(400)) {
                                        Vehicle v = new Bus("BUS-" + (System.currentTimeMillis()%1000));
                                        v.setRoute(new Route(new ArrayList<>(pendingRoute)));
                                        v.setCurrentTile(pendingRoute.get(0).getAccessTile());
                                        engine.addVehicle(v);
                                        updateStatus("🎉 Yay! A new Bus joined your fleet!");
                                    } else updateStatus("❌ Not enough coins for a Bus.");
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

                    // 【核心修改】：在这里实时刷新面板数据！
                    if (detailPanel.isVisible() && inspectedFacility != null) {
                        updateDetailPanel(inspectedFacility);
                    }

                    renderer.render(canvas.getGraphicsContext2D(), worldMap, engine.getVehicles());
                    minimapRenderer.render(minimapCanvas.getGraphicsContext2D(), worldMap);

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
        Button pause = createSmallBtn("⏸️"); pause.setOnAction(e -> engine.setSimulationSpeed(0));
        Button play = createSmallBtn("▶️"); play.setOnAction(e -> engine.setSimulationSpeed(1));
        Button fast = createSmallBtn("⏩"); fast.setOnAction(e -> engine.setSimulationSpeed(4));
        speedBox.getChildren().addAll(pause, play, fast);

        Button close = new Button("💾 Save & Quit");
        close.setStyle("-fx-background-color: #FFB7B2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
        close.setOnAction(e -> {
            SaveManager.saveGame(worldMap, "savegame.dat");
            showMainMenu();
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
        ToggleButton roadBtn = createToolBtn("🔨 Build Road\n($100)");
        ToggleButton stopBtn = createToolBtn("🚏 Place Stop\n($200)");
        ToggleButton lightBtn = createToolBtn("🚦 Traffic Light\n($50)");
        ToggleButton routeBtn = createToolBtn("🚛 Buy Vehicle\n($500)");
        // 【新增】：推土机按钮
        ToggleButton bulldozeBtn = createToolBtn("🧨 Bulldoze\n($50)");

        inspectBtn.setToggleGroup(tools);
        roadBtn.setToggleGroup(tools);
        stopBtn.setToggleGroup(tools);
        lightBtn.setToggleGroup(tools);
        routeBtn.setToggleGroup(tools);
        bulldozeBtn.setToggleGroup(tools); // 别忘了加组

        inspectBtn.setSelected(true);

        tools.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                inspectBtn.setSelected(true); 
            } else {
                if (newVal == inspectBtn) { currentTool = ToolMode.INSPECT; updateStatus("🔍 Mode: Inspecting tiles."); }
                else if (newVal == roadBtn) { currentTool = ToolMode.BUILD_ROAD; updateStatus("🔨 Mode: Click empty land to build roads."); }
                else if (newVal == stopBtn) { currentTool = ToolMode.PLACE_STOP; updateStatus("🚏 Mode: Click land adjacent to a facility to place a Stop."); }
                else if (newVal == lightBtn) { currentTool = ToolMode.TRAFFIC_LIGHT; updateStatus("🚦 Mode: Click a road to install Traffic Lights."); }
                else if (newVal == routeBtn) { 
                    currentTool = ToolMode.BUY_VEHICLE; 
                    pendingRoute.clear();
                    updateStatus("🚛 Mode: Click Facility A, then Facility B to spawn a vehicle."); 
                }
                // 【新增监听逻辑】
                else if (newVal == bulldozeBtn) {
                    currentTool = ToolMode.BULLDOZE;
                    updateStatus("🧨 Mode: Click a road to destroy it and clear the land.");
                }
            }
        });

        // 加上 bulldozeBtn 显示
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
                new Label("📦 Produces: Goods"),
                new Label("🪵 Stockpile: " + i.getStockpile(CargoType.GOODS_A))
            );
        }
        
        for(var node : detailPanel.getChildren()) {
            if(node instanceof Label) {
                node.setStyle(node.getStyle() + "-fx-text-fill: #5D4037; -fx-font-weight: bold;");
            }
        }
    }
}