package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
    private static Music music;

    public Sounds(){
        music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Music/happytown.ogg"));
        music.setVolume(GameHandler.musicVolume);
    }

    public static void create(){
        music.setLooping(true);
        music.play();
    }

    public static void dispose(){
        music.dispose();
    }
}
