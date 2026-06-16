package populationPlay.visual;

import populationPlay.World;
import populationPlay.Bird;
import populationPlay.Energy;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;

public class MapPanel extends JPanel {

    private World world;
    public static int worldSize;

    public MapPanel(World w, int wSize) {
        world = w;
        worldSize = wSize;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

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
        int scaleFactor = 4;
        int worldActSize = scaleFactor*worldSize;
        g2.setColor(forestColor);
        g2.fillRect(0, 0, worldActSize/2, worldActSize/2);
        g2.setColor(plainsColor);
        g2.fillRect(worldActSize/2, 0, worldActSize/2, worldActSize/2);
        g2.setColor(swampColor); //
        g2.fillRect(0, worldActSize/2, worldActSize/2, worldActSize/2);
        g2.setColor(desertColor);
        g2.fillRect(worldActSize/2, worldActSize/2, worldActSize/2, worldActSize/2);

        //draw energy
        g2.setColor(Color.GREEN);

        ArrayList<Energy> energySnapshot = new ArrayList<Energy>(world.getUsableEnergyList());
        for (Energy e : energySnapshot) {

            int x = e.getPosition()[0] * scaleFactor;
            int y = e.getPosition()[1] * scaleFactor;

            g2.fillRect(x, y, 2, 2);
        }

        // draw birds
        // g2.setColor(Color.RED);

        ArrayList<Bird> birdsSnapshot = new ArrayList<>(world.getAliveList());


        // Save the old stroke to restore it later
        java.awt.Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(0.75f)); // Gives the outline a distinct thickness

        for (Bird b : birdsSnapshot) {
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

            // STEP 2: FILL INNER CORE WITH GENETIC SPECIES COLOR
            g2.setColor(b.getSpeciesColor());
            g2.fillOval(x, y, 4, 4); // Standard size as requested
        }

        // Restore default stroke thickness
        g2.setStroke(oldStroke);




    }


}

