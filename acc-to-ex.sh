#!/bin/bash
echo "transforming files"

#sed -E 's/ä/a/g' result.txt | cat >> processedA.txt

# remove Umlauts
sed -E 's/[ä]/ae/g ; s/[ü]/ue/g; s/[ö]/oe/g ; s/Ü/UE/g ; s/Ä/AE/g; s/Ö/OE/g; s/ß/ss/g; s/\.//g' result.txt >_a.txt

sed -E 's/Mio €/MioEuro/g; s/in \s*%/inProzent/g; s/://g' _a.txt >_b.txt

# rm leading whitespace
sed 's/^[ \t]*//' _b.txt >_bb.txt

# insert marker for empty start
sed 's/^  /##/' _bb.txt > _bbb.txt



# further processing
#sed '$d;N; /^\(.*\)\n\1$/!P; D' _a.txt > _b.txt

# regex ( [-,€a-zA-z0-9():€"„“]+)+ -> $1##
# removes all duplicate lines
#awk '!seen[$0]++' _bbb.txt >_c.txt

# sed '$!N; s/^\(.*\)\n\1$/\1/; t; D' processedC.txt > d.txt

# vim -esu NONE +'g/\v^(.+)$\_.{-}^\1$/d' +wq

#sed -n 'G; s/\n/&&/; /^\([ -~]*\n\).*\n\1/d; s/\n//; h; P' processedA.txt > processedB.txt

#sed -e '' processedA.txt > processedB.txt

#cat result.txt | sed 's/\  */d'

#
#for file in *txt
#do
#	echo "processing $file"
#	COMPANY=(echo "$file")
#
#	COMPANY=$(echo $file | sed 's/\-layout-crop.txt//')
#
#
#cat $COMPANY >> processed.a
#
#echo "company is $COMPANY"
#
#echo "upper is $upper"
#
#
#	cat $file | sed  's/\   */|/g'  >> processed.a
#	# 's/[[:blank:]]*$//'; '/^$/d' $file-processed.txt
#	echo "done sed processing of file $file"
#	echo ""
#	echo ""
#
#	echo "##########" >> processed.a
#done

# cleanup processing results

# sed 's/\![0-9a-z]//d' processed.a processed.b
