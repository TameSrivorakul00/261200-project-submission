package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Iterator;


public class MyGdxGame implements Screen {

	///////// Member Variable of class /////////
	private Texture dropImage;
	private Texture dropImage2;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	////////////////////////////////////////////

	//// Variables that hold instances of custom class ////
	private Kitty kitty1;
	private Kitty kitty2;
	///////////////////////////////////////////////////////

	private Array<Rectangle> pumpkin;
	private long lastDropTime;
	private int countPumpkin = 0; //Score tracker for player
	private int countPumpkin2 = 0; //Score tracker for player2
	private long gameStartTime;   // Time when the game started (in nanoseconds)
	private long gameDuration = 60 * 1000000000L; // 67 seconds (Actual game + bonus round)
	private boolean isGameOver = false;

	private float initialPumpkinSpeed = 50; // Initial speed in pixels per second
	private float boostedPumpkinSpeed = 100; // Increased speed after 20 seconds
	private long lastSpeedIncreaseTime = 0;
	private Texture backgroundImage;

	private long bonusRoundStartTime;
	private boolean isInBonusRound = false;
	private Array<Texture> pumpkinTextures;
	private Texture pumpkinTexture;
	private BitmapFont scoreFont1;
	private BitmapFont scoreFont2;
	private BitmapFont winnerFont;

	private Array<Rectangle> specialPumpkin;
	private Array<Texture> specialPumpkinTextures;
	//private Texture specialRaindropTexture;
	private long lastSpecialDropTime;

	private BitmapFont font;
	private Array fontParameter;


	// Initializes assets and objects
	public MyGdxGame(final Drop game) {
		batch = new SpriteBatch();

		kitty1 = new Kitty(368, 20, 20, 20, "hello kitty black.png");
		kitty2 = new Kitty(32, 20, 20, 20, "hello kitty purple.png");

		dropImage = new Texture(Gdx.files.internal("Pumpkin.png"));
		pumpkinTextures = new Array<Texture>();

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("song.mp3"));
		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 850, 480); // View that user sees

		pumpkin = new Array<Rectangle>();
		spawnPumpkin();

		gameStartTime = TimeUtils.nanoTime();

		backgroundImage = new Texture(Gdx.files.internal("background.jpg"));

		bonusRoundStartTime = -1;
		initialPumpkinSpeed = 50;

		pumpkinTextures = new Array<Texture>();
		pumpkinTextures.add(new Texture(Gdx.files.internal("candy1.png")));
		pumpkinTextures.add(new Texture(Gdx.files.internal("candy2.png")));

		dropImage2 = new Texture(Gdx.files.internal("skull.png"));
		specialPumpkin = new Array<Rectangle>();
		specialPumpkinTextures = new Array<Texture>();
		//specialRaindropTextures.add(new Texture(Gdx.files.internal("skull.png")));

		scoreFont1 = new BitmapFont();
		scoreFont2 = new BitmapFont();

		scoreFont1.getData().setScale(2.0f);
		scoreFont2.getData().setScale(2.0f);

		winnerFont = new BitmapFont();
		winnerFont.getData().setScale(3.0f);

// Load custom TrueTypeFont
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Elfboyclassic-PKZgZ.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 40;
		font = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}





	// Main game loop where we handle user input, update game logic, and render the game
	@Override
	public void render (float delta) {

		//Kitty 1 controller
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			kitty1.getRectangle().x -= 300 * Gdx.graphics.getDeltaTime(); // Number is for speed
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			kitty1.getRectangle().x += 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.UP))
			kitty1.getRectangle().y += 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
			kitty1.getRectangle().y -= 300 * Gdx.graphics.getDeltaTime();

		//Kitty 2 controller
		if(Gdx.input.isKeyPressed(Input.Keys.A))
			kitty2.getRectangle().x -= 300 * Gdx.graphics.getDeltaTime(); // Number is for speed
		if(Gdx.input.isKeyPressed(Input.Keys.D))
			kitty2.getRectangle().x += 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.W))
			kitty2.getRectangle().y += 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.S))
			kitty2.getRectangle().y -= 300 * Gdx.graphics.getDeltaTime();

		if(kitty1.getRectangle().x < -45) kitty1.getRectangle().x = -45; //Don't let kitty1  go beyond the left side
		if(kitty1.getRectangle().x > 800 - 64) kitty1.getRectangle().x = 800 - 64;
		if(kitty1.getRectangle().y < 0) kitty1.getRectangle().y = 0;
		if(kitty1.getRectangle().y > 480 - 64) kitty1.getRectangle().y = 480 - 64;

		if(kitty2.getRectangle().x < -45) kitty2.getRectangle().x = -45; //Don't let kitty2 go beyond the left side
		if(kitty2.getRectangle().x > 800 - 64) kitty2.getRectangle().x = 800 - 64;
		if(kitty2.getRectangle().y < 0) kitty2.getRectangle().y = 0;
		if(kitty2.getRectangle().y > 480 - 64) kitty2.getRectangle().y = 480 - 64;


		// Normal pumpkin raindrop
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnPumpkin();

		for (Iterator<Rectangle> iter = pumpkin.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime(); // Speed of pumpkin
			if(raindrop.y + 64 < 0) iter.remove();

			if(raindrop.overlaps(kitty1.getRectangle())) { // If pumpkin hits kitty1
				dropSound.play();
				countPumpkin++;
				iter.remove();
			}

			if(raindrop.overlaps(kitty2.getRectangle())) { // If pumpkin hits kitty2
				dropSound.play();
				countPumpkin2++;
				iter.remove();
			}
		}

		// SpecialRaindrop
		if (TimeUtils.nanoTime() - lastSpecialDropTime >= 5 * 1000000000L) {
			spawnSpecialPumpkin();
		}

		for (Iterator<Rectangle> iter = specialPumpkin.iterator(); iter.hasNext();) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 300 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0) {
				iter.remove();
			}

			// Check if a special raindrop overlaps with either kitty
			if (raindrop.overlaps(kitty1.getRectangle())) {
				dropSound.play();
				countPumpkin--; // Decrease the score by 1
				iter.remove();
			}

			if (raindrop.overlaps(kitty2.getRectangle())) {
				dropSound.play();
				countPumpkin2--; // Decrease the score by 1
				iter.remove();
			}
		}

		long elapsedTime = TimeUtils.nanoTime() - gameStartTime;
		if (elapsedTime >= gameDuration) {
			if (!isGameOver) {
				endGame();
			}
		}

		ScreenUtils.clear(0, 0, 0, 0);
		camera.update(); //Camera sees most recent one
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		for(Rectangle raindrop: pumpkin) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}

		batch.draw(kitty2.getTexture(), kitty2.getRectangle().x, kitty2.getRectangle().y);
		batch.draw(kitty1.getTexture(), kitty1.getRectangle().x, kitty1.getRectangle().y);
		batch.end();

		long elapsedTimeSeconds = TimeUtils.nanosToMillis(TimeUtils.nanoTime() - gameStartTime) / 1000; // Convert to seconds
		long timeSinceLastSpeedIncrease = elapsedTimeSeconds - lastSpeedIncreaseTime;

		if (timeSinceLastSpeedIncrease >= 20) {
			// Increase the raindrop falling speed every 20 seconds
			int speedIncreaseCount = (int) (timeSinceLastSpeedIncrease / 20);
			initialPumpkinSpeed = boostedPumpkinSpeed * (speedIncreaseCount + 1); // Increase the speed
			lastSpeedIncreaseTime += 20 * speedIncreaseCount; // Update the last speed increase time
		}

		if (elapsedTimeSeconds >= 40) {
			initialPumpkinSpeed = boostedPumpkinSpeed * 3; // Make the speed after 40 seconds
		}

		for (Rectangle raindrop : pumpkin) {
			raindrop.y -= initialPumpkinSpeed * Gdx.graphics.getDeltaTime();
		}

		if (!isInBonusRound) {
			// Check if 1 minute has passed
			if (TimeUtils.nanoTime() - gameStartTime >= 53 * 1000000000L) {
				startBonusRound();
			}
		} else {
			// Bonus round logic here
			long bonusRoundElapsedTime = TimeUtils.nanoTime() - bonusRoundStartTime;

			if (bonusRoundElapsedTime <= 7 * 1000000000L) {
				// Update the raindrop texture for all raindrops during the bonus round
				pumpkinTexture = pumpkinTextures.random();
				spawnPumpkin();
			}  else if (bonusRoundElapsedTime > (gameDuration + 53 * 1000000000L)) { // Stop the game after 1 minute and 7 seconds
				endBonusRound();
			}
		}

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (Rectangle raindrop : pumpkin) {
			if (isInBonusRound) {
				batch.draw(pumpkinTexture, raindrop.x, raindrop.y); // Use the bonus round texture
			} else {
				batch.draw(dropImage, raindrop.x, raindrop.y); // Use the default raindrop texture
			}
		}

		for (Rectangle specialRaindrop : specialPumpkin) {
			batch.draw(dropImage2, specialRaindrop.x, specialRaindrop.y); // Use the special raindrop texture (skull.png)
		}

		batch.draw(kitty2.getTexture(), kitty2.getRectangle().x, kitty2.getRectangle().y);
		batch.draw(kitty1.getTexture(), kitty1.getRectangle().x, kitty1.getRectangle().y);

		font.draw(batch, "Purple Kitty: " + countPumpkin2, 45, 460);
		font.draw(batch, "Black Kitty: " + countPumpkin, 550, 460);

		if (isGameOver && !winningMessage.isEmpty()) {
			winnerFont.draw(batch, winningMessage, Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2);
		}
		batch.end();

	}

	private void spawnPumpkin() {
		long elapsedTime = TimeUtils.nanoTime() - gameStartTime;

		if (elapsedTime <= (53 + 7) * 1000000000L) {
			Rectangle raindrop = new Rectangle();
			raindrop.x = MathUtils.random(0, 800 - 64);
			raindrop.y = 480;
			raindrop.width = 64;
			raindrop.height = 64;

			if (isInBonusRound) {
				// Randomly select a raindrop texture from the array
				pumpkinTexture = pumpkinTextures.random();
			} else {
				// Use the default raindrop texture
				pumpkinTexture = dropImage;
			}

			pumpkin.add(raindrop);
			lastDropTime = TimeUtils.nanoTime();
		}
	}

	private void spawnSpecialPumpkin() {
		if(!isInBonusRound) {
			long elapsedTime = TimeUtils.nanoTime() - lastSpecialDropTime;

			if (elapsedTime >= 5 * 1000000000L) {
				Rectangle raindrop = new Rectangle();
				raindrop.x = MathUtils.random(0, 800 - 64);
				raindrop.y = 480;
				raindrop.width = 64;
				raindrop.height = 64;

				pumpkinTexture = dropImage2;

				specialPumpkin.add(raindrop);
				lastSpecialDropTime = TimeUtils.nanoTime();
			}
		}
	}

	private void startBonusRound() {
		isInBonusRound = true;
		bonusRoundStartTime = TimeUtils.nanoTime();
	}

	private void endBonusRound() {
		checkAndDisplayWinner();
		isInBonusRound = false;
	}

	private void endGame() {
		checkAndDisplayWinner();
		isGameOver = true;
	}

	private String winningMessage = "";
	private void checkAndDisplayWinner() {
		long elapsedTime = TimeUtils.nanoTime() - gameStartTime;

		if (elapsedTime <= (60 + 7) * 1000000000L) {
			if (countPumpkin > countPumpkin2) {
				winningMessage = "Black Kitty wins!";
			} else if (countPumpkin2 > countPumpkin) {
				winningMessage = "Purple Kitty wins!";
			} else {
				winningMessage = "It's a tie!";
			}
		}
	}

	private void announceWin(String message) {
		long elapsedTime = TimeUtils.nanoTime() - gameStartTime;

		if (elapsedTime <= (60 + 7) * 1000000000L) {
			System.out.println(message);
			isGameOver = true;
		}
	}

	// Method used to clean up and release resources when the game is done
	@Override
	public void dispose () {
		dropImage.dispose();
		backgroundImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
		//font.dispose();

		for (Texture texture : pumpkinTextures) {
			texture.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
}
