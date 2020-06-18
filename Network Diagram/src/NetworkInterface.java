public interface NetworkInterface {

    abstract void status(); // prints summary of active and unactive nodes

    abstract void setActive(int v, boolean b); // sets activity of node v

    abstract boolean isActive(int v); // checks if node v is active or unactive

    abstract void changeEdge(int v, int w, double W); // modifies, deletes, or adds edge from v to w
}