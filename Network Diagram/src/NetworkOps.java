import java.util.*;

public class NetworkOps {
    private static Network N;
    private static Scanner scanner;

    /**
     * 1. Display current active network, show network status, show connected components 
     * 2. Display vertices and edges of MST 
     * 3. Display the shortest path
     * by latency from vertex i to j 
     * 4. Display all distinct paths from vertex i to j with weight <= x and total # of paths 
     * 5. Node i in graph will go down 
     * 6. Node i in graph will come back up 
     * 7. Change weight of edge (i, j) to x 
     * 8. Quit program
     */
    public static void displayMenu() {
        System.out.println("COMMANDS (enter letter): ");
        System.out.println("1. Report           \tR");
        System.out.println("2. MST              \tM");
        System.out.println("3. Shortest Path    \tS (i, j)");
        System.out.println("4. Distinct Paths   \tP (i, j, x)");
        System.out.println("5. Move Down        \tD (i)");
        System.out.println("6. Move Up          \tU (i)");
        System.out.println("7. Change Weight    \tC (i, j, x)");
        System.out.println("8. Quit             \tQ\n");
    }

    // 1. Reports (NOTE: ASSUMES SOURCE s = 0)
    public static void report() {
        System.out.println("Printing report of current Network:\n");
        N.status();
        CC cc = new CC(N);
        cc.components();
    }

    // 2. Uses Kruskal's algo to find MST(s)
    public static void MST() {
        CC cc = new CC(N);
        cc.forestMST();
    }

    /**
     * 3. Uses Dijkstra's algo to find shortest path
     * 
     * @param i starting vertex
     * @param j ending vertex
     */
    public static void shortPath() {
        System.out.println("Find shortest path between two vertices:\n");
        N.status();
        System.out.print("Enter first vertex: ");
        int i = scanner.nextInt();
        System.out.print("Enter second vertex: ");
        int j = scanner.nextInt();
        System.out.println();
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(N);
        DijkstraSP path = new DijkstraSP(G, i);
        path.SP(j);
        System.out.println();
    }

    /**
     * 4. Uses recursive backtracking to find all paths
     * 
     * @param i    starting vertex
     * @param j    ending vertex
     * @param maxW max cumulative path weight
     */
    public static void allPaths() {
        System.out.println("Find all distinct paths between two active vertices:\n");
        N.status();
        System.out.print("Enter first vertex: ");
        int i = scanner.nextInt();
        System.out.print("Enter second vertex: ");
        int j = scanner.nextInt();
        System.out.print("Enter max weight: ");
        int x = scanner.nextInt();
        System.out.println();
        DepthFirstPaths paths = new DepthFirstPaths(N);
        paths.allPaths(i, j, x);
        System.out.println();
    }

    /**
     * 5 & 6. Removes node from network and all connected edges
     * 
     * @param i node to toggle
     * @param b turn node on or off
     */
    private static void toggleNode(boolean b) {
        char c = 'Y';
        while (c == 'Y') {
            N.status();
            System.out.print((b == false) ? "Enter node to go down: " : "Enter node to go back up: ");
            int i = scanner.nextInt();
            N.setActive(i, b);
            System.out.print((b == false) ? "Do you want to deactivate another node? [Y/N]:  " 
                : "Do you want to reactivate another node? [Y/N]:  ");
            c = scanner.next().toUpperCase().charAt(0);
            System.out.println();
        }
    }

    /**
     * 7. Changes the weight of node and adds or removes node based on weight
     * 
     * @param i
     * @param j
     * @param x
     */
    private static void changeWeight() {
        char c = 'Y';
        while (c == 'Y') {
            N.status();
            System.out.print("Enter first vertex: ");
            int i = scanner.nextInt();
            System.out.print("Enter second vertex: ");
            int j = scanner.nextInt();
            System.out.print("Enter weight: ");
            double x = scanner.nextDouble();
            N.changeEdge(i, j, x);
            System.out.print("Do you want to change another node? [Y/N]:  ");
            c = scanner.next().toUpperCase().charAt(0);
            System.out.println();
        }
    }

    /**
     * @param args[0] textfile graph representation
     * @param args[1] method operation
     */
    public static void main(String[] args) {
        System.out.println("\nLoading graph from: " + args[0] + "\n");
        In in = new In(args[0]);
        N = new Network(in);
        scanner = new Scanner(System.in);
        char command;
        while (true) {
            displayMenu();
            System.out.print("Enter command:  ");
            command = scanner.next().toUpperCase().charAt(0);
            System.out.println();
            switch (command) {
            case 'R': // R
                report();
                break;
            case 'M': // M
                MST();
                break;
            case 'S': // S
                shortPath();
                break;
            case 'P': // P
                allPaths();
                break;
            case 'D': // D
                toggleNode(false);
                break;
            case 'U': // U
                toggleNode(true);
                break;
            case 'C': // C
                changeWeight();
                break;
            case 'Q': // Q
                scanner.close();
                System.exit(0);
                break;
            }
        }
    }
}