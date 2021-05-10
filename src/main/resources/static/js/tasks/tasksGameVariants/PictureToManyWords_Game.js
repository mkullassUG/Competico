const PictureToManyWords_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "PictureToManyWords";

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
      
        self.pictures = taskData.picture;
        self.words = taskData.words;
        console.log(self.pictures)
        console.log(self.words)
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