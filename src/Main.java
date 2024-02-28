import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;


public class Main {
    // I/O config
    private static final String INPUT_PATH = "src/input.png"; // Path to the input texture
    private static final String OUTPUT_PATH = "src/out/output.png"; // Where to save the output texture
    private static final String INPUT_MAPPING_PATH = "src/mappings/input.txt"; // Path to the input texture format's mapping
    private static final String OUTPUT_MAPPING_PATH = "src/mappings/output.txt"; // Path to the output texture format's mapping

    // Advanced config
    private static final String MAPPING_SEPARATOR_REGEX = "\\s+"; // Regex to match separator for mapping values

    public static void main(String[] args) throws IOException {
        List<List<Integer>> inputMapping = spaceSplitIntFile(INPUT_MAPPING_PATH);
        List<List<Integer>> outputMapping = spaceSplitIntFile(OUTPUT_MAPPING_PATH);

        BufferedImage inputImg = ImageIO.read(new File(INPUT_PATH));
        BufferedImage outputImg = rearrange(inputImg, inputMapping, outputMapping);

        saveOutputImage(outputImg, OUTPUT_PATH);
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
                String[] split = scanner.nextLine().split(MAPPING_SEPARATOR_REGEX); // Split by separator
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
        int height = width;
        
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
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    // Join two BufferedImage's together
    private static BufferedImage stitch(BufferedImage a, BufferedImage b, boolean vertical) {
        int width = vertical ? Math.max(a.getWidth(), b.getWidth()) : a.getWidth() + b.getWidth();
        int height = vertical ? a.getHeight() + b.getHeight() : Math.max(a.getHeight(), b.getHeight());

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();

        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(a, null, 0, 0);
        if (vertical) {
            g2.drawImage(b, null, 0, a.getHeight());
        } else {
            g2.drawImage(b, null, a.getWidth(), 0);
        }
        g2.dispose();

        return newImage;
    }

    private static List<List<BufferedImage>> sliceImage(BufferedImage inputImg, List<List<Integer>> map, Map<Integer, Rectangle> imageSectionsMap) {
        List<List<BufferedImage>> pieces = new ArrayList<>(getMaxSublistLength(map) * map.size());
        for (int y = 0; y < map.size(); y++) {
            List<Integer> line = map.get(y);
            pieces.add(new ArrayList<>(line.size()));
            for (int x = 0; x < line.size(); x++) {
                Integer cell = line.get(x);

                BufferedImage cutout = getImageSection(inputImg, imageSectionsMap.get(cell));
                pieces.get(y).add(debugMark(cutout, (10*y)+x+""));
            }
        }
        return pieces;
    }


    private static BufferedImage rearrange(BufferedImage inputImg, List<List<Integer>> inputMap, List<List<Integer>> outputMap) {
        Map<Integer, Rectangle> imageSectionsMap = mapImage(inputImg, inputMap);

        List<List<BufferedImage>> pieces = sliceImage(inputImg, outputMap, imageSectionsMap);

        int width = getMaxSublistLength(pieces);
        int height = pieces.size();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (List<BufferedImage> line : pieces) {

            int lineWidth = line.size() * line.stream().max(Comparator.comparingInt(BufferedImage::getWidth)).get().getWidth();
            int lineHeight = line.stream().max(Comparator.comparingInt(BufferedImage::getHeight)).get().getHeight();
            BufferedImage blob = new BufferedImage(lineWidth, lineHeight, BufferedImage.TYPE_INT_ARGB);
            for (BufferedImage cell : line) {
                blob = stitch(blob, cell, false);
            }

            result = stitch(result, blob, true);
        }

        return result;
    }

    private static BufferedImage debugMark(BufferedImage image, String text) {
        return image;
        /*Graphics g = image.getGraphics();
        g.setColor(Color.green);
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.drawString(text, image.getWidth()/2, image.getHeight()/2);
        g.drawRect(0, 0, image.getWidth()-1, image.getHeight()-1);
        g.dispose();
        return image;*/
    };

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