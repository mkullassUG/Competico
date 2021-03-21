
const LobbyJoinLogic = (debug = false) => {
  var self = {}
  var nickname, gameCode, logged, isHost, gameStarted;
  var LobbyJoinInit = (data) => {

    
    isHost = data.isHost;
    gameCode = data.gameCode;
    logged = data.logged;
    gameStarted = data.gameStarted;
    nickname = data.nickname;

    /* 
    ///ustaliliśmy, że tego nie wysyłamy, bo itak musze być zalogowany żeby to wysłać?
    if (!data.logged)
      window.location = "/login"; 
    */

    if ((data.isHost != null && data.isHost) || 
      (data.gameCode != null && data.gameCode != ""))
      window.location = "/game/" + data.gameCode;

      /*
        TODO
        nie zdarzyłem napisac wstawiania nicku gracza do navbara, najpierw potrzeba wziąc navbar ze strony np dashboard któr yzdaje się miał pole nickname po prawej stronie
      */

      $('.hideBeforeLoad').css("display", "flex").fadeIn();
  }

  var gameFound = (data) => {

    if (data)
      window.location = "game/" + data;
    else {
      alert("Nie znaleziono żadnych gier");
    }
  }

  var isCodeCorrect = (data) => {
    /*TODO
      -w przyszłości dojdzie isPublic.
      -dodac modala który da info o lobby z przyciskami dołączenia/ anulowania i może info czyje to lobby (pobierane przez innego endpointa)
    */
    if (data.exists)
      if (!data.isFull)
        window.location = "game/" + $("#inputCode")[0].value;
      else
        alert("lobby pełne");
    else
      alert("kod nie prawidłowy");
  }

  var lobbyCreated = (code) => window.location = "/game/" + code;
  /*Ajax*/
  var ajaxReceiveWhoAmI = () => {

    $('.hideBeforeLoadModal').modal('show');
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/playerinfo",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug) {
          console.log("ajaxReceiveWhoAmI success");
          console.log(data);
        }
        
        $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
        LobbyJoinInit(data);
      },
      error: function(jqXHR, status, err) {
        if (debug)
          console.warn("ajaxReceiveWhoAmI error");
        
        $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
      }
    });
  }
  
  var ajaxReceiveFoundGame = () => {
    $('.hideBeforeLoadModal').modal('show');
    /*
    odpowiada 404 jesli nie znjadzie lobby żadneg ow danej chwili więcwyskakuje erro w konsoli, zwraca stringa "No lobby available"
    
    */

    //var send = {}
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "api/v1/lobby/random",//"game/findGame",
      //data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug) {
          console.log("ajaxReceiveFoundGame success");
          console.log("hide")
          console.log(data)
          console.log(textStatus)
          console.log(jqXHR)
        }
          $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');

          if (jqXHR.status == 200) 
            gameFound(data);
           else
            alert("Nie ma wolnego lobby");
      },
      error: function(jqXHR, status, err) {
        if (debug) {
          console.warn("ajaxReceiveFoundGame error");
          console.log(jqXHR)
          console.log(status)
          console.log(err)
        }
        alert("Nie znaleziono wolnego lobby");
        $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');


      }
    });
  }

  var ajaxFindGameByCode = (code) => {
    $('.hideBeforeLoadModal').modal('show');
    //var send = {code: code}
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "api/v1/lobby/" + code,//"game/isCodeCorrect",
      //data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug) {
          console.log("ajaxFindGameByCode success");
          
          console.log(data)
          console.log(textStatus)
          console.log(jqXHR)
        }
          $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
          isCodeCorrect(data);
      },
      error: function(jqXHR, status, err) {
        if (debug)
          console.warn("ajaxFindGameByCode error");

        $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
      }
    });
  }
  
  var ajaxCreateLobby = () => {
    $('.hideBeforeLoadModal').modal('show');
    var send = {}
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "api/v1/lobby",//"game/createLobby",
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug){
          console.log("ajaxCreateLobby success");
          console.log(data)
          console.log(textStatus)
          console.log(jqXHR)
          }
          $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
          
          if (jqXHR.status == 201) {
            
            lobbyCreated(data);
          } else
            alert("nie stworzono lobby");
      },
      error: function(jqXHR, status, err) {
        if (debug)
          console.warn("ajaxCreateLobby error");
        
        $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
      }
    });
  }
  

  $("#btnJoinCode").on("click",() => {
    ajaxFindGameByCode($("#inputCode")[0].value);
  });
  $("#btnHost").on("click",()=>{
    ajaxCreateLobby();
  });
  $("#btnSearch").on("click",()=>{
    ajaxReceiveFoundGame();
  });
  $("#inputCode").on("click input paste change focus keypress", (e)=> {
    if ($("#inputCode")[0].value != "") {
      $("#btnJoinCodeInvisible").hide();
      $("#btnJoinCodeCollapse").show();
    } else {
      $("#btnJoinCodeInvisible").show();
      $("#btnJoinCodeCollapse").hide();
    }

  })

  /* debug  */
  var debugSetup = () => {
    if (debug == true) {
      /*LobbyJoinInit({
        logged: true,
        nickname: "GraczTestowy",
        gameCode: "",
        isHost: false,
        gameStarted: false,
      });*/
      ajaxReceiveWhoAmI();

      // self.gameFound = (game) => {
      //   gameFound(game);
      // }
      self.isCodeCorrect = (lobby) => {
        isCodeCorrect(lobby);
      }
      self.lobbyCreated = ()=>{return lobbyCreated;}
      
      self.nickname = nickname;
      self.isHost = isHost;
      self.gameCode = gameCode;
      self.logged = logged;
      self.gameStarted = gameStarted;
    }
  }

  /* inicjalizacja*/
  if (debug)
    debugSetup();
  else
    ajaxReceiveWhoAmI();

  return self;
}

LobbyJoinLogic.create = (debug = false) => {

  if (debug)
    return LobbyJoinLogic(debug);
  else
    LobbyJoinLogic()
}

//LobbyJoinLogic.create();
var debug = LobbyJoinLogic.create(debug = true);