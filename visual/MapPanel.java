package populationPlay.visual;

import populationPlay.World;
import populationPlay.Bird;
import populationPlay.Genes;
import populationPlay.Energy;
import populationPlay.Biome;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

public class MapPanel extends JPanel {

    private World world;
    public static int worldSize;
    private BufferedImage biomeLayout;
    private double zoomFactor = 1.0;     // 1.0 = Normal scaling, 2.0 = Double size, etc.
    private double offsetX = 0.0;        // Camera horizontal panning coordinate
    private double offsetY = 0.0;        // Camera vertical panning coordinate
    public int scaleFactor=0;

    private int lastMouseX;              // Tracks the previous mouse X frame index
    private int lastMouseY;
    // Tracks the previous mouse Y frame index

    private static final double MIN_ZOOM = 0.2;  // Allows zooming out to see the whole world
    private static final double MAX_ZOOM = 15.0; //

    public MapPanel(World w, int wSize) {
        world = w;
        worldSize = wSize;
        initializeCameraControls();

        scaleFactor = 8;
        biomeLayout = new BufferedImage(worldSize * scaleFactor, worldSize * scaleFactor, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = biomeLayout.createGraphics();
        for (int biomeX=0; biomeX<worldSize; biomeX++) {
            for (int biomeY=0; biomeY<worldSize; biomeY++) {
                Biome thisBiome = world.getBiomeMap() [biomeX][biomeY];
                g2.setColor(thisBiome.getBiomeColor());
                g2.fillRect(biomeX*scaleFactor, biomeY*scaleFactor, scaleFactor, scaleFactor);
            }
        }

        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // A. Capture the initial graphics transform state to avoid breaking UI/overlays later
        AffineTransform originalTransform = g2d.getTransform();

        // B. Apply your camera transformation pipeline mathematically to the rendering engine
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoomFactor, zoomFactor);

        //colors
        Color plainsColor = Color.BLACK;
        Color forestColor = new Color(15, 70, 2, 255);
        Color swampColor = new Color(88, 127, 79, 255);
        Color desertColor = new Color (175, 175, 100, 255);
        Color coastColor = new Color(100, 150, 200, 255);
        Color mountainColor = Color.gray;

        Graphics2D g2 = (Graphics2D) g;

        // background
        g2.setColor(Color.gray);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // biomes
        if (biomeLayout != null) {
            g2.drawImage(biomeLayout, 0, 0, null);
        }

//        int worldActSize = scaleFactor*worldSize;
//        g2.setColor(forestColor);
//        g2.fillRect(0, 0, worldActSize/2, worldActSize/2);
//        g2.setColor(plainsColor);
//        g2.fillRect(worldActSize/2, 0, worldActSize/2, worldActSize/2);
//        g2.setColor(swampColor); //
//        g2.fillRect(0, worldActSize/2, worldActSize/2, worldActSize/2);
//        g2.setColor(desertColor);
//        g2.fillRect(worldActSize/2, worldActSize/2, worldActSize/2, worldActSize/2);


        //draw energy
        //g2.setColor(Color.GREEN);

        ArrayList<Energy> energySnapshot = new ArrayList<Energy>(world.getUsableEnergyList());
        for (Energy e : energySnapshot) {
            if (e.isSprouted()) {
                g2.setColor(Color.green);
            } else {
                // g2.setColor(Color.YELLOW);
                g2.setColor(new Color (175, 175, 100, 0));
            }

            int x = e.getPosition()[0] * scaleFactor;
            int y = e.getPosition()[1] * scaleFactor;

            g2.fillRect(x, y, 2, 2);
        }

        // draw birds
        // g2.setColor(Color.RED);

        ArrayList<Bird> birdsSnapshot = world.getAliveList();


        // Save the old stroke to restore it later
        java.awt.Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(0.75f)); // Gives the outline a distinct thickness

        for (int i = 0; i < birdsSnapshot.size(); i++) {
            if (i >= birdsSnapshot.size()) break;

            Bird b = birdsSnapshot.get(i);
            if (b == null) continue;
            int x = b.getPosition()[0] * scaleFactor;
            int y = b.getPosition()[1] * scaleFactor;

//            // high contrast outer silhouette
//            // Pure white ring for babies, Neon Magenta ring for breeding adults
//            if (b.juvenile) {
//                g2.setColor(Color.WHITE);
//            } else {
//                g2.setColor(new Color(255, 0, 130)); // Neon Magenta
//            }
//            // Draw an empty ring slightly shifted outward to perfectly encapsulate the inner core
//            g2.drawOval(x - 1, y - 1, 5, 5);

            if (b.getID().equals("Average model")) {
                g2.setColor(Color.black);
                g2.drawOval(x - 1, y - 1, 6, 6);
            }
            if (b.ecoScale>0.66) {
                g2.setColor(Color.red);
                g2.drawOval(x - 1, y - 1, 5, 5);
            } else if (b.ecoScale>0.4) {
                g2.setColor(Color.blue);
                g2.drawOval(x - 1, y - 1, 5, 5);
            }
            g2.setColor(b.getSpeciesColor());
            g2.fillOval(x, y, 4, 4); // Standard size as requested
        }

        // Restore default stroke thickness
        g2.setStroke(oldStroke);
        g2d.setTransform(originalTransform);

    }

    public void initializeCameraControls() {
        MouseAdapter universalController = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Anchor the start position the microsecond a finger touches down
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int currentX = e.getX();
                int currentY = e.getY();

                // Calculate how far the cursor slid since the last layout frame tick
                int dx = currentX - lastMouseX;
                int dy = currentY - lastMouseY;

                // TRACKPAD CONDITION 1: Zooming (Hold Control or Command while dragging)
                if (e.isControlDown() || e.isMetaDown()) {
                    double oldZoom = zoomFactor;

                    // Dragging UP zooms IN, dragging DOWN zooms OUT
                    if (dy < 0) {
                        zoomFactor = Math.min(MAX_ZOOM, zoomFactor * 1.04); // Smooth 4% step
                    } else if (dy > 0) {
                        zoomFactor = Math.max(MIN_ZOOM, zoomFactor / 1.04);
                    }

                    // Focal Anchor Math: Keeps the tile directly under your cursor perfectly stable
                    offsetX = currentX - (currentX - offsetX) * (zoomFactor / oldZoom);
                    offsetY = currentY - (currentY - offsetY) * (zoomFactor / oldZoom);
                }
                // TRACKPAD CONDITION 2: Panning (Regular drag across trackpad)
                else {
                    offsetX += dx;
                    offsetY += dy;
                }

                // Update frame history anchors
                lastMouseX = currentX;
                lastMouseY = currentY;

                // Tell Swing to clear the canvas and redraw everything with the new camera space
                repaint();
            }
        };

        // Bind the listener engine to capture clicks and sliding arcs
        this.addMouseListener(universalController);
        this.addMouseMotionListener(universalController);
    }

    public int convertScreenXToGridIndex(int screenClickX, int tileSize) {
        return (int) Math.floor((screenClickX - offsetX) / (zoomFactor * tileSize));
    }

    public int convertScreenYToGridIndex(int screenClickY, int tileSize) {
        return (int) Math.floor((screenClickY - offsetY) / (zoomFactor * tileSize));
    }
}

