package ca.crit.hungryhamster.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

import ca.crit.hungryhamster.GameHandler;
import ca.crit.hungryhamster.main.Background;
import ca.crit.hungryhamster.main.GameScreen;
import ca.crit.hungryhamster.main.GameText;
import ca.crit.hungryhamster.main.Sounds;

public class MainMenu implements Screen {
    //STATES
    private enum MenuState {
        INIT,
        LOGIN,
        REGISTER,
        CONFIG
    }
    private static MenuState menuState;
    //SCREEN
    private final Camera camera;
    private final Viewport viewport;
    //GRAPHICS
    private final SpriteBatch batch;
    private final Background background;
    private Skin skin;
    private Stage mainStage, loginStage, configStage;
    private GameScreen gameScreen;
    private Sounds sounds;
    private GameText titleText;

    public MainMenu(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        mainStage = new Stage();
        loginStage = new Stage();
        configStage = new Stage();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        background = new Background();
        titleText = new GameText("Hungry Hamster", Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        titleText.setX(10);
        titleText.setY(115);
        titleText.setScales(0.16f, 0.38f);
        skin = new Skin(Gdx.files.internal("UISkin/uiskin.json"));
        mainMenuConstruct();
        registerMenuConstruct();
        loginMenuConstruct();
        configMenuConstruct();
        menuState = MenuState.INIT;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
        background.renderDynamicBackground(deltaTime, batch);
        background.renderStaticBackground(batch);
        titleText.draw(batch);
        batch.end();

        switch (menuState) {
            case INIT:
                Gdx.input.setInputProcessor(mainStage);
                mainStage.draw();
                mainStage.act(deltaTime);
            break;
            case LOGIN:
                //Gdx.input.setInputProcessor(loginStage);
            break;
            case REGISTER:
                //Gdx.input.setInputProcessor(loginStage);
            break;
            case CONFIG:
                //Gdx.input.setInputProcessor(configStage);
            break;

        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
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
        titleText.dispose();
    }

    private void mainMenuConstruct() {
        //Buttons
        TextButton playButton = new TextButton("Jugar", skin);
        TextButton finishButton = new TextButton("Salir", skin);
        //Table
        Table table = new Table();
        table.setFillParent(true);
        //Table interns
        table.row().padBottom(20);
        table.add(playButton).width(200).height(60).padBottom(20);
        table.row();
        table.add(finishButton).width(200).height(60);
        table.debug();
        //Stage
        mainStage.addActor(table);
    }

    private void loginMenuConstruct() {
        //Labels
        //Text Fields
        //Buttons
        //Table
        //Table Interns
        //Stage
    }

    private void registerMenuConstruct() {
        //Labels
        //Text Fields
        //Buttons
        //Table
        //Table Interns
        //Stage
    }

    private void configMenuConstruct() {
        //Labels
        //Text Fields
        //Buttons
        //Table
        //Table Interns
        //Stage
    }
}
