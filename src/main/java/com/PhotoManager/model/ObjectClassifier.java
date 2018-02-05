package com.PhotoManager.model;

import com.PhotoManager.GUInterface;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ObjectClassifier {
    private byte[] graphDef;
    private List<String> labels;

    public ObjectClassifier() {
        setInceptionPath();
    }

    private void setInceptionPath() {
        try {
            File f = new File(new File(GUInterface.class.getResource("").getPath())
                    .getParentFile().getParentFile(), "inception_dec_2015");
            String INCEPTION_PATH = f.getCanonicalPath();
            graphDef = readAllBytesOrExit(Paths.get(INCEPTION_PATH, "tensorflow_inception_graph.pb"));
            labels = readAllLinesOrExit(Paths.get(INCEPTION_PATH, "imagenet_comp_graph_label_strings.txt"));
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    /**
     * Suggest a tag based on the image using Inception and TensorFlow.
     *
     * @param imagePath the path to the image file
     * @return the suggested tag
     */
    public String suggestTag(String imagePath) {
        byte[] imageBytes = readAllBytesOrExit(Paths.get(imagePath));
        try (Tensor image = Tensor.create(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);
            int indexBestLabel = maxIndex(labelProbabilities);
            return labels.get(indexBestLabel);
        }
    }

    /* This method was sourced from Taha Emara Copyright 2017,
      (http://emaraic.com/blog/object-recognition-using-TensorFlow-Java)
      More info in Phase2/License
    */
    private float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) {
                final long[] resultShape = result.shape();
                if (result.numDimensions() != 2 || resultShape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(resultShape)));
                }
                int numLabels = (int) resultShape[1];
                float[][] resultList = new float[1][numLabels];
                return (result.copyTo(resultList))[0];
            }
        }
    }

    /* This method was sourced from Taha Emara Copyright 2017,
      (http://emaraic.com/blog/object-recognition-using-TensorFlow-Java)
      More info in Phase2/License
    */
    private int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

    /* This method was sourced from Taha Emara Copyright 2017,
      (http://emaraic.com/blog/object-recognition-using-TensorFlow-Java)
      More info in Phase2/License
    */
    private byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /* This method was sourced from Taha Emara Copyright 2017,
      (http://emaraic.com/blog/object-recognition-using-TensorFlow-Java)
      More info in Phase2/License
    */
    private List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }
}
