package zombiehouse.graphics;

import java.util.Random;

import javafx.fxml.FXMLLoader;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
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


  //
  private PhongMaterial redMaterial = new PhongMaterial();
  {
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.DARKRED);
  }
  //the boxes are for zombie life display.
  Box lifebox5=new Box(1,0.1,0.1);

  {
    lifebox5.setMaterial(redMaterial);
    lifebox5.setDepthTest(DepthTest.ENABLE);
    lifebox5.setTranslateY(-2.2);
  }
  Box lifebox4=new Box(0.8,0.1,0.1);

  {
    lifebox4.setMaterial(redMaterial);
    lifebox4.setDepthTest(DepthTest.ENABLE);
    lifebox4.setTranslateY(-2.2);
  }
  Box lifebox3=new Box(0.6,0.1,0.1);

  {
    lifebox3.setMaterial(redMaterial);
    lifebox3.setDepthTest(DepthTest.ENABLE);
    lifebox3.setTranslateY(-2.2);
  }

  Box lifebox2=new Box(0.4,0.1,0.1);

  {
    lifebox2.setMaterial(redMaterial);
    lifebox2.setDepthTest(DepthTest.ENABLE);
    lifebox2.setTranslateY(-2.2);
  }

  Box lifebox1=new Box(0.2,0.1,0.1);

  {
    lifebox1.setMaterial(redMaterial);
    lifebox1.setDepthTest(DepthTest.ENABLE);
    lifebox1.setTranslateY(-2.2);
  }
  /**
   * Create a Zombie3D by loading in 8 random, contiguous frames,
   * setting the mesh group's scale and Y translation, and preparing
   * the model to rotate on the Y axis.
   */
  public Zombie3D(int type)
  {
    //add life display.
    getChildren().add(lifebox5);
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    //add one to avoid 0.
    int randomStart = random.nextInt(MAXIMUM_FRAME)+1;
    this.currentFrame = randomStart;
    if(type == 2)
    {
      for (int i = 0; i <= MAXIMUM_FRAME; i++)
      {
        try
        {
          // Load in zombie meshes
          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/res/masterZombie" + i + ".fxml"));
          Group zombieModel = fxmlLoader.load();
          zombieModel.setVisible(false);
          getChildren().add(zombieModel);
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      setScaleX(60);
      setScaleY(60);
      setScaleZ(60);
      setTranslateY(-260);
    } else
    {
      for (int i = 0; i <= MAXIMUM_FRAME; i++)
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
      setScaleX(220);
      setScaleY(220);
      setScaleZ(220);
      setTranslateY(-260);
    }


    setRotationAxis(Rotate.Y_AXIS);

    // Make sure zombies are on different frames to avoid "synchronized" movement
    getChildren().get(currentFrame).setVisible(true);

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
      //cycle through 1-39, but not 0. 0 is for the life box.
      currentFrame = 1;
    }
    getChildren().get(currentFrame).setVisible(true);


  }

  /**
   * set the display of zombie life
   * @param life  zombie life, from 1-5, int value.
   */
  public void setLife(int life)
  {
    if(life>5 || life <1)
      System.out.println("wrong input for zombie life");
    switch(life)
    {
      case 1: getChildren().set(0,lifebox1);
        break;
      case 2: getChildren().set(0,lifebox2);
        break;
      case 3: getChildren().set(0,lifebox3);
        break;
      case 4: getChildren().set(0,lifebox4);
        break;
      case 5: getChildren().set(0,lifebox5);
        break;
      default: break;


    }


  }

}
