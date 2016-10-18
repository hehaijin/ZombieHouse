package zombiehouse.graphics;

import java.util.Random;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * This past self model was found at
 * http://forum.avora.org/viewtopic.php?f=33&t=16
 * by Clint Bellanger
 *
 * Each PastSelf3D is tied to a pastSelf.
 *
 * @author Joshua Donckels
 *
 */
public class PastSelf3D extends Group
{

  /**
   * Creates a pastSelf with no animations b/c it represent the ghost of your past self.
   */
  public PastSelf3D()
  {
    try
    {
      // Load in past self meshes
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

    getChildren().get(0).setVisible(true);
  }
}
