[![Sponsor GrantKlassy](https://img.shields.io/badge/Sponsor-GrantKlassy-ea4aaa?logo=github-sponsors&logoColor=white&style=for-the-badge)](https://github.com/sponsors/GrantKlassy)

# HateSnake

Code last updated @ [2026-05-05](https://github.com/GrantKlassy/HateSnake/commits/main)

> Snake, but the apple wants you dead.

The apple runs BFS from the snake's head every respawn. If your reachable
region is small enough to fill, it baits you one square in front of your face
so you trap yourself. Otherwise it picks the cruelest reachable square ---
shrinking your post-eat region, hugging corners, and dragging you the long way
around. There is no friendly mode.

## Quickstart

```bash
git clone https://github.com/GrantKlassy/HateSnake.git
cd HateSnake
task local:run
```

Requires [Task](https://taskfile.dev) and Temurin 17. Processing 3.3.6 will
not run on a newer JDK. On Fedora, `task local:dnf` installs the JDK from the
Adoptium repo.

## Tasks

| Command                  | What it does                                 |
| ------------------------ | -------------------------------------------- |
| `task local:run`         | Compile and launch the game window           |
| `task local:test`        | JUnit 5 suite, headless, no display required |
| `task local:jar`         | Build a runnable `HateSnake.jar`             |
| `task local:build`       | Build the container image                    |
| `task local:docker:test` | Re-run the suite inside the runtime image    |
| `task local:clean`       | Remove build artifacts                       |

## Container

The multi-stage `Dockerfile` compiles main and test sources, runs the JUnit
suite in the builder, and ships a non-root runtime on a pinned Temurin 17 JRE.
Tests gate the build, so a green image is a green test run. `task check`
exercises the container build as part of the standard repo check fan-out.

## Tests

JUnit 5 (Jupiter) + AssertJ via the JUnit Platform Console Launcher. No Maven,
no Gradle. Coverage spans `Position`, `Direction`, `Rgb`, `Snake`, the
`Apple` BFS pathfinder, and the `Game` state machine including grace-frame
handling and win/loss transitions.

## License

[0BSD](LICENSE.txt). Do whatever you want.
