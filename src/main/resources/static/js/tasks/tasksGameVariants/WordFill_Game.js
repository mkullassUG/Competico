//WordFill
const WordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);

    self.Draggable;

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
        
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
            maxWidth = Math.max(maxWidth, elems[i].offsetWidth + 1);
        }
        $(".answerHolderWrapper").width(maxWidth-16); //-16 bo tak...

        self.DraggableObject = () => {
            var that = {}

            that.init = (initData) => {
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
                        that.trigger_drop(answerElements[i],answerParentElements[i]);
                    }
                },2);

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
                          
                            that.answerDroppedOn(ui.draggable[0],event.target);
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
            }
            
            /*żeby na starcie dobrze się ustawiały odpowiedzi w centrum "answer holdera",
            BUG, nie działa, odpowiedzi przy inicie nie są dragowane?
            
            MOŻE POWODOWAĆ BUGA!*/
            that.trigger_drop = (draggable, droppable) => {
                //dla każdej stworzonej odpowiedzi upuść ją na parencie
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

            that.answerDroppedOn = (answerDiv, fieldDiv) => {
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
            that.init();
            return that;
        }

        self.Draggable = self.DraggableObject();
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

    var superResizeObserverVariantFunction = self.ResizeObserverVariantFunction;
    self.ResizeObserverVariantFunction = () => {
        superResizeObserverVariantFunction();

        //to trzeba ogarnąć, observer nie może się powielać a unobserve nie działa

        var keys = Object.keys(self.answerCurrentlyAt);
        for (let i = 0; i < keys.length; i++){
            var key = keys[i];
            var element = self.answerCurrentlyAt[key];
        
            self.Draggable.trigger_drop(element.answerDiv, element.fieldDiv);
        }
    }

    self.taskVariantInit(taskData);

    return self;
}