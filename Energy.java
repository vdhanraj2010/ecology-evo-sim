package populationPlay;
import java.util.ArrayList;

import java.util.ArrayList;

public class Energy {

    private int size;
    private int age;
    private int reproduceAge;
    private static int maxAge = 20;
    private double reproduceChance;

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

        position = new int[] {(int) (Math.random()*100), (int) (Math.random()*100)};


//        return [age, size, position];

    // tempStats = [size, speed
    }

    public void grow() {
        age++;
        if (age>maxAge && size>0) {
            this.consume();
        } else if (age>size) {

        }
    }

    public void spore(World myWorld) { // maybe make it seed for 5 years unaffected
        if (age>size && size>0) {
            int newX = position[0] + (int)(Math.random()*11)-5;
            int newY = position[1] + (int)(Math.random()*11)-5;

            //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe]
            newX = (newX>=100) ? newX-100 : newX;
            newX = (newX<0) ? newX+100 : newX;

            newY = (newY>=100) ? newY-100 : newY;
            newY = (newY<0) ? newY+100 : newY;

            //int[] position = {newX, newY};// changes position
            position[0]=newX;
            position[1]=newY;

            myWorld.sporeEnergy(new int[] {newX, newY}, this.size);


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