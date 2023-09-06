package ca.crit.hungryhamster.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

import java.util.Objects;

import ca.crit.hungryhamster.GameHandler;
import ca.crit.hungryhamster.main.Background;
import ca.crit.hungryhamster.main.GameText;

public class MainMenu implements Screen{
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
    private final Skin skin;
    private final Stage mainStage, loginStage, registerStage, configStage;
    private final GameText titleText, whoPlaysText, registerText;
    //private final Sound clickButtonSound;

    public MainMenu() {
        mainStage = new Stage();
        loginStage = new Stage();
        registerStage = new Stage();
        configStage = new Stage();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(GameHandler.WORLD_WIDTH, GameHandler.WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        background = new Background();
        skin = new Skin(Gdx.files.internal("UISkin/uiskin.json"));
        titleText = new GameText("Hungry Hamster", Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        titleText.setX(10);
        titleText.setY(115);
        titleText.setScales(0.16f, 0.38f);
        whoPlaysText = new GameText("¿Quién juega?", Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        whoPlaysText.setX(15);
        whoPlaysText.setY(115);
        whoPlaysText.setScales(0.15f, 0.38f);
        registerText = new GameText("Registro", Gdx.files.internal("Fonts/logros.fnt"), Gdx.files.internal("Fonts/logros.png"), false);
        registerText.setX(23);
        registerText.setY(115);
        registerText.setScales(0.16f, 0.38f);
        //clickButtonSound = Gdx.audio.newSound(Gdx.files.internal("Sounds"));
        menuState = MenuState.INIT;
    }

    @Override
    public void show() {
        mainMenuConstruct();
        registerMenuConstruct();
        loginMenuConstruct();
        configMenuConstruct();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
        background.renderDynamicBackground(deltaTime, batch);
        background.renderStaticBackground(batch);
        switch (menuState) {
            case INIT:
                titleText.draw(batch);
            break;
            case LOGIN:
                whoPlaysText.draw(batch);
            break;
            case REGISTER:
                registerText.draw(batch);
            break;
            case CONFIG:
            break;
        }
        batch.end();

        switch (menuState) {
            case INIT:
                Gdx.input.setInputProcessor(mainStage);
                mainStage.draw();
                mainStage.act(deltaTime);
            break;
            case LOGIN:
                Gdx.input.setInputProcessor(loginStage);
                loginStage.draw();
                loginStage.act(deltaTime);
            break;
            case REGISTER:
                Gdx.input.setInputProcessor(registerStage);
                registerStage.draw();
                registerStage.act(deltaTime);
            break;
            case CONFIG:
                Gdx.input.setInputProcessor(configStage);
                configStage.draw();
                configStage.act(deltaTime);
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
        TextButton btnPlay = new TextButton("Jugar", skin);
        TextButton btnFinish  = new TextButton("Salir", skin);
        //Listeners
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuState = MenuState.LOGIN;
            }
        });
        btnFinish.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
        //Table
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, -25);
        //Table interns
        table.row().padBottom(20);
        table.add(btnPlay).width(200).height(60).padBottom(20);
        table.row();
        table.add(btnFinish).width(200).height(60);
        //table.debug();
        //Stage
        mainStage.addActor(table);
    }

    private void loginMenuConstruct() {
        //Labels
        Label lblID = new Label("ID", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField idField = new TextField("", skin);
        //Buttons
        TextButton btnNewPatient = new TextButton("Nuevo paciente", skin);
        TextButton btnNext = new TextButton("Siguiente", skin);
        //Listeners
        btnNewPatient.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuState = MenuState.REGISTER;
            }
        });
        btnNext.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!Objects.equals(idField.getText(), "")) {
                    //Protect more the variable
                    //Search for the ID in database
                    GameHandler.playerID = idField.getText();
                    System.out.println("ID: " + GameHandler.playerID);
                    menuState = MenuState.CONFIG;
                }
                else {
                    lblError.setText("Coloca un ID");
                }
            }
        });
        //Table
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, -80);
        //Table Interns
        table.add(lblError).padBottom(10).colspan(2).center();
        table.row();
        table.add(lblID).width(30).height(50).padBottom(10).right();
        table.add(idField).width(300).height(50).padBottom(10).left();
        table.row().padBottom(100);
        table.add(btnNext).width(150).height(50).colspan(2);
        table.row();
        table.add(btnNewPatient).width(150).height(50).colspan(2).padLeft(-450);
        //table.debug();
        //Stage
        loginStage.addActor(table);
    }

    private void registerMenuConstruct() {
        final int lblPadRight = 150;
        final int fieldPadRight = 100;
        final int fieldHeight = 50, fieldWidth = 300;
        //Labels
        Label lblName = new Label("Nombre:", skin);
        Label lblAge = new Label("Edad:", skin);
        Label lblID = new Label("ID:", skin);
        Label lblGender = new Label("Genero:", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField fieldName = new TextField("", skin);
        TextField fieldAge = new TextField("", skin);
        TextField fieldID = new TextField("", skin);
        //Buttons
        TextButton btnAccept = new TextButton("Aceptar", skin);
        TextButton btnReturn = new TextButton("Regresar", skin);
        TextButton btnMale = new TextButton("Hombre", skin, "toggle");
        TextButton btnFemale = new TextButton("Mujer", skin, "toggle");
        //Listeners
        btnAccept.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //Check info
                if(fieldName.getText() != null && !fieldName.getText().equals("") && !fieldName.getText().equals(" ")) {
                    GameHandler.playerName = fieldName.getText().trim().toLowerCase();
                }
                else {
                    lblError.setText("Porfavor ingresa un nombre");
                }
                if(fieldID.getText() != null && !fieldID.getText().equals("")) {
                    GameHandler.playerID = fieldName.getText().trim().toLowerCase();
                }
                else {
                    lblError.setText("Porfavor ingresa un ID");
                }
                if(fieldAge.getText() != null && !fieldAge.getText().equals("")) {
                    try {
                        GameHandler.playerAge = Integer.parseInt(fieldAge.getText().trim());
                    }
                    catch (NumberFormatException ex) {
                        lblError.setText("Porfavor ingresa un numero en la edad");
                    }
                }
                else {
                    lblError.setText("Porfavor ingresa un numero");
                }
                if(GameHandler.playerGender == null) {
                    lblError.setText("Porfavor selecciona un genero");
                }
                //Connect to database and send info
                menuState = MenuState.CONFIG;
            }
        });
        btnReturn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuState = MenuState.LOGIN;
            }
        });
        btnMale.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameHandler.playerGender = "Male";
                btnFemale.setChecked(false);
            }
        });
        btnFemale.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameHandler.playerGender = "Female";
                btnMale.setChecked(false);
            }
        });
        //Table
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(-20, -50);
        //Table Interns
        table.add(lblError).height(50).width(50).padBottom(10).center();
        table.row();
        table.add(lblName).height(50).width(75).padLeft(lblPadRight).right();
        table.add(fieldName).height(fieldHeight).width(fieldWidth).colspan(2).padRight(fieldPadRight).left();
        table.row();
        table.add(lblAge).height(50).width(75).padLeft(lblPadRight).right();
        table.add(fieldAge).colspan(2).height(fieldHeight).width(fieldWidth).colspan(2).padRight(fieldPadRight).left();
        table.row();
        table.add(lblID).height(50).width(75).padLeft(lblPadRight).right();
        table.add(fieldID).height(fieldHeight).width(fieldWidth).colspan(2).padRight(fieldPadRight).left();
        table.row().padBottom(50);
        table.add(lblGender).height(50).width(75).padLeft(lblPadRight).right();
        table.add(btnMale).height(50).width(100).padLeft(20);
        table.add(btnFemale).height(50).width(100).padRight(120);
        table.row();
        table.add(btnReturn).height(50).width(150).padRight(0);
        table.add(btnAccept).height(50).width(150).padLeft(270).colspan(2);
        //table.debug();
        //Stage
        registerStage.addActor(table);
    }

    private void configMenuConstruct() {
        //Labels
        Label lblSteps = new Label("Numero de escalones", skin);
        Label lblTime = new Label("Tiempo (Min)", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField fieldSteps = new TextField("", skin);
        TextField fieldTime = new TextField("", skin);
        //Buttons
        TextButton btnPlay = new TextButton("Jugar", skin);
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!Objects.equals(fieldTime.getText(), "") && !Objects.equals(fieldSteps, "")) {
                    try {
                        GameHandler.numHouseSteps = Integer.parseInt(fieldSteps.getText().trim());
                        GameHandler.sessionTime = Integer.parseInt(fieldTime.getText().trim());
                        //Start the game
                        GameHandler.startGame = true;
                    }
                    catch (NumberFormatException ex) {
                        lblError.setText("Inserte numeros porfavor");
                    }

                }
            }
        });
        //Table
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, -30);
        //Table Interns
        table.add(lblError).padBottom(20).colspan(2);
        table.row();
        table.add(lblSteps).padRight(50);
        table.add(lblTime);
        table.row().padBottom(30);
        table.add(fieldSteps).width(200).height(50).padRight(50).padBottom(50);
        table.add(fieldTime).width(200).height(50).padBottom(50);
        table.row().padBottom(50);
        table.add(btnPlay).width(150).height(60).colspan(2);
        //table.debug();
        //Stage
        configStage.addActor(table);
    }
}
