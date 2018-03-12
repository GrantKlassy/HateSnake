#!/bin/bash
DIR=$(pwd | rev | cut -d'/' -f 1 | rev)
if [ "$DIR" != "HateSnake" ]; then
	echo "Run from top HateSnake directory"
	exit
fi
java -cp $(find ./lib/ -type f | tr '\n' ':')$(find ./bin/ | tr '\n' ':') HateSnake
