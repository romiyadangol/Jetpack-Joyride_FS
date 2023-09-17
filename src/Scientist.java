import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Scientist extends Rectangle {
    public static final int LEFT = 0, RIGHT = 1;
    private final int BLANK = 0x00000000;

    private BufferedImage scientistWalking1 = JetpackJoyridePanel.loadBuffImg("scientist_stationary.png");
    private BufferedImage scientistWalking2 = JetpackJoyridePanel.loadBuffImg("scientist_moving.png");
    private BufferedImage scientistCrouching = JetpackJoyridePanel.loadBuffImg("scientist_crouching.png");
    private BufferedImage scientistFainting = JetpackJoyridePanel.loadBuffImg("scientist_fainting.png");
    private BufferedImage scientistFaintingUpsideDown = JetpackJoyridePanel.loadBuffImg("scientist_fainting_upsidedown.png");

    private int x, y;
    private int dir;
    private static final int speed = 5;
    private int width, height;

    private static boolean isMoving = true;
    private boolean walking, crouching, fainting;  // the scientist's different phases: walking, crouching, fainting
    private boolean flipped = false;               // if the scientist's image needs to be flipped upside down
    private boolean isAbleToCrouch = false;
    private boolean canPlayFaintingSound = true;

    private static int maxWalkingPoseCount = 4;    // tells when to reset walking pose count back to 0
    private int walkingPoseCount = 0;              // tells when to switch barry's legs when walking (0, 1 - one leg forward, 2, 3 - other leg forward)

    public Scientist(int ddir, int ccrouch) {
        super();
        walking = true;
        crouching = false;
        fainting = false;

        dir = ddir;

        // only allows some of the scientist to crouch:
        if(ccrouch == 0) {
            isAbleToCrouch = true;
        }

        if(dir == RIGHT) {
            // flips the images to face right:
            scientistWalking1 = JetpackJoyridePanel.flipImage(scientistWalking1);
            scientistWalking2 = JetpackJoyridePanel.flipImage(scientistWalking2);
            scientistCrouching = JetpackJoyridePanel.flipImage(scientistCrouching);
            scientistFainting = JetpackJoyridePanel.flipImage(scientistFainting);
            scientistFaintingUpsideDown = JetpackJoyridePanel.flipImage(scientistFaintingUpsideDown);
        }

        width = scientistWalking1.getWidth(null);
        height = scientistWalking1.getHeight(null);

        x = JetpackJoyridePanel.WIDTH;
        y = JetpackJoyridePanel.HEIGHT - height - 100;

        setBounds(x, y, width, height);
    }

    // Getter and setter methods:
    public int getHitByLaserFallingDirection() {   // if the scientist hits a laser, they fall in the opposite direction
        if(dir == RIGHT) {
            return LEFT;
        }
        else {
            return RIGHT;
        }
    }
    public int getDir() {
        return dir;
    }
    protected Rectangle getCollision(Rectangle rect2) {
        Area a1 = new Area(this);
        Area a2 = new Area(rect2);
        a1.intersect(a2);
        return a1.getBounds();
    }
    public void crouch() { // makes scientist crouch
        walking = false; crouching = true; fainting = false;
    }
    public void walk() { // makes scientist walk
        walking = true; crouching = false; fainting = false;
    }
    public void faint(int direction) { // makes scientist faint
        if(dir == direction) {
            flipped = true;
        }

        walking = false; crouching = false; fainting = true;

        playFaintingSound();
    }
    public BufferedImage getImage() {
        if(walking) {
            if(walkingPoseCount > maxWalkingPoseCount/2) {   // one leg forward
                return scientistWalking1;
            }
            else {                                           // other leg forward
                return scientistWalking2;
            }
        } else if (crouching) {
            return scientistCrouching;
        } else {
            if(flipped) {
                return scientistFaintingUpsideDown;
            }
            else {
                return scientistFainting;
            }
        }
    }
    public boolean isFainted() {
        return fainting;
    }
    public boolean canCrouch() {
        return isAbleToCrouch;
    }
    public static void stopMoving() {
        isMoving = false;
    }

    // plays the sound of the scientist fainting:
    public void playFaintingSound() {
        if(canPlayFaintingSound) {
            SoundPlayer.playSoundEffect(SoundPlayer.scientistFainting, 0);
            canPlayFaintingSound = false;
        }
    }

    public boolean collidesWith(Zapper zapper) {
        if (intersects(zapper)) {                                                                                     // checks if the boundaries intersect
            Rectangle intersectBounds = getCollision(zapper);                                                         // calculates the collision overlay
            if (!intersectBounds.isEmpty()) {
                // Check all the pixels in the collision overlay to determine
                // if there are any non-alpha pixel collisions...
                for (int x = intersectBounds.x; x < intersectBounds.x + intersectBounds.width; x++) {
                    for (int y = intersectBounds.y; y < intersectBounds.y + intersectBounds.height; y++) {
                        int scientistPixel = getImage().getRGB(x - (int) getX(), y - (int) getY());
                        int zapperPixel = zapper.getImage().getRGB(x - (int)zapper.getX(), y - (int)zapper.getY());

                        // 255 is completely transparent, you might consider using something
                        // a little less absolute, like 225, to give you a sligtly
                        // higher hit right, for example...
                        if (scientistPixel != BLANK && zapperPixel != BLANK) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void move() {
        int dx;
        if(walking) {                                    // if the scientist is walking
            if(dir == LEFT) {                            // if the scientist is facing left
                dx = JetpackJoyridePanel.speedX-speed;   // the scientist moves left
            }
            else {                                       // if the scientist is facing right
                dx = JetpackJoyridePanel.speedX+speed;   // the scientist moves right
            }
        }
        else {                                           // if the scientist isn't walking
            dx = JetpackJoyridePanel.speedX;             // the scientist moves with the background (looks like they are stationary)
        }
        translate(dx, 0);
        x += dx;
    }

    public void draw(Graphics g) {
        if(walking) {
            if(isMoving) {
                walkingPoseCount++;
            }
            if(walkingPoseCount > maxWalkingPoseCount/2) {
                g.drawImage(scientistWalking1, x, y, null);
            }
            else {
                g.drawImage(scientistWalking2, x, y, null);
            }
            if(walkingPoseCount > maxWalkingPoseCount) {
                walkingPoseCount = 0;
            }
        }
        else if(crouching) {
            g.drawImage(scientistCrouching, x, y, null);
        }
        else if(fainting) {
            if(flipped) {
                g.drawImage(scientistFaintingUpsideDown, x, y, null);
            }
            else {
                g.drawImage(scientistFainting, x, y, null);
            }
        }
    }
}
