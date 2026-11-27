/**
 * Caminho mínimo de origem única pelo algoritmo de Bellman-Ford, como no Cormen (BELLMAN-FORD):
 *
 *   INITIALIZE-SINGLE-SOURCE(G, s)
 *   repita |V| - 1 vezes:
 *       para cada aresta (u, v): RELAX(u, v, w)
 *   para cada aresta (u, v):
 *       se dist[v] > dist[u] + w: existe ciclo de peso negativo alcançável a partir de s
 *
 * Aceita pesos negativos. Pressupõe dígrafo ponderado.
 *
 * As arestas são percorridas vértice a vértice, por adj(u), e não pela lista edges().
 * Assim o custo depende da representação, que é o que o experimento do TP2 compara:
 *   - lista de adjacência: O(V · E);
 *   - matriz de adjacência: O(V · V²) = O(V³), porque adj(u) olha a linha u inteira.
 *
 * Segue o Cormen sem parada antecipada: faz sempre as |V| - 1 passadas, mesmo se uma
 * passada não mudar nenhuma distância.
 */
public class BellmanFord extends CaminhoMinimo {

    private boolean cicloNegativo;

    public BellmanFord(IGraph grafo, int origem) {
        super(grafo, origem);
        executar();
    }

    @Override
    protected String nome() {
        return "Bellman-Ford";
    }

    private void executar() {
        inicializar();
        int n = grafo.numVertices();
        for (int i = 1; i <= n - 1; i++) {
            for (int u = 0; u < n; u++) {
                if (dist[u] == Double.POSITIVE_INFINITY) {
                    continue; // dist[u] + w continuaria infinito: RELAX não muda nada
                }
                for (Edge e : grafo.adj(u)) {
                    relaxar(u, GraphUtils.outroExtremo(e, u), e.getWeight());
                }
            }
        }
        // verificação de ciclo negativo
        for (int u = 0; u < n && !cicloNegativo; u++) {
            if (dist[u] == Double.POSITIVE_INFINITY) {
                continue;
            }
            for (Edge e : grafo.adj(u)) {
                if (dist[u] + e.getWeight() < dist[GraphUtils.outroExtremo(e, u)]) {
                    cicloNegativo = true;
                    break;
                }
            }
        }
    }

    /** true se há ciclo de peso negativo alcançável a partir da origem (o Cormen devolve FALSE). */
    public boolean temCicloNegativo() {
        return cicloNegativo;
    }

    @Override
    protected boolean distanciasValidas() {
        return !cicloNegativo;
    }

    @Override
    protected String observacoes() {
        return cicloNegativo
                ? "ATENCAO: ciclo de peso negativo alcancavel a partir da origem; as distancias nao valem.\n"
                : "Sem ciclo de peso negativo alcancavel a partir da origem.\n";
    }
}
