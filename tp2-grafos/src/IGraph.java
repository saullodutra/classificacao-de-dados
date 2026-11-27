import java.util.List;

/**
 * Interface de grafo do modelo UML da disciplina.
 * Implementada por AdjListGraph (lista de adjacência) e AdjMatrixGraph (matriz de adjacência).
 *
 * Os vértices são numerados de 0 a numVertices() - 1.
 * Os algoritmos usam só estes métodos, então funcionam com as duas representações.
 */
public interface IGraph {

    GraphType type();

    List<Vertex> vertices();

    /** Todas as arestas, cada uma uma vez só (também no grafo não dirigido). */
    List<Edge> edges();

    /** Aresta não ponderada (peso padrão 1). */
    void addEdge(int u, int v);

    /** Aresta ponderada. */
    void addEdge(int u, int v, double weight);

    /** A aresta entre u e v, ou null se não existir. */
    Edge getEdge(int u, int v);

    /** Arestas incidentes a u (no dígrafo, as que saem de u). */
    List<Edge> adj(int u);

    int numVertices();

    int numEdges();
}
