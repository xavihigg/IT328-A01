/**
 * Java program to represent a graph data structure.
 * 
 * Author: @tomfreier
 * Date: 9/23/22
 */
package npc;

import java.util.ArrayList;
import java.util.Arrays;

public class Graph{
    private boolean adjMatrix[][];
    private int inDegree[];
    private ArrayList<int[]> edges;
    private int numVertices, numEdges;
    
    // getters
    public int getNumVertices(){ return numVertices;}
    public int getNumEdges(){ return numEdges;}
    public boolean[][] getAdjMatrix(){ return adjMatrix;}
    public int[] getInDegree(){ return inDegree;}
    public ArrayList<int[]> getEdges() {return edges;}

    public Graph(int numVertices){
        this.numVertices = numVertices;
        this.numEdges = 0;
        this.adjMatrix = new boolean[numVertices][numVertices];
        this.inDegree = new int[numVertices]; 
        this.edges = new ArrayList<>();
        /* populating array with falses */
        for (boolean[]row : adjMatrix) {
            Arrays.fill(row, false);
        }
    }

    /**
     * Adds an edge to the graph and increments the numEdges
     * @param u Node u of the edge
     * @param v Node v of the edge
     */
    public void addEdge(int u, int v){
        /* same vertex so not incrementing number of edges */
        if(u==v){
            this.adjMatrix[u][u] = true;
        }

        if(u<v){
            this.adjMatrix[u][v] = true;
            this.adjMatrix[v][u] = true; // undirected graph
            this.inDegree[u]++;
            this.inDegree[v]++;
            this.numEdges++;
            edges.add(new int[]{u,v});
        }
    }

    @Override
    public String toString(){
        String retVal = Integer.toString(numVertices) + "\n";
        
        //adding edges
        for(int row=0;row<numVertices;row++){
            for(int col=0;col<numVertices;col++){
                if(adjMatrix[row][col]){
                    retVal += "1 ";
                }
                else{
                    retVal += "0 ";
                }
            }
            retVal += "\n";
        }
        return retVal;
    }

    
}