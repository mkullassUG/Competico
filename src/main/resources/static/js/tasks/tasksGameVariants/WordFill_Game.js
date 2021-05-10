//WordFill
const WordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "WordFill";

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

        var taskContentReady = $(`<div id="taskContent">`);
        var taskAnswerHolderReady = $(`<div class="pb-2 mb-0 text-center" id="taskAnswerHolder">`);
        var howManyBlanksFound = 0;

        $("#GameDiv").html(``);
        if (self.startWithText) {
            for (let i = 0; i < self.textField.length; i++) {
                var textFieldNode = document.createTextNode(self.textField[i]);

                taskContentReady.append(textFieldNode);
                taskContentReady.append(((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder">&#8203</div></div>`));
                
                howManyBlanksFound++;
            }
        } else {
            for (let i = 0; i < self.textField.length; i++) {
                var textFieldNode = document.createTextNode(self.textField[i]);

                taskContentReady.append(((howManyBlanksFound>=self.emptySpaceCount)?"":`<div class="answerHolderWrapper"><div class="droppableAnswerHolder">&#8203</div></div>`));
                taskContentReady.append(textFieldNode);

                howManyBlanksFound++;
            }
        } 

        //przygotowanie taskAnswerHolder
        for (let i = 0; i < self.words.length; i++) {
            var wordNode = document.createTextNode(self.words[i]);

            var answerHolderWrapper = $(`<div class="answerHolderWrapper startHolder">`);
            var droppableAnswerHolder = $(`<div class="droppableAnswerHolder ui-state-highlight-custom" data-holding="answer`+i+`">`);
            var answerDraggable = $(`<div class="answer draggable" id="answer`+i+`">`);
            answerHolderWrapper.append(droppableAnswerHolder);
            droppableAnswerHolder.append(answerDraggable);
            droppableAnswerHolder.append("&nbsp;");
            answerDraggable.append(wordNode);
            
            taskAnswerHolderReady.append(answerHolderWrapper);
        }
        
        $("#GameDiv").append(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>`);
        $("#GameDiv").append(taskContentReady);

        $("#GameDiv").append(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>`);
        $("#GameDiv").append(taskAnswerHolderReady);
        
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
                    //2021-04-11 oby jak upuszcze na tym samemum miejscu nie pojawiły się problemy
                    $( ".draggable" ).draggable({
                        appendTo: "body",
                        stack: ".answer",
                        cursor: "move",
                        revert: 'invalid',
                        containment: "#GameDivDecoration",
                    });
                    $( ".droppableAnswerHolder" ).droppable({
                        // accept: ".answer",
                        accept: function(draggable) {
                            
                            //only let answers drop on not occupied holders
                            return draggable.hasClass("answer");// && !$(this).hasClass("ui-state-highlight");
                        },
                        drop: function( event, ui ) {

                            //(if holder is occupied then swap?)
                            var draggable = ui.draggable;
                            var droppable = $(this);

                            var holdingId = droppable.attr("data-holding");
                            if (holdingId) {
                                //console.log("is holding: " + holdingId);
                                //swap
                                var draggable2 = $("#" + holdingId);
                                //what was holding old draggable?
                                var droppable2 = $(document)
                                    .find(`[data-holding='`+ draggable.attr("id") +`']`);

                                //1
                                droppable
                                    .addClass( "ui-state-highlight" )
                                    .attr( "data-holding", draggable.attr('id') )
                                    .find( "p" )
                                    .html( "Dropped!" );
                    
                                draggable.position({
                                    my: "center",
                                    at: "center",
                                    of: droppable,
                                    using: function(pos) {
                                        $(this).animate(pos, 200, "linear");
                                    }
                                });
                                //2
                                droppable2
                                    .addClass( "ui-state-highlight" )
                                    .attr( "data-holding", draggable2.attr('id') )
                                    .find( "p" )
                                    .html( "Dropped!" );
                    
                                draggable2.position({
                                    my: "center",
                                    at: "center",
                                    of: droppable2,
                                    using: function(pos) {
                                        $(this).animate(pos, 200, "linear");
                                    }
                                });
                                return;
                            } else {

                                //else set answerHolder as occupied
                                // that.answerDroppedOn(ui.draggable[0],event.target);
                                //znajdź poprzedniego droppable i usuń mu holding
                                var droppable2 = $(document)
                                    .find(`[data-holding='`+ draggable.attr("id") +`']`);

                                if (droppable2.length > 0) {
                                    //console.log("usuwam starego droppable holding data");
                                    droppable2
                                        .attr("data-holding",null)
                                        .removeClass( "ui-state-highlight" );
                                }

                                $( this )
                                    .addClass( "ui-state-highlight" )
                                    .attr( "data-holding", draggable.attr('id') )
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
                        }
                    });
                });
            }
            
            /*żeby na starcie dobrze się ustawiały odpowiedzi w centrum "answer holdera",
            
            2021-04-11 może lepiej to zrobić nie eventem drag tylko:
                draggable.position  using  animate
            */
            that.trigger_drop = (draggable, droppable) => {
                //dla każdej stworzonej odpowiedzi upuść ją na parencie

                //new 2021-04-11 nowa wersja bez simulate
                $(draggable).position({
                    my: "center",
                    at: "center",
                    of: $(droppable),
                    using: function(pos) {
                        $(this).css(pos)
                        //$(this).animate(pos, 200, "linear");
                    }
                });
            }

            //BUG! 2021-04-11 jeśli są takie same słowa to nadpisuje pola
            //fix, uzywam tylko data-holding, pozbywam się that.answerDroppedOn
            // that.answerDroppedOn = (answerDiv, fieldDiv) => {
            //     console.log("answerDroppedOn");
     
            //     $(fieldDiv).attr("data-answer",answerDiv.innerText);
            //     //sprawdza czy wcześniej odpowiedź była przypisana do pola odpowiedzi
            //     if (self.answerCurrentlyAt[$(answerDiv).attr("id")]) {
            //         $(self.answerCurrentlyAt[$(answerDiv).attr("id")].fieldDiv).attr("data-answer", "");
            //         delete self.answerCurrentlyAt[$(answerDiv).attr("id")];
            //     }
            //     console.log(self.answerCurrentlyAt);
            //     self.answerCurrentlyAt[$(answerDiv).attr("id")] = {
            //         "answerDiv" : answerDiv,
            //         "fieldDiv": fieldDiv
            //     };
            // }

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

            var answerId = $(answerField).attr("data-holding");
            var answerElem = $("#" + answerId);
            //   answers.push($(answerField).attr("data-answer"));
            if (answerElem.length > 0)
                answers.push(answerElem[0].innerText);
            else
                answers.push("");
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
        //console.log("ResizeObserverVariantFunction");
        //to trzeba ogarnąć, observer nie może się powielać a unobserve nie działa

        //2021-04-11 new, będęszukać nie przez self.answerCurrentlyAt tylko data-holding
        //console.log("resize");
        // var keys = Object.keys(self.answerCurrentlyAt);
        // for (let i = 0; i < keys.length; i++){
        //     var key = keys[i];
        //     var element = self.answerCurrentlyAt[key];
        //     //TODO 2021-03-27 przydało by się zapobiegać zapętlaniu animacji... dodać do jakiegoś setTimeout i usuwać go jeśli się powtórzy observer
        //     self.Draggable.trigger_drop(element.answerDiv, element.fieldDiv);
        // }

        var fieldDivs = $("[data-holding]");
        for ( let i = 0; i < fieldDivs.length; i++) {
            var fieldDiv = $(fieldDivs[i]);
            var answerId = fieldDiv.attr(`data-holding`);
            var answerDiv = $("#" + answerId);

            self.Draggable.trigger_drop(answerDiv[0], fieldDiv[0]);
        }
    }

    self.taskVariantInit(taskData);

    return self;
}