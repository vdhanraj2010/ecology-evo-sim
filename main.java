package populationPlay;
import java.util.Scanner;
import populationPlay.visual.MapPanel;
import populationPlay.visual.SimulationFrame;

public class main {
    public static void main(String[] args) {
        int worldSize = 100;
        Scanner scnr = new Scanner(System.in);
        World myWorld = new World(worldSize);

       // myWorld.startUp("world_500-Hyrule");
       myWorld.startUp("grid");
        myWorld.spawnEnergy(100);
        //myWorld.spawnEnergyMap(1, 100000);
        //myWorld.spawnEnergyMap(2, 5000);

        MapPanel panel = new MapPanel(myWorld, worldSize);
        SimulationFrame frame = new SimulationFrame(panel);

        String reroll = "";
        while (!reroll.equals("c")) {
            myWorld.wipeOut();
            myWorld.spawnBirds(100, 0);
            System.out.println("Here is the new starting population: \n");
            myWorld.genResults(50);
            System.out.print("\nContinue or reroll? (type 'c' or 'r' respectively)? \n\t>>> ");
            reroll = scnr.next();
        }

        System.out.print("How many cycles?\n\t>>> ");
        int past = 0;

        System.out.println("\nEnter commands:");
        System.out.println("  <number>        -> run cycles");
        System.out.println("  save <filename>   -> save simulation as <filename>");
        System.out.println("  stop OR 0            -> quit\n");

        while (true) {
            System.out.print("\t>>> ");
            String input = scnr.nextLine().trim();

            // EXIT
            if (input.equals("stop") || input.equals("0")) {
                break;
            }

            // SAVE
            if (input.startsWith("save ")) {
                String fileName = input.substring(5).trim();
                myWorld.saveSimulation(fileName, past);
                System.out.println("Saved to: " + fileName);
                continue;
            }

            // OPTIONS
            if (input.equals("--o")) {
                System.out.println("\nEnter commands:");
                System.out.println("  <number>        -> run cycles");
                System.out.println("  save <filename>   -> save simulation as <filename>");
                System.out.println("  stop OR 0            -> quit\n");
                continue;
            }

            // RESET
            if (input.equals("--r")) {
                System.out.println("\nReset command not fully set up yet:");
                continue;
            }


            //Run N cycles
            try {
                int cycles = Integer.parseInt(input);
                for (int i = 0; i < cycles - 1; i++) {
                    if (i%10==0) {System.out.println("\n\tCycle " + ((int) i + past) + " >>> \n");}
                    //myWorld.spawnEnergy((int)(200.0 / myWorld.aliveList.size())); //check Notes for stats - stopped using this and made it self-contained
                    // myWorld.spawnEnergy((int)(500)); //test
                    //Ideas: spawn = random(0, maxEnergyPool) OR spawn = (int)(120 / Math.sqrt(aliveCount));
                    myWorld.energyCycle(0.2, 5, 10, 10);
                    myWorld.popSave(0, 5, 1000); // this now also dispenses 1000/popSize energy // usually 5, 5, 1000
                    myWorld.oneTick(5, 10, past + i, 10, 10);
                    myWorld.pause(0.0);
                    panel.repaint();
                    if (i%10==0) {myWorld.genResults(10);}

                    if (myWorld.aliveList.isEmpty()) {
                        System.out.println("Extinction!!!");
                        break;
                    }

                }
               // myWorld.spawnEnergy((int)(1000)); //test
                myWorld.energyCycle(0.33, 5, 10, 10);
                myWorld.endResults(5, 5, cycles);
                panel.repaint();
                //myWorld.printEnergyLayout();
                past += cycles;


                //System.out.println("\n\nHow many more cycles? (type '0' to end, type '--o for more commands) \n\t>>> ");
                continue;

            } catch (NumberFormatException e) {
                System.out.println("Invalid command. Use a number or 'save filename'.");
            }


            System.out.println("Simulation ended :)");
        }
        scnr.close();

    }

}



/* ok so for the next update, i wanna make the energy spawned linked to the amount before, so resource scarcity becomes a thing. ---DONE!
* lI want to add indicators in the printing for famines, mass deaths, cause of death, linked genes (like resistance+fertility=1), and more
* */



/* Notes:
 * SpawnEnergy Stats:
 * - 1000 stabilizes at 10-20
 * - 2000
 * - 5000 stabilizes at 100-150
 *
 * Regular spore: myWorld.energyCycle(0.50, 1, 5, 5);
 * */

/* before i commit the 1.1 version, there is a few stuff i wanna do:
1) make food not 2000/k spawn but rather existing ones duplicate (maybe spore). but i want ot do this in a way that more birds means less eergy but also have the support system that backs up if pop gets low (less birds means more energy). this would also allow for famine to occur in an area but not all areas
- yes, make spawn in area around like spore, use logistic reproduction on density --DONE!!!
2) i want to find a way to shorten dynasty names in a easy display fashion, so that when a population reaches a certain point where an (undecided) amount of birds share a long string of ancestors, it is replaced or indicated as to not have to search through 20-character strings for differences.
- dynasty name (with declaration) OR #13-(12)-2 for exaple --DONE!! (with colors)
3) possibly alter genes in a way that makes it more realistic yet not complex yet. this is not necessary but would be a good precursor to when i add environmental effects
- do this by adding more unique mutations --working on it for 1.2!!
4) make children spawn near parents (tell me if this is good or not)
- yes, use location --DONE!! (wasnt that hard)
5) make a mode after recieving results to ask for a graph of deaths, alive pop, or descendants of a lineage
 -  not done because it seems like a graphical thing, later*
 */

/* what should i do next? make the dynasty thing? alter genes to make it more diverse and realistic? make a mode for displaying a graph using dots and a line graph array? any otehr things to make this more evolution lik?
i recently saw a video of a similar project. this guy used neurons instead and made safe zones ahere cells had to get to. over a thousand generations the cells learned to move a certain direction through mutation and selection.
while my roject is more realistic and self sustaining, i want to be able to make it more evolution based like that */

/* Goals for version 1.2:
* 1) biomes should be complete, with multiple rudimentary effects and a couple special ones --IN PROGRESS
* 2) birds should automatically form groupings, yet still move in directions, but also prefer a biome through strong  specific survival traits, using a new vision Gene --DONE!!!
* 3) birds should be able to make decisions on where to go, based on direction and a factor of nearby food and nearby birds of same species and nearby birds of otehr species and the random weight --DONE!!!
* 4) use the above to make a simple neural system resulting in two nerons: direction and speed (so a vecotr control), using multiple genetically coded receptors, including: --speed is done, distance not yet
* > crowd preference   >direction of food in vision (weight for size and distance)   >aggression/aversion to other species    >stress gene which says when to use extra energy resources based on hp%
* ---the movement vecotr is categorized into: sensory genes, preference genes, and decision genes (stress +memory) --stress IN PROGRESS
* 5) make predator species, with red halo, triggered by a gene marking. omnivores maybe?
* 6) make species categorized in prep for predation and categorization / nomenclature
* 7) make an "average bird" for all, then for species -- for all, DONE!!, for species, not yet*/