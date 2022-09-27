package npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
            neighbors = findNieghbors(g);
            int n = g.getNumVertices();
            int largestDegree = getLargestDegree();
            double minCoverSize = Math.ceil((double) n / (largestDegree + 1.0));
            int minK = (int) minCoverSize;
            ArrayList<Integer> vertexCover = findMinimumVertexCover(graphs.get(i),minK);
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.format("G%d(%d, %d) (size=%d, ms=%d) {", i + 1, graphs.get(i).getNumVertices(),
                    graphs.get(i).getNumEdges(), vertexCover.size(), elapsedTime);
            printVertexCover(vertexCover);
        }
    }

    /**
     * Finds the minimum vertex cover of a civer graph
     * 
     * @param g Graph to find the vertex cover of.
     * @return Minimum vertex cover of the Graph g.
     */
    public ArrayList<Integer> findMinimumVertexCover(Graph g, int k) {
        int n = g.getNumVertices();
        ArrayList<ArrayList<Integer>> allVertexCovers = new ArrayList<>();
        ArrayList<Integer> originalVertexCover = new ArrayList<>();
        /* populating original cover w/ values */
        for (int i = 0; i < g.getNumVertices(); i++) {
            originalVertexCover.add(1);
        }

        boolean vertexCoverFound = false;
        int size, min;
        min = n + 1;
        /* apply algorithm? */
        for (int i = 0; i < g.getNumVertices(); i++) {
            if (vertexCoverFound)
                break;
            ArrayList<Integer> cover = new ArrayList<>(originalVertexCover);
            cover.set(i, 0);
            cover = removeCoveredVertexes(cover);
            size = getCoverSize(cover);
            min = Math.min(size, min);
            /* while our size is less than or equal to the the minimum vertex cover size */
            if (size <= k) {
                allVertexCovers.add(cover);
                vertexCoverFound = true;
                break;
            }
            for (int j = 0; j < n - k; j++) {
                cover = addUncoveredNeighbors(cover, j);
            }
            size = getCoverSize(cover);
            min = Math.min(size, min);
            allVertexCovers.add(cover);
            if (size <= k) {
                vertexCoverFound = true;
                break;
            }
        }
        /* pairwise unions */
        for (int p = 0; p < allVertexCovers.size(); p++) {
            if (vertexCoverFound)
                break;
            for (int q = p + 1; q < allVertexCovers.size(); q++) {
                if (vertexCoverFound)
                    break;
                ArrayList<Integer> cover = new ArrayList<>(originalVertexCover);
                for (int j = 0; j < cover.size(); j++) {
                    if (allVertexCovers.get(p).get(j) == 0 && allVertexCovers.get(q).get(j) == 0) {
                        cover.set(j, 0);
                    }
                }

                cover = removeCoveredVertexes(cover);
                size = getCoverSize(cover);
                min = Math.min(size, min);
                if (size <= k) {
                    vertexCoverFound = true;
                    break;
                }
                for (int j = 0; j < k; j++) {
                    cover = addUncoveredNeighbors(cover, j);
                }
                size = getCoverSize(cover);
                min = Math.min(size, min);
                if (size <= k) {
                    vertexCoverFound = true;
                    break;
                }
            }
        }
        ArrayList<Integer> cover = new ArrayList<>();
        for(int i=allVertexCovers.size()-1;i>=0;i--){
            int sz = getCoverSize(allVertexCovers.get(i));
            if(sz == min){
                cover = allVertexCovers.get(i);
                break;
            }
        }

        ArrayList<Integer> vertexesInCover = new ArrayList<>();
        for (int i = 0; i < cover.size(); i++) {
            if (cover.get(i) == 1) {
                vertexesInCover.add(i);
            }
        }

        return vertexesInCover;
    }

    private int getLargestDegree(){
        int largestDegree = 0;
        for(ArrayList<Integer> neighbor: neighbors){
            largestDegree = Math.max(largestDegree, neighbor.size());
        }
        return largestDegree;
    }
    /**
     * Linear search to find the max inDegree of the graph
     * 
     * @param inDegrees Array of indegrees of the graph
     * @return Index of the largest inDegree.
     */
    private int getLargestInDegreeIndex(int[] inDegrees) {
        int largestIndex = 0;
        for (int i = 1; i < inDegrees.length; i++) {
            if (inDegrees[i] > inDegrees[largestIndex]) {
                largestIndex = i;
            }
        }
        return largestIndex;
    }

    /**
     * Finds the adjacent neighbors for each node
     * 
     * @param g Graph to find the nieghbors of
     * @return ArrayList of ArraysLists with each row having the indexes of the
     *         neighbors the vertex is neighbors with.
     */
    private ArrayList<ArrayList<Integer>> findNieghbors(Graph g) {
        boolean[][] adjMatrix = g.getAdjMatrix();
        for (int i = 0; i < adjMatrix.length; i++) {
            ArrayList<Integer> neighbor = new ArrayList<Integer>();
            for (int j = 0; j < adjMatrix[0].length; j++) {
                if (adjMatrix[i][j]) {
                    neighbor.add(j);
                }
            }
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    /**
     * Checks if a given vertex is removable
     * 
     * @param neighbor List of neighbors for the current vertex being checked
     * @param cover    Current state of the cover
     * @return Boolean reflecting if vertex can be removed
     */
    private boolean isRemovable(ArrayList<Integer> neighbor, ArrayList<Integer> cover) {
        boolean removeable = true;
        for (int i = 0; i < neighbor.size(); i++) {
            if (cover.get(neighbor.get(i)) == 0) {
                removeable = false;
                break;
            }
        }
        return removeable;
    }

    /**
     * Finds the index of the vertex that will remove the most neighbors
     * 
     * @param cover Current state of the vertex cover
     * @return Index of the vertex to remove
     */
    private int maxRemoveableIndex(ArrayList<Integer> cover) {
        int maxRemovableIndex = -1;
        int max = -1;
        for (int i = 0; i < cover.size(); i++) {
            if (cover.get(i) == 1 && isRemovable(neighbors.get(i), cover)) {
                ArrayList<Integer> newCover = new ArrayList<>(cover);
                newCover.set(i, 0);
                int sum = 0;
                /* calculating number of removable indexes for current vertex */
                for (int j = 0; j < newCover.size(); j++) {
                    if (newCover.get(j) == 1 && isRemovable(neighbors.get(j), newCover)) {
                        sum++;
                    }
                }
                /* new max removable index found, so updating */
                if (sum > max) {
                    if (maxRemovableIndex == -1 || neighbors.get(maxRemovableIndex).size() >= neighbors.get(i).size()) {
                        max = sum;
                        maxRemovableIndex = i;
                    }
                }
            }
        }
        return maxRemovableIndex;
    }

    /**
     * Removes maximum removable vertex until the vertex cover has no removable
     * vertices
     * 
     * @param cover Current state of the vertex cover
     * @return New state of the vertex cover
     */
    private ArrayList<Integer> removeCoveredVertexes(ArrayList<Integer> cover) {
        ArrayList<Integer> newCover = cover;
        int remIndex = maxRemoveableIndex(newCover);
        /* removing indexes while possible */
        while (remIndex != -1) {
            newCover.set(remIndex, 0);
            remIndex = maxRemoveableIndex(newCover);
        }

        return newCover;
    }

    /**
     * Adds the uncovered neighbors to the vertex cover, so every edge is covered.
     * 
     * @param cover Current state of the vertex cover
     * @param k     Minimum vertex cover size
     * @return New state of the vertex cover
     */
    private ArrayList<Integer> addUncoveredNeighbors(ArrayList<Integer> cover, int k) {
        int count = 0;
        ArrayList<Integer> newCover = new ArrayList<Integer>(cover);
        for (int i = 0; i < cover.size(); i++) {
            if (newCover.get(i) == 1) {
                int sum, index;
                index = 0;
                sum = 0;
                for (int j = 0; j < neighbors.get(i).size(); j++) {
                    int coverIndex = neighbors.get(i).get(j);
                    /* setting index as there's a new neighbor to add */
                    if (newCover.get(coverIndex) == 0) {
                        index = j;
                        sum++;
                    }
                }
                int coverIndex = neighbors.get(i).get(index);
                /* adding new neighbor to cover */
                if (sum == 1 && cover.get(coverIndex) == 0) {
                    newCover.set(coverIndex, 1);
                    newCover.set(i, 0);
                    newCover = removeCoveredVertexes(newCover); // removing vertexes that are now covered by the
                                                                // addition of this new neighbor
                    count++;
                }

                /* count is larger than maximum k-vertex cover */
                if (count > k)
                    break;
            }
        }
        return newCover;
    }


    /**
     * Gets the cover size (amount of elements w/ value 1)
     * 
     * @param cover Vertex cover to get the size of
     * @return Size of the vertex cover
     */
    private int getCoverSize(ArrayList<Integer> cover) {
        int count = 0;
        for (int i = 0; i < cover.size(); i++) {
            if (cover.get(i) == 1)
                count++;
        }
        return count;
    }

    /**
     * Helper function to print a vertex cover for debugging purposes
     * 
     * @param vertexCover Vertex Cover to print
     */
    private void printVertexCover(ArrayList<Integer> vertexCover) {
        for (int j = 0; j < vertexCover.size() - 1; j++) {
            System.out.print(vertexCover.get(j) + ", ");
        }
        if (vertexCover.size() != 0) {
            System.out.print(vertexCover.get(vertexCover.size() - 1));
        }
        System.out.print("}\n");
    }
}
