# ZombieHouse
3D "Zombie House" game for CS351 @ UNM

Use WASD for movement, mouse for view swivel

F1 enables full-screen mode, F2 disables it

Press Escape to exit the game

The user has 5 life, while the zombies have 4.

If the player uses up all three past self's and dies, they will lose and be prompted for further action.

Each new level increases the player's speed and stamina, zombie spawn rate, and zombie speed.

A* works based around if a zombie is close enough to a wall, otherwise it uses a directional based movement.

Master zombie from:
http://opengameart.org/content/thin-zombie-awake-zombie-asset
ny dogchicken from Rosswet Mobile

RandomWalk and LineWalk zombie model from
tigerTowel on BlendSwap (For the random i just made the zombie have a white shirt)

The past self model was found at
http://forum.avora.org/viewtopic.php?f=33&t=16
by Clint Bellanger

For the first person hand and knife, the models were from:
The models of the first person hands were found at
https://sketchfab.com/models/547a45535f0c4fe787948f7a7a6a88db
by DavidFischer

The knife model is found at
http://tf3dm.com/3d-model/combat-knife-17573.html
by gamingstudio

The animations for the Player3D and Attack3D were done by us in blender.

Program startup and level restarting/loading takes a while, due to the expensive mesh loading.