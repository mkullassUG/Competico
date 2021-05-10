const SingleChoice_Game = (taskData) => {
    //podobne do wordfill

    var self = TaskGameVariant(taskData);
    
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
      
        self.content = taskData.content;
        self.possibleAnswers = taskData.possibleAnswers;
        console.log(self.content)
        console.log(self.possibleAnswers)


        //przygotowanie taskAnswerHolder
        var taskAnswerHolderReady = ``;
        for (let i = 0; i < self.possibleAnswers.length; i++) {
            taskAnswerHolderReady += 
            `<div class="answerSingle d-inline-block" id="answer`+i+`">
                ` + self.possibleAnswers[i] + `
            </div>`
        }

        $("#GameDiv").html(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>
        <div id="taskContent">`+ self.content +`</div>
        <h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>
        <div class="pb-2 mb-0 text-center" id="taskAnswerHolder">
            ` + taskAnswerHolderReady + `
        </div>`);
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