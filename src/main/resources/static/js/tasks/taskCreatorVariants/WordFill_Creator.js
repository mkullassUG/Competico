/*TODO
będę musiał przerabiać textarea na content editable żeby miec możliwosć wstawiania html dla bardziej intuicyjnego edytowania treści taska

ALE contenteditable jest przestarzałe (a input level 2 jest jeszcze nie używany wszędzie?)
*/
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
                
                buttonClone.mousedown(function(e) { // handle the mousedown event
                    e.preventDefault(); // prevent the textarea to loose focus!
                });    

                if ($("#" + self.taskName + "DivTaskText").length > 0) {
                    /*wyłaczanie przycisku jeśli nie mam focusa na textarea*/
                    
                    $("#" + self.taskName + "DivTaskText").on('blur', function(e) {
                        // your code here
                        buttonClone.attr('disabled','');
                    });

                    $("#" + self.taskName + "DivTaskText").on('focus', function(e) {
                        // your code here
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
        if ($("#customRange" + self.taskName + "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
        }
        // if ($("#" + self.taskName + "Dificulty").length) {
        //     self.taskContent.difficulty = $("#" + self.taskName + "Dificulty").val();
        // }

        //czy można pograc tagi
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
        //czy można pobrać instrukcje
        if ( $("#" + self.taskName + "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName + "DivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#" + self.taskName + "DivTaskText").length ) {
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

            var textString = $("#" + self.taskName + "DivTaskText").val();
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
            var inCorrectWordsString = $("#" + self.taskName + "DivIncorrectWords").val();
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

    //Deprecated, recommended sendTaskVariantToTasksets
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
        var incorWordsElem = $("#" + self.taskName + "DivIncorrectWords");
        incorWordsElem.val(possibleAnswersClone.join(", "));

        /*ustawiam difficulty*/
        $("#customRange" + self.taskName + "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName + "").trigger("change");

        $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("#" + self.taskName + "Dificulty").val(self.taskContent.difficulty);

        /*TODO ustawiam czcionkę*/
    }

    self.addNewIncorrectWord = ( ) => {
        $("#" + self.taskName + "DivIncorrectWords").focus();
    }

    self.addNewWord = (leftTag_, rightTag_ ) => {
        var yourTextarea = $("#" + self.taskName + "DivTaskText")[0];
        //pomogło
        //https://stackoverflow.com/questions/11076975/how-to-insert-text-into-the-textarea-at-the-current-cursor-position 
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
                /*sprawdzam jeszcze czy nie znajduje się czasem już wewnątrz takiego {[]}*/
                var partsL = leftSide.split(leftTag);
                for ( let i = 1 ; i < partsL.length; i++) {
                    var part = partsL[i];

                    if ( !part.includes(rightTag)) {
                        //oof zatrzymaj i wyświetl info, że tagi się nie zgadzają
                        $("#invalid" + self.taskName + "AddWord").show();
                        setTimeout(function(){$("#invalid" + self.taskName + "AddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        //oof zatrzymaj i wyświetl info, że tagi się nie zgadzają
                        $("#invalid" + self.taskName + "AddWord").show();
                        setTimeout(function(){$("#invalid" + self.taskName + "AddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + selText + rightTag) + rightSide;

                //umieszczam pozycje kursora pomiędzy {[]}
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