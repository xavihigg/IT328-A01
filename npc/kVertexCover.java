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
            int n = g.getNumVertices();
            int largestDegree = getLargestDegree();
            double minCoverSize = n - Math.ceil((double) n / (largestDegree + 1.0));
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
        ArrayList<Integer> cover = new ArrayList<>();
        ArrayList<int[]> edges = g.getEdges();
        while(edges.size() != 0){ // while edges remain
            int vertexToAdd = selectNextVertexToAdd(g, edges, cover);
            cover.add(vertexToAdd);
            removeCoveredEdges(vertexToAdd, edges);
        }
        Collections.sort(cover);
        return cover;
    }

    private ArrayList<ArrayList<Integer>> getIncidentEdges(Graph g, ArrayList<int[]> edges, ArrayList<Integer> cover){
        ArrayList<ArrayList<Integer>> incidentEdges = new ArrayList<>(g.getNumVertices());
        for(int i=0;i<g.getNumVertices();i++){
            incidentEdges.add(new ArrayList<>());
        }
        for(int[] edge: edges){
            int u = edge[0];
            int v = edge[1];
            
            if(!cover.contains(v)){
                ArrayList<Integer> u_val = incidentEdges.get(u);
                u_val.add(v);
                incidentEdges.set(u,u_val);
            }
            if(!cover.contains(u)){
                ArrayList<Integer> v_val = incidentEdges.get(v);
                v_val.add(u);
                incidentEdges.set(v,v_val);  
            }
        }
        return incidentEdges;
    }

    private int selectNextVertexToAdd(Graph g, ArrayList<int[]> edges, ArrayList<Integer> cover){
        ArrayList<ArrayList<Integer>> incidentEdges = getIncidentEdges(g, edges, cover);
        ArrayList<Integer> verticesWithMaxIncidentEdges = new ArrayList<>();
        int maxIncidentEdges = 0;
        for(int i=0;i<g.getNumVertices();i++){
            int numEdges = incidentEdges.get(i).size();
            if(numEdges > maxIncidentEdges){
                verticesWithMaxIncidentEdges.clear();
                maxIncidentEdges = numEdges;
            }
            if(numEdges == maxIncidentEdges){
                verticesWithMaxIncidentEdges.add(i);
            }
        }
        if(verticesWithMaxIncidentEdges.size() == 1){ 
            return verticesWithMaxIncidentEdges.get(0);
        }
        else{ // two vertices have same # of edges
            return selectTiedNodes(verticesWithMaxIncidentEdges, incidentEdges);
        }

    }
    
    private int selectTiedNodes(ArrayList<Integer> tiedVertices, ArrayList<ArrayList<Integer>> incidentEdges){
        ArrayList<ArrayList<Integer>> uncoveredEdges = new ArrayList<>();
        for(int i=0;i<tiedVertices.size();i++){
            uncoveredEdges.add(new ArrayList<>(incidentEdges.get(tiedVertices.get(i))));
        }
        int maxNumberOfUncoveredEdges = 0;
        int indexOfMaxNumberOfUncoveredEdges = tiedVertices.get(0);
        for(int i=0;i<tiedVertices.size();i++){
            for(int j=i+1;j<tiedVertices.size();j++){
                Iterator<Integer> it_vertex1 = uncoveredEdges.get(i).iterator();
                Iterator<Integer> it_vertex2 = uncoveredEdges.get(j).iterator();
                while(it_vertex1.hasNext() && it_vertex2.hasNext()){
                    if(it_vertex1.next() == it_vertex2.next()){
                        it_vertex1.remove();
                        it_vertex2.remove();
                    }
                }
            }
            if(uncoveredEdges.get(i).size() > maxNumberOfUncoveredEdges){
                maxNumberOfUncoveredEdges = uncoveredEdges.get(i).size();
                indexOfMaxNumberOfUncoveredEdges = tiedVertices.get(i);
            }
        }
        return indexOfMaxNumberOfUncoveredEdges;
    }
    private void removeCoveredEdges(int vertex, ArrayList<int[]> edges){
        Iterator<int[]> it = edges.iterator();
        while(it.hasNext()){
            int[] edge = it.next();
            if(edge[0] == vertex || edge[1] == vertex){
                it.remove();
            }
        }
    }
    private int getLargestDegree(){
        int largestDegree = 0;
        for(ArrayList<Integer> neighbor: neighbors){
            largestDegree = Math.max(largestDegree, neighbor.size());
        }
        return largestDegree;
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
