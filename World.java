package populationPlay;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
public class World {
     ArrayList<Bird> birdList;
    ArrayList<Bird> aliveList;
    ArrayList<Bird> deathList;
     ArrayList<Energy> energyList;
     ArrayList<Energy> usableEnergyList;
    int[][] decompGrid;
    ArrayList<Bird>[][] birdGrid;
     ArrayList<Energy>[][] energyGrid;
    int[][] energyLayout;
    Biome biomeMap[][];
    String biomeGrid;
    int worldSize;
    double bucketSize;
    final int numBuckets = 20;


    int enCount = 0;
    int parthCount=0;
    int birthCount = 0;
    String bType = "";

    public World (int worldSize) {
        this.worldSize=worldSize;
        birdList = new ArrayList<Bird>();
        aliveList = new ArrayList<Bird> ();
        deathList = new ArrayList<Bird> ();
        energyList = new ArrayList<Energy>();
        usableEnergyList = new ArrayList<Energy>();
        energyLayout = new int[worldSize][worldSize]; //usually 100
        decompGrid = new int[worldSize][worldSize];
    }

    public void startUp (String biomeType) {
        System.out.println("Starting world up...");
        Bird.setWorldSize(worldSize);
        Energy.setWorldSize(worldSize);
        bType = biomeType;
        initializeSpatialGrids(worldSize);
        System.out.println("Biomes set to map: \n\n"+setBiomeMap(biomeType)+ "\n");
    }/// Later might make this spawn birds and energy but for now manually in Main

    public void initializeSpatialGrids(int worldSize) {
        //calculate the width of buckets
        this.bucketSize = (double) worldSize / numBuckets;

        this.birdGrid = new ArrayList[numBuckets][numBuckets];
        this.energyGrid = new ArrayList[numBuckets][numBuckets];

        resetGrids();
    }


    public void oneTick(int cluster, int minAge, int cycleNum, int parthNum, int specLim) {
        resetGrids();
        ArrayList<Bird> newBorns = new ArrayList<Bird>();
        ArrayList<Bird> newDeaths = new ArrayList<Bird>();
        for (Bird b : aliveList) { /// for each bird, if its alive then run life cycle and try eating + reproducing

            b.lifeCycle(deathList, cycleNum, biomeMap); /// need to try making a list of deaths in order and why
            ArrayList<Energy> nearbyEnergyList = (ArrayList<Energy>) getNearbyOrgs(b.getPosition()[0], b.getPosition()[1], b.getGenes().getAbsorb(), energyGrid); //find all nearby energy in absorbR
            ArrayList<Bird> nearbyBirdList = getNearbyOrgs(b.getPosition()[0], b.getPosition()[1], b.getGenes().getAbsorb(), birdGrid); //find all nearby birds in absorbR -- IF we make another reproRad gene, this will need to be switched out

        //insert grid reset
            if (b.alive) {
                for (Energy e : nearbyEnergyList) {
                    if ((b.inRadius(e.getPosition()) && b.getHP()<b.getMaxHP()) && (e.getSize() > 0 && e.isSprouted())) { // if energy is in radius AND available
                        b.absorbEnergy(e);
                    }
                }

                /// reproduce check
                for (Bird b2 : nearbyBirdList) {
                    if (b.inRadius(b2.getPosition()) && (b2 != b) && b2.alive ){//&& b.getAge()>minAge) { // if Bird2 is not the same bird (no self-incest!!!) and not dead (no necrophilia!!!), then try reproducing (doesnt always work :(   )
                        for (int i=0; i<(Math.random()*cluster); i++) {
                            b.tryReproduce(b2, newBorns, this, minAge, specLim); //minAge not needed anymore
                        }
                    }
                }
                if (aliveList.size()<=parthNum && Math.random()<0.25) { //Parthenogenesis in population stress
                    b.tryReproduce(b, newBorns, this, minAge, 0);
                    System.out.println("PARTHEN!!!");
                    parthCount++;
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

        for (int deX=0; deX<decompGrid.length; deX++) {
            for (int deY=0; deY<decompGrid[0].length; deY++) {
                if (decompGrid[deX][deY]>0) {
                    decompGrid[deX][deY]--;
                }
            }

        }
    }

    public void energyCycle(double energyThresh, int radius, int maxLocalFood, int sporeNum) {
        for (int i=usableEnergyList.size()-1; i>=0; i--) {
            Energy e = usableEnergyList.get(i);
            Biome currBiome = biomeMap[e.getPosition()[0]][e.getPosition()[1]];
            double eGrowth = currBiome.getEnergyGrowth();
            double eSpread = currBiome.getEnergySpread();

          //  int nearby = nearbyEnergy(e, (int)(radius));
            int nearby = nearbyEnergy(e, radius, getNearbyOrgs(e.getPosition()[0], e.getPosition()[1], radius, energyGrid));
            double chance = energyThresh * (1.0 - ((double) nearby / (maxLocalFood*eGrowth)));

            chance = Math.min(1, Math.max(0, chance));

            // if (Math.random()>energyThresh) {
            for (int s=0; s<sporeNum*eGrowth; s++) {
                if (Math.random()<= chance) {
                    e.spore(this, eSpread);
                }
            }

            if (e.getSize()==0) {
                if (e.getSize() < 0) {
                    System.out.println("hi im ded");
                }
                energyLayout[e.getPosition()[0]][e.getPosition()[1]] = Math.max(0, energyLayout[e.getPosition()[0]][e.getPosition()[1]] - 1);
                // usableEnergyList.removeIf(dead -> dead.equals(e)); // soft launch into using only an alive energy list
                decompGrid[e.getPosition()[0]][e.getPosition()[1]] = (int) (currBiome.getDecompRate()*3);
                usableEnergyList.remove(i);

                enCount--;
            }

            e.grow(decompGrid);
        }

//        for (int j = decompGrid.length; j>=0; j--) {
//            Energy e = ;
//            Biome currBiome = biomeMap[e.getPosition()[0]][e.getPosition()[1]];
//            double decompRate = currBiome.getDecompRate();
//            int decompTime = (int) (decompRate*5);
//
//            if (e.decompCount>0) {
//                e.decompCount--;
//            }
//        }
    }



    public String setBiomeMap (String biomeType) {
        biomeMap = new Biome[worldSize][worldSize];
        System.out.println(worldSize);

        String biomeDisplay = "";
        biomeGrid = "FFFPPP" +
                "FFFPPP" +
                "FFFPPP" +
                "SSSMMM" +
                "SSSMMM" +
                "SSSMMM"; // imagine each 9 blocks is a 25x25 grid

        if (biomeType.equalsIgnoreCase("grid")) {
            for (int i=0; i<worldSize/2; i++) {
                for (int j=0; j<worldSize/2; j++) {
                    biomeMap[i][j]= new Biome("F");
                    biomeDisplay+="F";
                }
                for (int j=worldSize/2; j<worldSize; j++) {
                    biomeMap[i][j]= new Biome("P");
                    biomeDisplay+="P";
                }
                biomeDisplay+="\n";
            }
            for (int i=worldSize/2; i<worldSize; i++) {
                for (int j=0; j<worldSize/2; j++) {
                    biomeMap[i][j]= new Biome("S");
                    biomeDisplay+="S";
                }
                for (int j=worldSize/2; j<worldSize; j++) {
                    biomeMap[i][j]= new Biome("M");
                    biomeDisplay+="D";
                }
                biomeDisplay+="\n";
            }

        } else {
            System.out.print("Choose a preset biome map (grid): \n\t >>> ");
        }
        return biomeDisplay;
    }

    public void resetGrids() {
        // wipe old grids clean
        for (int x = 0; x < numBuckets; x++) {
            for (int y = 0; y < numBuckets; y++) {
                birdGrid[x][y] = new ArrayList<>();
                energyGrid[x][y] = new ArrayList<>();
            }
        }

        // fill the tick energy grid
        for (Energy e : usableEnergyList) {
            int eGridX = (int) (e.getPosition()[0] / bucketSize);
            int eGridY = (int) (e.getPosition()[1] / bucketSize);

            // make sure buckets can reach around the torus
            eGridX = Math.max(0, Math.min(eGridX, numBuckets - 1));
            eGridY = Math.max(0, Math.min(eGridY, numBuckets - 1));

            energyGrid[eGridX][eGridY].add(e);
        }

        // fill the tick bird grid based on positions
        for (Bird b : aliveList) {
            int bGridX = (int) (b.getPosition()[0] / bucketSize);
            int bGridY = (int) (b.getPosition()[1] / bucketSize);

            bGridX = Math.max(0, Math.min(bGridX, numBuckets - 1));
            bGridY = Math.max(0, Math.min(bGridY, numBuckets - 1));

            birdGrid[bGridX][bGridY].add(b);
        }
    }


    public <T> ArrayList<T> getNearbyOrgs(int xPos, int yPos, double searchRadius, ArrayList<T>[][] orgGrid){ //GUYS ths T color is SOOOOOOO prettyyyyy
        ArrayList<T> localList = new ArrayList<>();

        int inBucketX = (int) (xPos/bucketSize);
        int inBucketY = (int) (yPos/bucketSize);

        inBucketX = Math.max(0, Math.min(inBucketX, numBuckets - 1)); //config to make sure it counts in base amount
        inBucketY = Math.max(0, Math.min(inBucketY, numBuckets - 1));

        int buckRadius = (int) Math.ceil(searchRadius/bucketSize);
        buckRadius = Math.min(buckRadius, numBuckets / 2); //so it doesnt count same bucket

        for (int dx=-buckRadius; dx<=buckRadius; dx++) {
            for (int dy=-buckRadius; dy<=buckRadius; dy++) {
                int currBucketX = (inBucketX+dx + numBuckets) % numBuckets;
                int currBucketY = (inBucketY+dy + numBuckets) % numBuckets;

                localList.addAll(orgGrid[currBucketX][currBucketY]); //add all the energy in that bucket to the local list to search for
            }
        }
        return localList;
    }

    /* public ArrayList<Bird> getNearbyBirds(int xPos, int yPos, double searchRadius){
        ArrayList<Bird> localList = new ArrayList<>();

        int inBucketX = (int) (xPos/bucketSize);
        int inBucketY = (int) (yPos/bucketSize);

        inBucketX = Math.max(0, Math.min(inBucketX, numBuckets - 1)); //config to make sure it counts in base amount
        inBucketY = Math.max(0, Math.min(inBucketY, numBuckets - 1));

        int buckRadius = (int) Math.ceil(searchRadius/bucketSize);
        buckRadius = Math.min(buckRadius, numBuckets / 2); //so it doesnt count same bucket

        for (int dx=-buckRadius; dx<=buckRadius; dx++) {
            for (int dy=-buckRadius; dy<=buckRadius; dy++) {
                int currBucketX = (inBucketX+dx + numBuckets) % numBuckets;
                int currBucketY = (inBucketY+dy + numBuckets) % numBuckets;

                localList.addAll(birdGrid[currBucketX][currBucketY]); //add all the energy in that bucket to the local list to search for
            }
        }
        return localList;
    } */



    public void spawnEnergy(int amount) {
        for (int i=0; i<amount; i++) {
            Energy en = new Energy();
            energyList.add(en);
            usableEnergyList.add(en);
            energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;
            // System.out.println(""+en.getPosition()[0]+" "+en.getPosition()[1]);

            enCount++;
        }
    }

    public void spawnEnergyMap(int map, int amount){
        //make switch later
        if (map==1) { //square in top left corner
            for (int i=0; (i*i)<amount; i++) {
                for (int j=0; (j*j)<amount; j++) {
                    Energy en = new Energy(new int[] {i, j});
                    energyList.add(en);
                    usableEnergyList.add(en);
                    energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;

                    enCount++;
                }
            }
        } else if (map ==2) {
            int startPt;

            startPt = 50 - (int)(Math.sqrt(amount)/2); //center (50) - length/2

            for (int i=0; (i*i)<amount; i++) {
                for (int j=0; (j*j)<amount; j++) {
                    Energy en = new Energy(new int[] {i+startPt, j+startPt});
                    energyList.add(en);
                    usableEnergyList.add(en);
                    energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;

                    enCount++;
                }
            }
        } else {
            System.out.println("Map not found.");
        }
    }

    public void sporeEnergy(int[] coords, int size) {
        Energy en = new Energy(coords, size);
        energyList.add(en);
        usableEnergyList.add(en);
        energyLayout[en.getPosition()[0]][en.getPosition()[1]] ++;

        enCount++;
        //System.out.println("birthEN!");


    }

    public int nearbyEnergy(Energy e, int radius, ArrayList<Energy> nearbyList) {

        int count = 0;

        for(Energy other : nearbyList) {

            if (other.getSize() <= 0) continue;

            int dx = e.getPosition()[0] - other.getPosition()[0];
            int dy = e.getPosition()[1] - other.getPosition()[1];

            double distSqr = dx*dx + dy*dy ;

            if(distSqr <= radius*radius)
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

    public void addBird(Genes g, String newID, ArrayList<Bird> sepList, int[] newPos) {
        Bird b = new Bird(g, newID, newPos, biomeMap);
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

        int BinP=0;
        int BinF=0;
        int BinS=0;
        int BinM=0;

        for (Bird b : aliveList) {
            if (b.alive) {
                System.out.println(b);
                alNum++;
                alAgeSum += b.getAge();
            }

                Biome currBiome = biomeMap[b.getPosition()[0]][b.getPosition()[1]];
                String biomeIn = currBiome.getBiomeName();

                switch (biomeIn) {
                    case "Plains":
                        BinP++;
                        break;
                    case "Forest":
                        BinF++;
                        break;
                    case "Swamp":
                        BinS++;
                        break;
                    case "Mountain":
                        BinM++;
                        break;
                    default:
                        System.out.println("not in biome?");
                }
        }

        int eNum = 0;
        int EinP=0;
        int EinF=0;
        int EinS=0;
        int EinM=0;
        for (Energy e : usableEnergyList) {
            if (e.getSize()>0) {
                eNum++;
            }

            Biome currBiome = biomeMap[e.getPosition()[0]][e.getPosition()[1]];
            String biomeIn = currBiome.getBiomeName();

            switch (biomeIn) {
                case "Plains":
                    EinP++;
                    break;
                case "Forest":
                    EinF++;
                    break;
                case "Swamp":
                    EinS++;
                    break;
                case "Mountain":
                    EinM++;
                    break;
                default:
                    System.out.println("not in biome?");
            }
        }
        int alAvNum = (alNum==0) ? 0 : alAgeSum/alNum;



        System.out.println("-------------------------------------------------------------" +
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum +
                "\t\t\t Average Alive Age: " + alAvNum + " - Average (recent) Death Age: " + ddAgeSum/numDeadShow + " Partho Count: " + parthCount +
                "\n Energy in\t Plains: "+EinP+" - Forest: "+EinF+" - Swamp: "+EinS+" - Mountain: "+EinM + "\t Birds in\t Plains: "+BinP+" - Forest: "+BinF+" - Swamp: "+BinS+" - Mountain: "+BinM);


    }

    public void pause(double sec) {
        try { Thread.sleep((int) (1000*sec)); } catch (InterruptedException e) {System.out.println("error in processing..."); };
    }


    public void endResults(int cluster, int minAge, int cycleNum){
        System.out.println("\n\tLast Cycle\n");
        this.oneTick(cluster, minAge, cycleNum, 5, 10);

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

        int BinP=0;
        int BinF=0;
        int BinS=0;
        int BinM=0;

        for (Bird b : aliveList) {
            if (b.alive) {
                System.out.println(b);
                alNum++;
                alAgeSum += b.getAge();
            }

            Biome currBiome = biomeMap[b.getPosition()[0]][b.getPosition()[1]];
            String biomeIn = currBiome.getBiomeName();

            switch (biomeIn) {
                case "Plains":
                    BinP++;
                    break;
                case "Forest":
                    BinF++;
                    break;
                case "Swamp":
                    BinS++;
                    break;
                case "Mountain":
                    BinM++;
                    break;
                default:
                    System.out.println("not in biome?");
            }
        }

        int eNum = 0;
        int eNum2 = 0;
        int EinP=0;
        int EinF=0;
        int EinS=0;
        int EinM=0;
        for (Energy e : usableEnergyList) {
            if (e.getSize()>0) {
                eNum++;
            }

            Biome currBiome = biomeMap[e.getPosition()[0]][e.getPosition()[1]];
            String biomeIn = currBiome.getBiomeName();

            switch (biomeIn) {
                case "Plains":
                    EinP++;
                    break;
                case "Forest":
                    EinF++;
                    break;
                case "Swamp":
                    EinS++;
                    break;
                case "Mountain":
                    EinM++;
                    break;
                default:
                    System.out.println("not in biome?");
            }
        }
        for (Energy d : usableEnergyList) {

                eNum2++; //this num more than eNum, so some in usableEnergyList are <=0. but it is = enCount???
        }

        int alAgeAv = (alNum!=0) ? alAgeSum/alNum : 0;

        int ddAgeAv = (ddNum!=0) ? ddAgeSum/ddNum : 0;

        System.out.println("-------------------------------------------------------------" +
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum + " " + eNum2+
                "\n Energy in\t Plains: "+EinP+" - Forest: "+EinF+" - Swamp: "+EinS+" - Mountain: "+EinM + "\t Birds in\t Plains: "+BinP+" - Forest: "+BinF+" - Swamp: "+BinS+" - Mountain: "+BinM+
                "\n Average Alive Age: " + alAgeAv + " - Average Death Age: " + ddAgeAv);
        System.out.println("Alive: " + alNum + " - Dead: " + ddNum + " - Partho Count: " + parthCount + " - Birth Count: " + birdList.size());

        System.out.println("Biomes set to map: \n\n"+setBiomeMap(bType)+ "\n");

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


    public void saveSimulation(String filename, int currCycle) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));

            out.println("Cycle: " + currCycle);
            out.println("Alive: " + aliveList.size());
            out.println("Dead: " + deathList.size());

            out.println("\n=== Alive Birds ===");

            System.out.println();
            int alNum = 0;
            int alAgeSum = 0;
            for (Bird b : aliveList) {
                System.out.println(b);
                //System.out.println(", " + b.getGenes());
                alNum++;
                alAgeSum += b.getAge();

            }


            out.println("\n\n=== Dead Birds ===");

            int ddNum = 0;
            int ddAgeSum = 0;
            for (Bird b : deathList) {
                out.println(b);
                ddNum++;
                ddAgeSum +=b.getDie_age();

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



            out.close();

            System.out.println("Simulation saved.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Bird> getAliveList() {
        return aliveList;
    }

    public ArrayList<Energy> getUsableEnergyList() {
        return usableEnergyList;
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

