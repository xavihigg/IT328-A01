package npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class kVertexCover {
    private ArrayList<Graph> graphs;

    public static void main(String[] args) {
        String fileName;
        /* processing command line args */
        if (args.length == 0) {
            fileName = "testGraphs.txt";
        } else {
            fileName = args[0];
        }

        kVertexCover cover = new kVertexCover();
        cover.readFile(fileName);
        cover.findAllVertexCovers();
    }

    public kVertexCover() {
        graphs = new ArrayList<Graph>();
    }

    public boolean readFile(String inputFileName) {
        boolean fileFormatCorrect = true;
        Path path = Paths.get(inputFileName);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int numVertices = Integer.valueOf(line);
                Graph g = new Graph(numVertices);

                /* populating graph */
                for (int row = 0; row < numVertices; row++) {
                    line = reader.readLine();
                    int col = 0;
                    for (int i = 0; i < line.length(); i += 2) {
                        if (line.charAt(i) == '1') {
                            g.addEdge(row, col);
                        }
                        col++;
                    }
                }
                graphs.add(g);
                System.out.print(g);
            }

        } catch (IOException e) {
            System.err.println("Error opening next file: " + inputFileName);
            fileFormatCorrect = false;
        }
        return fileFormatCorrect;
    }

    public void findAllVertexCovers() {
        for (int i = 0; i < graphs.size(); i++) {
            long startTime = System.currentTimeMillis();
            ArrayList<Integer> vertexCover = findMinimumVertexCover(graphs.get(i));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.format("G%d(%d, %d) (size=%d, ms=%d) {", i + 1, graphs.get(i).getNumVertices(),
                    graphs.get(i).getNumEdges(), vertexCover.size(), elapsedTime);
            printVertexCover(vertexCover);
        }
    }

    public ArrayList<Integer> findMinimumVertexCover(Graph g) {
        int[] inDegree = g.getInDegree();
        ArrayList<int[]> remainingEdges = g.getEdges();
        ArrayList<Integer> vertexCover = new ArrayList<>(0);


        while (remainingEdges.size() != 0) {
            int indexToAddToCover = getLargestInDegreeIndex(inDegree);
            vertexCover.add(indexToAddToCover);
            /* removing any edges that are covered by this vertex */
            for (int i = 0; i < remainingEdges.size(); i++) {
                int[] possiblyCoveredEdge = remainingEdges.get(i);
                boolean edgeCovered = (indexToAddToCover == possiblyCoveredEdge[0]
                        || indexToAddToCover == possiblyCoveredEdge[1]);
                if (edgeCovered) {
                    inDegree[possiblyCoveredEdge[0]]--;
                    inDegree[possiblyCoveredEdge[1]]--;
                    remainingEdges.remove(i);
                    i--; // staying at same index
                }
            }
        }

        for (int i = 0; i < inDegree.length; i++) {
            /* a vertex wasn't included in the cover, so a cover isn't possible */
            if (inDegree[i] > 0) {
                vertexCover = new ArrayList<>();
                break;
            }
        }

        Collections.sort(vertexCover);
        return vertexCover;
    }

    private void printVertexCover(ArrayList<Integer> vertexCover) {
        for (int j = 0; j < vertexCover.size() - 1; j++) {
            System.out.print(vertexCover.get(j) + ", ");
        }
        if (vertexCover.size() != 0) {
            System.out.print(vertexCover.get(vertexCover.size() - 1));
        }
        System.out.print("}\n");
    }

    private int getLargestInDegreeIndex(int[] inDerees) {
        int largestIndex = 0;
        for (int i = 1; i < inDerees.length; i++) {
            if (inDerees[i] > inDerees[largestIndex]) {
                largestIndex = i;
            }
        }
        return largestIndex;
    }
}
