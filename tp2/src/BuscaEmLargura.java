import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * Busca em Largura (BFS), como no Cormen.
 *
 * A partir da origem s, visita os vértices em "camadas" usando uma FILA:
 * primeiro os vizinhos de s, depois os vizinhos dos vizinhos, e assim por diante.
 * Funciona em grafo não dirigido ou dígrafo; o peso das arestas é ignorado.
 *
 * Resultado:
 *  - dist[v]: número de arestas no menor caminho de s até v (-1 se não alcança);
 *  - pred[v]: vértice anterior a v nesse caminho (-1 se não tem);
 *  - ordem em que os vértices foram descobertos.
 *
 * Complexidade: O(V + E) com lista de adjacência; O(V²) com matriz (adj(u) custa O(V)).
 */
public class BuscaEmLargura {

    private final IGraph grafo;
    private final int origem;
    private final int[] dist;
    private final int[] pred;
    private final List<Integer> ordem = new ArrayList<>();

    public BuscaEmLargura(IGraph grafo, int origem) {
        this.grafo = grafo;
        this.origem = origem;
        int n = grafo.numVertices();
        dist = new int[n];
        pred = new int[n];
        executar();
    }

    private void executar() {
        Arrays.fill(dist, -1); // -1 = ainda não descoberto ("branco")
        Arrays.fill(pred, -1);
        Queue<Integer> fila = new ArrayDeque<>();
        dist[origem] = 0;
        ordem.add(origem);
        fila.add(origem);
        while (!fila.isEmpty()) {
            int u = fila.remove();
            for (Edge e : grafo.adj(u)) {
                int v = GraphUtils.outroExtremo(e, u);
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    pred[v] = u;
                    ordem.add(v);
                    fila.add(v);
                }
            }
        }
    }

    /** Caminho da origem até v, ou lista vazia se v não é alcançável. */
    public List<Integer> caminhoAte(int v) {
        List<Integer> caminho = new ArrayList<>();
        if (dist[v] == -1) {
            return caminho;
        }
        for (int x = v; x != -1; x = pred[x]) {
            caminho.add(0, x);
        }
        return caminho;
    }

    public int[] getDist() {
        return dist;
    }

    public int[] getPred() {
        return pred;
    }

    public int alcancados() {
        return ordem.size();
    }

    private String rotulo(int v) {
        return grafo.vertices().get(v).getLabel();
    }

    private String rotulos(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int id : ids) {
            sb.append(sb.length() == 0 ? "" : " ").append(rotulo(id));
        }
        return sb.toString();
    }

    public String relatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("Origem: ").append(rotulo(origem)).append('\n');
        sb.append("Ordem de descoberta: ").append(rotulos(ordem)).append('\n');
        sb.append(" vertice  dist  pred  caminho\n");
        for (int v = 0; v < dist.length; v++) {
            sb.append(String.format("%8s  %4s  %4s  %s%n", rotulo(v),
                    dist[v] == -1 ? "inf" : String.valueOf(dist[v]),
                    pred[v] == -1 ? "-" : rotulo(pred[v]),
                    dist[v] == -1 ? "(nao alcancavel)" : rotulos(caminhoAte(v))));
        }
        return sb.toString();
    }
}
