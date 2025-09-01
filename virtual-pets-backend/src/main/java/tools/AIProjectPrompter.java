package tools;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AIProjectPrompter extends JFrame {

    private static final int MAX_CHARS_PER_BATCH = 17500; // capped for free tier
    private final File projectFolder;

    public AIProjectPrompter() {
        super("AI Project Prompter");
        this.projectFolder = new File(System.getProperty("user.dir"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton scanBtn = new JButton("Scan Project");
        scanBtn.addActionListener(e -> scanProject());

        JPanel topPanel = new JPanel();
        topPanel.add(scanBtn);
        add(topPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    private void scanProject() {
        List<File> files = collectFiles(projectFolder);
        List<String> batches = createBatches(files, MAX_CHARS_PER_BATCH);

        JPanel batchPanel = new JPanel();
        batchPanel.setLayout(new BoxLayout(batchPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < batches.size(); i++) {
            String batch = batches.get(i);
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea textArea = new JTextArea(batch, 20, 80);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);

            JScrollPane scroll = new JScrollPane(textArea);
            JButton copyBtn = new JButton("Copy Batch " + (i + 1));
            copyBtn.addActionListener(e -> copyToClipboard(batch));

            panel.add(scroll, BorderLayout.CENTER);
            panel.add(copyBtn, BorderLayout.SOUTH);
            panel.setBorder(BorderFactory.createTitledBorder("Batch " + (i + 1)));
            batchPanel.add(panel);
        }

        JScrollPane mainScroll = new JScrollPane(batchPanel);
        setContentPane(mainScroll);
        revalidate();
        repaint();
    }

    private List<File> collectFiles(File folder) {
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().contains(File.separator + "target" + File.separator))
                    .filter(f -> !f.getAbsolutePath().contains(File.separator + "bin" + File.separator))
                    .filter(f -> !f.getAbsolutePath().contains(File.separator + ".idea" + File.separator))
                    .filter(f -> !f.getName().endsWith(".class"))
                    .filter(f -> !f.getAbsolutePath().contains(File.separator + "node_modules" + File.separator))
                    .filter(f -> !isLargeStatic(f))
                    .filter(f -> f.getName().endsWith(".java")
                            || f.getName().endsWith(".properties")
                            || f.getName().equals("pom.xml")
                            || f.getName().equals("docker-compose.yml"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isLargeStatic(File f) {
        String[] exts = {".png",".jpg",".jpeg",".gif",".mp4",".mov",".avi"};
        for (String ext : exts) if (f.getName().toLowerCase().endsWith(ext)) return true;
        return false;
    }

    private List<String> createBatches(List<File> files, int maxChars) {
        List<String> batches = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (File f : files) {
            StringBuilder content = new StringBuilder();
            content.append("=== FILE: ").append(f.getAbsolutePath()).append(" ===\n");
            try {
                List<String> lines = Files.readAllLines(f.toPath());
                for (String line : lines) {
                    String min = minifyLine(line);
                    if (!min.isEmpty()) content.append(min).append("\n");
                }
            } catch (IOException e) {
                content.append("// Error reading file: ").append(e.getMessage()).append("\n");
            }
            content.append("\n");

            if (current.length() + content.length() > maxChars) {
                batches.add(current.toString());
                current = new StringBuilder();
            }
            current.append(content);
        }
        if (current.length() > 0) batches.add(current.toString());

        return batches;
    }

    /** Minify a single line: remove comments, collapse spaces, trim */
    private String minifyLine(String line) {
        String noComments = line.replaceAll("//.*", "").replaceAll("/\\*.*?\\*/", "");
        return noComments.trim().replaceAll("\\s+", " ");
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Batch copied to clipboard!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIProjectPrompter::new);
    }
}
