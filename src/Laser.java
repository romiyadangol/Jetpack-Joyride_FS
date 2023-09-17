

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Laser extends Rectangle {                                                                      // laser uses Rectangle class to detect collisions with Barry and scientists
    private static final BufferedImage laser = JetpackJoyridePanel.loadBuffImg("laser.png");                // normal laser picture
    private static final BufferedImage flippedLaser = JetpackJoyridePanel.flipImage(laser);                 // flipped normal laser picture
    private static final BufferedImage firingLaser = JetpackJoyridePanel.loadBuffImg("laserfiring.png");    // firing laser picture
    private static final BufferedImage flippedFiringLaser = JetpackJoyridePanel.flipImage(firingLaser);     // flipped firing laser picture
    private static final int LASERSPEED = 5, LOADINGELLIPSESPEED = 3;                                       // laser speed coming into position, and speed of the loading ellipse closing down on laser
    private static final int FRAMESBEFOREPOSITION = 20, FRAMESBEFORECOOLDOWN = 40, FRAMESBEFOREOFF = 18;    // number of frames laser needs to stay in current state before moving onto next state
    private int frameNum;                                                                                   // keeps track of the number of frames at the current state

    public static final int LEFT = 0, RIGHT = 1;

    public static final int LASERBEAMGAP = 30;                                                              // keeps laser beam image aligned in the right spots on the lasers

    private boolean moving, warning, firing, cooling, off;                                                  // keeps track of the state of the laser

    private int ellipseRadiusX, ellipseRadiusY;                                                             // x and y radius of the loading ellipse
    private Ellipse2D.Double loadingEllipse;                                                                // ellipse appearing during warning state of the laser, gets progressively smaller until the laser fires
    private Point2D.Double loadingLineEndPoint;                                                             // stores one endpoint of the line appearing during warning state of the laser, shows user where the laser beam is located prior to laser firing

    private Point center;                                                                                   // center position of the laser
    private static final int WIDTH = laser.getWidth(null), HEIGHT = laser.getHeight(null);
    private int dir;                                                                                        // direction of laser during moving state

    public Laser(int ddir, int yy) {
        super();
        dir = ddir;

        moving = true; warning = false; firing = false; cooling = false; off = false;                       // the laser is moving

        center = new Point();

        if(dir == LEFT) {                                                                                   // if laser is heading left
            center.x = JetpackJoyridePanel.WIDTH + 50 + WIDTH/2;                                            // laser starts off and to the right of the screen
        } else {                                                                                            // if laser is heading right
            center.x = -50 - WIDTH/2;                                                                       // laser starts off and to the left of the screen
        }
        center.y = yy + HEIGHT/2;                                                                           // add half the height of the laser to the top left corner's y-coord to get y-coord of center

        frameNum = 0;
        ellipseRadiusX = 100;                                                                               // ellipse x and y radii start at 100 and 80 respectively
        ellipseRadiusY = 80;

        // initialize loading ellipse and loading line endpoint
        loadingEllipse = new Ellipse2D.Double();
        loadingLineEndPoint = new Point2D.Double();

        // sets the dimensions of the laser rectangle
        setBounds(center.x - WIDTH/2, center.y - HEIGHT/2, WIDTH, HEIGHT);
    }

    public Point2D getFiringEndPoint() {
        Point2D.Double endPoint = new Point2D.Double();                                                              // stores the endpoint of the laser beam
        if(dir == RIGHT) {
            endPoint.x = center.x - WIDTH/2 + firingLaser.getWidth();                                                // if the laser is heading right, the endpoint is the top right corner of the laser firing image
            endPoint.y = center.y - firingLaser.getHeight()/2 + LASERBEAMGAP;
        } else {
            endPoint.x = center.x + WIDTH/2 - firingLaser.getWidth();                                                // is the laser is heading left, the endpoint is the top left corner of the laser firing image
            endPoint.y = center.y - firingLaser.getHeight()/2 + LASERBEAMGAP;
        }
        return endPoint;
    }

    // getter methods:
    public Point2D getLoadingLineEndPoint() {
        return loadingLineEndPoint;
    }
    public boolean isWarning() {
        return warning;
    }
    public boolean isFiring() {
        return firing;
    }
    public boolean isOff() {
        return off;
    }

    public void move() {                                                                                    // moves laser and laser properties based on the current state of the laser
        if(moving && frameNum == FRAMESBEFOREPOSITION) {                                                    // if laser is moving and the frame count has reached the maximum number of frames before warning
            moving = false; warning = true;
            frameNum = 0;
            if(dir == RIGHT) {
                SoundPlayer.playSoundEffect(SoundPlayer.laserLoading, 0);                                   // plays laser loading sound effect only if direction is right to keep from playing twice in one laser pair
            }
        }
        else if(warning) {
            if(loadingEllipse.getWidth() <= getWidth() || loadingEllipse.getHeight() <= getHeight()) {      // if loading ellipse closed down on the laser
                warning = false; firing = true;                                                             // laser fires
                frameNum = 0;
                SoundPlayer.playSoundEffect(SoundPlayer.laserFiring, 3);                                    // play laser beam buzzing sound, looping 3 times
            }
        }
        else if(firing && frameNum == FRAMESBEFORECOOLDOWN) {                                               // if laser is firing and the frame count has reached the maximum number of frames before cooling
            firing = false; cooling = true;
            frameNum = 0;
            JetpackJoyridePanel.resetlaserBeamRects();                                                      // reset laser beam rectangle so no collisions happen when the laser isn't firing
        }
        else if(cooling && frameNum == FRAMESBEFOREOFF) {
            cooling = false; off = true;
            frameNum = 0;
        }

        if(moving) {
            if(dir == RIGHT) {
                center.translate(LASERSPEED, 0);                                                            // move right at laser speed
            } else {
                center.translate(-LASERSPEED, 0);                                                           // move left at laser speed
            }
            setLocation(center.x - WIDTH/2, center.y - height/2);                                           // moves laser rectangle to laser's current position
        }
        else if(warning) {
            loadingEllipse.setFrame(center.x - ellipseRadiusX, center.y - ellipseRadiusY, 2*ellipseRadiusX, 2*ellipseRadiusY);              // sets loading ellipse dimensions and sets position to be centered at the laser's center
            if(dir == RIGHT) {
                loadingLineEndPoint.x = center.x + ellipseRadiusX;                                                                          // if laser direction is right, loadingLineEndPoint's x-coord is the right of the ellipse
            }
            else {
                loadingLineEndPoint.x = center.x - ellipseRadiusX;                                                                          // if laser direction is left, loadingLineEndPoint's x-coord is the left of the ellipse
            }
            loadingLineEndPoint.y = center.y;

            // decrease ellipse x and y radii
            ellipseRadiusX -= LOADINGELLIPSESPEED;
            ellipseRadiusY -= LOADINGELLIPSESPEED;
        }
        else if(firing) {
            // centers the firingLaser image based on the normal laser image
            if(dir == RIGHT) {
                setBounds(center.x - WIDTH/2 , center.y - firingLaser.getHeight()/2, firingLaser.getWidth(), firingLaser.getHeight());
            }
            else {
                setBounds(center.x + WIDTH/2 - firingLaser.getWidth(), center.y - firingLaser.getHeight()/2, firingLaser.getWidth(), firingLaser.getHeight());
            }
        }
        else if(cooling) {
            if(dir == RIGHT) {
                center.translate(-LASERSPEED, 0);                                                                                         // if laser is cooling and the laser's direction is right, it moves left to get off the screen
            } else {
                center.translate(LASERSPEED, 0);                                                                                          // if laser is cooling and the laser's direction is left, it moves right to get off the screen
            }
            setBounds(center.x - laser.getWidth(null)/2, center.y - laser.getHeight(null)/2, WIDTH, height);                              // updates location and resets size to the normal lazer's size
        }
        frameNum++;                                                                                                                       // increase frame count by one
    }
    public void drawNormalLasers(Graphics g) {                                                                                            // draws the normal lasers used in moving, warning, and cooldown states
        if(dir == RIGHT) {
            g.drawImage(laser, center.x - WIDTH/2, center.y - height/2, null);                                                            // if the laser's direction is right, draw the normal laser image at the current position
        }
        else {
            g.drawImage(flippedLaser, center.x - WIDTH/2, center.y - height/2, null);                                                     // if the laser's direction is left, draw the flipped normal laser image at the current position
        }
    }
    public void draw(Graphics g) {
        if(moving) {
            drawNormalLasers(g);                                                                                                          // if laser is moving draw the standard lasers
        }
        else if(warning) {
            drawNormalLasers(g);                                                                                                          // if laser is warning draw the standard lasers

            // draw the loading ellipse
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.draw(loadingEllipse);

        }
        else if(firing) {
            // draws the firing lasers centered based on the normal laser image
            if(dir == RIGHT) {
                g.drawImage(firingLaser, center.x - WIDTH/2 , center.y - firingLaser.getHeight()/2, null);
            }
            else {
                g.drawImage(flippedFiringLaser, center.x + WIDTH/2 - flippedFiringLaser.getWidth(), center.y - flippedFiringLaser.getHeight(null)/2, null);
            }
        }
        else if(cooling) {
            drawNormalLasers(g);                                                                                                         // if laser is cooling draw the standard lasers
        }
    }
}
