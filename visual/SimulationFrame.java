package populationPlay.visual;

import javax.swing.JFrame;

public class SimulationFrame extends JFrame {

    public SimulationFrame(MapPanel mapPanel) {

        setTitle("Evolution Simulator");
        setSize(1000, 800);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(mapPanel);

        setVisible(true);
    }
}