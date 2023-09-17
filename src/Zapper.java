import java.awt.*;
import java.awt.image.*;

public class Zapper extends Rectangle {
    public static final BufferedImage diagonal1Zapper = JetpackJoyridePanel.loadBuffImg("zapper_diagonal.png");
    public static final BufferedImage diagonal2Zapper = JetpackJoyridePanel.loadBuffImg("zapper_diagonal_2.png");
    public static final BufferedImage verticalZapper = JetpackJoyridePanel.loadBuffImg("zapper.png");
    public static final BufferedImage horizontalZapper = JetpackJoyridePanel.loadBuffImg("zapper_horizontal.png");

    private BufferedImage Zapper;

    private String type;                                        // type of zapper: horizontal, vertical, etc.

    public Zapper(String type, int X, int Y) {
        this.type = type;
        if(type == "diagonal1") {
            Zapper = diagonal1Zapper;
        } else if (type == "diagonal2") {
            Zapper = diagonal2Zapper;
        } else if (type == "vertical") {
            Zapper = verticalZapper;
        } else if (type == "horizontal") {
            Zapper = horizontalZapper;
        }

        width = Zapper.getWidth();
        height = Zapper.getHeight();

        setBounds(X, Y, Zapper.getWidth(), Zapper.getHeight());
    }

    // Getter and setter methods:
    public String getType() {
        return type;
    }
    public BufferedImage getImage() {
        return Zapper;
    }

    public void move() {
        translate(JetpackJoyridePanel.speedX, 0);               // zapper moves with the background (looks like it's stationary)
    }

    public void draw(Graphics g) {
        g.drawImage(Zapper, (int)getX(), (int)getY(), null);
    }
}
