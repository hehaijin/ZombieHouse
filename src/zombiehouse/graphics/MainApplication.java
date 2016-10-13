package zombiehouse.graphics;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
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
import zombiehouse.level.house.Exit;
import zombiehouse.level.house.Level;
import zombiehouse.level.house.Tile;
import zombiehouse.level.house.Wall;
import zombiehouse.level.house.BookCase;
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
  private static final double WALL_COLLISION_OFFSET = 0.25;

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
  boolean spawnPastSelf = false;

  private int deathFrame = 0;

  FXMLLoader fxmlloader=new FXMLLoader();
  {
    fxmlloader.setLocation(getClass().getResource("/res/knife1.fxml"));
  }

  MeshView knife1= null;
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

    // Create the camera, set it to view far enough for any reasonably-sized map
    camera = new PerspectiveCamera(true);
    camera.setNearClip(0.1);
    camera.setFarClip(20000.0);
    camera.setFieldOfView(62.5);

    // Rotate camera on the y-axis for swivel in response to mouse
    camera.setVerticalFieldOfView(true);
    camera.setTranslateZ(cameraZDisplacement);
    camera.setTranslateY(cameraYDisplacement);
    camera.setRotationAxis(Rotate.Y_AXIS);
    camera.setDepthTest(DepthTest.ENABLE);
    scene.setCamera(camera);


    knife1=fxmlloader.load();
    knife1.setTranslateZ(cameraZDisplacement);
    knife1.setTranslateY(cameraYDisplacement);
    knife1.setRotationAxis(Rotate.Y_AXIS);
    sceneRoot.getChildren().add(knife1);
    knife1.setTranslateY(-345);
    knife1.getTransforms().add(new Rotate(100,0,0,0,Rotate.X_AXIS));

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
        tt.setNode(knife1);
        tt.setByX(50*Math.sin(cameraYRotation / 180 * 3.1415));
        tt.setByZ(50*Math.cos(cameraYRotation / 180 * 3.1415));
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
    floorMaterial1.setDiffuseMap(new Image(getClass().getResource("/res/dirt.png").toExternalForm()));

    floorMaterial2.setDiffuseColor(Color.WHITE);
    floorMaterial2.setSpecularColor(Color.WHITE.darker());
    floorMaterial2.setSpecularPower(128);
    floorMaterial2.setDiffuseMap(new Image(getClass().getResource("/res/floor2.png").toExternalForm()));

    floorMaterial3.setDiffuseColor(Color.WHITE);
    floorMaterial3.setSpecularColor(Color.WHITE.darker());
    floorMaterial3.setSpecularPower(128);
    floorMaterial3.setDiffuseMap(new Image(getClass().getResource("/res/floor3.png").toExternalForm()));

    floorMaterial4.setDiffuseColor(Color.WHITE);
    floorMaterial4.setSpecularColor(Color.WHITE.darker());
    floorMaterial4.setSpecularPower(128);
    floorMaterial4.setDiffuseMap(new Image(getClass().getResource("/res/floor4.png").toExternalForm()));

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

    exitMaterial.setDiffuseColor(Color.WHITE);
    exitMaterial.setSpecularColor(Color.WHITE);

    setupLevel();
    
    new GameLoop().start();
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
        Box floor = new Box(TILE_WIDTH_AND_HEIGHT, 10, TILE_WIDTH_AND_HEIGHT);
        if (house[x][z].zone == 0)
        {
          floor.setMaterial(floorMaterial1);
        }
        if (house[x][z].zone == 1)
        {
          floor.setMaterial(floorMaterial2);
        }
        if (house[x][z].zone == 2)
        {
          floor.setMaterial(floorMaterial3);
        } else
        {
          floor.setMaterial(floorMaterial4);
        }

        floor.setTranslateY(FLOOR_Y_DISPLACEMENT);
        floor.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
        floor.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
        sceneRoot.getChildren().add(floor);

        Box ceiling = new Box(TILE_WIDTH_AND_HEIGHT, 10, TILE_WIDTH_AND_HEIGHT);
        ceiling.setMaterial(ceilingMaterial);
        ceiling.setTranslateY(CEILING_Y_DISPLACEMENT);
        ceiling.setTranslateX(x * TILE_WIDTH_AND_HEIGHT);
        ceiling.setTranslateZ(z * TILE_WIDTH_AND_HEIGHT);
        sceneRoot.getChildren().add(ceiling);

        // If wall, place a ground-to-ceiling wall box
        if (house[x][z] instanceof Wall && !(house[x][z] instanceof BookCase))
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
            //System.out.println("removed");
          }
        }
        if (!sceneRoot.getChildren().contains(zombie.zombie3D))
        {
          sceneRoot.getChildren().add(zombie.zombie3D);
        }
      }
    }

    // Add all of the 3D zombie objects
    for (Zombie zombie : LevelVar.zombieCollection)
    {
      sceneRoot.getChildren().add(zombie.zombie3D);
    }

    //System.out.println("Number of zombies:" + LevelVar.zombieCollection.size());
    //System.out.println("Number of interactedWithPSZombies:" + LevelVar.interactedWithZombieCollection.size());

    for (PastSelf ps : LevelVar.pastSelfCollection)
    {
      sceneRoot.getChildren().add(ps.pastSelf3D);
    }

    // Create a zombie update timer
    ZTimer zMoves = new ZTimer();
    zMoves.zUpdateTimer.schedule(zMoves.myUpdate, Zombie.getDecisionRate(), Zombie.getDecisionRate());

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
    // The last-used user walking clip
    int lastClip = 1;
    long lastFrame = System.nanoTime();

    /**
     * Moves the player, if possible (no wall collisions) in the direction(s) requested by the user
     * with keyboard input, given the current angle determined by previous mouse input.
     */
    public void movePlayerIfRequested(double percentOfSecond)
    {
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
      
      if(Player.stamina >= 4.0)
      {
        staminaBar.setFill(Color.BLUE);
      }
      else if(Player.stamina >= 3.0)
      {
        staminaBar.setFill(Color.GREEN);
      }
      else if(Player.stamina >= 2.0)
      {
        staminaBar.setFill(Color.YELLOW);
      }
      else if(Player.stamina >= 1.0)
      {
        staminaBar.setFill(Color.ORANGE);
      }
      else if(Player.stamina >= 0.0)
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

      // Check for wall collisions
      if (!(LevelVar.house[round(desiredPlayerXPosition + WALL_COLLISION_OFFSET)][round(Player.yPosition)] instanceof Wall) &&
              !(LevelVar.house[round(desiredPlayerXPosition - WALL_COLLISION_OFFSET)][round(Player.yPosition)] instanceof Wall))
      {
        Player.xPosition += desiredXDisplacement * (percentOfSecond * Player.playerSpeed);
      }
      if (!(LevelVar.house[round(Player.xPosition)][round(desiredPlayerYPosition + WALL_COLLISION_OFFSET)] instanceof Wall) &&
              !(LevelVar.house[round(Player.xPosition)][round(desiredPlayerYPosition - WALL_COLLISION_OFFSET)] instanceof Wall))
      {
        Player.yPosition += desiredZDisplacement * (percentOfSecond * Player.playerSpeed);
      }

      // Calculate camera displacement
      cameraXDisplacement = Player.xPosition * TILE_WIDTH_AND_HEIGHT;
      cameraZDisplacement = Player.yPosition * TILE_WIDTH_AND_HEIGHT;

      // Move the point light with the light
      pl.setTranslateX(cameraXDisplacement);
      pl.setTranslateZ(cameraZDisplacement);

      // Calculate camera rotation
      cameraYRotation += PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;

      // Displace camera
      camera.setTranslateX(cameraXDisplacement);
      camera.setTranslateZ(cameraZDisplacement);

      // Rotate the camera
      camera.setRotate(cameraYRotation);
      

      if(tt.getStatus()!= Animation.Status.RUNNING) {

        knife1.setTranslateX(cameraXDisplacement + 100 * Math.sin(cameraYRotation / 180 * 3.1415));
        knife1.setTranslateZ(cameraZDisplacement + 100 * Math.cos(cameraYRotation / 180 * 3.1415));
        knife1.setRotate(cameraYRotation);
      }

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

    long lastTime = 0;
    int testframe = 0;
    int lasttestFrame = 0;
    /**
     * Called for every frame of the game. Moves the player, nearby zombies, and determiens win/loss conditions.
     */
    @Override
    public void handle(long time)
    {
      if(time - lastTime > 1_000_000_000) {
        lastTime = time;
        System.out.println(testframe - lasttestFrame);
        lasttestFrame = testframe;
      }
      testframe++;
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
              System.out.println("PS:" + ps.pastSelfID + " : " + ps.deathFrame);
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
          for (Zombie zombie : LevelVar.interactedWithZombieCollection)
          {
            Zombie3D z = zombie.zombie3D;
            int rFrame = frame - i; //restarted frame, to restart everything from 0
            int rFrameDivided = rFrame/4; //this is so it only goe through to the 4th amount of frames
              if ((rFrame < zombie.getDeathFrame()) && (rFrameDivided < zombie.getXPos().size()) && (rFrameDivided < zombie.getCameraPos().size()))
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
                if (totalDistance < 1 && frame % 5 == 0 && InputContainer.hit)
                {
                  zombie.bifrocatedFrame = frame;
                  int numOfZ = LevelVar.zombieCollection.size();
                  LineWalkZombie newZom = new LineWalkZombie(cRotate, zombie.positionX, zombie.positionY, zombie.curTile, numOfZ + 1);
                  sceneRoot.getChildren().add(newZom.zombie3D);
                  LevelVar.zombieCollection.add(newZom);
                }
                if (zombie.bifrocatedFrame != 0 && (frame - i) == zombie.bifrocatedFrame)
                {
                  int numOfZ = LevelVar.zombieCollection.size();
                  LineWalkZombie newZom = new LineWalkZombie(cRotate, zombie.positionX, zombie.positionY, zombie.curTile, numOfZ + 1);
                  sceneRoot.getChildren().add(newZom.zombie3D);
                  LevelVar.zombieCollection.add(newZom);
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
          if (zombie.getSmell())
          {
            if (!zombie.interactedWithPS)
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
              if (Player.life > 1)
              {
                Player.life--;
                Image img = new Image(getClass().getResourceAsStream("/res/life" + Player.life + ".png"));
                lifeView.setImage(img);
              } else
              {
                int positionForInner = 0;
                System.out.println("Restarting due to death!! ");
                for (Zombie zom : LevelVar.zombieCollection)
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
                  positionForInner++;
                }
                for (Zombie z : toAddToInteractedCollection)
                {
                  LevelVar.interactedWithZombieCollection.add(z);
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
            
            if(totalDistance < 1)
            {
              verticalCross.setFill(Color.YELLOW);
              horizontalCross.setFill(Color.YELLOW);
            }
            else if(totalDistance < 0.5)
            {
              verticalCross.setFill(Color.RED);
              horizontalCross.setFill(Color.RED);
            }
            else
            {
              verticalCross.setFill(Color.GREEN);
              horizontalCross.setFill(Color.GREEN);
            }

            if (totalDistance < 1 && frame % 5 == 0 && InputContainer.hit)
            {
              zombie.setLife(zombie.getLife() - 1);
              System.out.println("Life: " + zombie.getLife());
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
            
            
            double desiredPositionX = zombie.positionX - (distanceX / totalDistance * LevelVar.zombieSpeed * percentOfSecond);
            double desiredPositionY = zombie.positionY - (distanceY / totalDistance * LevelVar.zombieSpeed * percentOfSecond);
            
            /*
            // Check for wall collisions
            if (!(LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) &&
                    !(LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall))
            {
              zombie.positionX = desiredPositionX;
            }
            if (!(LevelVar.house[round(zombie.positionX)][round(desiredPositionY + WALL_COLLISION_OFFSET)] instanceof Wall) &&
                    !(LevelVar.house[round(zombie.positionX)][round(desiredPositionY - WALL_COLLISION_OFFSET)] instanceof Wall))
            {
              zombie.positionY = desiredPositionY;
            }*/
            if((LevelVar.house[round(desiredPositionX + WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) ||
                    (LevelVar.house[round(desiredPositionX - WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) ||
                    (LevelVar.house[round(zombie.positionX)][round(desiredPositionY + WALL_COLLISION_OFFSET)] instanceof Wall) ||
                    (LevelVar.house[round(zombie.positionX)][round(desiredPositionY - WALL_COLLISION_OFFSET)] instanceof Wall))
            {
              zombie.makeDecision();
              //zombie.move();
            }
            else
            {
              zombie.positionX = desiredPositionX;
              zombie.positionY = desiredPositionY;
            }
            zombie3D.nextFrame();

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
            LevelVar.interactedWithZombieCollection.remove(i - removedInThisIteration);
            removedInThisIteration++;
          }
          positionsInLoopToRemove.clear();
        }

        if (!positionsToRemove.isEmpty())
        {
          int removedInThisIteration = 0;
          for (int i : positionsToRemove)
          {
            LevelVar.zombieCollection.remove(i - removedInThisIteration);
            removedInThisIteration++;
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
            PastSelf newPS = new PastSelf(0, 0, 0, deathFrame, size + 1);
            newPS.setCPos(cameraPos);
            newPS.setXPos(xPos);
            newPS.setYPos(yPos);
            LevelVar.pastSelfCollection.add(newPS);
          } else
          {
            PastSelf newPS = new PastSelf(0, 0, 0, deathFrame, size + 1);
            newPS.setCPos(cameraPos);
            newPS.setXPos(xPos);
            newPS.setYPos(yPos);
            LevelVar.pastSelfCollection.add(newPS);
          }
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
        for (int i = 0; i < sceneRoot.getChildren().size(); i++)
        {
          if (sceneRoot.getChildren().get(i) instanceof Box || sceneRoot.getChildren().get(i) instanceof Zombie3D)
          {
            sceneRoot.getChildren().remove(sceneRoot.getChildren().get(i));
            i--;
          }
        }
        setupLevel();
        shouldRebuildLevel = false;
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
