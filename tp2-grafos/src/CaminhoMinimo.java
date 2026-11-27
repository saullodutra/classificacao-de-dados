import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parte comum dos algoritmos de caminho mínimo de ORIGEM ÚNICA (Bellman-Ford e Dijkstra),
 * com as rotinas do Cormen:
 *
 *   INITIALIZE-SINGLE-SOURCE: dist[v] = infinito e pred[v] = nil para todo v; dist[s] = 0.
 *   RELAX(u, v, w): se dist[u] + w < dist[v], então dist[v] = dist[u] + w e pred[v] = u.
 *
 * Também monta o caminho da origem até cada vértice e o relatório.
 */
public abstract class CaminhoMinimo {

    protected final IGraph grafo;
    protected final int origem;
    protected final double[] dist;
    protected final int[] pred;

    protected CaminhoMinimo(IGraph grafo, int origem) {
        if (origem < 0 || origem >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException("origem " + origem + " fora do intervalo");
        }
        this.grafo = grafo;
        this.origem = origem;
        int n = grafo.numVertices();
        dist = new double[n];
        pred = new int[n];
    }

    /** Nome do algoritmo, para o relatório. */
    protected abstract String nome();

    protected void inicializar() {
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        dist[origem] = 0;
    }

    /** RELAX: devolve true se a distância de v diminuiu. */
    protected boolean relaxar(int u, int v, double w) {
        if (dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            pred[v] = u;
            return true;
        }
        return false;
    }

    public double[] getDist() {
        return dist;
    }

    public int[] getPred() {
        return pred;
    }

    public int getOrigem() {
        return origem;
    }

    public boolean alcancavel(int v) {
        return dist[v] != Double.POSITIVE_INFINITY;
    }

    /** Caminho mínimo da origem até v, ou lista vazia se v não é alcançável. */
    public List<Integer> caminhoAte(int v) {
        List<Integer> caminho = new ArrayList<>();
        if (!alcancavel(v)) {
            return caminho;
        }
        // o limite de |V| passos evita laço infinito se houver ciclo negativo
        for (int x = v, passos = 0; x != -1 && passos <= dist.length; x = pred[x], passos++) {
            caminho.add(0, x);
        }
        return caminho;
    }

    /** Quantos vértices são alcançáveis a partir da origem (incluindo ela). */
    public int alcancados() {
        int c = 0;
        for (double d : dist) {
            if (d != Double.POSITIVE_INFINITY) {
                c++;
            }
        }
        return c;
    }

    /** Maior distância finita encontrada. */
    public double maiorDistancia() {
        double max = 0;
        for (double d : dist) {
            if (d != Double.POSITIVE_INFINITY && d > max) {
                max = d;
            }
        }
        return max;
    }

    protected String rotulo(int v) {
        return grafo.vertices().get(v).getLabel();
    }

    /** false quando as distâncias não valem (ciclo negativo no Bellman-Ford). */
    protected boolean distanciasValidas() {
        return true;
    }

    /** Linhas extras do relatório (ex.: ciclo negativo no Bellman-Ford). */
    protected String observacoes() {
        return "";
    }

    /** Relatório com no máximo maxVertices vértices listados (para os grafos grandes). */
    public String relatorio(int maxVertices) {
        StringBuilder sb = new StringBuilder();
        sb.append(nome()).append(" | origem: ").append(rotulo(origem)).append('\n');
        sb.append(observacoes());
        sb.append(" vertice      dist  pred  caminho\n");
        int limite = Math.min(dist.length, maxVertices);
        for (int v = 0; v < limite; v++) {
            String caminho;
            if (!alcancavel(v)) {
                caminho = "(nao alcancavel)";
            } else if (!distanciasValidas()) {
                caminho = "(indefinido: ciclo negativo)";
            } else {
                StringBuilder c = new StringBuilder();
                for (int x : caminhoAte(v)) {
                    c.append(c.length() == 0 ? "" : " -> ").append(rotulo(x));
                }
                caminho = c.toString();
            }
            sb.append(String.format("%8s  %8s  %4s  %s%n", rotulo(v),
                    alcancavel(v) ? GraphUtils.formatarPeso(dist[v]) : "inf",
                    pred[v] == -1 ? "-" : rotulo(pred[v]), caminho));
        }
        if (limite < dist.length) {
            sb.append("  ... (mais ").append(dist.length - limite).append(" vertices)\n");
        }
        return sb.toString();
    }

    public String relatorio() {
        return relatorio(Integer.MAX_VALUE);
    }
}
