/*
LobbyLogic odpowiada za:
1.Odczytanie informacji o kliencie
3.obsługiwanie przycisków i dynamicznego widoku lobby przez ajax i jquery
4.przeniesienie klienta na następny widok z gameLogic
*/
//TODO 2021-05-13 odczytać id gry jeśli jestem lektorem i przenieść na tablice wyników.... czy tego nie robie przy starcie gry?
//const LobbyLogic = (playerInfo, _lobbyCode, debug = false, $mock, windowMock) => {
const LobbyModule = (function($, window) {

  const LobbyLogic = (data, debug = false, successfulCreationCallback) => {
  
    /*  singleton   */
    if (LobbyLogic.singleton) {
      if ( successfulCreationCallback )
        successfulCreationCallback(LobbyLogic.singleton);
      return LobbyLogic.singleton;
    }
    var self = {};
    if (!LobbyLogic.singleton && !data)
      return LobbyLogic.getInstance(data, debug, successfulCreationCallback);
    LobbyLogic.singleton = self;

    /*  environment preparation  */

    /*       logic variables          */
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
    self.isLecturer;
    self.debug = debug;
    self.gameStarted;
  
    //to nie musi być w self.
    self.ajaxLobbyLoopTimeout;
    self.lobbyCode;
    self.nickname;
    self.username;
    self.gameObject;
    self.gameID;
    self.possiblyJoinedFromEndGame;
    self.preventFromShowingLobbyDeletedModal = true;
  
    /*       logic functions          */
    var LobbyInit = (data) => {
      /*data:
        playerInfo, accountInfo, lobbyCode, hideModal, showModal
      */
  
      if (self.debug)
        console.log("LobbyInit");
  
      self.isHost = data.playerInfo.isHost; 
      self.nickname = data.playerInfo.nickname;
      self.username = data.playerInfo.username;
      self.gameStarted = data.playerInfo.gameStarted;
      self.possiblyJoinedFromEndGame = data.playerInfo.gameCode? false : true;
  
      self.roles = data.accountInfo.roles? data.accountInfo.roles : [];
  
      self.lobbyCode = data.lobbyCode;
      self.hideModal = data.hideModal;
      self.showModal = data.showModal;
  
      self.isLecturer = self.roles.includes("LECTURER");
  
      if (self.gameStarted) {
        self.gameID = data.playerInfo.gameID;
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
      
      if (self.isLecturer) 
          $("body").addClass("lektorBody");
      else
          $("body").addClass("playerBody");
  
      if (self.isHost) {
        //ustawianie lobby widoku hosta
        $("#btnStart").show();
        $("#btnSettings").show();
        $("#lobbyHostUsername").text(self.nickname);
          if (self.isLecturer)
            self.setupLobbyForLecturer();
      } else {
        //ustawianie lobby widoku gracza
        $("#lobbyMainHost").removeClass("col-10").addClass("col-12");
        $("#lobbySettings").hide();
        $(".btnKick").addClass("collapse");
      }
      if ($('.btn').length && $('.btn').popover)
          $('.btn').popover();
  
      //ustaw kod
      if ($("#lobbyCode").length)
        $("#lobbyCode").text("Kod: " + self.lobbyCode);
  
      /*TODO, jeśli skończyłem grę to może chce zobaczyć wyniki wszystkich!! lobby exist:false dostaje i wyświetlam tylko modala żeby wyjść!!
      
      */
  
      self.ajaxReceiveLobbyChange();
  
      tooltipsUpdate();
  
      $('#exampleModalCenter').on('shown.bs.modal', function (e) {
        $('#btnSettings').one('focus', function (e) {
            $(this).blur();
        });
      });
      
      listenersSetup();
      //callback true-> jeśli coś się zmieniło false -> jeśli nie
      ajaxConnectionLoop((data)=>{self.updateCheck(data)});
      
      if (successfulCreationCallback)
          successfulCreationCallback(self);
    }
  
    /*       event listeners          */
    var listenersSetup = () => {
  
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
          $("#allowsRandomPlayersCB")[0].checked = self.allowsRandomPlayers;
        });
      if ($("#lektorCB").length)
        $("#lektorCB").on('click',(e) => {
            if ($("#lektorCB")[0].checked) {
                //pokazać wybór grupy
                $("#lectorModeOnDiv").show();
                $("#lectorModeOffDiv").hide();
            } else {
                //zgasić wybór grupy
                $("#lectorModeOnDiv").hide();
                $("#lectorModeOffDiv").show();
            }
        });
      if ($("#customTasksCB").length)
        $("#customTasksCB").on('click',(e) => {
            if ($("#customTasksCB")[0].checked) {
                //pokazać wybór grupy
                $("#customTaskSetModeOnDiv").show();
            } else {
                //zgasić wybór grupy
                $("#customTaskSetModeOnDiv").hide();
            }
        });
  
      if ($("#allowsRandomPlayersCB").length) 
        $("#allowsRandomPlayersCB").on('click', (e)=> {
            self.allowsRandomPlayers = $("#allowsRandomPlayersCB")[0].checked;
        });
  
      window.onresize = resizeWindow;
    }
  
    var tooltipsUpdate = () => {
  
      if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
          $('[data-toggle="tooltip"]').tooltip({
              trigger : 'hover'
          });
    }
  
    //new 2021-04-27
    var resizeWindow = () => {
        
        //var sy = window.scrollY;
        $("html").height("100%");
        if( self.debug )
          console.log("res");

        function isInt(n) {
          return n % 1 === 0;
        }
      
        var h1 = $(window.document).height();
        if ( isInt (h1))
          h1 -= 1;
        $("html").height(h1);
    } 
    
    self.startGame = (cbGame) => {
  
      if ( debug ) {
        console.log(self.isLecturer);
        console.log(self.gameID);
      }
      if ( self.isLecturer )
          window.location.replace("/game/results/" + self.gameID);
          //window.location = "/game/results/" + self.gameID;
      
      if ( !window.document.hasFocus() )
        startPageFlashing();

      $("#bigChangeDiv").removeClass("text-center").removeClass("hideBeforeLoad");
      $("#lobbyTop").hide();
      $("#lobbyCenter").hide();
      $("#lobbyBottom").hide();
      $("#gameBottom").show();
      $("#gameTop").show();
      $("#gameCenter").show();
  
      //tutaj wywołac obiekt taska
      self.gameObject = GameModule($, window).getInstance(self, false, cbGame);
    }
  
    self.copyTextToClipboard = (text) => {
      
      var textArea = window.document.createElement("textarea");
      textArea.value = text
      window.document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      
      var successful = false;
      try {
        successful = window.document.execCommand('copy');
        var msg = successful ? 'successful' : 'unsuccessful';
        if (self.debug)
          console.log('Copying text command was ' + msg);
      } catch (err) {
        if (self.debug)
          console.log('Oops, unable to copy');
      }
  
      window.document.body.removeChild(textArea);
      return successful;
    }
    
    var startPageFlashing = () => {

      var getIconUrl = () => {
        var link = window.document.querySelector("link[rel~='icon']");
        if (!link) 
          throw Error("no Original Icon");
        return link.href;
      }

      var original = window.document.title;
      var originalURL = getIconUrl();
      var timeout;

      var flashTitle = function (newMsg, newURL, howManyTimes) {

        function step() {
          window.document.title = (window.document.title == original) ? newMsg : original;
          changeIcon(getIconUrl() == originalURL? newURL : originalURL);

          if (--howManyTimes > 0) {
              timeout = setTimeout(step, 1000);
          };
            
          if ( window.document.hasFocus() )
            cancelFlashTitle(timeout);
        };
    
        howManyTimes = parseInt(howManyTimes);
    
        if (isNaN(howManyTimes)) {
            howManyTimes = 5;
        };
    
        cancelFlashTitle(timeout);
        step();
        changeIcon()
      };
      
      var cancelFlashTitle = function () {
          clearTimeout(timeout);
          window.document.title = original;
          changeIcon(originalURL);
      };

      var changeIcon = (newURL) => {
        var link = window.document.querySelector("link[rel~='icon']");
        link.href = newURL;
      }

      

      flashTitle("Game started!", "/assets/myIcons/CompeticoLogoMessage.svg", 9999);
    }

    self.leaveLobby = () => {
      //wyświewtlić modala czy napewno chce wyjść
      //window.location = "/lobby"; 
      window.location.replace("/lobby");
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
        if (self.debug)
          console.warn("lobby nie znalezione!");
        /* wyswietlic że Lobby zostało zakończone przez Hosta modal */
        //2021-04-22, (fast fix?) lobby deleted modal showing at the start randomly
        if (self.preventFromShowingLobbyDeletedModal) {
            self.preventFromShowingLobbyDeletedModal=false;
            return "prevented from displaying info";
        }
        if (self.debug)
          console.warn("wyswietlic że Lobby zostało zakończone przez Hosta modal");
        if ( $('#LobbyDeletedModalCenter').modal )
          $('#LobbyDeletedModalCenter').modal('show');
        return "displaying info";
      }
  
      //setup lobby
      if (!lobby.host) {
        if (self.debug)
          console.warn("Zainicjować wyjście klienta z tego lobby");
        return "lobby does not exist";
      }
      self.lobbyHost = lobby.host;
  
      if ($("#lobbyHostUsername").length)
        $("#lobbyHostUsername").text(self.lobbyHost.username + " " + self.lobbyHost.nickname);
      //------player list
      if($("#lobbyPlayersTable > .player").length)
        $("#lobbyPlayersTable > .player").remove();
  
      if (typeof lobby.players == 'undefined') {
        if (self.debug)
          console.warn("nie znaleziono żadnych graczy w lobby");
        return 0;
      }
      
      let playersLength = lobby.players.length;
      
      var isFirstPlayerInTable = true;

      var nicknameRepeats = (nickname, players) => {

        var counter = 0;
        for ( let i = 0; i < players.length; i++) {
          var player = players[i];
          if ( player.nickname.trim() === nickname.trim())
            counter++;
          if ( counter > 1) {
            break;
          }
        }
        return (counter>1);
      };

      if ($("#lobbyPlayersTable").length) {
          if ( self.lobbyHost.roles.includes("PLAYER")) {
            var specialPlayer = "",
            specialClass = "";
  
            if (self.username === self.lobbyHost.username && self.nickname ===  self.lobbyHost.nickname) {
              specialPlayer = "(me)";
              specialClass = "lobbyMe"
            } else {
              specialPlayer = "(host)";
            }
  
            let playerDiv = $(`<div class="media text-muted `+(!isFirstPlayerInTable?"pt-3":"")+` player `+specialClass+`">
                <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 1"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="white" dy=".3em">1</text></svg>
                <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                  <div class="d-flex justify-content-between align-items-center w-100">
                    <strong class="text-gray-dark">` + specialPlayer + ` `
                      + self.lobbyHost.nickname + `</strong>
                    
                  </div>
                  <span class="d-block text-left"><i>` + ((nicknameRepeats(self.lobbyHost.nickname, lobby.players))?self.lobbyHost.username:"") + `</i></span>
                </div>
              </div>`);
                
              $("#lobbyPlayersTable").append(playerDiv);
              isFirstPlayerInTable = false;
          }
  
          for (let i = 0; i < playersLength; i++) {
            let player = lobby.players[i];
            if (player.username !== self.lobbyHost.username && player.nickname !== self.lobbyHost.nickname) {
              var specialPlayer = "",
              specialClass = "";
              if (self.username === player.username && self.nickname ===  player.nickname) {
                specialPlayer = "(me)";
                specialClass = "lobbyMe"
              }
      
              let playerDiv = $(`<div class="media text-muted `+(!isFirstPlayerInTable?"pt-3":"")+` player `+specialClass+`">
                    <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: `+(i+2)+`"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="white" dy=".3em">`+(i+2)+`</text></svg>
                    <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                      <div class="d-flex justify-content-between align-items-center w-100">
                        <strong class="text-gray-dark">` + specialPlayer + ` `
                          + player.nickname + `</strong>
                        <a href="" class="btnKick" data-username="` + player.username + `">Usuń</a>
                      </div>
                      <span class="d-block text-left"><i>` + ((nicknameRepeats(player.nickname, lobby.players))?player.username:"") + `</i></span>
                    </div>
                  </div>`);
              isFirstPlayerInTable = false;
                  
                $("#lobbyPlayersTable").append(playerDiv);
            }
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
        $("#lobbyMaxPlayers").text("Gracze " + playersLength + "\\" + lobby.maxPlayers);
      }
  
      //------lobby settings dla hosta ustawic
      if (self.isHost) {
        if ( lobby.maxPlayers ) {
          $("#inputMaxPlayers").val(lobby.maxPlayers);
          self.lobbySettings.maxPlayers = lobby.maxPlayers;
          self.lobbySettingsPlaceholder.maxPlayers = lobby.maxPlayers;
        }
  
        if (lobby.allowsRandomPlayers === true || lobby.allowsRandomPlayers === false) {
  
          self.allowsRandomPlayers = lobby.allowsRandomPlayers;
          $("#allowsRandomPlayersCB")[0].checked == lobby.allowsRandomPlayers;
          self.lobbySettings.allowsRandomPlayers = lobby.allowsRandomPlayers;
          self.lobbySettingsPlaceholder.allowsRandomPlayers = lobby.allowsRandomPlayers;
        }
      }
  
      //... pozostałe zmiany jakie mogą zajść w lobby dodac poniżej
    }
  
    self.updateCheck = (data) => {
      
      if ( debug )
        console.log(data);
  
      self.ajaxLobbyLoopTimeout = setTimeout(()=>{ajaxConnectionLoop((data_)=>{self.updateCheck(data_)})},1000);
      if (data.lobbyExists === false) {
        //2021-04-22, (fast fix?) lobby deleted modal showing at the start randomly
        if (self.preventFromShowingLobbyDeletedModal) {
            self.preventFromShowingLobbyDeletedModal=false;
            //new bug fix
            //new 2021-04-27
            resizeWindow();
            return;
        }
        /*TODO 2021-03-01
        bug (jest stan w którym nie istnieje lobby i gra kiedy gra się rozpoczyna)
        fetchuje server wtedy dane z bazy więc nie wiadomo ile czasu trwa ten stan, a serwer nadal jes tw stanie odpowiadać na requesty*/
        
  
  
        console.warn("jest stan w którym nie istnieje lobby i gra, kiedy gra się rozpoczyna");
        if ( $('#LobbyDeletedModalCenter').modal )
          $('#LobbyDeletedModalCenter').modal('show');
      } else if (data.gameStarted == true) {
        
        if ( debug )
          console.log("start");
        $("body").removeClass("lektorBody").removeClass("playerBody");
        clearTimeout(self.ajaxLobbyLoopTimeout);
        self.gameID = data.gameID;
        self.startGame(); //tutaj tworzyć obiekt taska?
      } else if (data.lobbyContentChanged == true){
        self.ajaxReceiveLobbyChange();
      }
    } 
  
    self.updateLobbySettingsAsHost = () => {
      
      self.lobbySettings.maxPlayers = self.lobbySettingsPlaceholder.maxPlayers;
      self.lobbySettings.allowsRandomPlayers = self.lobbySettingsPlaceholder.allowsRandomPlayers;
      //... pozostałe ustawienia które można dodać poniżej
    }
  
    self.setupLobbyForLecturer = () => {
        $(".lectorEnableClass").show();
        $(".lectorDisableClass").hide();
    }
  
    /*     ajax http actions       */
    var sendAjaxStart = () => {
      if (self.isHost == false)
        return;
  
      clearTimeout(self.ajaxLobbyLoopTimeout);
  
      self.showModal();
      $.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/lobby/"+self.lobbyCode+"/start",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (self.debug)
            console.log("sendAjaxStart success");
          // if (data === false) {
            self.ajaxLobbyLoopTimeout = setTimeout(()=>{ajaxConnectionLoop((data_)=>{self.updateCheck(data_)})},1000);
          // }
          self.hideModal();
            //teraz lobbyloopa stopować
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.log("sendAjaxStart error");
          }
  
          self.ajaxLobbyLoopTimeout = setTimeout(()=>{ajaxConnectionLoop((data_)=>{self.updateCheck(data_)})},1000);
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
  
      //coverage jest robione przez moduł istambul / esprima który nie wspiera kodu ECMAScript 2017 i wyżej.
      //wspiera do ECMAScript 2016 
      //var send = {...self.lobbySettingsPlaceholder};
  
      var send = JSON.parse(JSON.stringify(self.lobbySettingsPlaceholder));
  
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
          }
          /*
            callback dla:
               self.ajaxLobbyLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
            lub niczego jeśli z końca gry odpalam LogicGame
          */
          callback(data);//true jeśli coś się zmieniło  
  
        },
        error: function(data, status, err) {
          if (self.debug){
            console.warn("connectionLoop error");
            console.warn(data);
          }
          callback(data);
        }
      });
    }
    self.ajaxReceiveLobbyChange = ( ) => {
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/lobby/" + self.lobbyCode,//"game/getLobby",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetLobbyChange success");
            console.log(data);
          }
          self.lobbySetupAfterChange(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug){
            console.warn("ajaxGetLobbyChange error");
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
          }
          self.hideModal();
  
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxSendJoin error");
          }
          self.hideModal();
        }
      });
    }
    
    /*  initalization  */
    LobbyInit(data);
    
    return self;
  }
  
  LobbyLogic.getInstance = (dataLazy, debug = false, successfulCreationCallback) => {
  
      if (LobbyLogic.singleton)
          return LobbyLogic.singleton;
  
      var lobbyCode = window.location.href.substring(window.location.href.lastIndexOf('/') + 1);
      
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

          return $.ajax({
              type     : "GET",
              cache    : false,
              url      : "/api/v1/playerinfo",
              contentType: "application/json",
              success: function(playerInfo, textStatus, jqXHR) {
                  if (debug){
                      console.log("ajaxReceiveWhoAmI success");
                  }
              },
              error: function(jqXHR, status, err) {
                  if (debug){
                      console.warn("ajaxReceiveWhoAmI error");
                  }
  
              }
          });
      }
  
      var ajaxReceiveAccountInfo = ( ) => {
  
          return $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/account/info",
            contentType: "application/json",
            success: function(accountInfo, textStatus, jqXHR) {
                if (debug) {
                    console.log("ajaxReceiveAccountInfo success");
                    console.log(accountInfo);
                }
            },
            error: function(jqXHR, status, err) {
                if (debug) {
                  console.warn("ajaxReceiveAccountInfo error");
                }
            }
          });
      }
  
      if ( dataLazy )
        return LobbyLogic(dataLazy, debug, successfulCreationCallback);
      else {
        showModal();
        return Promise.all([ajaxReceiveWhoAmI(),ajaxReceiveAccountInfo()]).then((values)=>{
          var playerInfo = values[0];
          var accountInfo = values[1];
          if ( debug ) {
            console.log(values);
            console.log("done");
          }

          var data = {
            playerInfo: playerInfo,
            accountInfo, accountInfo,
            lobbyCode: lobbyCode,
            showModal: showModal,
            hideModal: hideModal
          }

          LobbyLogic.singleton = LobbyLogic(data, debug, successfulCreationCallback);
          hideModal();

          return LobbyLogic.singleton;
        }).catch((e) => {
          console.warn("failed");
          console.warn(e);
          hideModal();
          // all requests finished but one or more failed
        });
      }
      
  }

    return {
      LobbyLogic: LobbyLogic, 
      getInstance: LobbyLogic.getInstance
    }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {LobbyModule};