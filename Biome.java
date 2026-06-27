package populationPlay;

import java.awt.*;

public class Biome {
    // Set up variables here
    private String biomeName="";
    private double energyGrowth;
    private double energySpread;
    private double movementCost;
    private double movementMult;
    private double visionMultiplier;
    private double temperature;
    private Color biomeColor;


    static Color plainsColor = new Color(91, 170, 79);
    static Color forestColor = new Color(15, 70, 2, 255);
    static Color swampColor = new Color(99, 134, 87, 255);
    static Color desertColor = new Color(210, 165, 90);
    static Color coastColor = new Color(220, 200, 140, 255);
    static Color mountainColor = new Color(130, 135, 140);
    static Color jungleColor = new Color(15, 120, 80);
    static Color tundraColor = new Color(210, 235, 245);
    static Color volcanicColor = new Color(55, 35, 40);
    static Color oceanColor = new Color(20, 40, 85, 255);

    public Biome (String biomeInd) {
        switch (biomeInd) {
            case "P": setPlains(); break;
            case "F": setForest(); break;
            case "S": setSwamp(); break;
            case "D": setDesert(); break;
            case "C": setCoast(); break;
            case "M": setMountain(); break;
            case "J": setJungle(); break;
            case "T": setTundra(); break;
            case "V": setVolcanic(); break;
            case "O": setOcean(); break;
            case "~": setOcean(); break;
            default: System.out.println("Please choose a valid biome (P, F, D, S, C):\n");
        }
        System.out.print(biomeInd);

//        if (biomeInd.equals("P")) {
//            setPlains();
//        } else if (biomeInd.equals("F")) {
//            setForest();
//        } else if (biomeInd.equals("D")) {
//            setDesert();
//        } else if (biomeInd.equals("S")) {
//            setSwamp();
//        } else if (biomeInd.equals("C")) {
//            setCoast();
//        } else if (biomeInd.equals("M")) {
//            setMountain();
//        } else if (biomeInd.equals("J")) {
//            setJungle();
//        } else if (biomeInd.equals("T")) {
//            setTundra();
//        } else if (biomeInd.equals("V")) {
//            setVolcanic();
//        } else if (biomeInd.equals("~")) {
//            setOcean();
//        } else {
//            System.out.println("Please choose a valid biome (P, F, D, S, C):\n");
//        }
    }

    /// These are the modifiers for each biome.
   //  for now, i am only changing movement Mult, not cost
    private void setPlains() {
        biomeName="Plains";
        biomeColor = plainsColor;
        energyGrowth=1.0;
        energySpread=1.0;
        movementMult=1.0;
        //movementCost=1.0; not using, so i deleted from other, but can copy paste from here when needed
    }

    private void setForest() {
        biomeName="Forest";
        biomeColor = forestColor;
        energyGrowth = 2.0;
        energySpread = 0.4;
        movementMult = 0.6;
        visionMultiplier = 0.8;
    }

    private void setSwamp() {
        biomeName="Swamp";
        biomeColor = swampColor;
        energyGrowth=2.3;
        energySpread=0.4;
        movementMult=0.5;
        movementCost = 0.7;
    }

    private void setDesert() {
        biomeName="Desert";
        biomeColor = desertColor;
        energyGrowth=0.8;
        energySpread=2.5;
        movementMult=1.3;
        temperature = 1.5;
    }

    private void setCoast() {
        biomeName="Coast";
        biomeColor = coastColor;
        energyGrowth=1.9;
        energySpread=1.0;
        movementMult=2.0;
    }

    private void setMountain() {
        biomeName="Mountain";
        biomeColor = mountainColor;
        energyGrowth=1.4;
        energySpread=1.0;
        movementMult=0.4;
    }

    private void setJungle() {
        biomeName="Jungle";
        biomeColor = jungleColor;
        energyGrowth=2.8;
        energySpread=0.7;
        movementMult=0.7;
        visionMultiplier = 0.7;
        // crowding penalty simulated disease
    }

    private void setTundra() { //special, has old age help
        biomeName="Tundra";
        biomeColor = tundraColor;
        energyGrowth=0.7;
        energySpread=0.6; //or 1.0
        movementMult=1.3;
        temperature = 0.1; // Cold!
        // insert age damage reduction
    }

    private void setVolcanic() { //special, random catastrophy but also good energy spot
        biomeName="Volcanic";
        biomeColor = volcanicColor;
        energyGrowth=5.0;
        energySpread=1.8; //or 1.0
        movementMult=1.2;
        // insert random wipeout of energy
    }

    private void setOcean() { //special, cannot cross except if they jump over it, no energy grows
        biomeName="Ocean";
        biomeColor = oceanColor;
        energyGrowth=0.2;
        energySpread=10; //or 1.0
        movementMult=5;
        // insert
    }


////    /// old modifiers
//    private void setPlains() {
//        energyGrowth=1.0;
//        energySpread=1.0;
//        movementMult=1.0;
//        //movementCost=1.0; not using, so i deleted from other, but can copy paste from here when needed
//    }
//
//    private void setForest() {
//        energyGrowth = 1.4;
//        energySpread = 0.6;
//        movementMult = 0.8;
//    }
//
//    private void setDesert() {
//        energyGrowth=0.2;
//        energySpread=2.5;
//        movementMult=0.3;
//    }
//
//    private void setSwamp() {
//        energyGrowth=1.6;
//        energySpread=0.5;
//        movementMult=0.4;
//    }
//
//    private void setCoast() {
//        energyGrowth=1.2;
//        energySpread=1.0;
//        movementMult=1.1;
//    }
//
//    private void setMountain() {
//        energyGrowth=0.7;
//        energySpread=1.0;
//        movementMult=0.2;
//    }
//
//    private void setJungle() {
//        energyGrowth=2.0;
//        energySpread=0.8;
//        movementMult=0.6;
//    }
//
//    private void setTundra() { //special, has old age help
//        energyGrowth=0.5;
//        energySpread=0.6; //or 1.0
//        movementMult=0.8;
//        // insert age damage reduction
//    }
//
//    private void setVolcanic() { //special, random catastrophy but also good energy spot
//        energyGrowth=1.5;
//        energySpread=1.8; //or 1.0
//        movementMult=1.0;
//        // insert random wipeout of energy
//    }
//
//    private void setOcean() { //special, cannot cross except if they jump over it, no energy grows
//        energyGrowth=0.0;
//        energySpread=0.0; //or 1.0
//        movementMult=0.1;
//        // insert
//    }







    public double getEnergyGrowth () {
        return energyGrowth;
    }

    public double getEnergySpread () {
        return energySpread;
    }

    public double getMovementMult () {
        return movementMult;
    }

    public double getDecompRate () {
        return 1.0;
    }

    public String getBiomeName () {
        return biomeName;
    }

    public Color getBiomeColor () {
        return biomeColor;
    }

    @Override
    public String toString() {
        return biomeName;
    }

}