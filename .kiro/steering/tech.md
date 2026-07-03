# Tech Stack

## Language & Version
- Java SE 1.8 (Java 8)

## Build System
- Eclipse JDT (no Maven/Gradle — project uses `.classpath` and `.project` files)
- Source directory: `src/`
- Output directory: `bin/`

## Libraries & Frameworks
- Java AWT and Swing only (no external dependencies)
- `javax.swing.Timer` for game loops and animation
- `java.awt.Graphics2D` for rendering

## Compiling & Running
Each sub-project has its own `main` method. Compile and run individual classes directly:

```sh
# From project root
javac -d bin src/games/snake/ai/*.java
java -cp bin games.snake.ai.Canvas

javac -d bin src/games/snake/*.java
java -cp bin games.snake.Canvas

javac -d bin src/ai_project/self_avoiding_walk/*.java
java -cp bin ai_project.self_avoiding_walk.Canvas

javac -d bin src/simple_machine/cube_timer/*.java
java -cp bin simple_machine.cube_timer.CubeTimer
```

Or use Eclipse's built-in "Run As → Java Application" on any class with a `main` method.

## Testing
No test framework is configured. There are no unit tests.
