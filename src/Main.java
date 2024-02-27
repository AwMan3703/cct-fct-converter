import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;

public class Main {

    public static void main(String[] args) {
        List<List<Integer>> inputMapping = spaceSplitIntFile("src/data/input.txt");
        List<List<Integer>> outputMapping = spaceSplitIntFile("src/data/output.txt");
        ImagePlus inputTplImg = IJ.openImage("src/data/input_texture.png");
        ImagePlus outputTplImg = IJ.openImage("src/data/output_texture.png");
    }

    private static List<List<Integer>> spaceSplitIntFile(String path) {
        try { // Read all the values off the file
            List<List<Integer>> result = new ArrayList<>(); // "wHy DiD yOu NoT uSe int[][]☝️🤓" shut up it's easier

            File file = new File(path);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { // For each line in the file
                String[] split = scanner.nextLine().split("\\s+");
                List<Integer> splitInt = new ArrayList<Integer>();
                for (String s : split) {
                    try {
                        splitInt.add(Integer.parseInt(s)); // try to convert to int
                    } catch (NumberFormatException e) {} //idgaf just go on
                }
                result.add(splitInt);
            }
            /*result.forEach((line)->{ // Uncomment to print the generated table
                System.out.println(line.toString());
            });*/
            return result;
        } catch (FileNotFoundException e) {
            System.err.println("Could not parse space-separated values file");
            e.printStackTrace();
        }
        return null;
    }

    public static void saveOutputImage(BufferedImage image, String fileName) {
        try {
            File output = new File(fileName);
            ImageIO.write(image, "PNG", output);
            System.out.println("Output image saved successfully as " + fileName);
        } catch (IOException e) {
            System.err.println("Error: Failed to save output image.");
        }
    }
}