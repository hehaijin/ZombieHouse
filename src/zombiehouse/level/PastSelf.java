/**
 *Zombie class written for ZombieHouse CS351 project that contains
 *the pathfinding and behavior algorithms for the Zombie objects in 
 *the game.
 *@Author Stephen Sagartz
 *@version 1.0
 *@since 2016-03-05 
 */

package zombiehouse.level;

import zombiehouse.graphics.PastSelf3D;

import java.util.ArrayList;
import java.util.List;

/**
 *Zombie class that contains methods inherited by the sub-classes of Zombie
 *as well as all Zombie variables. 
 */
public class PastSelf
{

  /**
   * the direction the Zombie will head in degrees
   */
  public double heading;

  public int PastSelfID;
  /**
   * the Zombie's current X coordinate in the ZombieHouse
   */
  public double positionX;
  /**
   * the Zombie's current Y coordinate in the ZombieHouse
   */
  public double positionY;

  public int pastSelfID;

  private ArrayList<Double> xPos = new ArrayList<>();

  private ArrayList<Double> yPos = new ArrayList<>();

  private ArrayList<Double> cPos = new ArrayList<>();

  /**
   * The Zombie3D that represents this zombie in a 3D graphical world
   */
  public PastSelf3D pastSelf3D;

  public Integer deathFrame;

  /**
   * Constructs a Zombie object with the specified heading, X coordinate position,
   * Y coordinate position, and the Tile it is in, preferably as given by its
   * X and Y coordinates
   */
  public PastSelf(double heading, double positionX, double positionY, Integer deathFrame, Integer psID) {
    this.heading = heading;
    this.positionX = positionX;
    this.positionY = positionY;
    this.deathFrame = deathFrame;
    this.PastSelfID = psID;
    pastSelf3D = new PastSelf3D();
  }

  public void setXPos(ArrayList<Double> xPositions)
  {
    xPos.addAll(xPositions);
  }

  public void setYPos(ArrayList<Double> yPositions) { yPos.addAll(yPositions); }

  public void setCPos(ArrayList<Double> cPositions) { cPos.addAll(cPositions); }

  public ArrayList<Double> getCameraPos() { return cPos; }

  public ArrayList<Double> getXPos()
  {
    return xPos;
  }

  public ArrayList<Double> getYPos()
  {
    return yPos;
  }
}