package populationPlay;
import java.text.DecimalFormat;
import java.util.Random;

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
    double energyOrientBias; //how strongly does the orientation to energy patches matter
    double crowdAff; //how much does the bird stray from or get attrafted to crowds of same species
    double aggroOrientBias;

    // String heritage; //ID of bird
    int[] speciesCode;

    public Genes() {
         int r =  (int) (Math.random()*10000); // r is a random integer ABCD

        size = r%10+1;  //size is last digit+1
        speed = ((r / 10) % 1000) / 100.0; //speed is the first three numbers of r in a decimal, arranged A.BC
        resistance = Math.random();
        fertility = 1.0-resistance;
        absorb_d = Math.random()*50+2; //this is the gene for absorb before the logarithmic curve (explained more in Bird tab)
        repro_d = Math.random()*50+2;
        maxHP = (int)(Math.random() * 150 + 50 - absorb_d);
        maxHP = Math.max(5, maxHP); //IDEA: r-select vs k-select. more HP means less cluster babies, but less hp = more babies
        /*visionDist = 5; //birds start with blindness, have to evolve sight
        speedPref = 0.5;
        energyOrientBias = 0;
        crowdAff = 0;
        aggroOrientBias = 0; */

        visionDist = Math.random()*20; //birds start with blindness, have to evolve sight
        //visionDist = 20;
        speedPref = Math.random();
        energyOrientBias = Math.random()/4;
        //energyOrientBias =1;
        crowdAff = Math.random()/2.5-0.2;
        //crowdAff = 0.5;
        aggroOrientBias = 0;
        clusterAmt = 5;
        speciesCode = new int[] {1000, 1000, 1000, 1000};


        // tempStats = [size, speed
    }

    public Genes(int s, double sp, int mhp, double res, double ab_d, double re_d, double vDist, double spPref, double enOrientBias, double crAff, double agOrientBias, int[] specCode) {
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
        crowdAff = crAff;
        aggroOrientBias = agOrientBias;

        speciesCode = specCode.clone();



    }

    public static Genes recombine(Genes a, Genes b) {

        int[] newSpecCode = new int[] {a.speciesCode[0], b.speciesCode[1], a.speciesCode[2], b.speciesCode[3]};

        int newSize = (Math.random() < 0.5) ? a.size : b.size; //takes 50% either parent size

        double newSpeed = average(a.speed, b.speed);
        newSpeed = mutateDouble(newSpecCode, newSpeed, 0.25);

        int newMaxHP = (int) Math.round(average(a.maxHP, b.maxHP));
        newMaxHP = mutateInt(newSpecCode, newMaxHP, 2);

        double newResistance = average(a.resistance, b.resistance);
        newResistance = mutateDouble(newSpecCode, newResistance, 0.05);

        double newAbsorbD = average(a.absorb_d, b.absorb_d);
        newAbsorbD = mutateDouble(newSpecCode, newAbsorbD, 3);

        double newReproD = average(a.repro_d, b.repro_d);
        newReproD = mutateDouble(newSpecCode, newReproD, 3);

        double newVisionDist = average(a.visionDist, b.visionDist);
        newVisionDist = mutateDouble(newSpecCode, newVisionDist, 5);

        double newSpPref = average(a.speedPref, b.speedPref);
        newSpPref = mutateDouble(newSpecCode, newSpPref, 0.5);

        double newEnOrientBias = average(a.energyOrientBias, b.energyOrientBias);
        newEnOrientBias = mutateDouble(newSpecCode, newEnOrientBias, 0.3);

        double newCrowdAff = average(a.crowdAff, b.crowdAff);
        newCrowdAff = mutateDouble(newSpecCode, newCrowdAff, 0.2);

        double newAgOrientBias = average(a.aggroOrientBias, b.aggroOrientBias);
        newAgOrientBias = mutateDouble(newSpecCode, newAgOrientBias, 0.1);


        /*
        double newSpeed = (Math.random() < 0.5) ? a.speed : b.speed;
         */ /// THis is for dominance or recessive if u want it later

        newSpeed = Math.max(0, newSpeed);
        newMaxHP = Math.max(1, newMaxHP);
        newResistance = Math.max(0, Math.min(1, newResistance));
        newAbsorbD = Math.max(0.5, newAbsorbD);
        newReproD = Math.max(0.5, newReproD);
        newVisionDist = Math.max(newVisionDist, 0);
        newSpPref = Math.max(Math.min(newSpPref, 1), 0);
        newEnOrientBias = Math.max(Math.min(newEnOrientBias, 1), 0);
        newCrowdAff = Math.max(Math.min(newCrowdAff, 1), -1);
        newAgOrientBias = Math.max(Math.min(newAgOrientBias, 1), -1);

        return new Genes(newSize, newSpeed, newMaxHP, newResistance, newAbsorbD, newReproD, newVisionDist, newSpPref, newEnOrientBias, newCrowdAff, newAgOrientBias, newSpecCode);
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


    //helper
    private static double average(double x, double y) {
        return (x + y) / 2.0;
    }

    private static double mutateDouble(int[] newSpecCode, double value, double range) { /// double mutation
        double mutation = 0.0;
        if (Math.random()<0.005) {
             mutation = (Math.random()*20 - 10) * range;
             mutateSpecCode(newSpecCode, 5);

        } else if (Math.random()<0.05) {
            mutation = (Math.random() - 0.5) * range;
            mutateSpecCode(newSpecCode, 1);
        }
        return value + mutation;
    }

    private static int mutateInt(int[] newSpecCode, int value, int range) { /// int mutation
        //int mutation = (int)((Math.random() - 0.5) * range);
        double mutation = 0.0;
        if (Math.random()<0.005) {
            mutation = (Math.random()*20 - 10) * range;
            mutateSpecCode(newSpecCode, 5);
        } else if (Math.random()<0.05) {
            mutation = (Math.random() - 0.5) * range;
            mutateSpecCode(newSpecCode, 1);
        }
        return value + (int) mutation;

    }

    private static void mutateSpecCode(int[] newSpecCode, int mutAmt) {
        int vecNum= (int)(Math.random()*newSpecCode.length);
        Random random = new Random();
        int sign = random.nextBoolean() ? 1 : -1;
        newSpecCode[vecNum] += mutAmt*sign;
       // return newSpecCode;
    }


    @Override
    public String toString() {
        DecimalFormat resF = new DecimalFormat("##.#");
        DecimalFormat absF = new DecimalFormat("##.##");
        return "Genes: speed - " + absF.format(speed) + ", size - " + size + ", HP - " + maxHP + ", resistance - " + (resF.format(resistance*100)) + ", fertility - " + (resF.format(fertility*100)) +
                "%, absorb_d - " + (absF.format(Math.pow(absorb_d, 1.0/2)))+ ", repro_d - " + (absF.format(Math.pow(repro_d, 1.0/2)))+ "%, visionDist - " + (absF.format(visionDist)) + ", speedPref - " + (absF.format(speedPref*100)) + "%, " +
                "enOrientBias - "+ absF.format((double) energyOrientBias*100) + "%, crowdAff - "+ absF.format((double) crowdAff*100) + "%";
    }



}

/* Notes:
* - For now, reproduction will use the absorb_d used for food absorption
* */