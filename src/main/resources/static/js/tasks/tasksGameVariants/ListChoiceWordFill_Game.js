const ListChoiceWordFill_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    
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
            var taskContentReady = "("+ (i+1) +"). ";

            if (startWithText) {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = textFieldRowArray[j];

                    taskContentReady += text;
                    if (wordChoicesRowArray[j]) {
                        taskContentReady += `<div class="choiceDiv">`;
                        for (let k = 0; k < (wordChoicesRowArray[j].length); k++) {
                            var choiceWord = wordChoicesRowArray[j][k];

                            if ( k == 0 ) 
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>`;
                            else 
                                taskContentReady += `/<div class="answerLCWF">`+choiceWord+`</div>`;
                        }   
                        taskContentReady += `</div>`;
                    }
                }
            } else {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = textFieldRowArray[j];

                    
                    if (wordChoicesRowArray[j]) {
                        taskContentReady += `<div class="choiceDiv">`
                        for (let k = 0; k < wordChoicesRowArray[j].length; k++) {
                            var choiceWord = wordChoicesRowArray[j][k];
                            
                            if ( k == 0 ) 
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>`;
                            else 
                                taskContentReady += `/<div class="answerLCWF">`+choiceWord+`</div>`;
                        }   
                        taskContentReady += `</div>`;
                    }
                    taskContentReady += text;
                }
            }

            $("#GameDiv").append(`
            <div class="singleContentAnswerContainer">
                <div class="border-top border-gray taskContent text-left">`+ taskContentReady +`</div>
            </div>`);
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
                that.choiceDiv.addClass("bg-primary").addClass("text-white").removeClass("unusedLCWF");
            }

            that.unuse = () => {
                that.used = false;
                that.choiceDiv.removeClass("bg-primary").removeClass("text-white").addClass("unusedLCWF");
            }

            that.init(answer_);

            return that;
        }

        var ChoiceWordFillContainer = (containerDiv_) => {
            var that = {};
            that.choiceContainers = [];

            that.init = (containerDiv_) => {
                that.choiceHoldingElements = $($(containerDiv_).find(".answerLCWF"));

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
        /*
        
        */

        var choiceDivCounter = 0;
        var ChoiceWordFillRows = $(".singleContentAnswerContainer");
        for (let i = 0; i < ChoiceWordFillRows.length; i++) {
            var ChoiceWordFillRow = $(ChoiceWordFillRows[i]);
            
            answers[i] = [];

            var choiceDivs = ChoiceWordFillRow.find(".choiceDiv");
            for ( let j = 0; j < choiceDivs.length; j++) {
                //var choiceDiv = choiceDivs[j];
                var cwfContainer = self.singleChoiceWordFillArray[choiceDivCounter];

                //pomieszałem obiekty choiceContainer z jquery znajdowaniem...
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