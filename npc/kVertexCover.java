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
    public static void main(String[] args){
        String fileName;
        /* processing command line args */
        if(args.length == 0){
            fileName = "testGraphs.txt";
        }
        else{
            fileName = args[0];
        }

        kVertexCover cover = new kVertexCover();
        cover.readFile(fileName);
        cover.findAllVertexCovers();
    }
    
    public kVertexCover(){
        graphs = new ArrayList<Graph>();
    }

    public boolean readFile(String inputFileName){
        boolean fileFormatCorrect = true;
        Path path = Paths.get(inputFileName);
        try(BufferedReader reader = Files.newBufferedReader(path)){
            String line;
            while((line =reader.readLine()) != null){
                int numVertices = Integer.valueOf(line);
                Graph g = new Graph(numVertices);
                
                /* populating graph */
                for(int row=0;row<numVertices;row++){
                    line = reader.readLine();
                    int col = 0;
                    for(int i=0;i<line.length();i+=2){
                        if(line.charAt(i) == '1'){
                            g.addEdge(row, col);
                        }
                        col++;
                    }
                }
                graphs.add(g);
                System.out.print(g);
            }       
            
        }
        catch(IOException e){
            System.err.println("Error opening next file: " + inputFileName);
            fileFormatCorrect = false;
        }
        return fileFormatCorrect;
    }

    public void findAllVertexCovers(){
        for(int i=0;i<graphs.size();i++){
            long startTime = System.currentTimeMillis();
            ArrayList<Integer> vertexCover = findMinimumVertexCover(graphs.get(i));
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.format("G%d(%d, %d) (size=%d, ms=%d) {", i+1, graphs.get(i).getNumVertices(), graphs.get(i).getNumEdges(), vertexCover.size(), elapsedTime);
            printVertexCover(vertexCover);
        }
    }
    @SuppressWarnings("unchecked") // java casting from cloned obj to arraylist creating warning in compiler
    public ArrayList<Integer> findMinimumVertexCover(Graph g){
        boolean[] vertexIncludedInCover = new boolean[g.getNumVertices()];
        Arrays.fill(vertexIncludedInCover,false);
        ArrayList<Integer[]> remainingEdges = (ArrayList<Integer[]>) g.getEdges().clone();        
        ArrayList<Integer> vertexCover = new ArrayList<>(0);
        while(remainingEdges.size() != 0){
            Integer[] edge = remainingEdges.get(0);
        
            vertexCover.add(edge[0]);
            vertexCover.add(edge[1]);        
            /* removing any edges that are covered by the two vertices just added to the cover */
            for(int i=0;i<remainingEdges.size();i++){
                Integer[] possiblyCoveredEdge = remainingEdges.get(i);
                boolean edgeNowCovered = (edge[0] == possiblyCoveredEdge[0] || edge[0] == possiblyCoveredEdge[1] || edge[1] == possiblyCoveredEdge[0] || edge[1] == possiblyCoveredEdge[1]);
    
                if(edgeNowCovered){
                    /* adding both edges to the cover */
    
                    vertexIncludedInCover[possiblyCoveredEdge[0]] = true;
                    vertexIncludedInCover[possiblyCoveredEdge[1]] = true;
                    remainingEdges.remove(i);
                    i--; // staying at same index
                }
            }
        }

        for(int i=0;i<vertexIncludedInCover.length;i++){
            /* a vertex wasn't included in the cover, so a cover isn't possible */
            if(!vertexIncludedInCover[i]){
                vertexCover = new ArrayList<>();
            }
        }

        Collections.sort(vertexCover);
        return vertexCover;
    }

    private void printVertexCover(ArrayList<Integer> vertexCover){
        for(int j=0;j<vertexCover.size()-1;j++){
            System.out.print(vertexCover.get(j) +", ");
        }
        if(vertexCover.size() != 0){
            System.out.print(vertexCover.get(vertexCover.size()-1)); 
        }
        System.out.print("}\n");
    }
}
