echo "converting pdfs to txt"

for file in *pdf

do
	echo "converting $file"
	pdftotext -layout $file $file.txt

done

