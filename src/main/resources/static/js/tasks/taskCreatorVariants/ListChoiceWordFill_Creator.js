const ListChoiceWordFill_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListChoiceWordFill";
    self.taskContent = {};
    self.taskContent.rows = [];

    /*  Logic functions */
    var wordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        var btnAddSingleChoiceWordFill = $("#ListChoiceWordFillAddWordFill");
        btnAddSingleChoiceWordFill.on('click',(e)=> {
            var createdElement = self.addSingleChoiceWordFill();

            //add new and focus
            //podobnie jak w CO i LSF tylko w inym miejscu, powinienem zrobić inaczej
            if (createdElement) {
                createdElement.find("textarea").focus()
            }

            //scroll to bottom of the div after adding new
            function gotoBottom(id){
                var element = document.getElementById(id);
                element.scrollTop = element.scrollHeight - element.clientHeight;
            }
            gotoBottom(self.taskName+"Div");

        });

        btnAddSingleChoiceWordFill.click();
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
        if ($("#customRangeLCW").length > 0) {
            self.taskContent.difficulty = $("#customRangeLCW").val();
        }
        // if ($("#ListChoiceWordFillDificulty").length) {
        //     self.taskContent.difficulty = $("#ListChoiceWordFillDificulty").val();
        // }
        //czy można pograc tagi
        if ($("#ListChoiceWordFillDivTaskTags").length) {
            var tagsString = $("#ListChoiceWordFillDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#ListChoiceWordFillDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#ListChoiceWordFillDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        var singleChoiceWordFills = $("#ChoiceWordFills").find(".singleChoiceWordFillDiv");
        if ( singleChoiceWordFills.length ) {
             
            self.taskContent.rows = [];

            for ( let i = 0; i < singleChoiceWordFills.length; i++) {
                //dla pojedynczego WordFilla poberać, potem do row wsadzać
                var currentRow = {};
                currentRow.text = [];
                currentRow.wordChoices = [];
                currentRow.startWithText;

                var singleChoiceWordFill = $(singleChoiceWordFills[i]);

                var textString = singleChoiceWordFill.find(".LCWFTextArea").val();
                var tagsWithWords = textString.match(/\{\[[^]+?\]\}/g);
                if (tagsWithWords != null){ //BUG 2021-02-08
                    var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",'').split("][")[0]);
                    var incorrectWordsArray = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",'').split("][")[1].split(","));
                } else {
                    var correctWords = [];
                    var incorrectWordsArray = [];
                }

                //get text
                //get correct and incorrect words
                //build regex 
                if ( !incorrectWordsArray.length ) {
                    
                    console.warn("W jednym z wierszy nie wstawiono opcji wyboru!");
                    alert("W jednym z wierszy nie wstawiono opcji wyboru!");
                    return;
                }

                var regexString = `\\{\\[`+correctWords[0]+ `\\]\\[`+ incorrectWordsArray[0].join(",")  +`\\]\\}`;
                for ( let i = 0; i < tagsWithWords.length; i++) {
                    var correctWord = correctWords[i];
                    var incorrectWords = incorrectWordsArray[i];

                    currentRow.wordChoices.push({
                        "correctAnswer" : correctWord,
                        "incorrectAnswers" : incorrectWords
                    });

                    if ( i > 0)
                        regexString += `|\\{\\[`+correctWords[i]+ `\\]\\[`+ incorrectWordsArray[i].join(",")  +`\\]\\}`
                }

                regexString = new RegExp(regexString, "g");
                var text = textString.split(regexString);

                //get starts with text
                currentRow.startWithText = text[0] != "";
                //tylko dla pierwszego, żeby było startsWithText
                currentRow.text = [...(text).filter((t,index)=>!(t==""&&index==0))]

                self.taskContent.rows[i] = currentRow;
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;

        //BUG fixed, przez id nie przyjmowało, ale nei wiem skąd id O.o? ale to było w ListWordFill
        // if (task.taskContent.id)
        //     delete task.taskContent.id;

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
        var tagsElem = $("#ListChoiceWordFillDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + 
                (previousTags==""?"":", ") 
                + tag);
        }

        /*ustawiam instrukcje*/
        $("#ListChoiceWordFillDivTaskInstruction").val(self.taskContent.instruction);


        $("#ChoiceWordFills").html(``);
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];

            var id = row.id;
            //var text = [...row.text];
            var wordChoices = [...row.wordChoices];
            /*wordChoices to array z obiektami:
                id:
                correctAnswer: "",
                incorrectAnswers: ["",...]
            */

            var wordChoicesSpecialWrapperArray = wordChoices.map(wcs => {

                var specialWrapper = "{[" + 
                wcs.correctAnswer +
                "][" +
                wcs.incorrectAnswers.join(",") +
                "]}";

                return specialWrapper;
            });

            var textWithChoices = "";
            if ( row.startWithText) {
                textWithChoices = row.text[0];
                for ( let i = 0; i < wordChoicesSpecialWrapperArray.length; i++) {
                    textWithChoices +=  wordChoicesSpecialWrapperArray[i] + row.text[i+1];
                }
            } else {
                for ( let i = 0; i < wordChoicesSpecialWrapperArray.length; i++) {
                    textWithChoices += wordChoicesSpecialWrapperArray[i] + row.text[i];
                }
            }


            var ChoiceWordFillElement = $(`
            <div class="form-group blue-border-focus singleChoiceWordFillDiv">
                <hr class="border border-primary">
                <div class="form-inline">
                    <button class="m-auto btn btn-primary btn-sm LCWFAddWord" id="LCWFAddWord`+i+`">Wstaw wybór odpowiedzi</button>
                    <div class="invalid-feedback invalidLCWFAddWord" id="invalidLCWFAddWord`+i+`">
                    Nie zgadza się umiejscowienie tagów {[]}.
                    </div>
                </div>
                <label class="LCWFDivTaskText">`+(i+1)+`</label>
                <div class="align-middle w-75 d-inline-block LCWFTextBlock">
                    <div class="form-label-group">
                        <textarea class="form-control taskTextTextarea LCWFTextArea"  id="LCWFDivTaskTextArea`+i+`" placeholder="cos" rows="4" >`+textWithChoices+`</textarea>
                        <label for="LCWFDivTaskTextArea">Treść</label>
                    </div>
                </div>
                <button class="align-middle d-inline-block btn btn-danger btn-sm LCWFDeleteButton" id="btnLCWFRemoveWordFill`+i+`">-</button>
            </div>`);

            $("#ChoiceWordFills").append(ChoiceWordFillElement);
        }

        self.setupListenersAndIndexesFromPosition(0);

        /*ustawiam difficulty*/
        $("#customRangeLCW").val(self.taskContent.difficulty);
        $("#customRangeLabelLCW").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("ListChoiceWordFillDificulty").val(self.taskContent.difficulty);

        
        /*TODO ustawiam czcionkę*/
    }

    self.addSingleChoiceWordFill = () => {

        //check if treść of other singleChoiceWordFills are empty
        var foundEmpty = false;
        var singleChoiceWordFills = $("#ChoiceWordFills").find(".singleChoiceWordFillDiv");
        for ( let i = 0; i < singleChoiceWordFills.length; i++) {
            var singleChoiceWordFill = $(singleChoiceWordFills[i]);
            //.trim()
            //.replaceAll(/\s/g,'')
            if ( singleChoiceWordFill.find(".taskTextTextarea").val().trim() === ""){
                foundEmpty = true;
                break;
            }
        }
        if (foundEmpty) {
            //wyświetl info i zakończ
            $("#invalidChoiceFillAddFill").show();
            setTimeout(function(){$("#invalidChoiceFillAddFill").fadeOut()},5000);
            return
        }

        var index = singleChoiceWordFills.length;

        var ChoiceWordFillElement = $(`
            <div class="form-group blue-border-focus singleChoiceWordFillDiv">
                <hr class="border border-primary">
                <div class="form-inline">
                    <button class="m-auto btn btn-primary btn-sm LCWFAddWord" id="LCWFAddWord`+index+`">Wstaw wybór odpowiedzi</button>
                    <div class="invalid-feedback invalidLCWFAddWord" id="invalidLCWFAddWord`+index+`">
                    Nie zgadza się umiejscowienie tagów {[]}.
                    </div>
                </div>
                <label class="LCWFDivTaskText">`+(index+1)+`</label>
                <div class="align-middle w-75 d-inline-block LCWFTextBlock">
                    <div class="form-label-group">
                        <textarea class="form-control taskTextTextarea LCWFTextArea"  id="LCWFDivTaskTextArea`+index+`" placeholder="cos" rows="4" ></textarea>
                        <label for="LCWFDivTaskTextArea">Treść</label>
                    </div>
                </div>
                <button class="align-middle d-inline-block btn btn-danger btn-sm LCWFDeleteButton" id="btnLCWFRemoveWordFill`+index+`">-</button>
            </div>`);

        $("#ChoiceWordFills").append(ChoiceWordFillElement);
        self.setupListenersAndIndexesFromPosition(index);

        return ChoiceWordFillElement;
    }

    self.setupListenersAndIndexesFromPosition = (elemPosition) => {

        
        var ChildrenAddWord = $(".LCWFAddWord");
        var ChildrenTextArea = $(".LCWFTextArea");

        for (let i = elemPosition; i < ChildrenAddWord.length; i ++) {
            var indexLabel = $($(".LCWFDivTaskText")[i]);
            indexLabel.text(i+1);

            var currentChildAddWord = $(ChildrenAddWord[i]);
            var currentChildTextArea = $(ChildrenTextArea[i]);


            if ( currentChildAddWord.length > 0) {

                var button = currentChildAddWord,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".form-group");
                        
                        //TODO:
                        self.addNewWordForChoiceWordFill("{[", "][", "]}", parentElem);
                    });
                    
                    buttonClone.mousedown(function(e) { // handle the mousedown event
                        e.preventDefault(); // prevent the textarea to loose focus!
                    });    
    
                    if (currentChildTextArea.length > 0) {
                        /*wyłaczanie przycisku jeśli nie mam focusa na textarea*/
                        
                        currentChildTextArea.on('blur', function(e) {
                            // your code here
                            var parentElem = $(e.target).closest(".form-group");
                            //.LWFAddIncorrectWord
                            parentElem.find(".LWFAddWord").attr('disabled','');
                        });
    
                        currentChildTextArea.on('focus', function(e) {
                            // your code here
                            var parentElem = $(e.target).closest(".form-group");

                            parentElem.find(".LWFAddWord").removeAttr("disabled");
                        });
                    }
                }
            }

            var btnRemove = $("#btnLCWFRemoveWordFill"+i);
            if ( btnRemove.length > 0 ) {
                var button = btnRemove,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".singleChoiceWordFillDiv");
                        self.deleteSingleChoiceWordFillParent(parentElem);
                    });
                }
            }
        }
    }

    self.deleteSingleChoiceWordFillParent = (parentElem) => {
        //ogarnąc który z kolei jest to element i delete, wszystkie wyżej elementy przesunać i pozmieniać listenery.
        //array.index(parentElem)
        var WordFillsDiv = parentElem.closest("#ChoiceWordFills");
        var allSingleChoiceWordFills = WordFillsDiv.find(".singleChoiceWordFillDiv");

        var currentAtPosition = allSingleChoiceWordFills.index(parentElem);

        parentElem.remove();

        self.setupListenersAndIndexesFromPosition(currentAtPosition);
    }

    //TODO: przerobić to na Choice czyli {[dobra_odpowiedz][zła1,zła2,zła3]}

    self.addNewWordForChoiceWordFill = (leftTag_, middleTag_, rightTag_, forElement) => {
        var yourTextarea = forElement.find(".LCWFTextArea")[0];
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
                console.log(leftSide);
                console.log(rightSide);
                var partsL = leftSide.split(leftTag);
                for ( let i = 1 ; i < partsL.length; i++) {
                    var part = partsL[i];

                    if ( !part.includes(rightTag)) {
                        //BUG, nie pozwala umieszczać za tagami następnych
                        console.log("1");

                        forElement.find(".invalidLCWFAddWord").show();
                        setTimeout(function(){forElement.find(".invalidLCWFAddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        //BUG 2, pozwala umieszczać wewnatrz tagu jaśli jest tam jakiś tekst
                        console.log("2");
                        forElement.find(".invalidLCWFAddWord").show();
                        setTimeout(function(){forElement.find(".invalidLCWFAddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + middleTag_+ rightTag) + rightSide;

                //umieszczam pozycje kursora pomiędzy {[ ... ][]]}
                myField.setSelectionRange(startPos+2,startPos+2);
            } else {
                myField.value += (leftTag + rightTag);
                myField.setSelectionRange(2,2);
            }
        }

        insertAtCursor(yourTextarea, leftTag_, rightTag_);
    }

    /* listeners */
    if( $("#customRangeLCW").length > 0 ) {
        $("#customRangeLCW").on("input",() => {

            self.taskContent.difficulty = $("#customRangeLCW").val();
            $("#customRangeLabelLCW").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    wordFillCreatorInit();
    return self;
}