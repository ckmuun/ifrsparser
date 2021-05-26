#!/bin/bash

rm result.txt
rm result-table.txt
touch result.txt
for file in *.pdf; do
  echo "converting $file"
  pdftotext -layout "$file" "$file".txt

  sed 's/\.//g' <"$file".txt
  sed -E 's/Mio €/MioEuro/g; s/in \s*%/inProzent/g; s/://g ; ' "$file".txt >temp.txt
  sed -E 's/^[a-zA-Z]+/\L&/; s/\s([A-Za-z]+)/\L\u\1/g' < temp.txt
  # sed 's/\( constant = *\)[^ ]*/\1substituteValue/' < "temp".txt
#  sed -E 's/([[:alpha:]]) ([[:alpha:]])/\1,\2/g'
  column -t -s "    " -o "|" temp.txt >>result-table.txt


  cat temp.txt >>result.txt
done

#sed -E 's/  +/;/g' result-table-notsd.txt >result-delim.txt
