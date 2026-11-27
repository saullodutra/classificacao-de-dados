import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafo representado por MATRIZ DE ADJACÊNCIA (MA), conforme o modelo UML.
 *
 * A posição matrix[u][v] guarda a aresta de u para v, ou null se ela não existe.
 *  - memória: O(V²), mesmo com poucas arestas
 *  - getEdge(u, v): O(1)
 *  - adj(u): O(V), porque precisa olhar a linha u inteira
 *
 * Guardar o objeto Edge (e não um número) resolve o problema do peso 0 dos arquivos:
 * "sem aresta" é null, e não um peso especial.
 * No grafo não dirigido a mesma aresta fica em matrix[u][v] e em matrix[v][u].
 */
public class AdjMatrixGraph implements IGraph {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Edge[][] matrix;
    private final GraphType type;

    public AdjMatrixGraph(int numVertices, GraphType type) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("numero de vertices negativo: " + numVertices);
        }
        this.type = type;
        this.vertices = new ArrayList<>(numVertices);
        this.edges = new ArrayList<>();
        this.matrix = new Edge[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i));
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
        if (matrix[u][v] != null) {
            return; // grafo simples: ignora aresta repetida
        }
        Edge e = new Edge(vertices.get(u), vertices.get(v), weight);
        edges.add(e);
        matrix[u][v] = e;
        if (type == GraphType.UNDIRECTED) {
            matrix[v][u] = e;
        }
    }

    @Override
    public Edge getEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        return matrix[u][v];
    }

    /** Percorre a linha u da matriz inteira: O(V). */
    @Override
    public List<Edge> adj(int u) {
        validateVertex(u);
        List<Edge> lista = new ArrayList<>();
        for (int v = 0; v < matrix.length; v++) {
            if (matrix[u][v] != null) {
                lista.add(matrix[u][v]);
            }
        }
        return lista;
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

    /** Mostra a matriz: o peso onde há aresta e "." onde não há. Só para grafos pequenos. */
    public String imprimirMatriz() {
        StringBuilder sb = new StringBuilder("      ");
        for (Vertex v : vertices) {
            sb.append(String.format("%5s", v.getLabel()));
        }
        sb.append('\n');
        for (int u = 0; u < matrix.length; u++) {
            sb.append(String.format("%5s:", vertices.get(u).getLabel()));
            for (int v = 0; v < matrix.length; v++) {
                sb.append(String.format("%5s", matrix[u][v] == null ? "." : GraphUtils.formatarPeso(matrix[u][v].getWeight())));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return GraphUtils.imprimir(this, 20);
    }
}
