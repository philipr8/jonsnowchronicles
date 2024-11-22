package utilz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

public class LoadSave {
	// File names for sprites and images
	public static final String PLAYER_ATLAS = "jon_snow1.png";
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String MENU_BUTTONS = "menuButton.png";
	public static final String MENU_BACKGROUND = "tbg.png";
	public static final String PAUSE_BACKGROUND = "pause_menu.png";
	public static final String SOUND_BUTTONS = "sound_button.png";
	public static final String URM_BUTTONS = "urm_buttons.png";
	public static final String VOLUME_BUTTONS = "volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG = "backgroundMenu.png";
	public static final String PLAYING_BG_IMG = "playing_bg_img.png";
	public static final String LVL1_BG = "bg1to2.png";
	public static final String LVL2_BG = "bg1to2.png";
	public static final String LVL3_BG = "bg3to4.png";
	public static final String LVL4_BG = "bg3to4.png";
	public static final String LVL5_BG = "bg5.png";
	public static final String BIG_CLOUDS = "tbg.png";
	public static final String SMALL_CLOUDS = "tbg.png";
	public static final String CRABBY_SPRITE = "wildlings.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";
	public static final String POTION_ATLAS = "potions_sprites.png";
	public static final String CONTAINER_ATLAS = "objects_sprites.png";
	public static final String TRAP_ATLAS = "trap_atlas.png";
	public static final String CANNON_ATLAS = "iceLauncher.png";
	public static final String CANNON_BALL = "iceball.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTIONS_MENU = "options_background.png";
	public static final String PINKSTAR_ATLAS = "white_walker.png";
	public static final String QUESTION_ATLAS = "question_atlas.png";
	public static final String EXCLAMATION_ATLAS = "exclamation_atlas.png";
	public static final String SHARK_ATLAS = "boltonSoldier.png";
	public static final String CREDITS = "credits_list.png";
	public static final String GRASS_ATLAS = "tbg.png";
	public static final String TREE_ONE_ATLAS = "TreesWinter.png";
	public static final String TREE_TWO_ATLAS = "tbg.png";
	public static final String GAME_COMPLETED = "game_completed.png";
	public static final String RAIN_PARTICLE = "rain_particle.png";
	public static final String WATER_TOP = "tbg.png";
	public static final String WATER_BOTTOM = "tbg.png";
	public static final String SHIP = "ship.png";
	public static final String TUTORIAL = "tutorialScreen.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName); // Retrieve the image file as an input stream
		try {
			img = ImageIO.read(is); // Read the image from the input stream

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close(); // Close the input stream
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;  // Return the loaded image
	}

	public static BufferedImage[] GetAllLevels() {
		URL url = LoadSave.class.getResource("/lvls");  // Gets the URL of the 'lvls' resource directory
		File file = null;

		try {
			file = new File(url.toURI()); // Create a file object using the URL
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File[] files = file.listFiles(); // Get all the files in the directory
		File[] filesSorted = new File[files.length]; // Create an array to store sorted files


		for (int i = 0; i < filesSorted.length; i++)
			for (int j = 0; j < files.length; j++) {
				if (files[j].getName().equals((i + 1) + ".png"))
					filesSorted[i] = files[j]; // Sort the files based on their names

			}

		BufferedImage[] imgs = new BufferedImage[filesSorted.length]; // Create an array to store the loaded images

		for (int i = 0; i < imgs.length; i++)
			try {
				imgs[i] = ImageIO.read(filesSorted[i]); // Read the images from the sorted files
			} catch (IOException e) {
				e.printStackTrace();
			}

		return imgs; // Return the array of loaded images
	}

}