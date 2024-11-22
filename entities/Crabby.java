package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.IsFloor;
import static utilz.Constants.Dialogue.*;

import gamestates.Playing;

public class Crabby extends Enemy {

	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY); // Initialize the wildling enemy with its position, dimensions, and sprite type
		initHitbox(22, 19); // Initialize the hitbox dimensions for collision detection
		initAttackBox(82, 19, 30); // Initialize the attack box dimensions for attacking the player
	}

	public void update(int[][] lvlData, Playing playing) {
		updateBehavior(lvlData, playing); // Update the behavior of the wilding enemy
		updateAnimationTick(); // Update the animation frame of the wildling enemy
		updateAttackBox(); // Update the position of the attack box
	}

	private void updateBehavior(int[][] lvlData, Playing playing) {
		if (firstUpdate)
			firstUpdateCheck(lvlData); // Check for the first update to perform necessary initialization

		if (inAir) {
			inAirChecks(lvlData, playing); // Perform checks for when the wildling enemy is in the air
		} else {
			switch (state) {
			case IDLE:
				if (IsFloor(hitbox, lvlData))
					newState(RUNNING); // Transition to the RUNNING state if the wildling enemy is on the floor
				else
					inAir = true; // Set the wilding enemy to be in the air if not on the floor
				break;
			case RUNNING:
				if (canSeePlayer(lvlData, playing.getPlayer())) { // Check if the wildling enemy can see the player
					turnTowardsPlayer(playing.getPlayer()); // Turn the wildling enemy towards the player
					if (isPlayerCloseForAttack(playing.getPlayer())) // Check if the player is close enough for an attack
						newState(ATTACK); // Transition to the ATTACK state
				}
				move(lvlData); // Move the wildling enemy

				if (inAir)
					playing.addDialogue((int) hitbox.x, (int) hitbox.y, EXCLAMATION); // Add dialogue when the wildling enemy is in the air

				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;
				if (aniIndex == 3 && !attackChecked)
					checkPlayerHit(attackBox, playing.getPlayer()); // Check if the player is hit during the ATTACK state
				break;
			case HIT:
				if (aniIndex <= GetSpriteAmount(enemyType, state) - 2)
					pushBack(pushBackDir, lvlData, 2f); // Apply pushback when the wildling enemy is hit
				updatePushBackDrawOffset(); // Update the wildling draw offset for rendering
				break;
			}
		}
	}

}