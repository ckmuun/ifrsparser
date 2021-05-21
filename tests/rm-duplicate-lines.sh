#fmt duplicate-lines.txt > _a.txt
awk '!seen[$0]++' duplicate-lines.txt > res.txt
