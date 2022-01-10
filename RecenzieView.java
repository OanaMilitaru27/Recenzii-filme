package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.Film;
import model.Recenzie;

/**
 * Frame ul ce o sa contina tabelul cu recenzii si restul componentelor pt adaugare/editare recenzie
 */
public class RecenzieView extends JFrame {
    private JTextField jTextFieldTitlu;
    private JTextField jTextFieldComentariu;
    private JTextField jTextFieldNota;
    private JTextField jTextFieldData;
    private JTable tableRecenzii;
    private JScrollPane scrollPane;

    private JButton adaugaRecenzie;
    private JButton editeazaRecenzie;
    private JButton curataTextul;
    private DefaultTableModel model;
    private List<Film> toateFilmeleDisponibile;
    private Film filmSelectat;

    public RecenzieView(List<Film> toateFilmeleDisponibile, Film filmSelectat) {
        this.toateFilmeleDisponibile = toateFilmeleDisponibile;
        this.filmSelectat = filmSelectat;
        initializare();
    }

    /**
     * Initialize the contents of the Frame.
     */
    private void initializare() {
        this.setBounds(100, 100, 800, 600);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.getContentPane().setLayout(null);
        this.setVisible(true);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(176, 196, 222));
        panel.setBounds(0, 0, 790, 790);
        this.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel titlu = new JLabel("Titlu:");
        titlu.setBounds(21, 83, 90, 14);
        panel.add(titlu);

        JLabel comentatiu = new JLabel("Comentariu:");
        comentatiu.setBounds(21, 105, 90, 14);
        panel.add(comentatiu);

        JLabel nota = new JLabel("Nota:");
        nota.setBounds(21, 127, 90, 14);
        panel.add(nota);

        JLabel data = new JLabel("Data:");
        data.setBounds(21, 148, 90, 14);
        panel.add(data);

        jTextFieldTitlu = new JTextField();
        jTextFieldTitlu.setColumns(10);
        jTextFieldTitlu.setBounds(100, 81, 132, 17);
        panel.add(jTextFieldTitlu);

        jTextFieldComentariu = new JTextField();
        jTextFieldComentariu.setColumns(10);
        jTextFieldComentariu.setBounds(100, 102, 132, 17);
        panel.add(jTextFieldComentariu);

        jTextFieldNota = new JTextField();
        jTextFieldNota.setColumns(10);
        jTextFieldNota.setBounds(100, 124, 132, 17);
        panel.add(jTextFieldNota);

        jTextFieldData = new JTextField();
        jTextFieldData.setColumns(10);
        jTextFieldData.setBounds(100, 145, 132, 17);
        panel.add(jTextFieldData);

        scrollPane = new JScrollPane();
        scrollPane.setBounds(250, 48, 520, 500);
        panel.add(scrollPane);

        tableRecenzii = new JTable();

        // setam culoare de background pt tabelul cu recenzii
        tableRecenzii.setBackground(new Color(176, 196, 222));
        // definim coloanele ce o sa fie folosite in tabelul cu recenzii
        Object[] columns = {"Titlu", "Comentariu", "Nota", "Data"};

        // initializam modelul tabelului cu recenzii
        model = new DefaultTableModel();
        // setam coloanele tabelului cu recenzii
        model.setColumnIdentifiers(columns);

        // populeaza tabelul cu recenziile pt filmul selectat
        populeazaModelulTabeluluiCuRecenzii(filmSelectat);
        tableRecenzii.setModel(model);

        // defieste functionalitatea de sortare ascendenta pt coloana Nota din tabelul cu recenzii
        TableRowSorter<TableModel> sorter = new TableRowSorter(tableRecenzii.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(1);
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        tableRecenzii.setRowSorter(sorter);

        // prindem evenimentul de mouse click, ca sa putem edita recenzia
        tableRecenzii.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int indexulRanduluiSelectat = tableRecenzii.getSelectedRow();
                jTextFieldTitlu.setText(model.getValueAt(indexulRanduluiSelectat, 0).toString());
                jTextFieldComentariu.setText(model.getValueAt(indexulRanduluiSelectat, 1).toString());
                jTextFieldNota.setText(model.getValueAt(indexulRanduluiSelectat, 2).toString());
                jTextFieldData.setText(model.getValueAt(indexulRanduluiSelectat, 3).toString());
            }
        });

        scrollPane.setViewportView(tableRecenzii);

        adaugaRecenzie = new JButton();
        adaugaRecenzie.addActionListener(e -> {
            // valideaza adaugarea unei noi recenzii
            if (jTextFieldTitlu.getText().equals("") ||
                    jTextFieldComentariu.getText().equals("") ||
                    jTextFieldNota.getText().equals("") ||
                    jTextFieldData.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Toate fieldurile sunt obligatorii!");
            } else {
                // valideaza daca nota este un numar mai mare ca 0 si daca data este in formatul corect
                if (isNotaANumber(jTextFieldNota.getText()) && isValidData(jTextFieldData.getText())) {
                    Object[] row = new Object[4];
                    row[0] = jTextFieldTitlu.getText();
                    row[1] = jTextFieldComentariu.getText();
                    row[2] = jTextFieldNota.getText();
                    row[3] = jTextFieldData.getText();

                    // adauga noua recenzie in tabelul cu recenzii
                    model.addRow(row);

                    // adauga noua recenzia la filmul selectat din lista de filme
                    int indexulFilmuluiSelectadDinListaDeFilme = toateFilmeleDisponibile.indexOf(filmSelectat);
                    toateFilmeleDisponibile.get(indexulFilmuluiSelectadDinListaDeFilme)
                            .getListaRecenzii()
                            .add(new Recenzie(jTextFieldTitlu.getText(),
                                    Double.valueOf(jTextFieldNota.getText()),
                                    jTextFieldComentariu.getText(),
                                    LocalDate.parse(jTextFieldData.getText(), DateTimeFormatter.ISO_LOCAL_DATE)));

                    // scriem din nou toate filmele si recenziile in fisier ca sa salvam si recenzia recent adaugata
                    scrieInFiserListaDeFilmeCuRecenziileAferenteFiecaruiFilm(toateFilmeleDisponibile);

                    // reseteaza texfield urile ca sa putem adauga o noua recenzie
                    jTextFieldTitlu.setText("");
                    jTextFieldComentariu.setText("");
                    jTextFieldNota.setText("");
                    jTextFieldData.setText("");
                } else {
                    // afiseaza un mesaj sa instiintam utilizatorul ca datele introduse nu sunt in formatul corect
                    JOptionPane.showMessageDialog(null, "Nota trebuie sa fie un numar decimal mai mare ca 0, iar data trebuie sa fie in formatul yyyy-MM-dd!");
                }
            }
        });
        adaugaRecenzie.setBounds(21, 280, 80, 50);
        adaugaRecenzie.setText("Adauga");
        panel.add(adaugaRecenzie);

        // crearea butonului de editeare recenzie
        editeazaRecenzie = new JButton();
        editeazaRecenzie.addActionListener(e -> {
            // propaga schimbarile recenziei editate in tableul cu recenzii
            int indexulRanduluiSelectat = tableRecenzii.getSelectedRow();
            model.setValueAt(jTextFieldTitlu.getText(), indexulRanduluiSelectat, 0);
            model.setValueAt(jTextFieldComentariu.getText(), indexulRanduluiSelectat, 1);
            model.setValueAt(jTextFieldNota.getText(), indexulRanduluiSelectat, 2);
            model.setValueAt(jTextFieldData.getText(), indexulRanduluiSelectat, 3);

        });
        editeazaRecenzie.setText("Editeaza");
        editeazaRecenzie.setBounds(133, 280, 80, 50);
        // adaugam butonul de editare recenzie in panel
        panel.add(editeazaRecenzie);

        curataTextul = new JButton();
        curataTextul.addActionListener(e -> {
            // reseteaza texfield urile
            jTextFieldTitlu.setText("");
            jTextFieldComentariu.setText("");
            jTextFieldNota.setText("");
            jTextFieldData.setText("");

        });
        curataTextul.setText("Reseteaza");
        curataTextul.setBounds(90, 340, 80, 50);
        // adaugam butonul de resetare recenzie in panel
        panel.add(curataTextul);

    }

    /**
     * Valideaza ca nota introdusa este un numar mai mare decat 0
     */
    private boolean isNotaANumber(String notaAsString) {
        try {
            double nota = Double.parseDouble(notaAsString);
            if (nota <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Valideaza ca data introdusa este in formatul corect
     */
    public boolean isValidData(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Populam modelul tabelului co toate recenziile disponibile ca sa afisam recenziile in tabel.
     */
    private void populeazaModelulTabeluluiCuRecenzii(Film filmSelectat) {
        for (Recenzie recenzie : filmSelectat.getListaRecenzii()) {
            Object[] row = new Object[4]; //array cu 4 elem
            row[0] = recenzie.getTitlu();
            row[1] = recenzie.getComentariu();
            row[2] = recenzie.getNota();
            row[3] = recenzie.getData();
            model.addRow(row);
        }
    }

    /***
     * Daca selectam un nou film trebuie sa repopulam tabelul cu recenziile aferente noului film selectat.
     */
    public void populeazaRecenziileInFunctieDeFilmulSelectat(Film filmSelectat) {
        // stergem toate datele din tabelul cu recenzii
        model.setRowCount(0);
        // repopulam tabelul cu recenziile necesare pt noul film selectat.
        populeazaModelulTabeluluiCuRecenzii(filmSelectat);
        // setam noul filmul selectat
        this.filmSelectat = filmSelectat;
    }

    /**
     * Scriem in fisier lista cu filme si cu noile recenzii adaugate
     */
    public void scrieInFiserListaDeFilmeCuRecenziileAferenteFiecaruiFilm(List<Film> listaCuFilmeleDisponibile) {
        FileWriter fisierulNouCreat = null;
        try {
            fisierulNouCreat = new FileWriter("C:\\Users\\oanam\\Downloads\\recenzii-filme\\recenzii-filme\\filme-recenzii.txt", false);
            for (Film film : listaCuFilmeleDisponibile) {
                String filmulCeTrebueScrisInFisier = film.getTitlulFilmului() + "|" + film.getAnulAparitiei();
                // scriem detaliile despre film in fisier, separate de caracterul "|"
                fisierulNouCreat.write(filmulCeTrebueScrisInFisier);
                fisierulNouCreat.write("\n");

                for (Recenzie recenzie : film.getListaRecenzii()) {
                    // scriem detaliile despre recenzie in fisier, separate de caracterul "|"
                    String recenziaCeTrebuieScrisaInFisier = recenzie.getTitlu() + "|" + recenzie.getNota() + "|" + recenzie.getComentariu() + "|" + recenzie.getData().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    fisierulNouCreat.write(recenziaCeTrebuieScrisaInFisier);
                    fisierulNouCreat.write("\n");
                }
            }

            fisierulNouCreat.close();
        }
        catch (Exception e) {
            if (fisierulNouCreat != null) {
                try {
                    fisierulNouCreat.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
