import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class SoundPlayer {
    public static final String background = "file:./sounds/background_music.wav";
    public static final String coin = "file:./sounds/coin_pickup.wav";
    public static final String laserFiring = "file:./sounds/laser_fire_lp.wav";
    public static final String laserLoading = "file:./sounds/laser_warning.wav";
    public static final String scientistFainting = "file:./sounds/scientist_faint.wav";
    public static final String barryWalking = "file:./sounds/foot_step.wav";
    public static final String barrySliding = "file:./sounds/fall_slide.wav";
    public static final String barryHurt = "file:./sounds/player_hurt_2.wav";
    public static final String barryZapped = "file:./sounds/barry_zapped.wav";
    public static final String missileWarning = "file:./sounds/missile_warning.wav";
    public static final String missileLaunch = "file:./sounds/missile_launch.wav";

    // used to play sound effects
    // soundToPlay is a string specifying the relative path of the sound effect file

    public static void playSoundEffect(String soundToPlay, int loopNum) {
        URL soundLocation;
        try {
            soundLocation = new URL(soundToPlay);
            Clip clip = null;
            clip = AudioSystem.getClip();
            AudioInputStream inputStream;
            inputStream = AudioSystem.getAudioInputStream(soundLocation);
            clip.open(inputStream);

            if(soundToPlay == background){
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-3.0f); 																// reduces volume of background music by 3 decibels.
            }
            clip.loop(loopNum);													// loops the clip loopNum times
            clip.start();														// play sound

            clip.addLineListener(new LineListener() {							// kill sound thread
                public void update (LineEvent evt) {
                    if (evt.getType() == LineEvent.Type.STOP) {
                        evt.getLine().close();
                    }
                }
            });

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

}