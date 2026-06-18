package populationPlay;

public class Biome {
    // Set up variables here
    private String biomeName="";
    private double energyGrowth;
    private double energySpread;
    private double movementCost;
    private double movementMult;
    private double temperature;

    public Biome (String biomeInd) {
        if (biomeInd.equals("P")) {
            biomeName="Plains";
            setPlains();
        } else if (biomeInd.equals("F")) {
            biomeName="Forest";
            setForest();
        } else if (biomeInd.equals("D")) {
            biomeName="Desert";
            setDesert();
        } else if (biomeInd.equals("S")) {
            biomeName="Swamp";
            setSwamp();
        } else if (biomeInd.equals("C")) {
            biomeName="Coast";
            setCoast();
        } else if (biomeInd.equals("M")) {
            biomeName="Mountain";
            setMountain();
        } else if (biomeInd.equals("J")) {
            biomeName="Jungle";
            setJungle();
        } else if (biomeInd.equals("T")) {
            biomeName="Tundra";
            setTundra();
        } else if (biomeInd.equals("V")) {
            biomeName="Volcanic";
            setVolcanic();
        } else if (biomeInd.equals("~")) {
            biomeName="Ocean";
            setOcean();
        } else {
            System.out.println("Please choose a valid biome (P, F, D, S, C):\n");
        }
    }

    /// These are the modifiers for each biome.
   //  for now, i am only changing movement Mult, not cost
    private void setPlains() {
        energyGrowth=1.0;
        energySpread=1.0;
        movementMult=1.0;
        //movementCost=1.0; not using, so i deleted from other, but can copy paste from here when needed
    }

    private void setForest() {
        energyGrowth = 1.4;
        energySpread = 0.3;
        movementMult = 0.6;
    }

    private void setDesert() {
        energyGrowth=0.1;
        energySpread=2.0;
        movementMult=0.3;
        temperature = 1.5;
    }

    private void setSwamp() {
        energyGrowth=2.0;
        energySpread=0.2;
        movementMult=0.4;
    }

    private void setCoast() {
        energyGrowth=1.5;
        energySpread=1.0;
        movementMult=1.2;
    }

    private void setMountain() {
        energyGrowth=0.5;
        energySpread=1.0;
        movementMult=0.4;
    }

    private void setJungle() {
        energyGrowth=2.0;
        energySpread=0.8;
        movementMult=0.6;
    }

    private void setTundra() { //special, has old age help
        energyGrowth=0.5;
        energySpread=0.6; //or 1.0
        movementMult=0.8;
        temperature = 0.1; // Cold!
        // insert age damage reduction
    }

    private void setVolcanic() { //special, random catastrophy but also good energy spot
        energyGrowth=1.5;
        energySpread=1.8; //or 1.0
        movementMult=1.0;
        // insert random wipeout of energy
    }

    private void setOcean() { //special, cannot cross except if they jump over it, no energy grows
        energyGrowth=0.0;
        energySpread=0.0; //or 1.0
        movementMult=0.1;
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



}