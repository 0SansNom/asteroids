package views;

import javafx.animation.AnimationTimer;

public class Timer extends AnimationTimer {

  private long lastTick = -1;
  private final View view;
  private boolean isStopped = true;

  public Timer(View view) {
    this.view = view;
  }

  @Override
  public void handle(long now) {
    if (isStopped) { return; }
    if (lastTick < 0) {
      lastTick = now;
      return;
    }
    double dt = (now - lastTick) * 1e-9;
    lastTick = now;
    view.tick(dt);
  }

  public boolean isStopped() {
    return isStopped;
  }

  public void stop() {
    lastTick = -1;
    isStopped = true;
  }

  public void restart() {
    isStopped = false;
  }
}
