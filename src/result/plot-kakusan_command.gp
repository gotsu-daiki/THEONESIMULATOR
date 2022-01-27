set bmargin 4
set lmargin 10
set rmargin 4
set key font"IPAGothic,13"
set boxwidth 0.5
set grid

set grid ytics linestyle 8
set boxwidth 0.5 relative
set yrange [0:20000]
set xtics font "IPAGothic,12"
set ylabel "拡散数［個］" font "IPAGothic,18"

#set title "災害地情報の拡散数" font "IPAGothic,20"
set xlabel "Length of Packet Chains"

#set terminal png notransparent size 800,500 font"Helvetica, 18"
set terminal png notransparent font"IPAGothic, 16"

set output "/dev/null"
#set key left
#set key bottom
set nokey

set style fill solid
set linetype 1 lc rgb "skyblue"
set linetype 2 lc rgb "sea-green"
set linetype 3 lc rgb "orange"
set linetype 4 lc rgb "yellow"
set linetype 5 lc rgb "pink"


#set style data histograms
set xtics rotate by -25 #X軸の文字の傾き
set xtics font "IPAGothic,11"
plot "kakusan_command_date_2.csv" using 0:3:2:xticlabels(1) with boxes lc variable,\
 "kakusan_command_date_2.csv" using 0:($3+.9):(sprintf("%3.0f",$3)) with labels notitle font "Arial,15"



set output "./output-kakusan_command.png"
replot

