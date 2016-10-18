package zombiehouse.level.house;

import javafx.scene.paint.Color;
import zombiehouse.common.LevelVar;
import zombiehouse.common.Player;
import java.util.ArrayList;

/**
 * @author Rob
 *         <p>
 *         Tile is the super-class for all the 'space' elements of a level (Floor, Wall, Exit)
 *         Contains all the common elements of Floor, Wall, Exit
 *         In theory Tile should not be initialized
 */
public class Tile
{
  public boolean visited = false; // only concerned if visited was used in generation
  public int xCor, yCor;
  public int zone;
  public ArrayList<Tile> neighbors = new ArrayList<>();
  public int cost;
  public boolean hasBeenSeen = false;
  
  /**
   * Simple constructor
   *
   * @param xCor the x-coordinate (index) on LevelVar.house
   * @param yCor the y-coordinate (index) on LevelVar.house
   * @param zone the zone ID for this tile
   */
  public Tile(int xCor, int yCor, int zone)
  {
    this.xCor = xCor;
    this.yCor = yCor;
    this.zone = zone;
  }
  
  /**
   * Set the neighbors of the current tile that aren't walls
   *
   * @param house
   */
  public void setNeighbors(Tile[][] house)
  {
    if (xCor + 1 < house.length && house[xCor + 1][yCor] instanceof Floor && !(house[xCor + 1][yCor] instanceof Wall))
      neighbors.add(house[xCor + 1][yCor]);
    if (yCor + 1 < house[0].length && house[xCor][yCor + 1] instanceof Floor && !(house[xCor][yCor + 1] instanceof
            Wall))
      neighbors.add(house[xCor][yCor + 1]);
    if (yCor - 1 >= 0 && house[xCor][yCor - 1] instanceof Floor && !(house[xCor][yCor - 1] instanceof Wall))
      neighbors.add(house[xCor][yCor - 1]);
    if (xCor - 1 >= 0 && house[xCor - 1][yCor] instanceof Floor && !(house[xCor - 1][yCor] instanceof Wall))
      neighbors.add(house[xCor - 1][yCor]);
  }
  
  /**
   * Add neighbor tile
   * @param nextTile neighbortile
   */
  public void addNeighbor(Tile nextTile)
  {
    neighbors.add(nextTile);
  }
  
  /**
   * Gets the neighbor tiles
   * @return neighbors
   */
  public ArrayList<Tile> getNeighbors()
  {
    return neighbors;
  }
  
  /**
   * Sets the visited flag
   * @param value
   */
  public void setVisited(boolean value)
  {
    visited = value;
  }
  
  /**
   * Sets the cost for A*
   * @param cost
   */
  public void setCost(int cost)
  {
    this.cost = cost;
  }
  
  public void isUsed()
  {
  }
  
  /**
   * Is used when debugging and printing the house
   * @return
   */
  public char getChar()
  {
    return 'f';
  }
  
  /**
   * Used in HouseAniTest
   * @return color
   */
  public Color getColor()
  {
    if (LevelVar.WITH_SIGHT && !hasBeenSeen)
    {
      return Color.BLACK;
    }
    return Color.WHITE;
  }
  
  /**
   * Debugging test
   * @return
   */
  public boolean isEmpty()
  {
    return false;
  }
  
  /**
   * If the tile can be seen
   */
  public void isSeen()
  {
    if (hasBeenSeen)
    {
      return;
    }
    double distFromPlayer = Math.abs(Player.xPosition - xCor) + Math.abs(Player.yPosition - yCor);
    if (distFromPlayer <= Player.playerSightRange)
    {
      hasBeenSeen = true;
    }
  }
}