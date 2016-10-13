package zombiehouse.graphics;

import java.util.Random;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * Zombie3D holds zombie animation frames that I animated in Blender.
 * Zombie model originally from http://www.blendswap.com/blends/view/4807.
 * Texturing and movement by me.
 *
 * Each Zombie3D is tied to a traditional Zombie object.
 *
 * @author Maxwell Sanchez
 *
 */
public class Zombie3D extends Group
{

  private static final int MAXIMUM_FRAME = 39;
  private int currentFrame = 0;
  private static Random random = new Random();

  /**
   * Create a Zombie3D by loading in 8 random, contiguous frames,
   * setting the mesh group's scale and Y translation, and preparing
   * the model to rotate on the Y axis.
   */
  public Zombie3D()
  {
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    int randomStart = random.nextInt(MAXIMUM_FRAME);
    this.currentFrame = randomStart;
    for (int i = 0; i <= 39; i++)
    {
      try
      {
        // Load in zombie meshes
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/res/Zombie" + i + ".fxml"));
        Group zombieModel = fxmlLoader.load();
        zombieModel.setVisible(false);
        getChildren().add(zombieModel);
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    setScaleX(200);
    setScaleY(200);
    setScaleZ(200);
    setTranslateY(-235);

    setRotationAxis(Rotate.Y_AXIS);

    // Make sure zombies are on different frames to avoid "synchronized" movement
    getChildren().get(random.nextInt(MAXIMUM_FRAME)).setVisible(true);
  }

  public void setType(String zombieType)
  {
    if (zombieType.equalsIgnoreCase("linewalk"))
    {

    }
  }

  /**
   * Change the current animation frame to the next frame.
   */
  public void nextFrame()
  {
    getChildren().get(currentFrame).setVisible(false);
    currentFrame += 1;
    if (currentFrame >= MAXIMUM_FRAME)
    {
      currentFrame = 0;
    }
    getChildren().get(currentFrame).setVisible(true);
  }
}
