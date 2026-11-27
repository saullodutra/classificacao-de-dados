import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lê os arquivos .gr do GTgraph (formato DIMACS) fornecidos pelo professor:
 *
 *   c comentario
 *   p sp 100 1980   -> 100 vertices e 1980 arcos
 *   a 64 73 66      -> arco de 64 para 73 com peso 66
 *
 * No arquivo os vértices vão de 1 a |V|; no programa, de 0 a |V|-1.
 *
 * ARCOS REPETIDOS: o GTgraph sorteia os arcos com reposição, então o mesmo par (u, v)
 * aparece mais de uma vez nos arquivos (no sample500-249500.gr, 66 mil pares).
 * A matriz só comporta uma aresta por par. Para as duas representações guardarem
 * exatamente o mesmo grafo, o leitor junta os repetidos e fica com o MENOR peso,
 * que é o único que importa para os caminhos mínimos.
 */
public final class GraphReader {

    public enum Representacao { LA, MA }

    /** Resultado da leitura: o grafo e quantos arcos o arquivo tinha. */
    public record Leitura(IGraph grafo, int arcosNoArquivo, int paresRepetidos) {
    }

    private GraphReader() {
    }

    public static Leitura ler(String caminho, Representacao rep) throws IOException {
        int n = -1;
        int arcos = 0;
        // chave = u * n + v; LinkedHashMap mantém a ordem do arquivo
        Map<Long, Double> menorPeso = new LinkedHashMap<>();
        int numeroLinha = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("c")) {
                    continue;
                }
                String[] p = linha.split("\\s+");
                if (p[0].equals("p")) {
                    n = Integer.parseInt(p[2]);
                } else if (p[0].equals("a")) {
                    if (n < 0) {
                        throw new IOException("linha " + numeroLinha + ": arco antes da linha 'p'");
                    }
                    long u = Long.parseLong(p[1]) - 1;
                    long v = Long.parseLong(p[2]) - 1;
                    double w = p.length > 3 ? Double.parseDouble(p[3]) : 1.0;
                    menorPeso.merge(u * n + v, w, Math::min);
                    arcos++;
                }
            }
        }
        if (n < 0) {
            throw new IOException("arquivo sem a linha 'p sp V E': " + caminho);
        }
        IGraph g = (rep == Representacao.LA)
                ? new AdjListGraph(n, GraphType.DIRECTED)
                : new AdjMatrixGraph(n, GraphType.DIRECTED);
        for (Map.Entry<Long, Double> e : menorPeso.entrySet()) {
            int u = (int) (e.getKey() / n);
            int v = (int) (e.getKey() % n);
            g.addEdge(u, v, e.getValue());
        }
        return new Leitura(g, arcos, arcos - g.numEdges());
    }
}
