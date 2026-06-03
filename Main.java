import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

// Typ wyliczeniowy do gatunkow ksiazek
enum Gatunek {
    FANTASTYKA, SCI_FI, HORROR, KRYMINAL, BIOGRAFIA, NAUKOWA, INNY;

    public static Gatunek zTekstu(String tekst) {
        try {
            return Gatunek.valueOf(tekst.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return INNY;
        }
    }
}

// Klasa do zapisywania co sie dzialo w programie
class Operacja {
    private String typOperacji;
    private String szczegoly;
    private LocalDateTime dataCzas;

    public Operacja(String typOperacji, String szczegoly) {
        this.typOperacji = typOperacji;
        this.szczegoly = szczegoly;
        this.dataCzas = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + dataCzas.format(formatter) + "] " + typOperacji + ": " + szczegoly;
    }
}

// Klasa uzytkownika biblioteki
class Uzytkownik {
    private String ID;
    private String imieInazwisko;
    private double karaFinansowa;
    private boolean czyZablokowany;

    public Uzytkownik(String ID, String imieInazwisko) {
        this.ID = ID;
        this.imieInazwisko = imieInazwisko;
        this.karaFinansowa = 0.0;
        this.czyZablokowany = false;
    }

    public String getID() { return ID; }
    public String getImieInazwisko() { return imieInazwisko; }
    public double getKaraFinansowa() { return karaFinansowa; }
    public boolean isCzyZablokowany() { return czyZablokowany; }

    public void naliczKare(double kwota) {
        this.karaFinansowa += kwota;
        if (this.karaFinansowa > 20.0) {
            this.czyZablokowany = true;
        }
    }

    public void oplacKare() {
        this.karaFinansowa = 0.0;
        this.czyZablokowany = false;
    }

    @Override
    public String toString() {
        String stan = czyZablokowany ? "ZABLOKOWANY (Kara: " + karaFinansowa + " zl)" : "Aktywny (Kara: " + karaFinansowa + " zl)";
        return imieInazwisko + " (ID: " + ID + ") - " + stan;
    }
}

// Klasa ksiazki
class Ksiazka {
    private String tytul;
    private String autor;
    private String isbn;
    private Gatunek gatunek;
    private Uzytkownik ktoWypozyczyl;

    public Ksiazka(String tytul, String autor, String isbn, Gatunek gatunek) {
        this.tytul = tytul;
        this.autor = autor;
        this.isbn = isbn;
        this.gatunek = gatunek;
        this.ktoWypozyczyl = null;
    }

    public String getTytul() { return tytul; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public Gatunek getGatunek() { return gatunek; }
    public Uzytkownik getKtoWypozyczyl() { return ktoWypozyczyl; }

    public void setKtoWypozyczyl(Uzytkownik uzytkownik) {
        this.ktoWypozyczyl = uzytkownik;
    }

    public boolean czyWypozyczona() {
        return ktoWypozyczyl != null;
    }

    @Override
    public String toString() {
        String status = czyWypozyczona() ? "Wypozyczona przez: " + ktoWypozyczyl.getImieInazwisko() : "Dostepna";
        return "[" + isbn + "] " + tytul + " - " + autor + " | Gatunek: " + gatunek + " | " + status;
    }
}

// Klasa glowna biblioteki
class Biblioteka {
    private ArrayList<Ksiazka> spisKsiazek = new ArrayList<>();
    private ArrayList<Uzytkownik> listaUzytkownikow = new ArrayList<>();
    private ArrayList<Operacja> historiaLogow = new ArrayList<>();

    // Metoda pomocnicza do dodawania logow
    private void dodajLog(String typ, String opis) {
        historiaLogow.add(new Operacja(typ, opis));
    }

    public void dodajKsiazke(Ksiazka ksiazka) {
        if (ksiazka.getIsbn().length() < 3) {
            System.out.println("Blad: Za krotki ISBN!");
            return;
        }
        spisKsiazek.add(ksiazka);
        dodajLog("Dodanie", "Dodano ksiazke " + ksiazka.getTytul());
        System.out.println("Dodano ksiazke do biblioteki.");
    }

    public void zarejestrujUzytkownika(Uzytkownik uzytkownik) {
        if (znajdzUzytkownika(uzytkownik.getID()) != null) {
            System.out.println("Blad: Uzytkownik o takim ID juz istnieje!");
            return;
        }
        listaUzytkownikow.add(uzytkownik);
        dodajLog("Rejestracja", "Dodano uzytkownika: " + uzytkownik.getImieInazwisko());
        System.out.println("Zarejestrowano uzytkownika.");
    }

    public void wyswietlWszystkie() {
        if (spisKsiazek.isEmpty()) {
            System.out.println("Biblioteka jest pusta.");
            return;
        }
        System.out.println("\nSpis ksiazek:");
        for (Ksiazka k : spisKsiazek) {
            System.out.println(k);
        }
    }

    public void wyswietlUzytkownikow() {
        if (listaUzytkownikow.isEmpty()) {
            System.out.println("Brak uzytkownikow w bazie.");
            return;
        }
        System.out.println("\nLista uzytkownikow:");
        for (Uzytkownik u : listaUzytkownikow) {
            System.out.println(u);
        }
    }

    public void wyszukajKsiazke(String fraza) {
        System.out.println("\nWyniki wyszukiwania:");
        boolean znaleziono = false;
        for (Ksiazka k : spisKsiazek) {
            if (k.getTytul().toLowerCase().contains(fraza.toLowerCase()) ||
                    k.getAutor().toLowerCase().contains(fraza.toLowerCase())) {
                System.out.println(k);
                znaleziono = true;
            }
        }
        if (!znaleziono) {
            System.out.println("Nie znaleziono nic pasujacego.");
        }
    }

    public void wypozyczKsiazke(String isbn, String idUzytkownika) {
        Uzytkownik czytelnik = znajdzUzytkownika(idUzytkownika);
        if (czytelnik == null) {
            System.out.println("Blad: Nie ma takiego uzytkownika.");
            return;
        }

        if (czytelnik.isCzyZablokowany()) {
            System.out.println("Blad: Konto zablokowane! Trzeba najpierw zaplacic kare: " + czytelnik.getKaraFinansowa() + " zl");
            return;
        }

        for (Ksiazka k : spisKsiazek) {
            if (k.getIsbn().equals(isbn)) {
                if (k.czyWypozyczona()) {
                    System.out.println("Ta ksiazka jest juz wypozyczona.");
                    return;
                }
                k.setKtoWypozyczyl(czytelnik);
                dodajLog("Wypozyczenie", k.getTytul() + " -> " + czytelnik.getImieInazwisko());
                System.out.println("Wypozyczono ksiazke.");
                return;
            }
        }
        System.out.println("Nie ma ksiazki o takim ISBN.");
    }

    public void zwrocKsiazke(String isbn) {
        for (Ksiazka k : spisKsiazek) {
            if (k.getIsbn().equals(isbn)) {
                if (!k.czyWypozyczona()) {
                    System.out.println("Ta ksiazka lezy w bibliotece.");
                    return;
                }
                Uzytkownik u = k.getKtoWypozyczyl();
                dodajLog("Zwrot", k.getTytul() + " <- od " + u.getImieInazwisko());
                k.setKtoWypozyczyl(null);
                System.out.println("Zwrocono ksiazke.");
                return;
            }
        }
        System.out.println("Nie ma ksiazki o takim ISBN.");
    }

    public void symulujUplywCzasu() {
        System.out.println("\nMinal miesiac... Naliczenie kar za przetrzymanie ksiazek.");
        for (Ksiazka k : spisKsiazek) {
            if (k.czyWypozyczona()) {
                k.getKtoWypozyczyl().naliczKare(10.50);
            }
        }
        dodajLog("System", "Naliczono kary za przetrzymanie.");
    }

    public void oplacKaryUzytkownika(String id) {
        Uzytkownik u = znajdzUzytkownika(id);
        if (u != null) {
            dodajLog("Kara", "Uzytkownik " + u.getImieInazwisko() + " zaplacil " + u.getKaraFinansowa() + " zl");
            u.oplacKare();
            System.out.println("Kara zostala oplacona, konto odblokowane.");
        } else {
            System.out.println("Nie znaleziono takiego uzytkownika.");
        }
    }

    public void wyswietlHistorie() {
        System.out.println("\nHistoria operacji w systemie:");
        if (historiaLogow.isEmpty()) {
            System.out.println("Brak wpisow.");
            return;
        }
        for (Operacja o : historiaLogow) {
            System.out.println(o);
        }
    }

    public void wyswietlStatystyki() {
        int razem = spisKsiazek.size();
        int wypozyczone = 0;
        for (Ksiazka k : spisKsiazek) {
            if (k.czyWypozyczona()) {
                wypozyczone++;
            }
        }
        System.out.println("\nStatystyki biblioteki:");
        System.out.println("Wszystkie ksiazki: " + razem);
        System.out.println("Wypozyczone: " + wypozyczone);
        System.out.println("Dostepne: " + (razem - wypozyczone));
        System.out.println("Ilosc uzytkownikow: " + listaUzytkownikow.size());
    }

    private Uzytkownik znajdzUzytkownika(String id) {
        for (Uzytkownik u : listaUzytkownikow) {
            if (u.getID().equalsIgnoreCase(id)) return u;
        }
        return null;
    }
}

// Klasa glowna z menu
public class Main {
    public static void main(String[] args) {
        Biblioteka biblioteka = new Biblioteka();
        Scanner scanner = new Scanner(System.in);

        // Ksiazki na start
        biblioteka.dodajKsiazke(new Ksiazka("Cyberiada", "Stanisław Lem", "2137", Gatunek.SCI_FI));
        biblioteka.dodajKsiazke(new Ksiazka("Upior Opery", "Gaston Leroux", "4200", Gatunek.HORROR));
        biblioteka.dodajKsiazke(new Ksiazka("Srebrne Oczy", "Scott Cawthon", "6167", Gatunek.FANTASTYKA));
        biblioteka.dodajKsiazke(new Ksiazka("Kon z Valony", "Patryk Olszowski", "4141", Gatunek.KRYMINAL));

        // Uzytkownicy na start
        biblioteka.zarejestrujUzytkownika(new Uzytkownik("U1", "Jan Kowalski"));
        biblioteka.zarejestrujUzytkownika(new Uzytkownik("U2", "Anna Nowak"));

        boolean dziala = true;

        while (dziala) {
            System.out.println("\nMENU BIBLIOTEKI:");
            System.out.println("1. Pokaz ksiazki");
            System.out.println("2. Dodaj ksiazke");
            System.out.println("3. Wypozycz ksiazke");
            System.out.println("4. Zwroc ksiazke");
            System.out.println("5. Szukaj ksiazki");
            System.out.println("6. Pokaz uzytkownikow");
            System.out.println("7. Dodaj uzytkownika");
            System.out.println("8. Zaplac kare");
            System.out.println("9. Pokaz historie logow");
            System.out.println("10. Statystyki");
            System.out.println("11. Symulacja czasu (dodaj kary)");
            System.out.println("12. Wyjscie");
            System.out.print("Wybor: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Podaj liczbe od 1 do 12!");
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
                    System.out.print("Gatunek (SCI_FI, HORROR, FANTASTYKA, KRYMINAL, BIOGRAFIA, NAUKOWA): ");
                    Gatunek gatunek = Gatunek.zTekstu(scanner.nextLine());
                    biblioteka.dodajKsiazke(new Ksiazka(tytul, autor, isbn, gatunek));
                    break;
                case 3:
                    System.out.print("ISBN ksiazki: ");
                    String isbnWyp = scanner.nextLine();
                    System.out.print("ID uzytkownika: ");
                    String idWyp = scanner.nextLine();
                    biblioteka.wypozyczKsiazke(isbnWyp, idWyp);
                    break;
                case 4:
                    System.out.print("ISBN ksiazki: ");
                    biblioteka.zwrocKsiazke(scanner.nextLine());
                    break;
                case 5:
                    System.out.print("Szukana fraza: ");
                    biblioteka.wyszukajKsiazke(scanner.nextLine());
                    break;
                case 6:
                    biblioteka.wyswietlUzytkownikow();
                    break;
                case 7:
                    System.out.print("ID: ");
                    String nowyId = scanner.nextLine();
                    System.out.print("Imie i nazwisko: ");
                    String noweDane = scanner.nextLine();
                    biblioteka.zarejestrujUzytkownika(new Uzytkownik(nowyId, noweDane));
                    break;
                case 8:
                    System.out.print("ID uzytkownika: ");
                    biblioteka.oplacKaryUzytkownika(scanner.nextLine());
                    break;
                case 9:
                    biblioteka.wyswietlHistorie();
                    break;
                case 10:
                    biblioteka.wyswietlStatystyki();
                    break;
                case 11:
                    biblioteka.symulujUplywCzasu();
                    break;
                case 12:
                    dziala = false;
                    System.out.println("Koniec programu.");
                    break;
                default:
                    System.out.println("Zly wybor. Wybierz cos od 1 do 12.");
            }
        }
        scanner.close();
    }
}
