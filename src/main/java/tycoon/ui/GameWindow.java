package tycoon.ui;
import tycoon.service.GameEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tycoon.model.*;
import java.util.ArrayList;

/**
 * Main UI window based on the provided wireframes.
 */
public class GameWindow extends Application {
    private static final int TILE_SIZE = 40;
    private WorldMap worldMap;
    private GameEngine engine;
    private GameRenderer renderer;

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize logic
        worldMap = new WorldMap(30, 20); // Create a 30x20 grid map [cite: 10]
        engine = new GameEngine(worldMap);
        renderer = new GameRenderer();

        // 2. Build Layout
        BorderPane root = new BorderPane();

        // Top Bar: Company info and Time controls
        root.setTop(createTopBar());

        // Center: Scrollable Game Map [cite: 102]
        ScrollPane scrollPane = new ScrollPane();
        Canvas canvas = new Canvas(worldMap.getWidth() * TILE_SIZE, worldMap.getHeight() * TILE_SIZE);
        scrollPane.setContent(canvas);
        root.setCenter(scrollPane);

        // Bottom Bar: Action buttons
        root.setBottom(createBottomBar());

        // 3. Animation Timer (The Game Loop)
        // This handles smooth movement and real-time updates [cite: 12, 110]
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                  double dt = (now - lastUpdate) / 1_000_000_000.0;
                  engine.tick(dt); 
        // 注意：你需要在 GameEngine 类里增加 getVehicles() 方法
                  renderer.render(canvas.getGraphicsContext2D(), worldMap, engine.getVehicles()); 
                }
                lastUpdate = now;
            }
        }.start();

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setTitle("Mini Transport Tycoon - Milestone 2");
        primaryStage.setScene(scene);
        // 4. Interaction: Click to build roads
        canvas.setOnMouseClicked(event -> {
         // Convert pixel coordinates to grid coordinates
            int x = (int) (event.getX() / TILE_SIZE);
            int y = (int) (event.getY() / TILE_SIZE);
    
            Tile currentTile = worldMap.getTile(x, y);
            if (currentTile instanceof EmptyTile) {
             // Replace with a RoadTile (Assuming height 0 and speed limit 50.0)
                RoadTile newRoad = new RoadTile(new Vector2(x, y), 0, worldMap, 50.0);
                worldMap.setTile(x, y, newRoad);
                // The AnimationTimer will automatically re-render this in the next frame
                System.out.println("Building road at: " + x + "," + y);
            }
        });
        primaryStage.show();
    }

    private HBox createTopBar() {
        HBox top = new HBox(20);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #999;");
        
        Label companyName = new Label("Company: ELTE Tycoon");
        Label capital = new Label("Capital: $" + engine.getBalance());
        
        // Time Speed Controls [cite: 49, 52, 53]
        HBox speedBox = new HBox(5, 
            new Button("||"), new Button(">"), new Button(">>"), new Button(">>>")
        );
        
        top.getChildren().addAll(companyName, capital, new Pane(), speedBox);
        HBox.setHgrow(top.getChildren().get(2), Priority.ALWAYS); // Spacer
        return top;
    }

    private HBox createBottomBar() {
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #999;");
        
        // Build Buttons from your wireframe
        bottom.getChildren().addAll(
            new Button("Build Road"),
            new Button("Place Stop"),
            new Button("Buy Vehicle"),
            new Button("Route Editor"),
            new Button("Traffic Lights")
        );
        return bottom;
    }

    public static void main(String[] args) {
        launch(args);
    }
}