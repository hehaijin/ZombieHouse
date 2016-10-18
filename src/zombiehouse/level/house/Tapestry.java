package zombiehouse.level.house;

import javafx.scene.paint.Color;

/**
 * A class for tapestry.
 *
 * @author Haijin He
 */
public class Tapestry extends Wall
{
  public Tapestry(int xCor, int yCor, int zone)
  {
    super(xCor, yCor, zone);
  }
  
  /**
   * For printHouse method in Level class
   *
   * @return character to represent tapestry
   */
  public char getChar()
  {
    return '^';
  }
  
  public Color getColor()
  {
    return null;
  }
}