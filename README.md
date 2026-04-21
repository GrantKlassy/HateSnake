# HateSnake

Code last updated @ [2026-04-20](https://github.com/GrantKlassy/HateSnake/commits/main)

> A snake style game which you will come to hate

## Background
TODO, link blog post talking about the background behind this project

## Running HateSnake
#### Linux
```bash
$ git clone https://github.com/GrantKlassy/HateSnake.git
$ cd HateSnake
$ ./linux/compile.sh && ./linux/run.sh
```
#### Windows
```
TODO Create a PowerShell script or a jar file for running on Windows
```

## Tests and container

A multi-stage `Dockerfile` compiles the sources, runs the headless smoke tests
in `src/test/SmokeTest.java`, and packages a minimal non-root runtime image
based on a pinned Eclipse Temurin 17 JRE. The build fails if any test fails,
so a green image is a green test run.

```bash
$ task build          # podman/docker build -t hatesnake:dev .
$ task local:docker:test     # run SmokeTest inside the runtime image
$ task local:docker:version  # print the JDK version baked into the image
```

`task check` also runs the container build as part of the standard repo check
fan-out.
