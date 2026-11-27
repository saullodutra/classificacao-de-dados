# TP2 - Grafos (Algoritmos e Classificação de Dados) - Parte 2

Até aqui o TP2 tem:
- as classes de grafo do modelo UML da disciplina (`Graph.pdf`);
- a leitura dos arquivos `.gr` do GTgraph fornecidos pelo professor;
- as buscas em largura e em profundidade (parte 1);
- a ordenação topológica, Kruskal, Prim, Bellman-Ford e Dijkstra (parte 2);
- um menu que mostra o grafo de entrada, o algoritmo executado, o resultado e o tempo de execução.

## Como rodar

Requisito: Java 17 ou mais novo. Rodar na pasta do projeto (onde fica a pasta `grafos/`).

```
javac -d bin src/*.java
java -cp bin Main
```

No VS Code: abrir a pasta do projeto (File > Open Folder) e rodar `src/Main.java`.

## Menu

Cada opção de 2 a 8 roda o algoritmo num grafo de exemplo do Cormen, primeiro em LA e
depois em MA, mostrando o grafo de entrada, o resultado e o tempo.

1. Mostra os grafos de exemplo como lista de adjacência e alguns também como matriz.
2. Busca em largura no grafo não dirigido do Cormen (origem `s`).
3. Busca em profundidade no dígrafo do Cormen, com os tempos de descoberta e término.
4. Ordenação topológica no DAG das roupas do Cormen e, para comparar, no dígrafo da DFS,
   que tem ciclo (o programa avisa que não existe ordenação).
5. Kruskal no grafo da árvore geradora mínima do Cormen (peso total 37).
6. Prim no mesmo grafo, com raiz `a` (peso total 37).
7. Bellman-Ford no dígrafo do Cormen com pesos negativos (origem `s`: t = 2, x = 4, y = 7,
   z = -2) e numa variante com ciclo negativo, que o algoritmo detecta.
8. Dijkstra no dígrafo do Cormen (origem `s`: t = 8, x = 9, y = 5, z = 7).
9. Lista os 15 grafos do professor (pasta `grafos/`), lê o escolhido nas duas representações
   e roda todos os algoritmos acima, mostrando um resumo do resultado e o tempo de cada um.
   Kruskal e Prim rodam numa versão não dirigida do grafo (ver abaixo). A saída também
   confere se Dijkstra e Bellman-Ford encontraram as mesmas distâncias.

Os tempos são medidos com `System.nanoTime()`, só em volta do algoritmo, depois de 5
execuções de aquecimento da JVM, que são descartadas.

## Classes

Modelo UML (`Graph.pdf`):

| Classe | O que faz |
|---|---|
| `GraphType` | `DIRECTED` ou `UNDIRECTED` |
| `IGraph` | Interface: `type`, `vertices`, `edges`, `addEdge`, `getEdge`, `adj`, `numVertices`, `numEdges` |
| `Vertex` | Vértice: `id` e `label` |
| `Edge` | Aresta: vértices `u` e `v` e peso (`double`, padrão 1) |
| `AdjListGraph` | Lista de adjacência (LA): memória O(V + E), `adj(u)` em O(grau) |
| `AdjMatrixGraph` | Matriz de adjacência (MA) de `Edge`: memória O(V²), `getEdge` em O(1), `adj(u)` em O(V) |

Os construtores lançam `IllegalArgumentException` se `numVertices < 0`, e `validateVertex`
lança `IndexOutOfBoundsException` para vértice fora do intervalo, como no modelo.

Demais classes:

| Classe | O que faz |
|---|---|
| `GraphReader` | Lê os arquivos `.gr` (`p sp V E` e `a u v w`) em LA ou MA |
| `GraphUtils` | Densidade, resumo, impressão e o "outro extremo" de uma aresta |
| `BuscaEmLargura` | BFS: distância, predecessor, caminho e ordem de descoberta |
| `BuscaEmProfundidade` | DFS: tempos d/f, predecessor, ordem de término e detecção de ciclo |
| `OrdenacaoTopologica` | DFS e ordem decrescente de término; avisa quando o dígrafo tem ciclo |
| `ArvoreGeradoraMinima` | Base de Kruskal e Prim: arestas escolhidas, peso total e relatório |
| `Kruskal` | AGM: arestas por peso crescente + conjuntos disjuntos (união por posto e compressão de caminho) |
| `Prim` | AGM a partir de uma raiz, com fila de prioridade mínima (heap) |
| `CaminhoMinimo` | Base de Bellman-Ford e Dijkstra: `INITIALIZE-SINGLE-SOURCE`, `RELAX`, caminho e relatório |
| `BellmanFord` | Caminho mínimo de origem única; aceita peso negativo e detecta ciclo negativo |
| `Dijkstra` | Caminho mínimo de origem única com fila de prioridade mínima; recusa peso negativo |
| `Exemplos` | Grafos pequenos (do Cormen e do enunciado) para conferir os algoritmos à mão |
| `Main` | Menu |

Os algoritmos usam só os métodos de `IGraph`, então funcionam com as duas representações.

### Detalhes de implementação

- **Restrições de cada algoritmo.** A ordenação topológica exige dígrafo; Kruskal e Prim exigem
  grafo não dirigido; Dijkstra exige pesos não negativos. Fora disso, o construtor lança
  `IllegalArgumentException` e o menu mostra o erro.
- **Bellman-Ford e o experimento.** O Bellman-Ford percorre as arestas vértice a vértice, por
  `adj(u)`, e não pela lista `edges()`. Assim o custo depende da representação: O(V·E) em LA e
  O(V³) em MA. Ele segue o Cormen e faz sempre as |V| - 1 passadas, sem parar antes. Só pula
  vértices com distância infinita, porque relaxar a partir deles não muda nada.
- **Dijkstra e Prim** usam `PriorityQueue`, que não tem `DECREASE-KEY`. Quando uma chave diminui,
  o vértice entra de novo na fila, e as entradas antigas são ignoradas ao sair.
- **Kruskal** usa a lista `edges()`, como no Cormen, então o tempo dele quase não muda entre
  LA e MA.
- **AGM nos arquivos do professor.** Os arquivos são dígrafos. Para rodar Kruskal e Prim,
  `GraphUtils.paraNaoDirigido` junta (u, v) e (v, u) numa aresta só, com o menor peso, e descarta
  laços. A conversão fica fora da medição de tempo.

## Observações sobre os arquivos do professor

- Nos arquivos os vértices vão de 1 a |V|; no programa, de 0 a |V|-1.
- **Arcos repetidos.** O GTgraph sorteia os arcos com reposição, então o mesmo par (u, v)
  aparece várias vezes. A matriz só comporta uma aresta por par; para LA e MA guardarem
  exatamente o mesmo grafo, o leitor junta os repetidos e fica com o menor peso (o único que
  importa para caminhos mínimos). Por isso o número de arestas e a densidade reais ficam
  abaixo dos nominais:

| Arquivo | Arcos no arquivo | Arestas distintas | Densidade nominal | Densidade real |
|---|---:|---:|---:|---:|
| sample100-1980 | 1.980 | 1.773 | 0,2 | 0,179 |
| sample100-3960 | 3.960 | 3.266 | 0,4 | 0,330 |
| sample100-5940 | 5.940 | 4.438 | 0,6 | 0,448 |
| sample100-7920 | 7.920 | 5.391 | 0,8 | 0,545 |
| sample100-9900 | 9.900 | 6.215 | 1,0 | 0,628 |
| sample200-7960 | 7.960 | 7.205 | 0,2 | 0,181 |
| sample200-15920 | 15.920 | 13.072 | 0,4 | 0,328 |
| sample200-23880 | 23.880 | 17.929 | 0,6 | 0,450 |
| sample200-31840 | 31.840 | 21.907 | 0,8 | 0,550 |
| sample200-39800 | 39.800 | 25.167 | 1,0 | 0,632 |
| sample500-49900 | 49.900 | 45.108 | 0,2 | 0,181 |
| sample500-99800 | 99.800 | 82.141 | 0,4 | 0,329 |
| sample500-149700 | 149.700 | 112.525 | 0,6 | 0,451 |
| sample500-199600 | 199.600 | 137.330 | 0,8 | 0,550 |
| sample500-249500 | 249.500 | 157.800 | 1,0 | 0,632 |

- Os arquivos têm arestas com peso 0; na matriz, "sem aresta" é `null`, e não um peso especial.
- Densidade: D = |E| / (|V|(|V|-1)) para dígrafos, como no enunciado; no grafo não dirigido
  cada aresta conta nos dois sentidos.

## O que falta

- Floyd-Warshall (caminho mínimo entre todos os pares);
- Ford-Fulkerson (fluxo máximo);
- o experimento LA × MA com Bellman-Ford e Dijkstra nos 15 grafos (pelo menos 10 execuções
  por configuração, com os tempos salvos para os boxplots);
- as opções desses itens no menu;
- o artigo com os resultados.
