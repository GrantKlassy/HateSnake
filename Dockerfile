# HateSnake container --- multi-stage build with the JUnit 5 suite gating the image.
#
# Base image versions are pinned via ARGs so builds are reproducible. Override at
# build time with --build-arg JDK_TAG=... if needed.
#
# Stage 1 (builder) compiles src/main + src/test, runs the full JUnit 5 suite via
# the Console Launcher, then packages a runnable jar of the production classes.
# If compilation or any test fails, the image does not exist --- so a successful
# build IS a successful test run.
#
# Stage 2 (runtime) is a minimal JRE-only image that runs as a non-root user.

ARG REGISTRY=docker.io/library
ARG JDK_TAG=17-jdk-alpine
# JRE runtime is noble (Ubuntu) rather than alpine --- the alpine Temurin JRE
# is a headless build (no libawt_xawt.so) and Processing 3 cannot open a
# window without it.
ARG JRE_TAG=17-jre-noble

# ============================================================================
# Stage 1: compile + test + jar
# ============================================================================
FROM ${REGISTRY}/eclipse-temurin:${JDK_TAG} AS builder

WORKDIR /build

COPY lib ./lib
COPY src ./src

RUN set -eux; \
    PROCESSING_CP=$(find ./lib/processing-3.3.6 -name '*.jar' | tr '\n' ':'); \
    JUNIT_JAR=./lib/junit/junit-platform-console-standalone-1.11.3.jar; \
    ASSERTJ_JAR=./lib/junit/assertj-core-3.26.3.jar; \
    TEST_CP="${JUNIT_JAR}:${ASSERTJ_JAR}"; \
    mkdir -p bin/main bin/test; \
    javac -Werror -Xlint:all -cp "${PROCESSING_CP}" -d bin/main src/main/hatesnake/*.java; \
    javac -cp "${PROCESSING_CP}:bin/main:${TEST_CP}" -d bin/test src/test/hatesnake/*.java; \
    java -jar "${JUNIT_JAR}" execute --class-path "bin/main:bin/test:${PROCESSING_CP}:${TEST_CP}" --scan-class-path --details=tree --fail-if-no-tests; \
    jar --create --file bin/HateSnake.jar --manifest src/main/manifest.txt -C bin/main .; \
    java -version 2> VERSION

# ============================================================================
# Stage 2: runtime
# ============================================================================
FROM ${REGISTRY}/eclipse-temurin:${JRE_TAG} AS runtime

LABEL org.opencontainers.image.source="https://github.com/GrantKlassy/HateSnake" \
      org.opencontainers.image.title="HateSnake" \
      org.opencontainers.image.description="A snake style game which you will come to hate"

# Processing 3 uses AWT (JAVA2D renderer) --- install the X11 libs, fonts, and
# freetype it needs to open a window. These are only required for `game` mode;
# the test suite never touches AWT.
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libfreetype6 \
        fontconfig \
        fonts-dejavu-core \
        libx11-6 \
        libxext6 \
        libxrender1 \
        libxtst6 \
        libxi6 && \
    rm -rf /var/lib/apt/lists/*

# Security: dedicated non-root system user with a locked shell login.
RUN groupadd --system hatesnake && \
    useradd --system --gid hatesnake --home-dir /app --shell /usr/sbin/nologin hatesnake

WORKDIR /app

COPY --from=builder --chown=hatesnake:hatesnake /build/bin /app/bin
COPY --from=builder --chown=hatesnake:hatesnake /build/lib /app/lib
COPY --from=builder --chown=hatesnake:hatesnake /build/VERSION /app/VERSION
COPY --chown=hatesnake:hatesnake docker/entrypoint.sh /app/entrypoint.sh
RUN chmod 0555 /app/entrypoint.sh

USER hatesnake

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["test"]
