#!/bin/sh
set -eu

PROCESSING_CP=$(find /app/lib/processing-3.3.6 -name '*.jar' | tr '\n' ':')
JUNIT_JAR=/app/lib/junit/junit-platform-console-standalone-1.11.3.jar
ASSERTJ_JAR=/app/lib/junit/assertj-core-3.26.3.jar
TEST_CP="${JUNIT_JAR}:${ASSERTJ_JAR}"
RUNTIME_CP="/app/bin/main:${PROCESSING_CP}"

case "${1:-test}" in
    test)
        exec java -jar "${JUNIT_JAR}" execute \
            --class-path "/app/bin/main:/app/bin/test:${PROCESSING_CP}:${TEST_CP}" \
            --scan-class-path \
            --details=tree \
            --fail-if-no-tests
        ;;
    game)
        exec java -cp "${RUNTIME_CP}" hatesnake.HateSnakeApp
        ;;
    jar)
        exec java -cp "${PROCESSING_CP}" -jar /app/bin/HateSnake.jar
        ;;
    version)
        cat /app/VERSION
        ;;
    *)
        exec java -cp "${RUNTIME_CP}" "$@"
        ;;
esac
