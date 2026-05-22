package populationPlay;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        World myWorld = new World();

        myWorld.startUp();
        myWorld.spawnEnergy(1000);

        String reroll = "";
        while (!reroll.equals("c")) {
            myWorld.wipeOut();
            myWorld.spawnBirds(10, 0);
            System.out.println("Here is the new starting population: \n");
            myWorld.genResults(50);
            System.out.print("\nContinue or reroll? (type 'c' or 'r' respectively)? \n\t>>> ");
            reroll = scnr.next();
        }

        System.out.print("How many cycles? \n\t>>> ");
        int cycles = scnr.nextInt();
        int past = 0;
        while (cycles>0) {
            for (int i = 0; i < cycles-1; i++) {
                System.out.println("\n\tCycle " + ((int)i+past) + " >>> \n");
                myWorld.spawnEnergy((int)(200.0 / myWorld.aliveList.size())); //check Notes for stats
                //Ideas: spawn = random(0, maxEnergyPool) OR spawn = (int)(120 / Math.sqrt(aliveCount));
                myWorld.popSave(5, 5, 1000); // this now also dispenses 1000/popSize energy
                myWorld.oneTick(10, 10, past+i);
                myWorld.pause(0.00);
                myWorld.genResults(50);

            }
            myWorld.endResults(5, 5, cycles);
            past += cycles;
            System.out.println("\n\nHow many more cycles? (type '0' to end) \n\t>>> ");
            cycles = scnr.nextInt();
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
- yes, make spawn in area around like spore, use logistic reproduction on density
2) i want to find a way to shorten dynasty names in a easy display fashion, so that when a population reaches a certain point where an (undecided) amount of birds share a long string of ancestors, it is replaced or indicated as to not have to search through 20-character strings for differences.
- dynasty name (with declaration) OR #13-(12)-2 for exaple
3) possibly alter genes in a way that makes it more realistic yet not complex yet. this is not necessary but would be a good precursor to when i add environmental effects
- do this by adding more unique mutations
4) make children spawn near parents (tell me if this is good or not)
- yes, use location
5) make a mode after recieving results to ask for a graph of deaths, alive pop, or descendants of a lineage*/

