# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2018/2/11
library(optparse)
option_list <- list(
make_option("--paired", default = F, type = "logical", help = "T test paired"),
make_option("--pCutoff", default = 0.05, type = "numeric", help = "P value cut off"),
make_option("--qCutoff", default = 1, type = "numeric", help = "fdr value cut off"),
make_option("--m", default = "tTest", type = "character", help = "test method"),
make_option("--p", default = "tTest", type = "character", help = "project name"),
make_option("--l", default = "1", type = "numeric", help = "logFC cut off"),
make_option("--ve", default = F, type = "logical", help = "var equal")
)
opt <- parse_args(OptionParser(option_list = option_list))
std <- function(x) sd(x) / sqrt(length(x))

data <- read.table(paste(opt$p, "_deal.txt", sep = ""), header = T, sep = "\t", check.names = FALSE, row.names = 1, quote = "")
print(head(data))
group <- read.table(quote = "", file = paste(opt$p, "_group.txt", sep = ""), header = F, sep = "\t", check.names = FALSE,
row.names = 1)
uniq.group <- as.character(unique(group$V2))
colnames(group) = c("Group")

sep.group <- combn(uniq.group, 2)
for (j in 1 : ncol(sep.group)) {
    group1Name = sep.group[, j][1]
    group2Name = sep.group[, j][2]
    group1 <- rownames(subset(group, Group == group1Name))
    group2 <- rownames(subset(group, Group == group2Name))
    group1 <- as.character(group1)
    group2 <- as.character(group2)
    mean1 = apply(data[, group1], 1, mean)
    mean2 = apply(data[, group2], 1, mean)
    logFC = log2(mean2 / mean1)
    okk = as.data.frame(logFC)
    rownames(okk) = rownames(data)
    for (i in 1 : nrow(data)) {
        x = as.numeric(data[i, group1])
        y = as.numeric(data[i, group2])
        okk$p[i] = tryCatch(
        {
            if (opt$m == "tTest") {
                tets = t.test(x , y, alternative = "two.sided", paired = opt$paired, var.equal = opt$ve)
            }else {
                tets = wilcox.test(x , y, alternative = "two.sided", paired = opt$paired)
            }
            tets$p.value
        }, error = function(e){
            1
        }
        )
    }
    okk$fdr = p.adjust(okk$p, method = "fdr", n = length(okk$p))
    fileName = paste(opt$p, "-", group1Name, "_vs_", group2Name, ".txt", sep = "")
    write.table(okk, fileName, sep = "\t", quote = F, col.names = NA)
    okk = subset(okk, p < opt$pCutoff)
    okk = subset(okk, fdr < opt$qCutoff)
    okk = subset(okk, abs(logFC) > opt$l)
    names(okk)[names(okk) == "logFC"] = paste("log2FC(", uniq.group[2], "/", uniq.group[1], ")", sep = "")
    write.table(okk, "result.txt", sep = "\t", quote = F, col.names = NA)
}





