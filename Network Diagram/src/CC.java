public class CC {
    private static final String NEWLINE = System.getProperty("line.separator");

    private boolean[] marked;   // marked[v] = has vertex v been marked?
    private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[id] = number of vertices in given component
    private int count;          // number of connected components
    private Network G;

    /**
     * Computes the connected components of the edge-weighted graph {@code G}.
     *
     * @param G the edge-weighted graph
     */
    public CC(Network G) {
        this.G = G;
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v] && G.isActive(v)) { // no cc for unactive v
                dfs(G, v);
                count++; 
            }
        }
    }

    // depth-first search for an EdgeWeightedGraph
    private void dfs(Network G, int v) {
        marked[v] = true;
        id[v] = count;
        size[count]++;
        for (NetworkEdge e : G.adj(v)) {
            if (e.isActive()) { // no off edges
                int w = e.other(v);
                if (!marked[w]) {
                    dfs(G, w);
                }
            }
        }
    }

    /**
     * Returns the component id of the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the component id of the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int id(int v) {
        validateVertex(v);
        return id[v];
    }

    /**
     * Returns the number of vertices in the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the number of vertices in the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int size(int v) {
        validateVertex(v);
        return size[id[v]];
    }

    /**
     * Returns the number of connected components in the graph {@code G}.
     *
     * @return the number of connected components in the graph {@code G}
     */
    public int count() {
        return count;
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean connected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id(v) == id(w);
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     * @deprecated Replaced by {@link #connected(int, int)}.
     */
    @Deprecated
    public boolean areConnected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id(v) == id(w);
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    // NEW
    public void components() {
        int m = this.count(); // number of components

        StdOut.println((m == 1) ? "Network is connected:\n" : "Network is diconnected: " + m + " connected components\n");

        // compute list of vertices in each connected component
        Queue<Integer>[] components = new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < this.G.V(); v++) {
            if (G.isActive(v)) components[this.id(v)].enqueue(v); // redundant since not connected to pruned sources
        }

        // print results
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < m; i++) { // components
            s.append("Component " + (i + 1) + ":" + NEWLINE);
            for (int v : components[i]) { // vertices
                s.append("Vertex " + v + ": ");
                for (NetworkEdge e : G.adj(v)) { // edges
                    if (e.isActive()) s.append(e + " "); // only active edges in adj list
                }
                s.append(NEWLINE);
            }
            s.append(NEWLINE);
        }
        StdOut.print(s);
    }

    /**
     * 
     */
    public void forestMST() {
        int m = this.count();
        int[] sources = new int[m];

        // compute list of vertices in each connected component
        Queue<Integer>[] components = new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < this.G.V(); v++) {
            if (G.isActive(v)) components[this.id(v)].enqueue(v); // redundant since not connected to pruned sources
        }
        // add sources (first vertex in each cc) to array 
        for (int i = 0; i < m; i++) {
            sources[i] = components[i].peek();
        }

        // use PrimMST on each source
        System.out.println((m > 1) ? "Printing " + m + " trees in this forest:\n" : "Printing MST:\n");
        PrimMST mst;
        for (int i = 0; i < sources.length; i++) {
            mst = new PrimMST(this.G, sources[i]);
            System.out.println("Tree " + (i + 1) + ":");
            mst.MST();
            System.out.println();
        }
    }
}