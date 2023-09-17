import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class Coin extends Rectangle {
    private static BufferedImage coinSpriteImage = JetpackJoyridePanel.loadBuffImg("coinsprite.png");
    private static final int NUMSPRITES = 6;                                                                               // number of sprites in the image
    public static final int WIDTH = coinSpriteImage.getWidth()/6, HEIGHT = coinSpriteImage.getHeight(), GAP = WIDTH + 2;
    private static BufferedImage[] sprites = getSprites(coinSpriteImage, NUMSPRITES);
    private static final Random rand = new Random();

    private static boolean isRotating;  // for if the coin is rotating or not
    private int currentSprite, x, y;

    public Coin(int xx, int yy) {
        super(xx, yy, sprites[0].getWidth(), sprites[0].getHeight());
        x = xx;
        this.y = yy;
        currentSprite = rand.nextInt(NUMSPRITES);                     // chooses a random sprite out of the 6 sprites in the image
        isRotating = true;                                            // the coin is rotating
    }
    // Getter methods:
    public BufferedImage getImage() {
        return sprites[currentSprite];
    }
    private static BufferedImage[] getSprites(BufferedImage spriteSheet, int numSprites) {
        BufferedImage[] sprites = new BufferedImage[numSprites];
        
        for(int i = 0; i < numSprites; i++) {
            sprites[i] = spriteSheet.getSubimage(i*WIDTH, 0, WIDTH, HEIGHT);
        }
        return sprites;
    }

    // translates the coin:
    public void translateCoin(int xx, int yy) {
        x += xx;
        y += yy;
        translate(xx, yy);
    }

    public static void stopRotating() {                     // stops the coin from rotating
        isRotating = false;
    }

    public void move() {
        if(isRotating) {
            currentSprite = (currentSprite+1)%NUMSPRITES;   // changes the sprite to the next one over (makes the coin appear as if it is spinning)
        }
        translateCoin(JetpackJoyridePanel.speedX, 0);
    }
    public void draw(Graphics g) {
        g.drawImage(sprites[currentSprite], x, y, null);
    }
}
