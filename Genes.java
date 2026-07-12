package populationPlay;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Genes {
    //ability
    double speed;
    double resistance;
    double fertility;
    int maxHP;
    int size;
    double absorb_d; //This is the factor passed down for absorb distance, raw (before curving)
    double repro_d;
    int clusterAmt;
    double visionDist;

    //behavioral
    double speedPref;
    double bearing;
    double energyOrientBias; //how strongly does the orientation to energy patches matter
    double flockAff; //how much does the bird stray from or get attrafted to flocks of same species
    double aggroOrientBias;
    double comfortPercentage;
    double energySensitivitySlope;

    //predatory
    double talonReach;
    double talonPower;
    double ecoScale;

    // String heritage; //ID of bird
    int[] speciesCode;

    public Genes() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int r = rand.nextInt(10000);
       // int r =  (int) (Math.random()*10000); // r is a random integer ABCD

        size = r%10+1;  //size is last digit+1
        speed = (((double) r / 10) % 1000) / 100.0; //speed is the first three numbers of r in a decimal, arranged A.BC
        resistance = rand.nextDouble();
        fertility = 1.0-resistance;
        absorb_d = rand.nextDouble()*50+2; //this is the gene for absorb before the logarithmic curve (explained more in Bird tab)
        repro_d = rand.nextDouble()*50+2;
        maxHP = (int)(rand.nextDouble() * 150 + 50 - absorb_d);
        maxHP = Math.max(5, maxHP); //IDEA: r-select vs k-select. more HP means less cluster babies, but less hp = more babies
        /*visionDist = 5; //birds start with blindness, have to evolve sight
        speedPref = 0.5;
        energyOrientBias = 0;
        flockAff = 0;
        aggroOrientBias = 0; */



        visionDist = rand.nextDouble()*20; //birds start with blindness, have to evolve sight
        //visionDist = 0;
        bearing=rand.nextDouble()*Math.PI*2;
        speedPref = rand.nextDouble();
        //speedPref = rand.nextDouble()/4;
        energyOrientBias = rand.nextDouble()/4;
        //energyOrientBias =0;
        flockAff = rand.nextDouble()/2.5-0.2;
        //flockAff = 0.5;
        aggroOrientBias = 0;
        comfortPercentage=rand.nextDouble(); //percent of vision calc in Bird flockAffVector
        clusterAmt = 5;
        ecoScale = 0;
        speciesCode = new int[] {1000, 1000, 1000, 1000};


        // tempStats = [size, speed
    }

    public Genes(int s, double sp, int mhp, double res, double ab_d, double re_d, double vDist, double spPref, double enOrientBias, double flAff, double agOrientBias, double comfPct, double ecoScl, int[] specCode) {
        // sets the genes to whatever give
        size = s;
        speed = sp;
        maxHP = mhp;
        resistance = res;
        fertility = 1.0-resistance;
        absorb_d = ab_d;
        repro_d = re_d;
        visionDist = vDist;
        speedPref = spPref;
        energyOrientBias = enOrientBias;
        flockAff = flAff;
        aggroOrientBias = agOrientBias;
        comfortPercentage=comfPct;
        ecoScale = ecoScl;

        speciesCode = specCode.clone();



    }

    public static Genes recombine(Genes a, Genes b) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        int[] newSpecCode = new int[] {a.speciesCode[0], b.speciesCode[1], a.speciesCode[2], b.speciesCode[3]};

        int newSize = (rand.nextDouble() < 0.5) ? a.size : b.size;  //takes 50% either parent size

        double newSpeed        = recombinedGeneDouble(a.speed, b.speed, 0.25, newSpecCode, 0, Double.MAX_VALUE);

        int    newMaxHP        = recombinedGeneInt(a.maxHP, b.maxHP, 2, newSpecCode, 1, Integer.MAX_VALUE);

        double newResistance   = recombinedGeneDouble(a.resistance, b.resistance, 0.05, newSpecCode, 0, 1);

        double newAbsorbD      = recombinedGeneDouble(a.absorb_d, b.absorb_d, 3, newSpecCode, 0.5, Double.MAX_VALUE);

        double newReproD       = recombinedGeneDouble(a.repro_d, b.repro_d, 3, newSpecCode, 0.5, Double.MAX_VALUE);

        double newVisionDist   = recombinedGeneDouble(a.visionDist, b.visionDist, 5, newSpecCode, 0, Double.MAX_VALUE);

        double newSpPref       = recombinedGeneDouble(a.speedPref, b.speedPref, 0.5, newSpecCode, 0, 1);

        double newEnOrientBias = recombinedGeneDouble(a.energyOrientBias, b.energyOrientBias, 0.3, newSpecCode, 0, 1);

        double newFlockAff     = recombinedGeneDouble(a.flockAff, b.flockAff, 0.2, newSpecCode, -1, 1);

        double newAgOrientBias = recombinedGeneDouble(a.aggroOrientBias, b.aggroOrientBias, 0.1, newSpecCode, -1, 1);

        double newComfPct      = recombinedGeneDouble(a.comfortPercentage, b.comfortPercentage, 0.1, newSpecCode, 0, 1);

        double newEcoScale     = recombinedGeneDouble(a.ecoScale, b.ecoScale, 0.1, newSpecCode, 0, 1);


        /*
        double newSpeed = (Math.random() < 0.5) ? a.speed : b.speed;
         */ /// THis is for dominance or recessive if u want it later

//        newSpeed = Math.max(0, newSpeed);
//        newMaxHP = Math.max(1, newMaxHP);
//        newResistance = Math.max(0, Math.min(1, newResistance));
//        newAbsorbD = Math.max(0.5, newAbsorbD);
//        newReproD = Math.max(0.5, newReproD);
//        newVisionDist = Math.max(newVisionDist, 0);
//        newSpPref = Math.max(Math.min(newSpPref, 1), 0);
//        newEnOrientBias = Math.max(Math.min(newEnOrientBias, 1), 0);
//        newFlockAff = Math.max(Math.min(newFlockAff, 1), -1);
//        newAgOrientBias = Math.max(Math.min(newAgOrientBias, 1), -1);
//        newComfPct = Math.max(Math.min(newComfPct, 1), 0);
//        newEcoScale = Math.max(Math.min(newEcoScale, 1), 0);

        return new Genes(newSize, newSpeed, newMaxHP, newResistance, newAbsorbD, newReproD, newVisionDist, newSpPref, newEnOrientBias, newFlockAff, newAgOrientBias, newComfPct, newEcoScale, newSpecCode);
    }

    public static double recombinedGeneDouble(double geneA, double geneB, double mutateRange, int[] newSpecCode, double min, double max) {
        double newGene = average(geneA, geneB);
        newGene = mutateDouble(newSpecCode, newGene, 0.1);
        if (newGene < min) return min;
        if (newGene > max) return max;
        return newGene;
    }

    public static int recombinedGeneInt(int geneA, int geneB, int mutateRange, int[] newSpecCode, int min, int max) {
        int newGene = (int) (average(geneA, geneB)+0.5);
        newGene = mutateInt(newSpecCode, newGene, mutateRange);
        if (newGene < min) return min;
        if (newGene > max) return max;
        return newGene;
    }

    //helper
    private static double average(double x, double y) {
        return (x + y) * 0.5;
    }

    private static double mutateDouble(int[] newSpecCode, double value, double range) { /// double mutation
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double roll = rand.nextDouble();

        if (roll<0.005) {
            mutateSpecCode(newSpecCode, 5);
            return value + (rand.nextDouble() * 20.0 - 10.0) * range;
        } else if (roll<0.05) {
            mutateSpecCode(newSpecCode, 1);
            return value + (rand.nextDouble() - 0.5) * range;
        }
        return value;
    }

    private static int mutateInt(int[] newSpecCode, int value, int range) { /// int mutation
        //int mutation = (int)((Math.random() - 0.5) * range);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double roll = rand.nextDouble();

        if (roll<0.005) {
            mutateSpecCode(newSpecCode, 5);
            return value + (int) ((rand.nextDouble() * 20.0 - 10.0) * range);
        } else if (roll<0.05) {
            mutateSpecCode(newSpecCode, 1);
            return value + (int) ((rand.nextDouble() - 0.5) * range);
        }
        return value;

    }

    private static void mutateSpecCode(int[] newSpecCode, int mutAmt) {
        int vecNum= (int)(Math.random()*newSpecCode.length);
        Random random = new Random();
        int sign = random.nextBoolean() ? 1 : -1;
        newSpecCode[vecNum] += mutAmt*sign;
        // return newSpecCode;
    }


    //obtaining methods
    public int getSize() {
        return size;
    }

    public double getSpeed() {
        return speed;
    }

    public int getHP() {
        return maxHP;
    }

    public double getResistance() {
        return resistance;
    }

    public double getAbsorb() {
        return absorb_d;
    }

    public double getRepro() {
        return repro_d;
    }

    public double getVisionDist() {
        return visionDist;
    }

    public double getEnergyOrientBias() {
        return energyOrientBias;
    }



    @Override
    public String toString() {
        DecimalFormat resF = new DecimalFormat("##.#");
        DecimalFormat absF = new DecimalFormat("##.##");
        return "Genes: speed - " + absF.format(speed) + ", size - " + size + ", HP - " + maxHP + ", resistance - " + (resF.format(resistance*100)) + ", fertility - " + (resF.format(fertility*100)) +
                "%, absorb_d - " + (absF.format(Math.pow(absorb_d, 1.0/2)))+ ", repro_d - " + (absF.format(Math.pow(repro_d, 1.0/2)))+ "%, visionDist - " + (absF.format(visionDist)) + ", speedPref - " + (absF.format(speedPref*100)) + "%, " +
                "enOrientBias - "+ absF.format((double) energyOrientBias*100) + "%, flockAff - "+ absF.format((double) flockAff*100) + "%, prefSpacing - " + absF.format(comfortPercentage*visionDist) + ", isPred - " + (ecoScale>0.5) + ", ecoScale - " + absF.format(ecoScale*100);
    }



}

/* Notes:
* - For now, reproduction will use the absorb_d used for food absorption
* */