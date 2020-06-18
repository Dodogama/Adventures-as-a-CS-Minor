import java.util.*;

public class DepthFirstPaths {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int END;
    private double MAX_W;
    private boolean[] marked;
    private Network G;
    int count;

    public DepthFirstPaths(Network G) {
        this.G = G;
        this.marked = new boolean[G.V()];
    }

    /**
     * Calls DFS to find all paths from source
     * @param v
     * @param w
     * @param W
     */
    public void allPaths(int v, int w, double W) {
        this.END = w;
        this.MAX_W = W;

        count = 0;
        List<NetworkEdge> path = new ArrayList<NetworkEdge>();

        System.out.println("Path(s) of weight less than " + W + " from " + v + " and " + w + ":");
        paths(v, 0, path);
        System.out.println(count + " paths found");

        /*
        // print results
        int n = paths.size();
        StringBuilder output = new StringBuilder();
        int i = 1; // current path
        for (List<NetworkEdge> p : paths) {
            output.append("Path " + i + ": ");
            System.out.println("Size of list: " + p.size());
            for (NetworkEdge e : p) {
                output.append(e + " ");
            }
            output.append(NEWLINE);
            i++;
        }
        StdOut.printf(output.toString());
        */
    }

    /**
     * Helper method using recursive DFS to find all paths from source
     * @param v
     * @param curW
     * @param curPath
     */
    public void paths(int v, double curW, List<NetworkEdge> curPath) {
        if (curW <= this.MAX_W) {
            if (v == this.END) {
                this.count++;
                StdOut.printf("Path %d (%.2f):\t", this.count, curW);
                for (NetworkEdge e : curPath) {
                    StdOut.print(e + "  ");
                }
                System.out.println();
                return; 
            }
            else {
                this.marked[v] = true;
                int w;
                for (NetworkEdge e : this.G.adj(v)) {
                    if (e.isActive()) {
                        w = e.other(v);
                        curPath.add(e);
                        if (!this.marked[w]) paths(w, curW + e.weight(), curPath); 
                        curPath.remove(e);
                    }
                }
                this.marked[v] = false; 
            }
        }
    }
}