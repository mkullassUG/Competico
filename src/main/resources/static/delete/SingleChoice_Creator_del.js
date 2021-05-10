/*TODO*/
const SingleChoice_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "SingleChoice";
    self.taskContent.content; //text
    self.taskContent.answer;//text
    self.taskContent.incorrectAnswers = []; //text array

    /*  Logic functions */
    var singleChoiceCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

    }

    var checkIfTaskReadySuper = self.checkIfTaskReady;
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile;
    self.prepareTaskJsonFile = () => {
        var task = prepareTaskJsonFileSuper();

        //czy można pobrać difficulty
        //slider
        if ($("#customRangeSC").length > 0) {
            self.taskContent.difficulty = $("#customRangeSC").val();
        }

        //czy można pograc tagi
        if ($("#SCDivTaskTags").length) {
            var tagsString = $("#SCDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#SCDivTaskInstruction").length )
            self.taskContent.instruction = $("#SCDivTaskInstruction").val().trim();
        
        //czy można pobrać treść
        if ( $("#SCDivTaskText").length )
            self.taskContent.content = $("#SCDivTaskText").val().trim();

        //czy można pobrać odpowiedź
        if ( $("#SCDivCorrectWord").length )
            self.taskContent.answer = $("#SCDivCorrectWord").val().trim();
        
        //czy można pobrać błędne odpowiedzi
        if ( $("#SCDivIncorrectWords").length ) {
            var incorrectAnswersString = $("#SCDivIncorrectWords").val();
            var incorrectAnswers = incorrectAnswersString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.incorrectAnswers = [];
            for (let i = 0; i < incorrectAnswers.length; i++) {
                self.taskContent.incorrectAnswers.push(incorrectAnswers[i]);
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;

        return task;
    }

    var setupDemoFromCurrentSuper = self.setupDemoFromCurrent;
    self.setupDemoFromCurrent = () => {
        setupDemoFromCurrentSuper();
        /*TODO:
        podgląd stworzonego zadania jako gry*/
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
        
        /*ustawiam tagi*/
        var tagsElem = $("#SCDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam instrukcje*/
        $("#SCDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var taskTextElem = $("#SCDivTaskText");
        taskTextElem.val(self.taskContent.content);

        /*ustawiam dodatkowe słowa*/
        var incorWordsElem = $("#SCDivIncorrectWords");
        incorWordsElem.val(self.taskContent.incorrectAnswers.join(", "));

        var incorWordsElem = $("#SCDivCorrectWord");
        incorWordsElem.val(self.taskContent.answer);

        /*ustawiam difficulty*/
        $("#customRangeSC").val(self.taskContent.difficulty);
        $("#customRangeLabelSC").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

        /*TODO ustawiam czcionkę*/
    }
    /* listeners */
    if( $("#customRangeSC").length > 0 ) {
        $("#customRangeSC").on("input",() => {

            self.taskContent.difficulty = $("#customRangeSC").val();
            $("#customRangeLabelSC").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    singleChoiceCreatorInit();
    return self;
}