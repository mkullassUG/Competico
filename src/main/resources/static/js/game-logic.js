/*TODO
  1.żeby odpowiedzi zamieniały się miejscami / albo nie pozwolić istniec dwom w tym samym miejscu

  2. porpawić wygląd

  3. po zmianei szerokości okna żeby wracały na swoją pozycję
  obecnie mam problem z cleartimeoutami i jeden element lata po ekranie...

*/

/*TODO
  WAŻNE 2021-03-20:

  detekcja czy przycisk jest niżej od gaedivz żeby nei zasłaniać:
  
console.log(
	"is botton top ("
		+ $("#btnNextTask").offset().top + 
	"px) lower than gamediv bottom ("+($("#GameDiv").offset().top + $("#GameDiv").height())+"px + 60 margin): " + (
	($("#btnNextTask").offset().top) >
	($("#GameDiv").offset().top + $("#GameDiv").height() + 60)
	)
)
*/
const GameModule = (function($, window) {

  const GameLogic = ( lobby, _task, successfulCreationCallback) => {

    
    /*       singleton          */
    if (GameLogic.singleton)
        return GameLogic.singleton;
    var self = lobby;
    // if (!GameLogic.singleton)
    GameLogic.singleton = self;
    
    
    /*       logic variables          */
    self.currentTask = _task;
    self.currentTaskVariant;
    self.gameID; //dostaje z LobbyLogic whoami lub checkUpdateLobby
    self.myScore;
    self.allResults;
    self.allPreviousResults;
    self.GameFinished = false ;
    self.ajaxGamePingLoopTimeout;
    self.gameExist = true;
    self.cy = window.cytoscape({
      container: $("#cy"),
    
      style: [ // the stylesheet for the graph
        {
          selector: 'core',
          style: {
            'active-bg-size': 0
          },
          css: {
            events: 'no'
          }
        },
      
      {
        selector: 'node',
        style: {
        'background-color': '#666',
        // 'label': 'data(id)',
        "text-valign" : "center",
        "text-halign" : "center",
        'border-color': 'black',
        "border-opacity": "1",
        "border-width": "3px"
        },
        css: {
          events: 'no'
        }
      },
    
      {
        selector: 'edge',
        style: {
        'width': 3,
        'line-color': 'black',
        'target-arrow-color': 'black',
        'target-arrow-shape': 'triangle',
        'curve-style': 'bezier'
        },
        css: {
          events: 'no'
        }
      }
      ],
    
      layout: {
      name: 'grid',
      rows: 1
      },
      wheelSensitivity: 0.0
    });
    self.cy.boxSelectionEnabled(false);
    self.cy.panningEnabled(false);
    self.KropeczkiObserver;
  
    self.resizeGameObserver;
  
    self.GameCore = TaskGameCore();
  
    /*       logic functions          */
    self.gameInit = (task) => {
  
      if (task.hasGameFinished ) {
        console.log("hasGameFinished");
        console.log(task);
        self.setupEndGame(task);
        return;
      }
  
      //pytaj o taska
      ajaxReceiveGameChange();
      
      //losowo podczas startu gry występuje bug z modalem blokującym interfejs użytkownika, tutaj prowizorycznie się go pozbywam
      //zauważyłem że dzieje się to tylko gdy mam przeglądarke w pomniejszonym oknie, jak mam fullscreen to jest ok
      //ale też nie zawsze, rozszerzyłem o troche ekram, cofnąłem do tyłu i znow bug
      
      //new 2021-04-11 zakomentowane bo bug sie raz pojawił
      //ale może lepiej z body jeszcze dodatkowo usuwać tą klase dziwnąco overflow-hidden dodaje??
      // if ($(".modal-backdrop")[0]) {
      //   //$(".modal-backdrop").remove();
      //   $('#LobbyDeletedModalCenter').modal('hide');
      //   console.warn("Usunąłem natrętnego modala!");
      // } else {
      //   console.warn("nie wykryłem modala");
      //   console.warn($(".modal-backdrop"));
      // }
  
      if ($(".modal-backdrop")[0]) {
        //zostawia to klase .modal-open w body
  
        //$(".modal-backdrop").remove()
        //$("body").removeClass("modal-open");
        //console.warn("Usunąłem natrętnego modala!");
      }
  
      //jeśli chcemy wywysłać info np o tym żew gracz nie ma focusa na grze...
      //ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
      self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(self.lobbyCode)},10000);

      if ( successfulCreationCallback )
        successfulCreationCallback(self);
    }
    
    self.gameSetupAfterChange = (game) => {
      /*game:
      {
        game: zadania - zadania, dopuki mam jeszcze zadania
  
        albo
  
        hasGameFinished: true - true jeśli skończyłem gre, nia ma zadań
      }*/
      //setup task
      //$(window).off("resize");
      if (game.hasGameFinished ) {
        console.log("hasGameFinished");
        console.log(game.hasGameFinished);
        self.setupEndGame(game);
      }  else {
        self.setupNewTask(game);
      }
    }
  
    self.finishTask = () => {
      //wyświewtlić modala czy napewn ochce oddac zadanie
      if (!self.currentTaskVariant) {
        console.warn("nie ma gotowych odpowiedzi!");
        /*
          Dla testowych danych zakomentowałem bo nie pozwalało przejśc do nastepnego zadania gdy jak opierwszy nie był działający już task
        */
        //return;
      }
      var answers = self.currentTaskVariant.getAnswers();
  
      ajaxSendAnswerAndReceiveNext(answers);
    }
    
    self.setupNewTask = (task) => {
  
      //czy otrzymany task jest pusty
      if (task == null) {
        if (self.debug)
          console.warn("task was empty");
        return;
      }
      
      self.currentTask = task;//bug 2021-02-26, zmiana z - na =
      
      if (!task.taskName)
        console.warn("Could not read task name!");
  
      //czyszczenie porpzedniego taska jeśli jakiś był
      //przygotowanie miejsca na następnego taska
      if ($("#GameDiv").length)
        $("#GameDiv").html("");
  
      //mok task1
      if (self.debug) 
        console.log(task);
      
      self.currentTaskVariant = self.GameCore.getVariant(task.taskName, task.task, task.instruction);
      self.setupResizeGameObserver();
  
      //ustawianie kropeczek
      self.buildCy(task.taskCount,task.currentTaskNumber);
  
      if ( self.KropeczkiObserver )
        self.KropeczkiObserver.unobserve(window.document.querySelector("#gameInstruction"));
      
      self.KropeczkiObserver = new window.ResizeObserver(function(entries) {
  
        self.buildCy(task.taskCount,task.currentTaskNumber);
      });
      
      self.KropeczkiObserver.observe(window.document.querySelector("#gameInstruction"));
  
      self.taskComeAnimation();
    }
  
    //podzieliłem to na gameObserver i variantObserver
    self.setupResizeGameObserver = () => {
        //informuje czy gamediv jest za duży
        if (self.resizeGameObserver)
            self.resizeGameObserver.unobserve(window.document.querySelector("#GameDiv"));
        self.resizeGameObserver = new window.ResizeObserver(function(entries) {
            //new 2021-03-20 TODO
            if (!(($("#btnNextTask").offset().top) > ($("#GameDiv").offset().top + $("#GameDiv").height() + 60))) 
              console.warn("GameDiv wychodzi poza ekran lub przycisk zasłania!");
  
            self.resizeWindow();
        });
  
        self.resizeGameObserver.observe(window.document.querySelector("#GameDiv"));
    }
  
    self.buildCy = (totalTasks, current) => {
          
      self.cy.elements().remove();
  
      var cyElementWidth = window.document.getElementById("cy").offsetWidth;
      var cyElementHeight = window.document.getElementById("cy").offsetHeight;
      var nodeSpace = cyElementWidth/totalTasks;
  
      var eles_group = [];
  
      //połączenia
      var offset = {
          x: nodeSpace/2,
          y: cyElementHeight/2
      };
  
      for (let i = 0; i < totalTasks; i++) {
    
        var parentKey = "node" + (i-1);
        var currentKey = "node" + i;
        
        var xMove = i * nodeSpace;
  
        //dodaj edge parent-child
        if ( i != 0){
          eles_group.push({ group: 'edges', data: {id: "E" + i, target: currentKey, source: parentKey} });
        }
        eles_group.push({group: 'nodes', data: { id: currentKey}, position: { x: offset.x + xMove, y: offset.y }});
        //dodaj noda
        
  
        /*
        .style({
          'background-color': 'white',
          'border-color': 'blue'
        });
        */
      }
        
      var eles = self.cy.add(eles_group);
  
      var nodes = self.cy.nodes();
      nodes.ungrabify();
      for ( let i = 0; i < nodes.length; i++) {
  
        if ( i < current)
          nodes[i].style( { 'background-color' : 'green', 'border-color': '#004400' });
        else if ( i == current)
          nodes[i].style( { 'background-color' : 'white' });
      }
  
    }
  
    self.KropeczkiObserver;
  
    self.setupEndGame = (game) => {
      window.location.replace("/game/results/" + self.gameID + "#afterGame");
    }
  
    self.taskComeAnimation = () => {
      
        //animate receive
        //wyłączam bo wiele bugów

        
        // $('#GameWrapperDiv').css({
        //   left: "-2000px",
        // });
        // $('#GameWrapperDiv').animate({
        //   zoom: 1,
        //   opacity: 1,
        //   left: "+=2000"
        // }, 1000, function() {
        //   // Animation complete.
        //   $('#GameWrapperDiv').css({
        //     left: "inherit",
        //     zoom: "inherit",
        //     opacity: "inherit",
        //   });
        // });
    }

    self.taskDoneAnimation = () => {
  
        //animate send away
        //wyłączam bo wiele bugów
        
        // $('#GameWrapperDiv').animate({
        //   zoom: 0.5,
        //   opacity: 0.25,
        //   left: "+=3000"
        // }, 1000, function() {
        //   // Animation complete.
        //   $('#GameWrapperDiv').css({
        //       left: "-2000px",
        //   });
        // });
    }
    self.resizeWindow = () => {
      if ( self.currentTaskVariant && !self.currentTaskVariant.isTaskDone) {
          //console.log("szerokośc okna pod gre");
          $("html").height("100%");
          function isInt(n) {
              return n % 1 === 0;
          }
          
          var h1 = $(window.document).height();
          if ( isInt (h1))
              h1 -= 1;
          $("html").height(h1);
      } else {
          //console.log("szerokośc okna 100%");
          $("html").height("100%");
      }
    }
  
    /*       event listeners          */
    if ($("#btnSendAnswer").length)
      $("#btnSendAnswer").on("click",(e) => {
        if (self.debug)
          console.log("btnNextTask")
  
          self.finishTask();
      })
      
    window.onresize = self.resizeWindow;
  
    /*     ajax http actions       */
    var ajaxReceiveGameChange = ( ) => {
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/game/"+self.lobbyCode+"/tasks/current",
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (self.debug)
            console.log("ajaxReceiveGameChange success");
          self.gameSetupAfterChange(data);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxReceiveGameChange error");
          }
        }
      });
    }
    
    var ajaxSendAnswerAndReceiveNext = ( answer ) => {
      self.showModal();
      var send = answer;
      
      $.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/game/"+self.lobbyCode+"/tasks/answer",
        data     : JSON.stringify(send),
        contentType: "application/json",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxSendAnswerAndReceiveNext success");
            // console.log(data);
            // console.log(textStatus);
            // console.log(jqXHR);
          }
          
          if ( self.currentTaskVariant )
            self.currentTaskVariant.isTaskDone = true;
  
          self.taskDoneAnimation();
  
          self.hideModal();
          ajaxReceiveGameChange();
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxSendAnswerAndReceiveNext error");
            console.warn(jqXHR);
            console.warn(status);
            console.warn(err);
          }
          
          self.hideModal();
        }
      });
    }
    
    /*   ajax http requests       */
    var ajaxGamePing = (lobbyCode) => {

      $.ajax({
        type     : "POST",
        cache    : false,
        url      : "/api/v1/game/"+lobbyCode+"/ping",
        success: function(data, textStatus, jqXHR) {
          if (self.debug) {
            console.log("ajaxGamePing success");
            // console.log(data);
            // console.log(textStatus);
            // console.log(jqXHR);
          }
          self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(lobbyCode)},10000);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGamePing error");
            console.warn(jqXHR);
            console.warn(status);
            console.warn(err);
  
          }
        }
      });
    }
  
    /*  initalization  */
    self.gameInit(self.currentTask);
     
    return self;
  }
  GameLogic.getInstance = (lobby, _task, successfulCreationCallback) => {
  
    if (GameLogic.singleton)
      return GameLogic.singleton;
  
    var ajaxGetNextTask = ( ) => {
      lobby.showModal();
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/game/" + lobby.lobbyCode + "/tasks/current",
        contentType: "application/json",
        success: function(task, textStatus, jqXHR) {
          if (lobby.debug) {
            console.log("ajaxGetNextTask success");
          }
          lobby.hideModal();
  
          GameLogic.singleton = GameLogic(lobby, task, successfulCreationCallback);
  
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGetNextTask error");
          }
          lobby.hideModal();
        }
      });
    }
    
    // if (self.debug) 
    //   return GameLogic(null, debug = true);
  
    ajaxGetNextTask();
    return GameLogic.singleton;
  }
  
  return {
    GameLogic:GameLogic,
    getInstance: GameLogic.getInstance
  }
})

//var debug = GameLogic.create(self.debug = true);

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {GameModule};