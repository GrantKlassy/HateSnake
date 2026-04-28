[![Sponsor GrantKlassy](https://img.shields.io/badge/Sponsor-GrantKlassy-ea4aaa?logo=github-sponsors&logoColor=white&style=for-the-badge)](https://github.com/sponsors/GrantKlassy)

# HateSnake

Code last updated @ [2026-04-27](https://github.com/GrantKlassy/HateSnake/commits/main)

> A snake style game which you will come to hate

## Background
TODO, link blog post talking about the background behind this project

## Running HateSnake

Requires [Task](https://taskfile.dev) and Temurin 17 (Processing 3.3.6 is incompatible
with newer JDKs). On Fedora, `task local:dnf` will install the JDK from the Adoptium
repo.

```bash
$ git clone https://github.com/GrantKlassy/HateSnake.git
$ cd HateSnake
$ task local:run
```

Other useful tasks:

```bash
$ task local:test     # run the JUnit 5 suite (headless, no display required)
$ task local:compile  # javac main + test sources to bin/
$ task local:jar      # build a runnable HateSnake.jar
$ task local:clean    # remove all build artifacts
```

## Tests and container

The test suite uses **JUnit 5 (Jupiter) + AssertJ**, driven by the JUnit Platform
Console Launcher --- no Maven or Gradle required. The full suite covers `Position`,
`Direction`, `Rgb`, `Snake`, `Apple` (BFS pathfinding), and the `Game` state
machine (including grace-frame handling and win/loss transitions).

A multi-stage `Dockerfile` compiles main + test sources, runs the JUnit suite
inside the builder stage, packages a runnable jar, and ships a minimal non-root
runtime image based on a pinned Eclipse Temurin 17 JRE. The build fails if any
test fails, so a green image is a green test run.

```bash
$ task local:build           # podman/docker build -t hatesnake:dev .
$ task local:docker:test     # re-run the JUnit suite inside the runtime image
$ task local:docker:version  # print the JDK version baked into the image
```

`task check` also runs the container build as part of the standard repo check
fan-out.
