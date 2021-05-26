package main

import (
	"bufio"
	"fmt"
	"github.com/emirpasic/gods/lists/singlylinkedlist"
	"github.com/emirpasic/gods/maps/linkedhashmap"
	"github.com/emirpasic/gods/sets/hashset"
	"io/ioutil"
	"log"
	"os"
	"regexp"
	"strings"
)

// simple .txt accounting statements parser
func main() {

	file, err := os.Open("input.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer func() {
		if err = file.Close(); err != nil {
			log.Fatal(err)
		}
	}()

	b, err := ioutil.ReadAll(file)
	s := string(b)

	//fmt.Print(s)

	lines := strings.Split(s, "\n")
	//lines = rmDuplicateLinesAll(lines)

	indices := indexLines(lines)

	indexedLines := linkedhashmap.New()

	for index, line := range lines {
		indexedLines.Put(line, indices[index])
	}

	_ = mxOperation(lines)

	// furhter process non-duplicate lines
	writeLinesSliceToFile("processed.txt", lines)
}

func mxOperation(lines []string) []string {
	// put lines into char matrix
	rows := singlylinkedlist.New()

	for _, line := range lines {
		row := singlylinkedlist.New()
		for _, char := range line {
			row.Add(string(char))
		}
		rows.Add(row)
	}

	// go has no proper support for matrices. wow.
	matrix := make([][]string, 8000)

	// fill matrix
	for rowIndex, line := range lines {

		// todo determine longest line in file and use as length
		row := make([]string, 450)

		for colIndex, char := range line {

			row[colIndex] = string(char)
		}
		matrix[rowIndex] = row
	}

	/*
		for rowIndex, chars := range matrix  {

			for colIndex, char := range chars {

				if char != " " {
					continue
				}


			}
		}

	*/

	return lines

}

func insert() {

}

func indexLines(lines []string) []int {
	regex := regexp.MustCompile(`( [-,€a-zA-z0-9():"„“]+)+`)

	lineGroups := make([]int, len(lines))

	for index, line := range lines {
		groups := regex.FindAllString(line, -1)

		fmt.Println(len(groups))
		lineGroups[index] = len(groups)
	}
	return lineGroups
}

func sliceStatements() {

}

func writeLinesSliceToFile(filename string, lines []string) {

	file, err := os.OpenFile(filename, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)

	if err != nil {
		log.Fatalf("failed creating file: %s", err)
	}

	datawriter := bufio.NewWriter(file)

	for _, data := range lines {
		_, _ = datawriter.WriteString(data + "\n")
	}

	_ = datawriter.Flush()
	_ = file.Close()
}

func buildDocument() {

}

const DUPLICATE_LINE_REMOVED_INDICATOR = "DUPLICATE_LINE_REMOVED"

func rmDuplicateLinesAll(lines []string) []string {
	trimmedLines := make([]string, len(lines))

	copy(trimmedLines, lines)

	for index, line := range trimmedLines {
		fmt.Println("line: ", line, index)

		whitespace := regexp.MustCompile(`\s`)

		trimmedLines[index] = whitespace.ReplaceAllString(line, ``)
	}

	// lines without duplicates but also trimmed whitespacin> re-copy to preserve whitespace in original lines array
	noDups := rmDuplicateLines(trimmedLines)

	// clean the slice with the non-trimmed lines
	for index, _ := range lines {
		if noDups[index] == DUPLICATE_LINE_REMOVED_INDICATOR {
			lines[index] = ""
		}
	}
	return lines
}

func rmDuplicateLines(lines []string) []string {
	//	noDuplicates := make([]string, len(lines))

	lineSet := hashset.New()

	for index, line := range lines {

		if lineSet.Contains(line) {
			lines[index] = DUPLICATE_LINE_REMOVED_INDICATOR
			continue
		}

		lineSet.Add(line)
	}

	return lines
}

func printlines(lines []string) {
	for line := range lines {
		fmt.Println("line:")

		fmt.Println(line)
	}

}

type rawCompanyStatement struct {
}

type rawSingleStatement struct {
	Lines   []string
	Company string
}

type overallStatement struct {
}

type finstatement struct {
}
