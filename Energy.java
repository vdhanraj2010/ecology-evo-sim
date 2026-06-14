package populationPlay;
import java.util.ArrayList;

import java.util.ArrayList;

public class Energy {

    private int size;
    private int age;
    private int reproduceAge;
    private static int maxAge = 25;
    private double reproduceChance;
    public static int worldSize;

    public boolean alive = true; // not used
    private int[] position;

    public Energy() {
        // stats = randomize();
        randomize();
        age = 0;
    }

    public Energy(int[] start) {
        randomize();
        position = start;
    }

    public Energy(int[] start, int newSize) {
        randomize();
        position = start;
        size = newSize;
        //stats = start; // generate energy in a specific area, specific size
    }

    public void randomize(){
        int r = (int) (1000*Math.random());

        size = (int) (r%10) +1; //size is last digit +1

        position = new int[] {(int) (Math.random()*worldSize), (int) (Math.random()*worldSize)};


//        return [age, size, position];

    // tempStats = [size, speed
    }

    public static void setWorldSize(int wSize) {
        worldSize=wSize;
    }

    public void grow() {
        age++;
        if (age>maxAge && size>0) {
            this.consume();
        } else if (age>size) {

        }
    }

    public void spore(World myWorld, double eSpread) { // maybe make it seed for 5 years unaffected
        if (age>size && size>0) {
            int newX = position[0] + (int)((Math.random()*3*eSpread*2)-eSpread);// used to be radius 5, now is just 1 block * eSpread
            int newY = position[1] + (int)((Math.random()*3*eSpread*2)-eSpread);

            //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe]
            newX = (newX>=worldSize) ? newX-worldSize : newX;
            newX = (newX<0) ? newX+worldSize : newX;

            newY = (newY>=worldSize) ? newY-worldSize : newY;
            newY = (newY<0) ? newY+worldSize : newY;

            //int[] position = {newX, newY};// changes position
            position[0]=newX;
            position[1]=newY;

            // child gets HALF
            int childSize = (int) (size*0.7);

            // parent loses HALF
            size -= childSize;

            myWorld.sporeEnergy(new int[] {newX, newY}, this.size);


        }
    }

    public boolean isSprouted() {
        if (age>3) {
            return true;
        } else {
            return false;
        }
    }



    public int getAge() {
        return age;
    }

    public void ageUp() {
        age++;
    }

    public int getSize(){
        return size;
    }

    public int[] getPosition(){
        return position;
    }

    public int consume() {
        int food = size;
        size = 0;
        return food;
    }



}
/// NOW its is in world
 //
//public class EnergySpread {
//    private ArrayList<Energy> energyList;
//
//    public EnergySpread () {
//        return energyList;
//    }
//
//    public void spawnEnergy(int amount) {
//        for (int i=0; i<=amount; i++) {
//            Energy en = new Energy();
//            energyList.add(en);
//        }
//    }
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
//
//
//}



/* Motes:
 * energy overlap allowed in this version
 *
 * - remove consumed energy = new update later
 *
 */