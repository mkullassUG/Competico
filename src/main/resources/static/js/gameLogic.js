/*TODO
  1.żeby odpowiedzi zamieniały się miejscami / albo nie pozwolić istniec dwom w tym samym miejscu

  2. porpawić wygląd

  3. po zmianei szerokości okna żeby wracały na swoją pozycję
  obecnie mam problem z cleartimeoutami i jeden element lata po ekranie...

*/

const GameLogic = ( lobby, _task) => {

  /*       logic variables          */
  var self = lobby;
  self.currentTask = _task;
  self.currentTaskVariant;
  self.gameID; //dostaje z LobbyLogic whoami lub checkUpdateLobby
  self.myScore;
  self.allResults;
  self.allPreviousResults;
  self.GameFinished = false ;
  self.ajaxGamePingLoopTimeout;
  self.gameExist = true;
  self.cy = cytoscape({
    container: document.getElementById("cy"),
  
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
    if ($(".modal-backdrop")[0]) {
      $(".modal-backdrop").remove()
      console.warn("Usunąłem natrętnego modala!");
    }

    //jeśli chcemy wywysłać info np o tym żew gracz nie ma focusa na grze...
    //ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
    self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(self.lobbyCode)},10000);
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
    console.log(self.currentTaskVariant.getAnswers());
    var answers = self.currentTaskVariant.getAnswers();

    ajaxSendAnswerAndReceiveNext(answers);
  }
  /*
  {

  }
  */
  self.setupNewTask = (task) => {

    //czy otrzymany task jest pusty
    if (task == null) {
      if (self.debug)
        console.warn("task was empty");
      return;
    }
    
    self.currentTask - task;
    
    if (!task.taskName)
      console.warn("Could not read task name!");

    //czyszczenie porpzedniego taska jeśli jakiś był
    //przygotowanie miejsca na następnego taska
    if ($("#GameDiv").length)
      $("#GameDiv").html("");

    //mok task1
    if (self.debug) 
      console.log(task);
    
    //wybieranie odpowiedniej logiki dla konkretnego template'a
    switch (task.taskName) {
      case "WordFill":
        self.currentTaskVariant = TaskVariant0(task.task);
        break;
      case "WordConnect":
        self.currentTaskVariant = TaskVariant1(task.task);
        break;
      case "ChronologicalOrder":
        self.currentTaskVariant = TaskVariant2(task.task);
        break;
      case "template3":
        self.currentTaskVariant = TaskVariant3(task.task);//GameLogicVariants.logicVariant3(task);
        break;
      case "template4":
        self.currentTaskVariant = TaskVariant4(task.task);//GameLogicVariants.logicVariant4(task);
        break;
      case "template5":
        self.currentTaskVariant = TaskVariant5(task.task);//GameLogicVariants.logicVariant5(task);
        break;
      default:
          console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
          self.currentTaskVariant = {};
          //ListWordFill answers
          self.currentTaskVariant.getAnswers = () => { 
            console.log("hello ListWordFill");
            return {answers: [["test"]]} 
          }
        break;
    }

    //ustawianie kropeczek
    self.buildCy(task.taskCount,task.currentTaskNumber);

    $("#gameTimer").html(task.instruction);
    if ( self.KropeczkiObserver )
      self.KropeczkiObserver.unobserve(document.querySelector("#gameTimer"));
    
    self.KropeczkiObserver = new ResizeObserver(function(entries) {

      self.buildCy(task.taskCount,task.currentTaskNumber);
    });
    
    self.KropeczkiObserver.observe(document.querySelector("#gameTimer"));

  }

  self.buildCy = (totalTasks, current) => {
        
    self.cy.elements().remove();

    var cyElementWidth = document.getElementById("cy").offsetWidth;
    var cyElementHeight = document.getElementById("cy").offsetHeight;
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


    window.location.replace("/game/results/" + self.gameID);

  }
  /*       event listeners          */
  if ($("#btnSendAnswer").length)
    $("#btnSendAnswer").on("click",(e) => {
      if (self.debug)
        console.log("btnNextTask")

        self.finishTask();
    })
  
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
    console.log(send);
    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "/api/v1/game/"+self.lobbyCode+"/tasks/answer",
      data     : JSON.stringify(send),
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug) {
          console.log("ajaxSendAnswerAndReceiveNext success");
          console.log(data);
          console.log(textStatus);
          console.log(jqXHR);
          
        }
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
          console.log(data);
          console.log(textStatus);
          console.log(jqXHR);
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
GameLogic.getInstance = (lobby) => {

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

        GameLogic.singleton = GameLogic(lobby, task);

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
//var debug = GameLogic.create(self.debug = true);

