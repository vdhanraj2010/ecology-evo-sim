package populationPlay;

public class main {
    public static void main(String[] args) {
        World myWorld = new World();
        int cycles = 50;

        myWorld.startUp();
        myWorld.spawnEnergy(1000);
        myWorld.spawnBirds(20);
        for (int i=0; i<cycles; i++) {
            System.out.println("\n\tCycle " + i + " >>> \n");
            myWorld.spawnEnergy(100);
            myWorld.popSave(5, 10);
            myWorld.oneTick();
            myWorld.pause(0.2);
            myWorld.genResults();

        }
        myWorld.wipeout();
    }
}

/* ok so for the next update, i wanna make the energy spawned linked to the amount before, so resource scarcity becomes a thing.
* lI want to add indicators in the printing for famines, mass deaths, cause of death, linked genes (like resistance+fertility=1), and more
* */