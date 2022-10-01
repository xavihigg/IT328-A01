package npc;

import java.util.ArrayList;
import java.util.Iterator;

public class VertexCover {
    private ArrayList<Integer> cover;
    private Graph g;
    private ArrayList<int[]> edges;
    public VertexCover(Graph g){
        this.cover = new ArrayList<>();
        this.g = g;
        edges = g.getEdges();
    }
    public VertexCover(VertexCover vc){
        this.cover = vc.cover;
        this.g = vc.g;
        edges = g.getEdges();
    }
    public ArrayList<Integer> getCover(){return cover;}
    public ArrayList<int[]> getEdges(){return edges;}
    public void addToCover(int vertex){
        cover.add(vertex);
        removeCoveredEdges(vertex);
    }

    public void fillCover(){
        for(int i=0;i<g.getNumEdges();i++){
            cover.add(i);
        }
    }
    /**
     * Finds the minimal vertex cover of a given graph
     * 
     * @return Minimal vertex cover of the Graph.
     */
    public ArrayList<Integer> findMinimualertexCover() {
        ArrayList<Integer> cover = new ArrayList<>();
        ArrayList<int[]> edges = g.getEdges();
        while(edges.size() != 0){ // while edges remain
            int vertexToAdd = selectNextVertexToAdd();
            cover.add(vertexToAdd);
            removeCoveredEdges(vertexToAdd);
        }
        return cover;
    }

    private ArrayList<ArrayList<Integer>> getIncidentEdges(){
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

    public int selectNextVertexToAdd(){
        ArrayList<ArrayList<Integer>> incidentEdges = getIncidentEdges();
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
        else{
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
    public void removeCoveredEdges(int vertex){
        Iterator<int[]> it = edges.iterator();
        while(it.hasNext()){
            int[] edge = it.next();
            if(edge[0] == vertex || edge[1] == vertex){
                it.remove();
            }
        }
    }


}