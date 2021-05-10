
const GameResultsLogic = ( playerInfo_, gameID_, debug_) => {

    /*       logic variables          */
    var self = playerInfo_;
    self.gameID = gameID_;
    self.ajaxGameEndLoopTimeout;
    self.myScore;
    self.allResults;
    self.allPreviousResults;
    self.GameFinished = false ;
    self.debug = debug_;
    /*       logic functions          */
    self.gameResultsInit = (task) => {
  
      console.log("gameResultsInit");

      //losowo podczas startu gry występuje bug z modalem blokującym interfejs użytkownika, tutaj prowizorycznie się go pozbywam
      //zauważyłem że dzieje się to tylko gdy mam przeglądarke w pomniejszonym oknie, jak mam fullscreen to jest ok
      //ale też nie zawsze, rozszerzyłem o troche ekram, cofnąłem do tyłu i znow bug
      if ($(".modal-backdrop")[0]) {
        $(".modal-backdrop").remove()
        console.warn("Usunąłem natrętnego modala!");
      }
  
      //jeśli chcemy wywysłać info np o tym żew gracz nie ma focusa na grze...
      //ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
      //self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(self.lobbyCode)},10000);

      console.log("setupEndGame");
      
      //dalej będzie ustawianie i wysyłanie requestów z użyciem tego gameID
      ajaxGetPersonalGameResults(
        (gameData)=>{self.setPesonalResult(gameData)},
        self.gameID
      );
      //then
      ajaxGetTotalGameResults(
        (gameData)=>{self.setAllResults(gameData)},
        self.gameID
      );
      //then
      /* keep asking until game is finished for all players */
      ajaxCheckTotalScoreChanges(
        (changeOccurred) => {self.checkTotalScoreChanges(changeOccurred)},
        self.gameID)
    }
   
    self.setPesonalResult = (myScore) => {
        self.myScore = myScore;
        /*TODO 
            stawić własny winik do tabeli
        */
        
        var personalScoreTable = $("#personalScoreTable");

        personalScoreTable.html("");
        var newTr, newTdCount, newTdScore, newTdBonus, newTdTime;
        console.log(myScore);
        for (let i = 0; i < myScore.length; i++) {
            var currentTask = myScore[i];

            console.log(currentTask);

            var completionStyle = "redTask";
            if ( currentTask.completion == 1)
                completionStyle = "greenTask";
            else if ( currentTask.completion >= 0.85)
                completionStyle = "limeTask";
            else if ( currentTask.completion >= 0.65)
                completionStyle = "whiteTask";
            else if ( currentTask.completion >= 0.50)
                completionStyle = "yellowTask";
            else if ( currentTask.completion >= 0.25)
                completionStyle = "orangeTask";

            newTr = $('<tr class="'+ completionStyle +'">');
    
            newTdCount = $("<td>");
            newTdCount.append(i+1);
            
            newTdScore = $("<td>");
            newTdScore.append("<strong>" + parseFloat((currentTask.completion*100).toFixed(1)) + "%</strong>");

            newTdTime = $("<td>");
            newTdTime.append(self.calculateTime(currentTask.timeTaken));

            newTdBonus = $("<td>");
            newTdBonus.append('x'+currentTask.difficulty/100);
    
            newTr.append(newTdCount).append(newTdScore).append(newTdTime).append(newTdBonus);
            personalScoreTable.append(newTr);
        }
    }
    
    self.setAllResults = (gameData) => {
      self.allPreviousResults = self.allResults;
      self.allResults = gameData;
  
      console.log(self.allPreviousResults);
      console.log(self.allResults);
     
      var scoreTable = $("#scoreTable");
      if (self.allPreviousResults) { //update
        //var scoreTrs = $("#scoreTable").children();
  
        scoreTable.html("");
        var newTr,newTdCount,newTdNickname,newTdTotalScore,newTdTotalTime;
        for (let i = 0; i < self.allResults.length; i++) {
          console.log("stary !! wynik " + i);
          console.log(self.allResults[i]);
  
          //to nie będzie działać bo będę dostawać ich w innej kolejności
          //więc musiałbym pętlą przelatywać po username i nickach
          if ( self.allResults[i].totalScore != self.allPreviousResults[i].totalScore) {
            console.log("1zaszła zmiana wyniku dla: " + self.allResults[i].username)
            console.log("2zaszła zmiana wyniku dla: " + self.allPreviousResults[i].username)
          }
  
          if (self.username == self.allResults[i].username && self.nickname == self.allResults[i].nickname)
            newTr = $('<tr class="lobbyMe">');
          else if (self.allResults[i].hasFinished === undefined)
            newTr = $('<tr>');
          else if (self.allResults[i].removedForInactivity)
            newTr = $('<tr class="gameKicked">');
          else if (!self.allResults[i].hasFinished)
            newTr = $('<tr class="gameOtherNotFinished">');
          else
            newTr = $('<tr>');
  
          newTdCount = $("<td>");
          newTdCount.append(i+1);
          newTdNickname = $("<td>");
          newTdNickname.append("<strong>" +self.allResults[i].nickname + "</strong> <i>" + self.allResults[i].username + "</i>");
          newTdTotalScore = $("<td>");
          newTdTotalScore.append(self.allResults[i].totalScore);
          newTdTotalTime = $("<td>");
          newTdTotalTime.append(self.calculateTime(self.allResults[i].totalTime));
  
          newTr.append(newTdCount).append(newTdNickname).append(newTdTotalScore).append(newTdTotalTime);
          scoreTable.append(newTr);
        }
        /*TODO, 
          zrobić żeby kolory się zmieniały zamiast całego diva odświeżać
  
          muszę znaleźc najpierw u którego użytkownika zaszła zmiana
  
          potem znaleźć odpowiedni element który danego użytkownika zawiera
       
        var currentTr;
        for (let i = 0; i < self.allResults.length; i++) {
          var nickname = self.allResults[i].nickname;
          var username = self.allResults[i].username;
          //szukam nickname
          for (let j = 0; j < self.allResults.length; j ++) {
            currentTr = $($("#scoreTable > tr"))[j];
            var usernameNicknameTd = $($(currentTr).children()[1]);
            if (usernameNicknameTd.text() == nickname + " " + username) {
              //znalazłem tr
  
              if (self.allResults[i].totalScore != ) 
  
              break;
            }
          }
  
          console.log("wynik " + i);
          console.log(self.allResults[i]);
        }
         */
      } else { //init tr'ów
        scoreTable.html("");
        var newTr,newTdCount,newTdNickname,newTdTotalScore,newTdTotalTime;
        for (let i = 0; i < self.allResults.length; i++) {
          console.log("nowy !! wynik " + i);
          console.log(self.allResults[i]);
  
          if (self.username == self.allResults[i].nickname && self.nickname == self.allResults[i].username)
            newTr = $('<tr class="lobbyMe">');
          else if (self.allResults[i].hasFinished === undefined)
            newTr = $('<tr>');
          else if (self.allResults[i].removedForInactivity)
            newTr = $('<tr class="gameKicked">');
          else if (!self.allResults[i].hasFinished)
            newTr = $('<tr class="gameOtherNotFinished">');
          else
            newTr = $('<tr>');
  
          newTdCount = $("<td>");
          newTdCount.append(i+1);
          newTdNickname = $("<td>");
          newTdNickname.append("<strong>" +self.allResults[i].nickname + "</strong> <i>" + self.allResults[i].username + "</i>");
          newTdTotalScore = $("<td>");
          newTdTotalScore.append(self.allResults[i].totalScore);
          newTdTotalTime = $("<td>");
          newTdTotalTime.append(self.calculateTime(self.allResults[i].totalTime));
  
          newTr.append(newTdCount).append(newTdNickname).append(newTdTotalScore).append(newTdTotalTime);
          scoreTable.append(newTr);
        }
      }
      
    }

    self.calculateTime = (totalTime) => {
      var restOfTotalTime = totalTime;

      var godziny = Math.floor(restOfTotalTime / (1000 * 60 * 60));
      restOfTotalTime %= (1000 * 60 * 60);

      var minuty = Math.floor(restOfTotalTime / (1000 * 60));
      restOfTotalTime %= (1000 * 60);

      var sekundy = Math.floor(restOfTotalTime / (1000));
      restOfTotalTime %= (1000);

      var milisekundy = Math.floor(restOfTotalTime);

      console.log(totalTime);
      console.log(godziny);
      console.log(minuty);
      console.log(sekundy);
      console.log(milisekundy);

      return "" +(godziny? godziny + "godz ":"") + 
      (minuty? minuty + "min ":"") +
      (sekundy? sekundy + "sec ":"") +
      (milisekundy? milisekundy + "ms ":"") + "";

    }

    self.checkTotalScoreChanges = (changeOccurred) => {
  
      /* changeOccurred:
  
        haveResultsChanged:true
  
        haveResultsChanged:false
  
        
        gameExists:false;
  
      */
      if ( changeOccurred.gameExists !== false ) {
        self.ajaxGameEndLoopTimeout = setTimeout(()=>{
          ajaxCheckTotalScoreChanges((changeOccurred) => {self.checkTotalScoreChanges(changeOccurred)},
          self.gameID)
        },
          5000);
      } 
      /*TODO 
        obsługa danych z data (jeśli zaszły zmiany to wysyłać zapytani o to co się zmieniło)
      */
      if (changeOccurred.haveResultsChanged) {
        ajaxGetTotalGameResults(
          (gameData)=>{self.setAllResults(gameData)},
          self.gameID
        );
      }
      //nie wiem czy to odpalać
      console.log(changeOccurred.gameExists === false)
      if (changeOccurred.gameExists === false) {
        //ostatnie poproszenie o total results
        console.log("wykonano")
        ajaxGetTotalGameResults(
          (gameData)=>{self.setAllResults(gameData)},
          self.gameID
        );
      
      }
    }
    /*       event listeners          */
    if ($("#btnLeave").length)
        $("#btnLeave").on("click",()=>{
        if (self.debug)
            console.log("btnLeave")
            window.location.replace("/lobby");
        });

    if ($("#btnPersonalResults").length)
        $("#btnPersonalResults").on("click",()=>{
        if (self.debug)
            console.log("btnPersonalResults")

        });
    
    /*     ajax http actions       */
    
    
    
    /*   ajax http requests       */
    
    var ajaxCheckTotalScoreChanges = ( callback, gameID ) => {
      /*
      api/v1/{gameID}/scores/total/changes GET
      {}
      {true/false}
      */
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/total/changes",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxCheckTotalScoreChanges success");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
          }
          callback(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxCheckTotalScoreChanges error");
            console.warn(jqXHR);
            console.warn(status);
            console.warn(err);
  
          }
        }
      });
    }
  
    var ajaxGetPersonalGameResults = (callback, gameID) => {
      /*
      api/v1/{gameID}/scores/total GET
      {}
      [ {"username": String, "nickname": String, "totalScore": int, "hasFinished": true}, ...] 
      */
      self.showModal();
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/personal",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetPersonalGameResults success");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
          }
          callback(data);
          self.hideModal();
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetPersonalGameResults error");
            console.warn(jqXHR);
            console.warn(status);
            console.warn(err);
  
          }
          self.hideModal();
        }
      });
    }
  
    var ajaxGetTotalGameResults = (callback, gameID) => {
      /*
      api/v1/{gameID}/scores/personal GET
      {}
      [ {"competion": double, "timeTaken": long, "difficulty": double }, ...]
      */
      self.showModal();
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/total",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetTotalGameResults success");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            
          }
          callback(data);
          self.hideModal();
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetTotalGameResults error");
            console.warn(jqXHR);
            console.warn(status);
            console.warn(err);
  
          }
          self.hideModal();
        }
      });
    }
  
    /*  initalization  */
    self.gameResultsInit();
     
    return self;
}
GameResultsLogic.getInstance = (debug = false) => {
  
    if (GameResultsLogic.singleton)
        return GameResultsLogic.singleton;
    var gameID = window.location.href.substring(this.location.href.lastIndexOf('/') + 1);

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
            GameResultsLogic.singleton = GameResultsLogic(playerInfo, gameID, debug);
            console.log("GameResultsLogic");
        },
        error: function(jqXHR, status, err) {
            if (debug){
            console.warn("ajaxReceiveWhoAmI error");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            }
            
            hideModal();
        }
        });
    }
    
    ajaxReceiveWhoAmI();
    return GameResultsLogic.singleton;
}
  //var debug = GameLogic.create(self.debug = true);
  
  