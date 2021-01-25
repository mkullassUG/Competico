const taskMokTemplates = [
    {
      taskNumber: 0,
      taskName: `WordFill`,
      text: ["Lorem ipsum dolor sit amet, consectetur "," elit. Quisque vestibulum, enim id fringilla sodales, libero   ipsum "," erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis "," dolor nec turpis. Quisque elementum "," accumsan. Lorem ipsum dolor "," amet, consectetur adipiscing elit. In nec "," nisi, et semper nisl. Cras placerat "," orci eget congue. Duis vitae gravida odio. Etiam elit turpis, "," ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt."],
      startsWithText: true,
      emptySpaceCount: 8,
      possibleAnswers: ["slowo1","slowo2","slowo3","slowo4","slowo5","slowo6","slowo7","slowo8"]
    },
    {
      taskNumber: 1,
      taskName: `template1`,
      description: "Połącz odpowiednie słowa z definicjami",
      words: ["slowo1","slowo2","slowo3","slowo4","slowo5","slowo6","slowo7","slowo8","slowo9","slowo10",],
      definitions: ["definicja1","definicja2","definicja3","definicja4","definicja5","definicja6","definicja7","definicja8","definicja9","definicja10",]
    },
    
    {
      taskNumber: 2,
      taskName: `template2`,
      description: "Układanie zdań w kolejności chronologicznej",
      sentences: [
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Quisque vestibulum, enim id fringilla sodales, libero ipsum maximus erat, id ullamcorper elit ante auctor est.",
        "Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula",
        "Nullam gravida pretium finibus. Nam scelerisque ex nec accumsan dictum?",
        "Pellentesque habitant morbi tristique senectus et",
        "Pellentesque habitant morbi tristique senectus et netus et malesuada",
        "Ullamcorper nibh nec laoreet. Etiam scelerisque",
      ]
    },
    {
      taskNumber: 3,
      taskName: `template3`,
      description: "wstaw odpowiednie słowo w zdaniu (wariant z klawiatura)",
      textField: "Lorem ipsum dolor sit amet, consectetur [blank] elit. Quisque vestibulum, enim id fringilla sodales, libero ipsum [blank] erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis [blank] dolor nec turpis. Quisque elementum [blank] accumsan. Lorem ipsum dolor [blank] amet, consectetur adipiscing elit. In nec [blank] nisi, et semper nisl. Cras placerat [blank] orci eget congue. Duis vitae gravida odio. Etiam elit turpis, [blank] ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt.",
      inputSymbolDetectionRegExp: /\[blank\]/g,
      firstLetters: ["a","b","c","d","e","f","g","h"]
  
    },
    {
      taskNumber: 4,
      taskName: `template4`,
      description: "połącz słowa z ich synonimami"
    },
    
    {
      taskNumber: 5,
      taskName: `template5`,
      description: "odpowiedź true/false",
      textField: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vestibulum, enim id fringilla sodales, libero ipsum maximus erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis sem dolor nec turpis.",
      questions: ["Nullam gravida pretium finibus. Nam scelerisque ex nec accumsan dictum?",
      "ed consectetur ullamcorper nibh nec laoreet. Etiam scelerisque urna massa?",
      "Nam viverra sit amet arcu sit amet posuere?",
      "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas?",
      "Nunc vehicula placerat orci, tristique bibendum lacus porta quis?",
      "Duis euismod sollicitudin neque sit amet venenatis?"]
    }
];

var resize_ob;

//WordFill
const TaskVariant0 = (taskData) => {
    var self = TaskVariant(taskData);

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
        console.log(taskData)
        self.answerCurrentlyAt = {};
        self.taskData = taskData;
        self.textField = taskData.text;
        self.words = taskData.possibleAnswers;
        self.emptySpaceCount = taskData.emptySpaceCount;
        self.trigger_drop = (draggable, droppable) => {
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
        var taskContentReady = "";
        var howManyBlanksFound = 0;
        if (taskData.startsWithText) {
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
        
        //dla każdej stworzonej odpowiedzi upuść ją na parencie
        var answerElements = $(".answer");
        var answerParentElements = $(".answer").parent();
        for (let i = 0; i < answerElements.length; i++) {
          self.trigger_drop(answerElements[i],answerParentElements[i]);
        }
        var answerDroppedOn = (answerDiv, fieldDiv) => {
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
        if (resize_ob)
          resize_ob.unobserve(document.querySelector("#GameDiv"));
        resize_ob = new ResizeObserver(function(entries) {
          var keys = Object.keys(self.answerCurrentlyAt);
          for (let i = 0; i < keys.length; i++){
            var key = keys[i];
            var element = self.answerCurrentlyAt[key];
        
            self.trigger_drop(element.answerDiv, element.fieldDiv);
          }
        });

        resize_ob.observe(document.querySelector("#GameDiv"));
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
//WordConnect (nie działa na mobilnym)
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

    var taskContentReady= ""
    for (let i = 0; i < self.words.length; i++) {
      var word = self.words[i];
      var definition = self.definitions[i];
      taskContentReady += `<div class="line" ><div class="word" data-order="`+i+`">`;
      taskContentReady += word;
      taskContentReady += `</div><div class="definition" data-order="`+i+`">`;
      taskContentReady += definition;
      taskContentReady += `</div></div>`;
    }

    $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
    <div id="taskContent">`+ taskContentReady +`</div>
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

    var anEndpointDestination = {
      endpoint: "Dot",
      isSource: false,
      isTarget: true,
      maxConnections: -1,
      connectorStyle: {
          dashstyle: "2 4"
      },
      anchor: [0, 1, -1, 0]
    };
    var anEndpointSource = {
      endpoint: "Rectangle",
      isSource: true,
      isTarget: false,
      maxConnections: -1,
      anchor: [1, 0, 1, 0]
    };

    for (let i = 0; i < wordDivs.length; i++) {
      var wordDiv = $(wordDivs[i]);
      self.endpointSources.push(
        instance.addEndpoint(
          wordDiv, {
          connectorStyle: {strokeStyle: "blue", lineWidth: 1},
          connectorHoverStyle: {lineWidth: 2},
          },
          anEndpointSource,
        )
      )
      fixEndpoints(wordDiv);
    }
    for (let i = 0; i < definitionDivs.length; i++) {
      var definitionDiv = $(definitionDivs[i]);
      self.endpointDestinations.push(
        instance.addEndpoint(
          definitionDiv, {
              connectorStyle: {strokeStyle: "blue", lineWidth: 1},
              connectorHoverStyle: {lineWidth: 2},
          },
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
    resize_ob = new ResizeObserver(function(entries) {
      for (let i = 0; i < wordDivs.length; i++) {
        var wordDiv = $(wordDivs[i]);
        
        fixEndpoints(wordDiv);
      }
      for (let i = 0; i < definitionDivs.length; i++) {
        var definitionDiv = $(definitionDivs[i]);
        
        fixEndpoints(definitionDiv);
      }
    });
    resize_ob.observe(document.querySelector("#GameDiv"));
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
      var sourceIndex = parseInt($(self.connections[key].info.source[0]).data("order"));
      var targetIndex = parseInt($(self.connections[key].info.target[0]).data("order"));
      answers[sourceIndex] = targetIndex;
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
      taskContentReady += `<li class="ui-state-default ui-sortable-handle"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>` + sentence + `<span class="float-right ui-icon ui-icon-arrowthick-2-n-s"></span></li>`;
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

var currentObserver;

/*TODO
  (raczej nie użyje jquery-ui selectable, zwyczajne buttony onclick wystarczą)
*/
//ListWordFill
const TaskVariant3 = (taskData) => {
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
//WordFill bez animacji z klikaniem
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

const TaskVariant = (taskData) => {
  var self = taskData;//czy tu nie będzie porblemu, że nadpisuje i nie użyje ponownei taska

  self.taskVariantInit = (taskData) => {
    self.taskNumber = taskData.taskNumber;
    if (resize_ob)
      resize_ob.unobserve(document.querySelector("#GameDiv"));
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

//   const taskTemplates = [
//     {
//       taskId: 0,
//       nameId: `template0`,
//       description: "wstaw odpowiednie słowo w zdaniu",
//       textField: "Lorem ipsum dolor sit amet, consectetur [blank] elit. Quisque vestibulum, enim id fringilla sodales, libero ipsum [blank] erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis [blank] dolor nec turpis. Quisque elementum [blank] accumsan. Lorem ipsum dolor [blank] amet, consectetur adipiscing elit. In nec [blank] nisi, et semper nisl. Cras placerat [blank] orci eget congue. Duis vitae gravida odio. Etiam elit turpis, [blank] ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt.",
//       inputSymbolDetectionRegExp: /\[blank\]/g,
//       firstLetters: ["a","b","c","d","e","f","g","h"],

//     },
//     {
//       taskId: 1,
//       nameId: `template1`,
//       description: "odpowiedź true/false",
//       textField: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vestibulum, enim id fringilla sodales, libero ipsum maximus erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis sem dolor nec turpis.",
//       questions: ["Nullam gravida pretium finibus. Nam scelerisque ex nec accumsan dictum?",
//       "ed consectetur ullamcorper nibh nec laoreet. Etiam scelerisque urna massa?",
//       "Nam viverra sit amet arcu sit amet posuere?",
//       "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas?",
//       "Nunc vehicula placerat orci, tristique bibendum lacus porta quis?",
//       "Duis euismod sollicitudin neque sit amet venenatis?"]
//     },
//     {
//       taskId: 2,
//       nameId: `template2`,
//       description: "Połącz odpowiednie słowa z definicjami"
//     },
//     {
//       taskId: 3,
//       nameId: `template3`,
//       description: "połącz słowa z ich synonimami"
//     }

// ];

//useless ? NIE, może wygodniej będzie używać tego dużego obiektu
// const taskLogicVariants = {
    
//     //template0
//     logicVariant0 : (_taskData) => {
//       var self = {
//         answerCurrentlyAt: {},
//         taskData: null,
//         textField: null,
//         words: null,
//         emptySpaceCount: null
//       };
  
//       //przygotować vizualnie i logike na taska
//       var taskVariantInit = (taskData) => {
//         self.taskData = taskData;
//         self.textField = taskData.text;
//         //self.inputSymbolDetection = taskData.inputSymbolDetection;
//         //self.inputSymbolDetectionRegExp = taskData.inputSymbolDetectionRegExp;
//         //self.firstLetters = taskData.firstLetters;
//         self.words = taskData.possibleAnswers;
//         self.emptySpaceCount = taskData.emptySpaceCount;
        
//         self.trigger_drop = (draggable, droppable) => {
//           var draggable = $(draggable).draggable(),
//           droppable = $(droppable).droppable(),
//           droppableOffset = droppable.offset(),
//           draggableOffset = draggable.offset(),
//           dx = droppableOffset.left - draggableOffset.left,
//           dy = droppableOffset.top - draggableOffset.top;
  
//           draggable.simulate("drag", {
//               dx: dx,
//               dy: dy
//           });
//         }
//         //przygotowanie taskContent
//         /*
//         var i = 0;
//         var taskContentReady = self.textField.replace(self.inputSymbolDetectionRegExp, function() { 
//           return '<div class="answerHolderWrapper"><div class="droppableAnswerHolder" data-answer="">' + (self.firstLetters[i]?self.firstLetters[i++]:"") +'</div></div>';
//         });*/
//         var taskContentReady = "";
//         var howManyBlanksFound = 0;
//         if (taskData.startsWithText) {
//           for (let i = 0; i < self.textField.length; i++) {
//             taskContentReady += self.textField[i] + ((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder" data-answer="">_</div></div>`);
//             howManyBlanksFound++;
//           }
//         } else {
//           for (let i = 0; i < self.textField.length; i++) {
//             taskContentReady += ((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder" data-answer="">_</div></div>`) + self.textField[i];
//             console.log(howManyBlanksFound);
//             howManyBlanksFound++;
            
//           }
//         }
  
//         //przygotowanie taskAnswerHolder
//         var taskAnswerHolderReady = 
//         `<div class="pb-2 mb-0 " id="taskAnswerHolder">`;
//         for (let i = 0; i < self.words.length; i++) {
//           taskAnswerHolderReady += 
//           `<div class="answerHolderWrapper">
//             <div class="droppableAnswerHolder">
//               <div class="answer draggable">
//                 ` + self.words[i] + `
//               </div>_
//             </div>
//           </div>`
          
//         }
//         taskAnswerHolderReady += `</div>`;
  
//         $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
//         <div id="taskContent">`+ taskContentReady +`</div>
//         <h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>
//         <div class="pb-2 mb-0 " id="taskAnswerHolder">
//           ` + taskAnswerHolderReady + `
//         </div>`);
        
//         //dla każdej stworzonej odpowiedzi upuść ją na parencie
//         var answerElements = $(".answer");
//         var answerParentElements = $(".answer").parent();
//         for (let i = 0; i < answerElements.length; i++) {
//           self.trigger_drop(answerElements[i],answerParentElements[i]);
//         }

//         var answerDroppedOn = (answerDiv, fieldDiv) => {
  
//           //console.log("drop");
//           $(fieldDiv).data("answer",answerDiv.innerText);
  
//           //sprawdza czy wczęsniej odpowiedź była przypisana do pola odpowiedzi
//           if (self.answerCurrentlyAt[answerDiv.innerText]) {
//             $(self.answerCurrentlyAt[answerDiv.innerText].fieldDiv).data("answer","");
//             delete self.answerCurrentlyAt[answerDiv.innerText];
//           }
  
//           self.answerCurrentlyAt[answerDiv.innerText] = {
//             "answerDiv" : answerDiv,
//             "fieldDiv": fieldDiv
//           };
//         }
        
  
//         $( function() {
//           $( ".draggable" ).draggable({
//             appendTo: "body",
//             stack: ".answer",
//             cursor: "move",
//             revert: 'invalid',
//             containment: "#GameDivDecoration",
//           });
//           $( ".droppableAnswerHolder" ).droppable({
//             accept: ".answer",
//             drop: function( event, ui ) {
              
//               answerDroppedOn(ui.draggable[0],event.target);
//               $( this )
//                 .addClass( "ui-state-highlight" )
//                 .find( "p" )
//                   .html( "Dropped!" );
  
//               var $this = $(this);
  
//               ui.draggable.position({
//                 my: "center",
//                 at: "center",
//                 of: $this,
//                 using: function(pos) {
//                   $(this).animate(pos, 200, "linear");
//                 }
//               });
  
//             }
//           });
//         });
//         /*potrzebne żeby ustawiać elementy na swoich pozycjach bo "uciekają"*/
//         //$(window).off("resize")
//         //events = $._data(window, 'events');
  
//         //to trzeba ogarnąć, observer nie może się powielać a unobserve nie działa
//         console.log("ResizeObserver setup")
//         resize_ob = new ResizeObserver(function(entries) {
//           console.log("ResizeObserver inform");
//           var keys = Object.keys(self.answerCurrentlyAt);
//           for (let i = 0; i < keys.length; i++){
//             var key = keys[i];
//             var element = self.answerCurrentlyAt[key];
        
//             self.trigger_drop(element.answerDiv, element.fieldDiv);
//           }
//         });
//         // start observing for resize
//         resize_ob.observe(document.querySelector("#GameDiv"));
//       }
//       taskVariantInit(_taskData);
  
//       //pozbierać ospowiedzi z inputów i włożyć do answers..tablicy lepiej
//       self.getAnswers = () => {
//         var answers = [];
  
//         var answerFields = $("#GameDiv > #taskContent").find(".droppableAnswerHolder");
  
//         for(let i = 0; i < answerFields.length; i++) {
//           var answerField = answerFields[i];
//           answers.push($(answerField).data("answer"));
//         }
  
//         return answers;
//       }
  
//       //zresetować (wyczyścić i od nowa init)
//       self.reset = () => {
        
//         self.answerCurrentlyAt = {};
//         $("#GameDiv").html("");
  
//         taskVariantInit(_taskData);
//       }
  
//       return self;
//     },
//     logicVariant1 : (taskData) => {
//       var self = {};
  
  
//       return self;
//     },
//     //UWAGA, DZIAŁA JEŚLI JEST SIĘ NA WIDOKU MOBILNYM podczas ładowania zadania
//     logicVariant2 : (_taskData) => {
//       var self = {
//         answerCurrentlyAt: {},
//         sentences: null
//       };
  
//       var taskVariantInit = (taskData) => {
//         self.sentences = taskData.sentences;
//         var taskContentReady = `<ul id="sortable" class="ui-sortable">`;
//         for (let i = 0; i < self.sentences.length; i++) {
//           var sentence = self.sentences[i];
//           taskContentReady += `<li class="ui-state-default ui-sortable-handle"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>` + sentence + `<span class="float-right ui-icon ui-icon-arrowthick-2-n-s"></span></li>`;
//         }
//         taskContentReady += `</ul>`;
  
//         $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
//         <div id="taskContent">`+ taskContentReady +`</div>
//         `);
        
        
//         $( "#sortable" ).sortable({
//           zIndex: 9999,
//           containment: "#taskContent",
//           appendTo: document.body,
//           cursorAt: { top: 17 }
//         });
//         var appendTo = $( "#sortable" ).sortable( "option", "appendTo" );
//         console.log(appendTo);
//       }
//       taskVariantInit(_taskData);
  
//       self.getAnswers = () => {
//         var answers = [];
  
//         var liElements =  $("#sortable").find("li");
  
//         for(let i = 0; i < liElements.length; i++) {
//           var li = liElements[i];
//           var sentence = $(li).clone()    //clone the element
//           .children() //select all the children
//           .remove()   //remove all the children
//           .end()  //again go back to selected element
//           .text();
  
//           answers.push(sentence);
//         }
//         return answers;
//       }
  
//       self.reset = () => {
//         self.answerCurrentlyAt = {};
//         $("#GameDiv").html("");
  
//         taskVariantInit(_taskData);
//       }
  
//       return self;
//     },
//     logicVariant3 : (taskData) => {
//       var self = {};
  
  
//       return self;
//     },
// }

//observer holder
