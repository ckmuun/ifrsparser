package main

import (
	"fmt"
	"github.com/emirpasic/gods/sets/hashset"
	"io/ioutil"
	"log"
	"os"
	"regexp"
	"strings"
)

// simple .txt accounting statements parser
func main() {

	file, err := os.Open("result.txt")
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

	trimmedLines := make([]string, len(lines))

	copy(trimmedLines, lines)

	for index, line := range trimmedLines {
		fmt.Println("line: ", line, index)

		whitespace := regexp.MustCompile(`\s`)

		trimmedLines[index] = whitespace.ReplaceAllString(line, ``)
	}

	// lines without duplicates but also trimmed whitespace -> re-copy to preserve whitespace in original lines array
	noDups := rmDuplicateLines(trimmedLines)

	for index, _ := range lines {
		if noDups[index] == DUPLICATE_LINE_REMOVED_INDICATOR {
			lines[index] = ""
		}
	}

	printlines(lines)

}

const DUPLICATE_LINE_REMOVED_INDICATOR = "DUPLICATE_LINE_REMOVED"

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

type overallStatement struct {
}

type finstatement struct {
}
