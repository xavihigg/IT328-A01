package npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Reads in graphs from a text file and finds the minimum vertex cover for each graph.
 * Additionally includes a method for finding a k-VertexCover
 * @author: Tom Freier
 */
public class findVCover {
    private ArrayList<Graph> graphs;
    private ArrayList<Integer> minCover;

    public static void main(String[] args) {
        String fileName;
        /* processing command line args */
        if (args.length == 0) {
            fileName = "testGraphs.txt";
        } else {
            fileName = args[0];
        }

        /* creating new object and fining all the vertex covers for all the graphs */
        findVCover cover = new findVCover();
        cover.readFile(fileName);
        cover.findAllVertexCovers();
    }

    /**
     * Default Constructor for findVCover class.
     */
    public findVCover() {
        this.graphs = new ArrayList<Graph>();
        this.minCover = new ArrayList<>();
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

    /**
     * Finds the minimum vertex cover of a given graph by trying all the possible starting states
     * @param g Graph to find the vertex cover of
     * @return  ArrayList with the vertices included in the cover
     */
    public ArrayList<Integer> findMinimumVertexCover(Graph g) {
        VertexCover vc = new VertexCover(g);
        this.minCover = new ArrayList<Integer>(vc.getCover());
        for (int i = 0; i < g.getNumVertices(); i++) {
            vc = new VertexCover(g);
            if(vc.vertexCanBeRemoved(i)){
                vc.removeVertex(i);
                findMinimumVertexCover(vc);
            }
        }
        Collections.sort(this.minCover);
        return this.minCover;
    }

    /**
     * Recursive function that finds the minimum vertex cover by removing the vertex with the least number of edges that can be removed.
     * @param vc    Current state of the vertex cover
     * The minimum vertex cover will be stored at this.minCover
     */
    public void findMinimumVertexCover(VertexCover vc) {
        ArrayList<Integer> canidates = vc.findBestVertexToRemove();
        /* recursive calls to search all possible canidates */
        for (int i = 0; i < canidates.size(); i++) {
            VertexCover newVertexCover = new VertexCover(vc);
            newVertexCover.removeVertex(canidates.get(i)); // found n-1 cover
            findMinimumVertexCover(newVertexCover); // searching for n-2 cover
            if (newVertexCover.getCover().size() < this.minCover.size()) {
                this.minCover = new ArrayList<>(newVertexCover.getCover());
            }
        }
        if (vc.getCover().size() < this.minCover.size()) {
            this.minCover = new ArrayList<>(vc.getCover());
        }
    }

    /**
     * Recursive function that attempts to search for a k vertex cover
     * @param vc   Current state of the vertex cover 
     * @param k     Desired size to the cover to search for
     * The minimum vertex cover (that was found) will be stored at this.minCover
     */
    public void findKvertexCover(VertexCover vc, int k){
        /* base case */
        if(this.minCover.size() <= k){
            return;
        }
        else{ /* recursive calls to search all possible canidates*/
            ArrayList<Integer> canidates = vc.findBestVertexToRemove();
            for (int i = 0; i < canidates.size(); i++) {
                VertexCover newVertexCover = new VertexCover(vc);
                newVertexCover.removeVertex(canidates.get(i)); // found n-1 cover
                findKvertexCover(newVertexCover, k); // searching for n-2 cover
                if (newVertexCover.getCover().size() < this.minCover.size()) {
                    this.minCover = new ArrayList<>(newVertexCover.getCover());
                }
            }
            if (vc.getCover().size() < this.minCover.size()) {
                this.minCover = new ArrayList<>(vc.getCover());
            }
        }
    
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
