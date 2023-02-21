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
    self.isLecturer = false;
    /*       logic functions          */
    self.gameResultsInit = (task_) => {

      if ($(".modal-backdrop")[0]) {
        $(".modal-backdrop").remove()
      }
      
      self.isAfterGame = window.location.hash.includes("afterGame");

      if ( self.roles.includes("LECTURER")) {
        self.isLecturer = true;
        setupForLecturer();
      } else {
        setupForPlayer();
      }
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
      if (self.allPreviousResults) {
  
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

          if (self.isLecturer) {
            var checkResTd = $(`<td>`);
            var btnCheck = $(`<button class="btn btn-sm btn-info" data-toggle="modal" data-target="#personalResultsModal">Wyniki</button>`).on('click',(e)=>{
              lecturerCheckResBtn(e);
            });
            checkResTd.append(btnCheck);
            newTr.append(checkResTd);
            newTr[0].dataset['username'] = currentResult.username;
          }

          scoreTable.append(newTr);
          
          if ( self.previousScores ) {
            var prevScore = self.previousScores.filter(scor => {
              
                return scor.username === currentResult.username;
            })
            
            if ( prevScore.length > 0)
              if ( prevScore[0].totalScore !== currentResult.totalScore || 
                prevScore[0].totalTime !== currentResult.totalTime)
                updatedResultFor(newTr, currentResult.username);
          }
        }
      } else { 
        
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
          newTdNickname.append("<strong>" + currentResult.nickname + "</strong> <i>" + currentResult.username + "</i>");
          newTdTotalScore = $("<td>");
          newTdTotalScore.append(currentResult.totalScore);
          newTdTotalTime = $("<td>");
          newTdTotalTime.append(self.calculateTime(currentResult.totalTime));
  
          newTr.append(newTdCount).append(newTdNickname).append(newTdTotalScore).append(newTdTotalTime);
          scoreTable.append(newTr);

          if (self.isLecturer) {
            var checkResTd = $(`<td>`);
            var btnCheck = $(`<button class="btn btn-sm btn-info" data-toggle="modal" data-target="#personalResultsModal">Wyniki</button>`).on('click',(e)=>{
              lecturerCheckResBtn(e);
            });
            checkResTd.append(btnCheck);
            newTr.append(checkResTd);
            newTr[0].dataset['username'] = currentResult.username;
          }

          if ( self.previousScores ) {
              var prevScore = self.previousScores.filter(scor => {
                
                return scor.username === currentResult.username;
              })
              
              if ( prevScore.length > 0)
                if ( prevScore[0].totalScore !== currentResult.totalScore || 
                  prevScore[0].totalTime !== currentResult.totalTime)
                  updatedResultFor(newTr, currentResult.username);
          }
        }
      }
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

    var setupForLecturer = () => {
      $("#lecturersView").removeClass("collapse");
      $("#btnPersonalResults").remove();
      $("#mainLeaderboardHeader").text("Widok lektora");
    }

    var setupForPlayer = () => {
      $("#lecturersView").remove();
    }

    var lecturerCheckResBtn = (e) => {
      
      var target = $(e.target);
      if ( !target.is('tr'))
        target = target.closest("tr");

      var username = $(e.target).closest("tr")[0].dataset['username'];
      self.focusedUserResults = username;

      self.showModal();
      ajaxGetUserResults(username, self.gameID, setupLecturersViewOnPersonalResults)
    }

    var setupLecturersViewOnPersonalResults = (data) => {
      
      self.hideModal();

      var personalResultsModalLongTitle = $("#personalResultsModalLongTitle");
      personalResultsModalLongTitle.text("Wyniki gracza: " + self.focusedUserResults);
      self.setPesonalResult(data);
    }

    var updatedResultFor = (elem, username) => {
      
      if ( self.isLecturer && self.focusedUserResults === username) {
        self.showModal();
        ajaxGetUserResults(username, self.gameID, setupLecturersViewOnPersonalResults);
      }

      var orgClasses = elem[0].className.split(" ");
      
      elem.addClass("playerResultUpdate",500).removeClass(orgClasses,500);
      setTimeout(function(){elem
        .addClass(orgClasses,1000)
        .removeClass("playerResultUpdate",1000);},600)
    }

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
  
      if ( changeOccurred.gameExists !== false ) {
        self.ajaxGameEndLoopTimeout = setTimeout(()=>{
          ajaxCheckTotalScoreChanges((changeOccurred) => {self.checkTotalScoreChanges(changeOccurred)},
          self.gameID)
        },
          5000);
      } 
      if (changeOccurred.haveResultsChanged) {
        ajaxGetTotalGameResults(
          (gameData)=>{self.setAllResults(gameData)},
          self.gameID
        );
      }
      if (changeOccurred.gameExists === false) {
        
        ajaxGetTotalGameResults(
          (gameData)=>{self.setAllResults(gameData)},
          self.gameID
        );
      
      }
    }
    /*       event listeners          */
    if ($("#btnLeave").length)
      $("#btnLeave").on("click",()=>{
        if ( self.gameFinished )
          window.location.replace("/dashboard");
      });

    if ( $("#IDoWantToLeaveBtn").length )
      $("#IDoWantToLeaveBtn").on("click",()=>{
        
        window.location.replace("/dashboard");
      });
    
    /*     ajax http actions       */
    
    /*   ajax http requests       */
    
    var ajaxCheckTotalScoreChanges = ( callback, gameID ) => {

      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/total/changes",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxCheckTotalScoreChanges success");
            console.log(data);
          }
          if ( callback)
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxCheckTotalScoreChanges error");
            console.warn(jqXHR);
          }
        }
      });
    }
  
    var ajaxGetPersonalGameResults = (callback, gameID) => {
      
      self.showModal();
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/personal",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetPersonalGameResults success");
            console.log(data);
          }
          if ( callback )
            callback(data);
          self.hideModal();
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetPersonalGameResults error");
            console.warn(jqXHR);
          }
          self.hideModal();
        }
      });
    }
  
    var ajaxGetTotalGameResults = (callback, gameID) => {
      
      self.showModal();
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/total",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetTotalGameResults success");
            console.log(data);
          }
          callback(data);
          self.hideModal();
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetTotalGameResults error");
            console.warn(jqXHR);
          }
          self.hideModal();
        }
      });
    }
  
    var ajaxGetUserResults = (username, gameID, callback) => {

      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/scores/"+gameID+"/personal/" + username,
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGetUserResults success");
            console.log(data);
          }
          if ( callback )
            callback(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetUserResults error");
            console.warn(jqXHR);
          }
          if ( callback )
            callback(false);
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
        
        return $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(playerInfo, textStatus, jqXHR) {
            if (debug){
              console.log("ajaxReceiveWhoAmI success");
              console.log(playerInfo);
            }
        },
        error: function(jqXHR, status, err) {
            if (debug){
              console.warn("ajaxReceiveWhoAmI error");
              console.log(data);
            }
        }
      });
    }
    
    var getAccountInfo = (callback) => {
      return $.ajax({
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

    showModal();
    Promise.all([
      ajaxReceiveWhoAmI(),
      getAccountInfo()

    ]).then(values=>{

      var playerInfo = values[0];
      var accountInfo = values[1];
      playerInfo.showModal = showModal;
      playerInfo.hideModal = hideModal;
      playerInfo.roles = accountInfo.roles;

      GameResultsLogic.singleton = GameResultsLogic(playerInfo, gameID, debug);
      hideModal();
    }).catch(e=>{

      if ( debug )
        console.warn(e);
      hideModal();
    });

    return GameResultsLogic.singleton;
}
  
  