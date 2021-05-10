/*TODO*/
const PictureToManyWords_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "PictureToManyWords";
    self.taskContent.text; //text
    self.taskContent.correctAnswers; //text array
    self.taskContent.incorrectAnswers = []; //text array

    /*  Logic functions */
    var PictureToManyWordsCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);
        /*
            "correctAnswers": ["wda1","dawdawd ad1"],
            "incorrectAnswers": ["wda2","dawdawd ad2"],
            "picture": "img",
            "text": "wda dawdawd ad",
        */
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
        if ($("#customRangePictureToManyWords").length > 0) {
            self.taskContent.difficulty = $("#customRangePictureToManyWords").val();
        }

        //czy można pograc tagi
        if ($("#PTMWDivTaskTags").length) {
            var tagsString = $("#PTMWDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#PTMWDivTaskInstruction").length )
            self.taskContent.instruction = $("#PTMWDivTaskInstruction").val().trim();
        
        //czy można pobrać treść
        if ( $("#PTMWDivTaskText").length )
            self.taskContent.content = $("#PTMWDivTaskText").val().trim();

        //czy można pobrać odpowiedź
        if ( $("#PTMWDivCorrectWord").length )
            self.taskContent.answer = $("#PTMWDivCorrectWord").val().trim();
        
        //czy można pobrać błędne odpowiedzi
        if ( $("#PTMWDivIncorrectWords").length ) {
            var incorrectAnswersString = $("#PTMWDivIncorrectWords").val();
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
        var tagsElem = $("#PTMWDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam instrukcje*/
        $("#PTMWDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var taskTextElem = $("#PTMWDivTaskText");
        taskTextElem.val(self.taskContent.text);

        /*ustawiam dodatkowe słowa*/
        var incorWordsElem = $("#PTMWDivIncorrectWords");
        incorWordsElem.val(self.taskContent.incorrectAnswers.join(", "));

        var incorWordsElem = $("#PTMWDivCorrectWords");
        incorWordsElem.val(self.taskContent.correctAnswers.join(", "));

        var incorWordsElem = $("#PTMWDivPicture");
        incorWordsElem.val(self.taskContent.picture);

        /*ustawiam difficulty*/
        $("#customRangePictureToManyWords").val(self.taskContent.difficulty);
        $("#customRangePictureToManyWords").trigger("change");
        $("#customRangeLabelPTMW").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

        /*TODO ustawiam czcionkę*/
    }
    /* listeners */
    if( $("#customRangePictureToManyWords").length > 0 ) {
        $("#customRangePictureToManyWords").on("input",() => {

            self.taskContent.difficulty = $("#customRangePictureToManyWords").val();
            $("#customRangeLabelPTMW").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    PictureToManyWordsCreatorInit();
    return self;
}