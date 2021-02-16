const TaskCreator = (data_) =>{

    /*  Variables */
    var self = data_;
    self.taskID; //do edycji
    self.taskContent = {};
    self.taskContent.tags = [];
    self.taskContent.difficulty = 100.0;
    self.taskContent.instruction = "ToDo instruction";

    /*  Logic functions */
    var taskCreatorInit = () => {
        /*TODO
            zmieńdiva na diva konkretnego wariantu
        
        */
        
    }

    self.checkIfTaskReady = () => {
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu:

        1. żadne pole nie jest puste
        2. od ostatniego zapisu zaszły zmiany
        2.1 task nie jest identyczny z innym taskiem z listy istniejącym???
        */
    }

    self.prepareTaskJsonFile = () => {
        
        /*TODO:
        spakuj gotowe zadanie do pliku*/
        return {};
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }
    //wysłanie nowego
    self.sendTaskVariant = (ajaxCallback, onSuccess, preparedTask) => {
        ajaxCallback(
            preparedTask,
            (data) => {
                console.log("sent");
                onSuccess(); // self.setupImportedTasksTable();
            }
        );
    }
    //TODO edycja starego 
    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, preparedTask) => {
        ajaxCallback(
            preparedTask,
            (data) => {
                console.log("edited");
                onSuccess(); // self.setupImportedTasksTable();
            }
        );
    }

    /*TODO delete (delete nie musze robić z tego poziomu bo nie ważne jaki rodzaj taska)*/
    // self.deleteTaskVariant = (ajaxCallback, onSuccess, preparedTask) => {
    //     ajaxCallback(
    //         preparedTask,
    //         (data) => {
    //             console.log("deleted");
    //             onSuccess(); // self.setupImportedTasksTable();
    //         }
    //     );
    // }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
    }

    self.loadTaskFrom = (taskObject) => {
        /*TODO:
        wczytanie zadania na ekraz z obiektu lub pliku json*/
        self.taskContent = taskObject.taskContent;
    }

    /*needed for task init*/
    self.hideAllTaskDivsExceptGiven = (taskName) => {
        var taskDivName = taskName + "Div";

        var taskDivs = $("#taskHolder").children();
        for (let i = 0; i < taskDivs.length; i++) {
            var taskDiv = $(taskDivs[i]);
            taskDiv.hide();
        }

        $("#"+taskDivName).show();
    }

    /*Could do:*/
    /*
    self.prepareTaskCreatorButtons = (taskName) => {
        //wszystkie przyciski zaczynające się na [btn]+[taskName]+*

        //klonować i podmienić...

        //ale jakie funkcje wstawić? z tego miejsca ich nie widzę
        
        //trzeba zrobić lokalnie dla pojedynczych tasków (zeby nie robić w init wszystkiego)?
    }
    */


    /*  Initialization */
    taskCreatorInit();
    return self;
}

/*TODO
będę musiał przerabiać textarea na content editable żeby miec możliwosć wstawiania html dla bardziej intuicyjnego edytowania treści taska

ALE contenteditable jest przestarzałe (a input level 2 jest jeszcze nie używany wszędzie?)
*/
const WordFillCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "WordFill";
    self.taskContent.content = {};
    self.taskContent.content.text = [];
    self.taskContent.content.emptySpaces = [];
    self.taskContent.content.startWithText;
    self.taskContent.content.possibleAnswers = [];

    /*  Logic functions */
    var wordFillCreatorInit = () => {
        console.log("wordFillCreatorInit");

        self.hideAllTaskDivsExceptGiven(self.taskName);

        if ( $("#wordFillAddIncorrectWord").length > 0) {
            var button = $("#wordFillAddIncorrectWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length )
                buttonClone.on('click', (e) => {
                    console.log("click")
                    self.addNewIncorrectWord();
                });
        }

        if ( $("#wordFillAddWord").length > 0) {
            var button = $("#wordFillAddWord"),
            buttonClone = button.clone();
            button.replaceWith( buttonClone );

            if ( buttonClone.length ) {
                buttonClone.on('click', (e) => {
                    console.log("click")
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
        if ($("#wordFillDificulty").length) {
            self.taskContent.difficulty = $("#wordFillDificulty").val();
        }
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
            var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",''));

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
            self.taskContent.content.text = [...(text).filter(t=>t!="")]

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
    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, preparedTask = self.prepareTaskJsonFile()) => {
        /*TODO
        potrzeba jeszcze taskID*/
        sendEditedTaskVariantSuper(ajaxCallback, onSuccess, preparedTask);
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
        $("wordFillDificulty").val(self.taskContent.difficulty);

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
    /*  Initialization */
    wordFillCreatorInit();
    return self;
}
/*TODO*/
const ChronologicalOrderCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "ChronologicalOrder";
    self.taskContent.sentences = [];

    /*  Logic functions */
    var chronologicalOrderCreatorInit = () => {
        console.log("chronologicalOrderCreatorInit");
        self.hideAllTaskDivsExceptGiven(self.taskName);
        
        /*pozbywam się even listenerów z przycisków przez klonowanie*/

        var buttonAddSentence = $("#chronologicalOrderAddSentence"),
        buttonAddSentenceClone = buttonAddSentence.clone();
        buttonAddSentence.replaceWith( buttonAddSentenceClone );

        if ( buttonAddSentenceClone.length )
            buttonAddSentenceClone.on('click', (e) => {
                console.log("click")
                self.addNewSentence();
            });

        /*teraz coś co nie będzie potrzebne jeśli na początku będzie zero zdań*/
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
            "taskName" : "ChronologicalOrder",
            "taskContent" : {
                "id" : "f10298c5-7e1f-4961-ab07-1fe9410bb433",
                "instruction" : "Put the phrases in order:",
                "tags" : [ ],
                "sentences" : [ "Try to understand the problem and define the purpose of the program.", 
                    "Once you have analysed the problem, define the successive logical steps of the program.", 
                    "Write the instructions in a high-level language of your choice.", 
                    "Once the code is written, test it to detect bugs or errors.", 
                    "Debug and fix errors in your code.", 
                    "Finally, review the program’s documentation." ],
                "difficulty" : 100.0
            }
        }*/

        //czy można pobrać difficulty
        if ($("#chronologicalOrderDificulty").length) {
            self.taskContent.difficulty = $("#chronologicalOrderDificulty").val();
        }
        //czy można pograc tagi
        if ($("#chronologicalOrderDivTaskTags").length) {
            var tagsString = $("#chronologicalOrderDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#chronologicalOrderDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#chronologicalOrderDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#chronologicalOrderSentences").length ) {
            self.taskContent.sentences = [];
            var sentenceTextareas = $("#chronologicalOrderSentences").find(".taskTextTextarea");
            for (let i = 0; i < sentenceTextareas.length; i++) {
                var sentenceTextarea = $(sentenceTextareas[i]);
                var sentence = sentenceTextarea.val().trim()
                if (sentence == "") //Jakoś poinformować o tym?
                    continue;

                self.taskContent.sentences.push(sentence);
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

    var loadTaskFromSuper = self.loadTaskFrom;
    self.loadTaskFrom = (taskObject) => {
        loadTaskFromSuper(taskObject);
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#chronologicalOrderDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + ", " + tag);
        }

        /*ustawiam insmtrukcje*/
        $("#chronologicalOrderDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var sentenceDiv = $("#chronologicalOrderSentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.sentences.length; i++) {
            var sentence = self.taskContent.sentences[i];

            self.createSentence((i+1),sentence)
        }

        /*ustawiam difficulty*/
        $("#chronologicalOrderDificulty").val(self.taskContent.difficulty);


        /*TODO ustawiam czcionkę*/
    }

    self.addNewSentence = () => {
        
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewSentence = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var sentences = $("#chronologicalOrderSentences").children();
            if (sentences.length > 0) {
                for (let i = 0; i < sentences.length; i++) {

                    //zakładam że w kolejności po id
                    var sentenceTextarea = $("#chronologicalOrderDivTaskText" + (i+1)); 
                    var sentenceTextareaText = sentenceTextarea.val(); 

                    if ( sentenceTextareaText == "") {
                        // nie wstawiaj bo znalazłem puste pole
                        return false;
                    }
                }
            } 
            return true;
        }
        
        if (doMakeNewSentence()) {
            //add new
            var sentenceDivindex = $("#chronologicalOrderSentences").children().length + 1;
            self.createSentence(sentenceDivindex);
        } else {
            //display a message about awaiting empty sentences
            $("#invalidConnectAddSentence").show();
            setTimeout(function(){$("#invalidConnectAddSentence").fadeOut()},5000);
        }
    }

    self.createSentence = (i=1, sentence="") => {
        
        var htmlString = `<div class="form-group blue-border-focus" id="chronologicalOrderSentence`+i+`">
                <label for="chronologicalOrderDivTaskText`+i+`">`+i+`</label>
                <textarea class="w-75 d-inline-block form-control taskTextTextarea"  id="chronologicalOrderDivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`">`+sentence+`</textarea>
                <button class="d-inline-block btn btn-danger btn-sm" id="btnChronologicalOrderRemoveSentence`+i+`">-</button>
            </div>`;

        var sentenceDiv = $("#chronologicalOrderSentences");
        var element = $(htmlString);
        sentenceDiv.append(element);
        
        $("#btnChronologicalOrderRemoveSentence"+i).on('click', (e) => {
            console.log("remove");
            self.removeSentence(i);
        });
    }

    self.removeSentence = (index) => {

        /*robione w podobne sposób jak w wordConnectCreator*/
        var sentences = $("#chronologicalOrderSentences").children();
        for ( let i = index; i < sentences.length; i++) {
            
            var currentSentence = $("#chronologicalOrderDivTaskText"+(i));

            var nextSentence = $("#chronologicalOrderDivTaskText"+(i+1));

            currentSentence.val(nextSentence.val())
        }

        $(`#chronologicalOrderSentence`+sentences.length).remove();
    }
    /*  Initialization */
    chronologicalOrderCreatorInit();
    return self;
}

/*TODO*/
const WordConnectCreator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreator(data_);
    /*  Variables */
    self.taskName = "WordConnect";
    self.taskContent.leftWords = [];
    self.taskContent.rightWords = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var wordConnectCreatorInit = () => {
        console.log("wordConnectCreatorInit");

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
        /*{
            "taskName" : "WordConnect",
            "taskContent" : {
                "id" : "2d210a77-329e-41c9-9fe2-ef7b0dd6918a",
                "instruction" : "Match the words with their translations:",
                "tags" : [ ],
                "leftWords" : [ "keynote", "to convey (information)", "to unveil (a theme)", "consistent", "stiff", "a knack (for sth)", "a flair", "intricate", "dazzling", "to rehearse" ],
                "rightWords" : [ "myśl przewodnia, główny motyw", "przekazywać/dostarczać (informacje)", "odkryć, ujawnić, odsłonić", "spójny, zgodny, konsekwentny", "sztywny, zdrętwiały", "talent, zręczność", "klasa, dar", "zawiły, misterny", "olśniewający", "próbować, przygotowywać się" ],
                "correctMapping" : {
                "0" : 0,
                "1" : 1,
                "2" : 2,
                "3" : 3,
                "4" : 4,
                "5" : 5,
                "6" : 6,
                "7" : 7,
                "8" : 8,
                "9" : 9
                },
                "difficulty" : 100.0
            }
        }*/
        
        //czy można pobrać difficulty
        if ($("#wordConnectDificulty").length) {
            self.taskContent.difficulty = $("#wordConnectDificulty").val();
        }
        //czy można pograc tagi
        if ($("#wordConnectDivTaskTags").length) {
            var tagsString = $("#wordConnectDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#wordConnectDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#wordConnectDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#wordConnectConnections").length ) {
            self.taskContent.leftWords = [];
            self.taskContent.rightWords = [];
            self.taskContent.correctMapping = {}
            var sentenceTextareas = $("#wordConnectConnections").find(".taskTextTextarea");
            var cmCounter = 0;
            for (let i = 0; i < sentenceTextareas.length; i+=2) {

                var leftTextarea = $(sentenceTextareas[i]);
                var rightTextarea = $(sentenceTextareas[i+1]);

                var leftWord = leftTextarea.val().trim()
                var rightWord = rightTextarea.val().trim()

                if (leftWord == "" || rightWord == "") //Jakoś poinformować o tym?
                    continue;

                self.taskContent.leftWords.push(leftWord);
                self.taskContent.rightWords.push(rightWord);

                self.taskContent.correctMapping[cmCounter] = cmCounter;
                cmCounter++
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

    var loadTaskFromSuper = self.loadTaskFrom;
    self.loadTaskFrom = (taskObject) => {
        loadTaskFromSuper(taskObject);
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#wordConnectDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + ", " + tag);
        }

        /*ustawiam insmtrukcje*/
        $("#wordConnectDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var connectionDiv = $("#wordConnectConnections");
        connectionDiv.empty();

        for (let i = 0; i < self.taskContent.leftWords.length; i++) {
            var leftWord = self.taskContent.leftWords[i];
            var rightWord = self.taskContent.rightWords[i];

            self.createConnection(i+1, leftWord, rightWord);

        }

        /*ustawiam difficulty*/
        $("#wordConnectDificulty").val(self.taskContent.difficulty);


        /*TODO ustawiam czcionkę*/
    }

    self.addNewConnection = () => {
        
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewConnection = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var connections = $("#wordConnectConnections").children();
            if (connections.length > 0) {
                for (let i = 0; i < connections.length; i++) {
                    //var connection = connections[i];

                    //zakładam że w kolejności po id
                    var connectionWordTextarea = $("#wordConnectDivTaskWord" + (i+1)); 
                    var connectionWordTextareaText = connectionWordTextarea.val(); 
                    var connectionDefinitionTextarea = $("#wordConnectDivTaskDefinition" + (i+1));
                    var connectionDefinitionTextareaText = connectionDefinitionTextarea.val(); 

                    
                    if ( connectionWordTextareaText == "" &&  connectionDefinitionTextareaText == "") {
                        // nie wstawiaj bo znalazłem puste pole
                        return false;
                    }
                }
            } 
            return true;
        }
        
        if (doMakeNewConnection()) {
            //add new
            var connectionDivindex = $("#wordConnectConnections").children().length + 1;
            self.createConnection(connectionDivindex);
        } else {
            //display a message about awaiting empty connections
            $("#invalidConnectAddConnection").show();
            setTimeout(function(){$("#invalidConnectAddConnection").fadeOut()},5000);

        }
    }

    self.createConnection = (i=1, left="", right="") => {
        var htmlString = `<div class="form-group blue-border-focus" id="wordConnectConnection`+i+`">
            <hr class="border border-primary">
            <label for="wordConnectConnectionDiv`+i+`">`+i+`</label>
            <div class="w-75 COC d-inline-block" id="wordConnectConnectionDiv`+i+`">
                <label for="wordConnectDivTaskWord`+i+`">Słowo:</label>
                <textarea class="form-control taskTextTextarea"  id="wordConnectDivTaskWord`+i+`" rows="2" placeholder="Wstaw słowo nr `+i+`:">`+left+`</textarea>

                <label for="wordConnectDivTaskDefinition`+i+`">Definicja:</label>
                <textarea class="form-control taskTextTextarea"  id="wordConnectDivTaskDefinition`+i+`" rows="2" placeholder="Wstaw definicje nr `+i+`:">`+right+`</textarea>
            </div>
            <button class="d-inline-block btn btn-danger btn-sm" id="btnWordConnectRemoveConnection`+i+`">-</button>
        </div>`;

        var connectionDiv = $("#wordConnectConnections");
        var element = $(htmlString);
        connectionDiv.append(element);
        
        $("#btnWordConnectRemoveConnection"+i).on('click', (e) => {
            self.removeConnection(i);
        });
    }

    self.removeConnection = (index) => {
        /*teraz problem bo indeksy nie są poustawiane

            ALBO ustawiać wszystkim nowe indeksy, podmieniać listenery na buttonach (1)
            ALBO zrobić jeden statyczny rosnący index dla listenerów, indeksy w label ustawiac zaleznie od kolejności (2)
            ALBO wszystkie dane z textarea z niżej przemieszczać o jeden do góry w pętli i usunąć ostatni element (3)
        */

        //(3)
        var connections = $("#wordConnectConnections").children();
        for ( let i = index; i < connections.length; i++) {
            
            var currentWord = $("#wordConnectDivTaskWord"+(i));
            var currentDef = $("#wordConnectDivTaskDefinition"+(i));

            var nextWord = $("#wordConnectDivTaskWord"+(i+1));
            var nextDef = $("#wordConnectDivTaskDefinition"+(i+1));

            currentWord.val(nextWord.val())
            currentDef.val(nextDef.val())
        }
        $(`#wordConnectConnection`+connections.length).remove();
    }
    /*       event listeners          */
    if ( $("#wordConnectAddConnection").length > 0) {

        var buttonAddConnection = $("#wordConnectAddConnection"),
        buttonAddConnectionClone = buttonAddConnection.clone();
        buttonAddConnection.replaceWith( buttonAddConnectionClone );
        buttonAddConnectionClone.on('click', (e) => {
            
            self.addNewConnection();
            console.log("click")
        });
    }


    /*  Initialization */
    wordConnectCreatorInit();
    return self;
}