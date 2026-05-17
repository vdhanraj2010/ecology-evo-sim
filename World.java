package populationPlay;
import java.util.ArrayList;
public class World {
     ArrayList<Bird> birdList;
     ArrayList<Energy> energyList;

    public World () {
        birdList = new ArrayList<Bird>();
        energyList = new ArrayList<Energy>();
    }

    public void startUp () {
        System.out.println("Starting world up...");
    }/// Later might make this spawn birds and energy but for now manually in Main



    public void oneTick() {
        ArrayList<Bird> newBorns = new ArrayList<Bird>();
        for (Bird b : birdList) { /// for each bird, if its alive then run life cycle and try eating + reproducing

            b.lifeCycle();
            if (b.alive) {
                for (Energy e : energyList) {
                    if (b.inRadius(e.getPosition()) && e.getSize() > 0) { // if energy is in radius AND available
                        b.absorbEnergy(e);
                    }
                }

                /// reproduce check
                for (Bird b2 : birdList) {
                    if (b.inRadius(b2.getPosition()) && (b2 != b) && b2.alive) { // if Bird2 is not the same bird (no self-incest!!!) and not dead (no necrophilia!!!), then try reproducing (doesnt always work :(   )
                        b.tryReproduce(b2, newBorns, this);
                    }
                }

            }

        }


        for (Bird n : newBorns) {
            birdList.add(n);
        }



    }




    public void spawnEnergy(int amount) {
        for (int i=0; i<amount; i++) {
            Energy en = new Energy();
            energyList.add(en);
        }
    }

    public void spawnBirds(int amount) {
        for (int i=0; i<amount; i++) {
            Bird b = new Bird();
            birdList.add(b);
        }
    }

    public void addBird(Genes g, String newID, ArrayList<Bird> sepList) {
        Bird b = new Bird(g, newID);
        sepList.add(b);
    }

    public void popSave(int amount, int min) {
        if (birdList.size()<min) {
            spawnBirds(amount);
        }
    }


    public void genResults() {
        for (Bird b : birdList) {
            if (b.alive) {
                System.out.println(b);
            }
        }
        for (Bird b : birdList) {
            if (!b.alive) {
                System.out.println(b);
            }
        }


    }

    public void pause(double sec) {
        try { Thread.sleep((int) (1000*sec)); } catch (InterruptedException e) {System.out.println("error in processing..."); };
    }


    public void wipeout(){
        System.out.println("\n\tLast Cycle\n");
        this.oneTick();
        int alNum = 0;
        for (Bird b : birdList) {
            if (b.alive) {
                System.out.println(b);
                //System.out.println(", " + b.getGenes());
                alNum++;
            }
        }
        System.out.println();
        int ddNum = 0;
        for (Bird b : birdList) {
            if (!b.alive) {
                System.out.println(b);
                ddNum++;
            }
        }

        System.out.println("Alive: " + alNum + " - Dead: " + ddNum);

    };


//
//    public void energyCycle() { /// not used yet bc im not sure how the energyCycle will work and where it will be placed
//        for (Energy e : energyList) {
//            e.ageUp();
//
//            if (e.getAge()>20) {
//                energyList.remove(get)
//            }
//        }
//    }


}

/* Notes:
* - we NEED to make it pause after each generation (run) and list the alive Birds (OR maybe ALL of them, with the dead oens at the end) (ORRRR the dead ones with annoucnement)
* */

