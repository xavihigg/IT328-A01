package npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class kVertexCover {
    private ArrayList<Graph> graphs;
    ArrayList<ArrayList<Integer>> neighbors;

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
        this.graphs = new ArrayList<Graph>();
        this.neighbors = new ArrayList<ArrayList<Integer>>();
    }

    /**
     * Reads in a file of graphs
     * 
     * @param inputFileName Filename of the text document
     * @return Boolean reflecting if could be opened and fileFormatted correctly
     */
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
            }

        } catch (IOException e) {
            System.err.println("Error opening next file: " + inputFileName);
            fileFormatCorrect = false;
        }
        return fileFormatCorrect;
    }

    /**
     * Finds the vertex cover for each graph and displays an output to the console
     */
    public void findAllVertexCovers() {
        for (int i = 0; i < graphs.size(); i++) {
            long startTime = System.currentTimeMillis();
            
            Graph g = graphs.get(i);
            ArrayList<Integer> vertexCover = findMinimumVertexCover(g);
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.format("G%d(%d, %d) (size=%d, ms=%d) ", i + 1, g.getNumVertices(),
                    g.getNumEdges(), vertexCover.size(), elapsedTime);
            printVertexCover(vertexCover);
        }
    }

    public ArrayList<Integer> findMinimumVertexCover(Graph g){
        int minCoverSize = Integer.MAX_VALUE;
        ArrayList<Integer> minCover = new ArrayList<>();
        for(int i=1;i<g.getNumVertices();i++){
            VertexCover vc = new VertexCover(g);
            vc.fillCover();
            vc.removeVertex(i);
            ArrayList<Integer> cover = vc.findMinimualertexCover();
            if(cover.size() < minCoverSize){
                minCoverSize = cover.size();
                minCover = cover;
                System.out.print("Size: " + cover.size());
                printVertexCover(cover);
            }
        }
        Collections.sort(minCover);
        return minCover;
    }

    /**
     * Helper function to print a vertex cover for debugging purposes
     * 
     * @param vertexCover Vertex Cover to print
     */
    private void printVertexCover(ArrayList<Integer> vertexCover) {
        System.out.print("{");
        for (int j = 0; j < vertexCover.size() - 1; j++) {
            System.out.print(vertexCover.get(j) + ", ");
        }
        if (vertexCover.size() != 0) {
            System.out.print(vertexCover.get(vertexCover.size() - 1));
        }
        System.out.print("}\n");
    }
}
