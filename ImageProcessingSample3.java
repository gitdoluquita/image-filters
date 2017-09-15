import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.GridLayout;


public class ImageProcessingSample3 {
    public static void main (String[] args) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(new File("lena.png"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.toString());
            return;
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));

        BufferedImage[] images = {
            originalImage,
            convertToGrayScale(originalImage),
            invertImageColors(convertToGrayScale(originalImage)),
            //blurImage(originalImage, 10)
            OilPainting(originalImage,5,130)
        };
        for (BufferedImage image : images) {
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            frame.add(imageLabel);
        }

        frame.pack();
        frame.setVisible(true);
    }

    private static BufferedImage convertToGrayScale(
            BufferedImage originalImage
    ) {
        int columns = originalImage.getWidth();
        int rows = originalImage.getHeight();

        BufferedImage gsImage = new BufferedImage(
            columns, rows, BufferedImage.TYPE_INT_RGB
        );

        for(int x = 0; x < columns; ++x) {
            for(int y = 0; y < rows; ++y) {
                int pixelValue = originalImage.getRGB(x, y);

                int red = pixelValue >> 16 & 0xFF;
                int green = pixelValue >> 8 & 0xFF;
                int blue = pixelValue & 0xFF;

                int average = (red + green + blue) / 3;
                pixelValue = (average << 16) + (average << 8) + average;
                gsImage.setRGB(x, y, pixelValue);
            }
        }

        return gsImage;
    }

    private static BufferedImage invertImageColors(
        BufferedImage originalImage
    ) {
        int columns = originalImage.getWidth();
        int rows = originalImage.getHeight();
        BufferedImage invertedImage = new BufferedImage(
                columns, rows, BufferedImage.TYPE_INT_RGB
        );

        for(int x = 0; x < columns; ++x) {
            for(int y = 0; y < rows; ++y) {
                int pixelValue = originalImage.getRGB(x, y);
                int red = 255 - pixelValue >> 16 & 0xFF;
                int green = 255 - pixelValue >> 8 & 0xFF;
                int blue = 255 - pixelValue & 0xFF;
                pixelValue = (red << 16) + (green << 8) + blue;

                // int pixelValue = (255 << 16) + // red
                //     (255 << 8) + // green
                //     (255) - // blue
                //     originalImage.getRGB(x, y);

                invertedImage.setRGB(x, y, pixelValue);
            }
        }

        return invertedImage;
    }

private static BufferedImage blurImage(
        BufferedImage originalImage, int level
) {
    int columns = originalImage.getWidth();
    int rows = originalImage.getHeight();
    BufferedImage blurredImage = new BufferedImage(
        columns, rows, BufferedImage.TYPE_INT_RGB
    );

    int neighborhoodSize = (int) Math.pow(2 * level + 1, 2);

    for(int x = level; x < columns - level; ++x) {
        for(int y = level; y < rows - level; ++y) {
            int red = 0;
            int green = 0;
            int blue = 0;

            for(int xNeighbor = x - level;
                    xNeighbor <= x + level;
                    ++xNeighbor) {
                for(int yNeighbor = y - level;
                        yNeighbor <= y + level;
                        ++yNeighbor) {

                    int pixelValue =
                            originalImage.getRGB(xNeighbor, yNeighbor);
                    red += pixelValue >> 16 & 0xFF;
                    green += pixelValue >> 8 & 0xFF;
                    blue += pixelValue & 0xFF;
                }
            }

            int pixelValue = (red / neighborhoodSize << 16)
                    + (green / neighborhoodSize << 8)
                    + blue / neighborhoodSize;
            blurredImage.setRGB(x, y, pixelValue);
        }
    }

    return blurredImage;
}

private static BufferedImage OilPainting(BufferedImage originalImage, int r, int level){
  int columns = originalImage.getWidth();
  int rows = originalImage.getHeight();
  BufferedImage OilImage = new BufferedImage(columns, rows, BufferedImage.TYPE_INT_RGB);
  int red,green,blue,pixelValue;
  System.out.println("Entrou"+r+columns);

  for(int x = r; x < columns-r; ++x) {
      for(int y = r; y < rows-r; ++y) {
        System.out.println("X:"+x+"Y:"+ y);
        int maxIndex = 0;
        int curMax = -1;
        int[] intensityCount =new int[level];
        int[] averageR =new int[level];
        int[] averageG =new int[level];
        int[] averageB =new int[level];

        for (int i = y-r; i < y+r; i++) {
          for (int j = x; (j-x)*(j-x) + (i-y)*(i-y) <= r*r; j--) {
            pixelValue = originalImage.getRGB(j, i);
            //System.out.println(pixelValue);
            red = pixelValue >> 16 & 0xFF;
            green = pixelValue >> 8 & 0xFF;
            blue = pixelValue & 0xFF;
              int curIntensity = (int)((red+green+blue)/3*level)/255;
              intensityCount[curIntensity]++;
              averageR[curIntensity] += red;
              averageG[curIntensity] += green;
              averageB[curIntensity] += blue;
          }
          for (int j = x+1; (j-x)*(j-x) + (i-y)*(i-y) <= r*r; j++) {
            pixelValue = originalImage.getRGB(j, i);
            //System.out.println(pixelValue);
            red = pixelValue >> 16 & 0xFF;
            green = pixelValue >> 8 & 0xFF;
            blue =  pixelValue & 0xFF;
              int curIntensity = (int)((red+green+blue)/3*level)/255;
              intensityCount[curIntensity]++;
              averageR[curIntensity] += red;
              averageG[curIntensity] += green;
              averageB[curIntensity] += blue;
          }
        }
        for (int i=0;i<level;i++){
          if (intensityCount[i] > curMax){
            curMax= intensityCount[i];
            maxIndex= i;
          }
        }
        red = averageR[maxIndex] / curMax;
        green = averageG[maxIndex] / curMax;
        blue = averageB[maxIndex] / curMax;
        pixelValue = (red << 16) + (green << 8) + blue;
        System.out.println(pixelValue);
        OilImage.setRGB(x, y, pixelValue);
      }
    }
    return OilImage;
  }
}
