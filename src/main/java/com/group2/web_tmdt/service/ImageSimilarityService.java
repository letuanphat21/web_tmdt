package com.group2.web_tmdt.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Image Similarity Service using histogram comparison
 * Không dùng native library OpenCV, chỉ dùng Java built-in utilities
 */
@Service
@Slf4j
public class ImageSimilarityService {

    /**
     * Calculate histogram similarity between uploaded image and reference image URL
     * @param uploadedFile Uploaded image file
     * @param referenceImageUrl URL to reference image
     * @return Similarity score from 0 to 1 (1 = identical)
     */
    public double calculateSimilarityWithUploadedFile(MultipartFile uploadedFile, String referenceImageUrl) {
        try {
            log.info("Comparing uploaded image with: {}", referenceImageUrl);
            
            // Read uploaded image
            BufferedImage uploadedImage = ImageIO.read(uploadedFile.getInputStream());
            
            // Read reference image from URL
            BufferedImage referenceImage = ImageIO.read(new URL(referenceImageUrl));

            if (uploadedImage == null || referenceImage == null) {
                log.warn("One or both images are null. Uploaded: {}, Reference: {}", uploadedImage != null, referenceImage != null);
                return 0.0;
            }

            double similarity = calculateHistogramSimilarity(uploadedImage, referenceImage);
            log.debug("Similarity calculated: {}", similarity);
            return similarity;
        } catch (IOException e) {
            log.error("IOException when comparing images with URL: {}", referenceImageUrl, e);
            return 0.0;
        } catch (Exception e) {
            log.error("Unexpected error in calculateSimilarityWithUploadedFile", e);
            return 0.0;
        }
    }

    /**
     * Calculate histogram similarity between two BufferedImage
     * @param img1 First image
     * @param img2 Second image
     * @return Similarity score from 0 to 1
     */
    private double calculateHistogramSimilarity(BufferedImage img1, BufferedImage img2) {
        try {
            // Resize both images to same size (256x256) for fair comparison
            BufferedImage resized1 = resizeImage(img1, 256, 256);
            BufferedImage resized2 = resizeImage(img2, 256, 256);

            // Calculate histograms for each color channel
            int[] hist1 = calculateHistogram(resized1);
            int[] hist2 = calculateHistogram(resized2);

            // Compare histograms using Chi-Square distance
            double distance = chiSquareDistance(hist1, hist2);

            // Normalize to 0-1 scale (lower distance = higher similarity)
            // Using exponential decay to convert distance to similarity
            double similarity = Math.exp(-distance / 1000.0);
            
            double finalSimilarity = Math.max(0.0, Math.min(1.0, similarity));
            log.debug("Chi-square distance: {}, final similarity: {}", distance, finalSimilarity);
            return finalSimilarity;
        } catch (Exception e) {
            log.error("Error in calculateHistogramSimilarity", e);
            return 0.0;
        }
    }

    /**
     * Resize BufferedImage to target dimensions
     */
    private BufferedImage resizeImage(BufferedImage src, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    /**
     * Calculate histogram from image
     * Returns array of 256 bins (for grayscale)
     */
    private int[] calculateHistogram(BufferedImage image) {
        int[] histogram = new int[256];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                
                // Extract RGB components
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Calculate grayscale value (luminosity method)
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                histogram[gray]++;
            }
        }

        // Normalize histogram
        normalizeHistogram(histogram);
        return histogram;
    }

    /**
     * Normalize histogram to 0-1 range
     */
    private void normalizeHistogram(int[] histogram) {
        int sum = 0;
        for (int value : histogram) {
            sum += value;
        }

        if (sum > 0) {
            for (int i = 0; i < histogram.length; i++) {
                histogram[i] = (int) ((double) histogram[i] / sum * 10000);
            }
        }
    }

    /**
     * Calculate Chi-Square distance between two histograms
     * Lower value = more similar
     */
    private double chiSquareDistance(int[] hist1, int[] hist2) {
        double distance = 0.0;

        for (int i = 0; i < Math.min(hist1.length, hist2.length); i++) {
            int diff = hist1[i] - hist2[i];
            if (hist1[i] + hist2[i] > 0) {
                distance += (double) (diff * diff) / (hist1[i] + hist2[i]);
            }
        }

        return distance;
    }
}
