const TaskGameVariant = (taskData) => {
    var self = taskData;

    self.isTaskDone;/*used to remove eventListeners of specific tasks when they end*/

    self.taskVariantInit = (taskData) => {
        self.isTaskDone = false;
    }
  
    self.getAnswers = () => {
        var answers = [];
        return answers;
    }
  
    self.reset = () => {
        self.answerCurrentlyAt = {};
        $("#GameDiv").html("");
    }

    self.ResizeObserverVariantFunction = () => {
    }

    return self;
}