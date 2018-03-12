#!/bin/bash
DIR=$(pwd | rev | cut -d'/' -f 1 | rev)
if [ "$DIR" != "HateSnake" ]; then
	echo "Run from top HateSnake directory"
	exit
fi
javac -cp $(find ./lib/ -type f | tr '\n' ':') -d ./bin/ ./src/*.java
