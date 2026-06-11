package populationPlay;// import statements
import java.util.ArrayList;

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


    private static int id = 1; //id number that starts all origin birds
    private String myID = ""; // ID of the Bird
    private int reproID = 0; //
    public boolean alive = true;
    public boolean juvenile = true;
    public int deathYear = 0;
    public String deathCause = "";
    private Genes myGenes;

    public void birth(Genes genes) {
        age = 0; //sets age to 0
        position = new int[] {(int) (Math.random()*100), (int) (Math.random()*100)};
        speed = genes.getSpeed();
        size = genes.getSize();
        maxHP = genes.getHP();
        resistance = genes.getResistance();
        fertility = genes.fertility;
        hp = maxHP;
        absorb_rad = Math.pow(genes.getAbsorb(), 1.0/2) ; //uses a cbrt curve to determine the effect of the distance of absorption

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



    public Bird(Genes g, String newID, int[] pos) { //These are birds who spawned out of reproduction
        birth(g);
        myGenes = g;
       position = pos;  // moves to mother radius

        myID = newID;
    }

    public void lifeCycle(ArrayList<Bird> deathList, int cycleNum) {
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
            //later make older bird more likely to die
        } else {

            hp-= move(speed);

            if(juvenile){ //underage
                hp -= 1;
            } else if (age>=10 && juvenile) {
                juvenile = false;
            }

            if(age > resistance * 50) {
                hp -= age / 5;
            }

        }

        //...

        age++; // we will let the dead birds age to see each one's stats
    }

    private int move(double speed) {
        double distance = Math.pow(Math.random(), 1.0/1.5)*speed;  // later, want to make the root factor a gene as well

        double dir = Math.random()*Math.PI*2; // angle in radians of movement
        int dx = (int) (Math.cos(dir)*distance); // make a vector with magnitude distance
        int dy = (int) (Math.sin(dir)*distance);

        //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe
        int newX = (position[0]+dx);
        newX = (newX>100) ? newX-100 : newX;
        newX = (newX<0) ? newX+100 : newX;
        int newY = position[1]+dy;
        newY = (newY>100) ? newY-100 : newY;
        newY = (newY<0) ? newY+100 : newY;


        //int[] position = {newX, newY};// changes position
        position[0]=newX;
        position[1]=newY;

        //Later project: incrementally cover distance,
        /* int tempx = 0;
        int tempy = 0;

        for (int i=0; i<=dx; i++) {
            position = [position[0]+1, position[1]+1];
        } */

        //might make return move distance for viewerscape later

        //System.out.println("Bird " + myID + " moved " + (int)distance);
        return (int)distance;
    }
//BOTH absorbEnergy and tryReproduce selection and confirmation is handled by the World
    public void absorbEnergy (Energy e) {
        hp+=e.consume();

        if (hp>maxHP) {
            hp = maxHP;
        }
    }

    public void tryReproduce(Bird b2, ArrayList<Bird> newBorns, World myWorld, int minAge) {
        //ok so we take the call, do the random, make the genes + ID, then tell bList to make the bird using the new ID and genes
        hp -= 5; // trying to reproduce costs 10 hp --> changed to 1 to support cluster
        if (fertility>=Math.random() && age>=minAge && hp>maxHP*0.7) {  // if fertile and of age (yes, 10 is considered the min. reproductive age of them), then they can mate
            Genes newGenes = Genes.recombine(this.getGenes(), b2.getGenes());
            String newID = createNewID();

            int newX = position[0] + (int)((Math.random()*absorb_rad*2)-absorb_rad);// spawns baby in absorb radius
            int newY = position[1] + (int)((Math.random()*absorb_rad*2)-absorb_rad);

            //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe]
            newX = (newX>=100) ? newX-100 : newX;
            newX = (newX<0) ? newX+100 : newX;

            newY = (newY>=100) ? newY-100 : newY;
            newY = (newY<0) ? newY+100 : newY;

            //int[] position = {newX, newY};// changes position
            position[0]=newX;
            position[1]=newY;

            myWorld.addBird(newGenes, newID, newBorns, new int[] {newX, newY});
            //fertility-=0.2;
            hp -= 5;
        }
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

    @Override
    public String toString() {
        if (alive) {
            return "Bird #" + myID + " - age: " + age + " - children: " + reproID + " - hp: " + hp + "/" + maxHP + " - position: ("+ getPosition()[0] + ", " + getPosition()[1] + ")\t\t\t" + myGenes;
        } else {
            return "Bird #" + myID + " - dead at " + die_age + " - children: " + reproID + " - died in year " + deathYear + " - " + myGenes;
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


