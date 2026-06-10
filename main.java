package populationPlay;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        World myWorld = new World();

        myWorld.startUp();
        myWorld.spawnEnergy(1000);
        //myWorld.spawnEnergyMap(1, 1000);

        String reroll = "";
        while (!reroll.equals("c")) {
            myWorld.wipeOut();
            myWorld.spawnBirds(20, 0);
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
            String input = scnr.next().trim();

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
                    System.out.println("\n\tCycle " + ((int) i + past) + " >>> \n");
                    //myWorld.spawnEnergy((int)(200.0 / myWorld.aliveList.size())); //check Notes for stats - stopped using this and made it self-contained
                    myWorld.spawnEnergy((int)(0)); //test
                    //Ideas: spawn = random(0, maxEnergyPool) OR spawn = (int)(120 / Math.sqrt(aliveCount));
                    myWorld.energyCycle(0.50, 1, 5, 5);
                    myWorld.popSave(10, 5, 1000); // this now also dispenses 1000/popSize energy // usually 5, 5, 1000
                    myWorld.oneTick(10, 10, past + i);
                    myWorld.pause(0.1);
                    myWorld.genResults(50);

                }
                myWorld.spawnEnergy((int)(0)); //test
                myWorld.energyCycle(0.50, 2, 5, 5);
                myWorld.popSave(10, 5, 1000);
                myWorld.endResults(5, 5, cycles);
                myWorld.printEnergyLayout();
                past += cycles;


                //System.out.println("\n\nHow many more cycles? (type '0' to end, type '--o for more commands) \n\t>>> ");
                continue;

            } catch (NumberFormatException e) {
                System.out.println("Invalid command. Use a number or 'save filename'.");
            }

            scnr.close();
            System.out.println("Simulation ended :)");
        }

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
 * */

/* before i commit the 1.1 version, there is a few stuff i wanna do:
1) make food not 2000/k spawn but rather existing ones duplicate (maybe spore). but i want ot do this in a way that more birds means less eergy but also have the support system that backs up if pop gets low (less birds means more energy). this would also allow for famine to occur in an area but not all areas
- yes, make spawn in area around like spore, use logistic reproduction on density --DONE!!!
2) i want to find a way to shorten dynasty names in a easy display fashion, so that when a population reaches a certain point where an (undecided) amount of birds share a long string of ancestors, it is replaced or indicated as to not have to search through 20-character strings for differences.
- dynasty name (with declaration) OR #13-(12)-2 for exaple
3) possibly alter genes in a way that makes it more realistic yet not complex yet. this is not necessary but would be a good precursor to when i add environmental effects
- do this by adding more unique mutations
4) make children spawn near parents (tell me if this is good or not)
- yes, use location
5) make a mode after recieving results to ask for a graph of deaths, alive pop, or descendants of a lineage*/

/* what should i do next? make the dynasty thing? alter genes to make it more diverse and realistic? make a mode for displaying a graph using dots and a line graph array? any otehr things to make this more evolution lik?
i recently saw a video of a similar project. this guy used neurons instead and made safe zones ahere cells had to get to. over a thousand generations the cells learned to move a certain direction through mutation and selection.
while my roject is more realistic and self sustaining, i want to be able to make it more evolution based like that */