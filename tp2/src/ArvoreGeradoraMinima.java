import java.util.ArrayList;
import java.util.List;

/**
 * Parte comum de Kruskal e Prim: guarda as arestas escolhidas para a árvore geradora mínima
 * (AGM), o peso total e monta o relatório.
 *
 * AGM só faz sentido em grafo NÃO DIRIGIDO e ponderado. Se o grafo não for conexo, não existe
 * árvore geradora: Kruskal devolve uma floresta geradora mínima (uma árvore por componente) e
 * Prim devolve a árvore só do componente da raiz. O relatório avisa quando isso acontece.
 */
public abstract class ArvoreGeradoraMinima {

    protected final IGraph grafo;
    protected final List<Edge> arestas = new ArrayList<>();
    protected double pesoTotal;

    protected ArvoreGeradoraMinima(IGraph grafo) {
        if (grafo.type() != GraphType.UNDIRECTED) {
            throw new IllegalArgumentException("arvore geradora minima exige grafo nao dirigido (UNDIRECTED)");
        }
        this.grafo = grafo;
    }

    /** Nome do algoritmo, para o relatório. */
    protected abstract String nome();

    /** Arestas da árvore, na ordem em que o algoritmo as escolheu. */
    public List<Edge> getArestas() {
        return arestas;
    }

    public double getPesoTotal() {
        return pesoTotal;
    }

    /** true se a árvore liga todos os vértices (|V| - 1 arestas). */
    public boolean geradora() {
        return arestas.size() == Math.max(grafo.numVertices() - 1, 0);
    }

    protected void adicionar(Edge e) {
        arestas.add(e);
        pesoTotal += e.getWeight();
    }

    /** Relatório com no máximo maxArestas arestas listadas (para os grafos grandes). */
    public String relatorio(int maxArestas) {
        StringBuilder sb = new StringBuilder();
        sb.append(nome()).append(": arestas escolhidas, na ordem\n");
        int mostrar = Math.min(arestas.size(), maxArestas);
        for (int i = 0; i < mostrar; i++) {
            Edge e = arestas.get(i);
            sb.append(String.format("  %s - %s  (peso %s)%n", e.getU(), e.getV(),
                    GraphUtils.formatarPeso(e.getWeight())));
        }
        if (arestas.size() > mostrar) {
            sb.append("  ... (mais ").append(arestas.size() - mostrar).append(" arestas)\n");
        }
        sb.append(String.format("Arestas na arvore: %d (|V| - 1 = %d)%n", arestas.size(),
                Math.max(grafo.numVertices() - 1, 0)));
        if (!geradora()) {
            sb.append("Atencao: o grafo nao e conexo; o resultado nao cobre todos os vertices.\n");
        }
        sb.append("Peso total: ").append(GraphUtils.formatarPeso(pesoTotal)).append('\n');
        return sb.toString();
    }

    public String relatorio() {
        return relatorio(Integer.MAX_VALUE);
    }
}
