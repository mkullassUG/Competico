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
    maxPlayers: 10,
    lobbyVisibility: "Publiczne"
  }
  self.isHost;
  //self.isLecturer; //For future implementation of lecturer
  self.debug = debug;
  self.gameStarted;
  self.ajaxLoopTimeout;
  self.lobbyCode = _lobbyCode;
  self.nickname;
  self.gameObject;

  var lobbyTop = $("#lobbyTop");
  var lobbyCenter = $("#lobbyCenter");
  var lobbyBottom = $("#lobbyBottom");
  var gameTop = $("#gameTop");
  var gameCenter = $("#gameCenter");
  var gameBottom = $("#gameBottom");

  /*       logic functions          */
  self.lobbyInit = (playerInfo) => {
    console.log("lobbyInit");

    self.isHost = playerInfo.isHost; 
    self.nickname = playerInfo.nickname; 
    self.gameStarted = playerInfo.gameStarted;
    
    if (self.gameStarted) {
      self.startGame();
      //self.gameObject = GameLogic.create(self);
      return;
    }

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
      $(".btnKick").addClass("collapse")
    }
    if ($('.btn').length)
      $('.btn').popover();

    //ustaw kod
    if ($("#lobbyCode").length)
      $("#lobbyCode").html("Kod: " + self.lobbyCode);
    ajaxReceiveLobbyChange();
    if ($(".hideBeforeLoad").length)
      $('.hideBeforeLoad').fadeIn();

    self.ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
  }

  self.startGame = () => {

    lobbyTop.hide();
    lobbyCenter.hide();
    lobbyBottom.hide();

    gameTop.show();
    gameCenter.show();
    gameBottom.show();
    clearTimeout(self.ajaxLoopTimeout);
    console.log("clearTimeout(ajaxLoopTimeout);");

    //TODO, zrobic lepiej, usunac lorem ipsum
    $("#bigChangeDiv").removeClass("text-center").removeClass("hideBeforeLoad");
    $("#bigChangeDiv").html(`
    <div class="container fill">
      <!-- game -->
      <div class="row timer-m-custom" id="GameTop">
        <div class="col-12">
          <div id="gameTimer">Obecne zadanie</div>
        </div>
      </div>
      <div class="row mt-3 rounded" id="GameWrapperWrapper">
        <div class="col-12" id="GameWrapperDiv">
          <div id="GameDivDecoration" class="m-custom my-3 p-3 rounded" style="overflow:hidden;">
            <div class="bg-light unselectable btnShadow" id="GameDiv">
              <h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
              <div id="taskContent">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean quis laoreet orci, et <div class="answerHolder "></div> mauris. Nam rhoncus, quam congue iaculis cursus, arcu orci cursus ante, ac scelerisque velit nibh eget urna. Fusce a iaculis elit, a aliquam sapien. Aliquam posuere tincidunt mi in pulvinar. Ut at lobortis ipsum. Aliquam erat volutpat. <div class="answerHolder "></div> velit elit, suscipit nec mi eu, dignissim ullamcorper augue. Aliquam in sodales ex, in finibus dui. Nulla blandit laoreet eros vel ornare. In dignissim mauris at nulla condimentum gravida.

                Nam eu odio at ipsum condimentum pulvinar. Donec consectetur nibh dolor, gravida aliquam dui convallis non. Duis ut velit dictum, congue <div class="answerHolder "></div> nec, rutrum sem. Nam sagittis, felis a finibus condimentum, massa mauris accumsan elit, in egestas ante nisi id augue. Suspendisse laoreet odio libero, non euismod est dictum id. Donec pharetra, sapien efficitur blandit ullamcorper, velit nibh dignissim risus, placerat accumsan nibh enim ac lorem. In eu nisi arcu. Proin auctor egestas nisi vel pulvinar. Sed nec mauris eu lacus ultrices finibus et a leo. Fusce ac arcu a nisi maximus interdum vitae nec lorem.
              </div>
              <h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>
              <div class="pb-2 mb-0 " id="taskAnswerHolder">
                <div class="answerHolder ">
                  <div class="answer draggable">
                    słowo
                  </div>
                </div>
                <div class="answerHolder ">
                  <div class="answer draggable">
                    słowo
                  </div>
                </div>
                <div class="answerHolder ">
                  <div class="answer draggable">
                    słowo
                  </div>
                </div>
                <div class="answerHolder ">
                  
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row mt-3 text-center button-m-custom" id="GameBottom">
        <div class="col-12">
          <buton class="btn btn-lg btn-secondary mb-3 btnShadow" id="btnNextTask" data-toggle="modal" data-target="#answerModalCenter">Nastepne zadanie</buton>
        </div>
      </div>
    </div>`);

    //tutaj wywołac obiekt taska
    console.log("GameLogic.getInstance(self);");
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
    if (!lobby.exists) {
      console.warn("lobby nie znalezione!");
      return 0;
    }
    console.warn(lobby);

    //setup lobby
    if (!lobby.host) {
      console.warn("Zainicjować wyjście klienta z tego lobby");
      return 0;
    }

    if ($("#lobbyHostUsername").length)
      $("#lobbyHostUsername").html(lobby.host.username + " " + lobby.host.nickname);
    //------player list
    if($("#lobbyplayers > .player").length)
      $("#lobbyplayers > .player").remove();

    if (typeof lobby.players == 'undefined') {
      console.warn("nie znaleziono żadnych graczy w lobby");
      return 0;
    }
    
    let playersLength = lobby.players.length;
    for (let i = 0; i < playersLength; i++) {
      let player = lobby.players[i];
      let playerDiv = $(`<div class="media text-muted pt-3 player">
            <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 32x32"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="#007bff" dy=".3em">32x32</text></svg>
            <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
              <div class="d-flex justify-content-between align-items-center w-100">
                <strong class="text-gray-dark">`
                  + player.username + " " + player.nickname + `</strong>
                <a href="" class="btnKick" data-username="` + player.username + `">Usuń</a>
              </div>
              <span class="d-block">@username</span>
            </div>
          </div>`);
      if ($("#lobbyplayers").length)    
        $("#lobbyplayers").append(playerDiv);
    }
    
    if (self.isHost)
      if ($(".btnKick").length)
        $(".btnKick").on("click", (e) => {
          e.preventDefault();

          sendAjaxKickPlayer($(e.target).data("username"));

          if (debug == true)
            console.log("kicking: " + $(e.target).data("username"));
        });
    else
      if ($(".btnKick").length)
        $(".btnKick").addClass("collapse");
    //-----max players
    if (lobby.maxPlayers != null) {
      $("#lobbyMaxPlayers").val("Gracze " + playersLength + "\\" + lobby.maxPlayers);
    }
    //... pozostałe zmiany jakie mogą zajść w lobby dodac poniżej
  }

  self.updateCheck = (data) => {
    if(data.gameStarted == true) 
    self.startGame(); //tutaj tworzyć obiekt taska?
    else if (data.lobbyContentChanged == true)
      ajaxReceiveLobbyChange();
  } 

  self.updateLobbySettingsAsHost = () => {
    
    self.lobbySettings.maxPlayers = $("#inputMaxPlayers").val();
    self.lobbySettings.lobbyVisibility = $("#lobbyVisibility").val();
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

      if (self.isHost == true)
        sendAjaxSettingsChange();
    });
  if ($("#btnEndGame").length)
    $("#btnEndGame").on("click",()=>{
      if (self.debug)
        console.log("btnEndGame");

      if (self.gameStatus == "ended")
        sendAjaxEndGame();
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

    var send = {player: player}
    $.ajax({
      type     : "DELETE",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode + "/players",
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug)
          console.log("sendAjaxKickPlayer success");

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

    self.updateLobbySettingsAsHost();

    var send = { ...self.lobbySettings} 
    if (self.debug == true)
          console.log(send);

    $.ajax({
      type     : "PUT",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode,
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {

        if (self.debug == true)
          console.log("SettingsChange success");
      },
      error: function(jqXHR, status, err) {
        if (self.debug == true)
          console.log("SettingsChange error");
      }
    });
  }
  
  /*   ajax http requests       */
  var ajaxConnectionLoop = ( ) => {
    
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/lobby/" + self.lobbyCode + "/changes",//"game/connectionLoop",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug) {
          console.log("connectionLoop success");
          console.log(data);
        }
        
        self.ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
        self.updateCheck(data);//true jeśli coś się zmieniło

      },
      error: function(jqXHR, status, err) {
        if (self.debug){
          console.warn("connectionLoop error");
          // console.log(jqXHR);
          // console.log(status);
          // console.log(err);
        }
        self.ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
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
        console.log("LobbyLogic");
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
//LobbyLogic.create();

/*
odbieram{

  text: ["Lorem ipsum dolor sit amet,", {0: "a"},"sefsefsfsef", {}, "awdawdadad"],
  list: [],
}
odpowiedź: {

  array_w_kolejności:[ inpStr1, inpStr2,...],
}
*/
