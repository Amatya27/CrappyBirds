# Crappy Birds

<p align="center">
  <a href="https://amatya27.github.io/CrappyBirds/"><img src="media/gameplay.gif" width="560" alt="Crappy Birds gameplay: a white square flaps through gaps between black columns"></a>
</p>

<p align="center">
  <a href="https://amatya27.github.io/CrappyBirds/"><b>Play it in your browser</b></a> &nbsp;·&nbsp; Java Swing original in <code>src/</code>
</p>

My take on Flappy Bird, written in Java with Swing back in 2020. You're a small square, the columns keep coming, and one click or space bar flaps you up. Hit a column, the ceiling or the ground and it's game over. It keeps your high score for the session.

## How the game works

Everything runs on a 20 ms `javax.swing.Timer` tick:

- **Gravity:** every second tick the downward speed grows by 2, capped at 15.
- **Flap:** a jump cancels any downward speed and pushes the bird up by 10.
- **Columns:** pairs of rectangles move left at 10 px per tick with a gap between them. When a pair leaves the screen, a new one is added 600 px after the last, with a random height.
- **Scoring and collisions:** you score when the bird's center passes a column's center. Collisions use `Rectangle.intersects`, and the bird gets snapped to the edge it hit so it doesn't render inside a column.

<p align="center"><img src="media/java-game-over.png" width="560" alt="Java version, game over screen with score and high score"><br><sub>The original Java version running.</sub></p>

## Browser version

`docs/index.html` is a straight port to an HTML canvas, hosted on GitHub Pages. I kept the same constants and the same 20 ms tick (with a fixed-step loop, so it plays at the same speed on any refresh rate). On top of that it:

- scales to any screen size
- works with touch on phones
- remembers your high score between visits

## Running the Java version

You need a JDK (8 or newer).

```bash
javac -d out src/flappybird/*.java
java -cp out flappybird.FlappyBird
```

Or open the folder in NetBeans and run it from there.

**Fixed in 2026:** the source folder is `flappybird`, but two files declared `package flappyBird`. Windows didn't care, but it wouldn't compile on macOS or Linux. Package names now match the folder, and the NetBeans main class points to the right name.

## Files

| File | What it is |
|---|---|
| `src/flappybird/FlappyBird.java` | Game state, physics, input and drawing |
| `src/flappybird/Renderer.java` | The `JPanel` that calls the game's paint method |
| `src/flappybird/Image.java` | A small splash window built with the NetBeans form editor |
| `docs/index.html` | Browser version |
