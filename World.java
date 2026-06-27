package populationPlay;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class World {
     ArrayList<Bird> birdList;
    ArrayList<Bird> aliveList;
    ArrayList<Bird> deathList;
    ArrayList<Bird> recentDeathList; //since accumulated dead birds are not necessary, i will make list of their statements, print them instead.
    StringBuilder deathMemorandum;
    ArrayList<Bird> localBirdList = new ArrayList<>();
    ArrayList<Energy> localEnergyList = new ArrayList<>();
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
    final int numBuckets = 50;

    public static final Biome PLAINS = new Biome("P");
    public static final Biome FOREST = new Biome("F");
    public static final Biome DESERT = new Biome("D");
    public static final Biome SWAMP = new Biome("S");
    public static final Biome COAST = new Biome("C");
    public static final Biome MOUNTAIN = new Biome("M");
    public static final Biome JUNGLE = new Biome("J");
    public static final Biome TUNDRA = new Biome("T");
    public static final Biome VOLCANO = new Biome("V");
    public static final Biome OCEAN = new Biome("O");




    int enCount = 0;
    int parthCount=0;
    int birthCount = 0;
    int deathCount = 0;
    int ddAgeSum = 0;
    String bType = "";

    public World (int worldSize) {
        this.worldSize=worldSize;
        birdList = new ArrayList<Bird>();
        aliveList = new ArrayList<Bird> ();
        deathList = new ArrayList<Bird> ();
        recentDeathList = new ArrayList<Bird> ();
        deathMemorandum = new StringBuilder();
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


        for (int x = 0; x < numBuckets; x++) { //init the arrays so it doesnt error when clear
            for (int y = 0; y < numBuckets; y++) {
                birdGrid[x][y] = new ArrayList<>();
                energyGrid[x][y] = new ArrayList<>();
            }
        }

        resetGrids();
    }


    public void oneTick(int cluster, int minAge, int cycleNum, int parthNum, int specLim) {
        resetGrids();
        ArrayList<Bird> newBorns = new ArrayList<Bird>();
        ArrayList<Bird> newDeaths = new ArrayList<Bird>();
        for (Bird b : aliveList) { /// for each bird, if its alive then run life cycle and try eating + reproducing

            ArrayList<Energy> visionEnergy = getNearbyOrgs(b.getPosition(), b.getGenes().getVisionDist(), localEnergyList, energyGrid); //find all nearby energy in absorbR
            ArrayList<Bird> visionBirds = getNearbyOrgs(b.getPosition(), b.getGenes().getVisionDist(), localBirdList, birdGrid); //find all nearby birds in absorbR -- IF we make another reproRad gene, this will need to be switched out
            b.lifeCycle(cycleNum, deathMemorandum, biomeMap, visionEnergy, visionBirds); /// need to try making a list of deaths in order and why -- Done!

        //insert grid reset
            if (b.alive) {
                ArrayList<Energy> nearbyEnergyList = getNearbyOrgs(b.getPosition(), b.getAbsorbRad(), localEnergyList, energyGrid); //find all nearby energy in absorbR
                ArrayList<Bird> nearbyBirdList = getNearbyOrgs(b.getPosition(), b.getReproRad(), localBirdList, birdGrid);

                for (Energy e : nearbyEnergyList) {
                    if ((b.inRadius(b.getAbsorbRad(), e.getPosition()) && b.getHP()<b.getMaxHP()) && (e.getSize() > 0 && e.isSprouted())) { // if energy is in radius AND available
                        b.absorbEnergy(e);
                    }
                }

                /// reproduce check
                for (Bird b2 : nearbyBirdList) {
                    if (b.inRadius(b.getReproRad(), b2.getPosition()) && (b2 != b) && b2.alive ){//&& b.getAge()>minAge) { // if Bird2 is not the same bird (no self-incest!!!) and not dead (no necrophilia!!!), then try reproducing (doesnt always work :(   )
                        for (int i=0; i<(Math.random()*cluster); i++) {
                            b.tryReproduce(b2, newBorns, this, minAge, specLim); //minAge not needed anymore
                            if (b.getID().contains("Ave")) {System.out.println("i alive and happy");}   //   System.out.println(b.getID()+"wow");
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
                deathCount++;
                ddAgeSum+= b.getAge();
            }
        }

        birdList.addAll(newBorns);
        aliveList.addAll(newBorns);
        aliveList.removeAll(newDeaths);
        recentDeathList.addAll(newDeaths);

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
            int nearby = nearbyEnergy(e, radius, getNearbyOrgs(e.getPosition(), radius, localEnergyList, energyGrid)); //e.getPosition()[0], e.getPosition()[1]
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

        } else if (biomeType.equalsIgnoreCase("map3")) {
            loadMapPreset("src/populationPlay/mapPresets", "map3.txt");

        } else if (biomeType.equalsIgnoreCase("map4")) {
            loadMapPreset("src/populationPlay/mapPresets", "map4.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-1")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-1.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-2")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-2.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-3")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-3.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-4")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-4.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-5")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-5.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-6")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-6.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-Hyrule")) {
            loadMapPreset("src/populationPlay/mapPresets", "world_500-Hyrule.txt");

        } else if (biomeType.equalsIgnoreCase("world_500-Paldea")) {
            loadMapPreset("src/populationPlay/mapPresets", "organic_world_map_500x500.txt");

        } else {
            System.out.print("Choose a preset biome map (grid, map3): \n\t >>> ");
        }
        return biomeDisplay;
    }

    public void loadMapPreset(String folderName, String fileName) {
        String fullPath = folderName + "/" + fileName;
        File mapFile = new File(fullPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {

            for (int y = 0; y < worldSize; y++) {
                String line = reader.readLine();

                if (line == null) {
                    System.out.println("Error: File ended early at line " + y);
                    break;
                }

                // Loop horizontally
                for (int x = 0; x < worldSize; x++) {
                    char biomeLetter = line.charAt(x);
                    String letterStr = String.valueOf(biomeLetter);
                    biomeMap[x][y] = new Biome(letterStr);
                    System.out.print(biomeMap[x][y]);
                }
            }
            System.out.println("Successfully loaded map: " + fullPath);

        } catch (IOException e) {
            System.out.println("Current Java Directory: " + new File(".").getAbsolutePath());

            System.out.println("CRITICAL: Failed to load map at " + fullPath);
            System.out.println("Reason: " + e.getMessage());
            // Trigger your backup random map generator here if the file fails
        }
    }

    public void resetGrids() {
        // wipe old grids clean
        for (int x = 0; x < numBuckets; x++) {
            for (int y = 0; y < numBuckets; y++) {
                birdGrid[x][y].clear();
                energyGrid[x][y].clear();
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

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> getNearbyOrgs(int[] otherLocation, double searchRadius, ArrayList<T> localList, ArrayList<T>[][] orgGrid){ //GUYS ths T color is SOOOOOOO prettyyyy
        localList.clear();
        int xPos = otherLocation[0];
        int yPos = otherLocation[1];

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
        return (ArrayList<T>) localList;
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
        //   energyList.add(en);
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
              //      energyList.add(en);
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
               //     energyList.add(en);
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
      //  energyList.add(en); //use usableENergyList bc dead ones dont matter
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

    public Bird addBird(int age, Genes g, String newID, ArrayList<Bird> sepList, int[] newPos) {
        Bird b = new Bird(age, g, newID, newPos, biomeMap);
        sepList.add(b);
        return b;
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
        int deathRate = 0; // for 10 end in list

        // ArrayList<Bird> printDeadList = new ArrayList<Bird> ((numDeadShow< deathList.size()) ? deathList.subList(deathList.size()-numDeadShow-1, deathList.size()-1) : deathList);

        while (recentDeathList.size()>numDeadShow) { //remove the easrliest ones until the required dead amount shows, so it slides through and has last 10 (or wtv numDeadShow is)
            recentDeathList.remove(0);
            deathRate++; //addd for every removed from list
        }

        for (Bird b : recentDeathList) {
            if (!b.alive) {
                System.out.println(b);
                //ddNum++;
                ddAgeSum+=b.getDie_age();
            }
        }
        ddNum = deathCount;

        if (ddNum >= 50000) {
            flushDeathMemoToFile(deathMemorandum);
            deathMemorandum.setLength(0);
        }

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
                        //System.out.println("not in biome?");
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
                  //System.out.println("not in biome?");
            }
        }
        int alAvNum = (alNum==0) ? 0 : alAgeSum/alNum;




        System.out.println("-------------------------------------------------------------" +
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum +
                "\t\t\t Average Alive Age: " + alAvNum + " - Average (recent) Death Age: " + ddAgeSum/numDeadShow + " Partho Count: " + parthCount +
                "\n Energy in\t Plains: "+EinP+" - Forest: "+EinF+" - Swamp: "+EinS+" - Mountain: "+EinM + "\t Birds in\t Plains: "+BinP+" - Forest: "+BinF+" - Swamp: "+BinS+" - Mountain: "+BinM);


    }

    public void flushDeathMemoToFile(StringBuilder deathMemorandum) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historical_deaths.txt", true))) {
            writer.write(deathMemorandum.toString());
            System.out.println("--- Flushed 50,000 dead bird records to historical_deaths.txt ---");
        } catch (IOException e) {
            System.err.println("Failed to write death logs to file: " + e.getMessage());
        }
    }

    public void pause(double sec) {
        try { Thread.sleep((int) (1000*sec)); } catch (InterruptedException e) {System.out.println("error in processing..."); };
    }


    public void endResults(int cluster, int minAge, int cycleNum){
        System.out.println("\n\tLast Cycle\n");
        this.oneTick(cluster, minAge, cycleNum, 5, 10);


        System.out.println("\n\n=== Dead Birds ===");
        int ddNum = deathCount;
        int ddAgeSum = 0;
//        for (Bird b : deathList) {
//            System.out.println(b);
//            // ddNum++;
//            ddAgeSum +=b.getDie_age();
//
//        }
        System.out.print(deathMemorandum.toString());

        System.out.println();
        int alNum = 0;
        int alAgeSum = 0;
        int alChild =0;
        int[] alPos = new int[2];
        double alSpeed=0; double alSize=0; double alMaxHP = 0; double alRes=0; double alFert=0; double alAbsD=0; double alReprD=0; double alVisD=0; double alSpPref=0; double alEnOrBias=0; double alCrAff=0; double alAgOrBias=0;

        int BinP=0;
        int BinF=0;
        int BinS=0;
        int BinM=0;

        for (Bird b : aliveList) {
            if (b.alive) {
                System.out.println(b);
                if (b.getAge()>=0) {
                    alNum++;
                    alAgeSum += b.getAge();
                    alPos[0] += b.getPosition()[0];
                    alPos[1] += b.getPosition()[1];
                    alSpeed += b.getGenes().getSpeed();
                    alSize += b.getGenes().size;
                    alMaxHP += b.getMaxHP();
                    alRes += b.getGenes().getResistance();
                    alFert += 1 - b.getGenes().getResistance();
                    alAbsD += b.getAbsorbRad();
                    alReprD += b.getReproRad();
                    alVisD += b.getGenes().getVisionDist();
                    alSpPref += b.getGenes().speedPref;
                    alEnOrBias += b.getGenes().getEnergyOrientBias();
                    alCrAff += b.getGenes().crowdAff;
                    alAgOrBias += b.getGenes().aggroOrientBias;
                }
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
                   // System.out.println("not in biome?");
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
                   // System.out.println("not in biome?");
            }
        }
        eNum2 = usableEnergyList.size();


        int alAgeAv = (alNum!=0) ? alAgeSum/alNum : 0;
        double[] avgStats = new double[] {alSize, alSpeed, alMaxHP, alRes, alAbsD, alReprD, alVisD, alSpPref, alEnOrBias, alCrAff, alAgOrBias};
        for (int avgP=0; avgP<avgStats.length; avgP++) {
            avgStats[avgP] = avgStats[avgP]/alNum;
        }
        alSize=avgStats[0]; alSpeed=avgStats[1]; alMaxHP=avgStats[2]; alRes=avgStats[3]; alAbsD=avgStats[4]; alReprD=avgStats[5]; alVisD=avgStats[6]; alSpPref=avgStats[7]; alEnOrBias=avgStats[8]; alCrAff=avgStats[9]; alAgOrBias=avgStats[10];
        alPos[0]/=alNum; alPos[1]/=alNum;

        int ddAgeAv = (ddNum!=0) ? ddAgeSum/ddNum : 0;

        Genes avgGenes = new Genes((int) alSize, alSpeed, (int) alMaxHP, alRes, alAbsD, alReprD, alVisD, alSpPref, alEnOrBias, alCrAff, alAgOrBias, new int[] {0, 0, 0, 0});
       // Bird avgBird = new Bird(avgGenes, "Average model", alPos, biomeMap);
        Bird avgBird = addBird(-10000, avgGenes, "Average model", aliveList, alPos);

        System.out.println("-------------------------------------------------------------\n" + avgBird + "\n-------------------------------------------------------------"+
                "\n Alive: " + alNum + " - Deaths: " + ddNum + " - Energy left: " + eNum + " " + eNum2+
                "\n Energy in\t Plains: "+EinP+" - Forest: "+EinF+" - Swamp: "+EinS+" - Mountain: "+EinM + "\t Birds in\t Plains: "+BinP+" - Forest: "+BinF+" - Swamp: "+BinS+" - Mountain: "+BinM+
                "\n Average Alive Age: " + alAgeAv + " - Average Death Age: " + ddAgeAv);
        System.out.println("Alive: " + alNum + " - Dead: " + ddNum + " - Partho Count: " + parthCount + " - Birth Count: " + birdList.size());

       // System.out.println("Biomes set to map: \n\n"+setBiomeMap(bType)+ "\n");

    };

    public void printEnergyLayout() {
        System.out.println("\n");
        for (int[] ints : energyLayout) {
            System.out.println();
            for (int j = 0; j < energyLayout[0].length; j++) {
                String energyPer = "";
                if (ints[j] == 0) {
                    energyPer = "_";
                } else {
                    energyPer = "" + ints[j];
                }
                System.out.print(" " + energyPer);
            }
        }

        // System.out.println("\n\n " + (enCount-usableEnergyList.size()) + "   " + enCount);
    }

    public void wipeOut() {
        for (Bird b : birdList) {
            if (b.alive) {
                b.kill();
                System.out.println("Bird #"+ b.getID() + " died.");
            }
        }
      //  deathList.clear();
        deathMemorandum.setLength(0); //makes it empty
    }


    public void saveSimulation(String filename, int currCycle) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));

            out.println("Cycle: " + currCycle);
            out.println("Alive: " + aliveList.size());
            out.println("Dead: " + deathCount);

            out.println("\n=== Alive Birds ===");

            System.out.println();
            int alNum = 0;
            int alAgeSum = 0;
            for (Bird b : aliveList) {
                System.out.println(b);
                //System.out.println(", " + b.getGenes());
                if (b.getAge()>=0) {
                    alNum++;
                    alAgeSum += b.getAge();
                }

            }


            out.println("\n\n=== Dead Birds ===");

            int ddNum = deathCount;
            int ddAgeSum = 0;

            System.out.println(deathMemorandum.toString());
//            for (Bird b : deathList) {
//                out.println(b);
//                ddNum++;
//                ddAgeSum +=b.getDie_age();
//
//            }


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

    public Biome[][] getBiomeMap () {
        return biomeMap;
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
* - We need to find a way for 1-strings to stops being prevalent. maybe a bigger radius, or smt to make each one reproduce more --Fixed but returning???
*  - LETS make a graph option where u can graph the lineage of a ancestor --to do in 1.2
* */

