package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

	public static int MENU_1 = 0; // Identifier for the menu music
	public static int LEVEL_1 = 1; // Identifier for level 1 music
	public static int LEVEL_2 = 2; // Identifier for level 2 music (we actually decided to make all levels the same song)

	public static int DIE = 0; // Identifier for 'die' sound effect
	public static int JUMP = 1; // Identifier for 'jump' sound effect
	public static int GAMEOVER = 2; // Identifier for 'game over' sound effect
	public static int LVL_COMPLETED = 3; // Identifier for 'level completed' sound effect
	public static int ATTACK_ONE = 4; // Identifier for 'attack 1' sound effect
	public static int ATTACK_TWO = 5; // Identifier for 'attack 2' sound effect
	public static int ATTACK_THREE = 6; // Identifier for 'attack 3' sound effect
	// In the end, we actually decided on one attack sound effect so all 3 attack effects are the same sound
	
	private Clip[] songs, effects; // Arrays to store the song and effect clips
	private int currentSongId; // ID of the currently playing song
	private float volume = 1f; // Volume level for music
	private float volume2 = 0.3f; //Volume level for effects
	private boolean songMute, effectMute; // Flags to control muting of songs and effects
	private Random rand = new Random(); // Random number generator

	public AudioPlayer() {
		loadSongs(); // Load the song clips
		loadEffects(); // Load the effect clips
		playSong(MENU_1); // Plays the menu song by default
	}

	private void loadSongs() {
		String[] names = { "menu", "level1", "level2" }; // Names of the song files
		songs = new Clip[names.length]; // Initialize the songs array
		for (int i = 0; i < songs.length; i++)
			songs[i] = getClip(names[i]); // Loads each song clip
	}

	private void loadEffects() {
		String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3" }; // Names of the effect files
		effects = new Clip[effectNames.length]; // Initialize the effects array
		for (int i = 0; i < effects.length; i++)
			effects[i] = getClip(effectNames[i]); // Load each effect clip

		updateEffectsVolume(); // Update the volume levels of effects

	}

	private Clip getClip(String name) {
		URL url = getClass().getResource("/audio/" + name + ".wav"); // Get the URL of the audio file
		AudioInputStream audio;

		try {
			audio = AudioSystem.getAudioInputStream(url); // Get the audio input stream
			Clip c = AudioSystem.getClip(); // Create a new Clip object
			c.open(audio); // Open the audio input stream
			return c; // Return the loaded clip

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

			e.printStackTrace();
		}

		return null;

	}

	public void setVolume(float volume) {
		this.volume = volume; // Update the volume level for songs
		updateSongVolume(); // Apply the new volume level to the currently playing song
		updateEffectsVolume(); // Apply the new volume level to the effects
	}
	public void setVolume2(float volume2) {
		this.volume = volume2; // Update the volume level for effects
		updateEffectsVolume(); // Apply the new volume level to the effects
	}

	public void stopSong() {
		if (songs[currentSongId].isActive())
			songs[currentSongId].stop(); // Stop the currently playing song
	}

	public void setLevelSong(int lvlIndex) {
		if (lvlIndex % 2 == 0)
			playSong(LEVEL_1); // Play level 1 song if the level index is even
		else
			playSong(LEVEL_2); // Play level 2 song if the level index is odd
	}

	public void lvlCompleted() {
		stopSong(); // Stop the currently playing song
		playEffect(LVL_COMPLETED); // Play the "level completed" effect
	}

	public void playAttackSound() {
		int start = 4;
		start += rand.nextInt(3); // Randomly select an attack sound to play
		playEffect(start); // Play the selected attack sound
	}

	public void playEffect(int effect) {
		if (effects[effect].getMicrosecondPosition() > 0)
			effects[effect].setMicrosecondPosition(0); // Rewind the effect if it is already playing
		effects[effect].start(); // Start playing the effect
	}

	public void playSong(int song) {
		stopSong(); // Stop the currently playing song

		currentSongId = song; // Set the ID of the new song to play
		updateSongVolume(); // Apply the volume level to the new song
		songs[currentSongId].setMicrosecondPosition(0); // Reset the song to the beginning
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY); // Start playing the song in a loop
	}

	public void toggleSongMute() {
		this.songMute = !songMute; // Toggle the mute status of the songs
		for (Clip c : songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute); // Set the mute value for each song clip
		}
	}

	public void toggleEffectMute() {
		this.effectMute = !effectMute; // Toggle the mute status of the effects
		for (Clip c : effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute); // Set the mute value for each effect clip
		}
		if (!effectMute)
			playEffect(JUMP); // If effects are unmuted, play the "jump" effect
	}

	private void updateSongVolume() {

		FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volume) + gainControl.getMinimum(); // Calculate the new gain value based on the volume level
		gainControl.setValue(gain); // Set the new gain value for the song

	}

	private void updateEffectsVolume() {
		for (Clip c : effects) {
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volume) + gainControl.getMinimum();
			gainControl.setValue(gain); // Set the new gain value for each effect clip
		}
	}

}
