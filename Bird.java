package populationPlay;// import statements
import java.util.ArrayList;
import java.awt.Color;

public class Bird {
    private int[] stats;
    private int[] position = new int[2];
    private int age;
    private int die_age;
    private int hp;
    private int maxHP;
    private double speed;
    private double resistance;
    private double fertility;
    private int size;
    private double absorb_rad;
    private double repro_rad;
    private double visionDist;
    private double energyOrientBias;
    private double momentumAngle;
    public String biomeIn;
    public Biome currBiome;
    public static int worldSize;
    private boolean immortal = false;


    private static int id = 1; //id number that starts all origin birds
    private String myID = ""; // ID of the Bird
    private int reproID = 0; //
    public boolean alive = true;
    public boolean juvenile = true;
    public int deathYear = 0;
    public String deathCause = "";
    public int deathCauseDone = 0; //temp
    private Genes myGenes;

    public void birth(Genes genes) {
        age = 0; //sets age to 0
        position = new int[] {(int) (Math.random()*worldSize), (int) (Math.random()*worldSize)};
        speed = genes.getSpeed();
        size = genes.getSize();
        maxHP = genes.getHP();
        resistance = genes.getResistance();
        fertility = genes.fertility;
        hp = maxHP;
        absorb_rad = Math.pow(genes.getAbsorb(), 1.0/2) ; //uses a cbrt curve to determine the effect of the distance of absorption
        repro_rad = Math.pow(genes.getRepro(), 1.0/2) ;
        energyOrientBias = genes.getEnergyOrientBias();
        visionDist = genes.getVisionDist();


    }

    public Bird() {
        myGenes = new Genes();
        birth(myGenes);
        myID += id;

        id++;

    }

    public Bird(int age) {
        myGenes = new Genes();
        birth(myGenes);
        myID += id;
        this.age = age;

        id++;

    }



    public Bird(Genes g, String newID, int[] pos, Biome[][] biomeMap) { //These are birds who spawned out of reproduction
        birth(g);
        myGenes = g;
       position = pos;  // moves to mother radius
        currBiome = biomeMap[position[0]][position[1]];
        biomeIn = currBiome.getBiomeName();

        myID = newID;
    }

    public static void setWorldSize(int wSize) {
        worldSize=wSize;
    }

    public void lifeCycle(ArrayList<Bird> deathList, int cycleNum, Biome[][] biomeMap, ArrayList<Energy> nearbyEnergy, ArrayList<Bird> nearbyBirds) {

        currBiome = biomeMap[position[0]][position[1]];
        biomeIn = currBiome.getBiomeName();
        double movementFactor = currBiome.getMovementMult();


        if (hp <= 0 && alive) { // virgin clause added, lets see --> its makes everyone die after one child, so removed it
            alive = false;
            die_age = age; // we will preserve death age for analysis of the lifespan
            deathYear= cycleNum;
            deathList.add(this);
        } else if (age>30 && Math.random()<(0.05) && alive && reproID!=0) { //VIRGIN CLAUSE: cannot die if virgin and less than 10 :`)
            alive = false; //starting to question this, since we have the age hp decrement already
            die_age = age;
            deathList.add(this);
            deathYear= cycleNum;

            //System.out.println("WE COUDL BE IMMORTAL");
            immortal = true;
            //later make older bird more likely to die
        } else {


            hp-= move(speed*movementFactor, nearbyEnergy);
            if (hp<=0 && deathCauseDone==0) {
                deathCause="movement";
                deathCauseDone=1;
            }

            if (age>=10 && juvenile) { //IDEA: make juvenile scale a gene, early mature means less lifespan
                juvenile = false;
            }
            if(juvenile){ //underage
                hp -= 1;
            }
            hp -= (5 + age/50*2);
            if (hp<=0 && deathCauseDone==0) {
                deathCause="hunger";
                deathCauseDone=1;
            }


            if(age > resistance * 50) {
                hp -= age / 5;
                if (hp<=0 && deathCauseDone==0) {
                    deathCause="old age";
                    deathCauseDone=1;
                }
            }


        }

        //...

        age++; // we will let the dead birds age to see each one's stats
    }

    private int move(double speed, ArrayList<Energy> nearbyEnergy){
        double distance = Math.pow(Math.random(), 0.75/1.5)*speed;  // later, want to make the root factor a gene as well, originallt 1.0/1.5, made it 0.5/1.5 to make world bigger

        double randomDir = Math.random()*Math.PI*2; // angle in radians of movement
        //double dir = randomDir;
       double dir = energyOrientBias*angleToClosestEnergy(visionDist, nearbyEnergy) + randomDir*(1-energyOrientBias);
        momentumAngle = dir;
        int dx = (int) (Math.cos(dir)*distance); // make a vector with magnitude distance
        int dy = (int) (Math.sin(dir)*distance);

        //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe
        int newX = (position[0]+dx);
        int newY = position[1]+dy;

        //int[] position = {newX, newY};// changes position
        position[0]=(newX+worldSize)%worldSize;
        position[1]=(newY+worldSize)%worldSize;

        //Later project: incrementally cover distance,
        /* int tempx = 0;
        int tempy = 0;

        for (int i=0; i<=dx; i++) {
            position = [position[0]+1, position[1]+1];
        } */

        //might make return move distance for viewerscape later

        //System.out.println("Bird " + myID + " moved " + (int)distance);
        return (int)(distance);//*speed);
    }
//BOTH absorbEnergy and tryReproduce selection and confirmation is handled by the World
    public void absorbEnergy (Energy e) {
        hp+=(int) (e.consume()/absorb_rad*10);

        if (hp>maxHP) {
            hp = maxHP;
        }
    }

    public void tryReproduce(Bird b2, ArrayList<Bird> newBorns, World myWorld, int minAge, int specLim) {
        //ok so we take the call, do the random, make the genes + ID, then tell bList to make the bird using the new ID and genes
        hp -= 0; // trying to reproduce costs 10 hp --> changed to 1 to support cluster

        String[] b1IdNum = this.getID().split("-");
        String[] b2IdNum = b2.getID().split("-");

        //later, instead of a speciation limit, i will make it a factor of fertility calculation based on genetic distance
        // another thing i can do, risky, is that if the genes are too different then no breed
        // i think i will do the latter, by making a original number (1000), each mutation makes it alter + or -, and if the difference is too much, no mate. this way most times only recent ancestry can mate bc unique random lineage
//        int b1SpecLim;
//        int b2SpecLim;
//
//        int b1Ind = nthIndexOf(this.getID(), '-', specLim);
//        int b2Ind = nthIndexOf(b2.getID(), '-', specLim);
//
//        if (b1Ind != -1) {
//            // Take the sample up to the 6th dash
//            String result = input.substring(0, index);
//            System.out.println(result); // Outputs: 123-45-678-90-123
//        } else {
//            System.out.println("Less than 6 dashes found.");


//        if ((specLim>=b1IdNum.length || specLim>=b2IdNum.length) || specLim==0) {
//            b1SpecLim=1;
//            b2SpecLim=2;
//        } else {
//            // System.out.println(b1IdNum.length + " and "+ b2IdNum.length);
//
//            b1SpecLim = Integer.parseInt(b1IdNum[b1IdNum.length-specLim]);
//            b2SpecLim = Integer.parseInt(b2IdNum[b2IdNum.length-specLim]);
//        }

        double specDiffSqr = Math.pow(myGenes.speciesCode[0]-b2.getGenes().speciesCode[0], 2) + Math.pow(myGenes.speciesCode[1]-b2.getGenes().speciesCode[1], 2) +
                Math.pow(myGenes.speciesCode[2]-b2.getGenes().speciesCode[2], 2) + Math.pow(myGenes.speciesCode[3]-b2.getGenes().speciesCode[3], 2); // distance between species code vectors determine

        if (fertility>=Math.random() && age>=minAge && hp>maxHP*0.7 && specDiffSqr<=specLim*specLim) {  // if fertile and of age (yes, 10 is considered the min. reproductive age of them), then they can mate
            Genes newGenes = Genes.recombine(this.getGenes(), b2.getGenes());
            String newID = createNewID();

            int newX = position[0] + (int)((Math.random()*repro_rad*2)-repro_rad);// spawns baby in absorb radius
            int newY = position[1] + (int)((Math.random()*repro_rad*2)-repro_rad); //IDEA: gene for spawn radius --DONE!!

            //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe]
            newX = (newX+worldSize)%worldSize;
            newY = (newY+worldSize)%worldSize;
            //int[] position = {newX, newY};// changes position

            position[0]=newX;
            position[1]=newY;

            myWorld.addBird(newGenes, newID, newBorns, new int[] {newX, newY});
            //fertility-=0.2;
            hp -= (int) ((maxHP * 0.10) + absorb_rad);
        } //else if (specDiff>specLim) {
//             System.out.println("Speciation!!!");
//        }
    }

    public static int nthIndexOf(String text, char ch, int n) {
        int pos = text.indexOf(ch, 0);
        while (n-- > 1 && pos != -1) {
            pos = text.indexOf(ch, pos + 1);
        }
        return pos;
    }

    private String createNewID () {
        reproID++; // iterate the reproID
        String newID = myID + "-" + reproID; //newID is local, and e.g. parentID is 4-11 and reproID is 3, then the childID = 4-11-3
        return newID;
    }

    public void kill() {
        alive=false;
        resetID();
    }

    private static void resetID () {
        id = 1;
    }


    public int[] getPosition() {
        return position;
    }

    public Genes getGenes () {
        return myGenes;
    }

    public String getID() {
        return myID;
    }

    public int getAge() {
        return age;
    }

    public int getDie_age() {
        return die_age;
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public double getAbsorbRad() {
        return absorb_rad;
    }

    public double getReproRad() {
        return repro_rad;
    }




    public boolean inRadius(int[] otherPos) { // uses THIS radius and other (energy or bird) position
        if (Math.hypot(this.position[0] - otherPos[0], this.position[1] - otherPos[1]) <= absorb_rad && juvenile) {
            return true;
        } else if (Math.hypot(this.position[0] - otherPos[0], this.position[1] - otherPos[1]) <= absorb_rad*age/10 && !juvenile) {
            return true;
        } else {
            return false;
        }

        /// need to make it so that it interacts based on size boundary, NOT center position
    }

    public double angleToClosestEnergy(double radius, ArrayList<Energy> nearbyList) {

        Energy closestEnergy = getClosestEnergy(radius, nearbyList);

        if (closestEnergy == null){
            return momentumAngle;
        }

        int dx = this.getPosition()[0] - closestEnergy.getPosition()[0];
        int dy = this.getPosition()[1] - closestEnergy.getPosition()[1];
        double energyAngle = Math.atan2((float) -dy, (float) dx); //get the angle in radians of the vector from the bird to the closest energy wrt the x-axis

        return energyAngle;
    }

    private Energy getClosestEnergy(double radius, ArrayList<Energy> nearbyList) {
        double closestDistSqr = radius * radius;
        Energy closestEnergy = null;

        for (Energy e : nearbyList) {
            if (e.isSprouted()) {
                int dx = this.getPosition()[0] - e.getPosition()[0];
                int dy = this.getPosition()[1] - e.getPosition()[1];

                double distSqr = dx * dx + dy * dy;

                if (distSqr < closestDistSqr * closestDistSqr && distSqr < radius * radius) {
                    closestDistSqr = distSqr;
                    closestEnergy = e;
                }
            }
        } //now we have should the closest energy
        return closestEnergy;
    }

    public Color getSpeciesColor() {
        int[] code = this.myGenes.speciesCode; // Accesses the [4] length array

        // This scales a shift of +/- 150 into a usable 0-255 RGB range.
        int r = Math.max(0, Math.min(255, 128 + (code[0] - 1000) * 10));
        int g = Math.max(0, Math.min(255, 128 + (code[1] - 1000) * 10));
        int b = Math.max(0, Math.min(255, 128 + (code[2] - 1000) * 10));

        return new Color(r, g, b);
    }

    @Override
    public String toString() {
        if (alive) {
            return "Bird #" + myID + " - age: " + age + " - children: " + reproID + " - hp: " + hp + "/" + maxHP + " - position: ("+ getPosition()[0] + ", " + getPosition()[1] + ") - biome: " + biomeIn +" - immortal" + immortal + "\t\t\t" + myGenes;
        } else {
            return "Bird #" + myID + " - dead at " + die_age + " - children: " + reproID + " - died in year " + deathYear + " - died of " + deathCause +  " - immortal" + immortal +" - " + myGenes;
        }
    }

}


/// NOW its is in world
//
//public class BirdList {
//    private ArrayList<Bird> birdList;
//
//    public BirdList() {
//        birdList = new ArrayList<Bird>();
//    }
//
//
//
//    public void addBird(Genes g, String newID) {
//        Bird b = new Bird(g, newID);
//        birdList.add(b);
//    }
//
//    public void wipeout(){
//        //nothing yet
//    };
//}
//-------------------------------------------------------------
//
//private int[] randomize(){
//    int r = (int) (1000*Math.random());
//
//    int size = r%1000; //size is last digit
//    int speed = ((float) (r/10))/100 //
//
//    int[] pos = [(int) Math.random()*100, (int) Math.random()*100]
//
//
//    // tempStats = [size, speed
//}
//
//private reproduce(){
//    Energy()
//}
//
//public Energy(int[] start) {
//    stats = start; // the first birds can be created custom
//    life();
//}


/* Ideas:
* - make it absorb energy in a radius
* - add fertility to see how many babies made, controlle dby Genes */


/* Notes:
 * - use absorb_d for reproduction for now*/


// git commit -m "slightly improved biomes, not too different; made searching and interaction systems streamlined and less time-consuming to prep for intelligence systems, started energy decomp system"