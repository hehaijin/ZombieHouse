package zombiehouse.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * @author Joshua Donckels
 *
 * This class represents the players first person hands with a knife
 *
 * The models of the first person hands were found at
 * https://sketchfab.com/models/547a45535f0c4fe787948f7a7a6a88db
 * by DavidFischer
 *
 * The knife model is found at
 * http://tf3dm.com/3d-model/combat-knife-17573.html
 * by gamingstudio
 *
 * While the animations of this class were also done by me.
 */
public class Player3D extends Group
{
  private static final int MAXIMUM_FRAME = 40;
  public int currentFrame = 1;

  public Player3D()
  {
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
    setScaleX(110);
    setScaleY(110);
    setScaleZ(110);
    setTranslateY(-200);

    setRotationAxis(Rotate.Y_AXIS);
    
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
