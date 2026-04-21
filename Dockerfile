# HateSnake container --- multi-stage build with automated tests gating the image.
#
# Base image version is pinned via ARGs so builds are reproducible. Override at
# build time with --build-arg JDK_TAG=... if needed.
#
# Stage 1 (builder) compiles src/ and src/test/, packages the JAR, and runs the
# SmokeTest. If any of those fail, the image does not exist --- so a successful
# build IS a successful test run.
#
# Stage 2 (runtime) is a minimal JRE-only image that runs as a non-root user.

ARG REGISTRY=docker.io/library
ARG JDK_TAG=17-jdk-alpine
ARG JRE_TAG=17-jre-alpine

# ============================================================================
# Stage 1: compile + test
# ============================================================================
FROM ${REGISTRY}/eclipse-temurin:${JDK_TAG} AS builder

WORKDIR /build

COPY lib ./lib
COPY src ./src
COPY bin/manifest.txt ./bin/manifest.txt

RUN set -eux; \
	mkdir -p bin; \
	CP=$(find ./lib -type f -name '*.jar' | tr '\n' ':'); \
	javac -cp "$CP" -d ./bin ./src/*.java; \
	javac -cp "$CP:./bin" -d ./bin ./src/test/*.java; \
	(cd bin && jar cfm HateSnake.jar manifest.txt Apple.class Color.class Dir.class HateSnake.class Snake.class); \
	java -ea -cp "$CP:./bin" SmokeTest; \
	java -version 2> VERSION

# ============================================================================
# Stage 2: runtime
# ============================================================================
FROM ${REGISTRY}/eclipse-temurin:${JRE_TAG} AS runtime

LABEL org.opencontainers.image.source="https://github.com/GrantKlassy/HateSnake" \
      org.opencontainers.image.title="HateSnake" \
      org.opencontainers.image.description="A snake style game which you will come to hate"

# Security: dedicated non-root user with a locked password and no shell login.
RUN addgroup -S hatesnake && \
	adduser -S -G hatesnake -h /app -s /sbin/nologin hatesnake

WORKDIR /app

COPY --from=builder --chown=hatesnake:hatesnake /build/bin /app/bin
COPY --from=builder --chown=hatesnake:hatesnake /build/lib /app/lib
COPY --from=builder --chown=hatesnake:hatesnake /build/VERSION /app/VERSION
COPY --chown=hatesnake:hatesnake docker/entrypoint.sh /app/entrypoint.sh
RUN chmod 0555 /app/entrypoint.sh

USER hatesnake

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["test"]
