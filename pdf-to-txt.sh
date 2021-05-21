echo "converting pdfs to txt"

for filetxt in *txt
do
  rm $filetxt
done


touch result.txt

# iterate over pdfs in directory
for file in *pdf

do
	echo "converting $file"
	pdftotext -layout $file $file.txt

  echo  "$file" >> result.txt
  cat "$file".txt >> result.txt


done

cat result.txt
