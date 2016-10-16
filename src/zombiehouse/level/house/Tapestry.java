package zombiehouse.level.house;

/**
 *
 */

import javafx.scene.paint.Color;
import zombiehouse.common.LevelVar;

public class Tapestry extends Wall
{

  /**
   * constructor
   * @param xCor
   * @param yCor
   * @param zone
   */
  public Tapestry(int xCor, int yCor, int zone)
  {
    super(xCor, yCor, zone);
  }

  /**
   * For printHouse method in Level class
   * @return
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

