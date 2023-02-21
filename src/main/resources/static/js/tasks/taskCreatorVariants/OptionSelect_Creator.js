const OptionSelect_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "OptionSelect";
    self.taskContent.content = {};
    self.taskContent.content.correctAnswers = [];
    self.taskContent.content.incorrectAnswers = []; 

    /*  Logic functions */
    var OptionSelectCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

    }

    var checkIfTaskReadySuper = self.checkIfTaskReady;
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile;
    self.prepareTaskJsonFile = () => {
        var task = prepareTaskJsonFileSuper();

        if ($("#customRange" + self.taskName + "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
        }

        if ($("#" + self.taskName + "DivTaskTags").length) {
            var tagsString = $("#" + self.taskName + "DivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }

        if ( $("#" + self.taskName + "DivTaskInstruction").length )
            self.taskContent.instruction = $("#" + self.taskName + "DivTaskInstruction").val().trim();
        
        self.taskContent.content = {};
        if ( $("#" + self.taskName + "DivTaskText").length )
            self.taskContent.content.content = $("#" + self.taskName + "DivTaskText").val().trim();

        if ( $("#" + self.taskName + "DivCorrectWords").length )

            var correctAnswersString = $("#" + self.taskName + "DivCorrectWords").val();
            var correctAnswers = correctAnswersString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.content.correctAnswers = [];
            for (let i = 0; i < correctAnswers.length; i++) {
                self.taskContent.content.correctAnswers.push(correctAnswers[i]);
            }
        
        if ( $("#" + self.taskName + "DivIncorrectWords").length ) {
            var incorrectAnswersString = $("#" + self.taskName + "DivIncorrectWords").val();
            var incorrectAnswers = incorrectAnswersString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.content.incorrectAnswers = [];
            for (let i = 0; i < incorrectAnswers.length; i++) {
                self.taskContent.content.incorrectAnswers.push(incorrectAnswers[i]);
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;

        return task;
    }

    var setupDemoFromCurrentSuper = self.setupDemoFromCurrent;
    self.setupDemoFromCurrent = () => {
        setupDemoFromCurrentSuper();
    }

    var sendTaskVariantSuper = self.sendTaskVariant;
    self.sendTaskVariant = (ajaxCallback, onSuccess, preparedTask = self.prepareTaskJsonFile()) => {

        sendTaskVariantSuper(ajaxCallback, onSuccess, preparedTask);
    }

    var sendEditedTaskVariantSuper = self.sendEditedTaskVariant;
    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, taskID, preparedTask = self.prepareTaskJsonFile()) => {
        
        sendEditedTaskVariantSuper(ajaxCallback, onSuccess, taskID, preparedTask);
    }

    var loadTaskFromSuper = self.loadTaskFrom;
    self.loadTaskFrom = (taskObject) => {
        loadTaskFromSuper(taskObject);
        
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        var tagsElem = $("#" + self.taskName + "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#" + self.taskName + "DivTaskInstruction").val(self.taskContent.instruction);

        var taskTextElem = $("#" + self.taskName + "DivTaskText");
        taskTextElem.val(self.taskContent.content.content);

        var incorWordsElem = $("#" + self.taskName + "DivIncorrectWords");
        incorWordsElem.val(self.taskContent.content.incorrectAnswers.join(", "));

        var incorWordsElem = $("#" + self.taskName + "DivCorrectWords");
        incorWordsElem.val(self.taskContent.content.correctAnswers.join(", "));

        $("#customRange" + self.taskName + "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName + "").trigger("change");
        $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

    }
    
    /* listeners */
    if( $("#customRange" + self.taskName + "").length > 0 ) {
        $("#customRange" + self.taskName + "").on("input",() => {
            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
            $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    OptionSelectCreatorInit();
    return self;
}