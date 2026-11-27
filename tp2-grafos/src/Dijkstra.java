import java.util.PriorityQueue;

/**
 * Caminho mínimo de origem única pelo algoritmo de Dijkstra, como no Cormen (DIJKSTRA):
 *
 *   INITIALIZE-SINGLE-SOURCE(G, s)
 *   S = vazio; Q = fila de prioridade mínima com todos os vértices (chave = dist)
 *   enquanto Q não estiver vazia:
 *       u = EXTRACT-MIN(Q); S = S ∪ {u}
 *       para cada vizinho v de u: RELAX(u, v, w)
 *
 * Exige pesos NÃO NEGATIVOS (o construtor recusa o grafo se houver peso negativo).
 * Peso 0, que aparece nos arquivos do professor, é permitido.
 *
 * A fila de prioridade é um heap binário (PriorityQueue). Como ela não tem DECREASE-KEY,
 * quando dist[v] diminui o par (dist, v) é inserido de novo e as entradas antigas são
 * descartadas ao sair da fila (o vértice já está em S).
 *
 * Complexidade: O((V + E) log V) com lista de adjacência; com matriz, adj(u) custa O(V),
 * então soma O(V²) para percorrer as vizinhanças.
 */
public class Dijkstra extends CaminhoMinimo {

    private final boolean[] finalizado; // o conjunto S do Cormen

    public Dijkstra(IGraph grafo, int origem) {
        super(grafo, origem);
        for (Edge e : grafo.edges()) {
            if (e.getWeight() < 0) {
                throw new IllegalArgumentException("Dijkstra nao aceita peso negativo: aresta " + e);
            }
        }
        finalizado = new boolean[grafo.numVertices()];
        executar();
    }

    @Override
    protected String nome() {
        return "Dijkstra";
    }

    private void executar() {
        inicializar();
        // cada entrada é {dist, vértice}
        PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
        fila.add(new double[] {0, origem});
        while (!fila.isEmpty()) {
            int u = (int) fila.poll()[1];
            if (finalizado[u]) {
                continue; // entrada antiga, com distância maior
            }
            finalizado[u] = true;
            for (Edge e : grafo.adj(u)) {
                int v = GraphUtils.outroExtremo(e, u);
                if (!finalizado[v] && relaxar(u, v, e.getWeight())) {
                    fila.add(new double[] {dist[v], v});
                }
            }
        }
    }
}
