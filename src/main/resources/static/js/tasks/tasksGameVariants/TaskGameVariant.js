const TaskGameVariant = (taskData) => {
    var self = taskData;//czy tu nie będzie porblemu, że nadpisuje i nie użyje ponownie taska
  
    self.taskVariantInit = (taskData) => {
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
        console.log("ResizeObserverVariantFunction")
    }

    return self;
}


//potrzebne tylko do tworzenia nowych wariantów, mock

// const TaskVariantDemo = (taskData) => {
//     var self = TaskVariant(taskData);
    
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