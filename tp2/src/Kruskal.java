import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Árvore Geradora Mínima pelo algoritmo de Kruskal, como no Cormen (MST-KRUSKAL):
 *
 *   1. cada vértice começa no seu próprio conjunto (MAKE-SET);
 *   2. ordena as arestas por peso crescente;
 *   3. para cada aresta (u, v), nessa ordem: se u e v estão em conjuntos diferentes
 *      (FIND-SET(u) != FIND-SET(v)), a aresta entra na árvore e os conjuntos são unidos (UNION).
 *
 * Os conjuntos disjuntos (union-find) usam união por posto (rank) e compressão de caminho.
 *
 * Kruskal trabalha sobre a LISTA de arestas do grafo (edges()), e não sobre adj(u); por isso
 * o custo é praticamente o mesmo em LA e MA: O(E log E) da ordenação.
 */
public class Kruskal extends ArvoreGeradoraMinima {

    private final int[] pai;
    private final int[] posto;

    public Kruskal(IGraph grafo) {
        super(grafo);
        int n = grafo.numVertices();
        pai = new int[n];
        posto = new int[n];
        executar();
    }

    @Override
    protected String nome() {
        return "Kruskal";
    }

    private void executar() {
        for (int v = 0; v < pai.length; v++) {
            pai[v] = v; // MAKE-SET
        }
        List<Edge> ordenadas = new ArrayList<>(grafo.edges());
        ordenadas.sort(Comparator.comparingDouble(Edge::getWeight));
        for (Edge e : ordenadas) {
            int ru = encontrar(e.getU().getId());
            int rv = encontrar(e.getV().getId());
            if (ru != rv) {
                adicionar(e);
                unir(ru, rv);
                if (arestas.size() == pai.length - 1) {
                    break; // a árvore já tem |V| - 1 arestas
                }
            }
        }
    }

    /** FIND-SET com compressão de caminho. */
    private int encontrar(int x) {
        while (pai[x] != x) {
            pai[x] = pai[pai[x]];
            x = pai[x];
        }
        return x;
    }

    /** UNION por posto: a raiz de menor posto passa a apontar para a de maior posto. */
    private void unir(int ru, int rv) {
        if (posto[ru] < posto[rv]) {
            pai[ru] = rv;
        } else if (posto[ru] > posto[rv]) {
            pai[rv] = ru;
        } else {
            pai[rv] = ru;
            posto[ru]++;
        }
    }
}
