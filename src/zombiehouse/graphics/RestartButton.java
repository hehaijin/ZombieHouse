package zombiehouse.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import zombiehouse.common.LevelVar;
import zombiehouse.graphics.MainApplication.GameLoop;
import zombiehouse.level.house.Level;

/**
 * This is used in the eng game dialog to restart the game
 *
 * @author Joshua Donckels.
 */
public class RestartButton extends Button implements EventHandler<ActionEvent>
{
  Level level;
  Stage dialog;
  GameLoop gameLoop;
  
  /**
   * This button will restart the game by resetting everything, then starting game loop and closing
   * the end game dialog.
   *
   * @param level    the current level object
   * @param dialog   the end game dialog
   * @param gameLoop the game loop being used.
   */
  public RestartButton(Level level, Stage dialog, GameLoop gameLoop)
  {
    this.setOnAction(this);
    this.level = level;
    this.dialog = dialog;
    this.setText("Restart Game");
    this.gameLoop = gameLoop;
  }
  
  @Override
  public void handle(ActionEvent e)
  {
    dialog.close();
    
    LevelVar.levelNum = 0;
    LevelVar.spawnMax = 10;
    LevelVar.zombieSpeed = 0.5;
    LevelVar.pillarSpawnChance = 0.2;
    level = new Level();
    level.nextLevel();
    level.fullGen();
    
    gameLoop.start();
  }
}
