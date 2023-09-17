import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.Clip;

public class JetpackJoyride extends JFrame{
    JetpackJoyridePanel game;

    public JetpackJoyride() {
        super("Jetpack Joyride");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new JetpackJoyridePanel();
        add(game);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the window full screen

//        pack();

        setVisible(true);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent windowEvent){
                System.exit(0);
            }
        });

//        Thread gameThread = new Thread(() ->{
//           while (true){
//               game.repaint();
//               try {
//                   Thread.sleep(160);
//               }catch (InterruptedException e){
//                   e.printStackTrace();
//               }
//           }
//        });
//        gameThread.start();
    }

    public static void main(String[] arguments) {
         new JetpackJoyride();

    }
}

// INTERFACE
class JetpackJoyridePanel extends JPanel implements MouseListener, ActionListener, KeyListener {
//    public static final int WIDTH = 1000, HEIGHT = 750;

public static final int WIDTH = 1538, HEIGHT = 950;

    // width and height of the panel
    public static final int TOPBORDERHEIGHT = 120, BOTTOMBORDERHEIGHT = 100;
//    private static final Image background = new ImageIcon("Images/background.png").getImage();  // background image (the lab)
private static final Image background = new ImageIcon("Images/background1600.png").getImage();  // background image (the lab)

    private static int backgroundX, backgroundY, reverseBackgroundX, reverseBackgroundY;
    public static int speedX;  // speed of the background

    private static final int LEFT = 0, RIGHT = 1;

    private static Font myFont;

    private static boolean[] allKeys;
    private Point mouse = new Point();
    private Random rand = new Random();

    private static Image laserBeamImage;
    private static ArrayList<Rectangle> laserBeamRects;

    public static Barry barry;
    private ArrayList<Zapper> zappers;
    private ArrayList<Scientist> scientists;
    private ArrayList<Missile> missiles;
    private ArrayList<Laser[]> lasers;
    private ArrayList<Coin> coins;

    private String currentStretch;        // current stretch (identifies which obstacle is appearing: coins, zappers, lasers)
    private int currentCoins;             // current amount of coins (amounts after each game)
    public static int currentRun;         // current distance ran
    private int longestRun;               // longest run distance
    private String longestRunInfo;        // information about the longest run (name and distance travelled in the form "name: distance")
    private int numOfShields;             // current number of shields (amounts after each game)
    private double missileProbability;    // the probability of a missile/laser appearing

    private String screen;                // current screen (start, game, game over)
    private Image startScreen;

    private static boolean isGameOver;               // if the game is over
    private static boolean newLongestRunPrompted;    // if the player is prompted for their name when they set a new longest run
    private static String buyShieldsMessage;         // the message that appears when the player is trying to buy shields
    private static int buyShieldMessageFrameCount;   // the number of frames the the buy shield message appears for
    private static final int shieldCost = 600;		 // the number of coins it costs to buy a shield

    private static Rectangle buyShieldRect = new Rectangle(WIDTH/2 + 125, HEIGHT/2 - 130, 200, 100);  // "buy shield" button
    private static Rectangle restartGameRect = new Rectangle(WIDTH/2 - 300, HEIGHT/2+50, 600, 180);   // "restart game" button

    // COIN formation: spells out COIN
    private final Coin[] COINFormation = {new Coin(Coin.GAP,0), new Coin(Coin.GAP*2,0), new Coin(Coin.GAP*3,0),
            new Coin(0,Coin.GAP), new Coin(0,Coin.GAP*2), new Coin(0,Coin.GAP*3),
            new Coin(Coin.GAP,Coin.GAP*4), new Coin(Coin.GAP*2,Coin.GAP*4), new Coin(Coin.GAP*3,Coin.GAP*4), // the "C" part

            new Coin(Coin.GAP*5,Coin.GAP), new Coin(Coin.GAP*5,Coin.GAP*2), new Coin(Coin.GAP*5,Coin.GAP*3),
            new Coin(Coin.GAP*8,Coin.GAP), new Coin(Coin.GAP*8,Coin.GAP*2), new Coin(Coin.GAP*8,Coin.GAP*3),
            new Coin(Coin.GAP*6,0), new Coin(Coin.GAP*7,0), new Coin(Coin.GAP*6,Coin.GAP*4), new Coin(Coin.GAP*7,Coin.GAP*4),	// the "O" part

            new Coin(Coin.GAP*10,0), new Coin(Coin.GAP*11,0), new Coin(Coin.GAP*12,0),
            new Coin(Coin.GAP*10,Coin.GAP*4), new Coin(Coin.GAP*11,Coin.GAP*4), new Coin(Coin.GAP*12,Coin.GAP*4),
            new Coin(Coin.GAP*11,Coin.GAP), new Coin(Coin.GAP*11,Coin.GAP*2), new Coin(Coin.GAP*11,Coin.GAP*3),	// the "I" part

            new Coin(Coin.GAP*14,0), new Coin(Coin.GAP*14,Coin.GAP), new Coin(Coin.GAP*14,Coin.GAP*2),
            new Coin(Coin.GAP*14,Coin.GAP*3), new Coin(Coin.GAP*14,Coin.GAP*4),
            new Coin(Coin.GAP*17,0), new Coin(Coin.GAP*17,Coin.GAP), new Coin(Coin.GAP*17,Coin.GAP*2),
            new Coin(Coin.GAP*17,Coin.GAP*3), new Coin(Coin.GAP*17,Coin.GAP*4),
            new Coin(Coin.GAP*15,Coin.GAP), new Coin(Coin.GAP*16,Coin.GAP*2),	// the "N" part

            new Coin(Coin.GAP*20,0), new Coin(Coin.GAP*21,0), new Coin(Coin.GAP*22,0),
            new Coin(Coin.GAP*19,Coin.GAP*2), new Coin(Coin.GAP*20,Coin.GAP*2), new Coin(Coin.GAP*21,Coin.GAP*2), new Coin(Coin.GAP*22,Coin.GAP*2),
            new Coin(Coin.GAP*19,Coin.GAP*4), new Coin(Coin.GAP*20,Coin.GAP*4), new Coin(Coin.GAP*21,Coin.GAP*4), new Coin(Coin.GAP*22,Coin.GAP*4),
            new Coin(Coin.GAP*19,Coin.GAP), new Coin(Coin.GAP*22,Coin.GAP*3)
    };
    // CLUMP formation: 3 clumps of coins
    private final Coin[] CLUMPFormation = {new Coin(Coin.GAP, 0), new Coin(Coin.GAP*2, 0), new Coin(Coin.GAP*3, 0),
            new Coin(Coin.GAP*4, 0), new Coin(Coin.GAP*5, 0), new Coin(Coin.GAP*6, 0),
            new Coin(0, Coin.GAP), new Coin(Coin.GAP, Coin.GAP), new Coin(Coin.GAP*2, Coin.GAP),
            new Coin(Coin.GAP*3, Coin.GAP), new Coin(Coin.GAP*4, Coin.GAP), new Coin(Coin.GAP*5, Coin.GAP),
            new Coin(Coin.GAP*6, Coin.GAP), new Coin(Coin.GAP*7, Coin.GAP), new Coin(Coin.GAP, Coin.GAP*2),
            new Coin(Coin.GAP*2, Coin.GAP*2), new Coin(Coin.GAP*3, Coin.GAP*2), new Coin(Coin.GAP*4, Coin.GAP*2),
            new Coin(Coin.GAP*5, Coin.GAP*2), new Coin(Coin.GAP*6, Coin.GAP*2), // 1st clump

            new Coin(Coin.GAP*10, Coin.GAP*3), new Coin(Coin.GAP*11, Coin.GAP*3), new Coin(Coin.GAP*12, Coin.GAP*3),
            new Coin(Coin.GAP*13, Coin.GAP*3), new Coin(Coin.GAP*14, Coin.GAP*3), new Coin(Coin.GAP*15, Coin.GAP*3),
            new Coin(Coin.GAP*9, Coin.GAP*4), new Coin(Coin.GAP*10, Coin.GAP*4), new Coin(Coin.GAP*11, Coin.GAP*4),
            new Coin(Coin.GAP*12, Coin.GAP*4), new Coin(Coin.GAP*13, Coin.GAP*4), new Coin(Coin.GAP*14, Coin.GAP*4),
            new Coin(Coin.GAP*15, Coin.GAP*4), new Coin(Coin.GAP*16, Coin.GAP*4), new Coin(Coin.GAP*10, Coin.GAP*5),
            new Coin(Coin.GAP*11, Coin.GAP*5), new Coin(Coin.GAP*12, Coin.GAP*5), new Coin(Coin.GAP*13, Coin.GAP*5),
            new Coin(Coin.GAP*14, Coin.GAP*5), new Coin(Coin.GAP*15, Coin.GAP*5), // 2nd clump

            new Coin(Coin.GAP*19, Coin.GAP*6), new Coin(Coin.GAP*20, Coin.GAP*6), new Coin(Coin.GAP*21, Coin.GAP*6),
            new Coin(Coin.GAP*22, Coin.GAP*6), new Coin(Coin.GAP*23, Coin.GAP*6), new Coin(Coin.GAP*24, Coin.GAP*6),
            new Coin(Coin.GAP*18, Coin.GAP*7), new Coin(Coin.GAP*19, Coin.GAP*7), new Coin(Coin.GAP*20, Coin.GAP*7),
            new Coin(Coin.GAP*21, Coin.GAP*7), new Coin(Coin.GAP*22, Coin.GAP*7), new Coin(Coin.GAP*23, Coin.GAP*7),
            new Coin(Coin.GAP*24, Coin.GAP*7), new Coin(Coin.GAP*25, Coin.GAP*7), new Coin(Coin.GAP*19, Coin.GAP*8),
            new Coin(Coin.GAP*20, Coin.GAP*8), new Coin(Coin.GAP*21, Coin.GAP*8), new Coin(Coin.GAP*22, Coin.GAP*8),
            new Coin(Coin.GAP*23, Coin.GAP*8), new Coin(Coin.GAP*24, Coin.GAP*8) // 3rd clump
    };
    // CURVE formation: 2 overlapping curves of coins
    private final Coin[] CURVEFormation = {new Coin(0, Coin.GAP*4),

            new Coin(Coin.GAP, Coin.GAP*3), new Coin(Coin.GAP*2, Coin.GAP*2), new Coin(Coin.GAP*3, Coin.GAP*2),
            new Coin(Coin.GAP*4, Coin.GAP), new Coin(Coin.GAP*5, Coin.GAP), new Coin(Coin.GAP*6, Coin.GAP),
            new Coin(Coin.GAP*7, 0), new Coin(Coin.GAP*8, 0), new Coin(Coin.GAP*9, 0),
            new Coin(Coin.GAP*10, 0), new Coin(Coin.GAP*11, Coin.GAP), new Coin(Coin.GAP*12, Coin.GAP),
            new Coin(Coin.GAP*13, Coin.GAP), new Coin(Coin.GAP*14, Coin.GAP*2), new Coin(Coin.GAP*15, Coin.GAP*2), new Coin(Coin.GAP*16, Coin.GAP*3),

            new Coin(Coin.GAP, Coin.GAP*5), new Coin(Coin.GAP*2, Coin.GAP*6), new Coin(Coin.GAP*3, Coin.GAP*6),
            new Coin(Coin.GAP*4, Coin.GAP*7), new Coin(Coin.GAP*5, Coin.GAP*7), new Coin(Coin.GAP*6, Coin.GAP*7),
            new Coin(Coin.GAP*7, Coin.GAP*8), new Coin(Coin.GAP*8, Coin.GAP*8), new Coin(Coin.GAP*9, Coin.GAP*8),
            new Coin(Coin.GAP*10, Coin.GAP*8), new Coin(Coin.GAP*11, Coin.GAP*7), new Coin(Coin.GAP*12, Coin.GAP*7),
            new Coin(Coin.GAP*13, Coin.GAP*7), new Coin(Coin.GAP*14, Coin.GAP*6), new Coin(Coin.GAP*15, Coin.GAP*6), new Coin(Coin.GAP*16, Coin.GAP*5),

            new Coin(Coin.GAP*17, Coin.GAP*4),

            new Coin(Coin.GAP*18, Coin.GAP*3), new Coin(Coin.GAP*19, Coin.GAP*2), new Coin(Coin.GAP*20, Coin.GAP*2),
            new Coin(Coin.GAP*21, Coin.GAP), new Coin(Coin.GAP*22, Coin.GAP), new Coin(Coin.GAP*23, Coin.GAP),
            new Coin(Coin.GAP*24, 0), new Coin(Coin.GAP*25, 0), new Coin(Coin.GAP*26, 0),
            new Coin(Coin.GAP*27, 0), new Coin(Coin.GAP*28, Coin.GAP), new Coin(Coin.GAP*29, Coin.GAP),
            new Coin(Coin.GAP*30, Coin.GAP), new Coin(Coin.GAP*31, Coin.GAP*2), new Coin(Coin.GAP*32, Coin.GAP*2), new Coin(Coin.GAP*33, Coin.GAP*3),

            new Coin(Coin.GAP*18, Coin.GAP*5), new Coin(Coin.GAP*19, Coin.GAP*6), new Coin(Coin.GAP*20, Coin.GAP*6),
            new Coin(Coin.GAP*21, Coin.GAP*7), new Coin(Coin.GAP*22, Coin.GAP*7), new Coin(Coin.GAP*23, Coin.GAP*7),
            new Coin(Coin.GAP*24, Coin.GAP*8), new Coin(Coin.GAP*25, Coin.GAP*8), new Coin(Coin.GAP*26, Coin.GAP*8),
            new Coin(Coin.GAP*27, Coin.GAP*8), new Coin(Coin.GAP*28, Coin.GAP*7), new Coin(Coin.GAP*29, Coin.GAP*7),
            new Coin(Coin.GAP*30, Coin.GAP*7), new Coin(Coin.GAP*31, Coin.GAP*6), new Coin(Coin.GAP*32, Coin.GAP*6), new Coin(Coin.GAP*33, Coin.GAP*5),

            new Coin(Coin.GAP*34, Coin.GAP*4)
    };
    // BARRY formation: spells out BARRY
    private final Coin[] BARRYFormation = {
            new Coin(0,0), new Coin(0,Coin.GAP), new Coin(0,Coin.GAP*2), new Coin(0,Coin.GAP*3), new Coin(0,Coin.GAP*4),
            new Coin(Coin.GAP,0), new Coin(Coin.GAP*2,0), new Coin(Coin.GAP*3,Coin.GAP),
            new Coin(Coin.GAP*2,Coin.GAP*2), new Coin(Coin.GAP,Coin.GAP*2),
            new Coin(Coin.GAP*3,Coin.GAP*3), new Coin(Coin.GAP*2,Coin.GAP*4), new Coin(Coin.GAP,Coin.GAP*4),		// the "B" part

            new Coin(Coin.GAP*4,Coin.GAP), new Coin(Coin.GAP*4,Coin.GAP*2), new Coin(Coin.GAP*4,Coin.GAP*3), new Coin(Coin.GAP*4,Coin.GAP*4),
            new Coin(Coin.GAP*7,Coin.GAP), new Coin(Coin.GAP*7,Coin.GAP*2), new Coin(Coin.GAP*7,Coin.GAP*3), new Coin(Coin.GAP*7,Coin.GAP*4),
            new Coin(Coin.GAP*5,0), new Coin(Coin.GAP*6,0), new Coin(Coin.GAP*5,Coin.GAP*2), new Coin(Coin.GAP*6,Coin.GAP*2),	// the "A" part

            new Coin(Coin.GAP*9,0), new Coin(Coin.GAP*9,Coin.GAP), new Coin(Coin.GAP*9,Coin.GAP*2), new Coin(Coin.GAP*9,Coin.GAP*3), new Coin(Coin.GAP*9,Coin.GAP*4),
            new Coin(Coin.GAP*12,Coin.GAP), new Coin(Coin.GAP*12,Coin.GAP*2), new Coin(Coin.GAP*12,Coin.GAP*4),
            new Coin(Coin.GAP*10,0), new Coin(Coin.GAP*11,0), new Coin(Coin.GAP*10,Coin.GAP*2), new Coin(Coin.GAP*11,Coin.GAP*2),	// the "R" part

            new Coin(Coin.GAP*14,0), new Coin(Coin.GAP*14,Coin.GAP), new Coin(Coin.GAP*14,Coin.GAP*2), new Coin(Coin.GAP*14,Coin.GAP*3), new Coin(Coin.GAP*14,Coin.GAP*4),
            new Coin(Coin.GAP*17,Coin.GAP), new Coin(Coin.GAP*17,Coin.GAP*2), new Coin(Coin.GAP*17,Coin.GAP*4),
            new Coin(Coin.GAP*15,0), new Coin(Coin.GAP*16,0), new Coin(Coin.GAP*15,Coin.GAP*2), new Coin(Coin.GAP*16,Coin.GAP*2),	// the "R" part

            new Coin(Coin.GAP*19,0), new Coin(Coin.GAP*19,Coin.GAP), new Coin(Coin.GAP*19,Coin.GAP*2),
            new Coin(Coin.GAP*22,0), new Coin(Coin.GAP*22,Coin.GAP), new Coin(Coin.GAP*22,Coin.GAP*2),
            new Coin(Coin.GAP*20,Coin.GAP*2), new Coin(Coin.GAP*21,Coin.GAP*2),
            new Coin(Coin.GAP*21,Coin.GAP*3), new Coin(Coin.GAP*21,Coin.GAP*4)		// the "Y" part
    };

    // zapper formation #1: 3 vertical zappers (high, low, middle), 2 horizontal zapper (high, low)
    private final Zapper[] zapperFormation1 = {new Zapper("vertical", 0, TOPBORDERHEIGHT+50), new Zapper("vertical", Zapper.verticalZapper.getWidth()+20, HEIGHT-BOTTOMBORDERHEIGHT-(int)this.getHeight()-50), new Zapper("vertical", 2*Zapper.verticalZapper.getWidth()+2*20, HEIGHT/2-(int)this.getHeight()/2), new Zapper("horizontal", 3*Zapper.verticalZapper.getWidth()+3*20, TOPBORDERHEIGHT+50), new Zapper("horizontal", 3*Zapper.verticalZapper.getWidth()+Zapper.horizontalZapper.getWidth()+4*20, HEIGHT-BOTTOMBORDERHEIGHT-(int)this.getHeight()-50)};
    // zapper formtion #2: 4 vertical zapper (high, low, middle, middle)
    private final Zapper[] zapperFormation2 = {new Zapper("vertical", 0, TOPBORDERHEIGHT+50), new Zapper("vertical", Zapper.verticalZapper.getWidth()+20, HEIGHT-BOTTOMBORDERHEIGHT-(int)this.getHeight()-50), new Zapper("vertical", 2*Zapper.verticalZapper.getWidth()+2*20, HEIGHT/2-(int)this.getHeight()/2), new Zapper("vertical", 2*Zapper.verticalZapper.getWidth()+Zapper.horizontalZapper.getWidth()+3*20, HEIGHT/2-(int)this.getHeight()/2)};
    // zapper formation #3: 1 diagonal zapper (high), 1 vertical zapper (middle), 1 diagonal zapper (middle), 2 horizontal zapper (high, low)
    private final Zapper[] zapperFormation3 = {new Zapper("diagonal1", 0, TOPBORDERHEIGHT+50), new Zapper("vertical", Zapper.diagonal1Zapper.getWidth()+20, HEIGHT/2-(int)this.getHeight()/2), new Zapper("diagonal2", Zapper.diagonal1Zapper.getWidth()+Zapper.verticalZapper.getWidth()+2*20, HEIGHT/2-(int)this.getHeight()/2), new Zapper("horizontal", Zapper.diagonal1Zapper.getWidth()+Zapper.verticalZapper.getWidth()+Zapper.diagonal2Zapper.getWidth()+3*20, TOPBORDERHEIGHT+50), new Zapper("horizontal", Zapper.diagonal1Zapper.getWidth()+Zapper.verticalZapper.getWidth()+Zapper.diagonal2Zapper.getWidth()+Zapper.horizontalZapper.getWidth()+4*20, HEIGHT-BOTTOMBORDERHEIGHT-(int)this.getHeight()-50)};
    // zapper formation #4: 1 diagonal zapper (high), 3 vertical zappers (high, low, middle)
    private final Zapper[] zapperFormation4 = {new Zapper("diagonal1", 0, TOPBORDERHEIGHT+50), new Zapper("vertical", Zapper.diagonal1Zapper.getWidth()+20, TOPBORDERHEIGHT+50), new Zapper("vertical", Zapper.diagonal1Zapper.getWidth()+Zapper.verticalZapper.getWidth()+2*20, HEIGHT-BOTTOMBORDERHEIGHT-(int)this.getHeight()-50), new Zapper("vertical", Zapper.diagonal1Zapper.getWidth()+2*Zapper.verticalZapper.getWidth()+3*20, HEIGHT/2-(int)this.getHeight()/2)};

    private Coin[][] coinFormations = {COINFormation, CLUMPFormation, CURVEFormation, BARRYFormation};
    private Zapper[][] zapperFormations = {zapperFormation1, zapperFormation2, zapperFormation3, zapperFormation4};

    public JetpackJoyridePanel() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check if the 'Escape' key (KeyEvent.VK_ESCAPE) is pressed
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Exit the game when the 'Escape' key is pressed
                    System.exit(0);
                }
            }
        });

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(this);
        addKeyListener(this);

        InputStream is = JetpackJoyridePanel.class.getResourceAsStream("NewAthleticM54.ttf");
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch(Exception e) {
            myFont = new Font("Courier New", 1, 30);
        }

        screen = "start";   // sets the screen to the starting screen
        initialize();

        Timer myTimer = new Timer(80, this);
        setFocusable(true);
        requestFocus();
        myTimer.start();
    }

    // initializes the objects:
    public void initialize() {
        speedX =-30;
        backgroundX = 0;
        backgroundY = 0;
        reverseBackgroundX = WIDTH;
        reverseBackgroundY = 0;

        isGameOver = false;													// the game is not over when we start

        newLongestRunPrompted = false;										// the user is not prompted for their name yet
        buyShieldsMessage = "";
        buyShieldMessageFrameCount = 0;

        laserBeamImage = JetpackJoyridePanel.loadBuffImg("laserbeam.png");

        allKeys = new boolean[KeyEvent.KEY_LAST+1];

        coins = new ArrayList<Coin>();
        barry = new Barry("barry");
        zappers = new ArrayList<Zapper>();
        scientists = new ArrayList<Scientist>();
        missiles = new ArrayList<Missile>();
        lasers = new ArrayList<Laser[]>();
        laserBeamRects = new ArrayList<Rectangle>();

        currentStretch = "";												// there are no obstacles when the game starts
        currentCoins = getCoins();											// gets the number of coins from the coins file
        currentRun = 0;														// the player starts at 0 metres
        longestRun = Integer.parseInt((getLongestRun().split(": "))[1]);	// gets the longest run from the longest run file
        longestRunInfo = getLongestRun();									// gets the longest run information from the longest run file
        numOfShields = getNumOfShields();									// gets the number of shields from the number of shields file
        missileProbability = 0.01;											// the initial probability of a missile/laser appearing is 1%

//        startScreen = new ImageIcon("Images/start_screen.png").getImage();
        startScreen = new ImageIcon("Images/start_screen1650.png").getImage();

    }

    // loads BufferedImages:
    public static BufferedImage loadBuffImg(String n) {
        try {
            return ImageIO.read(new File("Images/" + n));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    // gets the current amount of coins from the "Coins.txt" file:
    public Integer getCoins() {
        File file = new File("Coins.txt");

        try {
            if(file.length() == 0) return 0;						// if the file hasn't been made yet, that means that no coins have been collected yet
            else {
                Scanner myReader = new Scanner(file);
                int data = Integer.parseInt(myReader.nextLine());
                myReader.close();
                return data;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    // writes the current amount of coins to the "Coins.txt" file:
    public void setCoins() {
        File file = new File("Coins.txt");

        // creates the file if it hasn't been made yet:
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {}
        }

        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(String.valueOf(currentCoins));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gets the distance of the longest run from the "LongestRun.txt" file:
    public String getLongestRun() {
        File file = new File("LongestRun.txt");

        try {
            if(file.length() == 0) return "nobody: 0";	// if the file hasn't been made yet, that means that no one has set the longest run yet
            else {
                Scanner myReader = new Scanner(file);
                String line = myReader.nextLine();      // the line is the form "name: score"
                myReader.close();
                return line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "nobody: 0";
        }
    }
    // writes the distance of the longest run to the "LongestRun.txt" file:
    public void setLongestRun() {
        File file = new File("LongestRun.txt");

        // creates the file if it hasn't been made yet:
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {}
        }

        try {
            FileWriter myWriter = new FileWriter(file);
            String name = JOptionPane.showInputDialog("You set the longest run! What is your name?");
            myWriter.write(name + ": " + String.valueOf(currentRun));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gets the current number of shields from the "NumOfShields.txt" file:
    public Integer getNumOfShields() {
        File file = new File("NumOfShields.txt");

        try {
            if(file.length() == 0) return 0;							// if the file hasn't been made yet, that means that no shields have been purchased yet
            else {
                Scanner myReader = new Scanner(file);
                Integer num = Integer.parseInt(myReader.nextLine());
                myReader.close();
                return num;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
    // writes the current number of shields to the "NumOfShields.txt" file:
    public void setNumOfShields() {
        File file = new File("NumOfShields.txt");

        // creates the file if it hasn't been made yet:
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {}
        }

        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(String.valueOf(numOfShields));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main Game Loop
    @Override
    public void actionPerformed(ActionEvent e){
        if(screen.equals("game")) {
            move();
        }
        repaint();
    }

    // adds scientists randomly:
    public void addScientists() {
        boolean canSpawn = new Random().nextDouble() < 0.04;     // scientists have a 4% chance of appearing
        if(canSpawn) {
            int randDir = rand.nextInt(2);                       // chooses a random direction for the scientist to face (left: 0, right: 1)
            int canCrouch = rand.nextInt(2);                     // chooses whether the scientist can crouch or not (50% chance)
            scientists.add(new Scientist(randDir, canCrouch));
        }
    }
    // removes scientists if they are off the screen:
    public void removeScientists() {
        ArrayList<Scientist> removedScientists = new ArrayList<Scientist>();

        for(Scientist scientist : scientists) {
            if(scientist.getX() + scientist.getWidth() <= 0) {
                removedScientists.add(scientist);
            }
        }

        scientists.removeAll(removedScientists);
    }

    // adds missiles randomly:
    public void addMissiles() {
        boolean canSpawn = new Random().nextDouble() < missileProbability;
        if(canSpawn && lasers.isEmpty() && missiles.isEmpty()) {
            int randDir = rand.nextInt(2);                                   // chooses a random direction for the missile to face (left: 0, right: 1)
            missiles.add(new Missile(randDir));
        }
    }
    // removes missiles if they are off the screen:
    public void removeMissiles() {
        ArrayList<Missile> removedMissiles = new ArrayList<Missile>();

        for(Missile missile : missiles) {
            if(missile.getX() + missile.getWidth() <= 0 || missile.getX() >= WIDTH) {
                removedMissiles.add(missile);
            }
        }

        missiles.removeAll(removedMissiles);
    }

    // adds lasers:
    public void addLaser() {
        boolean validLaserY = false;												 // if the y-coordinate of the laser is valid
        int[] randYList = {120, 320, 520};											 // preset list of y-coordinates (such that Barry has enough room to fly between the lasers)
        int randY = 0;
        while(!validLaserY) {													 	 // while the y-coordinate is invalid
            validLaserY = true;
            randY = randYList[rand.nextInt(3)];										 // chooses a random y-coordinate from the preset list
            for(Laser[] laserPair : lasers) {
                if(laserPair[0].getY() == randY && laserPair[1].getY() == randY) {
                    validLaserY = false;
                }
            }
        }
        Laser[] randLaserPair = {new Laser(Laser.RIGHT, randY), new Laser(Laser.LEFT, randY)};
        lasers.add(randLaserPair);
    }
    // removes laser if they are off the screen:
    public void removeLasers() {
        ArrayList<Laser[]> removedLasers = new ArrayList<Laser[]>();

        for(Laser[] laserPair : lasers) {
            Laser laser1 = laserPair[0];
            Laser laser2 = laserPair[1];
            if(laser1.isOff() && laser2.isOff()) {
                removedLasers.add(laserPair);
            }
        }

        lasers.removeAll(removedLasers);
    }

    // adds zappers:
    public void addZappers() {
        Zapper[] randFormation = zapperFormations[rand.nextInt(zapperFormations.length)];                        // chooses a random formation from the zapper formations
        for(Zapper zapper : randFormation) {
            Zapper newZapper = new Zapper(zapper.getType(), (int) zapper.getX()+WIDTH+20, (int) zapper.getY());  // translates the zappers right
            zappers.add(newZapper);
        }
    }
    // removes zappers if they are off the screen:
    public void removeZappers() {
        ArrayList<Zapper> removedZappers = new ArrayList<Zapper>();

        for(Zapper zapper : zappers) {
            if(zapper.getX() + zapper.getWidth() <= 0) {
                removedZappers.add(zapper);
            }
        }

        zappers.removeAll(removedZappers);
    }

    // adds coins:
    public void addCoins() {
        Coin[] randFormation = coinFormations[rand.nextInt(coinFormations.length)];        // chooses a random formation from the coin formations (COIN, BARRY, curve, etc.)
        for(Coin coin: randFormation) {
            Coin newCoin = new Coin((int) coin.getX()+WIDTH+20, (int) coin.getY()+200);    // translates the coins right and up
            coins.add(newCoin);
        }
    }
    // removes coins if they are off the screen or if barry collected them:
    public void removeCoins() {
        ArrayList<Coin> removedCoins = new ArrayList<Coin>();

        for(Coin coin : coins) {
            if(coin.getX() + coin.getWidth() <= 0 || barry.intersects(coin)) {
                removedCoins.add(coin);
            }
        }

        coins.removeAll(removedCoins);
    }

    // resets the laser beam:
    public static void resetlaserBeamRects() {
        laserBeamRects.clear();
    }

    // flips images horizontally:
    public static BufferedImage flipImage(BufferedImage pic) {
        BufferedImage reversedPic = new BufferedImage(pic.getWidth(), pic.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int xx = pic.getWidth()-1; xx > 0; xx--){
            for(int yy = 0; yy < pic.getHeight(); yy++){
                reversedPic.setRGB(pic.getWidth()-xx, yy, pic.getRGB(xx, yy));
            }
        }
        return reversedPic;
    }

    // draws the scores:
    public void drawScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(myFont.deriveFont(Font.BOLD, 40f));
        g.drawString(currentRun + "M", 10, 40);                               // draws the distance of the current run

        g.setFont(myFont.deriveFont(Font.BOLD, 20f));
        g.drawString("NUMBER OF SHIELDS " + numOfShields, WIDTH - 200, 40);   // draws the number of shields the player has

        Color silver = new Color(232, 232, 232);
        g.setColor(silver);
        g.setFont(myFont.deriveFont(Font.BOLD, 30f));
        g.drawString("BEST: " + longestRun + "M", 10, 70);                    // draws the distance of the longest run

        Color gold = new Color(255, 255, 26);
        g.setColor(gold);
        g.setFont(myFont.deriveFont(Font.BOLD, 25f));
        g.drawString(currentCoins + "", 10, 95);                              // draws the current amount of coins the player has
    }
    // draws the final scores (shows up on the game over screen):
    public void drawFinalScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(myFont.deriveFont(Font.BOLD, 50f));
        g.drawString("YOU FLEW", WIDTH/2 - 430, HEIGHT/2 - 250);

        Color gold = new Color(255, 255, 26);
        g.setColor(gold);
        g.setFont(myFont.deriveFont(Font.BOLD, 100f));
        g.drawString(currentRun + "M", WIDTH/2 - 430, HEIGHT/2 - 150);          // draws the distance of the player's run

        if(newLongestRun()) {                                                   // if the player set a new longest run
            g.setColor(Color.BLUE);
            g.setFont(myFont.deriveFont(Font.BOLD, 30f));
            g.drawString("NEW BEST", WIDTH/2 - 180, HEIGHT/2 - 250);            // draws "new best"
        }

        g.setColor(Color.WHITE);
        g.setFont(myFont.deriveFont(Font.BOLD, 50f));
        g.drawString("AND COLLECTED", WIDTH/2 - 430, HEIGHT/2 - 90);

        g.setColor(gold);
        g.setFont(myFont.deriveFont(Font.BOLD, 40f));
        g.drawString(currentCoins + " COINS", WIDTH/2 - 430, HEIGHT/2 - 40);   // draws the amount of coins the player collected
    }
    // draws the leaderboard (aka the player with the longest run):
    public void drawLeaderBoard(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(myFont.deriveFont(Font.BOLD, 30f));
        g.drawString("HIGH SCORE:", WIDTH/2 + 50, HEIGHT/2 - 250);

        Color gold = new Color(255, 255, 26);
        g.setColor(gold);
        g.setFont(myFont.deriveFont(Font.BOLD, 50f));
        if(longestRunInfo.split(": ")[0].length() > 8) {                                                                                         // if the top scoring player has more than 8 letters in their name
            g.drawString(longestRunInfo.split(": ")[0].substring(0, 7) + ": " + longestRunInfo.split(": ")[1], WIDTH/2 + 50, HEIGHT/2 - 195);    // draws the first 8 letters of the players name and the distance they ran
        }
        else {
            g.drawString(longestRunInfo, WIDTH/2 + 50, HEIGHT/2 - 195);                                                                          // draws the player's full name and the distance they ran
        }
    }

    // lets the player buy shields:
    public void buyShield() {
        if(currentCoins >= shieldCost) {																								// if the player has enough coins to buy a shield, the player is allowed to buy a shield
            currentCoins -= shieldCost;
            numOfShields++;
            buyShieldsMessage = "You bought a shield! You now have "+numOfShields+" shields and "+currentCoins+" coins left.";
            setNumOfShields();
            setCoins();
        }
        else {																															// the player is not allowed to buy a shield
            buyShieldsMessage = "Sorry, you don't have enough coins to buy a shield. Shields cost "+shieldCost+" coins!";
        }
    }

    // for if the player has beat the previous longest run:
    public boolean newLongestRun() {
        if(currentRun > longestRun) {
            return true;
        }
        return false;
    }

    public void move(){
        if(isGameOver) {					// if the game is over
            if(!barry.hitFloor) {			// if barry is on the floor
                barry.dying();				// barry is dying
            } else {
                speedX *= 0.9999;			// slows the background down
                if(speedX == 0) {
                    screen = "game over";
                    setCoins();
                    setNumOfShields();
                    return;
                }
            }
        }

        // moves the background
        backgroundX += speedX;
        reverseBackgroundX += speedX;

        // if either background is completely off the screen, add WIDTH onto it to make it reappear to the right of the other background
        if(backgroundX <= -WIDTH) backgroundX = reverseBackgroundX+WIDTH;
        if(reverseBackgroundX <= -WIDTH) reverseBackgroundX = backgroundX + WIDTH;

        boolean barryCollided = false;												// deactivates shield only when barry is not colliding with anything

        for(Coin coin: coins) {
            coin.move();
            if(coin.getX() + coin.getWidth() <= 0 || barry.intersects(coin)) {
                if(barry.intersects(coin)) {
                    SoundPlayer.playSoundEffect(SoundPlayer.coin, 1);
                    currentCoins++;
                }
            }
        }
        removeCoins();

        if(!currentStretch.equals("lasers")) {
            addMissiles();
        }
        for(Missile missile : missiles) {
            missile.move();
        }
        removeMissiles();

        for(Zapper zapper : zappers) {
            zapper.move();
        }
        removeZappers();

        for(Laser[] laserPair : lasers) {
            laserPair[0].move();
            laserPair[1].move();

            if(laserPair[0].isFiring() && laserPair[1].isFiring()) {
                Point2D firingEndPoint1 = laserPair[0].getFiringEndPoint();
                Point2D firingEndPoint2 = laserPair[1].getFiringEndPoint();

                laserBeamImage = laserBeamImage.getScaledInstance((int) Math.abs(firingEndPoint1.getX() - firingEndPoint2.getX())+3, laserBeamImage.getHeight(null), Image.SCALE_DEFAULT);					// the +3 is there to fill in some pixels since the scaling isn't perfect
                Rectangle laserBeamRect = new Rectangle((int) Math.min(firingEndPoint1.getX(), firingEndPoint2.getX()), (int) firingEndPoint1.getY(), laserBeamImage.getWidth(null), laserBeamImage.getHeight(null));
                laserBeamRects.add(laserBeamRect);
            }
        }
        removeLasers();

        if(!isGameOver) {
            barry.move(allKeys[KeyEvent.VK_SPACE]);
        }

        addScientists();
        for(Scientist scientist : scientists) {
            scientist.move();
            if(!scientist.isFainted()) {																									// if the scientist has not already fainted
                if(JetpackJoyridePanel.barry.getY() > scientist.getY() - JetpackJoyridePanel.barry.getHeight() && scientist.canCrouch()) {	// if Barry is on the same plane as the scientist
                    scientist.crouch();																										// the scientist crouches (if they can)
                }
                else {					// if Barry is well above the scientists
                    scientist.walk();	// the scientist walks normally
                }
            }
            if(scientist.intersects(barry) && !scientist.isFainted()) {				// if barry hits a scientist
                scientist.faint(RIGHT);												// the scientist faints
            }
            for(Zapper zapper : zappers) {
                if(scientist.collidesWith(zapper) && !scientist.isFainted()) {		// if a zapper hits a scientists

                    // scientist faints in the opposite direction they were walking towards:
                    if(scientist.getDir() == LEFT) {
                        scientist.faint(RIGHT);
                    }
                    else {
                        scientist.faint(LEFT);
                    }
                }
            }
            for(Laser[] laserPair : lasers) {
                if(laserPair[0].isFiring() && laserPair[1].isFiring()){
                    if(scientist.intersects(laserPair[0])) {
                        scientist.faint(scientist.getHitByLaserFallingDirection());		// the scientist faints in the opposite direction they are walking in
                    }
                    else if(scientist.intersects(laserPair[1])) {
                        scientist.faint(scientist.getHitByLaserFallingDirection());		// the scientist faints in the opposite direction they are walking in
                    }
                    if(scientist.intersects(laserBeamRects.get(lasers.indexOf(laserPair))) && !scientist.isFainted()) {		// if the laser beam hits a scientist
                        scientist.faint(scientist.getHitByLaserFallingDirection());			// the scientist faints in the opposite direction they are walking in
                    }
                }
            }
            for(Missile missile : missiles) {
                if(scientist.intersects(missile)) {				// if a missile hits a scientist
                    scientist.faint(missile.getDirection());	// the scientist faints
                }
            }
        }
        removeScientists();
        if(!isGameOver) {
            for(Zapper zapper : zappers) {
                if(barry.collidesWith(zapper)) {									// if barry hits a zapper
                    if(!barry.hasShield()) {										// if barry doesn't have a shield
                        isGameOver = true;											// the game is over
                        SoundPlayer.playSoundEffect(SoundPlayer.barryZapped, 0);
                        SoundPlayer.playSoundEffect(SoundPlayer.barryHurt, 0);
                    }
                    barryCollided = true;
                    barry.gotHit();        // barry got hit
                }
            }
            for(Laser[] laserPair : lasers) {
                if(laserPair[0].isFiring() && laserPair[1].isFiring()) {
                    if(barry.intersects(laserBeamRects.get(lasers.indexOf(laserPair))) || barry.intersects(laserPair[0]) || barry.intersects(laserPair[1])) {   // if barry hits one of the lasers in the pair of lasers or the laser beam between the lasers
                        if(!barry.hasShield()) {                                                                                								// if barry doesn't have a shield
                            isGameOver = true;                                                                                  								// the game is over
                            SoundPlayer.playSoundEffect(SoundPlayer.barryHurt, 0);
                        }
                        barryCollided = true;
                        barry.gotHit();                                                                                         								// barry got hit
                    }
                }
            }
            for(Missile missile: missiles) {
                if(missile.isFiring()) {
                    if(barry.intersects(missile)) {                                  // if barry hits a missile
                        if(!barry.hasShield()) {                                     // if barry doesn't have a shield
                            isGameOver = true;                                       // the game is over
                            SoundPlayer.playSoundEffect(SoundPlayer.barryHurt, 0);
                        }
                        barryCollided = true;
                        barry.gotHit();                                              // barry got hit
                    }
                }
            }
            if(!barryCollided && barry.isHit() && barry.hasShield()) {   // the frame right after barry is hit
                barry.resetHit();
                barry.deactivateShield();
                numOfShields--;
            }
        }

        currentRun++;

        // for every stretch of 100 metres, the speed of the background and the probability of the missile increases:
        if(currentRun % 100 == 0) {
            speedX -= 3;
            missileProbability += 0.001;
        }

        // for every stretch of 80 metres, a new obstacle appears
        if(currentRun % 80 == 0 && currentRun != 0) {
            double randSelection = new Random().nextDouble();

            if(randSelection < 0.4) {                                // coins have a 40% chance of appearing
                addCoins();
            }
            else if(randSelection >= 0.4 && randSelection < 0.75) {   // zappers have a 35% chance of appearing
                addZappers();
            }
            else {                                                    // lasers have a 35% chance of appearing
                int randLaserAmount = rand.nextInt(2)+1;
                for(int i = 0; i < randLaserAmount; i++) {
                    addLaser();
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if(screen.equals("start")) {
            g.drawImage(startScreen, 0, 0, null);															// draws the starting screen
        }
        else if(screen.equals("game") || screen.equals("game over")) {
            g.drawImage(background, backgroundX, backgroundY, null);										// draws the background
            g.drawImage(background, reverseBackgroundX+WIDTH, reverseBackgroundY, -WIDTH, HEIGHT, null);

            for(Coin coin: coins) {
                coin.draw(g);
            }

            for(Zapper zapper : zappers) {
                zapper.draw(g);
            }

            for(Scientist scientist : scientists) {
                scientist.draw(g);
            }

            for(Missile missile : missiles) {
                missile.draw(g);
            }

            for(Laser[] laserPair : lasers) {
                laserPair[0].draw(g);																		// draw the first laser in the pair
                laserPair[1].draw(g);																		// draw the second laser in the pair

                if(laserPair[0].isWarning() && laserPair[1].isWarning()) {																						// if the lasers are in the warning positon
                    Line2D.Double warningBeam = new Line2D.Double(laserPair[0].getLoadingLineEndPoint(), laserPair[1].getLoadingLineEndPoint());				// get the middle of the right edge of the right-facing laser and the middle of the left edge of the left-facing laser to calculate the length of the warning beam

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.RED);
                    g2d.draw(warningBeam);																														// draws the warning beam in red
                }
                else if(laserPair[0].isFiring() && laserPair[1].isFiring()) {
                    Point2D firingEndPoint1 = laserPair[0].getFiringEndPoint();																														// gets the firing endpoints of both lasers
                    Point2D firingEndPoint2 = laserPair[1].getFiringEndPoint();

                    laserBeamImage = laserBeamImage.getScaledInstance((int) Math.abs(firingEndPoint1.getX() - firingEndPoint2.getX())+3, laserBeamImage.getHeight(null), Image.SCALE_DEFAULT);		// use the distance between the firing endpoints to calculate and scale the width of the laser beam (+3 is to fill in pixels since scaling isn't perfect)
                    g.drawImage(laserBeamImage, (int) Math.min(firingEndPoint1.getX(), firingEndPoint2.getX()), (int) firingEndPoint1.getY(), null);												// draw the laser beam image
                }
            }

            barry.draw(g);

            if(screen.equals("game")) {
                drawScores(g);
            }

            if(screen.equals("game over")) {
                // the objects stop moving:
                Scientist.stopMoving();
                Coin.stopRotating();

                // the backgorund dims:
                Color transparentBlack = new Color(0, 0, 0, 190);
                g.setColor(transparentBlack);
                g.fillRect(0, 0, WIDTH, HEIGHT);

                drawFinalScores(g);

                if(newLongestRun() && !newLongestRunPrompted) {   // if the player set a new longest run
                    newLongestRunPrompted = true;                 // player is prompted for their name
                    setLongestRun();
                    longestRunInfo = getLongestRun();
                }
                if(!longestRunInfo.equals("nobody: 0")) {         // does not draw leaderboard if a player has not set a longest run yet
                    drawLeaderBoard(g);
                }

                Graphics2D g2d = (Graphics2D) g;

                // draws the "buy shields" button:
                g.setColor(Color.BLUE);
                g2d.fill(buyShieldRect);
                Color gold = new Color(255, 255, 26);
                g.setColor(gold);
                g.setFont(myFont.deriveFont(Font.BOLD, 20f));
                g.drawString(shieldCost+" COINS", WIDTH/2 + 180, HEIGHT/2 - 50);
                g.setColor(Color.WHITE);
                g.setFont(myFont.deriveFont(Font.BOLD, 30f));
                g.drawString("BUY SHIELDS", WIDTH/2 + 145, HEIGHT/2 - 80);

                // draws the "restart game" button:
                g.setColor(Color.BLUE);
                g2d.fill(restartGameRect);
                g.setColor(Color.WHITE);
                g.setFont(myFont.deriveFont(Font.BOLD, 80f));
                g.drawString("RESTART GAME", WIDTH/2 - 245, HEIGHT/2 + 170);

                // draws the buy shield message for 20 frames:
                if(buyShieldMessageFrameCount > 0) {
                    buyShieldMessageFrameCount++;
                    if(buyShieldMessageFrameCount == 20) {
                        buyShieldMessageFrameCount = 0;
                        buyShieldsMessage = "";
                    }
                }
                g.setFont(myFont.deriveFont(Font.BOLD, 25f));
                if(buyShieldsMessage.contains("Sorry")) {
                    g.drawString(buyShieldsMessage, 350, HEIGHT/2 + 30);   // draws the buy shield message
                }
                else if(buyShieldsMessage.contains("You bought")) {
                    g.drawString(buyShieldsMessage, 350, HEIGHT/2 + 30);   // draws the buy shield message
                }
            }
        }
    }

    @Override
    public void	mousePressed(MouseEvent e){
    }

    public void	mouseClicked(MouseEvent e){}
    public void	mouseEntered(MouseEvent e){}
    public void	mouseExited(MouseEvent e){}
    public void	mouseReleased(MouseEvent e){
        if(screen == "game over") {
            mouse = new Point(e.getX(),e.getY());
            if(buyShieldRect.contains(mouse)) {     // if the player clicks on the "buy shield" button
                buyShieldMessageFrameCount++;
                buyShield();
            }
            if(restartGameRect.contains(mouse)) {   // if the player clicks on the "restart game" button
                initialize();
                screen = "game";
            }
            mouse = new Point();
        }
    }

    public void	keyPressed(KeyEvent e) {
        if (screen.equals("start") && e.getKeyCode() == KeyEvent.VK_SPACE) {                                            // player presses space on starting screen to play
            SoundPlayer.playSoundEffect(SoundPlayer.background, Clip.LOOP_CONTINUOUSLY);								// plays the background music forever
            screen = "game";
        }
        if (screen.equals("game") && e.getKeyCode() == KeyEvent.VK_ENTER && numOfShields > 0 && !barry.hasShield()) {   // player presses enter within the game to activate their shield (if they have any)
            barry.activateShield();
        }

        allKeys[e.getKeyCode()] = true;
    }
    public void	keyReleased(KeyEvent e){
        allKeys[e.getKeyCode()] = false;
    }

    public void	keyTyped(KeyEvent e){}



}
