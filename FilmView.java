package view;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.*;

import model.Film;
import model.Recenzie;

/**
 * Frame ul ce o sa contina lista cu filmele disponibile si un ScrollPane sa putem scrolla daca sunt pre multe filme in lista
 */
public class FilmView extends JFrame {
    private RecenzieView recenzieView;
    private List<Film> toateFilmeleDisponibile;

    public FilmView() {
        // declaram JList ul ce o sa contina detaliile despre film (titlul si anul)
        JList<Film> filmJList = new JList<>();
        // Punem lista cu filme intru un ScrollPane sa putem scrolla daca sunt pre multe filme in lista
        JScrollPane scrollPaneFilme = new JScrollPane(filmJList);

        filmJList.addListSelectionListener(e -> {
            Film filmSelectat = filmJList.getSelectedValue();
            if (recenzieView == null) {
                // creaza frame ul cu recenziile filmului selectat
                recenzieView = new RecenzieView(toateFilmeleDisponibile, filmSelectat);
            } else {
                recenzieView.setVisible(true);
                // populam recenziile din tabela de recenzii in functie de filmul selectat
                recenzieView.populeazaRecenziileInFunctieDeFilmulSelectat(filmSelectat);
            }
        });
        //folosind constructorul fara argumente al clasei DefaultListModel si
        //adaugand apoi elemente pentru acest model:
        DefaultListModel<Film> model = new DefaultListModel<>();
        filmJList.setModel(model);
        // populeaza modelul pentru filmJList cu filmele disponibile
        toateFilmeleDisponibile = creeazaListaDeFilmeCuRecenziileAferenteFiecaruiFilm();
        model.addAll(toateFilmeleDisponibile);
        filmJList.setBackground(Color.CYAN);

        // set the jframe height and width
        this.setPreferredSize(new Dimension(210, 310));

        this.add(scrollPaneFilme);//adaugam filme
        this.pack(); //marime frame

        this.setLocationRelativeTo(null);//locatia frame ului
        this.setVisible(true); //face vizibil frame ul
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); //iesirea din aplicatie
    }

    /**
     * Dupa citirea fisierului de filme si recenzii cream obiectele pentru Filme si atasam lista recenziile aferente fiecarui film
     */
    public List<Film> creeazaListaDeFilmeCuRecenziileAferenteFiecaruiFilm() {
        List<Film> listaCuFilmeleDisponibile = new ArrayList<>();

        // fiecare linie poate contine detalii despre film sau despre recenzia filmului
        List<String> filmeCuRecenzii = citesteTotFisierulLinieCuLinie();
        // iteram peste toalte liniile si impartim fiecare linie din fisier dupa delimitatorul "|" ca sa vedem
        // daca linia contine detalii despre film sau despre recenzie
        filmeCuRecenzii.forEach(line -> {
            String[] lineSplited = line.split(Pattern.quote("|"));
            //daca avem doar 2 elemente in linia citita inseamna ca detaliile sunt depre film
            if (lineSplited.length == 2) {
                Film film = new Film(lineSplited[0], lineSplited[1], new ArrayList<>());
                listaCuFilmeleDisponibile.add(film);
            }
            //daca avem doar 4 elemente in linia citita inseamna ca detaliile sunt recenzia filmului
            if (lineSplited.length == 4) {
                // luam ultimul film din lista de filme deja creeata si ii atasam recenzia
                Film ultimulFilmDinLista = listaCuFilmeleDisponibile.get(listaCuFilmeleDisponibile.size() - 1);
                Recenzie recenzie = new Recenzie(lineSplited[0], Double.valueOf(lineSplited[1]), lineSplited[2],
                        LocalDate.parse(lineSplited[3], DateTimeFormatter.ISO_LOCAL_DATE));
                //adauga recenzia la filmul aferent ei
                ultimulFilmDinLista.getListaRecenzii().add(recenzie);
            }
        });

        // returnam lista cu filme disponibile. fiecarui film are atasata si lista cu recenziile exisente
        return listaCuFilmeleDisponibile;
    }

    /**
     * Citim continutul fisierului linie cu linie si il returnam ca o lista de String uri
     */
    public List<String> citesteTotFisierulLinieCuLinie() {
        try {
            File fisier = new File("C:\\Users\\oanam\\Downloads\\recenzii-filme\\recenzii-filme\\filme-recenzii.txt");
            Scanner scanner = new Scanner(fisier);

            List<String> savedText = new ArrayList<>();

            while (scanner.hasNextLine()) {
                savedText.add(scanner.nextLine());
            }

            return savedText;
        } catch (Exception e) {
            System.out.println("Fisierul filme-recenzii nu a putut fi citit");
            return new ArrayList();
        }
    }
}
