package populationPlay;
import java.util.ArrayList;

import java.util.ArrayList;

public class Energy {

    private int size;
    private int age;

    public boolean alive = true; // not used
    private int[] position;

    public void randomize(){
        int r = (int) (1000*Math.random());

        size = (int) (r%10) +1; //size is last digit +1

        position = new int[] {(int) (Math.random()*100), (int) (Math.random()*100)};


//        return [age, size, position];

    // tempStats = [size, speed
    }

    public Energy() {
        // stats = randomize();
        randomize();
        age = 0;
    }

    public Energy(int[] start) {
        //stats = start; // generate energy in a specific area, specific size
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