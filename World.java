package populationPlay;
import java.lang.reflect.Array;
import java.util.ArrayList;
public class World {
     ArrayList<Bird> birdList;
    ArrayList<Bird> aliveList;
    ArrayList<Bird> deathList;
     ArrayList<Energy> energyList;
     ArrayList<Energy> usableEnergyList;
    int[][] energyLayout;

    int enCount = 0;

    public World () {
        birdList = new ArrayList<Bird>();
        aliveList = new ArrayList<Bird> ();
        deathList = new ArrayList<Bird> ();
        energyList = new ArrayList<Energy>();
        usableEnergyList = new ArrayList<Energy>();
        energyLayout = new int[100][100];
    }

    public void startUp () {
        System.out.println("Starting world up...");
    }/// Later might make this spawn birds and energy but for now manually in Main



    public void oneTick(int cluster, int minAge, int cycleNum) {
        ArrayList<Bird> newBorns = new ArrayList<Bird>();
        ArrayList<Bird> newDeaths = new ArrayList<Bird>();
        for (Bird b : aliveList) { /// for each bird, if its alive then run life cycle and try eating + reproducing

            b.lifeCycle(deathList, cycleNum); /// need to try making a list of deaths in order and why
            if (b.alive) {
                for (Energy e : usableEnergyList) {
                    if (b.inRadius(e.getPosition()) && e.getSize() > 0) { // if energy is in radius AND available
                        b.absorbEnergy(e);
                    }
                }

                /// reproduce check
                for (Bird b2 : aliveList) {
                    if (b.inRadius(b2.getPosition()) && (b2 != b) && b2.alive) { // if Bird2 is not the same bird (no self-incest!!!) and not dead (no necrophilia!!!), then try reproducing (doesnt always work :(   )
                        for (int i=0; i<(Math.random()*cluster); i++) {
                            b.tryReproduce(b2, newBorns, this, minAge);
                        }
                    }
                }

            } else {
                //aliveList.removeIf(a -> a == b); ------ moved below to do by newDeaths
                newDeaths.add(b);
            }
        }
        for (Bird n : newBorns) {
            birdList.add(n);
            aliveList.add(n);
        }

        for (Bird d : newDeaths) {
            aliveList.removeIf(a -> a == d);

        }
    }

    public void energyCycle(double energyThresh, int radius, int maxLocalFood, int sporeNum) {
        for (int i=usableEnergyList.size()-1; i>=0; i--) {
            Energy e = usableEnergyList.get(i);

            int nearby = nearbyEnergy(e, radius);
            double chance = energyThresh * (1.0 - ((double) nearby / maxLocalFood));

            // if (Math.random()>energyThresh) {
            for (int s=0; s<sporeNum; s++) {
                if (Math.random()<= chance) {
                    e.spore(this);
                }
            }

            if (e.getSize()<=0) {
                energyLayout[e.getPosition()[0]][e.getPosition()[1]] --;
                usableEnergyList.removeIf(dead -> dead.equals(e)); // soft launch into using only an alive energy list
                enCount--;
            }

            if (e.getSize()<0) {
                System.out.println("hi im ded");
            }
            e.grow();
        }
    }




    public void spawnEnergy(int amount) {
        for (int i=0; i<amount; i++) {
            Energy en = new Energy();
            energyList.add(en);
            usableEnergyList.add(en);
            energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;
            System.out.println(""+en.getPosition()[0]+" "+en.getPosition()[1]);

            enCount++;
        }
    }

    public void sporeEnergy(int[] coords, int size) {
        Energy en = new Energy(coords, size);
        energyList.add(en);
        usableEnergyList.add(en);
        energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;

        enCount++;
        System.out.println("birthEN!");


    }

    public int nearbyEnergy(Energy e, int radius) {

        int count = 0;

        for(Energy other : energyList) {

            int dx = e.getPosition()[0] - other.getPosition()[0];
            int dy = e.getPosition()[1] - other.getPosition()[1];

            double dist = Math.sqrt(dx*dx + dy*dy);

            if(dist <= radius)
                count++;
        }

        return count;
    }

    public void spawnBirds(int amount, int age) {
        for (int i=0; i<amount; i++) {
            Bird b = new Bird(age);
            birdList.add(b);
            aliveList.add(b);
        }
    }

    public void addBird(Genes g, String newID, ArrayList<Bird> sepList) {
        Bird b = new Bird(g, newID);
        sepList.add(b);
    }

    public void popSave(int amount, int min, int energyAmount) {
        if (aliveList.size()<min) {
            spawnBirds(amount, (int) (Math.random()*30));
            //spawnEnergy(energyAmount/aliveList.size());
        }

    }


    public void genResults(int numDeadShow) {
        System.out.println("\n * Here are the last " + numDeadShow + " deaths: \n");
        int ddNum = 0;
        int ddAgeSum = 0;

        ArrayList<Bird> printDeadList = new ArrayList<Bird> ((numDeadShow< deathList.size()) ? deathList.subList(deathList.size()-numDeadShow-1, deathList.size()-1) : deathList);
        for (Bird b : printDeadList) {
            if (!b.alive) {
                System.out.println(b);
                //ddNum++;
                ddAgeSum+=b.getDie_age();
            }
        }
        ddNum = deathList.size();

        System.out.println("\n * Here are the alive birds: \n");
        int alNum = 0;
        int alAgeSum = 0;
        for (Bird b : birdList) {
            if (b.alive) {
                System.out.println(b);
                alNum++;
                alAgeSum += b.getAge();
            }
        }

        int eNum = 0;
        for (Energy e : energyList) {
            if (e.getSize()>0) {
                eNum++;
            }
        }
        int alAvNum = (alNum==0) ? 0 : alAgeSum/alNum;



        System.out.println("-------------------------------------------------------------" +
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum +
                "\t\t\t Average Alive Age: " + alAvNum + " - Average (recent) Death Age: " + ddAgeSum/numDeadShow);


    }

    public void pause(double sec) {
        try { Thread.sleep((int) (1000*sec)); } catch (InterruptedException e) {System.out.println("error in processing..."); };
    }


    public void endResults(int cluster, int minAge, int cycleNum){
        System.out.println("\n\tLast Cycle\n");
        this.oneTick(cluster, minAge, cycleNum);

        int ddNum = 0;
        int ddAgeSum = 0;
        for (Bird b : deathList) {
            System.out.println(b);
            ddNum++;
            ddAgeSum +=b.getDie_age();

        }

        System.out.println();
        int alNum = 0;
        int alAgeSum = 0;
        for (Bird b : aliveList) {
            System.out.println(b);
            //System.out.println(", " + b.getGenes());
            alNum++;
            alAgeSum += b.getAge();

        }

        int eNum = 0;
        int eNum2 = 0;
        for (Energy e : usableEnergyList) {
            if (e.getSize()>0) {
                eNum++; //this num is more than the amount of net energy spawned (enCount). Why?
            }
        }
        for (Energy d : usableEnergyList) {

                eNum2++; //this num more than eNum, so some in usableEnergyList are <=0. but it is = enCount???
        }

        int alAgeAv = (alNum!=0) ? alAgeSum/alNum : 0;

        int ddAgeAv = (ddNum!=0) ? ddAgeSum/ddNum : 0;

        System.out.println("-------------------------------------------------------------" +
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum + " " + eNum2+
                "\n Average Alive Age: " + alAgeAv + " - Average Death Age: " + ddAgeAv);
        System.out.println("Alive: " + alNum + " - Dead: " + ddNum);

    };

    public void printEnergyLayout() {
        System.out.println("\n");
        for (int i = 0; i<energyLayout.length; i++) {
            System.out.println();
            for (int j=0; j<energyLayout[0].length; j++) {
                String energyPer = "";
                if (energyLayout[i][j]==0) {
                    energyPer = "_";
                } else {
                    energyPer = ""+energyLayout[i][j];
                }
                System.out.print(" "+energyPer);
            }
        }

        System.out.println("\n\n " + (enCount-usableEnergyList.size()) + "   " + enCount);
    }

    public void wipeOut() {
        for (Bird b : birdList) {
            if (b.alive) {
                b.kill();
                System.out.println("Bird #"+ b.getID() + " died.");
            }
        }
        deathList.clear();
    }


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
* - we NEED to make it pause after each generation (run) and list the alive Birds (OR maybe ALL of them, with the dead oens at the end) (ORRRR the dead ones with annoucnement) --DONE!!!
* - We need to find a way for 1-strings to stops being prevalent. maybe a bigger radius, or smt to make each one reproduce more
*  - LETS make a graph option where u can graph the lineage of a ancestor
* */

