const WordFill_Creator = (data_ = {}, debug = false, $jq, myWindow, deps = {}, successfulCreationCallback) => {

    /* environment preparation */
    if ( deps.TaskCreatorVariant && typeof TaskCreatorVariant == "undefined")
        TaskCreatorVariant = deps.TaskCreatorVariant;
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);

    /*  Variables */
    self.taskName = "WordFill";
    self.taskContent.content = {};
    self.taskContent.content.text = [];
    self.taskContent.content.emptySpaces = [];
    self.taskContent.content.startWithText;
    self.taskContent.content.possibleAnswers = [];

    /*  Logic functions */
    var WordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        if ( $("#" + self.taskName + "AddIncorrectWord").length > 0) {
            var button = $("#" + self.taskName + "AddIncorrectWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length )
                buttonClone.on('click', (e) => {
                    self.addNewIncorrectWord();
                });
        }

        if ( $("#" + self.taskName + "AddWord").length > 0) {
            var button = $("#" + self.taskName + "AddWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length ) {
                buttonClone.on('click', (e) => {
                    self.addNewWord("{[", "]}");
                });
                
                buttonClone.mousedown(function(e) {
                    e.preventDefault();
                });    

                if ($("#" + self.taskName + "DivTaskText").length > 0) {
                    
                    $("#" + self.taskName + "DivTaskText").on('blur', function(e) {
                        buttonClone.attr('disabled','');
                    });

                    $("#" + self.taskName + "DivTaskText").on('focus', function(e) {
                        buttonClone.removeAttr("disabled");
                    });
                }
            }
        }

        if ( successfulCreationCallback )
            successfulCreationCallback(true);
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
        
        if ( $("#" + self.taskName + "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName + "DivTaskInstruction").val().trim();
        }   
        
        if ( $("#" + self.taskName + "DivTaskText").length ) {
            
            self.taskContent.content.text = [];
            self.taskContent.content.emptySpaces = [];
            self.taskContent.content.startWithText;
            self.taskContent.content.possibleAnswers = [];

            var textString = $("#" + self.taskName + "DivTaskText").val();
            var tagsWithWords = textString.match(/\{\[[^]+?\]\}/g);
            if (tagsWithWords != null)
                var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",''));
            else 
                var correctWords = [];

            for (let i = 0; i < correctWords.length; i++) {
                var correctWord = correctWords[i];
                
                var word = correctWord.trim()
                if (word == "")//could inform user about this?
                    continue;

                    self.taskContent.content.emptySpaces.push({
                        'answer':word
                    });
            }

            var inCorrectWordsString = $("#" + self.taskName + "DivIncorrectWords").val();
            var inCorrectWords = inCorrectWordsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.content.possibleAnswers = [...correctWords, ...inCorrectWords];
            
            var regexString = `\\{\\[`+correctWords[0]+ "\\]\\}";
            for ( let i = 1; i < correctWords.length; i++) {
                regexString += "|\\{\\[" +correctWords[i] + "\\]\\}";
            }
            regexString += "";
            regexString = new RegExp(regexString, "g");
            var text = textString.split(regexString);
            
            self.taskContent.content.startWithText = text[0] != "";
            self.taskContent.content.text = [...(text).filter((t,index)=>!(t==""&&index==0))]

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
        loadTaskFromSuper(taskObject); /*" + self.taskName + " ma ten "content" jeszcze*/
        
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#" + self.taskName + "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam insmtrukcje*/
        $("#" + self.taskName + "DivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var taskTextElem = $("#" + self.taskName + "DivTaskText");
        taskTextElem.val("");
        var textString = "";
        var possibleAnswersClone = [...self.taskContent.content.possibleAnswers];

        var texts = self.taskContent.content.text;
        var answer;
        for (let i = 0; i < texts.length; i++) {
            var textPiece = texts[i];

            if (!(self.taskContent.content.startWithText && i == texts.length-1)) {
                answer = self.taskContent.content.emptySpaces[i].answer;

                let wordRemoveIndex = possibleAnswersClone.indexOf(answer);
                possibleAnswersClone.splice(wordRemoveIndex,1);
            }

            if (self.taskContent.content.startWithText) {
                if ( i < texts.length-1) {
                    textString += textPiece
                    + `{[` + answer + `]}`;
                } else 
                    textString += textPiece;
            } else {
                textString += `{[` + answer + `]}`
                    + textPiece;
            }
            
        }
        taskTextElem.val(textString);


        /*ustawiam dodatkowe słowa*/
        var incorWordsElem = $("#" + self.taskName + "DivIncorrectWords");
        incorWordsElem.val(possibleAnswersClone.join(", "));

        /*ustawiam difficulty*/
        $("#customRange" + self.taskName + "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName + "").trigger("change");

        $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
    }

    self.addNewIncorrectWord = ( ) => {
        $("#" + self.taskName + "DivIncorrectWords").focus();
    }

    self.addNewWord = (leftTag_, rightTag_ ) => {

        var yourTextarea = $("#" + self.taskName + "DivTaskText")[0];
        var insertAtCursor = (myField, leftTag, rightTag) => {

            var selText = window.getSelection().toString();
            selText = selText.replaceAll(leftTag,"").replaceAll(rightTag,"");
            //IE support
            if (window.document.selection) {
                myField.focus();
                sel = window.document.selection.createRange();
                sel.text = (leftTag + selText + rightTag);
            }
            //MOZILLA and others
            else if (myField.selectionStart || myField.selectionStart == '0') {
                var startPos = myField.selectionStart;
                var endPos = myField.selectionEnd;

                var leftSide = myField.value.substring(0, startPos);
                var rightSide = myField.value.substring(endPos, myField.value.length);
                var partsL = leftSide.split(leftTag);
                for ( let i = 1 ; i < partsL.length; i++) {
                    var part = partsL[i];

                    if ( !part.includes(rightTag)) {
                        $("#invalid" + self.taskName + "AddWord").show();
                        setTimeout(function(){$("#invalid" + self.taskName + "AddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        $("#invalid" + self.taskName + "AddWord").show();
                        setTimeout(function(){$("#invalid" + self.taskName + "AddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + selText + rightTag) + rightSide;

                myField.setSelectionRange(startPos+2+selText.length,startPos+2+ selText.length);
            } else {
                myField.value += (leftTag + selText + rightTag);
                myField.setSelectionRange(2 + selText.length,2+ selText.length);
            }
        }
        insertAtCursor(yourTextarea, leftTag_, rightTag_);
    }
    /* listeners */
    if( $("#customRange" + self.taskName + "").length > 0 ) {
        $("#customRange" + self.taskName + "").on("input",() => {

            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
            $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    self.keyCombos = (e) =>{
        let evtobj = window.event ? event : e;
        //key combo for ctrl+b
        if (evtobj.keyCode == 66 && evtobj.ctrlKey) {
            e.preventDefault();
            self.addNewWord("{[", "]}");
        }
    }
    $("#" + self.taskName + "DivTaskText").keydown( self.keyCombos );

    
    /*  Initialization */
    WordFillCreatorInit();
    return self;
}

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {WordFill_Creator};