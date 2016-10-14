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
public class PastSelf3D extends Group
{

  private static final int MAXIMUM_FRAME = 9;
  private static Random random = new Random();

  /**
   * Create a Zombie3D by loading in 8 random, contiguous frames,
   * setting the mesh group's scale and Y translation, and preparing
   * the model to rotate on the Y axis.
   */
  public PastSelf3D()
  {
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    try
    {
      // Load in zombie meshes
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("/res/pastSelfM.fxml"));
      Group pastSelfModel = fxmlLoader.load();
      pastSelfModel.setVisible(false);
      getChildren().add(pastSelfModel);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    setScaleX(220);
    setScaleY(220);
    setScaleZ(220);
    setTranslateY(-260);

    setRotationAxis(Rotate.Y_AXIS);

    // Make sure zombies are on different frames to avoid "synchronized" movement
    getChildren().get(random.nextInt(MAXIMUM_FRAME)).setVisible(true);
  }
}
