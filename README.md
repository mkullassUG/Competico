<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/mkullassUG/Competico">
    <img src="src/main/resources/static/assets/myIcons/CompeticoLogo.svg" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Competico</h3>

  <p align="center">
    An app for teachers who want to teach language more creatively!
    <br />
    <a href="https://github.com/mkullassUG/Competico/tree/master/dokumentacja"><strong>Explore the docs »</strong></a>
    <br />
    <br />
   </p>
</div>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project
Compatico jest aplikacją przeglądarkową, zbudowaną z myślą o lektorach i uczniach języka angielskiego.

Celem aplikacji jest wspieranie użytkowników w nauce języka anielskiego poprzez dobieranie zadań zgodnie z obecną znajomością języka i przeprowadzanie gier e-learningowych ułatwiających wzbogacanie słownictwa oraz poprawiających umiejętność czytania ze zrozumieniem. 

Daje ona lektorom łatwy dostęp do tworzenia interaktywnych ćwiczeń, dołączania uczniów do grup i przypisywania ich pod wcześniej wymyślone zadania do wspólnego rozwiązywania.
Gracze mogą rywalizować ze sobą o miejsce w globalnym rankingu przez rozwiązywanie losowo wybranych z puli zadań, losowanych pod ich obecny poziom wiedzy.

z aplikacji można korzystać zarówno na komputerach stacjonarnych jak i urządzeniach mobilnych.

Jstnieją inne dobre aplikacje od nauki języka angielskiego (Kahoot, Moodle), jednak nie łączą one w sobie zapamiętywania poziomu umiejętności gracza, rywalizacji o miejsce w globalnym rankingu oraz swobodnego prowadzenia zajęć dla grup uczniów pod nadzorem lektora.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With
Główne frameworki/biblioteki użyte w budowie projektu:

* [![Spring][Spring.io]][Spring-url]
* [![Thymeleaf][thymeleaf.org]][Thymeleaf-url]
* [![Bootstrap][getbootstrap.com]][Bootstrap-url]
* [![PostgreSQL][postgresql.org]][PostgreSQL-url]
* [![JQuery][JQuery.com]][JQuery-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started
Niżej opisane są niezbędne wymagania oraz kroki do uruchomienia aplikacji na własnej maszynie.

### Prerequisites
Dla minimalnej konfiguracji można użyć systemu Windows 10 z wcześniej zainstalowaną bazą danych PostgreSQL wersji 13 i środowiskiem Java w wersji 11+.

Należy przygotować serwer SMTP, który używany będzie do potwierdzania zakładanych kont graczy i lektorów. Do minimalnej konfiguracji polacany jest gmail.
Na obecną chwilę, gdy pisane jest to README, aby poprawnie połączyć serwer SMTP gmail'a z Competico należy na koncie google włączyć 2-etapową weryfikację oraz utworzyć 16-cyfrowy token dostępu który będzie używany przez Competico jako hasło do logowania się.

Opcjonalnym jest dodanie certyfikatu SSL do łączenia się z serwerem przez protokół HTTPS. 

### Installation
Aplikacja ma strukturę klient-server a całą jej konfiguracje dokonujemy wewnątrz pliku application.properties znajdującego się pod ścieżką: "\src\main\resources".

Zalecane jest aby ustawiać wartości konfiguracyjne poprzez zmienne środowiskowe.

Konfiguracja składa się z kilku części:


* PostgreSQL
```sh
  COMPETICO_DATABASE:
  Stwórz baze danych i użytą nazwę ustaw pod zmienną środowiskową COMPETICO_DATABASE

  POSTGRES_PASS: 
  Hasło administratora bazy danych ustaw pod zmienną POSTGRES_PASS

  Zmień wartość własności "spring.jpa.hibernate.ddl-auto" na "create" przed uruchomieniem ją po raz pierwszy. 
  Przed ponownym uruchomieniem aplikacji ustaw spowrotem na "validate", w przeciwnym wypadku zawartość bazy danych zostanie usunięta.

  ```

* SMTP
```sh
  Pod zmiennymi środowiskowymi EMAIL_USER i EMAIL_PASS ustaw dane logowania dla wybranego serwisu maila
  Pozostałe własności pod nazwami "spring.mail" należy ustawić analogicznie według podanych zaleceń używanego serwisu SMTP
  ```
* Domain
```sh
  Pod zmienną SERVER_URL należy podać własną domenę, pod którą widoczny będzie serwer HTTP
  ```

* SSL (Opcjonalne)
```sh
  Aby serwer był widoczny pod protokołem HTTPS, należy:
  -Umieścić certyfikat SSL pod ścieżką "\src\main\resources\keystore"
  -Token certyfikatu umieścić pod zmienną środowiskową KEYSTORE_PASS
  -W application.properties odpowiednio (postępując według komentarzy podanych w pliku) zakomentować i odkomentować odpowiednie wartości 
  ```

Zalecane jest aby aplikacja Spring'a została spakowana przez Maven'a, (odpowiedni plik pom.xml znajduje się w katalogu głównym) 
następnie servery PostgreSQL i aplikacji umieszczone zostały na kontenerach np. używając Docker'a

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage
Po odwiedzeniu strony po raz pierwszy należy stworzyć konto z rolą lektora albo gracza.
Zależnie od wybranej roli konta, użytkownicy będą różnić się w dostępie do funkcjonalności aplikacji.
Rola Lektora pozwala na:
  - Tworzenie grup lektorskich i dodawanie do nich graczy (zakładka Grupy)
  - Tworzenie zadań (zakładka Task Manager)
  - Organizowanie gier grupowych, w których będa uczestniczyć dodani gracze
Rola gracza pozwala na:
  - Branie udziału w organizowanych przez lektora grach grupowych
  - Rywalizowanie w publicznych rozgrywkach, za które przypisywane są punkty i widoczne w globalnym rankingu graczy

Zarówno gry globalne, jak i grupowe składają się z trzech faz – lobby, rozgrywki oraz tablicy wyników.

Gracze po wejściu w zakładkę "Gra" mają wybór uczestniczenia w grze globalnej, mogą stworzyć własne lobby publiczne lub prywatne (do którego można dołączyć wyłącznie przez wygenerowany kod lobby) lub znaleźć istniejące lobby z innymi graczami. Do gier z przyjaciółmi mogą dołączać za pomocą kodów.

Gry grupowe mogą być stworzone przez lektora pod zakładką "Grupy". Z tego pola lektor zarządza swoimi grupami. Każda grupa posiada kod który gracze mogą użyć do dołączenia. 
Po stworzeniu lobby, automatycznie zostanie rozesłany komunikat do wszystkich członków grupy z opcją dołącznia do gry.
Lektor może zarządzać danym lobby, np. przez wybranie zestawów zadań, które zostaną użyte podczas gry.

Gdy lektor lub gracz (zależnie od danego lobby) rozpoczną grę zostaną przeniesieni do widoku rozgrywki z pierwszym zadaniem z puli zadań.

Gdy zostanie oddane ostatnie zadanie, zostaną przeniesieni na widok tablicy wyników. Na tym etapie gra się zakończyła i możliwe jest przejrzenie uzyskanych punktów za poszczególne zadania.

<!-- ROADMAP -->
## Roadmap

- [...]
- [x] Potwierdzanie adresu email konta
- [x] Dodanie możliwości wysyłania komunikatów/wiadomości w grupach lektorskich
- [ ] Dodanie zadań z obrazkami (file server)
- [ ] Czat z lektorem podczas prowadzenia gry grupowej
- [ ] Customizacja profilu użytkownika 
    - [ ] Ustawianie obrazków profilowych (file server)
    - [ ] Wyświetlanie osiągnięć, gwiazdek danych przez lektorów lub liczbe rozegranych gier
- [ ] Liczba polubień danych zestawów zadań (Możliwość oceniania zestawów zadań przez graczy)

<!-- CONTRIBUTING -->
## Creators
mkullass
* [![mkullassUG][https://github.com/mkullassUG]][mkullassUG-url]
GhillieWolf
* [![GhillieWolf][github.com/GhillieWolf]][GhillieWolf-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE 
## License
<p align="right">(<a href="#readme-top">back to top</a>)</p>
-->






<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[Spring.io]: https://img.shields.io/badge/Spring-white?style=for-the-badge&logo=spring&logoColor=67aa3c
[Spring-url]: https://spring.io
[thymeleaf.org]: https://img.shields.io/badge/Thymeleaf-white?style=for-the-badge&logo=thymeleaf&logoColor=005f0f
[Thymeleaf-url]: https://www.thymeleaf.org
[getbootstrap.com]: https://img.shields.io/badge/Bootstrap-6d2cf0?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[postgresql.org]: https://img.shields.io/badge/Spring-white?style=for-the-badge&logo=spring&logoColor=67aa3c
[PostgreSQL-url]: https://www.postgresql.org
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com 
