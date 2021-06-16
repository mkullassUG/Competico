
const GameResultsLogic = ( playerInfo_, gameID_, debug_) => {

    /*       logic variables          */
    var self = playerInfo_;
    self.gameID = gameID_;
    self.ajaxGameEndLoopTimeout;
    self.myScore;
    self.allResults;
    self.allPreviousResults;
    self.gameFinished = false;
    self.debug = debug_;
    self.myPlace;
    self.previousScores = null;
    /*       logic functions          */
    self.gameResultsInit = (task_) => {

      //losowo podczas startu gry występuje bug z modalem blokującym interfejs użytkownika, tutaj prowizorycznie się go pozbywam
      //zauważyłem że dzieje się to tylko gdy mam przeglądarke w pomniejszonym oknie, jak mam fullscreen to jest ok
      //ale też nie zawsze, rozszerzyłem o troche ekram, cofnąłem do tyłu i znow bug
      if ($(".modal-backdrop")[0]) {
        $(".modal-backdrop").remove()
        if ( self.debug )
          console.warn("Usunąłem natrętnego modala!");
      }
      
      self.isAfterGame = window.location.hash.includes("afterGame");
      //jeśli chcemy wywysłać info np o tym żew gracz nie ma focusa na grze...
      //ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
      //self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(self.lobbyCode)},10000);

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
        for (let i = 0; i < myScore.length; i++) {
            var currentTask = myScore[i];

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
     
      var scoreTable = $("#scoreTable");
      scoreTable.html("");
      var newTr,newTdCount,newTdNickname,newTdTotalScore,newTdTotalTime, myCurrentPlace = false;
      if (self.allPreviousResults) { //update
  
        for (let i = 0; i < self.allResults.length; i++) {
          var currentResult = self.allResults[i];
          //to nie będzie działać bo będę dostawać ich w innej kolejności
          //więc musiałbym pętlą przelatywać po username i nickach
          
          if (self.username == currentResult.username && currentResult.removedForInactivity) {
            newTr = $('<tr class="lobbyMeKicked" title="Wykluczony za nieaktywność">');
            myCurrentPlace = i+1;
          } else if (self.username == currentResult.username) {
            newTr = $('<tr class="lobbyMe">');
            myCurrentPlace = i+1;
          } 
          else if (currentResult.removedForInactivity)
            newTr = $('<tr class="gameKicked" title="Wykluczony za nieaktywność">');
          else if (currentResult.hasFinished === undefined)
            newTr = $('<tr>');
          else if (!currentResult.hasFinished)
            newTr = $('<tr class="gameOtherNotFinished" title="Nadal w grze">');
          else
            newTr = $('<tr>');
  
          newTdCount = $("<td>");
          newTdCount.append(i+1);
          newTdNickname = $("<td>");
          newTdNickname.append("<strong>" +currentResult.nickname + "</strong> <i>" + currentResult.username + "</i>");
          newTdTotalScore = $("<td>");
          newTdTotalScore.append(currentResult.totalScore);
          newTdTotalTime = $("<td>");
          newTdTotalTime.append(self.calculateTime(currentResult.totalTime));
  
          newTr.append(newTdCount).append(newTdNickname).append(newTdTotalScore).append(newTdTotalTime);
          scoreTable.append(newTr);
          
          if ( self.previousScores ) {
            var prevScore = self.previousScores.filter(scor => {
              
                return scor.username === currentResult.username;
            })
            
            if ( prevScore.length > 0)
              if ( prevScore[0].totalScore !== currentResult.totalScore || 
                prevScore[0].totalTime !== currentResult.totalTime)
                updatedResultFor(newTr);
          }
        }
      } else { //init tr'ów
        
        for (let i = 0; i < self.allResults.length; i++) {
          var currentResult = self.allResults[i];
          
          if (self.username == currentResult.username && currentResult.removedForInactivity) {
            newTr = $('<tr class="lobbyMeKicked" title="Wykluczony za nieaktywność">');
            myCurrentPlace = i+1;
          } else if (self.username == currentResult.username) {
            newTr = $('<tr class="lobbyMe">');
            myCurrentPlace = i+1;
          } 
          else if (currentResult.removedForInactivity)
            newTr = $('<tr class="gameKicked" title="Wykluczony za nieaktywność">');
          else if (currentResult.hasFinished === undefined)
            newTr = $('<tr>');
          else if (!currentResult.hasFinished)
            newTr = $('<tr class="gameOtherNotFinished" title="Nadal w grze">');
          else
            newTr = $('<tr>');
  
          newTdCount = $("<td>");
          newTdCount.append(i+1);
          newTdNickname = $("<td>");
          newTdNickname.append("<strong>" +currentResult.nickname + "</strong> <i>" + currentResult.username + "</i>");
          newTdTotalScore = $("<td>");
          newTdTotalScore.append(currentResult.totalScore);
          newTdTotalTime = $("<td>");
          newTdTotalTime.append(self.calculateTime(currentResult.totalTime));
  
          newTr.append(newTdCount).append(newTdNickname).append(newTdTotalScore).append(newTdTotalTime);
          scoreTable.append(newTr);

          if ( self.previousScores ) {
              var prevScore = self.previousScores.filter(scor => {
                
                return scor.username === currentResult.username;
              })
              
              if ( prevScore.length > 0)
                if ( prevScore[0].totalScore !== currentResult.totalScore || 
                  prevScore[0].totalTime !== currentResult.totalTime)
                  updatedResultFor(newTr);
          }
        }
      }
      // tooltipsUpdate();
      self.previousScores = self.allResults;

      if ( self.allResults) {
        var stillPlaying = self.allResults.filter(player => {

          if (player.hasFinished === false && !player.removedForInactivity)
            return true;
          else
            return false;
        });
        if ( self.gameFinished !== true && stillPlaying.length < 1) {
          self.gameFinished = true;
          self.myPlace = myCurrentPlace;
  
          if ( self.myPlace && self.isAfterGame)
            self.popoutAndConfetti();
        }
      }
    }

    var updatedResultFor = (elem) => {
      var orgClasses = elem[0].className.split(" ");
      
      elem.addClass("playerResultUpdate",500).removeClass(orgClasses,500);
      setTimeout(function(){elem
        .addClass(orgClasses,1000)
        .removeClass("playerResultUpdate",1000);},600)
    }
    // var tooltipsUpdate = () => {
    
    //   if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
    //       $('[data-toggle="tooltip"]').tooltip({
    //           show: {
    //             delay: 5000,
    //             duration: 0
    //           }
    //       });
    // }

    self.popoutAndConfetti = ( ) => {
      
        $('#areYouLeavingModal').modal('hide');
        $("#myPlace").text("Zająłeś/łaś " + self.myPlace + " miejsce!"); 
        $('#gameFinishedModal').modal('show');
        const startit = () => {
          setTimeout(function () {
            $.confetti["start"]()
          }, 1);
        };
  
        const stopit = () => {
          setTimeout(function () {
            $.confetti["stop"]()
          }, 2000);
        };
  
        startit();
        stopit();
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
      if (changeOccurred.gameExists === false) {
        //ostatnie poproszenie o total results
        
        ajaxGetTotalGameResults(
          (gameData)=>{self.setAllResults(gameData)},
          self.gameID
        );
      
      }
    }
    /*       event listeners          */
    if ($("#btnLeave").length)
      $("#btnLeave").on("click",()=>{

        //TODO: is game finished?

        if (self.debug)
            console.log("btnLeave");
        if ( self.gameFinished )
          window.location.replace("/dashboard");
      });

    if ( $("#IDoWantToLeaveBtn").length )
      $("#IDoWantToLeaveBtn").on("click",()=>{
        if (self.debug)
            console.log("btnLeave");
        window.location.replace("/dashboard");
      });

    if ($("#btnPersonalResults").length)
        $("#btnPersonalResults").on("click",()=>{
          if (self.debug)
              console.log("btnPersonalResults");
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

    if ( gameID.includes("afterGame"))
      gameID = gameID.split("#")[0];
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
  
  