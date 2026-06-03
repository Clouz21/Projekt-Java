import java.util.ArrayList;
import java.util.Scanner;

// Klasa reprezentujaca ksiazke
class Ksiazka {
    private String tytul;
    private String autor;
    private String isbn;
    private boolean czyWypozyczona;

    public Ksiazka(String tytul, String autor, String isbn) {
        this.tytul = tytul;
        this.autor = autor;
        this.isbn = isbn;
        this.czyWypozyczona = false;
    }

    public String getTytul() { return tytul; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public boolean isCzyWypozyczona() { return czyWypozyczona; }

    public void setCzyWypozyczona(boolean czyWypozyczona) {
        this.czyWypozyczona = czyWypozyczona;
    }

    @Override
    public String toString() {
        String status = czyWypozyczona ? "Wypozyczona" : "Dostepna";
        return "[" + isbn + "] " + tytul + " - " + autor + " (" + status + ")";
    }
}

        // Klasa zarzadzajaca lista ksiazek
        class Biblioteka {
            private ArrayList<Ksiazka> spisKsiazek = new ArrayList<>();

            public void dodajKsiazke(Ksiazka ksiazka) {
                spisKsiazek.add(ksiazka);
                System.out.println("Dodano: " + ksiazka.getTytul());
            }

            public void wyswietlWszystkie() {
                if (spisKsiazek.isEmpty()) {
                    System.out.println("Biblioteka jest pusta.");
                    return;
                }
                for (Ksiazka k : spisKsiazek) {
                    System.out.println(k);
                }
            }

            public void wypozyczKsiazke(String isbn) {
                for (Ksiazka k : spisKsiazek) {
                    if (k.getIsbn().equals(isbn)) {
                        if (k.isCzyWypozyczona()) {
                            System.out.println("Ksiazka jest juz wypozyczona.");
                            return;
                        }
                        k.setCzyWypozyczona(true);
                        System.out.println("Wypozyczono: " + k.getTytul());
                        return;
                    }
                }
                System.out.println("Nie znaleziono ksiazki.");
            }

            public void zwrocKsiazke(String isbn) {
                for (Ksiazka k : spisKsiazek) {
                    if (k.getIsbn().equals(isbn)) {
                        if (!k.isCzyWypozyczona()) {
                            System.out.println("Ta ksiazka nie byla wypozyczona.");
                            return;
                        }
                        k.setCzyWypozyczona(false);
                        System.out.println("Zwrocono: " + k.getTytul());
                        return;
                    }
                }
                System.out.println("Nie znaleziono ksiazki.");
            }
        }

        // klasa uruchamiająxca
        public class Main {
            public static void main(String[] args) {
                Biblioteka biblioteka = new Biblioteka();
                Scanner scanner = new Scanner(System.in);

                // Poczatkowe dane
                biblioteka.dodajKsiazke(new Ksiazka("Cyberiada", "Stanisław Lem", "2137"));
                biblioteka.dodajKsiazke(new Ksiazka("Upiór Opery", "Gaston Leroux", "4200"));
                biblioteka.dodajKsiazke(new Ksiazka("Srebrne Oczy", "Scott Cawthon", "6167"));
                biblioteka.dodajKsiazke(new Ksiazka("Koń z Valony", "Patryk olszowski", "4141"));

                boolean dziala = true;

                while (dziala) {
                    System.out.println("\nMENU:");
                    System.out.println("1. Pokaz ksiazki");
                    System.out.println("2. Dodaj ksiazke");
                    System.out.println("3. Wypozycz ksiazke");
                    System.out.println("4. Zwroc ksiazke");
                    System.out.println("5. Wyjscie");
                    System.out.print("Wybor: ");

                    if (!scanner.hasNextInt()) {
                        System.out.println("Podaj liczbe!");
                        scanner.next();
                        continue;
                    }

                    int wybor = scanner.nextInt();
                    scanner.nextLine();

                    switch (wybor) {
                        case 1:
                            biblioteka.wyswietlWszystkie();
                            break;
                        case 2:
                            System.out.print("Tytul: ");
                            String tytul = scanner.nextLine();
                            System.out.print("Autor: ");
                            String autor = scanner.nextLine();
                            System.out.print("ISBN: ");
                            String isbn = scanner.nextLine();
                            biblioteka.dodajKsiazke(new Ksiazka(tytul, autor, isbn));
                            break;
                        case 3:
                            System.out.print("Podaj ISBN: ");
                            biblioteka.wypozyczKsiazke(scanner.nextLine());
                            break;
                        case 4:
                            System.out.print("Podaj ISBN: ");
                            biblioteka.zwrocKsiazke(scanner.nextLine());
                            break;
                        case 5:
                            dziala = false;
                            System.out.println("Koniec programu.");
                            break;
                        default:
                            System.out.println("Zly wybor.");
                    }
                }
                scanner.close();
            }
        }
