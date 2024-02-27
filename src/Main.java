import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;

public class Main {

    public static void main(String[] args) {
        List<List<Integer>> inputMapping = spaceSplitIntFile("src/data/input.txt");
        List<List<Integer>> outputMapping = spaceSplitIntFile("src/data/output.txt");
        ImagePlus inputImg = IJ.openImage("src/data/input_texture.png");

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