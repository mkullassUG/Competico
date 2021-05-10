const SentenceToManyPictures_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "SentenceToManyPictures";

    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
      
        self.pictures = taskData.pictures;
        self.sentence = taskData.sentence;
        console.log(self.pictures)
        console.log(self.sentence)
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