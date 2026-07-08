package populationPlay;
import populationPlay.Biome;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapScanner {



    public static void main (String[] args) {
        int size = 500;
        char[][] mapGrid = new char[size][size];

        String rawMapName = "mediterranean";
        String rawMapDir = "src/populationPlay/mapPresets/" + rawMapName+".png";
        System.out.println(rawMapDir);
        File rawMapFile = new File(rawMapDir);
        BufferedImage rawMapImage=null;
        try {
            rawMapImage = ImageIO.read(rawMapFile);
        } catch (IOException e) {
            System.err.println("Failed to read map: " + e.getMessage());
        }

        assert rawMapImage != null;
        int imgWidth = rawMapImage.getWidth();
        int imgHeight = rawMapImage.getHeight();
        double widthScale = (float)imgWidth/size;
        double heightScale = (float)imgHeight/size;

        Color[] biomeColorList =  new Color[] {Biome.oceanColor, Biome.plainsColor, Biome.forestColor, Biome.swampColor,Biome.desertColor, Biome.coastColor, Biome.mountainColor, Biome.jungleColor, Biome.tundraColor, Biome.volcanicColor};
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
               int oceanCount=0; int plainsCount=0; int forestCount=0; int swampCount=0; int desertCount=0; int coastCount=0; int mountainCount=0; int jungleCount=0; int tundraCount=0; int volcanicCount=0;
                int[] countList = new int[10];
                boolean isWater = false;


                for (int k=0; k<heightScale; k++) {
                    for (int l = 0; l < widthScale; l++) {
                        int rawRGB = rawMapImage.getRGB((int) (j * widthScale + l), (int) (i * heightScale + k));
                        Color pxlColor = new Color(rawRGB);
                        int smallestDistSqr = Integer.MAX_VALUE;;
                        int closestBiomeIndex = 0;

                        for (int m = 0; m < biomeColorList.length; m++) {
                            Color biomeColor = biomeColorList[m];
                            int redDiff = biomeColor.getRed() - pxlColor.getRed();
                            int blueDiff = biomeColor.getBlue() - pxlColor.getBlue();
                            int greenDiff = biomeColor.getGreen() - pxlColor.getGreen();
                            int colorDiffSqr = redDiff * redDiff + blueDiff * blueDiff + greenDiff * greenDiff;
                            if (colorDiffSqr < smallestDistSqr) { //if dist is smallest so far
                                smallestDistSqr = colorDiffSqr;
                                closestBiomeIndex = m;
                            }
                        }
                        countList[closestBiomeIndex]++;
                        if (closestBiomeIndex==0) {
                            isWater=true;
                        }
                    }
                }

                char tileLetter;
                if (isWater) {
                    tileLetter='~';
                } else {
                    int maxVotes = -1;
                    int winningBiomeIndex = 1;

                    for (int b = 1; b < countList.length; b++) {
                        if (countList[b] > maxVotes) {
                            maxVotes = countList[b];
                            winningBiomeIndex = b;
                        }
                    }

                    tileLetter = switch (winningBiomeIndex) {
                            case 0 -> '~';
                            case 1 -> 'P';
                            case 2 -> 'F';
                            case 3 -> 'S';
                            case 4 -> 'D';
                            case 5 -> 'C';
                            case 6 -> 'M';
                            case 7 -> 'J';
                            case 8 -> 'T';
                            case 9 -> 'V';
                            default -> 'V';
                    };
                }
                mapGrid[i][j] = tileLetter;



            }
        }
        String outputFilename = rawMapDir+"_"+size+".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            for (int i = 0; i < size; i++) {
                writer.write(new String(mapGrid[i]));
                writer.newLine();
            }

            System.out.println("Success! Map "+ rawMapName + " was created!");

        } catch (IOException e) {
            System.out.println("Could not write map to the file: " + e.getMessage());
        }
    }
}
