/*
TODO
Odpalić Ajax whoAmI  wewnątrz strony domowej i ujawnić przyciski dla zalogoanych jeśli zalogowany klient jest


<li class="nav-item">
    <a class="nav-link" href="lobby">Gra</a>
</li>


<li class="nav-item mr-auto disabled">
    <a class="nav-link" href="#">Profil</a>
</li>

<a class="nav-link mr-r pl-0 logOutButton" href="/logout">Wyloguj</a>



<div class="navbar-collapse justify-content-md-center collapse" id="navbarsExample08" >
    <ul class="navbar-nav w-100">
    <li class="nav-item ml-lg-auto">
        <a class="nav-link" href="/">Strona Domowa</a>
    </li>
    <li class="nav-item">
        <a class="nav-link active" href="lobby">Gra</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="#">Wsparcie</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="dashboard">Rankingi</a>
    </li>
    <li class="nav-item mr-auto disabled">
        <a class="nav-link" href="#">Profil</a>
    </li>
    </ul>
    <span></span>
    <a class="nav-link mr-r pl-0 logOutButton" href="/logout">Wyloguj</a>
</div>
*/

  
const IndexLogic = (accountInfo_) => {
  
    /*       logic variables          */
    var self = accountInfo_;
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;
  
    /*       logic functions          */
    self.indexLogic = (accountInfo) => {
        console.log("indexLogic");
    
        self.roles = accountInfo.roles?accountInfo.roles:[];

        if (self.authenticated) {
            //wyświetl przycisk od gry na środku ekranu
            $("#indexHelloPlayer").text('Witaj ' + self.nickname + '!')
            $("#indexJoinGame").show();
        } else {
            $("#indexHelloPlayer").text('Dołącz do nas!');
            $("#indexLogin").show();
            //wyświetl przycisk od zalogowania się na środku ekranu
            //nie pokazuj navbara
        }
        //navbar preparation
        NavbarLogic.singleton = NavbarLogic(accountInfo, debug);
    }
  
    /*       event listeners          */
    if ( $("#indexJoinGame").length > 0 )
        $("#indexJoinGame").on("click",()=> {
            window.location.href='/lobby';
        })
    if ( $("#indexJoinGame").length > 0 )
        $("#indexLogin").on("click",()=> {
            window.location.href='/login';
        })
    /*     ajax http actions       */
    
    /*   ajax http requests       */
  
    /*  initalization  */
    self.indexLogic(accountInfo_);
    
    return self;
}
  
IndexLogic.getInstance = (debug = false) => {
  
    if (IndexLogic.singleton)
      return IndexLogic.singleton;
  
    var ajaxReceiveAccountInfo = ( ) => {
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/account/info",
        // url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(accountInfo, textStatus, jqXHR) {
          if (debug){
            console.log("ajaxReceiveAccountInfo success");
            console.log(typeof accountInfo);
            console.log(accountInfo);
            console.log(textStatus);
            console.log(jqXHR);
          }
          if (typeof accountInfo == 'string') //nie zalogowany
            IndexLogic.singleton = IndexLogic({}, debug);
          else //zalogowany
            IndexLogic.singleton = IndexLogic(accountInfo, debug);
          console.log("IndexLogic");
        },
        error: function(jqXHR, status, err) {
          if (debug){
            console.warn("ajaxReceiveAccountInfo error");
          }
        }
      });
    }
    
    ajaxReceiveAccountInfo();
}