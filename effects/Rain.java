package effects;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.Game;
import utilz.LoadSave;

public class Rain {

	private Point2D.Float[] drops; // Array to store the positions of raindrops
	private Random rand; // Random number generator
	private float rainSpeed = 1.25f; // Speed at which the raindrops fall
	private BufferedImage rainParticle; // Image for the raindrop particle


	public Rain() {
		rand = new Random();
		drops = new Point2D.Float[1000]; // Initialize the array to hold 1000 raindrop positions
		rainParticle = LoadSave.GetSpriteAtlas(LoadSave.RAIN_PARTICLE); // Load the image for the raindrop particle
		initDrops(); // Initialize the positions of the raindrops
	}

	private void initDrops() {
		for (int i = 0; i < drops.length; i++)
			drops[i] = getRndPos(); // Generate random positions for each raindrop
	}

	private Point2D.Float getRndPos() {
		return new Point2D.Float((int) getNewX(0), rand.nextInt(Game.GAME_HEIGHT)); // Get a random position for a raindrop
	}

	public void update(int xLvlOffset) {
		for (Point2D.Float p : drops) {
			p.y += rainSpeed; // Update the y-coordinate of each raindrop to simulate falling
			if (p.y >= Game.GAME_HEIGHT) {
				p.y = -20; // Reset the raindrop to the top of the screen if it falls below the game height
				p.x = getNewX(xLvlOffset); // Generate a new x-coordinate for the raindrop
			}
		}
	}

	private float getNewX(int xLvlOffset) {
		float value = (-Game.GAME_WIDTH) + rand.nextInt((int) (Game.GAME_WIDTH * 3f)) + xLvlOffset; // Generate a new x-coordinate for a raindrop within the game boundaries
		return value;
	}

	public void draw(Graphics g, int xLvlOffset) {
		for (Point2D.Float p : drops)
			g.drawImage(rainParticle, (int) p.getX() - xLvlOffset, (int) p.getY(), 3, 12, null); // Draw each raindrop using the raindrop particle image
	}

}
