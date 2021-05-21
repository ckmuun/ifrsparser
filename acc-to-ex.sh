
#!/bin/bash
echo "transforming files"

mkdir output
cd output
for file in *
do
	rm $file
done
cd ..
cd annual-reports-2020
rm processed.txt

touch processed.a

for file in *txt
do
	echo "processing $file"
	COMPANY=$(echo $file)

	COMPANY=$(echo $file | sed 's/\-layout-crop.txt//')


cat $COMPANY >> processed.a

echo "company is $COMPANY"

echo "upper is $upper"


	cat $file | sed  's/\   */|/g'  >> processed.a
	# 's/[[:blank:]]*$//'; '/^$/d' $file-processed.txt
	echo "done sed processing of file $file"
	echo ""
	echo ""

	echo "##########" >> processed.a
done


# cleanup processing results

# sed 's/\![0-9a-z]//d' processed.a processed.b

mv processed.a processed.txt
