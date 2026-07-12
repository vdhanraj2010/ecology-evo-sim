package populationPlay.visual;

import javax.swing.JFrame;

public class SimulationFrame extends JFrame {

    private int totalTicksElapsed = 0; // Replaces the old console 'past' variable

    public SimulationFrame(MapPanel mapPanel) {

        setTitle("Evolution Simulator");
            setSize(1000, 1000);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(mapPanel);

        setVisible(true);
    }

    
}