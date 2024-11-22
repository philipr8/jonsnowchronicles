package effects;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.Dialogue.*;

public class DialogueEffect {

	private int x, y, type; // Coordinates and type of the dialogue effect
	private int aniIndex, aniTick; // Animation index and tick for the effect
	private boolean active = true; // Indicates if the effect is currently active

	public DialogueEffect(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public void update() {
		aniTick++; // Increment the animation tick
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++; // Move to the next animation frame
			if (aniIndex >= GetSpriteAmount(type)) {
				active = false; // Deactivate the effect if the animation is complete
				aniIndex = 0; // Reset the animation index for reuse
			}
		}
	}

	public void deactive() {
		active = false; // Deactivate the effect
	}

	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
		active = true; // Reset the effect with new coordinates and reactivate it
	}

	public int getAniIndex() {
		return aniIndex; // Get the current animation index of the effect
	}

	public int getX() {
		return x; // Get the x-coordinate of the effect
	}

	public int getY() {
		return y; // Get the y-coordinate of the effect
	}

	public int getType() {
		return type; // Get the type of the effect
	}

	public boolean isActive() {
		return active; // Check if the effect is currently active
	}
}
