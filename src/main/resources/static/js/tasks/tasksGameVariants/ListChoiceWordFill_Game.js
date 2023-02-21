const ListChoiceWordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);

    self.taskName = "ListChoiceWordFill";
    self.singleChoiceWordFillArray = [];

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
  
        self.answerCurrentlyAt = [];
        self.textFields = taskData.text; 
        self.wordChoices = taskData.wordChoices;
        self.startWithText = taskData.startWithText;
        self.isTaskDone = false; 
        var rows = self.wordChoices.length;
  
        $("#GameDiv").html(``);
        for (let i = 0; i < rows; i++) {
            var textFieldRowArray = self.textFields[i];
            var wordChoicesRowArray = self.wordChoices[i];
            var startWithText = self.startWithText[i];
            
            var taskContentDiv = $(`<div class="border-top border-gray taskContent text-left">` + ((rows>1)? `(` + (i+1) + `). ` : "") + `</div>`);

            if (startWithText) {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = document.createTextNode(textFieldRowArray[j]);

                    taskContentDiv.append(text)
                    if (wordChoicesRowArray[j]) {
                        var choiceDiv = $(`<div class="choiceDiv">`);

                        taskContentDiv.append(choiceDiv);
                        for (let k = 0; k < (wordChoicesRowArray[j].length); k++) {
                            var choiceWord = document.createTextNode(wordChoicesRowArray[j][k]);

                            var answerVariantDiv = $(`<div class="answer` + self.taskName + `F">`);
                            if ( k != 0 ) 
                                choiceDiv.append("/");

                            answerVariantDiv.append(choiceWord);
                            choiceDiv.append(answerVariantDiv);
                        }   
                    }
                }
            } else {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = document.createTextNode(textFieldRowArray[j]);

                    if (wordChoicesRowArray[j]) {
                        var choiceDiv = $(`<div class="choiceDiv">` + (i+1) + `</div>`);
                        
                        taskContentDiv.append(choiceDiv);
                        for (let k = 0; k < wordChoicesRowArray[j].length; k++) {
                            var choiceWord = document.createTextNode(wordChoicesRowArray[j][k]);
                            
                            var answerVariantDiv = $(`<div class="answer` + self.taskName + `F">`);

                            if ( k != 0 ) 
                                choiceDiv.append("/");

                            answerVariantDiv.append(choiceWord);
                            choiceDiv.append(answerVariantDiv);
                        }   
                    }
                    taskContentDiv.append(text);
                }
            }
            
            var singleContentAnswerContainerDiv = $(`<div class="singleContentAnswerContainer">`);
            singleContentAnswerContainerDiv.append(taskContentDiv);
            $("#GameDiv").append(singleContentAnswerContainerDiv);
        }

        var ChoiceContainer = (answer_, choiceWordFillContainer_) => {
            var that = {};
            that.choiceDiv;
            that.used;
            that.text;
            that.mainChoiceWordFillContainer = choiceWordFillContainer_;
            
            that.init = (answer) => {

                that.choiceDiv = $(answer);
                that.used = false;
                that.text = $(answer).text();

                that.choiceDiv.on('click', (e) => {
                    if (!that.selected) {
                        that.mainChoiceWordFillContainer.choiceWasSelected(that);
                    } else
                        that.unuse();
                })
            }

            that.use = () => {
                that.used = true;
                that.choiceDiv.addClass("bg-primary").addClass("text-white").removeClass("unused" + self.taskName + "F");
            }

            that.unuse = () => {
                that.used = false;
                that.choiceDiv.removeClass("bg-primary").removeClass("text-white").addClass("unused" + self.taskName + "F");
            }

            that.init(answer_);

            return that;
        }

        var ChoiceWordFillContainer = (containerDiv_) => {
            var that = {};
            that.choiceContainers = [];

            that.init = (containerDiv_) => {
                that.choiceHoldingElements = $($(containerDiv_).find(".answer" + self.taskName + "F"));

                for ( let i = 0; i < that.choiceHoldingElements.length; i++) {
                    var choice = that.choiceHoldingElements[i];
                    that.choiceContainers.push(ChoiceContainer(choice, that));
                }
            }

            that.unuseAllChoices = () => {
                for (let i = 0; i < that.choiceContainers.length; i++) {
                    var choice = that.choiceContainers[i];
                    choice.unuse();
                }
            }
            that.choiceWasSelected = (choice) => {
                that.unuseAllChoices();
                choice.use();
            }

            that.init(containerDiv_);
            return that;
        }

        var containers = $(".choiceDiv");
        for (let i = 0; i < containers.length; i++) {
            var containerDiv = containers[i];
            self.singleChoiceWordFillArray.push( ChoiceWordFillContainer(containerDiv) );
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];

        var choiceDivCounter = 0;
        var ChoiceWordFillRows = $(".singleContentAnswerContainer");
        for (let i = 0; i < ChoiceWordFillRows.length; i++) {
            var ChoiceWordFillRow = $(ChoiceWordFillRows[i]);
            
            answers[i] = [];

            var choiceDivs = ChoiceWordFillRow.find(".choiceDiv");
            for ( let j = 0; j < choiceDivs.length; j++) {
                
                var cwfContainer = self.singleChoiceWordFillArray[choiceDivCounter];

                var answerWasSelected = false;
                for ( let k = 0; k < cwfContainer.choiceContainers.length; k++) {
                    var singleChoiceElement = cwfContainer.choiceContainers[k];
    
                    if (singleChoiceElement.used){
                        answers[i][j] = singleChoiceElement.text;
                        answerWasSelected = true;
                    }
                }
                if (!answerWasSelected)
                    answers[i][j] = null;

                choiceDivCounter++;
            }
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