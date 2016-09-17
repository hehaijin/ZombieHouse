package zombiehouse.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 9/15/2016.
 */
public class PastSelf
{
  private List<Double> xPos;
  private List<Double> yPos;
  private List<Double> cameraPos;
  private int deathFrame;
  
  public PastSelf(List<Double> xPos, List<Double> yPos, List<Double> cameraPos, int deathFrame)
  {
    this.xPos = xPos;
    this.yPos = yPos;
    this.cameraPos = cameraPos;
    this.deathFrame = deathFrame;
  }
  
  public double getXPos(Integer frame)
  {
    return xPos.get(frame);
  }
  
  public double getYPos(Integer frame)
  {
    return yPos.get(frame);
  }
  
  public double getCameraPos(Integer frame)
  {
    return cameraPos.get(frame);
  }
  
  public int getDeathFrame(Integer deathFrame)
  {
    return deathFrame;
  }
}