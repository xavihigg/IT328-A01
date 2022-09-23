package npc.program1;
import npc.Graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class kVertexCover {
    public static void main(String[] args){
        readFile("testGraphs.txt");
    }
    
    public static boolean readFile(String inputFileName){
        boolean fileFormatCorrect = true;
        try(BufferedReader reader = Files.newBufferedReader(Path.of(inputFileName))){
            String line;
            while((line =reader.readLine()) != null){
                int numVertices = Integer.valueOf(line);
                Graph g = new Graph(numVertices);
                boolean[][] adjMatrix = g.getAdjMatrix();
                
                /* populating graph */
                for(int row=0;row<numVertices;row++){
                    line = reader.readLine();
                    int col = 0;
                    for(int i=0;i<line.length();i+=2){
                        adjMatrix[row][col] = (line.charAt(i) == '1');
                        col++;
                    }
                }

                System.out.println(g);
            }       
            
        }
        catch(IOException e){
            System.err.println("Error opening next file: " + inputFileName);
            fileFormatCorrect = false;
        }
        return fileFormatCorrect;
    }


}
