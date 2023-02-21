
const LobbyJoinLogic = (data, debug = false, depMocks = {}) => {
  
  
  /*  singleton   */
  if (LobbyJoinLogic.singleton)
    return LobbyJoinLogic.singleton;
  var self = {};
  if (!LobbyJoinLogic.singleton && !data)
    LobbyJoinLogic.getInstance(data, depMocks, debug);

  /*  environment preparation  */
  $ = typeof $ != 'undefined'? $ : depMocks.$mock;
  window =  typeof window != 'undefined'? window : depMocks.windowMock;

  if ( depMocks.NavbarLogic && typeof NavbarLogic == "undefined")
      NavbarLogic = depMocks.NavbarLogic;
  if ( depMocks.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
      PageLanguageChanger = depMocks.PageLanguageChanger;
  
  /* logic variables */
  var nickname, gameCode, logged, isHost, gameStarted;

  var LobbyJoinInit = (data) => {

    isHost = data.playerInfo.isHost;
    gameCode = data.playerInfo.gameCode;
    logged = data.playerInfo.logged;
    gameStarted = data.playerInfo.gameStarted;
    nickname = data.playerInfo.nickname;
    
    self.roles = data.accountInfo.roles? data.accountInfo.roles : [];
    self.isLecturer = self.roles.includes("LECTURER");

    self.hideModal = data.hideModal;
    self.showModal = data.showModal;

    if ((isHost != null && isHost) || 
      (gameCode != null && gameCode != ""))
      window.location = "/game/" + gameCode;

    if ( self.isLecturer ) {
      $("#btnSearch").parent().css("visibility","hidden");
      $("#inputCode").parent().css("visibility","hidden");
    }
    if (typeof NavbarLogic != "undefined")
        NavbarLogic(data.playerInfo, debug);
      
    $('.hideBeforeLoad').css("display", "flex").fadeIn();
    resizeWindow();

    if ( typeof PageLanguageChanger != "undefined")
      PageLanguageChanger();


    listenersSetup();

    if ( MessagesModule )
        MessagesModule().getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});
  }

  /*       event listeners          */
  var listenersSetup = () => {
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
    });
    $("#inputCode").keydown( self.onEnterSubmit );
    window.onresize = resizeWindow;
  }

  /* messages */
  self.messagerFunction = (data) => {

    if ( data.areNew ) {
        //dźwiek
        //console.log("Ding!")
    }

    var updateNavbar = () => {
      $("#messageUnreadMessages").text(
          (data.numberOfMessages+data.numberOfLobbies)?
          ((data.numberOfMessages+data.numberOfLobbies)>99?
          "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
      $("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
  }
    updateNavbar();
  }
  self.onEnterSubmit = (e) => {
    
    var inputCode = $("#inputCode");
    var btnJoinCode = $("#btnJoinCode");
    if (e.keyCode === 13) { 
      e.preventDefault();
      if ( inputCode.val().trim() != "") {
        btnJoinCode.click();
      }
    }

  }
  /*  logic functions  */
  var resizeWindow = () => {
      
      $("html").height("100%");
      
      function isInt(n) {
        return n % 1 === 0;
      }
      
      var h1 = $(window.document).height();
      if ( isInt (h1))
        h1 -= 1;
      $("html").height(h1);
  } 
  
  var gameFound = (data) => {

    if (data)
      window.location = "game/" + data;
    else {
      if ( typeof PageLanguageChanger != "undefined" )
        displayFailInfoAnimation(PageLanguageChanger().getTextFor("CouldNotFindGames"));
      else
        displayFailInfoAnimation("Nie znaleziono żadnych gier");
    }
  }

  var displayFailInfoAnimation = (text) => {
    var failInfoDiv = $(`<div class="failSuccessInfo alert alert-danger">` + text + `</div>`)
    $("#buttonHolderWrapper").append(failInfoDiv)

    failInfoDiv.animate({
      top: "13%",
      opacity: 1
    }, 2000, function() {
      // Animation complete.
      setTimeout(function(){
        failInfoDiv.animate({
          top: "16%",
          opacity: 0
        }, 1000, function() {
          // Second Animation complete.
          failInfoDiv.remove();
        });
      },2000)
      
    });
  }

  var isCodeCorrect = (data) => {

    if (data.exists)
      if (!data.isFull)
        window.location = "game/" + $("#inputCode")[0].value;
      else
        if ( typeof PageLanguageChanger != "undefined" )
          displayFailInfoAnimation(PageLanguageChanger().getTextFor("NoAvailableLobby"));
        else
          displayFailInfoAnimation("lobby pełne");
    else
      if ( typeof PageLanguageChanger != "undefined" )
        displayFailInfoAnimation(PageLanguageChanger().getTextFor("IncorrectCode"));
      else
        displayFailInfoAnimation("kod nieprawidłowy");
  }

  var lobbyCreated = (code) => window.location = "/game/" + code;

  /*Ajax*/
  var ajaxReceiveFoundGame = () => {
    self.showModal();

    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "api/v1/lobby/random",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug) {
          console.log("ajaxReceiveFoundGame success");
          console.log(data)
        }
          self.hideModal();

          if (jqXHR.status == 200) 
            gameFound(data);
          else
            if ( typeof PageLanguageChanger != "undefined" )
              displayFailInfoAnimation(PageLanguageChanger().getTextFor("NoAvailableLobby"));
            else
              displayFailInfoAnimation("Nie znaleziono wolnego lobby");
      },
      error: function(jqXHR, status, err) {
        if (debug) {
          console.warn("ajaxReceiveFoundGame error");
          console.log(jqXHR)
        }
        
        if ( typeof PageLanguageChanger != "undefined" )
          displayFailInfoAnimation(PageLanguageChanger().getTextFor("NoAvailableLobby"));
        else
          displayFailInfoAnimation("Nie znaleziono wolnego lobby");

        self.hideModal();
      }
    });
  }

  var ajaxFindGameByCode = (code) => {

    self.showModal();
    
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "api/v1/lobby/" + code,
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug) {
          console.log("ajaxFindGameByCode success");
          console.log(data)
        }
          self.hideModal();
          isCodeCorrect(data);
      },
      error: function(jqXHR, status, err) {
        if (debug)
          console.warn("ajaxFindGameByCode error");

        self.hideModal();
      }
    });
  }
  
  var ajaxCreateLobby = () => {
    $('.hideBeforeLoadModal').modal('show');
    var send = {}
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "api/v1/lobby",
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (debug){
          console.log("ajaxCreateLobby success");
          console.log(data)
          }
          self.hideModal();
          
          if (jqXHR.status == 201) {
            
            lobbyCreated(data);
          } else
          if ( typeof PageLanguageChanger != "undefined" )
            displayFailInfoAnimation(PageLanguageChanger().getTextFor("LobbyNotCreated"));
          else
            displayFailInfoAnimation("Nie stworzono lobby");
      },
      error: function(jqXHR, status, err) {
        if (debug)
          console.warn("ajaxCreateLobby error");
        
        self.hideModal();
      }
    });
  }
  
  /* debug  */
  var debugSetup = () => {
    if (debug == true) {
      LobbyJoinInit(data);

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
    LobbyJoinInit(data);

  return self;
}

LobbyJoinLogic.getInstance = (dataLazy, debug = false, depMocks = {}, cbTest) => {
  
  if (LobbyJoinLogic.singleton)
    return LobbyJoinLogic.singleton;

  $ = typeof $ != 'undefined'? $ : depMocks.$mock;
  window =  typeof window != 'undefined'? window : depMocks.windowMock;

  var showModal = () => {
      if (typeof $("").modal != 'undefined') {
          $('.hideBeforeLoadModal').modal('show');
      }
  }
  var hideModal = () => {
      if (typeof $("").modal != 'undefined') {
          $(".hideBeforeLoadModal").on('shown.bs.modal', function() { $(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
      }
  }

  var ajaxReceiveWhoAmI = ( ) => {

    showModal();

    $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(playerInfo, textStatus, jqXHR) {
            if (debug){
                console.log("ajaxReceiveWhoAmI success");
            }
            
            ajaxReceiveAccountInfo(playerInfo);
        },
        error: function(jqXHR, status, err) {
            if (debug){
                console.warn("ajaxReceiveWhoAmI error");
            }

            hideModal();
        }
    });
  }
  var ajaxReceiveAccountInfo = ( playerInfo ) => {

    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/account/info",
      contentType: "application/json",
      success: function(accountInfo, textStatus_, jqXHR_) {
          if (debug) {
              console.log("ajaxReceiveAccountInfo success");
              console.log(accountInfo);
          }
          
          playerInfo.showModal = showModal;
          playerInfo.hideModal = hideModal;
          playerInfo.roles = accountInfo.roles;

          var data = {
            playerInfo: playerInfo, 
            accountInfo: accountInfo,
            showModal: showModal,
            hideModal: hideModal
          }
          if ( cbTest )
              cbTest("success");

          LobbyJoinLogic.singleton = LobbyJoinLogic(data, debug, depMocks);
          hideModal();
      },
      error: function(jqXHR_, status_, err_) {
          if (debug) {
            console.warn("ajaxReceiveAccountInfo error");
          }
          hideModal();
      }
    });
  }

  if (dataLazy)
    return LobbyJoinLogic(dataLazy, debug, depMocks);
  else
    ajaxReceiveWhoAmI()

  return LobbyJoinLogic.singleton;
}
