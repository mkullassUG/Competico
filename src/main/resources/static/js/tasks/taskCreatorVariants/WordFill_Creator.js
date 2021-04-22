/*TODO
będę musiał przerabiać textarea na content editable żeby miec możliwosć wstawiania html dla bardziej intuicyjnego edytowania treści taska

ALE contenteditable jest przestarzałe (a input level 2 jest jeszcze nie używany wszędzie?)
*/
const WordFill_Creator = (data_ = {}) => {

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
    var wordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        if ( $("#wordFillAddIncorrectWord").length > 0) {
            var button = $("#wordFillAddIncorrectWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length )
                buttonClone.on('click', (e) => {
                    self.addNewIncorrectWord();
                });
        }

        if ( $("#wordFillAddWord").length > 0) {
            var button = $("#wordFillAddWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length ) {
                buttonClone.on('click', (e) => {
                    self.addNewWord("{[", "]}");
                });
                
                buttonClone.mousedown(function(e) { // handle the mousedown event
                    e.preventDefault(); // prevent the textarea to loose focus!
                });    

                if ($("#wordFillDivTaskText").length > 0) {
                    /*wyłaczanie przycisku jeśli nie mam focusa na textarea*/
                    
                    $("#wordFillDivTaskText").on('blur', function(e) {
                        // your code here
                        buttonClone.attr('disabled','');
                    });

                    $("#wordFillDivTaskText").on('focus', function(e) {
                        // your code here
                        buttonClone.removeAttr("disabled");
                    });
                }
            }
        }
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
        /*{
            "taskName" : "WordFill",
            "taskContent" : {
                "id" : "4e8f1bec-07ad-4a2e-a0d6-bf225ca91aa1",
                "instruction" : "Complete the text with the missing words:",
                "tags" : [ ],
                "content" : {
                "id" : "5b3c7a43-c0e6-41bc-8de2-27fcb2a10c0a",
                "text" : [ "I’m sorry to have to tell you that there has been some ", " in the project and we won’t be able to ", " our original ", " on July 30th for completing the ", " of the new software. Pedro’s absence for three weeks caused a bit of a ", ", and there were more delays when we realised that there was still some ", " in the databases that needed cleaning up. Still, I am confident that we can complete the project by the end of next month." ],
                "emptySpaces" : [ {
                    "answer" : "slippage"
                }, {
                    "answer" : "stick to"
                }, {
                    "answer" : "deadline"
                }, {
                    "answer" : "rollout"
                }, {
                    "answer" : "bottleneck"
                }, {
                    "answer" : "dirty data"
                } ],
                "startWithText" : true,
                "possibleAnswers" : [ "bottleneck", "deadline", "dirty data", "migrate", "rollout", "slippage", "stick to", "within", "scope" ]
                },
                "difficulty" : 100.0
            }
        }*/


        //czy można pobrać difficulty
        //slider
        if ($("#customRangeWordFill").length > 0) {
            self.taskContent.difficulty = $("#customRangeWordFill").val();
        }
        // if ($("#wordFillDificulty").length) {
        //     self.taskContent.difficulty = $("#wordFillDificulty").val();
        // }

        //czy można pograc tagi
        if ($("#wordFillDivTaskTags").length) {
            var tagsString = $("#wordFillDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#wordFillDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#wordFillDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#wordFillDivTaskText").length ) {
            /*
                self.taskContent.content.text = [];
                self.taskContent.content.emptySpaces = [];
                self.taskContent.content.startWithText;
                self.taskContent.content.possibleAnswers = [];
            */
            self.taskContent.content.text = [];
            self.taskContent.content.emptySpaces = [];
            self.taskContent.content.startWithText;
            self.taskContent.content.possibleAnswers = [];

            var textString = $("#wordFillDivTaskText").val();
            var tagsWithWords = textString.match(/\{\[[^]+?\]\}/g);
            if (tagsWithWords != null) //BUG 2021-02-08
                var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",''));
            else 
                var correctWords = [];
            //get emptySpaces
            for (let i = 0; i < correctWords.length; i++) {
                var correctWord = correctWords[i];
                
                var word = correctWord.trim()
                if (word == "") //Jakoś poinformować o tym?
                    continue;

                    self.taskContent.content.emptySpaces.push({
                        'answer':word
                    });
            }
            
            //get possibleAnswers
            var inCorrectWordsString = $("#wordFillDivIncorrectWords").val();
            var inCorrectWords = inCorrectWordsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.content.possibleAnswers = [...correctWords, ...inCorrectWords];
            
            //get text
            //build regex 
            var regexString = `\\{\\[`+correctWords[0]+ "\\]\\}";
            for ( let i = 1; i < correctWords.length; i++) {
                regexString += "|\\{\\[" +correctWords[i] + "\\]\\}";
            }
            regexString += "";
            regexString = new RegExp(regexString, "g");
            var text = textString.split(regexString);
            
            //get starts with text
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
        loadTaskFromSuper(taskObject); /*WordFill ma ten "content" jeszcze*/
        
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#wordFillDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + 
                (previousTags==""?"":", ") 
                + tag);
        }

        /*ustawiam insmtrukcje*/
        $("#wordFillDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var taskTextElem = $("#wordFillDivTaskText");
        taskTextElem.val("");
        var textString = "";
        var possibleAnswersClone = [...self.taskContent.content.possibleAnswers];

        var texts = self.taskContent.content.text;
        var answer;
        for (let i = 0; i < texts.length; i++) {
            var textPiece = texts[i];

            if (!(self.taskContent.content.startWithText && i == texts.length-1)) {
                answer = self.taskContent.content.emptySpaces[i].answer;

                //przy okazji pozbywam się poprawnych odpowedzi żeby później wstawić dodatkowe
                let wordRemoveIndex = possibleAnswersClone.indexOf(answer);
                possibleAnswersClone.splice(wordRemoveIndex,1);
            }

            if (self.taskContent.content.startWithText) {
                if ( i < texts.length-1) {
                    textString += textPiece
                    + `{[` + answer + `]}`;
                } else 
                    textString += textPiece; // ostatnia część
            } else {
                textString += `{[` + answer + `]}`
                    + textPiece;
            }
            
        }
        taskTextElem.val(textString);


        /*ustawiam dodatkowe słowa*/
        var incorWordsElem = $("#wordFillDivIncorrectWords");
        incorWordsElem.val("");
        var incorWordsString;
        for (let i = 0; i < possibleAnswersClone.length; i++) {
            var incorWord = possibleAnswersClone[i];

            incorWordsString = incorWordsElem.val();
            incorWordsElem.val(incorWordsString + 
                (incorWordsString==""?"":", ") 
                + incorWord);
        }

        /*ustawiam difficulty*/
        $("#customRangeWordFill").val(self.taskContent.difficulty);
        $("#customRangeLabelWordFill").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("#wordFillDificulty").val(self.taskContent.difficulty);

        /*TODO ustawiam czcionkę*/
    }

    self.addNewIncorrectWord = ( ) => {
        $("#wordFillDivIncorrectWords").focus();
    }

    self.addNewWord = (leftTag_, rightTag_ ) => {
        var yourTextarea = $("#wordFillDivTaskText")[0];
        //pomogło
        //https://stackoverflow.com/questions/11076975/how-to-insert-text-into-the-textarea-at-the-current-cursor-position 
        var insertAtCursor = (myField, leftTag, rightTag) => {
            //IE support
            if (document.selection) {
                myField.focus();
                sel = document.selection.createRange();
                sel.text = (leftTag + rightTag);
            }
            //MOZILLA and others
            else if (myField.selectionStart || myField.selectionStart == '0') {
                var startPos = myField.selectionStart;
                var endPos = myField.selectionEnd;

                var leftSide = myField.value.substring(0, startPos);
                var rightSide = myField.value.substring(endPos, myField.value.length);
                /*sprawdzam jeszcze czy nie znajduje się czasem już wewnątrz takiego {[]}*/
                var partsL = leftSide.split(leftTag);
                for ( let i = 1 ; i < partsL.length; i++) {
                    var part = partsL[i];

                    if ( !part.includes(rightTag)) {
                        //oof zatrzymaj iw yświetl info że tagi się nie zgadzają
                        $("#invalidWordFillAddWord").show();
                        setTimeout(function(){$("#invalidWordFillAddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        //oof zatrzymaj iw yświetl info że tagi się nie zgadzają
                        $("#invalidWordFillAddWord").show();
                        setTimeout(function(){$("#invalidWordFillAddWord").fadeOut()},5000);
                        return;
                    }
                }



                myField.value = leftSide + (leftTag + rightTag) + rightSide;

                // myField.selectionStart = startPos + myValue.length;
                // myField.selectionEnd = startPos + myValue.length;

                //umieszczam pozycje kursora pomiędzy {[]}
                myField.setSelectionRange(startPos+2,startPos+2);
            } else {
                myField.value += (leftTag + rightTag);
                myField.setSelectionRange(2,2);
            }
        }
        insertAtCursor(yourTextarea, leftTag_, rightTag_);
    }
    /* listeners */
    if( $("#customRangeWordFill").length > 0 ) {
        $("#customRangeWordFill").on("input",() => {

            self.taskContent.difficulty = $("#customRangeWordFill").val();
            $("#customRangeLabelWordFill").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    wordFillCreatorInit();
    return self;
}