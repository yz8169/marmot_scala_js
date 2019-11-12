# coding=UTF-8
import plotly
import plotly.figure_factory as ff
import pandas as pd
import argparse
import scipy

parser = argparse.ArgumentParser()
parser.add_argument("-s", type=str, default=[], nargs='+', help="will cluster samples")
parser.add_argument("-pm", type=str, default="euclidean", help="pdist metric")
parser.add_argument("-lm", type=str, default="complete", help="linkage method")
args = parser.parse_args()
df = pd.read_csv("deal.txt", sep='\t', index_col=0)
if args.s:
    df = df[args.s]
tDf = df.T
names = tDf.index
pm = args.pm
lm = args.lm
height = len(names) * 15 + 300
d = lambda x: scipy.spatial.distance.pdist(x, metric=pm)
linkage = lambda x: scipy.cluster.hierarchy.linkage(x, lm)
fig = ff.create_dendrogram(tDf, distfun=d, linkagefun=linkage, orientation='left', labels=names)
fig["layout"].update({"title": "Dendrogram Plot", "width": 1160, "height": height,"margin":{
    "l":240
}})
fig["data"].update({"hoverinfo": "x+y"})
config = dict(
    displaylogo=False,
    showLink=False,
)
# plotly.offline.plot(fig, filename="basic-scatter.html", config=config)
a = plotly.offline.plot(fig, filename="basic-scatter.html", config=config, include_plotlyjs=False, output_type="div")
f = open('div.txt', 'w')
f.write(a)
f.close()
