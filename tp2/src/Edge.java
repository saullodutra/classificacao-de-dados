/**
 * Aresta do grafo (modelo UML): liga o vértice u ao vértice v, com um peso.
 * Sem peso informado, vale 1 (grafo não ponderado).
 * No dígrafo a aresta vai de u para v; no grafo não dirigido a ordem não importa.
 */
public class Edge {

    private final Vertex u;
    private final Vertex v;
    private final double weight;

    public Edge(Vertex u, Vertex v) {
        this(u, v, 1.0);
    }

    public Edge(Vertex u, Vertex v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public Vertex getU() {
        return u;
    }

    public Vertex getV() {
        return v;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "(" + u + ", " + v + ", " + GraphUtils.formatarPeso(weight) + ")";
    }
}
