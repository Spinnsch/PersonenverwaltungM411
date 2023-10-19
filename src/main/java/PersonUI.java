import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonUI {
    private JFrame frame;
    private JButton anzeigenButton;
    private JButton hinzufuegenButton;
    private JButton loeschenButton; // Neuer Button
    private JTextArea ergebnisTextArea;
    private JButton bearbeitenButton;

    public PersonUI() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        anzeigenButton = new JButton("Personen anzeigen");
        anzeigenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                anzeigenPersonen();
            }
        });
        frame.add(anzeigenButton, BorderLayout.NORTH);

        hinzufuegenButton = new JButton("Person hinzufügen");
        hinzufuegenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hinzufuegenPerson();
            }
        });
        frame.add(hinzufuegenButton, BorderLayout.CENTER);

        loeschenButton = new JButton("Person löschen"); // Neuer Button
        loeschenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loeschenPerson();
            }
        });

        bearbeitenButton = new JButton("Personen ändern");
        bearbeitenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBearbeitungsfenster(); // Rufen Sie die Methode zum Öffnen des Bearbeitungsfensters auf
            }
        });



        // Erstellen Sie ein JPanel, um die Buttons "Person hinzufügen" und "Person löschen" zu gruppieren
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(hinzufuegenButton);
        buttonPanel.add(loeschenButton);
        buttonPanel.add(bearbeitenButton);
        frame.add(buttonPanel, BorderLayout.SOUTH); // Fügt das buttonPanel hinzu

        ergebnisTextArea = new JTextArea();
        frame.add(ergebnisTextArea, BorderLayout.CENTER);
    }
    private void anzeigenPersonen() {
        Connection connection = DatabaseConnection.getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM personen");
            StringBuilder resultText = new StringBuilder();

            while (resultSet.next()) {
                String vorname = resultSet.getString("Vorname");
                String nachname = resultSet.getString("Nachname");
                String geschlecht = resultSet.getString("Geschlecht");
                String geburtsdatum = resultSet.getString("Geburtsdatum");
                String region = resultSet.getString("Region");
                String ahvNummer = resultSet.getString("AHV_Nummer");
                int kinder = resultSet.getInt("Kinder");

                resultText.append("Vorname: " + vorname + "\n");
                resultText.append("Nachname: " + nachname + "\n");
                resultText.append("Geschlecht: " + geschlecht + "\n");
                resultText.append("Geburtsdatum: " + geburtsdatum + "\n");
                resultText.append("Region: " + region + "\n");
                resultText.append("AHV-Nummer: " + ahvNummer + "\n");
                resultText.append("Kinder: " + kinder + "\n");
                resultText.append("----------------------------\n");
            }

            ergebnisTextArea.setText(resultText.toString());

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fügen Sie die JTextArea in eine JScrollPane ein, um das Scrollen zu ermöglichen
        JScrollPane scrollPane = new JScrollPane(ergebnisTextArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Aktualisieren der Anzeige
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }
    private String formatDatum(String inputDatum) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(inputDatum);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Ungültiges Datumsformat. Verwenden Sie DD.MM.YYYY.");
            return null;
        }
    }
    private void hinzufuegenPerson() {
        JTextField vornameField = new JTextField(20);
        JTextField nachnameField = new JTextField(20);
        JTextField ahvNummerField = new JTextField(20);
        JTextField kinderField = new JTextField(2);

        JTextField geburtsdatumField = new JTextField(10);
        // Erstellen Sie JComboBox für die Regionen und das Geschlecht
        JComboBox<String> regionComboBox = new JComboBox<>(new String[]{"Zürich", "Rest"});
        JComboBox<String> geschlechtComboBox = new JComboBox<>(new String[]{"Männlich", "Weiblich", "GahtDichNütAh"});

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Vorname:"));
        inputPanel.add(vornameField);
        inputPanel.add(new JLabel("Nachname:"));
        inputPanel.add(nachnameField);
        inputPanel.add(new JLabel("Geschlecht:"));
        inputPanel.add(geschlechtComboBox);
        inputPanel.add(new JLabel("Geburtsdatum (DD.MM.YYYY):"));
        inputPanel.add(geburtsdatumField);
        inputPanel.add(new JLabel("Region:"));
        inputPanel.add(regionComboBox);
        inputPanel.add(new JLabel("AHV-Nummer. max 20 Zeichen:"));
        inputPanel.add(ahvNummerField);
        inputPanel.add(new JLabel("Kinder:"));
        inputPanel.add(kinderField);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Person hinzufügen", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String vorname = vornameField.getText();
            String nachname = nachnameField.getText();
            String geburtsdatumInput = geburtsdatumField.getText();
            String geburtsdatum = formatDatum(geburtsdatumInput);
            String region = regionComboBox.getSelectedItem().toString();
            String geschlecht = geschlechtComboBox.getSelectedItem().toString();
            String ahvNummer = ahvNummerField.getText();
            int kinder = Integer.parseInt(kinderField.getText());

            if (geburtsdatum != null) {
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null) {
                    try {
                        String insertSQL = "INSERT INTO personen (Vorname, Nachname, Geschlecht, Geburtsdatum, Region, AHV_Nummer, Kinder) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                        preparedStatement.setString(1, vorname);
                        preparedStatement.setString(2, nachname);
                        preparedStatement.setString(3, geschlecht);
                        preparedStatement.setString(4, geburtsdatum);
                        preparedStatement.setString(5, region);
                        preparedStatement.setString(6, ahvNummer);
                        preparedStatement.setInt(7, kinder);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Person wurde erfolgreich hinzugefügt.");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Fehler beim Hinzufügen der Person.");
                        }

                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Fehler beim Hinzufügen der Person.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Keine Verbindung zur Datenbank.");
                }
            }
        }
    }

    private void loeschenPerson() {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Abfrage, um alle Personen abzurufen
                String selectSQL = "SELECT ID, Vorname, Nachname FROM personen";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectSQL);

                DefaultListModel<String> personListModel = new DefaultListModel<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String vorname = resultSet.getString("Vorname");
                    String nachname = resultSet.getString("Nachname");
                    personListModel.addElement("ID: " + id + ", Vorname: " + vorname + ", Nachname: " + nachname);
                }

                JList<String> personList = new JList<>(personListModel);
                JScrollPane scrollPane = new JScrollPane(personList);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                int result = JOptionPane.showConfirmDialog(frame, scrollPane, "Person löschen", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    int selectedIndex = personList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        // Extrahiere die ausgewählte Person ID
                        String selectedPerson = personListModel.getElementAt(selectedIndex);
                        int id = Integer.parseInt(selectedPerson.split(",")[0].replace("ID: ", "").trim());

                        // Lösche die ausgewählte Person anhand der ID
                        String deleteSQL = "DELETE FROM personen WHERE ID = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
                        preparedStatement.setInt(1, id);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Person wurde erfolgreich gelöscht.");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Fehler beim Löschen der Person.");
                        }

                        preparedStatement.close();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Bitte wählen Sie eine Person aus.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Fehler beim Löschen der Person.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Keine Verbindung zur Datenbank.");
        }
    }

    private void openBearbeitungsfenster() {
        int selectedPersonID = getSelectedPersonID();
        PersonData personData = getPersonDataByID(selectedPersonID);

        if (personData != null) {
            JFrame bearbeitenFrame = new JFrame();
            bearbeitenFrame.setTitle("Person bearbeiten");
            bearbeitenFrame.setLayout(new GridLayout(8, 2));

            // Textfelder für die Personendaten
            JTextField vornameField = new JTextField(personData.getVorname());
            JTextField nachnameField = new JTextField(personData.getNachname());
            JComboBox<String> geschlechtComboBox = new JComboBox<>(new String[]{"Männlich", "Weiblich", "GahtDichNütAh"});
            geschlechtComboBox.setSelectedItem(personData.getGeschlecht());
            JTextField geburtsdatumField = new JTextField(personData.getGeburtsdatum());
            JTextField regionField = new JTextField(personData.getRegion());
            JTextField ahvNummerField = new JTextField(personData.getAHVNummer());
            JTextField kinderField = new JTextField(String.valueOf(personData.getKinder()));

            bearbeitenFrame.add(new JLabel("Vorname:"));
            bearbeitenFrame.add(vornameField);
            bearbeitenFrame.add(new JLabel("Nachname:"));
            bearbeitenFrame.add(nachnameField);
            bearbeitenFrame.add(new JLabel("Geschlecht:"));
            bearbeitenFrame.add(geschlechtComboBox);
            bearbeitenFrame.add(new JLabel("Geburtsdatum:"));
            bearbeitenFrame.add(geburtsdatumField);
            bearbeitenFrame.add(new JLabel("Region:"));
            bearbeitenFrame.add(regionField);
            bearbeitenFrame.add(new JLabel("AHV-Nummer:"));
            bearbeitenFrame.add(ahvNummerField);
            bearbeitenFrame.add(new JLabel("Kinder:"));
            bearbeitenFrame.add(kinderField);

            JButton speichernButton = new JButton("Speichern");
            speichernButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Implementieren Sie die Methode, um die bearbeiteten Daten zu speichern
                    updatePersonData(selectedPersonID, vornameField.getText(), nachnameField.getText(), geschlechtComboBox.getSelectedItem().toString(), geburtsdatumField.getText(), regionField.getText(), ahvNummerField.getText(), Integer.parseInt(kinderField.getText()));
                    bearbeitenFrame.dispose();
                }
            });

            bearbeitenFrame.add(speichernButton);
            bearbeitenFrame.pack();
            bearbeitenFrame.setVisible(true);
        }
    }

    private int getSelectedPersonID() {
        // Implementieren Sie diese Methode, um die ID der ausgewählten Person zu erhalten
        // Zum Beispiel aus einer Tabelle oder einem Auswahlfeld
        int selectedID = 0; // Setzen Sie den Standardwert auf 0, wenn keine Person ausgewählt wurde
        return selectedID;
    }

    private PersonData getPersonDataByID(int personID) {
        Connection connection = DatabaseConnection.getConnection(); // Stellen Sie sicher, dass Sie Ihre Datenbankverbindung korrekt erstellen

        try {
            String query = "SELECT * FROM personen WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, personID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String vorname = resultSet.getString("Vorname");
                String nachname = resultSet.getString("Nachname");
                String geschlecht = resultSet.getString("Geschlecht");
                String geburtsdatum = resultSet.getString("Geburtsdatum");
                String region = resultSet.getString("Region");
                String ahvNummer = resultSet.getString("AHV_Nummer");
                int kinder = resultSet.getInt("Kinder");

                return new PersonData(personID, vorname, nachname, geschlecht, geburtsdatum, region, ahvNummer, kinder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Rückgabe von null, wenn keine Daten gefunden wurden oder ein Fehler aufgetreten ist
    }


    private void updatePersonData(int personID, String vorname, String nachname, String geschlecht, String geburtsdatum, String region, String ahvNummer, int kinder) {
        // Implementieren Sie diese Methode, um die Personendaten in der Datenbank zu aktualisieren
        // Verwenden Sie die übergebenen Parameter, um die Daten zu aktualisieren
    }

    public class PersonData {
        private int personID;
        private String vorname;
        private String nachname;
        private String geschlecht;
        private String geburtsdatum;
        private String region;
        private String ahvNummer;
        private int kinder;

        public PersonData(int personID, String vorname, String nachname, String geschlecht, String geburtsdatum, String region, String ahvNummer, int kinder) {
        }

        public String getVorname() {
            return vorname;
        }

        public String getNachname() {
            return nachname;
        }

        public String getGeschlecht() {
            return geschlecht;
        }

        public String getGeburtsdatum() {
            return geburtsdatum;
        }

        public String getRegion() {
            return region;
        }

        public String getAHVNummer() {
            return ahvNummer;
        }

        public int getKinder() {
            return kinder;
        }

        // Getter- und Setter-Methoden für die Attribute
        // ...
    }


    public JFrame getFrame() {
        return frame;
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PersonUI window = new PersonUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
