package com.rogurea.view;

import com.rogurea.Roguera;
import com.rogurea.resources.GameResources;

import javax.sound.sampled.*;
import java.io.IOException;

public class GameSound {

    Clip clip;

    public void play(){
        this.play(0);
    }

    public void play(int n){
        if(Roguera.isSoundOn)
            clip.loop(n);
    }

    public void stop(){
        clip.stop();
    }

    public GameSound(String file){
        try {
            clip = AudioSystem.getClip();

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(GameResources.class.getResource(GameResources.SOUND_DIR+file));
            clip.open(audioInputStream);

            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-25.0f);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
