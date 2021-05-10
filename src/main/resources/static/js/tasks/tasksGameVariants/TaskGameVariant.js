const TaskGameVariant = (taskData) => {
    var self = taskData;

    self.isTaskDone;/*used to remove eventListeners of specific tasks when they end*/

    self.taskVariantInit = (taskData) => {
        self.isTaskDone = false;
    }
  
    self.getAnswers = () => {
        var answers = [];//nie zawsze jest arrayem
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


//potrzebne tylko do tworzenia nowych wariantów, mock

// const TaskVariantDemo = (taskData) => {
//     var self = TaskGameVariant(taskData);
    
//     var taskVariantInitSuper = self.taskVariantInit;
//     self.taskVariantInit = (taskData) => {
//       taskVariantInitSuper(taskData);
  
//     }
  
//     var getAnswersSuper = self.getAnswers;
//     self.getAnswers = () => {
//       var answers = getAnswersSuper();
  
//       return answers;
//     }
  
//     var resetSuper = self.reset;
//     self.reset = () => {
//       resetSuper();
  
//       self.taskVariantInit(taskData);
//     }
  
//     self.taskVariantInit(taskData);
//     return self;
// }