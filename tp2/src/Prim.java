import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Árvore Geradora Mínima pelo algoritmo de Prim, como no Cormen (MST-PRIM):
 *
 *   - chave[v]: menor peso de uma aresta que liga v à árvore já construída (inicialmente infinito);
 *   - pred[v]:  o vértice da árvore do outro lado dessa aresta;
 *   - começa pela raiz (chave 0) e, a cada passo, tira da fila de prioridade mínima o vértice u
 *     de menor chave, coloca a aresta (pred[u], u) na árvore e atualiza a chave dos vizinhos de u.
 *
 * A fila de prioridade é um heap binário (PriorityQueue). Como ela não tem DECREASE-KEY,
 * quando a chave de v diminui o par (chave, v) é inserido de novo e as entradas antigas são
 * descartadas ao sair da fila (já estão na árvore).
 *
 * Complexidade: O(E log V) com lista de adjacência; com matriz, adj(u) custa O(V),
 * então soma O(V²) para percorrer as vizinhanças.
 */
public class Prim extends ArvoreGeradoraMinima {

    private final int raiz;
    private final double[] chave;
    private final int[] pred;
    private final boolean[] naArvore;
    private final Edge[] arestaPred; // a aresta (pred[v], v), para não precisar de getEdge

    public Prim(IGraph grafo, int raiz) {
        super(grafo);
        this.raiz = raiz;
        int n = grafo.numVertices();
        chave = new double[n];
        pred = new int[n];
        naArvore = new boolean[n];
        arestaPred = new Edge[n];
        if (n > 0) {
            executar();
        }
    }

    public Prim(IGraph grafo) {
        this(grafo, 0);
    }

    @Override
    protected String nome() {
        return "Prim (raiz " + grafo.vertices().get(raiz).getLabel() + ")";
    }

    private void executar() {
        Arrays.fill(chave, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        chave[raiz] = 0;
        // cada entrada é {chave, vértice}
        PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
        fila.add(new double[] {0, raiz});
        while (!fila.isEmpty()) {
            int u = (int) fila.poll()[1];
            if (naArvore[u]) {
                continue; // entrada antiga, com chave maior
            }
            naArvore[u] = true;
            if (pred[u] != -1) {
                adicionar(arestaPred[u]);
            }
            for (Edge e : grafo.adj(u)) {
                int v = GraphUtils.outroExtremo(e, u);
                if (!naArvore[v] && e.getWeight() < chave[v]) {
                    chave[v] = e.getWeight();
                    pred[v] = u;
                    arestaPred[v] = e;
                    fila.add(new double[] {chave[v], v});
                }
            }
        }
    }

    public int[] getPred() {
        return pred;
    }
}
