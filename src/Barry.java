import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Barry extends Rectangle {
    private static final BufferedImage barryWalking1 = JetpackJoyridePanel.loadBuffImg("barry.png");
    private static final BufferedImage barryWalking2 = JetpackJoyridePanel.loadBuffImg("barry2.png");
    private static final BufferedImage barryRising = JetpackJoyridePanel.loadBuffImg("barry_rising.png");
    private static final BufferedImage barryFalling = JetpackJoyridePanel.loadBuffImg("barry_falling.png");
    private static final BufferedImage barryForwards = JetpackJoyridePanel.loadBuffImg("barry_forwards.png");
    private static final BufferedImage barryDead = JetpackJoyridePanel.loadBuffImg("barry_dead.png");

    private static final BufferedImage barryWalkingShield1 = JetpackJoyridePanel.loadBuffImg("barry_shield.png");
    private static final BufferedImage barryWalkingShield2 = JetpackJoyridePanel.loadBuffImg("barry2_shield.png");
    private static final BufferedImage barryRisingShield = JetpackJoyridePanel.loadBuffImg("barry_rising_shield.png");
    private static final BufferedImage barryFallingShield = JetpackJoyridePanel.loadBuffImg("barry_falling_shield.png");

    private final int BLANK = 0x00000000;/////

    private boolean rising, falling, walking;         // Barry's phases: rising, falling, or walking
    private boolean shield, hit;                      // for if barry has a shield or is hit
    public boolean tumbling = false, dying = false;   // for if barry is tumbling or dying

    private static int maxWalkingPoseCount = 4;       // tells when to reset walking pose count back to 0
    private int walkingPoseCount = 0;                 // tells when to switch barry's legs when walking (0, 1 - one leg forward, 2, 3 - other leg forward)

    private int width, height;
    private static final int X = JetpackJoyridePanel.WIDTH/3;     // Barry's x-coordinate
    private int y;
    private double risingYSpeed = 40, fallingYSpeed = 40;         // Barry's rising and falling speeds
    public static double GRAVITY = 10;                          // gravity constant (in this game it's 0.99)

    public boolean hitFloor = false;                              // for if Barry hit the floor when tumbling
    private boolean canPlayBarrySlidingSound = true;              // plays sound for when Barry is sliding on the floor
    private static final int barryWalkingSoundMaxFrameCount = 3;
    private int barryWalkingSoundFrameCount = 0;
    private int barryRotationAngle = 270;                         // angle of rotation for when Barry is tumbling

    public Barry(String picName) {
        super();

        walking = true;                                           // Barry is walking
        falling = false;
        rising = false;

        width = barryWalking1.getWidth(null);
        height = barryWalking1.getHeight(null);

        y = JetpackJoyridePanel.WIDTH-height-21;
        setBounds(X, y, width, height);
    }

    // Getter and setter methods:
    public void activateShield() {
        shield = true;
    }
    public void deactivateShield() {
        shield = false;
    }
    public boolean hasShield() {
        return shield;
    }
    public boolean isHit() {
        return hit;
    }
    public void resetHit() {
        hit = false;
    }
    public void gotHit() {
        hit = true;
    }
    public int getDyingYSpeed() {
        return (int) fallingYSpeed;
    }
    protected Rectangle getCollision(Rectangle rect2) {   // finds the area of intersection between barry and rect2
        Area a1 = new Area(this);                         // area of barry
        Area a2 = new Area(rect2);                        // area of rect2
        a1.intersect(a2);                                 // finds the area of intersection and assigns it to a1
        return a1.getBounds();                            // returns the bounding rectangle of a1
    }
    public BufferedImage getImage() {
        if(walking) {
            if(walkingPoseCount > maxWalkingPoseCount/2) {   // one leg forward
                if(shield) {
                    return barryWalkingShield1;
                }
                else {
                    return barryWalking1;
                }
            }
            else {                                           // other leg forward
                if(shield) {
                    return barryWalkingShield2;
                }
                else {
                    return barryWalking2;
                }
            }
        } else if (rising) {
            if(shield) {
                return barryRisingShield;
            }
            else {
                return barryRising;
            }
        } else if (falling) {
            if(shield) {
                return barryFallingShield;
            }
            else {
                return barryFalling;
            }

            // barry does not have a shield if he is tumbling/dying:
        } else if (tumbling) {
            return barryForwards;
        } else {
            return barryDead;
        }
    }

    // Accelerates Barry when he is falling:
    public void accelerate(double accelerationX, double accelerationY) {
        JetpackJoyridePanel.speedX += accelerationX;
        fallingYSpeed += accelerationY;
    }

    public void move(boolean spacePressed) {
        if(spacePressed) {                                     // if the space bar is pressed
            rising = true; falling = false; walking = false;   // Barry rises
            translate(0, (int) -risingYSpeed);
            y -= risingYSpeed;
        }
        else {                                                 // if the space bar isn't pressed
            rising = false; falling = true; walking = false;   // Barry falls
            translate(0, (int) fallingYSpeed);
            y += fallingYSpeed;
        }

        if(y > JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT) {              // if Barry is above the floor of the lab
            y = JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT;
            setLocation(X, JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT);
        } else if (y < JetpackJoyridePanel.TOPBORDERHEIGHT) {                                           // if Barry is below the ceiling of the lab
            y = JetpackJoyridePanel.TOPBORDERHEIGHT;
            setLocation(X, JetpackJoyridePanel.TOPBORDERHEIGHT);
        }
        if(y == JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT) {             // if Barry is on the floor of the lab
            rising = false; falling = false; walking = true;                                            // Barry walks

            // plays the sound of Barry's footsteps:
            if(barryWalkingSoundFrameCount == barryWalkingSoundMaxFrameCount) {
                barryWalkingSoundFrameCount = 0;
                SoundPlayer.playSoundEffect(SoundPlayer.barryWalking, 0);
            }
            barryWalkingSoundFrameCount++;
        }
        setBounds(X, y, getImage().getWidth(), getImage().getHeight());
    }

    // for when Barry is dying:
    public void dying() {
        if(y < JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT) {    // if barry is above the floor
            rising = false; falling = false; walking = false; tumbling = true;                // Barry tumbles
            y += fallingYSpeed;
            accelerate(0, GRAVITY);                                                           // gravity accelerates barry downwards
        }
        else {                                                                                // if barry is on or below the floor of the lab
            playBarrySlidingSound();

            rising = false; falling = false; walking = false; tumbling = false; dying = true;  // barry is dying (he slides on the floor unconscious)

            y = JetpackJoyridePanel.HEIGHT-height-JetpackJoyridePanel.BOTTOMBORDERHEIGHT;     // change barry's y-position to be right on the floor
            setLocation(X, y);
            hitFloor = true;                                                                  // barry hit the floor
        }
        setBounds(X, y, getImage().getWidth(), getImage().getHeight());                       // update position, width, and height of barry rectangle
    }

    // plays the sound of Barry sliding on the floor:
    private void playBarrySlidingSound() {
        if(canPlayBarrySlidingSound) {
            SoundPlayer.playSoundEffect(SoundPlayer.barrySliding, 0);
            canPlayBarrySlidingSound = false;
        }
    }

    // checks if Barry has collided with a Zapper:
    public boolean collidesWith(Zapper zapper) {
        if (intersects(zapper)) {                                                                                       // checks if the boundaries intersect
            Rectangle intersectBounds = getCollision(zapper);                                                           // calculates the intersecting area of the collision
            if (!intersectBounds.isEmpty()) {                                                                           // if the intersection isn't empty, we have to check pixels to for a collision
                // iterate through all the pixels of the intersecting area
                for (int x = intersectBounds.x; x < intersectBounds.x + intersectBounds.width; x++) {
                    for (int y = intersectBounds.y; y < intersectBounds.y + intersectBounds.height; y++) {
                        int barryPixel = getImage().getRGB(x - (int) getX(), y - (int) getY());                         // colour of barry's image pixel at that pixel
                        int zapperPixel = zapper.getImage().getRGB(x - (int)zapper.getX(), y - (int)zapper.getY());     // colour of zapper's image pixel at that pixel

                        if (barryPixel != BLANK && zapperPixel != BLANK) {                                              // if both barry's and the zapper's pixels aren't transparent
                            return true;                                                                                // there is an intersection between barry and the zapper
                        }
                    }
                }
            }
        }
        return false;                                                                                                   // didn't intersect or didn't find any colliding pixels
    }

    public void draw(Graphics g) {
        if(walking) {
            walkingPoseCount++;
            if(walkingPoseCount > maxWalkingPoseCount/2) {                                                                                                                           // one leg forward
                setBounds(X, y, barryWalking1.getWidth(null), barryWalking1.getHeight(null));
                if(shield) {
                    g.drawImage(barryWalkingShield1, X - barryWalkingShield1.getWidth()/2 + (int)getWidth()/2, y - barryWalkingShield1.getHeight()/2 + (int)getHeight()/2, null);    // draws barry with a shield so that it's centered at the same point as barry without a shield
                }
                else {
                    g.drawImage(barryWalking1, X, y, null);
                }
            }
            else {                                                                                                                                                                   // other leg forward
                setBounds(X, y, barryWalking2.getWidth(null), barryWalking2.getHeight(null));
                if(shield) {
                    g.drawImage(barryWalkingShield2, X - barryWalkingShield2.getWidth()/2 + (int)getWidth()/2, y - barryWalkingShield2.getHeight()/2 + (int)getHeight()/2, null);    // draws barry with a shield so that it's centered at the same point as barry without a shield
                }
                else {
                    g.drawImage(barryWalking2, X, y, null);
                }
            }
            if(walkingPoseCount > maxWalkingPoseCount) {                // resets walking pose count
                walkingPoseCount = 0;
            }
        } else if (rising) {
            setBounds(X, y, barryRising.getWidth(null), barryWalking2.getHeight(null));
            if(shield) {
                g.drawImage(barryRisingShield, X - barryRisingShield.getWidth()/2 + (int)getWidth()/2, y - barryRisingShield.getHeight()/2 + (int)getHeight()/2, null);             // draws barry with a shield so that it's centered at the same point as barry without a shield
            }
            else {
                g.drawImage(barryRising, X, y, null);
            }
        } else if (falling) {
            setBounds(X, y, barryFalling.getWidth(null), barryWalking2.getHeight(null));
            if(shield) {
                g.drawImage(barryFallingShield, X - barryFallingShield.getWidth()/2 + (int)getWidth()/2, y - barryFallingShield.getHeight()/2 + (int)getHeight()/2, null);          // draws barry with a shield so that it's centered at the same point as barry without a shield
            }
            else {
                g.drawImage(barryFalling, X, y, null);
            }
        } else if(tumbling) {
            // rotates barry:
            AffineTransform identity = new AffineTransform();

            Graphics2D g2d = (Graphics2D)g;
            AffineTransform trans = new AffineTransform();
            trans.setTransform(identity);
            trans.translate(X, y);
            trans.rotate(Math.toRadians(barryRotationAngle));
            g2d.drawImage(getImage(), trans, null);

            barryRotationAngle += 10;                                                                           // rotates barry 10 more degrees to the right every frame
            barryRotationAngle = barryRotationAngle % 360;                                                      // keeps barry's angle of rotation between 0 and 360
        } else {
            g.drawImage(barryDead, X, y, null);
        }
    }
}
