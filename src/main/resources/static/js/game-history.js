 
const GameHistoryLogic = (playerInfo_, pageNumber_, debug_, deps = {}) => {
  
    /*       logic variables          */
    var self = playerInfo_;
    self.nickname;
    self.username;
    self.debug = debug_;
    self.pageNumber = pageNumber_;
    self.initDone = false;

    if ( deps.NavbarLogic && typeof NavbarLogic == "undefined")
        NavbarLogic = deps.NavbarLogic;
    if ( deps.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
        PageLanguageChanger = deps.PageLanguageChanger;

    /*       logic functions          */
    self.gameHistoryInit = (playerInfo) => {
      
      var initActions = ( ) => {
        if (self.initDone === true) 
            return;
        else
            self.initDone = true;

        self.nickname = playerInfo.nickname;
        self.username = playerInfo.username;

        ajaxGetGameHistory((data)=>self.setupGameHistory(data));
        NavbarLogic(playerInfo, self.debug);
      }

      if ( typeof PageLanguageChanger != "undefined")
          PageLanguageChanger(false, self.debug, false, ()=>{initActions(); self.InitWithPageLanguageChanger();});
      else
          initActions();
      
    }
    
    self.InitWithPageLanguageChanger = () => {
      //zmiana tekstu w tablicy
    }

    self.setupGameHistory = (data) => {

        if ( data == null)
            return;

        $("#currentUser").html(self.nickname + " <small>" + self.username + "</small>")
        var gameTable = $("#gameTable");
        gameTable.html("");
        var newTr, newTdCount, newTdScore;

        if (data.content.length <= 0) {
          gameTable.append("Nie rozegrano żadnych gier.");
          return;
        }
        for (let i = 0; i < data.content.length; i++) {
            var gameID = data.content[i].id;
             var gameDate = data.content[i].date;
            newTr = $('<tr>');

            newTdCount = $("<td>");
            newTdCount.append(i+1);

            newTdScore = $("<td>");
            newTdScore.append('<a href="/game/results/' + gameID + '">Gra rozegrana ' + gameDate + '</a>');

            newTr.append(newTdCount).append(newTdScore);
            gameTable.append(newTr);
        }
    }

    /*       event listeners          */
    if ($("#gameHistory").length)
      $("#gameHistory").on("click",(e) => {

        /*TODO:
            nawigacja po stronach historii
        */

        if (self.debug == true)
          console.log("gameHistory")
          window.location.replace("/game/history/1");
      });
  
    /*     ajax http actions       */
    
    /*   ajax http requests       */
    var ajaxGetGameHistory = ( callback ) => {
      
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/game/history/" + self.pageNumber,
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxSendJoin success");
            console.log(data);
          }

          callback(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxSendJoin error");
          }
        }
      });
    }
    
    /*  initalization  */
    self.gameHistoryInit(playerInfo_);
    
    return self;
}
  
GameHistoryLogic.getInstance = (debug = false) => {
  
    if (GameHistoryLogic.singleton)
      return GameHistoryLogic.singleton;
    
    var pageNumber = window.location.href.substring(this.location.href.lastIndexOf('/') + 1);

    var ajaxReceiveWhoAmI = ( ) => {
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
          GameHistoryLogic.singleton = GameHistoryLogic(playerInfo, pageNumber, debug);
        },
        error: function(jqXHR, status, err) {
          if (debug){
            console.warn("ajaxReceiveWhoAmI error");
          }
        }
      });
    }
    
    ajaxReceiveWhoAmI();
}