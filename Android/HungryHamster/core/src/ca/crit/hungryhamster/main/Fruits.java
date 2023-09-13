package ca.crit.hungryhamster.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum Fruits {
    BANANA,
    APPLE,
    GRAPE,
    GREEN_APE,
    PINEAPPLE,
    KIWI,
    CHERRY,
    STRAWBERRY;

    private TextureRegion texture;
    private float Yposition;
    public static final int totalFruits = 8;
    private Fruits() {
        Texture image = new Texture(Gdx.files.internal("Food/food.png"));
        TextureRegion[][] tmp = TextureRegion.split(image, image.getWidth()/4, image.getHeight()/2);
        switch (this.ordinal()) {
            case 0: //BANANA
                this.texture = tmp[0][0];
            break;
            case 1: //APPLE
                this.texture = tmp[0][1];
            break;
            case 2: //GRAPE
                this.texture = tmp[0][2];
            break;
            case 3: //GREEN_APE
                this.texture = tmp[0][3];
            break;
            case 4: //PINEAPPLE
                this.texture = tmp[1][0];
            break; //KIWI
            case 5:
                this.texture = tmp[1][1];
            break;
            case 6: //CHERRY
                this.texture = tmp[1][2];
            break;
            case 7: //STRAWBERRY
                this.texture = tmp[1][3];
        }
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void setYposition(float yposition) {
        Yposition = yposition;
    }
}
