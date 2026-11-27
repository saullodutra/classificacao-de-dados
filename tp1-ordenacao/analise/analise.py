"""
TP1 - Análise dos tempos de execução.

Lê resultados/tempos.csv (um tempo individual por linha) e gera:
  - resultados/estatisticas.csv  (média, mediana, mín, máx, desvio-padrão, CV)
  - resultados/expoentes.csv     (inclinação log-log de cada algoritmo)
  - relatorio/figuras/*.pdf      (gráficos usados no relatório e nos slides)
  - relatorio/tabelas/*.tex      (tabelas LaTeX geradas a partir dos dados)

Uso: python3 analise/analise.py   (a partir da raiz do projeto)
"""
from pathlib import Path
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

RAIZ = Path(__file__).resolve().parent.parent
CSV = RAIZ / "resultados" / "tempos.csv"
FIG = RAIZ / "relatorio" / "figuras"
TAB = RAIZ / "relatorio" / "tabelas"
FIG.mkdir(parents=True, exist_ok=True)
TAB.mkdir(parents=True, exist_ok=True)

# Paleta categórica (ordem fixa) e tons de texto
C = ["#2a78d6", "#eb6834", "#1baf7a", "#eda100"]
CINZA = "#8a8984"
TXT, TXT2 = "#0b0b0b", "#52514e"

QUAD = ["Bubble", "Insertion", "Selection"]
NLOGN = ["Shell", "Heap", "Merge", "Quick"]
LIN = ["Counting", "Radix", "Bucket"]
ORDEM = QUAD + NLOGN + LIN
MARC = {"Bubble": "o", "Insertion": "s", "Selection": "^",
        "Shell": "o", "Heap": "s", "Merge": "^", "Quick": "D",
        "Counting": "o", "Radix": "s", "Bucket": "^"}
COR = {**{a: C[i] for i, a in enumerate(QUAD)},
       **{a: C[i] for i, a in enumerate(NLOGN)},
       **{a: C[i] for i, a in enumerate(LIN)}}
CFG_COR = {"crescente": C[2], "aleatorio": C[0], "decrescente": C[1]}
CFG_NOME = {"crescente": "Crescente", "aleatorio": "Aleatória", "decrescente": "Decrescente"}

plt.rcParams.update({
    "font.size": 9, "axes.titlesize": 9.5, "axes.labelsize": 9,
    "axes.edgecolor": "#c9c8c3", "axes.labelcolor": TXT2, "xtick.color": TXT2, "ytick.color": TXT2,
    "axes.grid": True, "grid.color": "#ecebe7", "grid.linewidth": 0.6,
    "axes.spines.top": False, "axes.spines.right": False,
    "legend.frameon": False, "legend.fontsize": 8, "lines.linewidth": 1.6,
    "savefig.bbox": "tight", "figure.dpi": 150, "font.family": "DejaVu Sans",
})

d = pd.read_csv(CSV)
d["ms"] = d.tempo_ns / 1e6

# ----------------------------------------------------------------------
# Estatísticas descritivas
# ----------------------------------------------------------------------
est = (d.groupby(["impl", "config", "n", "algoritmo"]).ms
         .agg(reps="count", media="mean", mediana="median", minimo="min", maximo="max", desvio="std")
         .reset_index())
est["cv_pct"] = 100 * est.desvio / est.media
est.to_csv(RAIZ / "resultados" / "estatisticas.csv", index=False, float_format="%.4f")

def med(impl, cfg, alg):
    s = est[(est.impl == impl) & (est.config == cfg) & (est.algoritmo == alg)].sort_values("n")
    return s.n.values, s.mediana.values, s

# ----------------------------------------------------------------------
# Expoente empírico: inclinação de log(t) x log(n) (tamanhos >= 10^4)
# ----------------------------------------------------------------------
linhas = []
for impl in ["generico", "int"]:
    for alg in ORDEM:
        n, t, _ = med(impl, "aleatorio", alg)
        m = n >= 10000
        if m.sum() >= 2:
            b = np.polyfit(np.log10(n[m]), np.log10(t[m]), 1)[0]
            linhas.append({"impl": impl, "algoritmo": alg, "expoente": b,
                           "n_min": int(n[m].min()), "n_max": int(n[m].max())})
exp = pd.DataFrame(linhas)
exp.to_csv(RAIZ / "resultados" / "expoentes.csv", index=False, float_format="%.3f")

def salva(fig, nome):
    fig.savefig(FIG / f"{nome}.pdf")
    fig.savefig(FIG / f"{nome}.png", dpi=200)
    plt.close(fig)

def fmt_n(ax):
    ax.set_xscale("log"); ax.set_yscale("log")
    ax.set_xlabel("tamanho do vetor n")

def linha(ax, n, t, alg, rotulo=None):
    ax.plot(n, t, color=COR[alg], marker=MARC[alg], ms=5, label=rotulo or alg,
            markeredgecolor="white", markeredgewidth=0.8)

def referencia(ax, n0, t0, n1, pot, texto):
    """Reta de referência t ~ n^pot (ou n log n) passando por (n0, t0)."""
    ns = np.array([n0, n1], dtype=float)
    if pot == "nlogn":
        ts = t0 * (ns * np.log2(ns)) / (n0 * np.log2(n0))
    else:
        ts = t0 * (ns / n0) ** pot
    ax.plot(ns, ts, color=CINZA, ls=":", lw=1.2)
    ax.annotate(texto, (ns[1], ts[1]), color=CINZA, fontsize=7.5, xytext=(3, 0),
                textcoords="offset points", va="center")

# ----------------------------------------------------------------------
# Fig 1 - quadráticos, ordem aleatória, log-log
# ----------------------------------------------------------------------
fig, axs = plt.subplots(1, 2, figsize=(7.2, 2.9), sharey=True)
for ax, impl, tit in zip(axs, ["generico", "int"], ["Genérico (Comparable / Integer[])", "Primitivo (int[])"]):
    for alg in QUAD:
        n, t, _ = med(impl, "aleatorio", alg)
        linha(ax, n, t, alg)
    n, t, _ = med(impl, "aleatorio", "Bubble")
    referencia(ax, 1000, t[0] * 0.25, 100000, 2, "∝ n²")
    fmt_n(ax); ax.set_title(tit)
axs[0].set_ylabel("tempo mediano (ms)")
axs[0].legend(loc="upper left")
salva(fig, "fig_quadraticos")

# ----------------------------------------------------------------------
# Fig 2 - O(n log n) e lineares, ordem aleatória, log-log
# ----------------------------------------------------------------------
fig, axs = plt.subplots(1, 3, figsize=(7.4, 2.9), sharey=True)
for alg in NLOGN:
    n, t, _ = med("generico", "aleatorio", alg); linha(axs[0], n, t, alg)
    n, t, _ = med("int", "aleatorio", alg); linha(axs[1], n, t, alg)
for alg in LIN:
    n, t, _ = med("int", "aleatorio", alg); linha(axs[2], n, t, alg)
n, t, _ = med("int", "aleatorio", "Quick")
axs[2].plot(n, t, color=CINZA, ls="--", lw=1.2, label="Quick (int), ref.")
for ax, tit in zip(axs, ["n log n — genérico", "n log n — int", "Lineares — int"]):
    fmt_n(ax); ax.set_title(tit); ax.legend(loc="upper left", fontsize=7)
axs[0].set_ylabel("tempo mediano (ms)")
salva(fig, "fig_nlogn_lineares")

# ----------------------------------------------------------------------
# Fig 3 - tempo normalizado pela complexidade teórica
# ----------------------------------------------------------------------
fig, axs = plt.subplots(1, 3, figsize=(7.4, 2.8))
for alg in QUAD:
    n, t, _ = med("int", "aleatorio", alg)
    axs[0].plot(n, t * 1e6 / n**2, color=COR[alg], marker=MARC[alg], ms=5, label=alg, markeredgecolor="white")
axs[0].set_title("Quadráticos: t / n²"); axs[0].set_ylabel("ns por unidade")
for alg in NLOGN:
    n, t, _ = med("int", "aleatorio", alg)
    axs[1].plot(n, t * 1e6 / (n * np.log2(n)), color=COR[alg], marker=MARC[alg], ms=5, label=alg, markeredgecolor="white")
axs[1].set_title("n log n: t / (n log₂ n)")
for alg in LIN:
    n, t, _ = med("int", "aleatorio", alg)
    axs[2].plot(n, t * 1e6 / n, color=COR[alg], marker=MARC[alg], ms=5, label=alg, markeredgecolor="white")
axs[2].set_title("Lineares: t / n")
for ax in axs:
    ax.set_xscale("log"); ax.set_xlabel("n"); ax.legend(fontsize=7); ax.set_ylim(bottom=0)
salva(fig, "fig_normalizado")

# ----------------------------------------------------------------------
# Fig 4 - influência da configuração (n = 10.000, int e genérico)
# ----------------------------------------------------------------------
def barras_config(impl, algs, n, nome, titulo, figsize=(7.4, 2.9)):
    fig, ax = plt.subplots(figsize=figsize)
    x = np.arange(len(algs)); w = 0.26
    for k, cfg in enumerate(["crescente", "aleatorio", "decrescente"]):
        vals = []
        for alg in algs:
            s = est[(est.impl == impl) & (est.config == cfg) & (est.n == n) & (est.algoritmo == alg)]
            vals.append(s.mediana.iloc[0] if len(s) else np.nan)
        ax.bar(x + (k - 1) * w, vals, w * 0.92, color=CFG_COR[cfg], label=CFG_NOME[cfg])
    ax.set_xticks(x, algs, fontsize=7.5 if len(algs) > 8 else 9); ax.set_yscale("log")
    ax.set_ylabel("tempo mediano (ms, log)")
    ax.set_title(titulo, pad=18); ax.legend(ncol=3, loc="lower center", bbox_to_anchor=(0.5, 1.0))
    ax.grid(axis="x", visible=False)
    salva(fig, nome)

barras_config("int", ORDEM, 10000, "fig_config_int_10k", "Efeito da ordem de entrada — int[], n = 10.000")
barras_config("generico", QUAD + NLOGN, 10000, "fig_config_gen_10k", "Efeito da ordem de entrada — genérico, n = 10.000", (6.4, 2.9))
barras_config("int", NLOGN + LIN, 1000000, "fig_config_int_1M", "Efeito da ordem de entrada — int[], n = 1.000.000", (6.4, 2.9))

# ----------------------------------------------------------------------
# Fig 5 - boxplots (dispersão entre repetições)
# ----------------------------------------------------------------------
def boxplot(impl, cfg, n, algs, nome, titulo):
    fig, ax = plt.subplots(figsize=(6.4, 2.7))
    dados = [d[(d.impl == impl) & (d.config == cfg) & (d.n == n) & (d.algoritmo == a)].ms.values for a in algs]
    bp = ax.boxplot(dados, tick_labels=algs, widths=0.55, patch_artist=True,
                    medianprops=dict(color=TXT, lw=1.4), flierprops=dict(marker="o", ms=3, mfc=CINZA, mec="none"),
                    boxprops=dict(facecolor="#d7e6f8", edgecolor=C[0]), whiskerprops=dict(color=C[0]),
                    capprops=dict(color=C[0]))
    ax.set_ylabel("tempo (ms)"); ax.set_title(titulo); ax.grid(axis="x", visible=False)
    salva(fig, nome)

boxplot("int", "aleatorio", 1000000, NLOGN + LIN, "fig_box_int_1M",
        "Distribuição das 30 repetições — int[], aleatório, n = 1.000.000")
boxplot("generico", "aleatorio", 1000000, NLOGN, "fig_box_gen_1M",
        "Distribuição das 30 repetições — genérico, aleatório, n = 1.000.000")
boxplot("int", "aleatorio", 100000, QUAD, "fig_box_int_quad_100k",
        "Distribuição das 10 repetições — int[], aleatório, n = 100.000")

# ----------------------------------------------------------------------
# Fig 6 - genérico x int (razão das medianas)
# ----------------------------------------------------------------------
fig, ax = plt.subplots(figsize=(6.4, 2.6))
algs = QUAD + NLOGN
for k, n in enumerate([10000, 100000]):
    r = []
    for alg in algs:
        g = est[(est.impl == "generico") & (est.config == "aleatorio") & (est.n == n) & (est.algoritmo == alg)].mediana.iloc[0]
        i = est[(est.impl == "int") & (est.config == "aleatorio") & (est.n == n) & (est.algoritmo == alg)].mediana.iloc[0]
        r.append(g / i)
    x = np.arange(len(algs))
    b = ax.bar(x + (k - 0.5) * 0.38, r, 0.36, color=C[k], label=f"n = {n:,}".replace(",", "."))
    for xi, v in zip(x, r):
        ax.annotate(f"{v:.1f}×", (xi + (k - 0.5) * 0.38, v), ha="center", va="bottom", fontsize=6.5,
                    color=TXT2, xytext=(0, 1), textcoords="offset points")
ax.axhline(1, color=CINZA, lw=0.8)
ax.set_xticks(x, algs); ax.set_ylabel("tempo genérico / tempo int")
ax.set_title("Custo da versão genérica (Comparable) em relação a int[] — ordem aleatória")
ax.legend(); ax.grid(axis="x", visible=False)
salva(fig, "fig_generico_vs_int")

# ----------------------------------------------------------------------
# Tabelas LaTeX
# ----------------------------------------------------------------------
def num(v):
    if v >= 100: s = f"{v:,.0f}"
    elif v >= 10: s = f"{v:,.1f}"
    elif v >= 1: s = f"{v:,.2f}"
    else: s = f"{v:,.3f}"
    return s.replace(",", "X").replace(".", ",").replace("X", ".")

def tabela_cfg(impl, cfg, nome, legenda, rotulo):
    s = est[(est.impl == impl) & (est.config == cfg)]
    ns = sorted(s.n.unique())
    algs = [a for a in ORDEM if a in s.algoritmo.unique()]
    cab = " & ".join([f"{n:,}".replace(",", ".") for n in ns])
    out = [r"\begin{table}[htbp]\centering\small", rf"\caption{{{legenda}}}\label{{{rotulo}}}",
           r"\setlength{\tabcolsep}{4pt}",
           r"\begin{tabular}{l" + "r" * len(ns) + "}", r"\toprule",
           rf"Algoritmo & {cab} \\", r"\midrule"]
    for a in algs:
        cel = []
        for n in ns:
            r = s[(s.n == n) & (s.algoritmo == a)]
            cel.append(num(r.media.iloc[0]) if len(r) else "--")
        out.append(f"{a} & " + " & ".join(cel) + r" \\")
    out += [r"\bottomrule", r"\end{tabular}", r"\end{table}"]
    (TAB / f"{nome}.tex").write_text("\n".join(out), encoding="utf-8")

tabela_cfg("int", "aleatorio", "tab_int_aleatorio",
           r"Tempo médio (ms) — implementação \texttt{int[]}, ordem aleatória", "tab:int-aleat")
tabela_cfg("generico", "aleatorio", "tab_gen_aleatorio",
           r"Tempo médio (ms) — implementação genérica (\texttt{Comparable}), ordem aleatória", "tab:gen-aleat")

def tabela_detalhe(impl, cfg, n, nome, legenda, rotulo):
    s = est[(est.impl == impl) & (est.config == cfg) & (est.n == n)].copy()
    s["o"] = s.algoritmo.map({a: i for i, a in enumerate(ORDEM)}); s = s.sort_values("o")
    out = [r"\begin{table}[htbp]\centering\small", rf"\caption{{{legenda}}}\label{{{rotulo}}}",
           r"\begin{tabular}{lrrrrrrr}", r"\toprule",
           r"Algoritmo & Rep. & Média & Mediana & Mín. & Máx. & Desvio & CV (\%) \\", r"\midrule"]
    for _, r in s.iterrows():
        out.append(f"{r.algoritmo} & {r.reps} & {num(r.media)} & {num(r.mediana)} & {num(r.minimo)} & "
                   f"{num(r.maximo)} & {num(r.desvio)} & " + f"{r.cv_pct:.1f}".replace(".", ",") + r" \\")
    out += [r"\bottomrule", r"\end{tabular}", r"\end{table}"]
    (TAB / f"{nome}.tex").write_text("\n".join(out), encoding="utf-8")

tabela_detalhe("int", "aleatorio", 1000000, "tab_det_int_1M",
               r"Estatísticas completas (ms) — \texttt{int[]}, ordem aleatória, $n = 10^6$", "tab:det-int-1M")
tabela_detalhe("generico", "aleatorio", 100000, "tab_det_gen_100k",
               r"Estatísticas completas (ms) — genérico, ordem aleatória, $n = 10^5$", "tab:det-gen-100k")

def tabela_config(impl, n, nome, legenda, rotulo, algs):
    out = [r"\begin{table}[htbp]\centering\small", rf"\caption{{{legenda}}}\label{{{rotulo}}}",
           r"\begin{tabular}{lrrrr}", r"\toprule",
           r"Algoritmo & Crescente & Aleatória & Decrescente & Decresc./Aleat. \\", r"\midrule"]
    for a in algs:
        v = {}
        for cfg in ["crescente", "aleatorio", "decrescente"]:
            r = est[(est.impl == impl) & (est.config == cfg) & (est.n == n) & (est.algoritmo == a)]
            v[cfg] = r.media.iloc[0] if len(r) else np.nan
        if np.isnan(v["aleatorio"]): continue
        razao = f"{v['decrescente']/v['aleatorio']:.2f}".replace(".", ",")
        out.append(f"{a} & {num(v['crescente'])} & {num(v['aleatorio'])} & {num(v['decrescente'])} & {razao}" + r" \\")
    out += [r"\bottomrule", r"\end{tabular}", r"\end{table}"]
    (TAB / f"{nome}.tex").write_text("\n".join(out), encoding="utf-8")

tabela_config("int", 10000, "tab_cfg_int_10k", r"Tempo médio (ms) por configuração — \texttt{int[]}, $n = 10^4$",
              "tab:cfg-int-10k", ORDEM)
tabela_config("generico", 10000, "tab_cfg_gen_10k", r"Tempo médio (ms) por configuração — genérico, $n = 10^4$",
              "tab:cfg-gen-10k", QUAD + NLOGN)
tabela_config("int", 50000, "tab_cfg_int_50k", r"Tempo médio (ms) por configuração — quadráticos \texttt{int[]}, $n = 5\cdot10^4$",
              "tab:cfg-int-50k", QUAD)
tabela_config("generico", 50000, "tab_cfg_gen_50k", r"Tempo médio (ms) por configuração — quadráticos genérico, $n = 5\cdot10^4$",
              "tab:cfg-gen-50k", QUAD)

# expoentes
out = [r"\begin{table}[htbp]\centering\small",
       r"\caption{Expoente empírico $b$ do ajuste $t \approx c\,n^b$ (ordem aleatória, $n \ge 10^4$)}\label{tab:expoentes}",
       r"\begin{tabular}{llrrr}", r"\toprule",
       r"Algoritmo & Complexidade (médio) & $b$ genérico & $b$ \texttt{int} & Faixa de $n$ (\texttt{int}) \\", r"\midrule"]
teo = {"Bubble": r"$\Theta(n^2)$", "Insertion": r"$\Theta(n^2)$", "Selection": r"$\Theta(n^2)$",
       "Shell": r"$\approx O(n^{3/2})$", "Heap": r"$\Theta(n\log n)$", "Merge": r"$\Theta(n\log n)$",
       "Quick": r"$\Theta(n\log n)$", "Counting": r"$\Theta(n+k)$", "Radix": r"$\Theta(d(n+b))$", "Bucket": r"$\Theta(n)$"}
for a in ORDEM:
    g = exp[(exp.impl == "generico") & (exp.algoritmo == a)]
    i = exp[(exp.impl == "int") & (exp.algoritmo == a)]
    gs = f"{g.expoente.iloc[0]:.2f}".replace(".", ",") if len(g) else "--"
    ii = i.iloc[0]
    faixa = f"$10^{{{int(np.log10(ii.n_min))}}}$--$10^{{{int(np.log10(ii.n_max))}}}$"
    out.append(f"{a} & {teo[a]} & {gs} & {ii.expoente:.2f}".replace(".", ",") + f" & {faixa}" + r" \\")
out += [r"\bottomrule", r"\end{tabular}", r"\end{table}"]
(TAB / "tab_expoentes.tex").write_text("\n".join(out), encoding="utf-8")

print("Estatísticas:", len(est), "configurações; figuras e tabelas geradas.")
print(exp.round(3).to_string(index=False))
