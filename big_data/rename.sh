n=0
for f in $(ls . | sort); do
	n=$((n+1))
	mv -n "$f" "${n}.txt"
done
