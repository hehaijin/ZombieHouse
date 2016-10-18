package zombiehouse.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * Created by joshu on 10/17/2016.
 */
public class Attack3D extends Group
{
  private static final int MAXIMUM_FRAME = 30;
  public int currentFrame = 1;

  public Attack3D()
  {
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    for(int i = 1; i <= 30; i++)
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
