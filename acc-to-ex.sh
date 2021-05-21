#!/bin/bash
echo "transforming files"


# "evolution stages" of processing. Kinda clumsy but need for ref
rm processedA.txt
rm processedB.txt
rm processedC.txt
rm processedD.txt

touch processedA.txt
touch processedB.txt
touch processedC.txt
touch processedD.txt


#sed -E 's/ä/a/g' result.txt | cat >> processedA.txt


sed -E 's/[ä]/ae/g ; s/[ü]/ue/g; s/[ö]/oe/g ; s/Ü/UE/g ; s/Ä/AE/g; s/Ö/OE/g; s/ß/ss/g'   result.txt >> processedA.txt


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

