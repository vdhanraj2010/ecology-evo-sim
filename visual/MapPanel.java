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

    public MapPanel(World w) {
        world = w;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //draw energy
        g2.setColor(Color.GREEN);

        ArrayList<Energy> energySnapshot = new ArrayList<Energy>(world.getUsableEnergyList());
        for (Energy e : energySnapshot) {

            int x = e.getPosition()[0] * 8;
            int y = e.getPosition()[1] * 8;

            g2.fillRect(x, y, 3, 3);
        }

        // draw birds
        g2.setColor(Color.RED);

        ArrayList<Bird> birdsSnapshot = new ArrayList<>(world.getAliveList());

        for (Bird b : birdsSnapshot) {

            int x = b.getPosition()[0] * 8;
            int y = b.getPosition()[1] * 8;

            g2.fillOval(x, y, 4, 4);
        }


    }
}