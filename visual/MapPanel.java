package populationPlay.visual;

import populationPlay.World;
import populationPlay.Bird;
import populationPlay.Energy;

import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

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
        Color forestColor = new Color(15, 70, 2);
        Color swampColor = new Color(88, 127, 79);
        Color coastColor = new Color(100, 150, 200);
        Color mountainColor = Color.gray;
        Color desertColor = new Color(199, 166, 100);

        Graphics2D g2 = (Graphics2D) g;

        // background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // biomes
        int scaleFactor = 6;
        int worldActSize = scaleFactor*worldSize;
        g2.setColor(forestColor);
        g2.fillRect(0, 0, worldActSize/2, worldActSize/2);
        g2.setColor(plainsColor);
        g2.fillRect(worldActSize/2, 0, worldActSize/2, worldActSize/2);
        g2.setColor(swampColor); //
        g2.fillRect(0, worldActSize/2, worldActSize/2, worldActSize/2);
        g2.setColor(mountainColor);
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
        g2.setColor(Color.RED);

        ArrayList<Bird> birdsSnapshot = new ArrayList<>(world.getAliveList());

        for (Bird b : birdsSnapshot) {

            int x = b.getPosition()[0] * scaleFactor;
            int y = b.getPosition()[1] * scaleFactor;

            g2.fillOval(x, y, 4, 4);
        }


    }
}