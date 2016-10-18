package zombiehouse.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * @author Joshua Donckels.
 *         This is used to be the animations that represent the player attacking or stabbing a zombie.
 *         The models of the first person hands were found at
 *         https://sketchfab.com/models/547a45535f0c4fe787948f7a7a6a88db
 *         by DavidFischer
 *         <p>
 *         The knife model is found at
 *         http://tf3dm.com/3d-model/combat-knife-17573.html
 *         by gamingstudio
 *         <p>
 *         but all of the animations here were created by me
 */
public class Attack3D extends Group
{
  private static final int MAXIMUM_FRAME = 30;
  public int currentFrame = 1;
  
  public Attack3D()
  {
    for (int i = 1; i <= 30; i++)
    {
      try
      {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/res/attackAnimation" + i + ".fxml"));
        Group pastSelfModel = fxmlLoader.load();
        pastSelfModel.setVisible(false);
        getChildren().add(pastSelfModel);
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    setScaleX(110);
    setScaleY(110);
    setScaleZ(110);
    setTranslateY(-200);
    
    setRotationAxis(Rotate.Y_AXIS);
    
    // Make sure zombies are on different frames to avoid "synchronized" movement
    getChildren().get(0).setVisible(true);
  }
  
  public void nextFrame()
  {
    getChildren().get(currentFrame).setVisible(false);
    currentFrame += 1;
    if (currentFrame >= MAXIMUM_FRAME)
    {
      //cycle through 1-30.
      currentFrame = 1;
    }
    getChildren().get(currentFrame).setVisible(true);
  }
}
