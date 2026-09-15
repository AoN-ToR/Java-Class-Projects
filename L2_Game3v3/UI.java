import javax.swing.*;

public class UI {
    // Ask for an integer ID via the GUI buttons (if GUI is open) or fallback to
    // manual input.
    public static int askForId(String prompt, int min, int max) {
        String[] options = new String[max - min + 1];
        for (int i = 0; i < options.length; i++) {
            options[i] = Integer.toString(min + i);
        }
        String res = GameScreen.askForSelection(prompt, options);
        if (res == null)
            return 0;
        try {
            int v = Integer.parseInt(res.trim());
            return v;
        } catch (NumberFormatException e) {
            System.out.println("Answer must be a number");
            return 0;
        }
    }

    // Ask choosing from descriptive options (buttons show the provided strings).
    // Returns 1-based index of the selected option, or 0 if cancelled/invalid.
    public static int askForChoiceIndex(String prompt, String[] options) {
        if (options == null || options.length == 0)
            return 0;
        String res = GameScreen.askForSelection(prompt, options);
        if (res == null)
            return 0;
        // match exact option first
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(res))
                return i + 1;
        }
        // fallback: try parse numeric input
        try {
            int v = Integer.parseInt(res.trim());
            if (v >= 1 && v <= options.length)
                return v;
        } catch (NumberFormatException e) {
            System.out.println("Answer must be a number or one of the provided options");
        }
        return 0;
    }

    // Preserve existing behavior for string prompts: show input dialog and also log
    // to GUI
    public static String askForString(String prompt) {
        GameScreen.appendLog(prompt);
        return JOptionPane.showInputDialog(null, prompt);
    }

    public static void showMessage(String message) {
        GameScreen.appendLog(message);
        JOptionPane.showMessageDialog(null, message);
    }

    public static void waitForOK(String message) {
        GameScreen.askForSelection(message, new String[] { "OK" });
    }
}
