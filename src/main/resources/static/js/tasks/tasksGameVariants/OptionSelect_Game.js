const OptionSelect_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "OptionSelect";

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
  
        self.content = taskData.content;
        self.possibleAnswers = taskData.possibleAnswers;
        console.log(self.content)
        console.log(self.possibleAnswers)

        //przygotowanie taskAnswerHolder
        var taskAnswerHolderReady = $(`<div class="pb-2 mb-0 text-center" id="taskAnswerHolder">`);

        $("#GameDiv").append(``);
        for (let i = 0; i < self.possibleAnswers.length; i++) {
            var possibleAnswersNode = document.createTextNode(self.possibleAnswers[i]);

            var answerVariantNode = $(`<div class="optionSelectAnswer d-inline-block" id="answer`+i+`">`);
            answerVariantNode.append(possibleAnswersNode);
            taskAnswerHolderReady.append(answerVariantNode);
        }
        var taskContentNode = $(`<div id="taskContent">`);

        $("#GameDiv").append(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Content: </h6>`);
        taskContentNode.append(document.createTextNode(self.content));
        $("#GameDiv").append(taskContentNode);
        $("#GameDiv").append(`<h6 class="border-bottom border-gray pb-2 mb-0 text-dark"> Answers: </h6>`);
        $("#GameDiv").append(taskAnswerHolderReady);
        
        var options = $("#taskAnswerHolder").children();
        for ( let i = 0; i < options.length; i++) {
            var option = $(options[i]);

            option.on('click',(e) => {
                var option =  $(e.target);
                option.toggleClass("selectedOption");
            });
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        
        var options = $("#taskAnswerHolder").children();

        for ( let i = 0; i < options.length; i++) {
            var option = $(options[i]);

            if ( option.hasClass("selectedOption"))
                answers.push(option.html());
        }

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