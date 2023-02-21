const WordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "WordFill";

    self.Draggable;

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
        
        self.answerCurrentlyAt = {};
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
        
        var elems = $(".answer ");
        var maxWidth = 0;
        for (let i = 0; i < elems.length; i++){
            maxWidth = Math.max(maxWidth, elems[i].offsetWidth + 1);
        }
        $(".answerHolderWrapper").width(maxWidth-16); 

        self.DraggableObject = () => {
            var that = {}

            that.init = (initData) => {
               
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
                        revert: 'invalid',
                        containment: "#GameDivDecoration",
                    });
                    $( ".droppableAnswerHolder" ).droppable({
                        accept: function(draggable) {
                            
                            //only let answers drop on not occupied holders
                            return draggable.hasClass("answer");
                        },
                        drop: function( event, ui ) {

                            var draggable = ui.draggable;
                            var droppable = $(this);

                            var holdingId = droppable.attr("data-holding");
                            if (holdingId) {
                                
                                var draggable2 = $("#" + holdingId); //swap
                                var droppable2 = $(document)
                                    .find(`[data-holding='`+ draggable.attr("id") +`']`);

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
                                var droppable2 = $(document)
                                    .find(`[data-holding='`+ draggable.attr("id") +`']`);

                                if (droppable2.length > 0) {
                                    
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
            
            that.trigger_drop = (draggable, droppable) => {

                $(draggable).position({
                    my: "center",
                    at: "center",
                    of: $(droppable),
                    using: function(pos) {
                        $(this).css(pos)
                    }
                });
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

            var answerId = $(answerField).attr("data-holding");
            var answerElem = $("#" + answerId);
            
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