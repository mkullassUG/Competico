const GameModule = (function($, window) {

  const GameLogic = ( lobby, _task, successfulCreationCallback) => {

    
    /*       singleton          */
    if (GameLogic.singleton)
        return GameLogic.singleton;
    var self = lobby;
    
    GameLogic.singleton = self;
    
    
    /*       logic variables          */
    self.currentTask = _task;
    self.currentTaskVariant;
    self.gameID; 
    self.myScore;
    self.allResults;
    self.allPreviousResults;
    self.GameFinished = false ;
    self.ajaxGamePingLoopTimeout;
    self.gameExist = true;
    self.cy = window.cytoscape({
      container: $("#cy"),
    
      style: [
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
        self.setupEndGame(task);
        return;
      }
  
      ajaxReceiveGameChange();
      
      self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(self.lobbyCode)},10000);

      if ( successfulCreationCallback )
        successfulCreationCallback(self);
    }
    
    self.gameSetupAfterChange = (game) => {

      if ( game === undefined) {

        if ( self.debug ){
          console.warn("To nie powinno się wydarzyć!");
          console.warn(game);
        }
        return;
      }
      
      if (game.hasGameFinished ) {
        
        self.setupEndGame(game);
      }  else {
        self.setupNewTask(game);
      }
    }
  
    self.finishTask = () => {

      if (!self.currentTaskVariant) {
        console.warn("nie ma gotowych odpowiedzi!");
      }

      var answers = self.currentTaskVariant.getAnswers();
      ajaxSendAnswerAndReceiveNext(answers);
    }
    
    self.setupNewTask = (task) => {
  
      if (task == null) {
        if (self.debug)
          console.warn("task was empty");
        return;
      }
      
      self.currentTask = task;
  
      if ($("#GameDiv").length)
        $("#GameDiv").html("");
  
      self.currentTaskVariant = self.GameCore.getVariant(task.taskName, task.task, task.instruction);
      self.setupResizeGameObserver();
  
      self.buildCy(task.taskCount,task.currentTaskNumber);
  
      if ( self.KropeczkiObserver )
        self.KropeczkiObserver.unobserve(window.document.querySelector("#gameInstruction"));
      
      self.KropeczkiObserver = new window.ResizeObserver(function(entries) {
  
        self.buildCy(task.taskCount,task.currentTaskNumber);
      });
      
      self.KropeczkiObserver.observe(window.document.querySelector("#gameInstruction"));
  
      self.taskComeAnimation();
    }
  
    self.setupResizeGameObserver = () => {

        if (self.resizeGameObserver)
            self.resizeGameObserver.unobserve(window.document.querySelector("#GameDiv"));
        self.resizeGameObserver = new window.ResizeObserver(function(entries) {
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
  
      var offset = {
          x: nodeSpace/2,
          y: cyElementHeight/2
      };
  
      for (let i = 0; i < totalTasks; i++) {
    
        var parentKey = "node" + (i-1);
        var currentKey = "node" + i;
        
        var xMove = i * nodeSpace;
  
        if ( i != 0){
          eles_group.push({ group: 'edges', data: {id: "E" + i, target: currentKey, source: parentKey} });
        }
        eles_group.push({group: 'nodes', data: { id: currentKey}, position: { x: offset.x + xMove, y: offset.y }});
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
        
          $("html").height("100%");
          function isInt(n) {
              return n % 1 === 0;
          }
          
          var h1 = $(window.document).height();
          if ( isInt (h1))
              h1 -= 1;
          $("html").height(h1);
      } else {
          $("html").height("100%");
      }
    }
  
    /*       event listeners          */
    if ($("#btnSendAnswer").length)
      $("#btnSendAnswer").on("click",(e) => {
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
            console.warn(data);
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
          }
          self.ajaxGamePingLoopTimeout = setTimeout(()=>{ajaxGamePing(lobbyCode)},10000);
        },
        error: function(jqXHR, status, err) {
          if (self.debug) {
            console.warn("ajaxGamePing error");
            console.warn(jqXHR);
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
  
    ajaxGetNextTask();
    return GameLogic.singleton;
  }
  
  return {
    GameLogic:GameLogic,
    getInstance: GameLogic.getInstance
  }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {GameModule};