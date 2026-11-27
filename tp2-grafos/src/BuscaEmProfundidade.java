import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Busca em Profundidade (DFS), como no Cormen.
 *
 * Vai o mais fundo possível em cada caminho antes de voltar (recursão).
 * Percorre o grafo inteiro: quando termina a partir de um vértice, recomeça no próximo
 * vértice ainda não visitado, formando uma floresta de busca.
 *
 * Resultado:
 *  - d[v]: tempo de descoberta (v fica cinza);
 *  - f[v]: tempo de término (v fica preto);
 *  - pred[v]: pai de v na floresta;
 *  - ordem de término (base da ordenação topológica);
 *  - se o grafo tem ciclo (encontrou aresta de retorno).
 *
 * Complexidade: O(V + E) com lista de adjacência; O(V²) com matriz.
 */
public class BuscaEmProfundidade {

    private static final int BRANCO = 0, CINZA = 1, PRETO = 2;

    private final IGraph grafo;
    private final int[] cor;
    private final int[] d;
    private final int[] f;
    private final int[] pred;
    private final List<Integer> ordemDescoberta = new ArrayList<>();
    private final List<Integer> ordemTermino = new ArrayList<>();
    private int tempo;
    private boolean temCiclo;

    public BuscaEmProfundidade(IGraph grafo) {
        this.grafo = grafo;
        int n = grafo.numVertices();
        cor = new int[n];
        d = new int[n];
        f = new int[n];
        pred = new int[n];
        executar();
    }

    private void executar() {
        Arrays.fill(cor, BRANCO);
        Arrays.fill(pred, -1);
        tempo = 0;
        for (int u = 0; u < grafo.numVertices(); u++) {
            if (cor[u] == BRANCO) {
                visitar(u);
            }
        }
    }

    private void visitar(int u) {
        tempo++;
        d[u] = tempo;
        cor[u] = CINZA;
        ordemDescoberta.add(u);
        for (Edge e : grafo.adj(u)) {
            int v = GraphUtils.outroExtremo(e, u);
            if (cor[v] == BRANCO) {
                pred[v] = u;
                visitar(v);
            } else if (cor[v] == CINZA && (grafo.type() == GraphType.DIRECTED || v != pred[u])) {
                temCiclo = true; // aresta de retorno
            }
        }
        cor[u] = PRETO;
        tempo++;
        f[u] = tempo;
        ordemTermino.add(u);
    }

    public int[] getD() {
        return d;
    }

    public int[] getF() {
        return f;
    }

    public int[] getPred() {
        return pred;
    }

    public List<Integer> getOrdemTermino() {
        return ordemTermino;
    }

    public boolean temCiclo() {
        return temCiclo;
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
        sb.append("Ordem de descoberta: ").append(rotulos(ordemDescoberta)).append('\n');
        sb.append("Ordem de termino:    ").append(rotulos(ordemTermino)).append('\n');
        sb.append("Tem ciclo: ").append(temCiclo ? "sim" : "nao").append('\n');
        sb.append(" vertice    d/f    pred\n");
        for (int v = 0; v < d.length; v++) {
            sb.append(String.format("%8s  %3d/%-3d  %4s%n", rotulo(v), d[v], f[v],
                    pred[v] == -1 ? "-" : rotulo(pred[v])));
        }
        return sb.toString();
    }
}
