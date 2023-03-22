<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/mkullassUG/Competico">
    <img src="src/main/resources/static/assets/myIcons/CompeticoLogo.svg" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Competico</h3>

  <p align="center">
    Aplikacja dla nauczycieli i uczniów, którzy chcą kreatywnie uczyć się języka angielskiego!
    <br />
    <a href="./dokumentacja"><strong>Explore the docs »</strong></a>
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

Z aplikacji można korzystać zarówno na komputerach stacjonarnych jak i urządzeniach mobilnych.

Istnieją inne dobre aplikacje od nauki języka angielskiego, takie jak "Kahoot" lub "Moodle", ale nie łączą one zapamiętywania poziomu umiejętności gracza, rywalizacji o miejsce w globalnym rankingu oraz swobodnego prowadzenia zajęć dla grup uczniów pod nadzorem lektora.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With
Główne aplikacje, frameworki i biblioteki użyte w budowie projektu:

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
Dla minimalnej konfiguracji można użyć systemu Windows 10, z wcześniej zainstalowaną bazą danych PostgreSQL w wersji 13 i środowiskiem Java w wersji 11+.

Należy przygotować serwer SMTP, który używany będzie do potwierdzania zakładanych kont graczy i lektorów. Do minimalnej konfiguracji można przykładowo użyć serwisu Gmail.
Na czas pisania Readme, aby poprawnie połączyć serwer SMTP Gmail'a z Competico należy na koncie Google włączyć 2-etapową weryfikację oraz utworzyć 16-cyfrowy token dostępu, który będzie używany przez Competico do uwierzytelniania połączenia z zewnętrznym serwisem.

Opcjonalnym jest dodanie certyfikatu SSL do łączenia się z serwerem przez protokół HTTPS. 

### Installation
Aplikacja ma strukturę klient-server a całą jej konfiguracje dokonujemy wewnątrz pliku application.properties znajdującego się pod ścieżką: "\src\main\resources".

Zalecane jest aby ustawiać wartości konfiguracyjne poprzez zmienne środowiskowe.

Konfiguracja składa się z kilku części:


* PostgreSQL setup
```sh
	[...]
	spring.datasource.url=jdbc:postgresql://localhost:5432/${COMPETICO_DATABASE:teamproj}
	spring.datasource.username=postgres
	spring.datasource.password=${POSTGRES_PASS}
	[...]
	#spring.jpa.hibernate.ddl-auto=create
	spring.jpa.hibernate.ddl-auto=validate
	[...]

  Stworzyć baze danych (np. przez graficzny interfejs narzędzia pgAdmin), jej nazwę ustawić pod zmienną środowiskową COMPETICO_DATABASE.

  Poniżej należy ustawić dane logowania administratora bazy danych. 
  Analogicznie obok "spring.datasource.username=" podać nazwę użytkownika i pod zmienną środowiskową POSTGRES_PASS należy ustawić hasło 

  Przy pierwszym uruchamianiu aplikacji, ustawić "spring.jpa.hibernate.ddl-auto" na "create". 
  Przed ponownym uruchomieniem aplikacji, ustawić spowrotem na "validate". 
  Pominięcie tego kroku będzie skutkować przywracaniem startowych wartości oraz usuwaniem nowych z bazy danych, przy każdym ponownym uruchomieniu aplikacji.

  ```

* SMTP
```sh
	spring.mail.host=smtp.gmail.com
	spring.mail.username=${EMAIL_USER} 
	spring.mail.password=${EMAIL_PASS}
	spring.mail.port=587
	spring.mail.properties.mail.smtp.auth=true
	spring.mail.properties.mail.smtp.starttls.enable=true

  Pod zmiennymi środowiskowymi EMAIL_USER i EMAIL_PASS, 
  ustawić dane logowania dla wybranego serwisu maila
  
  Pozostałe własności, pod nazwami "spring.mail", 
  należy ustawić analogicznie, 
  według podanych zaleceń wybranego serwisu SMTP
  ```
* Domain
```sh
	[...]
	app.url=${SERVER_URL:localhost}
	[...]
	
  Pod zmienną SERVER_URL należy podać własną domenę, 
  pod którą widoczny będzie serwer HTTP
  ```

* SSL (Opcjonalne)
```sh
  Aby serwer był widoczny pod protokołem HTTPS, należy:
  -Umieścić certyfikat SSL pod ścieżką "\src\main\resources\keystore"
  -Token certyfikatu umieścić pod zmienną środowiskową KEYSTORE_PASS
  -W application.properties odpowiednio, zakomentować i odkomentować odpowiednie pola,
	kierując się wskazówkami z komentarzy
  -Konfiguracja z przykładową nazwą certyfikatu "teamproj.p12": 
  
	#dla wylaczonego ssl:
	server.port=80
	#dla wlaczonego ssl:
	#server.port=443
	#http.port=80
	#server.ssl.key-store-type=PKCS12
	#server.ssl.key-store=classpath:keystore/teamproj.p12
	#server.ssl.key-store-password=${KEYSTORE_PASS}
	#server.ssl.key-alias=teamproj
	#server.ssl.enabled=true
  ```

Zalecane jest aby aplikacja Spring'a została spakowana przez Maven'a, (odpowiedni plik pom.xml znajduje się w katalogu głównym). 
Następnie aby servery PostgreSQL i aplikacji umieszczone zostały na kontenerach np. używając Docker'a.

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

###Rozgrywka
Zarówno gry globalne, jak i grupowe składają się z trzech faz – lobby, rozgrywki oraz tablicy wyników.

Gracze po wejściu w zakładkę "Gra" mają wybór uczestniczenia w grze globalnej, mogą stworzyć własne lobby publiczne lub prywatne (do którego można dołączyć wyłącznie przez wygenerowany kod lobby) lub znaleźć istniejące lobby z innymi graczami. Do gier z przyjaciółmi mogą dołączać za pomocą kodów.

Gry grupowe mogą być stworzone przez lektora pod zakładką "Grupy". Z tego pola lektor zarządza swoimi grupami. Każda grupa posiada kod który gracze mogą użyć do dołączenia. 
Po stworzeniu lobby, automatycznie zostanie rozesłany komunikat do wszystkich członków grupy z opcją dołącznia do gry.
Lektor może zarządzać danym lobby, np. przez wybranie zestawów zadań, które zostaną użyte podczas gry.

Gdy lektor lub gracz (zależnie od danego lobby) rozpoczną grę zostaną przeniesieni do widoku rozgrywki z pierwszym zadaniem z puli zadań.

Gdy zostanie oddane ostatnie zadanie, zostaną przeniesieni na widok tablicy wyników. Na tym etapie gra się zakończyła i możliwe jest przejrzenie uzyskanych punktów za poszczególne zadania.

###Tworzenie Zadań
Dla użytkowników z rolą lektora, dodatkowo widnieje zakładka Task Manager. Jest to przycisk dropdown z listą różnych szablonów zadań. Po wybraniu odpowiedniego szablonu, użytkownik zostanie przeniesiony do widoku tworzenia i zarządzania swoimi zadaniami.

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

* [![mkullassUG][mkullassUG-shield]][mkullassUG-url] 
* [![GhillieWolf][GhillieWolf-shield]][GhillieWolf-url] 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE 
## License
<p align="right">(<a href="#readme-top">back to top</a>)</p>
-->



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[Spring.io]: https://img.shields.io/badge/Spring-white?style=for-the-badge&logo=spring&logoColor=67aa3c
[Spring-url]: https://spring.io
[thymeleaf.org]: https://img.shields.io/badge/Thymeleaf-005f0f?style=for-the-badge&logo=thymeleaf&logoColor=white
[Thymeleaf-url]: https://www.thymeleaf.org
[getbootstrap.com]: https://img.shields.io/badge/Bootstrap-6d2cf0?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[postgresql.org]: https://img.shields.io/badge/postgresql-30628a?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com 

[mkullassUG-shield]: https://img.shields.io/badge/mkullass-161b22?style=for-the-badge&logo=github&logoColor=white
[mkullassUG-url]: https://github.com/mkullassUG
[GhillieWolf-shield]: https://img.shields.io/badge/GhillieWolf-161b22?style=for-the-badge&logo=github&logoColor=white
[GhillieWolf-url]: https://github.com/GhillieWolf