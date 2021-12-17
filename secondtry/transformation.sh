#!/bin/bash

rm result.txt
rm result-table.txt
touch result.txt
for file in *.pdf; do
  echo "converting $file"
  pdftotext -fixed "$file" "$file".txt

# basic cleanup
  echo "basic cleanup"
  sed 's/\.//g'  "$file".txt  > temp0.txt   # | sed -E 's/[ \t]*$//' "$1"



  datamash transpose --no-strict < temp0.txt | cat > transposed.txt
  datamash reverse --no-strict < transposed.txt | cat > retransposed.txt


  echo "convert dashes to minus"
  sed 's/–/-/g ; s/– /-/g ' temp0.txt | sed 's/^/|/g' | rev | sed 's/^/|/g' | rev > temp01.txt

  #datamash transpose --no-strict < temp01.txt


  #echo "rm emtpy lines"
#  sed '/^$/d'

  sed -E 's/Mio €/MioEuro/g; s/in \s*%/inProzent/g; s/://g ; ' temp01.txt >temp.txt

  # THIS IS WHERE THE MAGIC HAPPENS
  sed -E  's/   +/  /g; s/  /|/g' temp.txt | column  -t -x -s "|" -o "|" | sed -E 's/   / /g' > "$file".table1.txt
  #datamash transpose --no-strict < temp0.txt | cat > transposed.txt

# transform to camelCase
  echo "convert to camelCase"
  sed -E 's/^[a-zA-Z]+/\L&/; s/\s([A-Za-z]+)/\L\u\1/g'  temp.txt > temp1.txt

  rev temp1.txt | column -t -s "   " -o "|" | rev > table.txt


  rev temp1.txt | sed -E 's/  +/;/g' | rev > result-delim.txt

# reverse lines
#  rev temp.txt > tempRev.txt



 # sed -E 's/\s([A-Za-z]+)/\L\u\1/g' temp1.txt > temp11.txt

  sed -E 's/ +/   /g' temp2.txt > temp21.txt
  #sed -E 's/ +/   /g' temp2.txt | rev > temp21.txt

  # create table
  column -t -s "   " -o "|" temp2.txt  >>result-table.txt



#  cat temp.txt >>result.txt
done


 # sed 's/\( constant = *\)[^ ]*/\1substituteValue/' < "temp".txt
#  sed -E 's/([[:alpha:]]) ([[:alpha:]])/\1,\2/g'
