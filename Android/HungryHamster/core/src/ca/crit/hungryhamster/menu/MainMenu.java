package ca.crit.hungryhamster.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
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
    private final Skin shadeSkin;
    private final String mainSkinDir = "UISkin/uiskin.json";
    private final String shadeSkinDir = "ShadeUISkin/uiskin.json";
    private final Stage mainStage, loginStage, registerStage, configStage;
    private final GameText titleText, whoPlaysText, registerText, configText;
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
        skin = new Skin(Gdx.files.internal(mainSkinDir));
        shadeSkin = new Skin(Gdx.files.internal(shadeSkinDir));
        titleText = new GameText("Hungry Hamster", 10, 115);
        whoPlaysText = new GameText("¿Quién juega?", 16, 115);
        whoPlaysText.setScales(0.15f, 0.38f);
        registerText = new GameText("Registro", 23, 115);
        configText = new GameText("Configura", 20, 120);
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
                configText.draw(batch);
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
        Label lblID = new Label("No.Carnet:", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField idField = new TextField("", skin);
        //Buttons
        TextButton btnNewPatient = new TextButton("Nuevo paciente", skin);
        TextButton btnNext = new TextButton("Siguiente", skin);
        TextButton btnExit = new TextButton("Salir", skin);
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
        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
        //Table
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, -80);
        //Table Interns
        table.add(lblError).padBottom(10).colspan(2).center();
        table.row();
        table.add(lblID).width(30).height(50).padBottom(10).right().padRight(60);
        table.add(idField).width(300).height(50).padBottom(10).padRight(130);
        table.row().padBottom(100);
        table.add(btnNext).width(150).height(50).colspan(2);
        table.row();
        table.add(btnExit).width(150).height(50);
        table.add(btnNewPatient).width(150).height(50).padLeft(50).right();
        //table.debug();
        //Stage
        loginStage.addActor(table);
    }

    private void registerMenuConstruct() {
        final int lblPadRight = 100;
        final int fieldPadRight = 100;
        final int fieldHeight = 50, fieldWidth = 300;
        //Labels
        Label lblName = new Label("Nombre:", skin);
        Label lblAge = new Label("Edad:", skin);
        Label lblID = new Label("No.Carnet:", skin);
        Label lblGender = new Label("Sexo:", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField fieldName = new TextField("", skin);
        TextField fieldAge = new TextField("", skin);
        TextField fieldID = new TextField("", skin);
        //Buttons
        TextButton btnAccept = new TextButton("Aceptar", skin);
        TextButton btnReturn = new TextButton("Regresar", skin);
        TextButton btnMale = new TextButton("Masculino", skin, "toggle");
        TextButton btnFemale = new TextButton("Femenino", skin, "toggle");
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
        btnMale.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                GameHandler.playerGender = "Male";
                btnFemale.setChecked(false);
            }
        });
        btnFemale.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
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
        table.add(lblID).height(50).width(75).padLeft(lblPadRight).right().padRight(10);
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
    private boolean fieldCheck(TextField fieldMaxStep, TextField fieldMinStep, Label lblError) {
        int fieldMaxCounts, fieldMinCounts;
        try {
            fieldMaxCounts = Integer.parseInt(fieldMaxStep.getText().trim());
            fieldMinCounts = Integer.parseInt(fieldMinStep.getText().trim());

            if(fieldMinCounts >= fieldMaxCounts) {
                lblError.setText("El escalon inferior no puede ser mayor o igual al superior");
                fieldMinCounts = fieldMaxCounts - 1;
                if(fieldMinCounts <= 0)
                    fieldMinCounts = 0;

                fieldMinStep.setText(String.valueOf(fieldMinCounts));
                return false;
            }
            else
                return true;
        }
        catch (NumberFormatException exception) {
            lblError.setText("Porfavor pon numeros");
            return false;
        }
    }
    private void configMenuConstruct() {
        final int fieldWidth = 200;
        final int fieldHeight = 54;
        final int btnWidth = 150;
        final int btnHeight = 60;
        //Labels
        Label lblMaxStep = new Label("Escalon superior", skin);
        Label lblMinStep = new Label("Escalon inferior", skin);
        Label lblTime = new Label("Tiempo (Mins)", skin);
        Label lblError = new Label("", skin);
        //Text Fields
        TextField fieldMaxStep = new TextField("10", skin);
        TextField fieldMinStep = new TextField("0", skin);
        TextField fieldTime = new TextField("1.00", skin);
        TextField fieldExtra = new TextField("", skin);
        fieldMaxStep.setAlignment(Align.center);
        fieldMinStep.setAlignment(Align.center);
        fieldTime.setAlignment(Align.center);
        fieldExtra.setAlignment(Align.center);
        //Buttons
        TextButton btnPlay = new TextButton("Jugar", skin);
        TextButton btnReturn = new TextButton("Regresar", skin);
        Button[] btnArrows = new Button[8];
        for(int i = 0; i < btnArrows.length; i+=2) { //Construct for btnArrows
            btnArrows[i] = new Button(shadeSkin, "left");
            btnArrows[i+1] = new Button(shadeSkin, "right");
        }
        //Tables
        Table mainTable = new Table();
        Table upperArrowsTable = new Table();
        Table lowerArrowsTable = new Table();
        Table timeArrowsTable = new Table();
        Table extraArrowsTable = new Table();
        //Tables characteristics
        mainTable.setFillParent(true);
        mainTable.setPosition(0, -40);
        //Checkboxes
        CheckBox cbExtraFruit = new CheckBox("Fruta extra", shadeSkin);
        //Trying to rotate
        /*
        for(Button i : btnArrows) {
            batch.flush();
            i.setTransform(true);
            i.rotateBy(-90);
            batch.flush();

        }
        Not working try by the atlas or json file
         */

        //Listeners
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!Objects.equals(fieldTime.getText(), "") && !Objects.equals(fieldMaxStep, "")) {
                    try {
                        GameHandler.maxStep = Integer.parseInt(fieldMaxStep.getText().trim());
                        GameHandler.minStep = Integer.parseInt(fieldMinStep.getText().trim());
                        GameHandler.numHouseSteps = GameHandler.maxStep - GameHandler.minStep;
                        GameHandler.sessionTime = Float.parseFloat(fieldTime.getText().trim());
                        if(!fieldExtra.getText().equals("")) {
                            GameHandler.extraStep = Integer.parseInt(fieldExtra.getText().trim());
                        }
                        GameHandler.countsToWin = GameHandler.numHouseSteps + GameHandler.extraStep;
                        //Start the game
                        if(fieldCheck(fieldMaxStep, fieldMinStep, lblError))
                            GameHandler.startGame = true;
                    }
                    catch (NumberFormatException ex) {
                        lblError.setText("Inserte numeros porfavor");
                    }

                }
            }
        });
        //Making the listeners of the arrow buttons
        for(int i = 0; i < btnArrows.length; i+=2) {
            int finalI = i; //In order to avoid memory leaks
            btnArrows[i].addListener(new ChangeListener() { //Up
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    final DecimalFormat df = new DecimalFormat("0.00");
                    int counter;
                    float time;
                    try {
                        if(finalI < 2) {
                            counter = Integer.parseInt(fieldMaxStep.getText().trim());
                            counter++;
                            if (counter >= 32)
                                counter = 32;
                            fieldMaxStep.setText(String.valueOf(counter));
                        }
                        else if (finalI < 4) {
                            time = Float.parseFloat(fieldTime.getText().trim());
                            time += 0.1f;
                            fieldTime.setText(String.valueOf(df.format(time)));
                        }
                        else if (finalI < 6){
                            counter = Integer.parseInt(fieldMinStep.getText().trim());
                            counter++;
                            fieldMinStep.setText(String.valueOf(counter));
                        }
                        else {
                            counter = Integer.parseInt(fieldExtra.getText().trim());
                            counter++;
                            if (counter >= 3)
                                counter = 3;
                            if(cbExtraFruit.isChecked())
                                fieldExtra.setText(String.valueOf(counter));
                        }
                        lblError.setText("");
                    }
                    catch (NumberFormatException exception) {
                        lblError.setText("Please select a number");
                    }
                    fieldCheck(fieldMaxStep, fieldMinStep, lblError);
                }
            });
            btnArrows[i+1].addListener(new ChangeListener() { //Down
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    final DecimalFormat df = new DecimalFormat("0.00");
                    int counter;
                    float time;
                    try {
                        if(finalI < 2) {
                            counter = Integer.parseInt(fieldMaxStep.getText().trim());
                            counter--;
                            if(counter <= 1)
                                counter = 1;
                            fieldMaxStep.setText(String.valueOf(counter));

                        }
                        else if (finalI < 4) {
                            time = Float.parseFloat(fieldTime.getText().trim());
                            time -= 0.1f;
                            if(time <= 0)
                                time = 0;
                            fieldTime.setText(String.valueOf(df.format(time)));
                        }
                        else if (finalI < 6){
                            counter = Integer.parseInt(fieldMinStep.getText().trim());
                            counter--;
                            if(counter <= 0)
                                counter = 0;
                            fieldMinStep.setText(String.valueOf(counter));
                        }
                        else {
                            counter = Integer.parseInt(fieldExtra.getText().trim());
                            counter--;
                            if(counter <= 1)
                                counter = 1;
                            if(cbExtraFruit.isChecked())
                                fieldExtra.setText(String.valueOf(counter));
                        }
                        lblError.setText("");
                    }
                    catch (NumberFormatException exception) {
                        lblError.setText("Porfavor coloque un numero valido");
                    }
                    fieldCheck(fieldMaxStep, fieldMinStep, lblError);
                }
            });
        }
        btnReturn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuState = MenuState.LOGIN;
            }
        });
        cbExtraFruit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(cbExtraFruit.isChecked()) {
                    fieldExtra.setDisabled(false);
                    if(fieldExtra.getText().toLowerCase().trim().equals("") || fieldExtra.getText().equals("0")) {
                        fieldExtra.setText("1");
                    }
                }
                else {
                    fieldExtra.setText("");
                    fieldExtra.setDisabled(true);
                }
            }
        });
        //Table Interns
        mainTable.add(lblError).padBottom(0).colspan(3);
        mainTable.row();
        mainTable.add(lblMaxStep);
        mainTable.add(new Actor()); //Not null member for blank space
        mainTable.add(lblTime);
        mainTable.row();
        mainTable.add(fieldMaxStep).width(fieldWidth).height(fieldHeight);
            upperArrowsTable.add(btnArrows[0]); //Up
            upperArrowsTable.row();
            upperArrowsTable.add(btnArrows[1]); //Down
        mainTable.add(upperArrowsTable).left();
        mainTable.add(fieldTime).width(fieldWidth).height(fieldHeight);
            timeArrowsTable.add(btnArrows[2]); //Up
            timeArrowsTable.row();
            timeArrowsTable.add(btnArrows[3]); //Down
        mainTable.add(timeArrowsTable);
        mainTable.row();
        mainTable.add(lblMinStep);
        mainTable.add(new Actor()); //Not null member for blank space
        mainTable.add(cbExtraFruit);
        mainTable.row().padBottom(20);
        mainTable.add(fieldMinStep).width(fieldWidth).height(fieldHeight);
            lowerArrowsTable.add(btnArrows[4]); //Up
            lowerArrowsTable.row();
            lowerArrowsTable.add(btnArrows[5]); //Down
        mainTable.add(lowerArrowsTable).left().padRight(50);
        mainTable.add(fieldExtra).width(fieldWidth).height(fieldHeight);
            extraArrowsTable.add(btnArrows[6]);
            extraArrowsTable.row();
            extraArrowsTable.add(btnArrows[7]);
        mainTable.add(extraArrowsTable).left();
        mainTable.row().padBottom(20);
        mainTable.add(btnPlay).width(btnWidth).height(btnHeight).colspan(3);
        mainTable.row();
        mainTable.add(btnReturn).width(btnWidth).height(btnHeight).left();
        //mainTable.debug();
        //Stage
        configStage.addActor(mainTable);
    }
}
