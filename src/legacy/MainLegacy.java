package legacy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainLegacy {

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
            saveOutputImage(outputImage, "output_texture.png");
            System.out.println("Output image generated successfully.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public static int[][] readInputImage(String fileName) throws IOException {
        System.out.println("File path: " + fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("Input image file not found.");
        }
        System.out.println("Input image file found.");
        Scanner scanner = new Scanner(file);
        int rows = 0;
        int cols = 0;
        while (scanner.hasNextLine()) {
            rows++;
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            cols = Math.max(cols, tokens.length);
        }
        scanner.close();

        System.out.println("Input image size: " + rows + "x" + cols);

        scanner = new Scanner(file);
        int[][] image = new int[rows][cols];
        int rowIndex = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            for (int colIndex = 0; colIndex < tokens.length; colIndex++) {
                if (!tokens[colIndex].equals("-")) {
                    image[rowIndex][colIndex] = Integer.parseInt(tokens[colIndex]);
                } else {
                    image[rowIndex][colIndex] = -1; // Placeholder value
                }
            }
            rowIndex++;
        }
        scanner.close();
        System.out.println("Input image read successfully.");
        return image;
    }

    public static BufferedImage readTextureImage(String fileName, String mappingFile) {
        try {
            System.out.println("Reading texture mapping file: " + mappingFile);
            File mapping = new File(mappingFile);
            if (!mapping.exists()) {
                throw new IOException("Texture mapping file not found.");
            }
            System.out.println("Texture mapping file found.");

            BufferedReader reader = new BufferedReader(new FileReader(mapping));
            List<List<Integer>> textureMappingList = new ArrayList<>();
            String line;
            int currentTextureNumber = 1; // Starting texture number
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line from texture mapping file: " + line);
                String[] numbers = line.trim().split("\\s+");
                List<Integer> textureNumbers = new ArrayList<>();
                for (String number : numbers) {
                    if (number.equals("-")) {
                        // Fill the remaining tiles in this row with 0s
                        while (textureNumbers.size() < 8) {
                            textureNumbers.add(0);
                        }
                        break; // Go to the next row
                    }
                    textureNumbers.add(Integer.parseInt(number));
                    currentTextureNumber++;
                }
                textureMappingList.add(textureNumbers);
            }
            reader.close();

            int maxCols = textureMappingList.stream().mapToInt(List::size).max().orElse(0);
            int[][] textureMapping = new int[textureMappingList.size()][maxCols];
            for (int i = 0; i < textureMappingList.size(); i++) {
                for (int j = 0; j < textureMappingList.get(i).size(); j++) {
                    textureMapping[i][j] = textureMappingList.get(i).get(j);
                }
            }

            for (int[] row : textureMapping) {
                for (int num : row) {
                    System.out.print(num + " ");
                }
                System.out.println();
            }

            if (fileName.equals("texture_image.png")) {
                System.out.println("Texture image file found: " + fileName);
                return ImageIO.read(new File(fileName));
            }

            throw new IOException("Texture image mapping not found for file: " + fileName);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }


    public static BufferedImage generateOutputImage(int[][] inputImage, BufferedImage textureImage, String mappingFile) {
        try {
            int tileSize = 16;
            int rows = inputImage.length;
            int cols = inputImage[0].length; // Adjusted to reflect the correct number of columns

            BufferedReader reader = new BufferedReader(new FileReader(new File(mappingFile)));
            List<List<Integer>> textureMappingList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] numbers = line.trim().split("\\s+");
                List<Integer> textureNumbers = new ArrayList<>();
                for (String number : numbers) {
                    if (!number.equals("-")) {
                        textureNumbers.add(Integer.parseInt(number));
                    }
                }
                textureMappingList.add(textureNumbers);
            }
            reader.close();

            BufferedImage outputImage = new BufferedImage(cols * tileSize, rows * tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();
            for (int i = 0; i < rows; i++) {
                if (i >= textureMappingList.size()) break; // Ensure textureMappingList has enough rows
                List<Integer> rowList = textureMappingList.get(i);
                // Adjust the number of columns to match the rowList size
                cols = rowList.size();
                for (int j = 0; j < cols; j++) {
                    int inputTexture = inputImage[i][j];
                    if (inputTexture == 0) {
                        // Skip drawing empty tile
                        continue;
                    }
                    if (inputTexture != 0 && inputTexture <= cols * rows) {
                        int textureIndex = inputTexture - 1; // Adjust to start indexing from 0
                        int textureRow = textureIndex / cols;
                        int textureCol = textureIndex % cols;
                        int textureX = textureCol * tileSize;
                        int textureY = textureRow * tileSize;
                        // Check if input texture is within the bounds of the texture image
                        if (textureX >= 0 && textureY >= 0 && textureX < textureImage.getWidth() && textureY < textureImage.getHeight()) {
                            // Draw texture image onto output image based on texture mapping coordinates
                            g2d.drawImage(textureImage.getSubimage(textureX, textureY, tileSize, tileSize), j * tileSize, i * tileSize, null);
                        } else {
                            System.err.println("Error: Texture image subimage out of bounds for texture number " + inputTexture);
                        }
                    } else {
                        System.err.println("Error: Texture number out of bounds at position (" + i + ", " + j + ")");
                    }
                }
            }
            g2d.dispose();
            return outputImage;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
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