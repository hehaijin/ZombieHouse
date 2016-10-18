package zombiehouse.level;

import zombiehouse.graphics.PastSelf3D;
import java.util.ArrayList;

/**
 * Past Self class that stores information on the history of the player and
 * replays it when the player dies
 *
 * @author Anton Kuzmin
 */
public class PastSelf
{
  /**
   * the direction the past self will head in degrees
   */
  public double heading;
  
  /**
   * ID for the past self since there can be multiple
   */
  public int PastSelfID;
  
  /**
   * the past self's current X coordinate in the ZombieHouse
   */
  public double positionX;
  
  /**
   * the past self's current Y coordinate in the ZombieHouse
   */
  public double positionY;
  
  /**
   * List that stores the x-coordinate history of the past self
   */
  private ArrayList<Double> xPos = new ArrayList<>();
  
  /**
   * List that stores the y-coordinate history of the past self
   */
  private ArrayList<Double> yPos = new ArrayList<>();
  
  /**
   * List that stores the camera position history of the past self
   */
  private ArrayList<Double> cPos = new ArrayList<>();
  
  /**
   * The PastSelf3D that represents this past self in a 3D graphical world
   */
  public PastSelf3D pastSelf3D;
  
  /**
   * frame at which the player died
   */
  public Integer deathFrame;
  
  /**
   * Constructs a past self object with the specified heading, X coordinate position,
   * Y coordinate position, and ID
   */
  public PastSelf(double heading, double positionX, double positionY, Integer deathFrame, Integer psID) {
    this.heading = heading;
    this.positionX = positionX;
    this.positionY = positionY;
    this.deathFrame = deathFrame;
    this.PastSelfID = psID;
    pastSelf3D = new PastSelf3D();
  }
  
  /**
   * Sets the x-position list
   */
  public void setXPos(ArrayList<Double> xPositions)
  {
    xPos.addAll(xPositions);
  }
  
  /**
   * Sets the y-position list
   */
  public void setYPos(ArrayList<Double> yPositions)
  {
    yPos.addAll(yPositions);
  }
  
  /**
   * Sets the camera position list
   */
  public void setCPos(ArrayList<Double> cPositions)
  {
    cPos.addAll(cPositions);
  }
  
  /**
   * Returns the camera position list
   *
   * @return list
   */
  public ArrayList<Double> getCameraPos()
  {
    return cPos;
  }
  
  /**
   * Returns the x-position list
   *
   * @return list
   */
  public ArrayList<Double> getXPos()
  {
    return xPos;
  }
  
  /**
   * Returns the y-position list
   *
   * @return list
   */
  public ArrayList<Double> getYPos()
  {
    return yPos;
  }
}