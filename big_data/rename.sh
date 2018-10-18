n=0
for f in *.txt; do
	n=$((n+1))
	mv -n "$f" "${n}.txt"
	echo $n
done
