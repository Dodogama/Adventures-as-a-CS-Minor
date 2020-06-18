import java.util.*;

public class Network extends EdgeWeightedGraph implements NetworkInterface {
    private boolean[] active;

    public Network(In in) {
        super(in);
        this.active = new boolean[this.V()];
        Arrays.fill(this.active, true);
    }

    public void status() {
        List<Integer> activeN = new ArrayList<>();
        List<Integer> unactiveN = new ArrayList<>();
        for (int v = 0; v < this.V(); v++) {
            if (this.active[v])     activeN.add(v);
            else                    unactiveN.add(v);
        }
        System.out.println("The following nodes are active: ");
        if (!activeN.isEmpty()) {
            for (int i : activeN) System.out.print(i + "  ");
        }
        else System.out.print("none");
        System.out.println();
        System.out.println("\nThe following nodes are down: ");
        if (!unactiveN.isEmpty()) {
            for (int i : unactiveN) System.out.print(i + "  ");
        }
        else System.out.print("none");
        System.out.println("\n");
    }

    public void setActive(int v, boolean b) {
        if (this.active[v] == b) {
            System.out.println("Node " + v + ((b == true) ? " is already up" : " is already down"));
        }
        else {
            this.active[v] = b;
            for (NetworkEdge e : this.adj(v)) {
                e.setActive(b);
            }
            System.out.println("Node " + v + ((b == true) ? " is now active" : " is now unactive")); 
            System.out.println();
        }
    }

    public boolean isActive(int v) {
        return active[v];
    }

    public void changeEdge(int v, int w, double W) {
        boolean found = false;
        for (NetworkEdge e : adj(v)) {
            if (e.other(v) == w) {
                found = true;
                if (W >= 0) {
                    System.out.print("Edge " + e);
                    e.updateWeight(W);
                    System.out.println(" updated weight to: " + W + "\n");
                }
                else {
                    e.setActive(false);
                    System.out.println("Edge " + e + " turned off\n");
                }
            }
        }
        if (!found) {
            NetworkEdge e = new NetworkEdge(v, w, W);
            this.addEdge(e);
            System.out.println("New edge " + e + " added\n");
        }
    }
}