package GameV1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.math.*;
import java.io.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.viewport.*;
import java.util.Random;

public class GameCore extends ApplicationAdapter implements InputProcessor {

    //Temp Variables
    double dR1S = 1, dR2S = 1, dR3S = 1;
    
    Random rand = new Random();
    SpriteBatch batch;
    Texture imgReticle, imgSprite, imgObstacle, imgBg, imgPause, imgBox, imgMenu;
    Sprite spReticle, spChar, spObs1, spObs2, spObs3, spObs4, spObs5, spBG, spBox, spMenuBG;
    int nCursorX, nCursorY, nWindW, nWindH, nRockX1 = -90, nRockX2 = -60, nRockX3 = -30;
    float fCharRot, fCharMove, fCharAdditive, fPosX, fPosY;
    boolean isTouch, isPaused, isMenu = true, isMusicOn, isMMusic, isMusicEnable, isMusicClassic = false;
    private ExtendViewport viewport;
    Music menuMusic, bgMusic, menuMusicFly, menuMusicSci, bgMusicFly, bgMusicSci;
    GameCore game;
    EntityPlayer objPlayer;

    @Override
    public void create() {
        nWindW = Gdx.graphics.getWidth();
        nWindH = Gdx.graphics.getHeight();
        OrthographicCamera camera = new OrthographicCamera(nWindW, nWindH);
        camera.setToOrtho(true, nWindW, nWindH);
        if (Gdx.app.getType() == ApplicationType.Android) {
            int ANDROID_WIDTH = nWindW;
            int ANDROID_HEIGHT = nWindH;
            camera = new OrthographicCamera(ANDROID_WIDTH, ANDROID_HEIGHT);
            camera.translate(ANDROID_WIDTH / 2, ANDROID_HEIGHT / 2);
            camera.update();
        } else {
            camera = new OrthographicCamera(nWindW, nWindH);
            camera.translate(nWindW / 2, nWindH / 2);
            camera.update();
        }
        batch = new SpriteBatch();
        imgMenu = new Texture("Main_Menu.png");
        imgPause = new Texture("pausedImg.png");
        imgReticle = new Texture("badlogic.jpg");
        imgSprite = new Texture("characterSprite0.png");
        imgObstacle = new Texture("rockObstacleL.png");
        imgBg = new Texture("imgBG.jpg");
        imgBox = new Texture("RectBarr.jpg");
        spMenuBG = new Sprite(imgMenu);
        spMenuBG.setSize(nWindW, nWindH);
        spBox = new Sprite(imgBox);
        spBox.setScale(nWindW, nWindH/15);
        spObs1 = new Sprite(imgObstacle);
        spObs2 = new Sprite(imgObstacle);
        spObs3 = new Sprite(imgObstacle);
        //spObs4 = new Sprite(imgobstacle);
        //spObs5 = new Sprite(imgobstacle);
        spBG = new Sprite(imgBg);
        spBG.setSize(nWindW, nWindH);
        spReticle = new Sprite(imgReticle);
        spReticle.setOrigin(spReticle.getWidth() / 2, spReticle.getHeight() / 2);
        spReticle.setSize(nWindW / 200, nWindW / 200);
        spChar = new Sprite(imgSprite);
        spChar.setCenter(spChar.getWidth(), spChar.getHeight());
        spChar.setSize(nWindW / 12/*5*/, nWindW / 15);
        Gdx.input.setInputProcessor(this);
        fPosY = nWindH / 2;
        menuMusicFly = Gdx.audio.newMusic(Gdx.files.internal("gliding_by_isaac_wilkins.ogg"));
        bgMusicFly = Gdx.audio.newMusic(Gdx.files.internal("gliding_by_isaac_wilkins_loop.ogg"));
        menuMusicSci = Gdx.audio.newMusic(Gdx.files.internal("lucs-200th_floor.ogg"));//Hint next time:
        bgMusicSci = Gdx.audio.newMusic(Gdx.files.internal("Astrum-Wormhole.ogg"));//Dont make track sets the same!
        menuMusic = menuMusicFly;
        bgMusic = bgMusicFly;
        objPlayer = new EntityPlayer();
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (isMenu == false) {
                isMenu = true;
            } else {
                System.exit(3);
            }
        }
        if (isMenu) {
            batch.begin();
            batch.draw(imgMenu, 0, 0, nWindW, nWindH);
            batch.end();
            if (menuMusic.isPlaying() == false) {
                bgMusic.stop();
                menuMusic.play();
                menuMusic.setLooping(true);//music code came from http://stackoverflow.com/questions/27767121/how-to-play-music-in-loop-in-libgdx
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                menuMusic.setLooping(false);
                menuMusic.stop();
                isMenu = false;
            }
        } else if (isMenu == false) {
            if (bgMusic.isPlaying() == false) {
                bgMusic.setLooping(true);
                bgMusic.play();//music code came from http://stackoverflow.com/questions/27767121/how-to-play-music-in-loop-in-libgdx
            }
            nCursorX = Gdx.input.getX();
            nCursorY = nWindH - Gdx.input.getY();
            fCharRot = findAngle(fPosX, fPosY, nWindW * 2 / 3, nCursorY);
            fCharMove = (nCursorY - fPosY) / 13;
            fPosY += fCharMove;
            spChar.setRotation(fCharRot - 90);
            fPosX = nWindW / 5;
            //if(Gdx.input.isKeyPressed(Keys.F11))Gdx.graphics.setDisplayMode(Gdx.graphics.);
            Gdx.gl.glClearColor(0.128f, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            fPosX = nWindW / 5;
            //batch.setTransformMatrix(game.getCamera().view);
            //batch.setProjectionMatrix(game.getCamera().projection);
            batch.begin();
            batch.draw(spBG, 0, 0, nWindW, nWindH);
            batch.draw(spBox, 0, nWindH);
            batch.draw(spBox, 0, 0);
            batch.draw(spObs1, nRockX1, nWindH - nWindH / 4 - spObs1.getHeight() / 2 + nWindH / 12);
            batch.draw(spObs2, nRockX2, nWindH / 2 - spObs2.getHeight() / 2);
            batch.draw(spObs3, nRockX3, nWindH / 2 - nWindH / 4 - spObs3.getHeight() / 2 - nWindH / 12);
            //batch.draw(spObs4, Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5, 45);
            //batch.draw(spObs5, Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5, 45);
            batch.draw(spChar, fPosX - spChar.getWidth() / 2, fPosY - spChar.getHeight() / 2, spChar.getOriginX(), spChar.getOriginY(), spChar.getHeight(), spChar.getWidth(), spChar.getScaleX(), spChar.getScaleY(), spChar.getRotation(), true);
            //batch.draw(spChar, fPosX - spChar.getWidth() / 2, fPosY - spChar.getHeight() / 2, spChar.getOriginX(), spChar.getOriginY(), spChar.getHeight(), spChar.getWidth(), spChar.getScaleX(), spChar.getScaleY(), spChar.getRotation(), true);
            batch.draw(spReticle, nWindW * 2 / 3, nCursorY - spReticle.getHeight() / 2, spReticle.getOriginX(), spReticle.getOriginY(), spReticle.getWidth(), spReticle.getHeight(), spReticle.getScaleX(), spReticle.getScaleY(), spReticle.getRotation());
            batch.end();
            fCharMove += nWindH/252;
            if(nRockX1 > -spObs1.getWidth()) {
                nRockX1-=dR1S;
            } else {
                dR1S = (rand.nextDouble()+1)*8;
                nRockX1 = nWindW + nWindW/10;
            }
            if(nRockX2 > -spObs2.getWidth()) {
                nRockX2-=dR2S;
            } else {
                dR2S = (rand.nextDouble()+1)*8;
                nRockX2 = nWindW + nWindW/10;
            }
            if(nRockX3 > -spObs3.getWidth()) {
                nRockX3-=dR3S;
            } else {
                dR3S = (rand.nextDouble()+1)*8;
                nRockX3 = nWindW + nWindW/10;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                menuMusic.stop();
                bgMusic.stop();
                if(isMusicClassic) {
                    menuMusic = menuMusicFly;
                    menuMusic.setVolume(2f);
                    bgMusic = bgMusicFly;
                    bgMusic.setVolume(2f);
                    isMusicClassic = false;
                } else {
                    menuMusic = menuMusicSci;
                    menuMusic.setVolume(0.2f);
                    bgMusic = bgMusicSci;
                    bgMusic.setVolume(0.2f);
                    isMusicClassic = true;
                }
            }
    }

    public float findAngle(double dX1, double dY1, double dX2, double dY2) {
        float fAngle;
        fAngle = (float) Math.toDegrees(Math.atan2(dY1 - dY2, dX1 - dX2));
        return fAngle;
    }

    @Override
    public void resize(int width, int height) {
        // viewport must be updated for it to work properly
        //viewport.update(width, height);
        //camera.update();
    }
    
    @Override
    public void dispose() {
        //img.dispose();
        //item.dispose();
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}


/*       Boolean fullScreen = Gdx.graphics.isFullscreen();
        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (fullScreen == true)
            Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
        else
            Gdx.graphics.setFullscreenMode(currentMode);
*/