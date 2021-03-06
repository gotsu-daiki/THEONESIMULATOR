reset
set key left top
set bmargin 4
set lmargin 10
set rmargin 4
set key font"IPAGothic,13"
set grid
set terminal png notransparent font"IPAGothic, 16"
set grid linestyle 1 linecolor 0
set xlabel "シミュレーション時間の経過[s]" font "Arial,15"
set ylabel "避難所到着ノード数[人]" font "Arial,15"
plot "no.txt" with linespoints pointsize 1 pointtype 7 title "通信なし",\
"epidemic.txt" with linespoints pointsize 1 pointtype 7 title "Epidemic",\
"45.txt" with linespoints pointsize 1 pointtype 7 title "提案手法(45°)",\
"20.txt" with linespoints pointsize 1 pointtype 7 title "提案手法(20°)",\
"10.txt" with linespoints pointsize 1 pointtype 7 title "提案手法(10°)",\
"5.txt" with linespoints pointsize 1 pointtype 7 title "提案手法-(5°)"

set output "./output_reach_command.png"
replot
