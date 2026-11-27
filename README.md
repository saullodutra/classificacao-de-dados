# Classificação de Dados

Trabalhos da disciplina de **Algoritmos e Classificação de Dados** (Engenharia de Computação, UNIPAMPA - campus Bagé).

Dupla: Saullo Dutra e JV.

## Trabalhos

| Pasta | O que é | Situação |
|---|---|---|
| [`tp1-ordenacao/`](tp1-ordenacao/) | TP1: 10 algoritmos de ordenação em Java, experimento de tempo de execução em duas máquinas, relatório e apresentação | Concluído |
| [`tp2-grafos/`](tp2-grafos/) | TP2: representações de grafos (lista e matriz de adjacência) e os algoritmos de grafos, com o experimento LA × MA para Bellman-Ford e Dijkstra | Em andamento |
| [`exercicio-veiculos/`](exercicio-veiculos/) | Exercício de classes em Java: `Vehicle`, `Truck` e `OffRoad` | Concluído |

Cada pasta tem o código e, quando houver, um `README.md` explicando como rodar.

## Como rodar um trabalho Java

Requisito: Java 17 ou mais novo. Exemplo com o TP2:

```
cd tp2-grafos
javac -d bin src/*.java
java -cp bin Main
```

## Como a gente trabalha no repositório

- A branch `main` tem sempre a versão que compila e funciona.
- Cada parte nova é feita numa branch própria (ex.: `jv/floyd-warshall`) e entra na `main`
  por um Pull Request, para o outro ver o que mudou antes de juntar.
- Arquivos gerados (`bin/`, `*.class`) e de IDE ficam fora do repositório (ver `.gitignore`).
