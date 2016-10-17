package zombiehouse.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * Created by joshu on 10/17/2016.
 */
public class Player3D extends Group
{
  private static final int MAXIMUM_FRAME = 40;
  private int currentFrame = 1;

  public Player3D()
  {
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    for(int i = 1; i <= 40; i++)
    {
      try
      {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/res/knifeandHand" + i + ".fxml"));
        Group pastSelfModel = fxmlLoader.load();
        pastSelfModel.setVisible(false);
        getChildren().add(pastSelfModel);
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    setScaleX(80);
    setScaleY(80);
    setScaleZ(80);
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
      //cycle through 1-40.
      currentFrame = 1;
    }
    getChildren().get(currentFrame).setVisible(true);


  }
}
