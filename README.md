# xemantic-kotlin-swing-dsl

_Express your Swing code easily in Kotlin_

This project was born when I had an urgent need to quickly provide a simple UI for remote JVM
application. I needed a [remote control for my Robot](https://xemantic.com/#we-are-the-robots),
talking over [OSC](https://en.wikipedia.org/wiki/Open_Sound_Control) protocol, already coded in
Kotlin and based on
[functional reactive programming](https://en.wikipedia.org/wiki/Functional_reactive_programming)
principles (See [we-are-the-robots](https://github.com/xemantic/we-are-the-robots) on GitHub). I
decided to go for Swing, to stay in the same ecosystem, but I remember the experience of coding GUI
in Swing years ago, and I remember what I liked about it and what was painful. Fortunately now I
also have the experience of building Domain Specific Languages in Kotlin and I quickly realized that
I can finally swing the way I always wanted to.


## Example

```kotlin
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.ioScheduler
import java.awt.Dimension
import javax.swing.SwingConstants

fun main() = mainFrame("My Browser") {
  val contentBox = label("") {
    horizontalAlignment = SwingConstants.CENTER
    verticalAlignment = SwingConstants.CENTER
    preferredSize = Dimension(300, 300)
  }
  val urlBox = textField(10)
  val goAction = button("Go!") {
    isEnabled = false
    actionEvents
      .withLatestFrom(urlBox.textChanges) { _, url: String -> url }
      .doOnAfterNext { url ->
        isEnabled = false
        contentBox.text = "Loading: $url"
      }
      .delay(1000, ioScheduler) // e.g. REST request
      .observeOn(swingScheduler)
      .subscribe { url ->
        isEnabled = true
        contentBox.text = "Ready: $url"
      }
  }
  urlBox.textChanges.subscribe { url ->
    goAction.isEnabled = url.isNotBlank()
    contentBox.text = "Will load: $url"
  }
  contentPane = borderPanel {
    layout.hgap = 4
    panel.border = emptyBorder(4)
    north = borderPanel {
      west = label("URL")
      center = urlBox
      east = goAction
    }
    center = contentBox
  }
}
```

will produce:

![example app image](docs/xemantic-kotlin-swing-dsl-example.png)

Benefits:

* compact code, minimal verbosity
* declarative instead of imperative
* functional reactive programming way of handling events
  ([Reaktive](https://github.com/badoo/Reaktive))
* component encapsulation - communication through well defined event streams
* `swingScheduler` for receiving asynchronously produced events (see below)

And here is an equivalent code in Java for the sake of comparison:

```java
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SwingJavaWay {

  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    SwingUtilities.invokeAndWait(SwingJavaWay::createFrame);
  }

  private static void createFrame() {

    JFrame frame = new JFrame("My Browser");

    final JLabel contentBox = new JLabel();
    contentBox.setHorizontalAlignment(SwingConstants.CENTER);
    contentBox.setVerticalAlignment(SwingConstants.CENTER);
    contentBox.setPreferredSize(new Dimension(300, 300));

    JPanel contentPanel = new JPanel(new BorderLayout());

    JPanel northContent = new JPanel(new BorderLayout(4, 0));
    northContent.add(new JLabel(("URL")), BorderLayout.WEST);
    northContent.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    JTextField urlBox = new JTextField(10);
    JButton goAction = new JButton("Go!");
    goAction.setEnabled(false);

    goAction.addActionListener(
        e -> {
          contentBox.setText("Loading: " + urlBox.getText());
          goAction.setEnabled(false);
          Timer timer =
              new Timer(
                  1000,
                  t -> {
                    contentBox.setText("Ready: " + urlBox.getText());
                    goAction.setEnabled(true);
                  });
          timer.setRepeats(false);
          timer.start();
        });
    northContent.add(goAction, BorderLayout.EAST);

    urlBox
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                fireChange(e);
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                fireChange(e);
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                fireChange(e);
              }

              private void fireChange(DocumentEvent e) {
                String text = urlBox.getText();
                contentBox.setText("Will load: " + text);
                goAction.setEnabled(!text.trim().isEmpty());
              }
            });

    northContent.add(urlBox, BorderLayout.CENTER);
    contentPanel.add(northContent, BorderLayout.NORTH);
    contentPanel.add(contentBox, BorderLayout.CENTER);

    frame.setContentPane(contentPanel);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
```

:information_source: There is also another more elaborate Kotlin example:
[SwingKotlinWayWithReactiveModelViewPresenter](src/test/kotlin/SwingKotlinWayWithReactiveModelViewPresenter.kt) 
, showing how to use [Model-View-Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
pattern with this library, where events from the view are represented as reactive `Observable`s.


## Swing scheduler

By default everything in Swing is supposed to run on the same thread. Any update to any GUI
component should happen there, otherwise concurrency might cause consistency issues. But in many
cases long running computation, or asynchronous IO, will require us to receive results in one thread
and display them in the main Swing thread. This is what `swingScheduler` is for:

```kotlin
fun main() = mainFrame("swingScheduler example") {
  contentPane = label{
    observableInterval(1000, swingScheduler)
      .subscribe { tick ->
        text = tick.toString()
        println("Thread: ${Thread.currentThread()}")
      }
    preferredSize = Dimension(100, 100)
    horizontalAlignment = SwingConstants.CENTER
  }
}
```

The `observableInterval` creates a constant stream of ticks produced by another thread.
Once they happen, the subscription code will be handled by the Swing thread.
Most of the time it is not needed to specify `swingScheduler` for simple event handling,
because the default scheduler for receiving events will be usually the same as the one
used for publishing them, and this one is already the
Swing event thread.
