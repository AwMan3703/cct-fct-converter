import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;


public class Main {
    public static void main(String[] args) throws IOException {
        List<List<Integer>> inputMapping = spaceSplitIntFile("src/data/input.txt");
        List<List<Integer>> outputMapping = spaceSplitIntFile("src/data/output.txt");

        BufferedImage inputImg = ImageIO.read(new File("src/data/input_texture.png"));
        BufferedImage outputImg = rearrange(inputImg, inputMapping, outputMapping);

        saveOutputImage(outputImg, "src/data/outputimg.png");
    }

    private static <T> int getMaxSublistLength(List<List<T>> list) {
        return list.stream().max(Comparator.comparingInt(List::size)).get().size(); // Find longest list's length
    }

    private static List<List<Integer>> spaceSplitIntFile(String path) {
        try { // Read all the values off the file
            List<List<Integer>> result = new ArrayList<>(); // "wHy DiD yOu NoT uSe int[][]☝️🤓" shut up it's easier

            File file = new File(path);
            Scanner scanner = new Scanner(file);

            // Read the file
            while (scanner.hasNextLine()) { // For each line in the file
                String[] split = scanner.nextLine().split("\\s+"); // Split by spaces
                List<Integer> splitInt = new ArrayList<Integer>();
                for (String s : split) {
                    try {
                        splitInt.add(Integer.parseInt(s.trim())); // try to convert to int
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

    // Maps a table of integers to square sections of a BufferedImage
    private static Map<Integer, Rectangle> mapImage(BufferedImage image, List<List<Integer>> intMap) {
        int width = image.getWidth() / getMaxSublistLength(intMap);
        int height = image.getHeight() / intMap.size();
        
        Map<Integer, Rectangle> result = new HashMap<>();
        for (int mapY = 0; mapY < intMap.size(); mapY++) {
            List<Integer> line = intMap.get(mapY);
            for (int mapX = 0; mapX < line.size(); mapX++) {
                Integer cell = line.get(mapX);

                Rectangle rect = new Rectangle(
                        mapX * width,
                        mapY * height,
                        width, height
                );
                result.put(cell, rect);
            }
        }

        return result;
    }

    // Totally original function that I didn't copy-paste from stackoverflow what are you even saying
    // (thanks https://stackoverflow.com/a/4818980 for the utility <3)
    private static BufferedImage getImageSection(BufferedImage src, Rectangle rect) {
        return src.getSubimage(0, 0, rect.width, rect.height);
    }

    private static BufferedImage rearrange(BufferedImage inputImg, List<List<Integer>> inputMap, List<List<Integer>> outputMap) {
        Map<Integer, Integer> imageSectionsMap = mapImage();


        return new BufferedImage(1, 1, 2);
    }

    private static void saveOutputImage(BufferedImage image, String fileName) {
        try {
            File output = new File(fileName);
            ImageIO.write(image, "PNG", output);
            System.out.println("Output image saved successfully as " + fileName);
        } catch (IOException e) {
            System.err.println("Error: Failed to save output image.");
        }
    }
}