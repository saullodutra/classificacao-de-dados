import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordenação Topológica, como no Cormen (TOPOLOGICAL-SORT):
 *
 *   1. roda a busca em profundidade para calcular o tempo de término f[v] de cada vértice;
 *   2. ao terminar um vértice, coloca-o na FRENTE de uma lista;
 *   3. a lista final (ordem decrescente de f) é a ordenação topológica.
 *
 * Na ordem obtida, toda aresta (u, v) tem u antes de v.
 * Só existe ordenação topológica em dígrafo acíclico (DAG): se a DFS encontrar uma aresta de
 * retorno (ciclo), o resultado é "não é DAG" e a ordem não vale.
 *
 * Complexidade: O(V + E) com lista de adjacência; O(V²) com matriz (é o custo da DFS).
 */
public class OrdenacaoTopologica {

    private final IGraph grafo;
    private final List<Integer> ordem;
    private final boolean dag;
    private final BuscaEmProfundidade dfs;

    public OrdenacaoTopologica(IGraph grafo) {
        if (grafo.type() != GraphType.DIRECTED) {
            throw new IllegalArgumentException("ordenacao topologica exige um digrafo (DIRECTED)");
        }
        this.grafo = grafo;
        this.dfs = new BuscaEmProfundidade(grafo);
        this.dag = !dfs.temCiclo();
        // ordem de término invertida = inserir cada vértice terminado na frente da lista
        List<Integer> lista = new ArrayList<>(dfs.getOrdemTermino());
        Collections.reverse(lista);
        this.ordem = lista;
    }

    /** true se o dígrafo é acíclico (a ordenação existe). */
    public boolean isDag() {
        return dag;
    }

    /** A ordenação topológica (só faz sentido se isDag()). */
    public List<Integer> getOrdem() {
        return ordem;
    }

    /** Confere a ordem: toda aresta (u, v) precisa ter u antes de v. */
    public boolean ordemValida() {
        int[] posicao = new int[grafo.numVertices()];
        for (int i = 0; i < ordem.size(); i++) {
            posicao[ordem.get(i)] = i;
        }
        for (Edge e : grafo.edges()) {
            if (posicao[e.getU().getId()] >= posicao[e.getV().getId()]) {
                return false;
            }
        }
        return true;
    }

    private String rotulo(int v) {
        return grafo.vertices().get(v).getLabel();
    }

    public String relatorio() {
        StringBuilder sb = new StringBuilder();
        if (!dag) {
            sb.append("O digrafo tem ciclo: NAO existe ordenacao topologica.\n");
            return sb.toString();
        }
        int[] f = dfs.getF();
        sb.append("Ordenacao topologica (ordem decrescente do tempo de termino f):\n  ");
        for (int i = 0; i < ordem.size(); i++) {
            int v = ordem.get(i);
            sb.append(i == 0 ? "" : " -> ").append(rotulo(v)).append(" (f=").append(f[v]).append(')');
        }
        sb.append('\n');
        sb.append("Toda aresta (u, v) com u antes de v: ").append(ordemValida() ? "sim" : "NAO").append('\n');
        return sb.toString();
    }
}
