package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Drop game;
    OrthographicCamera camera;
    Texture backgroundImage;

    public MainMenuScreen(final Drop game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        backgroundImage = new Texture(Gdx.files.internal("MainScreen.jpg"));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.batch.draw(backgroundImage, 0, 0, 800, 480);

        //game.font.draw(game.batch, "Welcome to Drop!!! ", 200, 250);
        //game.font.draw(game.batch, "Press [Spacebar]", 265, 210);
        game.font.getData().setScale(2.0f);
        game.batch.end();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            game.setScreen(new MyGdxGame(game));
            dispose();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
    }
}