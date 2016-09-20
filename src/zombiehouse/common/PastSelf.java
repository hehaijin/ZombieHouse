package zombiehouse.common;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import zombiehouse.graphics.Zombie3D;

import java.util.List;


public class PastSelf
{
  private List<Double> xPosition;
  private List<Double> yPosition;
  private List<Double> cameraPosition;
  private int deathFrame;
  public Sphere s;
  public Zombie3D zombie3D;

  public PastSelf(List<Double> xPos, List<Double> yPos, int deathFrame)
  {
    PhongMaterial white = new PhongMaterial();
    this.xPosition = xPos;
    this.yPosition = yPos;
    //this.cameraPosition = cameraPos;
    this.deathFrame = deathFrame;
    this.s = new Sphere(50);
    white.setDiffuseColor(Color.WHITE);
    s.setMaterial(white);
    zombie3D = new Zombie3D();
    s.setTranslateY(-235);
  }
  
  public double getXPos(Integer frame)
  {
    return xPosition.get(frame);
  }
  
  public double getYPos(Integer frame)
  {
    return yPosition.get(frame);
  }
  
  public double getCameraPos(Integer frame)
  {
    return cameraPosition.get(frame);
  }

  public int getDeathFrame() { return deathFrame; }
}
