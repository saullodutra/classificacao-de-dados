import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafo representado por LISTA DE ADJACÊNCIA (LA), conforme o modelo UML.
 *
 * Cada vértice u tem uma lista com as arestas incidentes a ele.
 *  - memória: O(V + E)
 *  - percorrer as arestas de u: O(grau(u))
 *  - getEdge(u, v): O(grau(u)), porque percorre a lista de u
 *
 * No grafo não dirigido o mesmo objeto Edge entra na lista de u e na de v.
 * O grafo é simples: uma segunda aresta entre o mesmo par é ignorada
 * (o GraphReader trata os arcos repetidos dos arquivos antes, ficando com o menor peso).
 */
public class AdjListGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adj;
    private final GraphType type;

    public AdjListGraph(int numVertices, GraphType type) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("numero de vertices negativo: " + numVertices);
        }
        this.type = type;
        this.vertices = new ArrayList<>(numVertices);
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
            adj.add(new ArrayList<>());
        }
    }

    @Override
    public GraphType type() {
        return type;
    }

    @Override
    public List<Vertex> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    @Override
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public void addEdge(int u, int v) {
        addEdge(u, v, 1.0);
    }

    @Override
    public void addEdge(int u, int v, double weight) {
        validateVertex(u);
        validateVertex(v);
        if (getEdge(u, v) != null) {
            return; // grafo simples: ignora aresta repetida
        }
        Edge e = new Edge(vertices.get(u), vertices.get(v), weight);
        edges.add(e);
        adj.get(u).add(e);
        if (type == GraphType.UNDIRECTED && u != v) {
            adj.get(v).add(e);
        }
    }

    @Override
    public Edge getEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        for (Edge e : adj.get(u)) {
            if (GraphUtils.outroExtremo(e, u) == v) {
                return e;
            }
        }
        return null;
    }

    @Override
    public List<Edge> adj(int u) {
        validateVertex(u);
        return Collections.unmodifiableList(adj.get(u));
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numEdges() {
        return edges.size();
    }

    private void validateVertex(int u) {
        if (u < 0 || u >= numVertices()) {
            throw new IndexOutOfBoundsException("vertice " + u + " fora do intervalo 0.." + (numVertices() - 1));
        }
    }

    @Override
    public String toString() {
        return GraphUtils.imprimir(this, 20);
    }
}
