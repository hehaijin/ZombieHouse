package zombiehouse.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import zombiehouse.common.LevelVar;
import zombiehouse.level.house.Level;
import zombiehouse.graphics.MainApplication.*;

/**
 * Created by joshu on 10/16/2016.
 */
public class RestartButton extends Button implements EventHandler<ActionEvent>
{
  Level level;
  Stage dialog;
  GameLoop gameLoop;
  public RestartButton(Level level, Stage dialog, GameLoop gameLoop) {
    this.setOnAction(this);
    this.level = level;
    this.dialog = dialog;
    this.setText("Restart Game");
    this.gameLoop = gameLoop;
  }

  @Override
  public void handle(ActionEvent e) {
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
