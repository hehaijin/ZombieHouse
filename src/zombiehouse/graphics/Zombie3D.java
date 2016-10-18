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
 * I changed the models to be from the following:
 *
 * Master zombie from:
 * http://opengameart.org/content/thin-zombie-awake-zombie-asset
 * by dogchicken from Rosswet Mobile
 *
 * Other zombie model from
 * tigerTowel on BlendSwap (For the random i just made the texture have a white shirt)
 *
 * Each Zombie3D is tied to a traditional Zombie object.
 *
 * @author Maxwell Sanchez (past person) & Joshua & Haijin
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
  Box lifebox[]=new Box[5];

  {
    for(int i=0;i<5;i++)
    {
      lifebox[i]=new Box(i*0.2+0.2,0.1,0.1);
      lifebox[i].setMaterial(redMaterial);
      lifebox[i].setDepthTest(DepthTest.ENABLE);
      lifebox[i].setTranslateY(-2.2);
    }
  }

  /**
   * Create a Zombie3D by loading in all of its corresponding frames depending on the type
   * of zombie.
   */
  public Zombie3D(int type)
  {
    //add life display.
    getChildren().add(lifebox[4]);
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
    } else if(type == 1)
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
    else {
      for (int i = 0; i <= MAXIMUM_FRAME; i++)
      {
        try
        {
          // Load in zombie meshes
          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/res/ZombieR" + i + ".fxml"));
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
    else getChildren().set(0,lifebox[life-1]);


  }

}
