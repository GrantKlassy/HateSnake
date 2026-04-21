#!/bin/sh
set -eu

CP="/app/bin:$(find /app/lib -type f -name '*.jar' | tr '\n' ':')"

case "${1:-test}" in
	test)
		exec java -ea -cp "$CP" SmokeTest
		;;
	game)
		exec java -cp "$CP" HateSnake
		;;
	version)
		cat /app/VERSION
		;;
	*)
		exec java -cp "$CP" "$@"
		;;
esac
