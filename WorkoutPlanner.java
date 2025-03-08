import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class WorkoutPlanner {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField exerciseField, durationField;
    private JComboBox<String> dayBox;
    private final String FILE_NAME = "workout_schedule.csv";

    public WorkoutPlanner() {
        frame = new JFrame("Custom Workout Planner");
        frame.setSize(800, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(0, 51, 102)); // Deep Blue

        // Table Model
        String[] columns = {"Day", "Exercise", "Duration (mins)"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setBackground(new Color(173, 216, 230)); // Light Blue
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Arial", Font.BOLD, 14));

        // Load previous data
        loadWorkouts();

        // Panel for input fields
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));
        panel.setBackground(new Color(0, 76, 153)); // Darker Blue

        exerciseField = new JTextField();
        durationField = new JTextField();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        dayBox = new JComboBox<>(days);
        JButton addButton = new JButton("Add Workout");
        JButton suggestButton = new JButton("Suggest Workout");
        JButton clearButton = new JButton("Clear Schedule");

        JLabel exerciseLabel = new JLabel("Exercise:", SwingConstants.RIGHT);
        JLabel durationLabel = new JLabel("Duration:", SwingConstants.RIGHT);
        JLabel dayLabel = new JLabel("Day:", SwingConstants.RIGHT);
        exerciseLabel.setForeground(Color.WHITE);
        durationLabel.setForeground(Color.WHITE);
        dayLabel.setForeground(Color.WHITE);

        panel.add(dayLabel);
        panel.add(dayBox);
        panel.add(exerciseLabel);
        panel.add(exerciseField);
        panel.add(durationLabel);
        panel.add(durationField);
        panel.add(addButton);
        panel.add(suggestButton);
        panel.add(clearButton);

        // Button Styles
        addButton.setBackground(new Color(0, 153, 76)); // Green
        addButton.setForeground(Color.WHITE);
        suggestButton.setBackground(new Color(255, 165, 0)); // Orange
        suggestButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(204, 0, 0)); // Red
        clearButton.setForeground(Color.WHITE);

        // Button Actions
        addButton.addActionListener(this::addWorkout);
        suggestButton.addActionListener(this::suggestWorkout);
        clearButton.addActionListener(e -> clearWorkouts());

        // Layout
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addWorkout(ActionEvent e) {
        String day = (String) dayBox.getSelectedItem();
        String exercise = exerciseField.getText().trim();
        String duration = durationField.getText().trim();

        if (exercise.isEmpty() || duration.isEmpty() || !duration.matches("\\d+")) {
            JOptionPane.showMessageDialog(frame, "Please enter valid workout details!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.addRow(new Object[]{day, exercise, duration});
        saveWorkout(day, exercise, duration);
        exerciseField.setText("");
        durationField.setText("");
    }

    private void suggestWorkout(ActionEvent e) {
        String day = (String) dayBox.getSelectedItem();
        String suggestedWorkout = suggestExercise(day);
        JOptionPane.showMessageDialog(frame, "Suggested Workout for " + day + ": " + suggestedWorkout, "AI Workout Suggestion", JOptionPane.INFORMATION_MESSAGE);
    }

    private String suggestExercise(String day) {
        switch (day) {
            case "Monday": return "Full Body Workout";
            case "Tuesday": return "Cardio & HIIT";
            case "Wednesday": return "Leg Day";
            case "Thursday": return "Arms & Shoulders";
            case "Friday": return "Core & Abs";
            case "Saturday": return "Yoga & Stretching";
            case "Sunday": return "Rest Day";
            default: return "Custom Workout";
        }
    }

    private void saveWorkout(String day, String exercise, String duration) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(day + "," + exercise + "," + duration);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving workout data!", "File Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadWorkouts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3 && data[2].matches("\\d+")) {
                    model.addRow(data);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading workout data!", "File Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearWorkouts() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear the schedule?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error clearing data!", "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            model.setRowCount(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorkoutPlanner::new);
    }
}