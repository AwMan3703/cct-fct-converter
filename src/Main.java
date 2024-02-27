import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            int[][] inputImage = readInputImage("./data/input.txt");
            if (inputImage == null) {
                System.err.println("Error: Input image file is not valid.");
                return;
            }
            BufferedImage textureImage = readTextureImage("input_image.png", "input.txt");
            if (textureImage == null) {
                System.err.println("Error: Texture image file is not valid.");
                return;
            }
            BufferedImage outputImage = generateOutputImage(inputImage, textureImage, "output.txt");
            if (outputImage == null) {
                System.err.println("Error: Failed to generate output image.");
                return;
            }
            saveOutputImage(outputImage, "output_image.png");
            System.out.println("Output image generated successfully.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
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