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

  /*       logic functions          */
  self.gameInit = (task) => {

    console.log("gameInit");
    if (task.hasGameFinished ) {
      console.log("hasGameFinished");
      console.log(task.hasGameFinished);
      console.log(task);
      self.setupEndGame(task);
      return;
    }

    //pytaj o taska
    ajaxReceiveGameChange();

    //jeśli chcemy wywysłać info np o tym żew gracz nie ma focusa na grze...
    //ajaxLoopTimeout = setTimeout(ajaxConnectionLoop,1000);
  }
  
  self.gameSetupAfterChange = (game) => {
    /*game:
    {
      -task: {
        id: //wybiera odpowiednie id przypisane to template'u
        [pozostałe wartości odpowiednie dla danego template'u]
      }
      -time: //czas rozpoczęcia gry, potrzebny tylko na starcie,
      -hasGameFinished: true //posiada tylko jeśli gra się zakończyła i oddało się ostatnie zadanie... jeszcze do uzgodnienia z Michałem jak to będzie ogarnięte
      -result: //wyniki graczy wyświetlane na poczekalni po zakonczeniu gry
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
    
    //task = taskMokTemplates[2];
    //wybieranie odpowiedniej logiki dla konkretnego template'a
    switch (task.taskName) {
      case "WordFill":
        self.currentTaskVariant = TaskVariant0(task.task);//GameLogicVariants.logicVariant0(task);
        break;
      case "WordConnect":
        self.currentTaskVariant = TaskVariant1(task.task);//GameLogicVariants.logicVariant1(task);
        break;
      case "ChronologicalOrder":
        self.currentTaskVariant = TaskVariant2(task.task);//GameLogicVariants.logicVariant2(task);
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
          self.currentTaskVariant.getAnswers = () => { console.log("hello ListWordFill");return {answers: [["test"]]} }
        break;
    }
  }

  self.setupEndGame = (game) => {
    console.log("setupEndGame");
    
    if ($("#bigChangeDiv").length) {
      $("#bigChangeDiv").addClass("text-center").addClass("hideBeforeLoad");
      $("#bigChangeDiv").html(`
      <div class="row shadow-sm mt-3" id="gameEndCenter">
        <div class="col-12">
            <div class="lobbyDashboard">
              <h2 class="text-white">Dziękuje za grę</h2> 
              <div id="endGameDashboard">

              </div>
            </div>
        </div>
      </div>
      <div class="row shadow-sm mt-3" id="gameEndBottom">
        <div class="col-12">
          <buton class="btn btn-lg btn-secondary mb-3" id="btnEndGame">Zakończ grę</buton>
        </div>
      </div>`);
    }

      if ($("#btnEndGame").length)
        $("#btnEndGame").on("click",()=>{
      if (self.debug)
        console.log("btnEndGame");
        
        self.leaveLobby();
    });

    if (game.result) {
      //jeszcze nie jestem pewien w jakiej postaci będa przesyłane wyniki obecnego i pozostałych graczy. Do uzgodnienia z Michałem
      $("#endGameDashboard").html("Twój wynik: " + game.result); 
    }

  }

  /*       event listeners          */
  if ($("#btnSendAnswer").length)
    $("#btnSendAnswer").on("click",(e) => {
      if (self.debug)
        console.log("btnNextTask")

        self.finishTask();
    })
  
  /*     ajax http actions       */
  
  var sendAjaxEndGame = () => {

    $.ajax({
      type     : "POST",
      cache    : false,
      url      : "/api/v1/game/"+self.lobbyCode+"/finish",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {

        if (self.debug == true)
          console.log("sendAjaxEndGame success");
      },
      error: function(jqXHR, status, err) {
        if (self.debug == true)
          console.log("sendAjaxEndGame error");
      }
    });
  }
  
  /*   ajax http requests       */
  var ajaxReceiveGameChange = ( ) => {
    self.showModal();
    $.ajax({
      type     : "GET",
      cache    : false,
      url      : "/api/v1/game/"+self.lobbyCode+"/tasks/current",
      contentType: "application/json",
      success: function(data, textStatus, jqXHR) {
        if (self.debug)
          console.log("ajaxReceiveGameChange success");

        self.hideModal();
        self.gameSetupAfterChange(data);
      },
      error: function(jqXHR, status, err) {
        if (self.debug) {
          console.warn("ajaxReceiveGameChange error");
        }
        
        self.hideModal();
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

