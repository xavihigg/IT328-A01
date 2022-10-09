package npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CNF {
    private ArrayList<Graph> graphs;
    ArrayList<ArrayList<Integer>> neighbors;

    public static void main(String[] args) {
        String fileName;
        /* processing command line args */
        if (args.length == 0) {
            fileName = "cnfs2022.txt";
        } else {
            fileName = args[0];
        }
        CNF threeCNF = new CNF();
        threeCNF.readFile(fileName);
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
                line = line.trim();
                String[] cnf = line.split("\\s");
                int[] cnf3 = new int[cnf.length];
                
                int numberOfVariables = 0;
                for (int i = 0; i < cnf.length; i++) {
                    int num = Integer.parseInt(cnf[i]);
                    cnf3[i] = num;
                    if (Math.abs(num) > numberOfVariables) {
                        numberOfVariables = num;
                    }
                }
                // Create my list of descriptive nodes
                ArrayList<Node> nodes = new ArrayList<Node>();
                int numVertices = 2*numberOfVariables + cnf3.length;
                Graph graph = new Graph(numVertices);

                //Adding gadgets
                int label = 1;
                for (int row = 0; row < 2*numberOfVariables; row++) {
                    if (row % 2 == 0) {
                        graph.addEdge(row, row+1);
                        Node node = new Node(0, false, false, "a" + label);
                        nodes.add(node);
                    } else {
                        graph.addEdge(row - 1, row);
                        Node node = new Node(0, false, true, "-a" + label);
                        nodes.add(node);
                        label++;
                    }
                    
                }
                for (int j = 0; j < cnf3.length; j++) {
                    boolean sign;
                    if (cnf3[j] < 0) {
                        sign = false;
                        nodes.add(new Node(0, true, sign, "-a" + Math.abs(cnf3[j])));
                    } else {
                        sign = true;
                        nodes.add(new Node(0, true, sign, "a" + Math.abs(cnf3[j])));
                    }
                    
                }
                for (int i =0; i < nodes.size(); i++) {
                    System.out.println(nodes.get(i).name);
                }
                
                //Adding clauses
                int count = 1;
                for(int row = 2*numberOfVariables; row < numVertices; row++) {
                    if(count == 1) {
                        graph.addEdge(row, row+1);
                        graph.addEdge(row, row+2);
                    } else if (count == 2) {
                        graph.addEdge(row, row+1);
                        graph.addEdge(row, row-1);
                    } else if (count == 3) {
                        graph.addEdge(row, row-1);
                        graph.addEdge(row, row-2);
                    }
                    count++;
                    if(count > 3) {
                        count = 1;
                    }
                }
                
                //Add the edges between the verticies and the gadgets
                for(int row = 0; row < numVertices; row++) {
                    for(int column = 0; column < numVertices;column++) {
                        if(nodes.get(row).name.equals(nodes.get(column).name) && nodes.get(row).clause != nodes.get(column).clause) {
                            graph.addEdge(row, column);
                        }
                    }
                }

                int k = this.getK(cnf3.length / 3, numberOfVariables);
                VertexCover cover = new VertexCover(graph);
                findVCover findCover = new findVCover();
                findCover.findKvertexCover(cover, k);

                

                
                System.out.println(graph.toString());

                
            }

        } catch (IOException e) {
            System.err.println("Error opening next file: " + inputFileName);
            fileFormatCorrect = false;
        }
        return fileFormatCorrect;
    }

    public int getK(int numberOfClauses, int numberOfVariables) {
        return numberOfVariables + (2 * numberOfClauses);
    }
}
