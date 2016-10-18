package zombiehouse.level.house;

import javafx.scene.paint.Color;

/**
 * A class for bookcase.
 *
 * @author Haijin He
 */
public class BookCase extends Wall
{
  public BookCase(int xCor, int yCor, int zone)
  {
    super(xCor, yCor, zone);
  }
  
  /**
   * For printHouse method in Level class
   *
   * @return character to represent the bookcase
   */
  public char getChar()
  {
    return '&';
  }
  
  public Color getColor()
  {
    return null;
  }
}
