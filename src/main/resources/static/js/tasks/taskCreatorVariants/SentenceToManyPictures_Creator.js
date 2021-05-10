/*TODO*/
const SentenceToManyPictures_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "SentenceToManyPictures";
    self.taskContent.text; //text
    self.taskContent.correctAnswers  = []; //text array
    self.taskContent.incorrectAnswers = []; //text array

    /*  Logic functions */
    var SentenceToManyPicturesCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        /*
            "sentence": "wda dawdawd ad",
            "correctAnswers": ["img1", "img2"],
            "incorrectAnswers": ["img3", "img4"],
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
        if ($("#customRangeSentenceToManyPictures").length > 0) {
            self.taskContent.difficulty = $("#customRangeSentenceToManyPictures").val();
        }

        //czy można pograc tagi
        if ($("#STMPDivTaskTags").length) {
            var tagsString = $("#STMPDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#STMPDivTaskInstruction").length )
            self.taskContent.instruction = $("#STMPDivTaskInstruction").val().trim();
        
        //czy można pobrać treść
        if ( $("#STMPDivTaskText").length )
            self.taskContent.text = $("#STMPDivTaskText").val().trim();
        
        //czy można pobrać błędne odpowiedzi
        if ( $("#STMPDivIncorrectPictures").length ) {
            var incorrectAnswersString = $("#STMPDivIncorrectPictures").val();
            var incorrectAnswers = incorrectAnswersString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.incorrectAnswers = [];
            for (let i = 0; i < incorrectAnswers.length; i++) {
                self.taskContent.incorrectAnswers.push(incorrectAnswers[i]);
            }
        }

        //czy można poprawne błędne odpowiedzi
        if ( $("#STMPDivCorrectPictures").length ) {
            var correctAnswersString = $("#STMPDivCorrectPictures").val();
            var correctAnswers = correctAnswersString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.correctAnswers = [];
            for (let i = 0; i < correctAnswers.length; i++) {
                self.taskContent.correctAnswers.push(correctAnswers[i]);
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
        var tagsElem = $("#STMPDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam instrukcje*/
        $("#STMPDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var taskTextElem = $("#STMPDivTaskText");
        taskTextElem.val(self.taskContent.sentence);

        /*ustawiam dodatkowe słowa*/
        var incorWordsElem = $("#STMPDivIncorrectPictures");
        incorWordsElem.val(self.taskContent.incorrectAnswers.join(", "));

        var incorWordsElem = $("#STMPDivCorrectPictures");
        incorWordsElem.val(self.taskContent.correctAnswers.join(", "));

        /*ustawiam difficulty*/
        $("#customRangeSentenceToManyPictures").val(self.taskContent.difficulty);
        $("#customRangeSentenceToManyPictures").trigger("change");
        $("#customRangeLabelSTMP").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

        /*TODO ustawiam czcionkę*/
    }

    /* listeners */
    if( $("#customRangeSentenceToManyPictures").length > 0 ) {
        $("#customRangeSentenceToManyPictures").on("input",() => {

            self.taskContent.difficulty = $("#customRangeSentenceToManyPictures").val();
            $("#customRangeLabelSTMP").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    SentenceToManyPicturesCreatorInit();
    return self;
}