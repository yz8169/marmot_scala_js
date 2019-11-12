# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2019/4/27

colramp <-unlist(strsplit("#DADBDC-#FF0000","-"))
ramp <-colorRamp(as.vector(colramp))
heatcol <- paste(rgb(ramp(seq(0,1,length=100)),max=255))
heatcol<-as.data.frame(heatcol)
write.table(heatcol, "middle-red.txt", sep = "\t", quote = F, col.names = NA)
print(heatcol)
