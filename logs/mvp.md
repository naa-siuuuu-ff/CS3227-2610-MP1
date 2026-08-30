Next, the goal is to make the mvp. Here is a test to make sure that javafx is working

Your `pom.xml` is functional, but three specific adjustments should be made before running your Day 0 spike:

---

### Key Adjustments

- **Upgrade JavaFX Version to 21.0.11:** `17.0.2` is an older release that frequently encounters platform-native WebKit rendering bugs, especially on Apple Silicon (M-series) macOS and newer Linux distributions. Upgrading to `21.0.11` (the long-term release aligned with Java 17/21) provides a more stable embedded `WebView`.
- **Fix Missing JavaFX Modules Configuration:** If your project does not use a `module-info.java` file, `javafx-maven-plugin` must explicitly declare which JavaFX modules to include at runtime (`javafx.controls`, `javafx.fxml`, `javafx.web`). Without this, `mvn javafx:run` will throw `java.lang.NoClassDefFoundError: javafx/application/Application`.
- **Align CommonMark and Extension Versions:** Keep `commonmark.version` at `0.21.0` or update cleanly across the parser, table extension, and strikethrough extension.

---

### Updated `pom.xml`

## <Code here>

### Step to Verify Now

Create a minimal `src/main/java/com/notebook/MainApp.java` and execute:

```bash
mvn clean compile javafx:run

```

For your Day 0 de-risking spike, a console `System.out.println("Hello World")` is not enough. You need to verify that **JavaFX initializes, the graphic window opens, and the native embedded WebKit engine (`WebView`) renders HTML** without platform-binding crashes.

Replace the contents of `src/main/java/com/notebook/MainApp.java` with this minimal spike implementation:

```java
package com.notebook;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();

        // Spike test: render basic HTML to confirm WebKit/JavaFX Web works
        String testHtml = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: sans-serif; padding: 20px;">
                <h1 style="color: #2b579a;">Spike: JavaFX WebView Active</h1>
                <p>If you can read this, your JavaFX and WebKit native dependencies are functioning correctly.</p>
            </body>
            </html>
            """;

        webView.getEngine().loadContent(testHtml);

        StackPane root = new StackPane(webView);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("MP1 De-risking Spike");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

```

---

### How to Run the Spike

In your project root terminal, run:

```bash
mvn clean compile javafx:run

```

- **Pass condition:** A 600×400 desktop window pops up rendering the styled blue header and paragraph.
- **Fail condition:** The terminal throws native binary errors (e.g., `libjfxwebkit`, missing architecture `.dylib`/`.so`/`.dll`, or module access warnings).

Now with javaFX test done successfully, lets build the notebook system, then connect it to the javaFX UI.


