#!/usr/bin/env bash
# Plano completo dos experimentos do TP1, exatamente como foi executado.
# Grava cada tempo individual em resultados/tempos.csv.
#
# Uso:  ./roda_experimentos.sh        (Linux / macOS / WSL / Git Bash)
# Requer: JDK 11+ (javac) para compilar; JRE 11+ para executar.
# Tempo total aproximado na máquina do relatório: ~40 minutos.
set -e
cd "$(dirname "$0")"

JVM="-Xmx2g -Xss512m"
CSV=resultados/tempos.csv
mkdir -p bin resultados
javac --release 11 -d bin src/tp1/*.java

run() { java $JVM -cp bin tp1.Experimento --saida "$CSV" "$@"; }

# 0) testes de corretude
java -cp bin tp1.TesteCorretude resultados/testes_corretude.txt

# ---------------- ordem aleatória ----------------
# O(n log n) e lineares
run --impl generico --config aleatorio --ns 1000,10000,100000 --algs nlogn   --reps 30 --aquec 3
run --impl int      --config aleatorio --ns 1000,10000,100000,1000000 --algs rapidos --reps 30 --aquec 3
run --impl generico --config aleatorio --ns 1000000          --algs nlogn   --reps 30 --aquec 2
run --impl int      --config aleatorio --ns 10000000         --algs rapidos --reps 10 --aquec 1

# O(n^2)
for impl in generico int; do
  run --impl $impl --config aleatorio --ns 1000,5000,10000 --algs quadraticos --reps 30 --aquec 3
  run --impl $impl --config aleatorio --ns 50000           --algs quadraticos --reps 10 --aquec 0
  # n = 100.000: cada execução leva de 0,5 s a ~40 s; o aquecimento global da JVM basta.
  # (Na execução original o Bubble genérico foi dividido em lotes com --rep-inicial
  #  por causa de um limite de tempo por comando; o resultado é equivalente.)
  run --impl $impl --config aleatorio --ns 100000          --algs quadraticos --reps 10 --aquec 0
done

# ---------------- crescente e decrescente ----------------
for impl in generico int; do
  for cfg in crescente decrescente; do
    run --impl $impl --config $cfg --ns 10000   --algs todos       --reps 30 --aquec 3
    run --impl $impl --config $cfg --ns 50000   --algs quadraticos --reps 10 --aquec 0
    run --impl $impl --config $cfg --ns 1000000 --algs rapidos     --reps 30 --aquec 2
  done
done

# ---------------- análise ----------------
python3 analise/analise.py

echo "Concluido. Tempos em $CSV"
