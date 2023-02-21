const LobbyModule = (function(deps = {}) {

  var debug = false;
  if ( deps.debug )
      debug = true;

  if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
      throw Error("jQuery not defined");
  else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
      deps.$ = $;

  if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
      throw Error("window not defined");
  else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
      deps.window = window;

  

  var Ajax = function(){
    var self = {};

    self.getWhoAmI = ( callback ) => {
        
        return deps.$.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/playerinfo",
            contentType: "application/json",
            success: function(playerInfo, textStatus, jqXHR) {
                if (debug){
                    console.log("getWhoAmI success");
                    console.log(playerInfo);
                }
                if ( callback )
                    callback(playerInfo);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("getWhoAmI error");
                    console.warn(jqXHR);
                }
                if ( callback )
                    callback(false);
            }
        });
    }

    self.getAccountInfo = (callback) => {
        return deps.$.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/account/info",
            contentType: "application/json",
            success: function(accountInfo, textStatus, jqXHR) {
                if (debug) {
                    console.log("getAccountInfo success");
                    console.log(accountInfo);
                }
                if ( callback )
                    callback(accountInfo);
            },
            error: function(data, status, err) {
                if (debug) {
                    console.warn("getAccountInfo error");
                    console.warn(data);
                }
                if ( callback )
                    callback(false);
            }
        });
    } 

    /*     ajax http actions       */
    self.sendAjaxStart = (lobbyCode, callback) => {
  
      return deps.$.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/lobby/"+lobbyCode+"/start",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug)
            console.log("sendAjaxStart success");


          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug) {
            console.log("sendAjaxStart error");
          }

          if ( callback )
            callback(false);
        }
      });
    }
    self.sendAjaxLeave = (lobbyCode, callback) => {
      console.log("sendAjaxLeave");
      return deps.$.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/lobby/"+lobbyCode+"/leave",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug)
            console.log("sendAjaxLeave success");

          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug)
            console.log("sendAjaxLeave error");
          if ( callback )
            callback(false);
        }
      });
    }
    self.sendAjaxKickPlayer = (lobbyCode, player, callback) => {
      
      var send = player;
      return deps.$.ajax({
        type     : "DELETE",
        cache    : false,
        url      : "/api/v1/lobby/" + lobbyCode + "/players",
        data     : send,
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug){
            console.log("sendAjaxKickPlayer success");
          }

          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug)
            console.log("sendAjaxKickPlayer error");

          if ( callback )
            callback(false);
        }
      });
    }
    self.sendAjaxSettingsChange = (lobbySettingsPlaceholder, lobbyCode, callback) => {
  
      var send = JSON.parse(JSON.stringify(lobbySettingsPlaceholder));
      return deps.$.ajax({
        type     : "PUT",
        cache    : false,
        url      : "/api/v1/lobby/" + lobbyCode,
        data     : JSON.stringify(send),
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug == true) {
            console.log("SettingsChange success");
          }
          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug == true)
            console.log("SettingsChange error");
          if ( callback )
            callback(false);
        }
      });
    }
    
    /*   ajax http requests       */
    self.ajaxConnectionLoop = (lobbyCode, callback ) => {
      
      return deps.$.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/lobby/" + lobbyCode + "/changes",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug) {
            console.log("ajaxConnectionLoop success");
          }
          if ( callback )
            callback(data);
  
        },
        error: function(data, status, err) {
          if (debug){
            console.warn("connectionLoop error");
            console.warn(data);
          }
          if ( callback )
            callback(data);
        }
      });
    }
    self.ajaxReceiveLobbyChange = (lobbyCode, callback) => {

      return deps.$.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/lobby/" + lobbyCode,
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug) {
            console.log("ajaxGetLobbyChange success");
            console.log(data);
          }
          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug){
            console.warn("ajaxGetLobbyChange error");
          }
          if ( callback )
            callback(false);
        }
      });
    }
    self.ajaxSendJoin = (lobbyCode, callback ) => {

      return deps.$.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/lobby/join/" + lobbyCode,
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (debug) {
            console.log("ajaxSendJoin success");
            console.log(data);
          }
          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (debug) {
            console.warn("ajaxSendJoin error");
            console.warn(jqXHR);
          }
          if ( callback )
            callback(false);
        }
      });
    }

    self.getTasksetsInfo = ( callback ) => {
    
      return deps.$.ajax({
          type: "GET",
          cache: false,
          url: "/api/v1/tasksets/info",
          contentType: "application/json",
          success: function (data, status)
          {   
              if (debug) {
                  console.log("getTasksetsInfo success");
                  console.log(data);
              }

              else if ( callback )
                  callback(data);
          },
          error: function (xhr, desc, err)
          {
              if (debug ) {
                  console.log("getTasksetsInfo error");
                  console.log(xhr);
              }
              if ( callback)
                  callback(false);
          }
      }); 
    }

    self.putSetLobbyTasksets = (tasksetNames, lobbyCode, callback) => {

      var send = tasksetNames;
  
      return deps.$.ajax({
        type     : "PUT",
        cache    : false,
        url      : "/api/v1/lobby/"+lobbyCode+"/tasksets",
        data     : JSON.stringify(send),
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if ( debug ) {
            console.log("putSetLobbyTasksets success");
            console.log(data);
          }
          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if ( debug )
            console.log("putSetLobbyTasksets error");
          if ( callback )
            callback(false);
        }
      });
    }

    self.getGroupInfo = (code, callback) => {

      return deps.$.ajax({
          type     : "GET",
          cache    : false,
          url      : "/api/v1/groups/"+code+"/info",
          contentType: "application/json",
          success: function(data, textStatus_, jqXHR_) {
              if (debug) {
                  console.log("getGroupInfo success");
                  console.log(data);
              }
              if ( callback )
                  callback(data);
          },
          error: function(data, status, err) {
              if (debug) {
                  console.warn("getGroupInfo error");
                  console.warn(data);
              }
              if ( callback )
                  callback(false);
          }
      });
    } 

    self.setTasksetForGame
    return self;
  }();

  const LobbyLogic = (data, successfulCreationCallback) => {
  
    /*  singleton   */
    if (LobbyLogic.singleton) {
      if ( successfulCreationCallback )
        successfulCreationCallback(LobbyLogic.singleton);
      return LobbyLogic.singleton;
    }
    var self = {};
    if (!LobbyLogic.singleton && !data)
      return LobbyLogic.getInstance(data, successfulCreationCallback);
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
    self.gameStarted;
  
    self.ajaxLobbyLoopTimeout;
    self.lobbyCode;
    self.nickname;
    self.username;
    self.gameObject;
    self.gameID;
    self.possiblyJoinedFromEndGame;
    self.preventFromShowingLobbyDeletedModal = true;
    self.selectedTasksets;
    self.allTasksets;
    self.isGroupLobby;
    self.groupCode;
    self.playerInfo;

    /*       logic functions          */
    var LobbyInit = (data) => {
      
      self.initLobbyInfo = data.lobbyInfo;
      self.playerInfo = data.playerInfo;
      self.isHost = data.playerInfo.isHost; 
      self.nickname = data.playerInfo.nickname;
      self.username = data.playerInfo.username;
      self.gameStarted = data.playerInfo.gameStarted;
      self.possiblyJoinedFromEndGame = data.playerInfo.gameCode? false : true;
      self.isGroupLobby = data.lobbyInfo.isGroupLobby;
      self.groupCode = data.lobbyInfo.groupCode;
      self.groupInfo = data.groupInfo;
      if (self.isGroupLobby) {
        self.selectedTasksets = data.lobbyInfo.tasksets;
        self.allTasksets = data.tasksets;
      }
  
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
  
      deps.$("#lobbyTop").removeClass("collapse");
      deps.$("#lobbyCenter").removeClass("collapse");
      deps.$("#lobbyBottom").removeClass("collapse");
  
      if (deps.$(".hideBeforeLoad").length)
        deps.$('.hideBeforeLoad').fadeIn();
      
      if (!self.isHost && !(self.playerInfo.gameCode === self.lobbyCode)) {
        
        self.showModal();
        Ajax.ajaxSendJoin(self.lobbyCode,(data)=>{self.hideModal();});
      }
  
      if (typeof self.isHost == undefined && debug)
        console.warn("Critical error, invalid data received from server isHost == undefined");
      
      if (self.isLecturer) 
        deps.$("body").addClass("lektorBody");
      else
        deps.$("body").addClass("playerBody");
  
      if (self.isHost) {
        deps.$("#btnStart").show();
        deps.$("#btnSettings").show();
        deps.$("#lobbyHostUsername").text(self.nickname);
          if (self.isLecturer)
            self.setupLobbyForLecturer();
          else {
            deps.$("#dualList").remove();
            deps.$(".lectorEnableClass ").remove();
          }

      } else {
        deps.$("#lobbyMainHost").removeClass("col-10").addClass("col-12");
        deps.$("#lobbySettings").hide();
        deps.$(".btnKick").addClass("collapse");
        deps.$("#dualList").remove();
        deps.$(".lectorEnableClass ").remove();
      }
      if (deps.$('.btn').length && deps.$('.btn').popover)
        deps.$('.btn').popover();
  
      if (deps.$("#lobbyCode").length)
        deps.$("#lobbyCode").text("Kod: " + self.lobbyCode);
  
      self.lobbySetupAfterChange(self.initLobbyInfo);
  
      tooltipsUpdate();
  
      deps.$('#exampleModalCenter').on('shown.bs.modal', function (e) {
        deps.$('#btnSettings').one('focus', function (e) {
          deps.$(this).blur();
        });
      });
      
      listenersSetup();
      Ajax.ajaxConnectionLoop(
          self.lobbyCode,
          (data)=>{self.updateCheck(data)}
        );
      
      if (successfulCreationCallback)
          successfulCreationCallback(self);
    }
  
    /*       event listeners          */
    var listenersSetup = () => {
  
      if (deps.$("#btnSendStartGame").length)
        deps.$("#btnSendStartGame").on("click",() => {
  
          if (self.isHost == true) {

            clearTimeout(self.ajaxLobbyLoopTimeout);
            self.showModal();

            Ajax.sendAjaxStart(
              self.lobbyCode,
              (data) => {

                self.ajaxLobbyLoopTimeout = setTimeout(()=>{
                  Ajax.ajaxConnectionLoop(
                    self.lobbyCode,
                    (data_)=>{self.updateCheck(data_)}
                  )},1000);
                self.hideModal();

              });
          }
        });
      if (deps.$("#btnSendleave").length)
        deps.$("#btnSendleave").on("click",()=>{

          Ajax.sendAjaxLeave(self.lobbyCode, (data) => {self.leaveLobby();});
        });
      if (deps.$("#btnCopyCode").length)
        deps.$("#btnCopyCode").on("click",(e) => {
  
            self.copyTextToClipboard(self.lobbyCode);
        });
      if (deps.$("#btnSaveSettings").length)
        deps.$("#btnSaveSettings").on("click",()=>{
          
          self.saveAndSendLobbySettings();
          
        });
      if (deps.$("#btnEndGame").length)
        deps.$("#btnEndGame").on("click",()=>{
  
          if (self.gameStatus == "ended")
            sendAjaxEndGame();
        });
      if (deps.$("#btnSendLobbyDeleted").length)
        deps.$("#btnSendLobbyDeleted").on("click",()=>{
          self.leaveLobby();
        });
      if (deps.$("#btnSettings").length)
        deps.$("#btnSettings").on('click',(e) => {
          deps.$("#inputMaxPlayers").val(self.lobbySettings.maxPlayers);
          deps.$("#allowsRandomPlayersCB")[0].checked = self.allowsRandomPlayers;
        });
      if (deps.$("#customTasksCB").length)
        deps.$("#customTasksCB").on('click',(e) => {
            if (deps.$("#customTasksCB")[0].checked) {
                deps.$("#dualList").show();
            } else {
                deps.$("#dualList").hide();
            }
        });
  
      if (deps.$("#allowsRandomPlayersCB").length) 
        deps.$("#allowsRandomPlayersCB").on('click', (e)=> {
            self.allowsRandomPlayers = deps.$("#allowsRandomPlayersCB")[0].checked;
        });
  
      deps.window.onresize = resizeWindow;
    }
    
    var tooltipsUpdate = () => {
  
      if ( deps.$('[data-toggle="tooltip"]').tooltip !== null && deps.$('[data-toggle="tooltip"]').tooltip !== undefined)
        deps.$('[data-toggle="tooltip"]').tooltip({
              trigger : 'hover'
          });
    }
  
    var resizeWindow = () => {
        
        deps.$("html").height("100%");

        function isInt(n) {
          return n % 1 === 0;
        }
      
        var h1 = deps.$(deps.window.document).height();
        if ( isInt (h1))
          h1 -= 1;
        deps.$("html").height(h1);
    } 
    
    self.startGame = (cbGame) => {

      if ( self.isLecturer )
        deps.window.location.replace("/game/results/" + self.gameID);
      
      if ( !deps.window.document.hasFocus() )
        startPageFlashing();

      deps.$("#bigChangeDiv").removeClass("text-center").removeClass("hideBeforeLoad");
      deps.$("#lobbyTop").hide();
      deps.$("#lobbyCenter").hide();
      deps.$("#lobbyBottom").hide();
      deps.$("#gameBottom").show();
      deps.$("#gameTop").show();
      deps.$("#gameCenter").show();
  
      self.gameObject = GameModule(deps.$, deps.window).getInstance(self, false, cbGame);
    }
  
    self.copyTextToClipboard = (text) => {
      
      var textArea = deps.window.document.createElement("textarea");
      textArea.value = text
      deps.window.document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      
      var successful = false;
      try {
        successful = deps.window.document.execCommand('copy');
        var msg = successful ? 'successful' : 'unsuccessful';
        
      } catch (err) {
        if (debug)
          console.log('Oops, unable to copy');
      }
  
      deps.window.document.body.removeChild(textArea);
      return successful;
    }
    
    var startPageFlashing = () => {

      var getIconUrl = () => {
        var link = deps.window.document.querySelector("link[rel~='icon']");
        if (!link) 
          throw Error("no Original Icon");
        return link.href;
      }

      var original = deps.window.document.title;
      var originalURL = getIconUrl();
      var timeout;

      var flashTitle = function (newMsg, newURL, howManyTimes) {

        function step() {
          deps.window.document.title = (deps.window.document.title == original) ? newMsg : original;
          changeIcon(getIconUrl() == originalURL? newURL : originalURL);

          if (--howManyTimes > 0) {
              timeout = setTimeout(step, 1000);
          };
            
          if ( deps.window.document.hasFocus() )
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
          deps.window.document.title = original;
          changeIcon(originalURL);
      };

      var changeIcon = (newURL) => {
        var link = deps.window.document.querySelector("link[rel~='icon']");
        link.href = newURL;
      }

      

      flashTitle("Game started!", "/assets/myIcons/CompeticoLogoMessage.svg", 9999);
    }

    self.leaveLobby = () => {
      
      deps.window.location.replace("/lobby");
    }
  
    self.lobbySetupAfterChange = (lobby) => {
      
      if (!lobby.exists && self.possiblyJoinedFromEndGame === false) {
        if (debug)
          console.warn("lobby nie znalezione!");
        if (self.preventFromShowingLobbyDeletedModal) {
            self.preventFromShowingLobbyDeletedModal=false;
            return "prevented from displaying info";
        }
        if (debug)
          console.warn("wyswietlic że Lobby zostało zakończone przez Hosta modal");
        if ( deps.$('#LobbyDeletedModalCenter').modal )
          deps.$('#LobbyDeletedModalCenter').modal('show');
        return "displaying info";
      }
  
      if (!lobby.host) {
        if (debug)
          console.warn("Zainicjować wyjście klienta z tego lobby");
        return "lobby does not exist";
      }
      self.lobbyHost = lobby.host;
  
      if (deps.$("#lobbyHostUsername").length)
        deps.$("#lobbyHostUsername").text(self.lobbyHost.username + " " + self.lobbyHost.nickname);
      if(deps.$("#lobbyPlayersTable > .player").length)
        deps.$("#lobbyPlayersTable > .player").remove();
  
      if (typeof lobby.players == 'undefined') {
        if (debug)
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

      if (deps.$("#lobbyPlayersTable").length) {

          var withLecturer = 0;
          if ( self.lobbyHost.roles.includes("PLAYER")) {
            var specialPlayer = "",
            specialClass = "";
  
            if (self.username === self.lobbyHost.username && self.nickname ===  self.lobbyHost.nickname) {
              specialPlayer = "(me)";
              specialClass = "lobbyMe"
            } else {
              specialPlayer = "(host)";
            }
  
            let playerDiv = deps.$(`<div class="media text-muted `+(!isFirstPlayerInTable?"pt-3":"")+` player `+specialClass+`">
                <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: 1"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="white" dy=".3em">1</text></svg>
                <div class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">
                  <div class="d-flex justify-content-between align-items-center w-100">
                    <strong class="text-gray-dark">` + specialPlayer + ` `
                      + self.lobbyHost.nickname + `</strong>
                    
                  </div>
                  <span class="d-block text-left"><i>` + ((nicknameRepeats(self.lobbyHost.nickname, lobby.players))?self.lobbyHost.username:"") + `</i></span>
                </div>
              </div>`);
                
              deps.$("#lobbyPlayersTable").append(playerDiv);
              isFirstPlayerInTable = false;

              withLecturer++;
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
      
              let playerDiv = deps.$(`<div class="media text-muted `+(!isFirstPlayerInTable?"pt-3":"")+` player `+specialClass+`">
                    <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder: `+(i+2)+`"><title>Placeholder</title><rect width="100%" height="100%" fill="#007bff"></rect><text x="50%" y="50%" fill="white" dy=".3em">`+(i+1+ withLecturer)+`</text></svg>
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
                  
              deps.$("#lobbyPlayersTable").append(playerDiv);
            }
          }
      }
      
      
      if (self.isHost){
        if (deps.$(".btnKick").length)
          deps.$(".btnKick").on("click", (e) => {
            e.preventDefault();
            
            if (self.isHost === true)
              Ajax.sendAjaxKickPlayer(
                  self.lobbyCode, 
                  deps.$(e.target).data("username")
                );
          });
      } else {
        if (deps.$(".btnKick").length)
          deps.$(".btnKick").addClass("collapse");
      }
      if (lobby.maxPlayers != null) {
        deps.$("#lobbyMaxPlayers").text("Gracze " + playersLength + "/" + lobby.maxPlayers);
      }
  
      if (self.isHost) {
        if ( lobby.maxPlayers ) {
          deps.$("#inputMaxPlayers").val(lobby.maxPlayers);
          self.lobbySettings.maxPlayers = lobby.maxPlayers;
          self.lobbySettingsPlaceholder.maxPlayers = lobby.maxPlayers;
        }
  
        if (lobby.allowsRandomPlayers === true || lobby.allowsRandomPlayers === false) {
  
          self.allowsRandomPlayers = lobby.allowsRandomPlayers;
          deps.$("#allowsRandomPlayersCB")[0].checked == lobby.allowsRandomPlayers;
          self.lobbySettings.allowsRandomPlayers = lobby.allowsRandomPlayers;
          self.lobbySettingsPlaceholder.allowsRandomPlayers = lobby.allowsRandomPlayers;
        }
      }
    }
  
    self.updateCheck = (data) => {
  
      self.ajaxLobbyLoopTimeout = setTimeout(()=>{
        Ajax.ajaxConnectionLoop(
            self.lobbyCode,
            (data_)=>{self.updateCheck(data_)}
          )},1000);
      if (data.lobbyExists === false) {
        if (self.preventFromShowingLobbyDeletedModal) {
            self.preventFromShowingLobbyDeletedModal=false;
            resizeWindow();
            return;
        }
  
        if ( debug )
          console.warn("jest stan w którym nie istnieje lobby i gra, kiedy gra się rozpoczyna");
        if ( deps.$('#LobbyDeletedModalCenter').modal )
          deps.$('#LobbyDeletedModalCenter').modal('show');
      } else if (data.gameStarted == true) {
        
        deps.$("body").removeClass("lektorBody").removeClass("playerBody");
        clearTimeout(self.ajaxLobbyLoopTimeout);
        self.gameID = data.gameID;
        self.startGame(); 
      } else if (data.lobbyContentChanged == true){
        Ajax.ajaxReceiveLobbyChange(
          self.lobbyCode,
          (data) => {
            self.lobbySetupAfterChange(data)
          }
        );
      }
    } 
  
    self.updateLobbySettingsAsHost = () => {
      
      if ( data === false ) {
        displayInfoAnimation("Nie udało się zapisać zmian", false);
        return;
      }
      displayInfoAnimation("Zapisano zmiany", true);

      self.lobbySettings.maxPlayers = self.lobbySettingsPlaceholder.maxPlayers;
      self.lobbySettings.allowsRandomPlayers = self.lobbySettingsPlaceholder.allowsRandomPlayers;

      if ( self.dualListLogic ) {
        self.selectedTasksets = self.dualListLogic.getOutput();
      }
    }
    
    self.saveAndSendLobbySettings = () => {

      if (self.isHost == true) {
        self.lobbySettingsPlaceholder.maxPlayers = parseInt(deps.$("#inputMaxPlayers").val());
        
        var tasksets = [];
        if ( self.dualListLogic ) {
          tasksets = self.dualListLogic.getOutput();
        }
        
        self.lobbySettingsPlaceholder.allowsRandomPlayers = deps.$("#allowsRandomPlayersCB")[0].checked;
        if (self.isHost === true)
          Ajax.sendAjaxSettingsChange(
            self.lobbySettingsPlaceholder,
            self.lobbyCode,
            (data) => {
              if ( data === false ) {
                displayInfoAnimation("Nie udało się zapisać zmian", false);
                return;
              }
              Ajax.putSetLobbyTasksets(
                tasksets,
                self.lobbyCode,
                self.updateLobbySettingsAsHost
              );
            }
          );
      }
    };

    self.setupLobbyForLecturer = () => {
      
      deps.$(".lectorEnableClass").show();
      deps.$(".lectorDisableClass").hide();

      if ( self.isGroupLobby && self.isHost) {

        if ( !self.dualListLogic ) {
          self.dualListLogic = DualListModule(deps).DualListLogic({selector:"#dualList", tasksets: Object.keys(data.tasksets)});

          var prepareDualList = () => {

            self.dualListLogic.refresh();
            self.dualListLogic.insertOptions(Object.keys(self.allTasksets));
      
            
            self.dualListLogic.move(self.selectedTasksets);
      
          }
          prepareDualList();
        }

        deps.$("#exampleModalLongTitle").text("Ustawienia Lobby grupy: " + self.groupInfo.name);

        if ( self.selectedTasksets.length) {
          deps.$("#customTasksCB")[0].checked = true;
          deps.$("#dualList").show();
        } else {
          deps.$("#customTasksCB")[0].checked = false;
          deps.$("#dualList").hide();
        }
        
      } else {
        deps.$(".lectorEnableClass").remove();
        deps.$("#dualList").remove();

      }
    } 

    var displayInfoAnimation = (text, success = true) => {
      var previousMessages = deps.$(".failSuccessInfo");
      previousMessages.each((b,t)=>{
          deps.$(t).css({marginTop: '+=50px'});
      });

      var failInfoDiv = deps.$(`<div class="failSuccessInfo alert alert-`+(success?"success":"danger")+`">` + text + `</div>`)
      deps.$("body").append(failInfoDiv)
  
      failInfoDiv.animate({
        top: "6%",
        opacity: 1
      }, 2000, function() {
        // Animation complete.
        setTimeout(function(){
          failInfoDiv.animate({
            top: "9%",
            opacity: 0
          }, 1000, function() {
            // Second Animation complete.
            failInfoDiv.remove();
          });
        },2000)
        
      });
  }
    /*  initalization  */
    LobbyInit(data);
    
    return self;
  }
  
  LobbyLogic.getInstance = (dataLazy, successfulCreationCallback) => {
  
      if (LobbyLogic.singleton)
          return LobbyLogic.singleton;
  
      var lobbyCode = deps.window.location.href.substring(deps.window.location.href.lastIndexOf('/') + 1);
      
      var showModal = () => {
          if (typeof deps.$("").modal != 'undefined') {
            deps.$('.hideBeforeLoadModal').modal('show');
          }
      }
      var hideModal = () => {
          if (typeof deps.$("").modal != 'undefined') {
            deps.$(".hideBeforeLoadModal").on('shown.bs.modal', function() { deps.$(".hideBeforeLoadModal").modal('hide'); }).modal('hide');
          }
      }
  
      
  
      if ( dataLazy )
        return LobbyLogic(dataLazy, successfulCreationCallback);
      else {
        showModal();
        return Promise.all([
          Ajax.getWhoAmI(),
          Ajax.getAccountInfo(),
          Ajax.ajaxReceiveLobbyChange(lobbyCode),
        ]).then((values)=>{
          var playerInfo = values[0];
          var accountInfo = values[1];
          var lobbyInfo = values[2];
          
          var data = {
            playerInfo: playerInfo,
            accountInfo, accountInfo,
            lobbyCode: lobbyCode,
            showModal: showModal,
            hideModal: hideModal,
            lobbyInfo: lobbyInfo
          }

          if ( lobbyInfo.isGroupLobby && playerInfo.isHost && accountInfo.roles.includes("LECTURER") ) {

            return Promise.all([
              Ajax.getTasksetsInfo(),
              lobbyInfo.isGroupLobby?Ajax.getGroupInfo(lobbyInfo.groupCode):null
            ]).then((values)=>{

              data.tasksets = values[0];
              data.groupInfo = values[1];

              LobbyLogic.singleton = LobbyLogic(data, successfulCreationCallback);
              hideModal();

            }).catch((e) => {

              console.warn("failed to init 2");
              console.warn(e);
              hideModal();
              if ( successfulCreationCallback )
                successfulCreationCallback(false);
            });
          } else {
            
            LobbyLogic.singleton = LobbyLogic(data, successfulCreationCallback);
            hideModal();
          }


          return LobbyLogic.singleton;
        }).catch((e) => {
          console.warn("failed to init 1");
          console.warn(e);
          hideModal();
          if ( successfulCreationCallback )
            successfulCreationCallback(false);
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