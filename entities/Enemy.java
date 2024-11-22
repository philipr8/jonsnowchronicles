package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;

import java.awt.geom.Rectangle2D;

import gamestates.Playing;

import static utilz.Constants.Directions.*;
import static utilz.Constants.*;

import main.Game;

public abstract class Enemy extends Entity {
	protected int enemyType;
	protected boolean firstUpdate = true;
	protected int walkDir = LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active = true;
	protected boolean attackChecked;
	protected int attackBoxOffsetX;

	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;

		maxHealth = GetMaxHealth(enemyType); // Set the maximum health of the enemy based on its type
		currentHealth = maxHealth; // Set the current health of the enemy to its maximum
		walkSpeed = Game.SCALE * 0.35f; // Set the walking speed of the enemy
	}

	protected void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX; // Update the position of the attack box relative to the hitbox
		attackBox.y = hitbox.y;
	}

	protected void updateAttackBoxFlip() {
		if (walkDir == RIGHT)
			attackBox.x = hitbox.x + hitbox.width;
		else
			attackBox.x = hitbox.x - attackBoxOffsetX;

		attackBox.y = hitbox.y;
	}

	protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
		attackBox = new Rectangle2D.Float(x, y, (int) (w * Game.SCALE), (int) (h * Game.SCALE));
		this.attackBoxOffsetX = (int) (Game.SCALE * attackBoxOffsetX);
	}

	protected void firstUpdateCheck(int[][] lvlData) {
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true; // Check if the enemy is in the air during the first update
		firstUpdate = false;
	}

	protected void inAirChecks(int[][] lvlData, Playing playing) {
		if (state != HIT && state != DEAD) {
			updateInAir(lvlData); // Check and update the position of the enemy in the air
			playing.getObjectManager().checkSpikesTouched(this); // Check if the enemy has touched any spikes
			if (IsEntityInWater(hitbox, lvlData))
				hurt(maxHealth); // Inflict damage to the enemy if it is in water
		}
	}

	protected void updateInAir(int[][] lvlData) {
		if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.y += airSpeed; // Update the vertical position of the enemy while in the air
			airSpeed += GRAVITY; // Apply gravity to the air speed
		} else {
			inAir = false; // The enemy is no longer in the air
			hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed); // Adjust the position of the enemy to the floor or roof
			tileY = (int) (hitbox.y / Game.TILES_SIZE); // Update the tile position of the enemy
		}
	}

	protected void move(int[][] lvlData) {
		float xSpeed = 0;

		if (walkDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;

		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			if (IsFloor(hitbox, xSpeed, lvlData)) {
				hitbox.x += xSpeed; // Update the horizontal position of the enemy while walking
				return;
			}

		changeWalkDir(); // Change the walking direction of the enemy if unable to move in the current direction
	}

	protected void turnTowardsPlayer(Player player) {
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT; // Change the walking direction of the enemy to face towards the player if the player is on the right
		else
			walkDir = LEFT; // Change the walking direction of the enemy to face towards the player if the player is on the left
	}

	protected boolean canSeePlayer(int[][] lvlData, Player player) {
		int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE); // Calculate the tile position of the player vertically
		if (playerTileY == tileY) // Check if the player is on the same level as the enemy
			if (isPlayerInRange(player)) {
				if (IsSightClear(lvlData, hitbox, player.hitbox, tileY))
					return true; // Return true if there is a clear line of sight between the enemy and the player
			}
		return false; // Return false if the enemy cannot see the player or they are not on the same level
	}

	protected boolean isPlayerInRange(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); // Calculate the absolute horizontal distance between the enemy and the player
		return absValue <= attackDistance * 5; // Check if the player is within the attack range of the enemy, allowing a tolerance of 5 times the attack distance
	}

	protected boolean isPlayerCloseForAttack(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); // Calculate the absolute horizontal distance between the enemy and the player
		switch (enemyType) {
		case CRABBY -> {
			return absValue <= attackDistance; // Return true if the player is within the attack distance of the crab enemy
		}
		case SHARK -> {
			return absValue <= attackDistance * 2; // Return true if the player is within twice the attack distance of the shark enemy
		}
		}
		return false; // Return false if the enemy type is not recognized or the player is not within the attack range
	}

	public void hurt(int amount) {
		currentHealth -= amount; // Decrease the current health of the enemy by the specified amount
		if (currentHealth <= 0)
			newState(DEAD); // Change the state of the enemy to DEAD if its health is reduced to or below zero
		else {
			newState(HIT); // Change the state of the enemy to HIT if it is still alive
			if (walkDir == LEFT)
				pushBackDir = RIGHT; // Set the pushback direction to the opposite side if the enemy is walking towards the left
			else
				pushBackDir = LEFT; // Set the pushback direction to the opposite side if the enemy is walking towards the right
			pushBackOffsetDir = UP; // Set the pushback offset direction to UP
			pushDrawOffset = 0; // Reset the pushback draw offset
		}
	}

	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
		if (attackBox.intersects(player.hitbox))
			player.changeHealth(-GetEnemyDmg(enemyType), this); // Reduce the player's health by the enemy's damage value if the attack box intersects with the player's hitbox
		else {
			if (enemyType == SHARK)
				return; // Return early if the enemy type is a shark and the attack does not hit the player
		}
		attackChecked = true; // Set the attack as checked regardless of whether it hits the player or not
	}

	protected void updateAnimationTick() {
		aniTick++; // Increment the animation tick counter
		if (aniTick >= ANI_SPEED) { // Check if the animation tick has reached the specified speed
			aniTick = 0; // Reset the animation tick counter
			aniIndex++; // Increment the animation index
			if (aniIndex >= GetSpriteAmount(enemyType, state)) { // Check if the animation index exceeds the maximum sprite amount for the enemy type and state
				if (enemyType == CRABBY || enemyType == SHARK) { // Check if the enemy type is either CRABBY or SHARK
					aniIndex = 0; // Reset the animation index

					switch (state) {
					case ATTACK, HIT -> state = IDLE; // Change the state to IDLE if the previous state was ATTACK or HIT
					case DEAD -> active = false; // Deactivate the enemy if the previous state was DEAD
					}
				} else if (enemyType == PINKSTAR) { // Check if the enemy type is PINKSTAR
					if (state == ATTACK)
						aniIndex = 3; // Set the animation index to 3 if the current state is ATTACK
					else {
						aniIndex = 0; // Reset the animation index
						if (state == HIT) {
							state = IDLE; // Change the state to IDLE if the current state is HIT

						} else if (state == DEAD)
							active = false; // Deactivate the enemy if the current state is DEAD
					}
				}
			}
		}
	}

	protected void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT; // Change the walking direction to RIGHT if the current direction is LEFT
		else
			walkDir = LEFT; // Change the walking direction to LEFT if the current direction is RIGHT
	}

	public void resetEnemy() {
		hitbox.x = x; // Reset the hitbox's X coordinate to the initial X position
		hitbox.y = y; // Reset the hitbox's Y coordinate to the initial Y position
		firstUpdate = true; // Set the firstUpdate flag to true
		currentHealth = maxHealth; // Reset the current health to the maximum health
		newState(IDLE); // Change the state to IDLE
		active = true; // Activate the enemy
		airSpeed = 0; // Reset the air speed

		pushDrawOffset = 0; // Reset the pushback draw offset

	}

	public int flipX() {
		if (walkDir == RIGHT)
			return width; // Return the width if the walking direction is RIGHT
		else
			return 0; // Return 0 if the walking direction is not RIGHT
	}

	public int flipW() {
		if (walkDir == RIGHT)
			return -1; // Return -1 if the walking direction is RIGHT
		else
			return 1; // Return 1 if the walking direction is not RIGHT
	}

	public boolean isActive() {
		return active; // Return the value of the active flag indicating if the enemy is active
	}

	public float getPushDrawOffset() {
		return pushDrawOffset; // Return the pushback draw offset value
	}

}