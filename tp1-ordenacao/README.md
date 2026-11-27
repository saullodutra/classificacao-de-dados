# TP1 – Algoritmos de Classificação/Ordenação (ACD – UNIPAMPA Bagé)

Implementação em Java de 10 algoritmos de ordenação, testes de corretude, experimentos de
tempo de execução e análise dos resultados.

- **Relatório:** [`relatorio/TP1_relatorio.pdf`](relatorio/TP1_relatorio.pdf)
- **Apresentação:** [`apresentacao.html`](apresentacao.html) (baixar e abrir no navegador)

## Estrutura

```
src/tp1/
  Ordenadores.java          7 algoritmos de comparação, genéricos (<T extends Comparable<T>>)
  OrdenadoresInt.java       os mesmos 7 para int[] (extensão: genérico x primitivo)
  OrdenadoresLineares.java  Counting, Radix (LSD, base 10) e Bucket Sort
  Algoritmo.java            catálogo para chamar todos da mesma forma
  TesteCorretude.java       Etapa 1: vetores de 10-15 elementos + 2.000 vetores aleatórios
  Experimento.java          Etapas 3-4: gera entradas, mede com System.nanoTime(), grava CSV
analise/analise.py          Etapa 5: estatísticas, gráficos e tabelas LaTeX
resultados/
  tempos.csv                os 3.850 tempos individuais (impl, algoritmo, config, n, rep, tempo_ns)
  estatisticas.csv          média, mediana, mín, máx, desvio, CV por configuração
  expoentes.csv             expoente empírico do ajuste t = c·n^b
  testes_corretude.txt      saída dos testes (vetores antes e depois)
resultados-maquina-joao/    os mesmos experimentos rodados na máquina do JV, e a comparação
                            entre as duas máquinas (comparacao_*.txt)
relatorio/                  relatório LaTeX (relatorio.tex) + figuras + tabelas + PDF final
apresentacao.html           slides da apresentação
roda_experimentos.sh        reproduz todo o plano experimental
```

## Como executar

Requer JDK 11 ou superior.

```bash
javac --release 11 -d bin src/tp1/*.java
java -cp bin tp1.TesteCorretude                    # testes de corretude
java -Xmx2g -Xss512m -cp bin tp1.Experimento --impl int --config aleatorio \
     --ns 1000,10000 --algs todos --reps 30 --aquec 3 --saida resultados/tempos.csv
python3 analise/analise.py                         # gráficos e tabelas
```

Opções do `Experimento`: `--impl generico|int`, `--config aleatorio|crescente|decrescente`,
`--algs todos|quadraticos|nlogn|lineares|rapidos|Bubble,Quick,...`, `--reps`, `--aquec`,
`--rep-inicial` (para dividir uma configuração longa em lotes).

No IntelliJ: abra a pasta como projeto, marque `src` como Sources Root e rode as classes
`TesteCorretude` e `Experimento` (adicione `-Xmx2g -Xss512m` em VM options).

## Relatório no Overleaf

Compacte o conteúdo da pasta `relatorio/` (sem o PDF) num zip, envie em
*New Project → Upload Project* e compile `relatorio.tex` com pdfLaTeX.

## Ambiente das medições

AMD Ryzen 7 5825U (notebook com Windows), executando numa VM Linux Ubuntu 22.04.5
com 2 vCPUs e 2,8 GB de RAM; OpenJDK 11.0.32.
