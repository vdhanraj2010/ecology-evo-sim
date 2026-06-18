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
    public int decompCount=0;
    private int[] position;

    // later optimize to make them not randomzie every time
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

    public void grow(int [][] decompGrid) {
        if (decompGrid[this.position[0]][this.position[1]]<=0) {
            age++;
            if (age > maxAge && size > 0) {
                this.consume();
            } else if (age > size) {

            }
        } else if (decompGrid[this.position[0]][this.position[1]]>0 && !isSprouted()) {
            if (Math.random()<0.1) {
                this.consume();
            }
        } else {
            // this means it is a seed and in the decomp zone, so just freezes growth
            //System.out.println("Seed Frozen");
        }
    }

    public void spore(World myWorld, double eSpread) { // maybe make it seed for 5 years unaffected
        if (age>size && size>0) {
//            int newX = position[0] + (int)((Math.random()*3*eSpread*2)-eSpread*3);// used to be radius 5, now is just 1 block * eSpread
//            int newY = position[1] + (int)((Math.random()*3*eSpread*2)-eSpread*3);

            //check to make sure it isn't crossing bounds; if so, then it wraps around, like a globe]
//            newX = (newX+100)%worldSize;
//            newY = (newY+100)%worldSize;

            double angle = Math.random() * Math.PI * 2;

            double distance = (Math.random() * 4.0 + 1.0) * eSpread;

            int dx = (int) Math.round(Math.cos(angle) * distance);
            int dy = (int) Math.round(Math.sin(angle) * distance);
            int newX = (this.position[0] + dx + worldSize) % worldSize;
            int newY = (this.position[1] + dy + worldSize) % worldSize;



            //int[] position = {newX, newY};// changes position

            //position[0]=newX;
            //position[1]=newY;


            // child gets most
            int childSize = (int) (size*0.7);

            // parent loses HALF
            size -= childSize;

            // Stage or instantiate your new seed object
            myWorld.sporeEnergy(new int[] {newX, newY}, this.size);


        }
    }

    public boolean isSprouted() {
        if (age>2) {
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
        decompCount=3;
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