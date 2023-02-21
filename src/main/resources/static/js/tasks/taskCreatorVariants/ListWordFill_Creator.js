const ListWordFill_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListWordFill";
    self.taskContent = {};
    self.taskContent.rows = [];

    /*  Logic functions */
    var WordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        var btnAddSingleWordFill = $("#" + self.taskName+ "AddWordFill");
        btnAddSingleWordFill.on('click',(e)=> {
            
            var createdElement = self.addSingleWordFill();

            if (createdElement) {
                createdElement.find("textarea").focus()
            }

            function gotoBottom(id){
                var element = document.getElementById(id);
                element.scrollTop = element.scrollHeight - element.clientHeight;
            }
            gotoBottom(self.taskName+"Div");

        });

        self.checkAndClickOnAddButt();
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
        
        if ($("#customRange" + self.taskName+ "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName+ "").val();
        }

        if ($("#" + self.taskName+ "DivTaskTags").length) {
            var tagsString = $("#" + self.taskName+ "DivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }

        if ( $("#" + self.taskName+ "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName+ "DivTaskInstruction").val().trim();
        }   
        
        var singleWordFills = $("#WordFills").find(".singleWordFillDiv");
        if ( singleWordFills.length ) {
            
            self.taskContent.rows = [];
            
            for ( let i = 0; i < singleWordFills.length; i++) {

                var currentRow = {};
                currentRow.emptySpaces = [];
                currentRow.possibleAnswers = [];
                var singleWordFill = $(singleWordFills[i]);
                var textString = singleWordFill.find("." + self.taskName+ "TextArea").val();

                var tagsWithWords = textString.match(/\{\[[^]+?\]\}/g);
                if (tagsWithWords != null)
                    var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",''));
                else 
                    var correctWords = [];
                    
                for (let i = 0; i < correctWords.length; i++) {
                    var correctWord = correctWords[i];
                    
                    var word = correctWord.trim()
                    if (word == "") //Jakoś poinformować o tym?
                        continue;

                        currentRow.emptySpaces.push({
                            'answer':word
                        });
                }

                var inCorrectWordsString = singleWordFill.find("." + self.taskName+ "TDivIncorrectWords").val();
                var inCorrectWords = inCorrectWordsString.split(",")
                    .map(t=> t.trim())
                    .filter(t => t!="");
                    currentRow.possibleAnswers = [...correctWords, ...inCorrectWords];

                var regexString = `\\{\\[`+correctWords[0]+ "\\]\\}";
                for ( let i = 1; i < correctWords.length; i++) {
                    regexString += "|\\{\\[" +correctWords[i] + "\\]\\}";
                }
                
                regexString = new RegExp(regexString, "g");
                var text = textString.split(regexString);
                
                currentRow.startWithText = text[0] != "";
                currentRow.text = [...(text).filter((t,index)=>!(t==""&&index==0))]

                self.taskContent.rows[i] = currentRow;
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;

        if (task.taskContent.id)
            delete task.taskContent.id;
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
        self.checkAndClickOnAddButt();
    }
    
    self.checkAndClickOnAddButt = () => {
        var btnAddSingleWordFill = $("#" + self.taskName+ "AddWordFill");
        if ( btnAddSingleWordFill.length && !$("." + self.taskName+ "AddWord").length)
            btnAddSingleWordFill.click();
    }

    self.keyCombos = (e) =>{
        let evtobj = window.event ? event : e;
        //key combo for ctrl+b
        if (evtobj.keyCode == 66 && evtobj.ctrlKey) {
            e.preventDefault();
            var parentElem = $(e.target).closest(".form-group");

            self.addNewWordForWordFill("{[", "]}", parentElem);
        }
    }

    var newWordFillElement = (index, text = "", answers = "") => {
        return $(`
            <div class="form-group blue-border-focus singleWordFillDiv">
            
                <hr class="border border-primary">
                
                <label class="` + self.taskName+ `DivTaskText">`+(index+1)+`</label>
                <div class="align-middle w-75 d-inline-block ` + self.taskName+ `TextBlock">
                    <div class="form-label-group mb-0">
                        <textarea class="form-control taskTextTextarea ` + self.taskName+ `TextArea"  id="` + self.taskName+ `DivTaskText`+index+`" placeholder=" " rows="4" data-toggle="tooltip" data-placement="top" title="Aby wstawić brakujące słowa w treści zapisz je pomiędzy {[ i ]} lub użyj skrótu Ctrl + B">`+text+`</textarea>
                        <label for="` + self.taskName+ `DivTaskText" data-toggle="tooltip">Treść</label>
                    </div>
                    <div class="form-label-group">
                        <button class="m-auto btn btn-primary btn-sm ` + self.taskName+ `AddWord" id="` + self.taskName+ `AddWord`+index+`">Wstaw brakujące poprawne słowo</button>
                        <div class="invalid-feedback invalid` + self.taskName+ `AddWord" id="invalid` + self.taskName+ `AddWord`+index+`">
                        Nie zgadza się umiejscowienie tagów {[ i ]}.
                        </div>
                    </div>
                    <div class="form-inline">
                        <button class="btn btn-primary btn-sm m-1 ` + self.taskName+ `AddIncorrectWord" id="` + self.taskName+ `AddIncorrectWord`+index+`">Wstaw dodatkowe słowa</button> 
                    </div>
                    <div class="form-label-group mb-0">
                        <textarea class="form-control incorrectWords ` + self.taskName+ `TDivIncorrectWords" id="` + self.taskName+ `DivIncorrectWords`+index+`" placeholder=" " rows="1" data-toggle="tooltip" data-placement="top" title="Kolejne niepoprawne odpowiedzi oddzielaj przecinkiem ',' .">`+answers+`</textarea>
                        <label for="` + self.taskName+ `DivIncorrectWords">Niepoprawne odpowiedzi</label>
                    </div>
                </div>
                <button class="align-middle d-inline-block btn btn-danger btn-sm ` + self.taskName+ `DeleteButton" id="btn` + self.taskName+ `RemoveWordFill`+index+`" data-toggle="tooltip" data-placement="top" title="Usuń wiersz.">-</button>
            </div>`);
    }

    self.prepareLoadedTask = () => {
        
        var tagsElem = $("#" + self.taskName+ "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#" + self.taskName+ "DivTaskInstruction").val(self.taskContent.instruction);
        
        $("#WordFills").html(``);
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];

            var possibleAnswersWithoutCorrectWords = [...row.possibleAnswers]
            var correctAnswersArray = row.emptySpaces.map(a=>a.answer);

            possibleAnswersWithoutCorrectWords = possibleAnswersWithoutCorrectWords
                .map(a => !(correctAnswersArray
                    .includes(a))?a:"")
                .filter(a => a !== "")

            var possibleAnswersString = possibleAnswersWithoutCorrectWords.join(", ");

            var textWithAnswers = "";
            if ( row.startWithText) {
                textWithAnswers = row.text[0];
                for ( let i = 0; i < correctAnswersArray.length; i++) {
                    textWithAnswers += "{[" + correctAnswersArray[i] + "]}" + row.text[i+1];
                }

            } else {
                for ( let i = 0; i < correctAnswersArray.length; i++) {
                    textWithAnswers += "{[" + correctAnswersArray[i] + "]}" + row.text[i];
                }
            }

            var WordFillElement = newWordFillElement(i, textWithAnswers, possibleAnswersString);
            
            $("#WordFills").append(WordFillElement);

            
        }

        self.setupListenersAndIndexesFromPosition(0);
        tooltipsUpdate();

        $("#customRange" + self.taskName+ "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName+ "").trigger("change");
        $("#customRangeLabel" + self.taskName+ "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
    }

    var tooltipsUpdate = () => {

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }

    self.addSingleWordFill = () => {

        var foundEmpty = false;
        var singleWordFills = $("#WordFills").find(".singleWordFillDiv");
        for ( let i = 0; i < singleWordFills.length; i++) {
            var singleWordFill = $(singleWordFills[i]);
            
            if ( singleWordFill.find(".taskTextTextarea").val().trim() === ""){
                foundEmpty = true;
                break;
            }
        }
        if (foundEmpty) {
            $("#invalidFillAddFill").show();
            setTimeout(function(){$("#invalidFillAddFill").fadeOut()},5000);
            return
        }
        
        var index = singleWordFills.length;
        var WordFillElement = newWordFillElement(index, "", "");

        $("#WordFills").append(WordFillElement)
        self.setupListenersAndIndexesFromPosition(index);
        tooltipsUpdate();
            
        return WordFillElement;
    }

    self.setupListenersAndIndexesFromPosition = (elemPosition) => {
        
        var ChildrenAddWord = $("." + self.taskName+ "AddWord");
        var ChildrenTextArea = $("." + self.taskName+ "TextArea");
        var ChildrenAddIncorrectWord = $("." + self.taskName+ "AddIncorrectWord");

        for (let i = elemPosition; i < ChildrenAddWord.length; i ++) {
            var indexLabel = $($("." + self.taskName+ "DivTaskText")[i]);
            indexLabel.text(i+1);

            var currentChildAddWord = $(ChildrenAddWord[i]);
            var currentChildTextArea = $(ChildrenTextArea[i]);
            var currentChildAddIncorrectWord = $(ChildrenAddIncorrectWord[i]);

            if ( currentChildAddIncorrectWord.length > 0) {
                var button = currentChildAddIncorrectWord,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length )
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".form-group");
                        parentElem.find("." + self.taskName+ "TDivIncorrectWords").focus();
                    });
            }
    
            if ( currentChildAddWord.length > 0) {

                var button = currentChildAddWord,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".form-group");
                        self.addNewWordForWordFill("{[", "]}", parentElem);
                    });
                    
                    buttonClone.mousedown(function(e) {
                        e.preventDefault();
                    });    
                    
                    var TextArea = currentChildTextArea,
                    TextAreaClone = TextArea.clone();
                    TextArea.replaceWith( TextAreaClone );

                    if (TextAreaClone.length > 0) {
                        
                        TextAreaClone.on('blur', function(e) {
                            var parentElem = $(e.target).closest(".form-group");
                            parentElem.find("." + self.taskName+ "AddWord").attr('disabled','');
                        });
    
                        TextAreaClone.on('focus', function(e) {
                            var parentElem = $(e.target).closest(".form-group");
                            parentElem.find("." + self.taskName+ "AddWord").removeAttr("disabled");
                        });

                        TextAreaClone.keydown( self.keyCombos );
                    }
                }
            }
            
            var btnRemove = $("#btn" + self.taskName+ "RemoveWordFill"+i);
            if ( btnRemove.length > 0 ) {
                var button = btnRemove,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        $('.tooltip').tooltip('dispose');
                        var parentElem = $(e.target).closest(".singleWordFillDiv");
                        self.deleteSingleWordFillParent(parentElem);
                    });
                }
            }
        }  
    }

    self.deleteSingleWordFillParent = (parentElem) => {

        var WordFillsDiv = parentElem.closest("#WordFills");
        var allSingleWordFills = WordFillsDiv.find(".singleWordFillDiv");

        var currentAtPosition = allSingleWordFills.index(parentElem);

        parentElem.remove();

        self.setupListenersAndIndexesFromPosition(currentAtPosition);
    }

    self.addNewWordForWordFill = (leftTag_, rightTag_, forElement ) => {

        var yourTextarea = forElement.find("." + self.taskName+ "TextArea")[0];
        
        var insertAtCursor = (myField, leftTag, rightTag) => {

            var selText = window.getSelection().toString();
            selText = selText.replaceAll(leftTag,"").replaceAll(rightTag,"");

            //IE support
            if (document.selection) {
                myField.focus();
                sel = document.selection.createRange();
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
                        forElement.find(".invalid" + self.taskName+ "AddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "AddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        forElement.find(".invalid" + self.taskName+ "AddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "AddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + selText + rightTag) + rightSide;
                myField.setSelectionRange(startPos+2+ selText.length,startPos+2+ selText.length);
            } else {
                myField.value += (leftTag + selText + rightTag);
                myField.setSelectionRange(2+ selText.length,2+ selText.length);
            }
        }
        insertAtCursor(yourTextarea, leftTag_, rightTag_);
    }
    /* listeners */
    if( $("#customRange" + self.taskName+ "").length > 0 ) {
        $("#customRange" + self.taskName+ "").on("input",() => {

            self.taskContent.difficulty = $("#customRange" + self.taskName+ "").val();
            $("#customRangeLabel" + self.taskName+ "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    WordFillCreatorInit();
    return self;
}