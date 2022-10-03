package npc;

import java.util.ArrayList;

public class VertexCover {
    private ArrayList<Integer> cover;
    private Graph g;
    private ArrayList<ArrayList<Integer>> adjList;

    public VertexCover(Graph g) {
        this.cover = new ArrayList<>();
        this.g = g;
        this.adjList = g.generateAdjList();
    }

    public ArrayList<Integer> getCover() {return cover;}

    /**
     * Fills the cover so all nodes are included in it
     */
    public void fillCover() {
        for (int i = 0; i < g.getNumVertices(); i++) {
            this.cover.add(i);
        }
    }

    public void removeVertex(int vertex){
        cover.remove(Integer.valueOf(vertex));
    }


    /**
     * Finds the minimal vertex cover of a given graph
     * 
     * @return Minimal vertex cover of the Graph.
     */
    public ArrayList<Integer> findMinimualertexCover() {
        boolean continueSearching = true;
        while (continueSearching) {
            int vertexToRemove = findBestVertexToRemove();
            continueSearching = (vertexToRemove > 0);
            if (continueSearching) { // there is a vertex to remove
                removeVertex(vertexToRemove);
            } 
        }
        return cover;
    }

    /**
     * Finds the best vertex that can be removed.
     * The 'best' criteria is the vertex with the lowest degree and all it's edges are covered by another node 
     * @return Vertex to remove or -1 if no vertexes can be removed
     */
    private int findBestVertexToRemove(){
        // HashMap<Integer, Integer> degrees = Graph.generateInDegrees(adjList);
        ArrayList<Integer> verticesWithMinDegrees = new ArrayList<>();
        int minDegree = Integer.MAX_VALUE;
        for(int i=0; i<adjList.size();i++){
            if(cover.contains(i) && vertexCanBeRemoved(i)){
                // int degree = degrees.get(i);
                int degree = adjList.get(i).size();
                /* new minimum found */
                if(degree < minDegree){ 
                    verticesWithMinDegrees.clear();
                    minDegree = degree;
                }

                /* has minDegree so adding it to the list */
                if(degree == minDegree){
                    verticesWithMinDegrees.add(i);
                }
            }
        }
        if(verticesWithMinDegrees.size() > 0){
            return verticesWithMinDegrees.get(0);
        }
        else{
            return -1;
        }
    }
    /**
     * Determines if every edge this vertex has is covered by another vertex in the
     * cover
     * 
     * @param vertex Vertex to determine if it can be removed from the cover
     * @return True if the vertex can be removed from the cover, false if we cannot
     *         remove it
     */
    private boolean vertexCanBeRemoved(int vertex) {
        boolean edgeCovered = true;
        ArrayList<Integer> adjEdges = this.adjList.get(vertex);
        for(int i=0;i<adjEdges.size();i++){
            int otherVertexInEdge = adjEdges.get(i);
            if(!cover.contains(otherVertexInEdge)){
                return false;
            }
        }
        return edgeCovered; // edges are covered by another vertex
    }

}