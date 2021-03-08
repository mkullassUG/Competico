// var currentObserver;
var resize_observer;

const TaskVariant = (taskData) => {
  var self = taskData;//czy tu nie będzie porblemu, że nadpisuje i nie użyje ponownei taska

  self.taskVariantInit = (taskData) => {
    self.currentTaskNumber = taskData.currentTaskNumber;
    if (resize_observer)
      resize_observer.unobserve(document.querySelector("#GameDiv"));
  }

  self.getAnswers = () => {
    var answers = [];//nie zawsze jest arrayem

    return answers;
  }

  self.reset = () => {
    self.answerCurrentlyAt = {};
    $("#GameDiv").html("");

  }

  return self;
}

//WordFill
const TaskVariant0 = (taskData) => {
    var self = TaskVariant(taskData);

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
        console.log(taskData)
        self.answerCurrentlyAt = {};
        // self.taskData = taskData;
        self.textField = taskData.text;
        self.words = taskData.possibleAnswers;
        self.emptySpaceCount = taskData.emptySpaceCount;
        self.startWithText = taskData.startWithText;

        var taskContentReady = "";
        var howManyBlanksFound = 0;
        if (self.startWithText) {
          for (let i = 0; i < self.textField.length; i++) {
            taskContentReady += self.textField[i] + ((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder" data-answer="">_</div></div>`);
            howManyBlanksFound++;
          }
        } else {
          for (let i = 0; i < self.textField.length; i++) {
            taskContentReady += ((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder" data-answer="">_</div></div>`) + self.textField[i];
            console.log(howManyBlanksFound);
            howManyBlanksFound++;
            
          }
        } 

        //przygotowanie taskAnswerHolder
        var taskAnswerHolderReady = 
        `<div class="pb-2 mb-0 " id="taskAnswerHolder">`;
        for (let i = 0; i < self.words.length; i++) {
          taskAnswerHolderReady += 
          `<div class="answerHolderWrapper">
            <div class="droppableAnswerHolder">
              <div class="answer draggable">
                ` + self.words[i] + `
              </div>_
            </div>
          </div>`
        }
        taskAnswerHolderReady += `</div>`;
  
        $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
        <div id="taskContent">`+ taskContentReady +`</div>
        <h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>
        <div class="pb-2 mb-0 " id="taskAnswerHolder">
          ` + taskAnswerHolderReady + `
        </div>`);
        
        /*ustawienie szerokości każdego "answer holdera" na max długość najdłuższej odpowiedzi*/
        var elems = $(".answer ");
        var maxWidth = 0;
        for (let i = 0; i < elems.length; i++){
          /*ręcznie powniesione o 1 px w górę*/
          //console.log(elems[i])
          //console.log(elems[i].offsetWidth)
          maxWidth = Math.max(maxWidth, elems[i].offsetWidth + 1);
        }
        console.log(maxWidth);
        $(".answerHolderWrapper").width(maxWidth-16); //-16 bo tak...

        //dla każdej stworzonej odpowiedzi upuść ją na parencie
        /*żeby na starcie dobrze się ustawiały odpowiedzi w centrum "answer holdera",
        BUG, nie działa, odpowiedzi przy inicie nie są dragowane?*/
        self.trigger_drop = (draggable, droppable) => {
          //console.log("trigger_drop");
          var draggable = $(draggable).draggable(),
          droppable = $(droppable).droppable(),
          droppableOffset = droppable.offset(),
          draggableOffset = draggable.offset(),
          dx = droppableOffset.left - draggableOffset.left,
          dy = droppableOffset.top - draggableOffset.top;
          
          draggable.simulate("drag", {
              dx: dx,
              dy: dy
          });
        }
        /* 2021-02-26
        już widzę że bug polega na tym, że draguje answery jak jeszcze nie ma gdzie ich dragować?
        cancer rozwiązanie: poczekać chwilę
        u mnie wystarczają 2ms, 1ms to za mało.

        kolejny problem to, że nie będzie za dobrze w qunit testach to działało, chyba że poczekam ~3ms
        
        TODO wymyśleć lepsze rozwiązanie
        */
        setTimeout(function() {
          var answerElements = $(".answer");
          var answerParentElements = $(".answer").parent();
          for (let i = 0; i < answerElements.length; i++) {
            self.trigger_drop(answerElements[i],answerParentElements[i]);
          }
        },2);
        

        var answerDroppedOn = (answerDiv, fieldDiv) => {
         // console.log("answerDroppedOn");

          $(fieldDiv).data("answer",answerDiv.innerText);
          //sprawdza czy wczęsniej odpowiedź była przypisana do pola odpowiedzi
          if (self.answerCurrentlyAt[answerDiv.innerText]) {
            $(self.answerCurrentlyAt[answerDiv.innerText].fieldDiv).data("answer","");
            delete self.answerCurrentlyAt[answerDiv.innerText];
          }
  
          self.answerCurrentlyAt[answerDiv.innerText] = {
            "answerDiv" : answerDiv,
            "fieldDiv": fieldDiv
          };
        }
        
  
        $( function() {
          $( ".draggable" ).draggable({
            appendTo: "body",
            stack: ".answer",
            cursor: "move",
            revert: 'invalid',
            containment: "#GameDivDecoration",
          });
          $( ".droppableAnswerHolder" ).droppable({
            accept: ".answer",
            drop: function( event, ui ) {
              
              answerDroppedOn(ui.draggable[0],event.target);
              $( this )
                .addClass( "ui-state-highlight" )
                .find( "p" )
                  .html( "Dropped!" );
  
              var $this = $(this);
  
              ui.draggable.position({
                my: "center",
                at: "center",
                of: $this,
                using: function(pos) {
                  $(this).animate(pos, 200, "linear");
                }
              });
  
            }
          });
        });

        //to trzeba ogarnąć, observer nie może się powielać a unobserve nie działa
        console.log("ResizeObserver")
        if (resize_observer)
          resize_observer.unobserve(document.querySelector("#GameDiv"));
        resize_observer = new ResizeObserver(function(entries) {
          var keys = Object.keys(self.answerCurrentlyAt);
          for (let i = 0; i < keys.length; i++){
            var key = keys[i];
            var element = self.answerCurrentlyAt[key];
        
            self.trigger_drop(element.answerDiv, element.fieldDiv);
          }
        });

        resize_observer.observe(document.querySelector("#GameDiv"));
    }

    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();

        var answerFields = $("#GameDiv > #taskContent").find(".droppableAnswerHolder");
  
        for(let i = 0; i < answerFields.length; i++) {
          var answerField = answerFields[i];
          answers.push($(answerField).data("answer"));
        }

        return {"answers": answers};
    }

    var resetSuper = self.reset;
    self.reset = () => {
        resetSuper();

        self.taskVariantInit(params);
    }

    self.taskVariantInit(taskData);

    return self;
}
//WordConnect (nie działa na mobilnym?)
const TaskVariant1 = (taskData) => {
  var self = TaskVariant(taskData);

  var taskVariantInitSuper = self.taskVariantInit;
  self.taskVariantInit = (taskData) => {
    taskVariantInitSuper(taskData);

    self.answerCurrentlyAt = {};
    self.connections = {};
    self.endpointSources = [];
    self.endpointDestinations = [];
    self.words = taskData.leftWords;
    self.definitions = taskData.rightWords;

    var taskContentReady= $(`<div class="containerC">`);
    var taskContentLeft = $(`<div class="leftSideC">`);
    var taskContentRight = $(`<div class="rightSideC">`);
    for (let i = 0; i < self.words.length; i++) {
      var word = self.words[i];
      var definition = self.definitions[i];
      
      taskContentLeft.append(`<div class="leftLineC"><div class="word" data-order="`+i+`">` + word+ `</div></div>`);

      taskContentRight.append(`</div><div class="rightLineC"><div class="definition" data-order="`+i+`">` + definition + `</div></div>`);
    }
    taskContentReady.append(taskContentLeft);
    taskContentReady.append(taskContentRight);

    $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
    <div id="taskContent">`+ taskContentReady.html() +`</div>
    `);
    var wordDivs = $(".word"),
    definitionDivs = $(".definition");
    //connections

    var instance = jsPlumb.getInstance();

    var fixEndpoints = (parentnode) => {

      //get list of current endpoints
      var endpoints = instance.getEndpoints(parentnode);
  
      //there are 2 types - input and output
  
      var inputAr = $.grep(endpoints, function (elementOfArray, indexInArray) {
          return elementOfArray.isSource; //input
      });
  
      var outputAr = $.grep(endpoints, function (elementOfArray, indexInArray) {
          return elementOfArray.isTarget; //output
      });
  
      calculateEndpoint(inputAr, true);
      calculateEndpoint(outputAr, false);
  
      instance.repaintEverything();
    }

    var calculateEndpoint = (endpointArray, isInput) => {

      //multiplyer
      var mult = 1 / (endpointArray.length + 1);
  
      for (var i = 0; i < endpointArray.length; i++) {
  
          if (isInput) {
  
              //position
              endpointArray[i].anchor.x = 1;
              endpointArray[i].anchor.y = mult * (i + 1);
          } else {
  
              //position
              endpointArray[i].anchor.x = 0;
              endpointArray[i].anchor.y = mult * (i + 1);
          }
      }
    }

    var colorPicker = (iteration) => {
      if ( iteration % 9 == 0) return "purple";
      else if ( iteration % 9 == 0) return "gold";
      else if ( iteration % 8 == 0) return "cyan";
      else if ( iteration % 7 == 0) return "lime";
      else if ( iteration % 6 == 0) return "orange";
      else if ( iteration % 5 == 0) return "black";
      else if ( iteration % 4 == 0) return "pink";
      else if ( iteration % 3 == 0) return "yellow";
      else if ( iteration % 2 == 0) return "green";
      else if ( iteration % 1 == 0) return "blue";
      else return "red";
    }
    var anEndpointDestination;
    var anEndpointSource;

    for (let i = 0; i < wordDivs.length; i++) {
      var wordDiv = $(wordDivs[i]);
      anEndpointSource = {
        endpoint: "Rectangle",
        paintStyle:{fillStyle:"blue",radius:7,lineWidth:3},
        hoverPaintStyle:{fillStyle:"darkblue",radius:7,lineWidth:3},
        isSource: true,
        isTarget: false,
        maxConnections: 1,
        anchor: [0, 0, 0, 0, 10, 0],
        connectorStyle: {strokeStyle: colorPicker(i), lineWidth: 4},
        connectorHoverStyle: {lineWidth: 6},
        // connector : ["Bezier", { curviness: 30 }],
        connector: "Bezier",
      };
      self.endpointSources.push(
        instance.addEndpoint(
          wordDiv, 
          anEndpointSource,
        )
      )
      fixEndpoints(wordDiv);
    }
    for (let i = 0; i < definitionDivs.length; i++) {
      var definitionDiv = $(definitionDivs[i]);
      anEndpointDestination = {
        endpoint: "Dot",
        paintStyle:{strokeStyle:"blue",fillStyle:"transparent",radius:7,lineWidth:3},
        hoverPaintStyle:{strokeStyle:"blue",fillStyle:"blue",radius:7,lineWidth:3},  
        isSource: false,
        isTarget: true,
        maxConnections: 1,
        connectorStyle: {
            dashstyle: "2 4"
        },
        anchor: [0, 0, 0, 0, -10, 0],
        connectorStyle: {strokeStyle: colorPicker(i), lineWidth: 4},
        connectorHoverStyle: {lineWidth: 6},
        // connector : ["Bezier", { curviness: 30 }],
        connector: "Bezier",
        
      };
      self.endpointDestinations.push(
        instance.addEndpoint(
          definitionDiv, 
          anEndpointDestination,
        )
      )
      fixEndpoints(definitionDiv);
    }

    //tutaj na zmiane szerokości observer

    instance.bind("connection", function(info,ev) {
      self.connections[info.sourceId] = {
        info: info,
        ev: ev
    }
    });
    instance.bind("beforeDetach", function(connection) {
      console.log("beforeDetach");
      console.log(connection);
    });
  
    instance.bind("connectionDetached", function(info, originalEvent) {
      console.log("connectionDetached");
      console.log(info);
      console.log(originalEvent);
    });
    instance.bind("connectionMoved", function(info, originalEvent) {
      console.log("connectionMoved");
      console.log(info);
      console.log(originalEvent);
    });
  
    instance.bind("connectionAborted", function(info, originalEvent) {
      console.log("connectionMoved");
      console.log(info);
      console.log(originalEvent);
    });
  
    instance.bind("connectionDrag", function(info) {
      console.log("connectionDrag");
      console.log(info);
    });
    instance.bind("connectionDragStop", function(info) {
      console.log("connectionDrag");
      console.log(info);
    });
    console.log("ResizeObserver setup")
    resize_observer = new ResizeObserver(function(entries) {
      for (let i = 0; i < wordDivs.length; i++) {
        var wordDiv = $(wordDivs[i]);
        
        fixEndpoints(wordDiv);
      }
      for (let i = 0; i < definitionDivs.length; i++) {
        var definitionDiv = $(definitionDivs[i]);
        
        fixEndpoints(definitionDiv);
      }
    });
    resize_observer.observe(document.querySelector("#GameDiv"));
  }

  var getAnswersSuper = self.getAnswers;
  self.getAnswers = () => {

    //obecnie jest bug gdzie nie czytawięcej niż jednego połączenie wychodzacego ze słowa
    //w tym przypadku odpowiedzi nie są posegregowane 
    //var answers = getAnswersSuper();
    var answers = {};
    
    var keys = Object.keys(self.connections);

    for (let i = 0; i < keys.length; i++) {
      var key = keys[i];
      // var sourceIndex = parseInt($(self.connections[key].info.source[0]).data("order"));
      // var targetIndex = parseInt($(self.connections[key].info.target[0]).data("order"));

      var sourceText = $(self.connections[key].info.source[0]).text();
      var targetText = $(self.connections[key].info.target[0]).text();
      answers[sourceText] = targetText;
    }
    //TODO, zrobić mapę słowo index -> defincija index
    return  {"answerMapping": answers};
  }

  var resetSuper = self.reset;
  self.reset = () => {
      resetSuper();

      self.taskVariantInit(taskData);
  }

  self.taskVariantInit(taskData);

  return self;
}
//ChronologicalOrder
const TaskVariant2 = (taskData) => {
  var self = TaskVariant(taskData);

  var taskVariantInitSuper = self.taskVariantInit;
  self.taskVariantInit = (taskData) => {
    taskVariantInitSuper(taskData);

    self.answerCurrentlyAt = {};
    self.sentences = taskData.sentences;

    var taskContentReady = `<ul id="sortable" class="ui-sortable">`;
    for (let i = 0; i < self.sentences.length; i++) {
      var sentence = self.sentences[i];
      taskContentReady += `<li class="ui-state-default ui-sortable-handle"><span class="ui-icon-left ui-icon ui-icon-arrowthick-2-n-s"></span>` + sentence + `<span class="ui-icon-right ui-icon float-right ui-icon-arrowthick-2-n-s"></span></li>`;
    }
    taskContentReady += `</ul>`;

    $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
    <div id="taskContent">`+ taskContentReady +`</div>
    `);
    
    
    $( "#sortable" ).sortable({
      zIndex: 9999,
      containment: "#taskContent",
      appendTo: document.body,
      cursorAt: { top: 17 }
    });
    var appendTo = $( "#sortable" ).sortable( "option", "appendTo" );
    console.log(appendTo);
  }

  var getAnswersSuper = self.getAnswers;
  self.getAnswers = () => {
    var answers = getAnswersSuper();

    var liElements =  $("#sortable").find("li");

    for(let i = 0; i < liElements.length; i++) {
      var li = liElements[i];
      var sentence = $(li).clone()    //clone the element
      .children() //select all the children
      .remove()   //remove all the children
      .end()  //again go back to selected element
      .text();

      answers.push(sentence);
    }

    return {"answers": answers};
  }

  var resetSuper = self.reset;
  self.reset = () => {
    resetSuper();

    self.taskVariantInit(taskData);
  }

  self.taskVariantInit(taskData);

  return self;
}

/*TODO
  (raczej nie użyje jquery-ui selectable, zwyczajne buttony onclick wystarczą)
*/
//ListWordFill
const TaskVariant3 = (taskData) => {
  var self = TaskVariant(taskData);

  var taskVariantInitSuper = self.taskVariantInit;
  self.taskVariantInit = (taskData) => {
      taskVariantInitSuper(taskData);

      //NEW 2021-03-08
      console.log(taskData)
      self.answerCurrentlyAt = [];//teraz to będzie tablica obiektów trzymających zaznaczoną odpowiedź
      //TYLKO to sięnie zgrywa z tym co jest w reset TaskVariant, tam jest nadal obiekt
      //to moge zamiast tablicy obiekt trzymający indexy, ex

      // self.taskData = taskData;
      self.textField = taskData.text; //tablica tablic zdań
      self.words = taskData.possibleAnswers;//tablica tablic słów
      self.emptySpaceCount = taskData.emptySpaceCount; //teraz to jest tablica liczb (ale nie miała byc jedna odpowiedz na zdanie max?) O.o
      self.startWithText = taskData.startWithText; //tablica boolean

      //dla każdego zdania:
      var rows = self.words.length;

      $("#GameDiv").html(``);

      for (let i = 0; i < rows; i++) {
          var textField = self.textField[i];
          var words = self.words[i];
          var emptySpaceCount = self.emptySpaceCount[i];
          var startWithText = self.startWithText[i];

          //1.1 miejsce na index zdania 
          console.log("zdanie nr: " + i);

          //1.2 ustawić miejsce na tekst
          var taskContentReady = "("+ (i+1) +"). ";
          var howManyBlanksFound = 0;
          if (startWithText) {
            for (let i = 0; i < textField.length; i++) {
              taskContentReady += textField[i] + ((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answerLWF">[blank]</div>`);
              howManyBlanksFound++;
            }
          } else {
            for (let i = 0; i < textField.length; i++) {
              taskContentReady += ((howManyBlanksFound>=emptySpaceCount)?"":`<div class="answerLWF">[blank]</div>`) + textField[i];
              console.log(howManyBlanksFound);
              howManyBlanksFound++;
              
            }
          } 

          //2.1 ustawić miejsce na odpowiedzi
          var taskAnswerHolderReady = 
          `<div class="pb-2 mb-0 text-center" id="taskAnswerHolder">`;
          for (let i = 0; i < words.length; i++) {
            taskAnswerHolderReady += 
            `<div class="answerLWF">` + words[i] + `</div>`
          }
          taskAnswerHolderReady += `</div>`;


          $("#GameDiv").append(`
          <div id="taskContent" class="border-top border-gray">`+ taskContentReady +`</div>
          <div class="mb-0 border-bottom border-gray" id="taskAnswerHolder">
            ` + taskAnswerHolderReady + `
          </div>`)

          //2.2 przypisać listenery pod nade odpowiedzi i miejsce na ostateczną odpowiedź
          //Jquery-UI selectable (grupa tego zdania)

          //3. oddzielić zdanie od nastepnego
          
          //nie musze ustawiac szerokości holdera bo robie SELECTABLE a nie DRAGGABLE
          //https://api.jqueryui.com/selectable/
      }
  }

  var getAnswersSuper = self.getAnswers;
  self.getAnswers = () => {
    var answers = getAnswersSuper();

    return answers;
  }

  var resetSuper = self.reset;
  self.reset = () => {
    resetSuper();

    self.taskVariantInit(taskData);
  }

  self.taskVariantInit(taskData);
  return self;
}



//WordFill bez animacji z klikaniem (DELETE this?)
const TaskVariant4 = (taskData) => {
  var self = TaskVariant(taskData);
  
  var taskVariantInitSuper = self.taskVariantInit;
  self.taskVariantInit = (taskData) => {
    taskVariantInitSuper(taskData);

  }

  var getAnswersSuper = self.getAnswers;
  self.getAnswers = () => {
    var answers = getAnswersSuper();

    return answers;
  }

  var resetSuper = self.reset;
  self.reset = () => {
    resetSuper();

    self.taskVariantInit(taskData);
  }

  self.taskVariantInit(taskData);
  return self;
}

