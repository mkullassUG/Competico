/*
LobbyLogic odpowiada za:
1.Odczytanie informacji o kliencie
3.obsługiwanie przycisków i dynamicznego widoku lobby przez ajax i jquery
4.przeniesienie klienta na następny widok z gameLogic

*/


const LobbyLogic = (playerInfo, _lobbyCode, debug = false) => {

  /*       logic variables          */
  var self = playerInfo;
  self.lobbySettings = {
    maxPlayers: 1,
    allowsRandomPlayers: null
  }
  self.lobbySettingsPlaceholder = {
    maxPlayers: 1,
    allowsRandomPlayers: null
  }
  self.isHost;
  self.lobbyHost;
  //self.isLecturer; //For future implementation of lecturer
  self.debug = debug;
  self.gameStarted;
  //to nie musi być w self.
  self.ajaxLobbyLoopTimeout;
  self.lobbyCode = _lobbyCode;
  self.nickname;
  self.username;
  self.gameObject;
  self.gameID;
  self.possiblyJoinedFromEndGame;

  /*       logic functions          */
  self.lobbyInit = (playerInfo) => {

    self.isHost = playerInfo.isHost; 
    self.nickname = playerInfo.nickname;
    self.username = playerInfo.username;
    self.gameStarted = playerInfo.gameStarted;
    self.possiblyJoinedFromEndGame = playerInfo.gameCode? false : true;

    if (self.gameStarted) {
      self.gameID = playerInfo.gameID;
      self.startGame();
      return;
    }

    $("#lobbyTop").removeClass("collapse");
    $("#lobbyCenter").removeClass("collapse");
    $("#lobbyBottom").removeClass("collapse");

    if ($(".hideBeforeLoad").length)
      $('.hideBeforeLoad').fadeIn();
    
    //wysłać tylko jeśli z whoAmI wyszło że nie mam jeszcze lobby..??
    if (!self.isHost)
      ajaxSendJoin();

    if (typeof self.isHost == undefined && self.debug)
      console.warn("Critical error, invalid data received from server isHost == undefined");
    
    if (self.isHost) {
      //ustawianie lobby widoku hosta
      $("#btnStart").show();
      $("#btnSettings").show();
      $("#lobbyHostUsername").html(self.nickname);
    } else {
      //ustawianie lobby widoku gracza
      $("#lobbyMainHost").removeClass("col-10").addClass("col-12");
      $("#lobbySettings").hide();
      $(".btnKick").addClass("collapse");
    }
    if ($('.btn').length)
      $('.btn').popover();

    //ustaw kod
    if ($("#lobbyCode").length)
      $("#lobbyCode").html("Kod: " + self.lobbyCode);

    /*TODO, jeśli skończyłem grę to może chce zobaczyć wyniki wszystkich!! lobby exist:false dostaje i wyświetlam tylko modala żeby wyjść!!
    
    */
    ajaxReceiveLobbyChange();

    //callback true-> jeśli coś się zmieniło false -> jeśli nie
    ajaxConnectionLoop((data)=>{self.updateCheck(data)})
  }

  self.startGame = () => {
    $("#bigChangeDiv").removeClass("text-center").removeClass("hideBeforeLoad");
    $("#lobbyTop").hide();
    $("#lobbyCenter").hide();
    $("#lobbyBottom").hide();
    $("#gameBottom").show();
    $("#gameTop").show();
    $("#gameCenter").show();

    //tutaj wywołac obiekt taska
    self.gameObject = GameLogic.getInstance(self);
  }

  self.copyTextToClipboard = (text) => {
    var textArea = document.createElement("textarea");
    textArea.value = text
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
      var successful = document.execCommand('copy');
      var msg = successful ? 'successful' : 'unsuccessful';
      if (self.debug)
        console.log('Copying text command was ' + msg);
    } catch (err) {
      if (self.debug)
        console.log('Oops, unable to copy');
    }

    document.body.removeChild(textArea);
  }

  self.leaveLobby = () => {
    //wyświewtlić modala czy napewn ochce wyjść
    window.location = "/lobby"; 
  }

  self.lobbySetupAfterChange = (lobby) => {
    /*
    lobby: 
    {
      players: //tablica obiektów graczy z których można odczytać np "name"
      maxPlayers: //jedno z ustawień lobby
      host: nazwa obecnego hosta lobby
    }
    */
    if (!lobby.exists && self.possiblyJoinedFromEndGame === false) {
      console.warn("lobby nie znalezione!");
      console.warn(lobby);
      /*TODO
        wyswietlic że Lobby zostało zakończone przez Hosta modal


      */


      $('#LobbyDeletedModalCenter').modal('show');
      return 0;
    }
    console.warn(lobby);

    //setup lobby
    if (!lobby.host) {
      console.warn("Zainicjować wyjście klienta z tego lobby");
      return 0;
    }
    self.lobbyHost = lobby.host;

    if ($("#lobbyHostUsername").length)
      $("#lobbyHostUsername").html(self.lobbyHost.username + " " + self.lobbyHost.nickname);
    //------player list
    if($("#lobbyplayers > .player").length)
      $("#lobbyplayers > .player").remove();

    if (typeof lobby.players == 'undefined') {
      console.warn("nie znaleziono żadnych graczy w lobby");
      return 0;
    }
    //self.removePlaver(); //todo
    //$("#btnSendkick")
    let playersLength = lobby.players.length;
    for (let i = 0; i < playersLength; i++) {
      let player = lobby.players[i];
      if (player.username !== self.lobbyHost.username && player.nickname !== self.lobbyHost.nickname) {
        var specialPlayer = "",
        specialClass = "";
        if (self.username === player.username && self.nickname ===  player.nickname) {
          specialPlayer = "(me)";
          specialClass = "lobbyMe"
        }
        let playerDiv = $(`<div class="media text-muted pt-3 player `+specialClass+`">
              <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="#007bff" dy=".3em">32x32</text></svg>
              <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                <div class="d-flex justify-content-between align-items-center w-100">
                  <strong class="text-gray-dark">` + specialPlayer + ` `
                    + player.username + `</strong>
                  <a href="" class="btnKick" data-username="` + player.username + `">Usuń</a>
                </div>
                <span class="d-block text-left"><i>` + player.nickname + `</i></span>
              </div>
            </div>`);
        if ($("#lobbyplayers").length)    
          $("#lobbyplayers").append(playerDiv);
      }
    }
    
    if (self.isHost){
      if ($(".btnKick").length)
        $(".btnKick").on("click", (e) => {
          e.preventDefault();

          sendAjaxKickPlayer($(e.target).data("username"));

          if (debug == true)
            console.log("kicking: " + $(e.target).data("username"));
        });
    } else {
      if ($(".btnKick").length)
        $(".btnKick").addClass("collapse");
    }
    //-----max players
    if (lobby.maxPlayers != null) {
      $("#lobbyMaxPlayers").val("Gracze " + playersLength + "\\" + lobby.maxPlayers);
    }

    //------lobby settings dla hosta ustawic
    if (self.isHost) {
      if ( lobby.maxPlayers ) {
        $("#inputMaxPlayers").val(lobby.maxPlayers);
        self.lobbySettings.maxPlayers = lobby.maxPlayers;
        self.lobbySettingsPlaceholder.maxPlayers = lobby.maxPlayers;
      }

      if (lobby.allowsRandomPlayers === true || lobby.allowsRandomPlayers === false) {

        $("#allowsRandomPlayersCB").checked == lobby.allowsRandomPlayers;
        self.lobbySettings.allowsRandomPlayers = lobby.allowsRandomPlayers;
        self.lobbySettingsPlaceholder.allowsRandomPlayers = lobby.allowsRandomPlayers;
      }
    }

    //... pozostałe zmiany jakie mogą zajść w lobby dodac poniżej
  }

  self.updateCheck = (data) => {

    self.ajaxLobbyLoopTimeout = setTimeout(()=>{ajaxConnectionLoop((data_)=>{self.updateCheck(data_)})},1000);
    console.log("data.lobbyExists === false :" + (data.lobbyExists === false));
    if (data.lobbyExists === false) {
      console.log("wyjdź");
      console.log(data);
      $('#LobbyDeletedModalCenter').modal('show');
    } else if (data.gameStarted == true) {
      clearTimeout(self.ajaxLobbyLoopTimeout);
      self.gameID = data.gameID;
      self.startGame(); //tutaj tworzyć obiekt taska?
    } else if (data.lobbyContentChanged == true){
      ajaxReceiveLobbyChange();
    }
  } 

  self.updateLobbySettingsAsHost = () => {
    
    self.lobbySettings.maxPlayers = self.lobbySettingsPlaceholder.maxPlayers;
    self.lobbySettings.allowsRandomPlayers = self.lobbySettingsPlaceholder.allowsRandomPlayers;
    //... pozostałe ustawienia które można dodać poniżej
  }

  /*       event listeners          */
  if ($("#btnStart").length)
    $("#btnStart").on("click",()=>{
      if (self.debug)
        console.log("btnStart");

      if (self.isHost == true)
        sendAjaxStart();
    });
  if ($("#btnSendleave").length)
    $("#btnSendleave").on("click",()=>{
      if (self.debug)
        console.log("btnSendleave")
        sendAjaxLeave();
    });
  if ($("#btnCopyCode").length)
    $("#btnCopyCode").on("click",(e) => {
      if (self.debug == true)
        console.log("btnCopyCode")

        self.copyTextToClipboard(self.lobbyCode);
    });
  if ($("#btnSaveSettings").length)
    $("#btnSaveSettings").on("click",()=>{
      if (self.debug)
        console.log("btnSaveSettings");

      if (self.isHost == true) {
        self.lobbySettingsPlaceholder.maxPlayers = parseInt($("#inputMaxPlayers").val());
        self.lobbySettingsPlaceholder.allowsRandomPlayers = $("#allowsRandomPlayersCB")[0].checked;
        sendAjaxSettingsChange();
        
      }
    });
  if ($("#btnEndGame").length)
    $("#btnEndGame").on("click",()=>{
      if (self.debug)
        console.log("btnEndGame");

      if (self.gameStatus == "ended")
        sendAjaxEndGame();
    });
  if ($("#btnSendLobbyDeleted").length)
    $("#btnSendLobbyDeleted").on("click",()=>{
      if (self.debug)
        console.log("btnSendLobbyDeleted");
      self.leaveLobby();
    });
  if ($("#btnSettings").length)
    $("#btnSettings").on('click',(e) => {
      $("#inputMaxPlayers").val(self.lobbySettings.maxPlayers);
      $("#allowsRandomPlayersCB").checked = self.allowsRandomPlayers;
    });
  /*     ajax http actions       */
  var sendAjaxStart = () => {
    if (self.isHost == false)
      return;
    self.showModal();
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "/api/v1/lobby/"+self.lobbyCode+"/start",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug)
          console.log("sendAjaxStart success");
        
          self.hideModal();
      },
      error: function(jqXHR, status, err) {
        if (self.debug) {
          console.log("sendAjaxStart error");
          //gameSetupAfterChange();
        }
        self.hideModal();
      }
    });
  }
  var sendAjaxLeave = () => {
    console.log("sendAjaxLeave");
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "/api/v1/lobby/"+self.lobbyCode+"/leave",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug)
          console.log("sendAjaxLeave success");

        self.leaveLobby();
      },
      error: function(jqXHR, status, err) {
        if (self.debug)
          console.log("sendAjaxLeave error");
        self.leaveLobby();
        
      }
    });
  }
  var sendAjaxKickPlayer = (player) => {
    if (self.isHost == false)
      return;

    var send = player;
    $.ajax({
      type     : "DELETE",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode + "/players",
      data     : send,
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug){
          console.log("sendAjaxKickPlayer success");
          console.log("/api/v1/lobby/" + self.lobbyCode + "/players");
          console.log(send);
          console.log(data);
          console.log(textStatus);
          console.log(jqXHR);
        }
        //chyba że w checkChanges będzie usuwać go innym a samemu będzie pisac że lobby nie istnieje... albo whoami będzie sprawdzać kiedyś tam?
        console.warn("Zaimplementować usuwanie gracza!!");
      },
      error: function(jqXHR, status, err) {
        if (self.debug)
          console.log("sendAjaxKickPlayer error");
      }
    });
  }
  var sendAjaxSettingsChange = () => {
    if (self.isHost == false)
      return;

    var send = { ...self.lobbySettingsPlaceholder} 
    if (self.debug == true)
      console.log(send);

    $.ajax({
      type     : "PUT",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode,
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        /* dostaje false jeśli:
          lobby nie istnieje
          nie jest hostem
          póbuje zmniejszyć maxPlayerCount poniżej ilośc graczy obecnych w lobby
        */
        if (self.debug == true) {
          console.log("SettingsChange success");
          console.log(data);
          console.log(jqXHR);
          console.log(textStatus);
        }
        if (data)
          self.updateLobbySettingsAsHost();
      },
      error: function(jqXHR, status, err) {
        if (self.debug == true)
          console.log("SettingsChange error");
      }
    });
  }
  
  /*   ajax http requests       */
  var ajaxConnectionLoop = ( callback ) => {
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode + "/changes",//"game/connectionLoop",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug) {
          console.log("ajaxConnectionLoop success");
          console.log("/api/v1/lobby/" + self.lobbyCode + "/changes");
          console.log(data);
        }
        /*
          callback dla:
             self.ajaxLobbyLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
          lub niczego jeśli z końca gry odpalam LogicGame
        */
        callback(data);//true jeśli coś się zmieniło  

      },
      error: function(jqXHR, status, err) {
        if (self.debug){
          console.warn("connectionLoop error");
          // console.log(jqXHR);
          // console.log(status);
          // console.log(err);
        }
        callback(data);
      }
    });
  }
  var ajaxReceiveLobbyChange = ( ) => {
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode,//"game/getLobby",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug) {
          console.log("ajaxGetLobbyChange success");
          console.log(data);
          console.log(textStatus);
          console.log(jqXHR);
        }
        self.lobbySetupAfterChange(data);
      },
      error: function(jqXHR, status, err) {
        if (self.debug){
          console.warn("ajaxGetLobbyChange error");
          // console.log(jqXHR);
          // console.log(status);
          // console.log(err);
        }
      }
    });
  }
  var ajaxSendJoin = ( ) => {
    self.showModal();
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "/api/v1/lobby/join/" + self.lobbyCode,
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug) {
          console.log("ajaxSendJoin success");
          //console.log(jqXHR);
        }
        self.hideModal();

      },
      error: function(jqXHR, status, err) {
        if (self.debug) {
          console.warn("ajaxSendJoin error");
          console.warn(jqXHR);
          console.warn(status);
          console.warn(err);
          //console.log(jqXHR);
        }
        self.hideModal();
      }
    });
  }
  
  /*  initalization  */
  self.lobbyInit(playerInfo);
  
  return self;
}

LobbyLogic.getInstance = (debug = false) => {

  if (LobbyLogic.singleton)
    return LobbyLogic.singleton;
  var lobbyCode = window.location.href.substring(this.location.href.lastIndexOf('/') + 1);

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
          console.log(playerInfo);
          console.log(textStatus);
          console.log(jqXHR);
        }
        hideModal();
        playerInfo.showModal = showModal;
        playerInfo.hideModal = hideModal;
        LobbyLogic.singleton = LobbyLogic(playerInfo, lobbyCode, debug);
      },
      error: function(jqXHR, status, err) {
        if (debug){
          console.warn("ajaxReceiveWhoAmI error");
          // console.log(data);
          // console.log(textStatus);
          // console.log(jqXHR);
        }
        
        hideModal();
      }
    });
  }
  
  ajaxReceiveWhoAmI();
}