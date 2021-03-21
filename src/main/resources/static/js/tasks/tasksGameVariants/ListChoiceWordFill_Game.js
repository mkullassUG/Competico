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
            console.log("i: " + i)

            var textFieldRowArray = self.textFields[i];
            var wordChoicesRowArray = self.wordChoices[i];
            var startWithText = self.startWithText[i];
  
            var taskContentReady = "("+ (i+1) +"). ";

            if (startWithText) {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = textFieldRowArray[j];
                    console.log("j: " + j)
                    taskContentReady += text;
                    if (wordChoicesRowArray[j])
                        for (let k = 0; k < (wordChoicesRowArray[j].length); k++) {
                            var choiceWord = wordChoicesRowArray[j][k];
                            console.log("k: " + k)
                            taskContentReady += `<div class="choiceDiv">`
                            if ( k == 0 && wordChoicesRowArray[j].length > 1) {
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>` + "/";
                            } else if (k== wordChoicesRowArray[j].length-1 && wordChoicesRowArray[j].length > 1) {
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>`;
                            } else {
                                taskContentReady += "/" +`<div class="answerLCWF">`+ choiceWord +`</div>` + "/";
                            }
                            
                        }   
                    taskContentReady += `</div>`;
                }
            } else {
                for (let j = 0; j < textFieldRowArray.length; j++) {
                    var text = textFieldRowArray[j];

                    taskContentReady += `<div class="choiceDiv">`
                    if (wordChoicesRowArray[j])
                        for (let k = 0; k < wordChoicesRowArray[j].length; k++) {
                            var choiceWord = wordChoicesRowArray[j][k];
                            
                            taskContentReady += `<div class="choiceDiv">`
                            if ( k == 0 && wordChoicesRowArray[j].length > 1) {
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>` + "/";
                            } else if (k== wordChoicesRowArray[j].length-1 && wordChoicesRowArray[j].length > 1) {
                                taskContentReady += `<div class="answerLCWF">`+choiceWord+`</div>`;
                            } else {
                                taskContentReady += "/" +`<div class="answerLCWF">`+ choiceWord +`</div>` + "/";
                            }
                        }   
                    taskContentReady += `</div>` + text;
                }
            }

            $("#GameDiv").append(`
            <div class="singleContentAnswerContainer">
                <div class="border-top border-gray taskContent">`+ taskContentReady +`</div>
            </div>`)
            
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
                    console.log(that.selected)
                    if (!that.selected) {
                        console.log('click');
                        that.mainChoiceWordFillContainer.choiceWasSelected(that);
                    } else
                        that.unuse();
                })
            }

            that.use = () => {
                that.used = true;
                that.choiceDiv.addClass("bg-primary").addClass("text-white");
            }

            that.unuse = () => {
                that.used = false;
                that.choiceDiv.removeClass("bg-primary").removeClass("text-white");
            }

            that.init(answer_);

            return that;
        }

        var ChoiceWordFillContainer = (containerDiv_) => {
            var that = {};
            that.choiceContainers = [];

            that.init = (containerDiv_) => {
                that.choiceHoldingElements = $($(containerDiv_).find(".answerLCWF"));
                console.log(that.choiceContainers)

                for ( let i = 0; i < that.choiceHoldingElements.length; i++) {
                    var choice = that.choiceHoldingElements[i];
                    console.log(choice)
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
                console.log(that.choiceContainers)

                that.unuseAllChoices();
                choice.use();
            }

            that.init(containerDiv_);
            return that;
        }

        var containers = $(".singleContentAnswerContainer");
        for (let i = 0; i < containers.length; i++) {
            var containerDiv = containers[i];
            self.singleChoiceWordFillArray.push( ChoiceWordFillContainer(containerDiv) );
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];

        for (let i = 0; i < self.singleChoiceWordFillArray.length; i++) {
            var cwfContainer = self.singleChoiceWordFillArray[i];
            answers[i] = [];

            for ( let j = 0; j < cwfContainer.choiceContainers.length; j++) {
                var singleChoiceElement = cwfContainer.choiceContainers[j];

                //jak to ma działać, że więcej może być odpowiedzi dlatego w arrayu trzymam?
                var counter = 0;
                if (singleChoiceElement.used){
                    answers[i][counter] = singleChoiceElement.text;
                    counter++;
                }
            }
        }
        console.log(answers)
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