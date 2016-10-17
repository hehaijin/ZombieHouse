package zombiehouse.graphics;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import zombiehouse.audio.AudioFiles;
import zombiehouse.audio.DirectionalPlayer;
import zombiehouse.common.InputContainer;
import zombiehouse.common.LevelVar;
import zombiehouse.common.Player;
import zombiehouse.level.PastSelf;
import zombiehouse.level.house.*;
import zombiehouse.level.zombie.*;

import java.awt.*;

import java.util.ArrayList;


/**
 * This class manages all 3D rendering, sets up key listeners,
 * and responds to key events.
 * <p>
 * WASD used for traditional movement, mouse swivels the camera.
 * <p>
 * Player cannot move through walls, and zombie collisions trigger
 * a level reset.
 *
 * @author Maxwell Sanchez
 */
public class MainApplication extends Application
{

  private double cameraXDisplacement = 0;
  private double cameraYDisplacement = -375;
  private double cameraZDisplacement = 0;

  private double cameraYRotation = 0;

  private static final double TARGET_FRAMES_PER_SECOND = 60;

  private static final double PLAYER_TURN_SPEED = 0.07;
  private static final double PLAYER_TURN_SMOOTHING = 0.36;

  private static final double FLOOR_Y_DISPLACEMENT = -10;
  private static final double CEILING_Y_DISPLACEMENT = -600;
  private static final double WALL_HEIGHT = 600;
  private static final double TILE_WIDTH_AND_HEIGHT = 400;
  private static final double WALL_COLLISION_OFFSET = 0.35;

  private static final int WINDOW_WIDTH = 800;
  private static final int WINDOW_HEIGHT = 600;

  private static final PhongMaterial floorMaterial1 = new PhongMaterial();
  private static final PhongMaterial floorMaterial2 = new PhongMaterial();
  private static final PhongMaterial floorMaterial3 = new PhongMaterial();
  private static final PhongMaterial floorMaterial4 = new PhongMaterial();
  private static final PhongMaterial ceilingMaterial = new PhongMaterial();
  private static final PhongMaterial bookcaseMaterial = new PhongMaterial();
  private static final PhongMaterial wallMaterial = new PhongMaterial();
  private static final PhongMaterial exitMaterial = new PhongMaterial();
  private static final PhongMaterial tapestryMaterial[] = new PhongMaterial[4];

  private Level level;
  private Stage stage;

  private PointLight pl;
  private PerspectiveCamera camera;
  private Group sceneRoot;
  private Image life5;
  private ImageView lifeView;
  private Rectangle staminaBar;
  private Label staminaLabel;
  private Rectangle verticalCross;
  private Rectangle horizontalCross;

  private ArrayList<Double> xPos = new ArrayList<>();
  private ArrayList<Double> yPos = new ArrayList<>();
  private ArrayList<Double> cameraPos = new ArrayList<>();
  private ArrayList<Zombie> toAddToInteractedCollection = new ArrayList<>();
  private ArrayList<Zombie> toAddToBifurcatedCollection = new ArrayList<>();
  private boolean spawnPastSelf = false;
  private long lastTime = 0;
  private int deathFrame = 0;

  private GameLoop gameLoop = new GameLoop();
  private int zombieKillCount = 0;
  private int levelCount = 0;

  FXMLLoader fxmlloader = new FXMLLoader();

  {
    fxmlloader.setLocation(getClass().getResource("/res/Bayonet.fxml"));
  }

  Group knife = null;
  TranslateTransition tt = new TranslateTransition();


  /**
   * Create a robot to reset the mouse to the middle of the screen.
   */
  private Robot robot;

  {
    try
    {
      robot = new Robot();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  // Stores requests to rebuild the level graphically, so that rebuilding is done in a thread-safe manner
  boolean shouldRebuildLevel = false;

  /**
   * Called on initial application startup. Setup the camera, point light,
   * scene, key listeners, and materials, as well as starting the
   * primary game loop.
   *
   * @param stage The stage to set up the 3D graphics on
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    stage.setOnCloseRequest(event -> System.exit(0));
    this.stage = stage;
    //creates a stackpane as container for 2D and 3D scenes.
    //xcene is now the scene for primary stage.
    StackPane pane = new StackPane();

    Scene xscene = new Scene(pane, 800, 600, true, SceneAntialiasing.BALANCED);

    // Create group to hold 3D objects
    sceneRoot = new Group();
    SubScene scene = new SubScene(sceneRoot, WINDOW_WIDTH, WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
    scene.setFill(Color.BLACK);
    pane.getChildren().add(scene);

    life5 = new Image(getClass().getResourceAsStream("/res/life5.png"));
    lifeView = new ImageView(life5);
    Label life = new Label("", lifeView);

    staminaBar = new Rectangle(150, 13);
    staminaBar.setFill(Color.BLUE);
    pane.getChildren().add(staminaBar);
    StackPane.setAlignment(staminaBar, Pos.TOP_LEFT);
    staminaBar.getTransforms().add(new Translate(25, 65));

    staminaLabel = new Label("Stamina: " + Player.stamina);
    staminaLabel.setTextFill(Color.BLACK);
    pane.getChildren().add(staminaLabel);
    StackPane.setAlignment(staminaLabel, Pos.TOP_LEFT);
    staminaLabel.getTransforms().add(new Translate(65, 63));

    verticalCross = new Rectangle(2, 20);
    verticalCross.setFill(Color.GREEN);
    pane.getChildren().add(verticalCross);

    horizontalCross = new Rectangle(20, 2);
    horizontalCross.setFill(Color.GREEN);
    pane.getChildren().add(horizontalCross);

    pane.getChildren().add(life);
    StackPane.setAlignment(life, Pos.TOP_LEFT);
    life.getTransforms().add(new Translate(20, 20));
    life.getTransforms().add(new Scale(0.1, 0.1));
    scene.heightProperty().bind(pane.heightProperty());
    scene.widthProperty().bind(pane.widthProperty());

    // Hide the cursor
    xscene.setCursor(Cursor.NONE);


    // Spawn the first level
    LevelVar.zombie3D = true;
    level = new Level();
    level.nextLevel();
    level.fullGen();

    // Create a "lantern" for the user
    pl = new PointLight(Color.WHITE);
    pl.setDepthTest(DepthTest.ENABLE);
    pl.setTranslateY(cameraYDisplacement);

    sceneRoot.getChildren().add(pl);
    AmbientLight am = new AmbientLight();
    // sceneRoot.getChildren().add(am);

    // Create the camera, set it to view far enough for any reasonably-sized map
    camera = new PerspectiveCamera(true);
    camera.setNearClip(0.1);
    camera.setFarClip(4000.0);
    camera.setFieldOfView(62.5);

    // Rotate camera on the y-axis for swivel in response to mouse
    camera.setVerticalFieldOfView(true);
    camera.setTranslateZ(cameraZDisplacement);
    camera.setTranslateY(cameraYDisplacement);
    camera.setRotationAxis(Rotate.Y_AXIS);
    camera.setDepthTest(DepthTest.ENABLE);
    scene.setCamera(camera);


    //knife=fxmlloader.load();
    //knife.setScaleX(20);
    //knife.setScaleY(20);
    //knife.setScaleZ(20);
    //knife.setTranslateZ(cameraZDisplacement);
    //knife.setTranslateY(cameraYDisplacement + 100);
    //knife.setRotationAxis(Rotate.Y_AXIS);
    //sceneRoot.getChildren().add(knife);
    //knife.setTranslateY(-330);
    //knife.getTransforms().add(new Rotate(90,0,0,0,Rotate.Z_AXIS));
    //knife.getTransforms().add(new Rotate(-90,0,0,0,Rotate.Y_AXIS));

    // Set up key listeners for WASD (movement), F1/F2 (full screen toggle), Shift (run), Escape (exit), F3 (cheat)
    xscene.setOnKeyPressed(event ->
    {
      KeyCode keycode = event.getCode();
      if (keycode == KeyCode.W)
      {
        InputContainer.forward = true;
      } else if (keycode == KeyCode.S)
      {
        InputContainer.backward = true;
      } else if (keycode == KeyCode.A)
      {
        InputContainer.left = true;
      } else if (keycode == KeyCode.D)
      {
        InputContainer.right = true;
      } else if (keycode == KeyCode.SPACE)
      {
        InputContainer.hit = true;
        tt.setDuration(Duration.millis(300));
        tt.setNode(knife);
        tt.setByX(50 * Math.sin(cameraYRotation / 180 * 3.1415));
        tt.setByZ(50 * Math.cos(cameraYRotation / 180 * 3.1415));
        tt.setAutoReverse(true);
        tt.play();
        AudioFiles.shout.setVolume(0.25);
        AudioFiles.shout.play();

      } else if (keycode == KeyCode.F1)
      {
        stage.setFullScreen(true);
      } else if (keycode == KeyCode.F2)
      {
        stage.setFullScreen(false);
      } else if (keycode == KeyCode.SHIFT)
      {
        InputContainer.run = true;
      } else if (keycode == KeyCode.ESCAPE)
      {
        System.exit(0);
      } else if (keycode == KeyCode.F3) /* Cheat key to advance levels */
      {
        level.nextLevel();
        rebuildLevel();
      }
    });

    xscene.setOnKeyReleased(event ->
    {
      KeyCode keycode = event.getCode();
      if (keycode == KeyCode.W)
      {
        InputContainer.forward = false;
      } else if (keycode == KeyCode.S)
      {
        InputContainer.backward = false;
      } else if (keycode == KeyCode.A)
      {
        InputContainer.left = false;
      } else if (keycode == KeyCode.D)
      {
        InputContainer.right = false;
      } else if (keycode == KeyCode.SPACE)
      {
        InputContainer.hit = false;
      } else if (keycode == KeyCode.SHIFT)
      {
        InputContainer.run = false;
      }
    });

    // Add mouse listener
    xscene.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
    {
      double rotateAmountY = event.getScreenX() - InputContainer.lastMouseX;
      rotateAmountY *= PLAYER_TURN_SPEED;

      // Smooth inertia swivel
      InputContainer.remainingCameraPan += rotateAmountY;

      try
      {
        double topX = event.getScreenX() - event.getSceneX();
        double topY = event.getScreenY() - event.getSceneY();

        // Reset mouse to middle of screen
        robot.mouseMove((int) topX + (int) scene.getWidth() / 2, (int) topY + (int) scene.getHeight() / 2);

        InputContainer.lastMouseX = topX + scene.getWidth() / 2;
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    });

    stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
    stage.setScene(xscene);
    stage.show();

    // Load textures from files to use for floor, walls, and ceiling
    floorMaterial1.setDiffuseColor(Color.WHITE);
    floorMaterial1.setSpecularColor(Color.WHITE.darker());
    floorMaterial1.setSpecularPower(128);
    floorMaterial1.setDiffuseMap(new Image(getClass().getResource("/res/brick.png").toExternalForm()));

    floorMaterial2.setDiffuseColor(Color.WHITE);
    floorMaterial2.setSpecularColor(Color.WHITE.darker());
    floorMaterial2.setSpecularPower(128);
    floorMaterial2.setDiffuseMap(new Image(getClass().getResource("/res/tiles.png").toExternalForm()));

    floorMaterial3.setDiffuseColor(Color.WHITE);
    floorMaterial3.setSpecularColor(Color.WHITE.darker());
    floorMaterial3.setSpecularPower(128);
    floorMaterial3.setDiffuseMap(new Image(getClass().getResource("/res/floor1.png").toExternalForm()));

    floorMaterial4.setDiffuseColor(Color.WHITE);
    floorMaterial4.setSpecularColor(Color.WHITE.darker());
    floorMaterial4.setSpecularPower(128);
    floorMaterial4.setDiffuseMap(new Image(getClass().getResource("/res/wooden.png").toExternalForm()));

    bookcaseMaterial.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0));
    bookcaseMaterial.setSpecularColor(Color.BLACK);
    bookcaseMaterial.setSpecularPower(256);
    bookcaseMaterial.setDiffuseMap(new Image(getClass().getResource("/res/bookcase2.png").toExternalForm()));


    ceilingMaterial.setDiffuseColor(Color.WHITE);
    ceilingMaterial.setSpecularColor(Color.BLACK.darker().darker().darker().darker());
    ceilingMaterial.setSpecularPower(25);
    ceilingMaterial.setDiffuseMap(new Image(getClass().getResource("/res/shale.png").toExternalForm()));

    wallMaterial.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0));
    wallMaterial.setSpecularColor(Color.BLACK);
    wallMaterial.setSpecularPower(256);
    wallMaterial.setDiffuseMap(new Image(getClass().getResource("/res/wall.png").toExternalForm()));

    for (int i = 1; i <= 4; i++)
    {
      tapestryMaterial[i - 1] = new PhongMaterial();
      tapestryMaterial[i - 1].setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0));
      tapestryMaterial[i - 1].setSpecularColor(Color.BLACK);
      tapestryMaterial[i - 1].setSpecularPower(256);
      tapestryMaterial[i - 1].setDiffuseMap(new Image(getClass().getResource("/res/demon" + i + ".png").toExternalForm()));
    }


    exitMaterial.setDiffuseColor(Color.WHITE);
    exitMaterial.setSpecularColor(Color.WHITE);

    setupLevel();

    gameLoop.start();
  }

  /**
   * Informs the program that a level rebuild has been requested.
   */
  public void rebuildLevel()
  {
    shouldRebuildLevel = true;
  }

  /**
   * Sets up the 3D objects to represent a 2D Tile[][] house in a 3D world.
   */
  public void setupLevel()
  {
    Tile[][] house = LevelVar.house;
    // Loop through all tiles


    for (int x = 0; x < house.length; x++)
    {
      for (int z = 0; z < house[0].length; z++)
      {
        // Always have a floor and ceiling
        //4*4 floor share a box
        if (x % 4 == 0 && z % 4 == 0)
        {
          Box floor = new Box(TILE_WIDTH_AND_HEIGHT * 4, 10, TILE_WIDTH_AND_HEIGHT * 4);

          if (house[x][z].zone == 0)
          {
            floor.setMaterial(floorMaterial1);
          } else if (house[x][z].zone == 1)
          {
            floor.setMaterial(floorMaterial2);
          } else if (house[x][z].zone == 2)
          {
            floor.setMaterial(floorMaterial3);
          } else
          {
            floor.setMaterial(floorMaterial4);
          }

          floor.setTranslateY(FLOOR_Y_DISPLACEMENT);
          floor.setTranslateX((x + 2) * TILE_WIDTH_AND_HEIGHT);
          floor.setTranslateZ((z + 2) * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(floor);

        }
        //4*4 ceiling share a box
        if (x % 4 == 0 && z % 4 == 0)
        {
          Box ceiling = new Box(TILE_WIDTH_AND_HEIGHT * 4, 10, TILE_WIDTH_AND_HEIGHT * 4);
          ceiling.setMaterial(ceilingMaterial);
          ceiling.setTranslateY(CEILING_Y_DISPLACEMENT);
          ceiling.setTranslateX((x + 2) * TILE_WIDTH_AND_HEIGHT);
          ceiling.setTranslateZ((z + 2) * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(ceiling);
        }


        // If wall, place a ground-to-ceiling wall box
        if (house[x][z] instanceof Wall && !(house[x][z] instanceof BookCase) && !(house[x][z] instanceof Tapestry))
        {
          Box wall = new Box(TILE_WIDTH_AND_HEIGHT, WALL_HEIGHT, TILE_WIDTH_AND_HEIGHT);
          wall.setMaterial(wallMaterial);
          wall.setTranslateY(-WALL_HEIGHT / 2);
          wall.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
          wall.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(wall);
        }

        if (house[x][z] instanceof BookCase)
        {
          Box bookcase = new Box(TILE_WIDTH_AND_HEIGHT, WALL_HEIGHT, TILE_WIDTH_AND_HEIGHT);
          bookcase.setMaterial(bookcaseMaterial);
          bookcase.setTranslateY(-WALL_HEIGHT / 2);
          bookcase.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
          bookcase.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(bookcase);
        }

        if (house[x][z] instanceof Tapestry)
        {
          Box tapestry = new Box(TILE_WIDTH_AND_HEIGHT, WALL_HEIGHT, TILE_WIDTH_AND_HEIGHT);
          tapestry.setMaterial(tapestryMaterial[(int) (Math.random() * 4)]);
          tapestry.setTranslateY(-WALL_HEIGHT / 2);
          tapestry.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
          tapestry.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(tapestry);
        }

        // If exit, place a ground-to-ceiling exit box
        else if (house[x][z] instanceof Exit)
        {
          Box exit = new Box(TILE_WIDTH_AND_HEIGHT, WALL_HEIGHT, TILE_WIDTH_AND_HEIGHT);
          exit.setMaterial(exitMaterial);
          exit.setTranslateY(-WALL_HEIGHT / 2);
          exit.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
          exit.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(exit);
        }
      }
    }

    toAddToInteractedCollection.clear();

    if (LevelVar.interactedWithZombieCollection.size() > 0)
    {
      for (Zombie zombie : LevelVar.interactedWithZombieCollection)
      {
        for (int i = 0; i < LevelVar.zombieCollection.size(); i++)
        {
          if (zombie.zombieID == LevelVar.zombieCollection.get(i).zombieID)
          {
            LevelVar.zombieCollection.remove(i);
          }
        }
        if (!sceneRoot.getChildren().contains(zombie.zombie3D))
        {
          sceneRoot.getChildren().add(zombie.zombie3D);
        }
      }
    }

    sceneRoot.getChildren().add(Player.player3D);

    // Add all of the 3D zombie objects
    for (Zombie zombie : LevelVar.zombieCollection)
    {
      sceneRoot.getChildren().add(zombie.zombie3D);
    }

    for (PastSelf ps : LevelVar.pastSelfCollection)
    {
      sceneRoot.getChildren().add(ps.pastSelf3D);
    }

    //System.out.println(LevelVar.zombieCollection.size());
  }

  private void addEndScreen()
  {
    gameLoop.stop();

    Stage endDialog = new Stage();
    endDialog.setTitle("Your brains have been eaten.");
    VBox mainBox = new VBox(10);
    mainBox.setPrefSize(400, 200);
    mainBox.setAlignment(Pos.CENTER);

    Label text = new Label("You have used up all 3 lives!\nHighest level: " + (LevelVar.levelNum + 1) +
            "\nZombies killed: " + zombieKillCount + "\nBetter luck next time.");
    text.setAlignment(Pos.CENTER);
    text.setPrefSize(400, 100);

    HBox hB = new HBox(10);
    hB.setPrefSize(400, 100);
    hB.setAlignment(Pos.CENTER);
    CloseButton cB = new CloseButton(stage, endDialog);
    cB.setPrefSize(120, 40);
    RestartButton rB = new RestartButton(level, endDialog, gameLoop);
    rB.setPrefSize(120, 40);
    hB.getChildren().addAll(cB, rB);

    mainBox.getChildren().addAll(text, hB);

    Scene sc = new Scene(mainBox);
    endDialog.setScene(sc);
    endDialog.show();
  }

  /**
   * @author Maxwell Sanchez
   *         <p>
   *         GameLoop handles the primary game animation frame timing.
   */
  class GameLoop extends AnimationTimer
  {
    // Used for timing events that don't happen every frame
    int frame = 0;
    int playerFrame = 0;
    // The last-used user walking clip
    int lastClip = 1;
    long lastFrame = System.nanoTime();

    /**
     * Moves the player, if possible (no wall collisions) in the direction(s) requested by the user
     * with keyboard input, given the current angle determined by previous mouse input.
     */
    public void movePlayerIfRequested(double percentOfSecond)
    {
      playerFrame++;
      double desiredZDisplacement = 0;

      // Calculate information for horizontal and vertical player movement based on direction
      double cos = Math.cos(cameraYRotation / 180.0 * 3.1415);
      double sin = Math.sin(cameraYRotation / 180.0 * 3.1415);

      // Include all user input (including those which cancel out) to determine z offset
      desiredZDisplacement += (InputContainer.forward) ? (cos) : 0;
      desiredZDisplacement -= (InputContainer.backward) ? (cos) : 0;
      desiredZDisplacement += (InputContainer.left) ? (sin) : 0;
      desiredZDisplacement -= (InputContainer.right) ? (sin) : 0;

      // Include all user input (including those which cancel out) to determine x offset
      double desiredXDisplacement = 0;
      desiredXDisplacement += (InputContainer.forward) ? (sin) : 0;
      desiredXDisplacement -= (InputContainer.backward) ? (sin) : 0;
      desiredXDisplacement -= (InputContainer.left) ? (cos) : 0;
      desiredXDisplacement += (InputContainer.right) ? (cos) : 0;

      // Prevent diagonal move speed-boost
      double displacementMagnitude = Math.abs(desiredZDisplacement) + Math.abs(desiredXDisplacement);
      double displacementScaleFactor = 1 / displacementMagnitude;

      boolean isRunning = false;

      if (Double.isInfinite(displacementScaleFactor)) displacementScaleFactor = 1;
      if (InputContainer.run && Player.stamina > 0)
      {
        displacementScaleFactor *= 2;
        Player.stamina -= 1.0 / TARGET_FRAMES_PER_SECOND;
        isRunning = true;
      }

      // Player out of stamina
      else if (Player.stamina <= 0)
      {
        InputContainer.run = false;
      }

      // Player is not *trying* to run, so allow stamina regeneration
      if (!InputContainer.run)
      {
        Player.stamina += Player.staminaRegen / TARGET_FRAMES_PER_SECOND;
        if (Player.stamina > Player.maxStamina) Player.stamina = Player.maxStamina;
      }

      double roundOffStamina = (double) Math.round(Player.stamina * 100) / 100;

      staminaLabel.setText("Stamina: " + roundOffStamina);

      if (Player.stamina >= 4.0)
      {
        staminaBar.setFill(Color.BLUE);
      } else if (Player.stamina >= 3.0)
      {
        staminaBar.setFill(Color.GREEN);
      } else if (Player.stamina >= 2.0)
      {
        staminaBar.setFill(Color.YELLOW);
      } else if (Player.stamina >= 1.0)
      {
        staminaBar.setFill(Color.ORANGE);
      } else if (Player.stamina >= 0.0)
      {
        staminaBar.setFill(Color.RED);
      }

      // How often to play the stepping noise (walking vs running)
      int stepFrequency = isRunning ? 20 : 40;

      // Play walking noises if player is moving
      if (desiredXDisplacement != 0 || desiredZDisplacement != 0)
      {
        if (frame % stepFrequency == 0)
        {
          // Alternate step clips
          if (lastClip == 2)
          {
            AudioFiles.userStep1.setVolume(isRunning ? 0.4 : 0.25);
            AudioFiles.userStep1.play();
            lastClip = 1;
          } else if (lastClip == 1)
          {
            AudioFiles.userStep2.setVolume(isRunning ? 0.4 : 0.25);
            AudioFiles.userStep2.play();
            lastClip = 2;
          }
        }
      }
      desiredXDisplacement *= displacementScaleFactor;
      desiredZDisplacement *= displacementScaleFactor;

      // If possible, the position the player indicated they wanted to move to
      double desiredPlayerXPosition = Player.xPosition + (desiredXDisplacement * (percentOfSecond * Player.playerSpeed));
      double desiredPlayerYPosition = Player.yPosition + (desiredZDisplacement * (percentOfSecond * Player.playerSpeed));

      // Player reached the exit
      if (LevelVar.house[(int) desiredPlayerXPosition][(int) desiredPlayerYPosition] instanceof Exit)
      {
        System.out.println("next level...");
        level.nextLevel();
        stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
        rebuildLevel();
      }

      // "Unstick" player
      while (!(LevelVar.house[round(Player.xPosition)][round(Player.yPosition)] instanceof Tile))
      {
        if (Player.xPosition < 5)
        {
          Player.xPosition += 1;
          Player.xPosition += 1;
        } else
        {
          Player.xPosition -= 1;
        }
      }

      boolean canMove = true;
      boolean wallCollisionMove = true;
      boolean masterZombieSense = false;

      for (Zombie z : LevelVar.zombieCollection)
      {
        double distanceX = (z.positionX - Player.xPosition);
        double distanceY = (z.positionY - Player.yPosition);
        double totalDistance = Math.abs(distanceX) + Math.abs(distanceY);

        if ((totalDistance < 0.3))
        {
          canMove = false;
        }

        if (z.type == 2 && totalDistance < 10)
        {
          masterZombieSense = true;
        }

        if (totalDistance < 2)
        {
          verticalCross.setFill(Color.GREEN);
          horizontalCross.setFill(Color.GREEN);
        }
        if (totalDistance < 1)
        {
          verticalCross.setFill(Color.YELLOW);
          horizontalCross.setFill(Color.YELLOW);
        }
        if (totalDistance < 0.5)
        {
          verticalCross.setFill(Color.RED);
          horizontalCross.setFill(Color.RED);
        }
      }

      if ((LevelVar.house[round(desiredPlayerXPosition + WALL_COLLISION_OFFSET)][round(Player.yPosition)] instanceof Wall) ||
              (LevelVar.house[round(desiredPlayerXPosition - WALL_COLLISION_OFFSET)][round(Player.yPosition)] instanceof Wall) ||
              (LevelVar.house[round(Player.xPosition)][round(desiredPlayerYPosition + WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(Player.xPosition)][round(desiredPlayerYPosition - WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(desiredPlayerXPosition + WALL_COLLISION_OFFSET)][round(desiredPlayerYPosition + WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(desiredPlayerXPosition - WALL_COLLISION_OFFSET)][round(desiredPlayerYPosition - WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(desiredPlayerXPosition + WALL_COLLISION_OFFSET)][round(desiredPlayerYPosition - WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(desiredPlayerXPosition - WALL_COLLISION_OFFSET)][round(desiredPlayerYPosition + WALL_COLLISION_OFFSET)] instanceof Wall) ||
              (LevelVar.house[round(Player.xPosition)][round(Player.yPosition)] instanceof Wall))
      {
        wallCollisionMove = false;
      }

      if (canMove && wallCollisionMove && !masterZombieSense)
      {
        Player.xPosition += desiredXDisplacement * (percentOfSecond * Player.playerSpeed);
        Player.yPosition += desiredZDisplacement * (percentOfSecond * Player.playerSpeed);
      } else if (canMove && wallCollisionMove && masterZombieSense)
      {
        Player.xPosition += (desiredXDisplacement * (percentOfSecond * Player.playerSpeed)) / 2;
        Player.yPosition += (desiredZDisplacement * (percentOfSecond * Player.playerSpeed)) / 2;
      } else if (!wallCollisionMove)
      {
        Player.xPosition -= 0.0001;
        Player.yPosition -= 0.0001;
      } else
      {
        Player.xPosition -= desiredXDisplacement * (percentOfSecond * Player.playerSpeed);
        Player.yPosition -= desiredZDisplacement * (percentOfSecond * Player.playerSpeed);
      }


      // Calculate camera displacement
      cameraXDisplacement = Player.xPosition * TILE_WIDTH_AND_HEIGHT;
      cameraZDisplacement = Player.yPosition * TILE_WIDTH_AND_HEIGHT;

      // Move the point light with the light
      pl.setTranslateX(cameraXDisplacement);
      pl.setTranslateZ(cameraZDisplacement);

      // Calculate camera rotation
      cameraYRotation += PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;

      double lastCameraYRotation = camera.getRotate();
      double xOffset = 200 * Math.sin(cameraYRotation / 180 * Math.PI);
      double yOffset = 200 * Math.cos(cameraYRotation / 180 * Math.PI);
      Player.player3D.setTranslateX(cameraXDisplacement + xOffset);
      Player.player3D.setTranslateZ(cameraZDisplacement + yOffset);
      Player.player3D.setRotate(cameraYRotation - 180);
      if (playerFrame % 4 == 0)
      {
        Player.player3D.nextFrame();
      } else if (isRunning)
      {
        Player.player3D.nextFrame();
      }

      // Displace camera
      camera.setTranslateX(cameraXDisplacement);
      camera.setTranslateZ(cameraZDisplacement);

      // Rotate the camera
      camera.setRotate(cameraYRotation);
      

      /*if(tt.getStatus()!= Animation.Status.RUNNING) {

        knife.setTranslateX(cameraXDisplacement + 90 + 100 * Math.sin(cameraYRotation / 180 * 3.1415));
        knife.setTranslateZ(cameraZDisplacement + 100 * Math.cos(cameraYRotation / 180 * 3.1415));
        knife.setRotate(cameraYRotation);
      }*/

      xPos.add(Player.xPosition);
      yPos.add(Player.yPosition);
      cameraPos.add(cameraYRotation);

      // Used for movement and swivel smoothing
      InputContainer.remainingCameraPan -= PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;
    }

    /**
     * Rounds the provided number up if decimal component >= 0.5, otherwise down.
     *
     * @param toRound Double to round
     * @return int Rounded number
     */
    private int round(double toRound)
    {
      if (toRound - ((int) toRound) < 0.5)
      {
        return (int) toRound;
      } else
      {
        return (int) toRound + 1;
      }
    }

    /**
     * Calculates the angle between two vectors, useful in directional sound calculation.
     *
     * @param x1 X component of vector 1
     * @param y1 Y component of vector 1
     * @param x2 X component of vector 2
     * @param y2 Y component of vector 2
     * @return double Angle, in degrees, between the provided vectors
     */
    public double angleBetweenVectors(double x1, double y1, double x2, double y2)
    {
      return Math.toDegrees(Math.atan2(x1 * y2 - x2 * y1, x1 * x2 + y1 * y2));
    }

    int lastFrameForFps = 0;

    /**
     * Called for every frame of the game. Moves the player, nearby zombies, and determiens win/loss conditions.
     */
    @Override
    public void handle(long time)
    {
      int position = 0;
      int pastSelfCSize = LevelVar.pastSelfCollection.size();
      //System.out.println(frame);
      if (frame == 0) lastFrame = time;
      frame++;
      double percentOfSecond = ((double) time - (double) lastFrame) / 2_000_000_000;
      movePlayerIfRequested(percentOfSecond);

      double playerDirectionVectorX = Math.toDegrees(Math.cos(cameraYRotation));
      double playerDirectionVectorY = Math.toDegrees(Math.sin(cameraYRotation));

      // Animate zombies every four frames to reduce computational load
      if (frame % 4 == 0)
      {
        ArrayList<Integer> positionsToRemove = new ArrayList<>();
        ArrayList<Integer> positionsInLoopToRemove = new ArrayList<>();
        int positionInLoop = 0;
        if (pastSelfCSize > 0)
        {
          for (PastSelf ps : LevelVar.pastSelfCollection)
          {
            PastSelf3D ps3D = ps.pastSelf3D;
            if (frame - ps.deathFrame < ps.deathFrame && (frame - ps.deathFrame) < ps.getXPos().size())
            {
              ps.positionX = ps.getXPos().get(frame - ps.deathFrame);
              ps.positionY = ps.getYPos().get(frame - ps.deathFrame);
              ps3D.setTranslateX(ps.positionX * TILE_WIDTH_AND_HEIGHT);
              ps3D.setTranslateZ(ps.positionY * TILE_WIDTH_AND_HEIGHT);
              ps3D.setRotate(ps.getCameraPos().get(frame - ps.deathFrame) - 180);
            } else
            {
              sceneRoot.getChildren().remove(ps3D);
            }
          }

          int i = LevelVar.pastSelfCollection.get(pastSelfCSize - 1).deathFrame;
          int rFrame = frame - i; //restarted frame, to restart everything from 0
          int rFrameDivided = rFrame / 4; //this is so it only goes through a 4th of the frames
          for (int j = 0; j < LevelVar.bifurcatedCollection.size(); j++)
          {
            Zombie zombie = LevelVar.bifurcatedCollection.get(j);
            Zombie3D z = zombie.zombie3D;
            if ((rFrame >= zombie.getBifurcatedSpawnFrame()) && (rFrame < zombie.getDeathFrame()) && (rFrameDivided < zombie.getXPos().size()))
            {
              if (!zombie.isAddedToScene)
              {
                sceneRoot.getChildren().add(z);
                zombie.isAddedToScene = true;
              }
              zombie.setPositionX(zombie.getXPos().get(zombie.positionForBifurcated));
              zombie.setPositionY(zombie.getYPos().get(zombie.positionForBifurcated));
              z.setTranslateX(zombie.positionX * TILE_WIDTH_AND_HEIGHT);
              z.setTranslateZ(zombie.positionY * TILE_WIDTH_AND_HEIGHT);
              z.setRotate(zombie.getCameraPos().get(rFrameDivided));
              zombie.positionForBifurcated++;
              z.nextFrame();
            } else if (zombie.isAddedToScene)
            {
              sceneRoot.getChildren().remove(z);
              zombie.isAddedToScene = false;
            }
          }
          for (Zombie zombie : LevelVar.interactedWithZombieCollection)
          {
            Zombie3D z = zombie.zombie3D;
            if ((rFrame < zombie.getDeathFrame()) && (rFrameDivided < zombie.getXPos().size()))
            {
              zombie.setPositionX(zombie.getXPos().get(rFrameDivided));
              zombie.setPositionY(zombie.getYPos().get(rFrameDivided));
              z.setTranslateX(zombie.positionX * TILE_WIDTH_AND_HEIGHT);
              z.setTranslateZ(zombie.positionY * TILE_WIDTH_AND_HEIGHT);
              z.setRotate(zombie.getCameraPos().get(rFrameDivided));
              double distanceX = (zombie.positionX - Player.xPosition);
              double distanceY = (zombie.positionY - Player.yPosition);
              double cRotate = zombie.getCameraPos().get(rFrameDivided);
              double totalDistance = Math.abs(distanceX) + Math.abs(distanceY);
              z.nextFrame();
              if (rFrameDivided + 2 < zombie.getXPos().size())
              {
                int playerX = (int) Player.xPosition;
                int playerY = (int) Player.yPosition;
                double zombieNextX = zombie.getXPos().get(rFrameDivided + 1);
                int zombieNextIntX = (int) zombieNextX;
                double zombieNextY = zombie.getYPos().get(rFrameDivided + 1);
                int zombieNextIntY = (int) zombieNextY;
                if (((totalDistance < 1 && InputContainer.hit && frame % 5 == 0) || ((playerX == zombieNextIntX) && (playerY == zombieNextIntY))) && (frame >= zombie.bifrocatedFrame + 60))
                {
                  zombie.bifrocatedFrame = frame;
                  int numOfZ = LevelVar.zombieCollection.size();
                  if (zombie.type == 0)
                  {
                    RandomWalkZombie newZom = new RandomWalkZombie(cRotate, zombie.positionX, zombie.positionY, zombie.curTile, numOfZ + 1);
                    newZom.setBifurcatedSpawnFrame(frame - i);
                    newZom.bifrocatedFrame = frame;
                    sceneRoot.getChildren().add(newZom.zombie3D);
                    LevelVar.zombieCollection.add(newZom);
                  } else if (zombie.type == 1)
                  {
                    LineWalkZombie newZom = new LineWalkZombie(cRotate, zombie.positionX, zombie.positionY, zombie.curTile, numOfZ + 1);
                    newZom.setBifurcatedSpawnFrame(frame - i);
                    newZom.bifrocatedFrame = frame;
                    sceneRoot.getChildren().add(newZom.zombie3D);
                    LevelVar.zombieCollection.add(newZom);
                  } else
                  {
                    MasterZombie newZom = new MasterZombie(cRotate, zombie.positionX, zombie.positionY, zombie.curTile, numOfZ + 1);
                    newZom.setBifurcatedSpawnFrame(frame - i);
                    newZom.bifrocatedFrame = frame;
                    sceneRoot.getChildren().add(newZom.zombie3D);
                    LevelVar.zombieCollection.add(newZom);
                  }
                }
              }
            } else
            {
              if (zombie.diesToPastSelf)
              {
                sceneRoot.getChildren().remove(z);
              } else
              {
                LevelVar.zombieCollection.add(zombie);
                positionsInLoopToRemove.add(positionInLoop);
              }
            }
            positionInLoop++;
          }
        }

        for (Zombie zombie : LevelVar.zombieCollection)
        {
          Zombie3D zombie3D = zombie.zombie3D;
          zombie3D.setTranslateX(zombie.positionX * TILE_WIDTH_AND_HEIGHT);
          zombie3D.setTranslateZ(zombie.positionY * TILE_WIDTH_AND_HEIGHT);

          // Move and rotate the zombie. A* doesn't currently work, so this allows zombies to move towards player. Ugly.
          double distance = Math.sqrt(Math.abs(zombie.positionX - Player.xPosition) * Math.abs(zombie.positionX - Player.xPosition) +
                  Math.abs(zombie.positionY - Player.yPosition) * Math.abs(zombie.positionY - Player.yPosition));
          if (zombie.scentDetection(zombie.getZombieSmell(), LevelVar.house))
          {
            zombie.setSmell(true);
            if (!zombie.interactedWithPS && zombie.type != 2)
            {
              zombie.interactedWithPS = true;
            }
            // Animate 3D zombie and move it to its parent zombie location
            zombie3D.nextFrame();
            double distanceX = (zombie.positionX - Player.xPosition);
            double distanceY = (zombie.positionY - Player.yPosition);
            double totalDistance = Math.abs(distanceX) + Math.abs(distanceY);

            // Player collided with zombie, restart level
            if (totalDistance < 0.5 && frame % 5 == 0)
            {
              if (Player.life > 1 && zombie.type != 2)
              {
                Player.life--;
                Image img = new Image(getClass().getResourceAsStream("/res/life" + Player.life + ".png"));
                lifeView.setImage(img);
              } else
              {
                Player.life = 0;
                Image img = new Image(getClass().getResourceAsStream("/res/life1.png"));
                lifeView.setImage(img);
              }

              if (Player.life == 0)
              {
                int positionForInner = 0;
                System.out.println("Restarting due to death!! ");
                for (Zombie zom : LevelVar.zombieCollection)
                {
                  if (zom.getBifurcatedSpawnFrame() != 0)
                  {
                    toAddToBifurcatedCollection.add(zom);
                    if (zom.getDeathFrame() <= 0)
                    {
                      zom.setDeathFrame(frame);
                    }
                    positionsToRemove.add(positionForInner);
                  } else
                  {
                    if (zom.interactedWithPS)
                    {
                      if (LevelVar.pastZombieCollection.size() > 0)
                      {
                        for (Zombie z : LevelVar.pastZombieCollection)
                        {
                          if (z.zombieID == zom.zombieID)
                          {
                            z.getXPos().addAll(zom.getXPos());
                            z.getYPos().addAll(zom.getYPos());
                            z.getCameraPos().addAll(zom.getCameraPos());
                            z.setDeathFrame(frame);
                            toAddToInteractedCollection.add(z);
                            break;
                          }
                        }
                      } else
                      {
                        toAddToInteractedCollection.add(zom);
                        zom.setDeathFrame(frame);
                      }
                      positionsToRemove.add(positionForInner);
                    }
                  }
                  positionForInner++;
                }
                LevelVar.interactedWithZombieCollection.addAll(toAddToInteractedCollection);
                if (!toAddToBifurcatedCollection.isEmpty())
                {
                  LevelVar.bifurcatedCollection.addAll(toAddToBifurcatedCollection);
                }
                for (PastSelf ps : LevelVar.pastSelfCollection)
                {
                  sceneRoot.getChildren().remove(ps.pastSelf3D);
                }
                Player.life = 5;
                lifeView.setImage(life5);
                deathFrame = frame;
                spawnPastSelf = true;
                level.restartLevel();
                rebuildLevel();
              }
            }

            if (totalDistance < 1)
            {
              verticalCross.setFill(Color.YELLOW);
              horizontalCross.setFill(Color.YELLOW);
            } else if (totalDistance < 0.5)
            {
              verticalCross.setFill(Color.RED);
              horizontalCross.setFill(Color.RED);
            } else
            {
              verticalCross.setFill(Color.GREEN);
              horizontalCross.setFill(Color.GREEN);
            }

            if (totalDistance < 1 && frame % 5 == 0 && InputContainer.hit)
            {
              zombie.setLife(zombie.getLife() - 1);
              zombie.zombie3D.setLife(zombie.getLife() - 1);
              //System.out.println("Life: " + zombie.getLife());
              if (zombie.getLife() == 1)
              {
                zombie.diesToPastSelf = true;
                zombie.setDeathFrame(frame);
                if (LevelVar.pastZombieCollection.size() > 0)
                {
                  for (Zombie z : LevelVar.pastZombieCollection)
                  {
                    if (z.zombieID == zombie.zombieID)
                    {
                      z.getXPos().addAll(zombie.getXPos());
                      z.getYPos().addAll(zombie.getYPos());
                      z.getCameraPos().addAll(zombie.getCameraPos());
                      z.setDeathFrame(frame);
                      toAddToInteractedCollection.add(z);
                      break;
                    }
                  }
                } else
                {
                  toAddToInteractedCollection.add(zombie);
                }
                positionsToRemove.add(position);
                sceneRoot.getChildren().remove(zombie3D);
              }
            }

            //System.out.println(zombie.zombieID + ": " + (zombie.positionX * TILE_WIDTH_AND_HEIGHT) + " - " + (zombie.positionY * TILE_WIDTH_AND_HEIGHT) + " VS." + zombie3D.getTranslateX() + " - " + zombie3D.getTranslateZ() );
            //System.out.println(zombie.zombieID + " : " + zombie.getHeading());
            double desiredPositionX = zombie.positionX - (distanceX / totalDistance * LevelVar.zombieSpeed * percentOfSecond);
            double desiredPositionY = zombie.positionY - (distanceY / totalDistance * LevelVar.zombieSpeed * percentOfSecond);

            if (totalDistance > 0.5)
            {
              if ((LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) ||
                      (LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) ||
                      (LevelVar.house[round(zombie.positionX)][round(desiredPositionY + WALL_COLLISION_OFFSET)] instanceof Wall) ||
                      (LevelVar.house[round(zombie.positionX)][round(desiredPositionY - WALL_COLLISION_OFFSET)] instanceof Wall) ||
                      (LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(desiredPositionY + WALL_COLLISION_OFFSET)] instanceof Wall) ||
                      (LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(desiredPositionY - WALL_COLLISION_OFFSET)] instanceof Wall) ||
                      (LevelVar.house[round(zombie.positionX)][round(zombie.positionY)] instanceof Wall) && zombie.type != 2)
              {
                zombie.makeDecision();
              } else
              {
                if (zombie.type == 2)
                {
                  double desiredMasterPositionX = zombie.positionX - (distanceX / totalDistance * LevelVar.zombieSpeed * LevelVar.masterZombieSpeedModifier * percentOfSecond);
                  double desiredMasterPositionY = zombie.positionY - (distanceY / totalDistance * LevelVar.zombieSpeed * LevelVar.masterZombieSpeedModifier * percentOfSecond);
                  zombie.positionX = desiredMasterPositionX;
                  zombie.positionY = desiredMasterPositionY;
                } else
                {
                  zombie.positionX = desiredPositionX;
                  zombie.positionY = desiredPositionY;
                }
              }
            } else
            {
              zombie.positionX -= 0.01;
              zombie.positionY -= 0.01;
            }

            zombie3D.nextFrame();

            //System.out.println("Player:" + Player.xPosition + "," + Player.yPosition + "   " + zombie.getHeading());
            double zombieVectorX = zombie.positionX - Player.xPosition;
            double zombieVectorY = zombie.positionY - Player.yPosition;

            // Accomodate all four quadrants of the unit circle, rotate to face the user
            if (distanceX < 0)
            {
              if (distanceY < 0)
              {
                double angle = 180 + Math.toDegrees(Math.atan((zombie.positionX - Player.xPosition) / (zombie.positionY - Player.yPosition)));
                zombie3D.setRotate(angle);
              } else
              {
                double angle = 360 + Math.toDegrees(Math.atan((zombie.positionX - Player.xPosition) / (zombie.positionY - Player.yPosition)));
                zombie3D.setRotate(angle);
              }
            } else if (distanceY < 0)
            {
              double angle = 180 + Math.toDegrees(Math.atan((zombie.positionX - Player.xPosition) / (zombie.positionY - Player.yPosition)));
              zombie3D.setRotate(angle);

            } else
            {
              double angle = Math.toDegrees(Math.atan((zombie.positionX - Player.xPosition) / (zombie.positionY - Player.yPosition)));
              zombie3D.setRotate(angle);
            }

            if (Math.random() > 0.98)
            {
              DirectionalPlayer.playSound(AudioFiles.randomZombieSound(), angleBetweenVectors(playerDirectionVectorX, playerDirectionVectorY, zombieVectorX, zombieVectorY), distance);
            }
          } else if (time - zombie.getLastTimeUpdated() >= 2_000_000_000)
          {
            zombie.setLastTimeUpdated(time);
            zombie.makeDecision();
            zombie.setSmell(false);
          }
          zombie.addXPos(zombie.positionX);
          zombie.addYPos(zombie.positionY);
          zombie.addCPos(zombie3D.getRotate());
          position++;
        }


        if (!positionsInLoopToRemove.isEmpty())
        {
          int removedInThisIteration = 0;
          for (int i : positionsInLoopToRemove)
          {
            int allowed = i - removedInThisIteration;
            if (allowed < LevelVar.interactedWithZombieCollection.size() && allowed >= 0)
            {
              LevelVar.interactedWithZombieCollection.remove(i - removedInThisIteration);
              removedInThisIteration++;
            }
          }
          positionsInLoopToRemove.clear();
        }

        if (!positionsToRemove.isEmpty())
        {
          int removedInThisIteration = 0;
          for (int i : positionsToRemove)
          {
            int allowed = i - removedInThisIteration;
            if (allowed < LevelVar.zombieCollection.size() && allowed >= 0)
            {
              LevelVar.zombieCollection.remove(i - removedInThisIteration);
              removedInThisIteration++;
            }
          }
          positionsToRemove.clear();
        }

        if (spawnPastSelf)
        {
          int size = LevelVar.pastSelfCollection.size();
          System.out.println("Adding past self");
          if (size > 0)
          {
            for (PastSelf ps : LevelVar.pastSelfCollection)
            {
              ps.deathFrame = deathFrame;
            }
          }

          PastSelf newPS = new PastSelf(0, 0, 0, deathFrame, size + 1);
          newPS.setCPos(cameraPos);
          newPS.setXPos(xPos);
          newPS.setYPos(yPos);
          LevelVar.pastSelfCollection.add(newPS);

          System.out.println("PS Count:" + LevelVar.pastSelfCollection.size());
          spawnPastSelf = false;
          xPos.clear();
          yPos.clear();
          cameraPos.clear();
        }
        lastFrame = time;
      }

      // Rebuild level if requested. Done here to occur on graphics thread to avoid concurrent modification exceptions.
      if (shouldRebuildLevel)
      {
        if (LevelVar.pastSelfCollection.size() <= 3)
        {
          System.out.println("here");
          for (int i = 0; i < sceneRoot.getChildren().size(); i++)
          {
            if (sceneRoot.getChildren().get(i) instanceof Box || sceneRoot.getChildren().get(i) instanceof Zombie3D)
            {
              sceneRoot.getChildren().remove(i);
              i--;
            }
          }
          setupLevel();
          shouldRebuildLevel = false;
        } else
        {
          addEndScreen();
        }
      }
    }
  }

  /* * Main kept for legacy applications.
   *
   * @param args Unused command-line arguments
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
