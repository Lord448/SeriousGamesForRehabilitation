package com.badlogic.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyProbesGame extends ApplicationAdapter {
    SpriteBatch batch;
    private Climb personaje;
    private GasFill bar;

    private Rocket rocket;
    @Override
    public void create() {
        batch = new SpriteBatch();
        personaje = new Climb(50, 50);
        bar = new GasFill(430, 10);
        rocket = new Rocket(200, 10);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor( 1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        personaje.render(batch);
        bar.render(batch, batch);
        rocket.draws(batch, batch);
        batch.end();
    }
}
