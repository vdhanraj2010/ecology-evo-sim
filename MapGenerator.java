//package populationPlay;
//
//import java.awt.Color;
//import java.awt.image.BufferedImage;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import javax.imageio.ImageIO;
//
//public class MapGenerator {
//
//    // Define our exact color palettes for matching
//    private static final Color COLOR_OCEAN    = new Color(0, 0, 255);       // Pure Blue
//    private static final Color COLOR_TUNDRA   = new Color(255, 255, 255);   // White
//    private static final Color COLOR_DESERT   = new Color(255, 255, 0);     // Yellow
//    private static final Color COLOR_JUNGLE   = new Color(0, 100, 0);       // Dark Green
//    private static final Color COLOR_FOREST   = new Color(0, 255, 0);       // Bright Green
//    private static final Color COLOR_MOUNTAIN = new Color(128, 128, 128);   // Gray
//    private static final Color COLOR_VOLCANIC = new Color(255, 0, 0);       // Pure Red
//    private static final Color COLOR_PLAINS   = new Color(210, 180, 140);   // Tan/Beige
//
//    public static void main(String[] args) {
//        String inputImagePath = "world_reference.png";
//        String outputTxtPath = "world_map_500x500.txt";
//        int size = 500;
//
//        char[][] grid = new char[size][size];
//
//        try {
//            File file = new File(inputImagePath);
//            if (!file.exists()) {
//                System.out.println("Error: Place your 500x500 image named '" + inputImagePath + "' in the project root!");
//                return;
//            }
//
//            BufferedImage image = ImageIO.read(file);
//
//            for (int y = 0; y < size; y++) {
//                for (int x = 0; x < size; x++) {
//                    if (x >= image.getWidth() || y >= image.getHeight()) {
//                        grid[y][x] = '~';
//                        continue;
//                    }
//
//                    Color pixelColor = new Color(image.getRGB(x, y));
//                    grid[y][x] = getClosestBiome(pixelColor);
//                }
//            }
//
//            // Coastal Protection Pass
//            char[][] finalGrid = new char[size][size];
//            for (int y = 0; y < size; y++) {
//                System.arraycopy(grid[y], 0, finalGrid[y], 0, size);
//            }
//
//            for (int y = 1; y < size - 1; y++) {
//                for (int x = 1; x < size - 1; x++) {
//                    if (grid[y][x] != '~') {
//                        if (grid[y-1][x] == '~' || grid[y+1][x] == '~' ||
//                                grid[y][x-1] == '~' || grid[y][x+1] == '~') {
//                            finalGrid[y][x] = 'C';
//                        }
//                    }
//                }
//            }
//
//            // Export text file
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputTxtPath))) {
//                for (int y = 0; y < size; y++) {
//                    bw.write(new String(finalGrid[y]));
//                    bw.newLine();
//                }
//                System.out.println("Success! Compiled precise map into: " + outputTxtPath);
//            }
//
//        } catch (IOException e) {
//            System.err.println("Error processing image: " + e.getMessage());
//        }
//    }
//
//    // Calculates 3D color distance to solve the "colors in the middle" problem
//    private static char getClosestBiome(Color pixel) {
//        char bestBiome = 'P';
//        double minDistance = Double.MAX_VALUE;
//
//        // Pair every biome token with its corresponding reference color
//        Object[][] biomeColors = {
//                {'~', COLOR_OCEAN},
//                {'T', COLOR_TUNDRA},
//                {'D', COLOR_DESERT},
//                {'J', COLOR_JUNGLE},
//                {'F', COLOR_FOREST},
//                {'M', COLOR_MOUNTAIN},
//                {'V', COLOR_VOLCANIC},
//                {'P', COLOR_PLAINS}
//        };
//
//        for (Object[] pair : biomeColors) {
//            char token = (char) pair[0];
//            Color targetColor = (Color) pair[1];
//
//            // 3D Euclidean distance formula for RGB space
//            double distance = Math.sqrt(
//                    Math.pow(pixel.getRed() - targetColor.getRed(), 2) +
//                            Math.pow(pixel.getGreen() - targetColor.getGreen(), 2) +
//                            Math.pow(pixel.getBlue() - targetColor.getBlue(), 2)
//            );
//
//            if (distance < minDistance) {
//                minDistance = distance;
//                bestBiome = token;
//            }
//        }
//
//        return bestBiome;
//    }
//}
package populationPlay;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class MapGenerator {
    public static void main(String[] args) {
        int size = 500;
        char[][] grid = new char[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                // Initialize the entire matrix as Deep Ocean
                grid[r][c] = '~';

                // High-frequency trigonometric waves to roughen shorelines naturally
                double shoreFuzz = 4.5 * Math.sin(c * 0.12) + 3.0 * Math.cos(r * 0.15)
                        + 1.5 * Math.sin(r * 0.4);
                double fuzzyRow = r + shoreFuzz;
                double fuzzyCol = c + shoreFuzz;

                // --- 1. NORTH AFRICAN COASTLINE MASKS (Bottom Sector) ---
                boolean isNorthAfrica = (fuzzyRow > 360 && fuzzyRow < 500 && fuzzyCol > 10 && fuzzyCol < 490);
                // Sculpt out the Gulf of Sirte / Libyan Sea indentation
                if (isNorthAfrica && (fuzzyCol > 230 && fuzzyCol < 320 && fuzzyRow < 390)) {
                    isNorthAfrica = false;
                }

                // --- 2. THE THREE MAJOR EUROPEAN PENINSULAS ---
                // A. Iberia (Spain/Portugal - Left Sector)
                boolean isIberia = (fuzzyRow > 140 && fuzzyRow < 310 && fuzzyCol > 15 && fuzzyCol < 135);

                // B. Italy (The Boot - Central Sector)
                // Modeled diagonally downward from northwest to southeast
                boolean isItaly = (fuzzyCol > 200 && fuzzyCol < 275 && fuzzyRow > 130 && fuzzyRow < 320);
                // Sharp cutoffs to sculpt the narrow boot profile and ankle curves
                if (isItaly && (fuzzyRow > 210 && fuzzyCol > 245)) isItaly = false;
                if (isItaly && (fuzzyRow > 260 && fuzzyCol < 235)) isItaly = false;

                // C. The Balkans & Greece (Right-Center Sector)
                boolean isBalkans = (fuzzyRow > 120 && fuzzyRow < 340 && fuzzyCol >= 275 && fuzzyCol < 390);
                // Sculpt out the Aegean Sea fractures/pelagic pockets
                if (isBalkans && (fuzzyRow > 250 && fuzzyCol > 340 && (r + c) % 5 == 0)) {
                    isBalkans = false;
                }

                // D. Anatolia / Asia Minor (Far Right Sector)
                boolean isAnatolia = (fuzzyRow > 180 && fuzzyRow < 290 && fuzzyCol >= 390 && fuzzyCol < 495);

                // E. Southern France & Central Mainland Europe (Top Border Connector)
                boolean isMainlandEurope = (fuzzyRow > 40 && fuzzyRow <= 160 && fuzzyCol > 60 && fuzzyCol < 440);

                // --- 3. THE EVOLUTIONARY LAB ISLANDS ---
                boolean isBalearic = (fuzzyCol > 85 && fuzzyCol < 105 && fuzzyRow > 230 && fuzzyRow < 250 && (r+c)%3==0);
                boolean isCorsica  = (fuzzyCol > 185 && fuzzyCol < 205 && fuzzyRow > 180 && fuzzyRow < 220);
                boolean isSardinia = (fuzzyCol > 180 && fuzzyCol < 208 && fuzzyRow >= 220 && fuzzyRow < 275);
                boolean isSicily   = (fuzzyCol > 225 && fuzzyCol < 260 && fuzzyRow > 310 && fuzzyRow < 345);
                boolean isCrete    = (fuzzyCol > 330 && fuzzyCol < 380 && fuzzyRow > 335 && fuzzyRow < 355);
                boolean isCyprus   = (fuzzyCol > 430 && fuzzyCol < 475 && fuzzyRow > 290 && fuzzyRow < 315);

                boolean isLand = isNorthAfrica || isIberia || isItaly || isBalkans ||
                        isAnatolia || isMainlandEurope || isBalearic ||
                        isCorsica || isSardinia || isSicily || isCrete || isCyprus;

                if (!isLand) {
                    continue;
                }

                // --- 4. TECTONIC BIOME ASSIGNMENTS & ACCURATE GEOGRAPHY ---

                // A. THE RING OF FIRE VOLCANIC HOTSPOTS ('V')
                // Southern Italy / Sicily Tectonic Rift Zone (Vesuvius, Stromboli, Etna)
                boolean isItalianVolcano = (isItaly || isSicily) && (fuzzyCol > 235 && fuzzyCol < 250 && fuzzyRow > 250 && fuzzyRow < 320 && r % 11 == 0);
                // Aegean Sea Arc (Santorini region)
                boolean isAegeanVolcano = (fuzzyCol > 340 && fuzzyCol < 365 && fuzzyRow > 290 && fuzzyRow < 320 && (r+c) % 13 == 0);

                if (isItalianVolcano || isAegeanVolcano) {
                    grid[r][c] = 'V';
                    continue;
                }

                // B. THE MOUNTAIN SHIELDS AND HIGH ALTITUDES ('M' & 'T')
                // The Great European Ridges (Alps in the North, Pyrenees in Iberia, Apennines down Italy)
                boolean isAlps = isMainlandEurope && (fuzzyRow > 90 && fuzzyRow < 135 && fuzzyCol > 160 && fuzzyCol < 280);
                boolean isPyrenees = (isMainlandEurope || isIberia) && (Math.abs(fuzzyRow - 145) < 12 && fuzzyCol > 50 && fuzzyCol < 130);
                boolean isApennines = isItaly && (fuzzyCol > 215 && fuzzyCol < 240 && fuzzyRow > 140 && fuzzyRow < 260);
                boolean isPindusAndTaurus = (isBalkans || isAnatolia) && (fuzzyRow > 180 && fuzzyRow < 230 && fuzzyCol > 300);

                if (isAlps || isPyrenees || isApennines || isPindusAndTaurus) {
                    // Highest Alpine peaks contain freezing Tundra caps
                    if ((isAlps && fuzzyRow < 110) || (isPindusAndTaurus && fuzzyCol > 440 && r % 3 == 0)) {
                        grid[r][c] = 'T'; // Alpine/Anatolian Tundra Caps
                    } else {
                        grid[r][c] = 'M'; // Rugged Mountain Barriers
                    }
                    continue;
                }

                // C. THE BIOME CLIMATE BRACKETS
                if (isNorthAfrica) {
                    // Deep continental interior drops off into the Sahara
                    if (fuzzyRow > 410) {
                        grid[r][c] = 'D'; // Sahara Desert Block
                    } else {
                        grid[r][c] = 'P'; // Semi-Arid Coastal Plains
                    }
                }
                else if (isMainlandEurope || isCorsica || (isIberia && fuzzyRow < 200)) {
                    // Dense vegetative wet woodlands of Southern France and Northern Spain
                    if (fuzzyCol > 140 && fuzzyCol < 190 && r % 4 == 0) {
                        grid[r][c] = 'J'; // Wetlands / Rhone Delta dense Jungle analogues
                    } else {
                        grid[r][c] = 'F'; // Forest Belt
                    }
                }
                else {
                    // Mediterranean Basin shrublands, scrublands, and open valleys
                    // Covers Italy, Greece, southern Iberia, and Mediterranean islands
                    if ((isBalkans || isAnatolia) && r % 6 == 0) {
                        grid[r][c] = 'F'; // Forested valley pockets
                    } else {
                        grid[r][c] = 'P'; // Maquis Shrubland / Grassy Plains analogues
                    }
                }
            }
        }

        // --- 5. AUTOMATED DYNAMIC COASTAL BUFFER PASS ---
        char[][] finalGrid = new char[size][size];
        for (int r = 0; r < size; r++) {
            System.arraycopy(grid[r], 0, finalGrid[r], 0, size);
        }

        for (int r = 1; r < size - 1; r++) {
            for (int c = 1; c < size - 1; c++) {
                if (grid[r][c] != '~') {
                    if (grid[r-1][c] == '~' || grid[r+1][c] == '~' ||
                            grid[r][c-1] == '~' || grid[r][c+1] == '~') {
                        finalGrid[r][c] = 'C'; // Maps realistic beach/coast wrappers
                    }
                }
            }
        }

        // --- 6. EXPORT THE STABLE GEOGRAPHIC DATA MATRIX ---
        String filename = "mediterranean_map_500x500.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int r = 0; r < size; r++) {
                bw.write(new String(finalGrid[r]));
                bw.newLine();
            }
            System.out.println("Success! Generated precision-accurate Mediterranean matrix file: " + filename);
        } catch (IOException e) {
            System.err.println("File compilation error: " + e.getMessage());
        }
    }
    }

//
//        private static final int[] p = new int[1000];
//
//        public static void main(String[] args) {
//            Random rand = new Random();
//            int[] permutation = new int[SIZE];
//            for (int i = 0; i < 256; i++) permutation[i] = i;
//            for (int i = 255; i > 0; i--) {
//                int index = rand.nextInt(i + 1);
//                int a = permutation[index];
//                permutation[index] = permutation[i];
//                permutation[i] = a;
//            }
//            for (int i = 0; i < 256; i++) {
//                p[i] = permutation[i];
//                p[256 + i] = permutation[i];
//            }
//
//            char[][] biomeGrid = new char[SIZE][SIZE];
//            System.out.println("Generating geological 500x500 toroidal map...");
//
//            // Low frequency creates massive global continental blocks
//            double baseScale = 0.95;
//
//            for (int x = 0; x < SIZE; x++) {
//                for (int y = 0; y < SIZE; y++) {
//
//                    // 4D Torus Projection Math for seamless mapping
//                    double angleX = ((double) x / SIZE) * 2.0 * Math.PI;
//                    double angleY = ((double) y / SIZE) * 2.0 * Math.PI;
//
//                    double tx = Math.cos(angleX) * baseScale;
//                    double ty = Math.sin(angleX) * baseScale;
//                    double tz = Math.cos(angleY) * baseScale;
//                    double tw = Math.sin(angleY) * baseScale;
//
//                    // Geological and Meteorological Noise Layers
//                    double elevation = getOctaveNoise(tx, ty, tz, tw, 4, 0.40);
//                    double moisture  = getOctaveNoise(tx + 300.0, ty + 300.0, tz + 300.0, tw + 300.0, 3, 0.45);
//                    double tempNoise = getOctaveNoise(tx + 600.0, ty + 600.0, tz + 600.0, tw + 600.0, 3, 0.50);
//
//                    // Convert math to clean 0.0 - 1.0 ranges
//                    elevation = (elevation + 1.0) / 2.0;
//                    moisture  = (moisture + 1.0) / 2.0;
//                    tempNoise = (tempNoise + 1.0) / 2.0;
//
//                    // TOROIDAL TUNDRA SYSTEM: Drives cold regions by a continuous,
//                    // seamless temperature model so it loops beautifully without forming artificial strip walls.
//                    double temperature = tempNoise;
//                    if (elevation > 0.62) {
//                        temperature -= (elevation - 0.62) * 0.6; // High elevation cooling
//                    }
//
//                    // --- ECOLOGICAL ASSIGNMENT MATRIX ---
//                    if (elevation < 0.35) {
//                        biomeGrid[x][y] = OCEAN;
//                    }
//                    // NARROW SHARP COASTS: Reduced threshold range keeps beaches tightly bound to oceans
//                    else if (elevation < 0.375) {
//                        biomeGrid[x][y] = COAST;
//                    }
//                    // MASSIVE MOUNTAINS & GUARANTEED VOLCANIC FIELDS
//                    else if (elevation > 0.65) {
//                        // Volcanics form in dense clusters at major high-elevation tectonic hotspots
//                        if (elevation > 0.74 && moisture > 0.45) {
//                            biomeGrid[x][y] = VOLCANIC;
//                        } else {
//                            biomeGrid[x][y] = MOUNTAIN;
//                        }
//                    }
//                    // CONTINENTAL LOWLAND MACRO-BIOMES
//                    else {
//                        if (temperature < 0.26) {
//                            biomeGrid[x][y] = TUNDRA;  // Expansive, organic polar/alpine ranges
//                        } else if (moisture < 0.30) {
//                            biomeGrid[x][y] = DESERT;  // Mega-scale arid deserts matching 50-step lifespans
//                        } else if (moisture > 0.76) {
//                            biomeGrid[x][y] = SWAMP;   // Giant connected wetland territories
//                        } else if (moisture < 0.46) {
//                            biomeGrid[x][y] = PLAINS;
//                        } else {
//                            // Thick forest bands separated by regional climate metrics
//                            biomeGrid[x][y] = (temperature > 0.58) ? JUNGLE : FOREST;
//                        }
//                    }
//                }
//            }
//
//            // Export data matrix to file
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter("world_500-4.txt"))) {
//                for (int y = 0; y < SIZE; y++) {
//                    for (int x = 0; x < SIZE; x++) {
//                        writer.write(biomeGrid[x][y]);
//                    }
//                    writer.newLine();
//                }
//                System.out.println("Success! Your balanced, natural text world is ready: 'world_500-4.txt'");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private static double getOctaveNoise(double x, double y, double z, double w, int octaves, double persistence) {
//            double total = 0;
//            double frequency = 1.0;
//            double amplitude = 1.0;
//            double maxValue = 0;
//            for (int i = 0; i < octaves; i++) {
//                total += perlin4D(x * frequency, y * frequency, z * frequency, w * frequency) * amplitude;
//                maxValue += amplitude;
//                amplitude *= persistence;
//                frequency *= 2.0;
//            }
//            return total / maxValue;
//        }
//
//        private static double perlin4D(double x, double y, double z, double w) {
//            int X = (int) Math.floor(x) & 255; int Y = (int) Math.floor(y) & 255;
//            int Z = (int) Math.floor(z) & 255; int W = (int) Math.floor(w) & 255;
//            x -= Math.floor(x); y -= Math.floor(y); z -= Math.floor(z); w -= Math.floor(w);
//            double u = fade(x); double v = fade(y); double t = fade(z); double q = fade(w);
//            int A = p[X] + Y; int AA = p[A] + Z; int AB = p[A + 1] + Z;
//            int B = p[X + 1] + Y; int BA = p[B] + Z; int BB = p[B + 1] + Z;
//            int AAA = p[AA] + W; int BAA = p[BA] + W; int ABA = p[AB] + W; int BBA = p[BB] + W;
//            int AAB = p[AA + 1] + W; int BAB = p[BA + 1] + W; int ABB = p[AB + 1] + W; int BBB = p[BB + 1] + W;
//            return lerp(q, lerp(t, lerp(v, lerp(u, grad(p[AAA], x, y, z, w), grad(p[BAA], x - 1, y, z, w)),
//                                    lerp(u, grad(p[ABA], x, y - 1, z, w), grad(p[BBA], x - 1, y - 1, z, w))),
//                            lerp(v, lerp(u, grad(p[AAB], x, y, z - 1, w), grad(p[BAB], x - 1, y, z - 1, w)),
//                                    lerp(u, grad(p[ABB], x, y - 1, z - 1, w), grad(p[BBB], x - 1, y - 1, z - 1, w)))),
//                    lerp(t, lerp(v, lerp(u, grad(p[AAA + 1], x, y, z, w - 1), grad(p[BAA + 1], x - 1, y, z, w - 1)),
//                                    lerp(u, grad(p[ABA + 1], x, y - 1, z, w - 1), grad(p[BBA + 1], x - 1, y - 1, z, w - 1))),
//                            lerp(v, lerp(u, grad(p[AAB + 1], x, y, z - 1, w - 1), grad(p[BAB + 1], x - 1, y, z - 1, w - 1)),
//                                    lerp(u, grad(p[ABB + 1], x, y - 1, z - 1, w - 1), grad(p[BBB + 1], x - 1, y - 1, z - 1, w - 1)))));
//        }
//
//        private static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
//        private static double lerp(double t, double a, double b) { return a + t * (b - a); }
//        private static double grad(int hash, double x, double y, double z, double w) {
//            int h = hash & 31;
//            double u = h < 24 ? x : y; double v = h < 16 ? y : z; double width = h < 8 ? z : w;
//            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v) + ((h & 4) == 0 ? width : -width);
//        }


