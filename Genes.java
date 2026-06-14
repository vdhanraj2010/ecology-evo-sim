package populationPlay;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Random;

public class Genes {
    double speed;
    double resistance;
    double fertility;
    int maxHP;
    int size;
    double absorb_d; //This is the factor passed down for absorb distance, raw (before curving)
    // String heritage; //ID of bird
    int[] speciesCode;

    public Genes() {
         int r =  (int) (Math.random()*10000); // r is a random integer ABCD

        size = r%10+1;  //size is last digit+1
        speed = ((r / 10) % 1000) / 100.0; //speed is the first three numbers of r in a decimal, arranged A.BC
        resistance = Math.random();
        fertility = 1.0-resistance;
        absorb_d = Math.random()*50+2; //this is the gene for absorb before the logarithmic curve (explained more in Bird tab)
        maxHP = (int)(Math.random() * 50 + 5 - absorb_d/2);
        maxHP = Math.max(5, maxHP); //IDEA: r-select vs k-select. more HP means less cluster babies, but less hp = more babies
        speciesCode = new int[] {1000, 1000, 1000, 1000};


        // tempStats = [size, speed
    }

    public Genes(int s, double sp, int mhp, double res, double ab_d, int[] specCode) {
        // sets the genes to whatever given
        size = s;
        speed = sp;
        maxHP = mhp;
        resistance = res;
        fertility = 1.0-resistance;
        absorb_d = ab_d;
        speciesCode = specCode;



    }

    public static Genes recombine(Genes a, Genes b) {

        int[] newSpecCode = new int[] {a.speciesCode[0], b.speciesCode[1], a.speciesCode[2], b.speciesCode[3]};

        int newSize = (Math.random() < 0.5) ? a.size : b.size; //takes 50% either parent size

        double newSpeed = average(a.speed, b.speed);
        newSpeed = mutateDouble(newSpecCode, newSpeed, 0.2);

        int newMaxHP = (int) Math.round(average(a.maxHP, b.maxHP));
        newMaxHP = mutateInt(newSpecCode, newMaxHP, 2);

        double newResistance = average(a.resistance, b.resistance);
        newResistance = mutateDouble(newSpecCode, newResistance, 0.1);

        double newAbsorbD = average(a.absorb_d, b.absorb_d);
        newAbsorbD = mutateDouble(newSpecCode, newAbsorbD, 3);


        /*
        double newSpeed = (Math.random() < 0.5) ? a.speed : b.speed;
         */ /// THis is for dominance or recessive if u want it later

        newSpeed = Math.max(0, newSpeed);
        newMaxHP = Math.max(1, newMaxHP);
        newResistance = Math.max(0, Math.min(1, newResistance));
        newAbsorbD = Math.max(0.5, newAbsorbD);

        return new Genes(newSize, newSpeed, newMaxHP, newResistance, newAbsorbD, newSpecCode);
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


    //helper
    private static double average(double x, double y) {
        return (x + y) / 2.0;
    }

    private static double mutateDouble(int[] newSpecCode, double value, double range) { /// double mutation
        double mutation = 0.0;
        if (Math.random()<0.01) {
             mutation = (Math.random()*10 - 20) * range;
             mutateSpecCode(newSpecCode, 5);

        } else if (Math.random()<0.1) {
            mutation = (Math.random() - 0.5) * range;
            mutateSpecCode(newSpecCode, 1);
        }
        return value + mutation;
    }

    private static int mutateInt(int[] newSpecCode, int value, int range) { /// int mutation
        //int mutation = (int)((Math.random() - 0.5) * range);
        double mutation = 0.0;
        if (Math.random()<0.01) {
            mutation = (Math.random()*10 - 20) * range;
            mutateSpecCode(newSpecCode, 5);
        } else if (Math.random()<0.1) {
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
        return "Genes: speed - " + absF.format(speed) + ", size - " + size + ", HP - " + maxHP + ", resistance - " + (resF.format(resistance*100)) + ", fertility - " + (resF.format(fertility*100)) + "%, absorb_d - " + (absF.format(Math.pow(absorb_d, 1.0/2)));
    }



}

/* Notes:
* - For now, reproduction will use the absorb_d used for food absorption
* */