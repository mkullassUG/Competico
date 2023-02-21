const ListChoiceWordFill_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListChoiceWordFill";
    self.taskContent = {};
    self.taskContent.rows = [];

    /*  Logic functions */
    var WordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        var btnAddSingleChoiceWordFill = $("#" + self.taskName+ "AddWordFill");
        btnAddSingleChoiceWordFill.on('click',(e)=> {
            var createdElement = self.addSingleChoiceWordFill();

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
        
        var singleChoiceWordFills = $("#ChoiceWordFills").find(".singleChoiceWordFillDiv");
        if ( singleChoiceWordFills.length ) {
             
            self.taskContent.rows = [];

            for ( let i = 0; i < singleChoiceWordFills.length; i++) {
                var currentRow = {};
                currentRow.text = [];
                currentRow.wordChoices = [];
                currentRow.startWithText;

                var singleChoiceWordFill = $(singleChoiceWordFills[i]);

                var textString = singleChoiceWordFill.find("." + self.taskName+ "FTextArea").val();
                var tagsWithWords = textString.match(/\{\[[^]+?\]\}/g);
                if (tagsWithWords != null){ 
                    var correctWords = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",'').split("][")[0]);
                    var incorrectWordsArray = tagsWithWords.map(w=> w.replace("{[",'').replace("]}",'').split("][")[1].split(","));
                } else {
                    var correctWords = [];
                    var incorrectWordsArray = [];
                }

                if ( !incorrectWordsArray.length ) { // could unform user?
                    
                    //console.warn("W jednym z wierszy nie wstawiono opcji wyboru!");
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
                currentRow.startWithText = text[0] != "";
                currentRow.text = [...(text).filter((t,index)=>!(t==""&&index==0))]
                self.taskContent.rows[i] = currentRow;
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
        loadTaskFromSuper(taskObject);
        self.prepareLoadedTask();
        self.checkAndClickOnAddButt();
    }

    self.checkAndClickOnAddButt = () => {
        var btnAddSingleChoiceWordFill = $("#" + self.taskName+ "AddWordFill");
        if ( btnAddSingleChoiceWordFill.length && !$("#ChoiceWordFills").children().length)
            btnAddSingleChoiceWordFill.click();
    }

    var newChoiceElement = (index, text = "") =>  $(`
            <div class="form-group blue-border-focus singleChoiceWordFillDiv">
                <hr class="border border-primary">
                
                <label class="` + self.taskName+ `FDivTaskText">`+(index+1)+`</label>
                <div class="align-middle w-75 d-inline-block ` + self.taskName+ `FTextBlock">
                    <div class="form-label-group ` + self.taskName+ `F-flg-mb-0">
                        <textarea class="form-control taskTextTextarea ` + self.taskName+ `FTextArea"  id="` + self.taskName+ `FDivTaskTextArea`+index+`" placeholder=" " rows="4" data-toggle="tooltip" data-placement="top" title="Kolejne pola wyboru wstawiaj wezług wzoru: {[poprawna odpowiedź][niepoprawne odpowiedzi oddzielone przecinkami ',']} lub użyj skrótu Ctrl + B">`+text+`</textarea>
                        <label for="` + self.taskName+ `FDivTaskTextArea">Treść</label>
                    </div>
                </div>
                
                <button class="align-middle d-inline-block btn btn-danger btn-sm ` + self.taskName+ `FDeleteButton" id="btn` + self.taskName+ `FRemoveWordFill`+index+`" data-toggle="tooltip" data-placement="top" title="Usuń cały wiersz">-</button>

                <div class="form-inline">
                    <button class="m-auto btn btn-primary btn-sm ` + self.taskName+ `FAddWord" id="` + self.taskName+ `FAddWord`+index+`">Wstaw wybór odpowiedzi</button>
                    <div class="invalid-feedback invalid` + self.taskName+ `FAddWord" id="invalid` + self.taskName+ `FAddWord`+index+`">
                    Nie zgadza się umiejscowienie tagów {[ ][ ]}.
                    </div>
                </div>
            </div>`);

    self.prepareLoadedTask = () => {
        
        var tagsElem = $("#" + self.taskName+ "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#" + self.taskName+ "DivTaskInstruction").val(self.taskContent.instruction);

        $("#ChoiceWordFills").html(``);
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];

            var wordChoices = [...row.wordChoices];

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

            var ChoiceWordFillElement = newChoiceElement(i,textWithChoices);

            $("#ChoiceWordFills").append(ChoiceWordFillElement);
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

    self.addSingleChoiceWordFill = () => {

        var foundEmpty = false;
        var singleChoiceWordFills = $("#ChoiceWordFills").find(".singleChoiceWordFillDiv");
        for ( let i = 0; i < singleChoiceWordFills.length; i++) {
            var singleChoiceWordFill = $(singleChoiceWordFills[i]);
            
            if ( singleChoiceWordFill.find(".taskTextTextarea").val().trim() === ""){
                foundEmpty = true;
                break;
            }
        }
        if (foundEmpty) {
            $("#invalidChoiceFillAddFill").show();
            setTimeout(function(){$("#invalidChoiceFillAddFill").fadeOut()},5000);
            return
        }

        var index = singleChoiceWordFills.length;

        
        var ChoiceWordFillElement = newChoiceElement(index,"");

        $("#ChoiceWordFills").append(ChoiceWordFillElement);
        self.setupListenersAndIndexesFromPosition(index);

        tooltipsUpdate();
            
        return ChoiceWordFillElement;
    }

    self.setupListenersAndIndexesFromPosition = (elemPosition) => {

        
        var ChildrenAddWord = $("." + self.taskName+ "FAddWord");
        var ChildrenTextArea = $("." + self.taskName+ "FTextArea");

        for (let i = elemPosition; i < ChildrenAddWord.length; i ++) {
            var indexLabel = $($("." + self.taskName+ "FDivTaskText")[i]);
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
                        
                        self.addNewWordForChoiceWordFill("{[", "][", "]}", parentElem);
                    });

                    buttonClone.mousedown(function(e) {
                        e.preventDefault();
                    });    
                    
                    var TextArea = currentChildTextArea,
                    TextAreaClone = TextArea.clone();
                    TextArea.replaceWith( TextAreaClone );

                    if (currentChildTextArea.length > 0) {
                        
                        currentChildTextArea.on('blur', function(e) {
                            var parentElem = $(e.target).closest(".form-group");
                            parentElem.find(".LWFAddWord").attr('disabled','');
                        });
    
                        currentChildTextArea.on('focus', function(e) {
                            var parentElem = $(e.target).closest(".form-group");
                            parentElem.find(".LWFAddWord").removeAttr("disabled");
                        });

                        TextAreaClone.keydown( self.keyCombos );
                    }
                }
            }

            var btnRemove = $("#btn" + self.taskName+ "FRemoveWordFill"+i);
            if ( btnRemove.length > 0 ) {
                var button = btnRemove,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".singleChoiceWordFillDiv");
                        $('.tooltip').tooltip('dispose');
                        self.deleteSingleChoiceWordFillParent(parentElem);
                    });
                }
            }
        }
    }

    self.deleteSingleChoiceWordFillParent = (parentElem) => {

        var WordFillsDiv = parentElem.closest("#ChoiceWordFills");
        var allSingleChoiceWordFills = WordFillsDiv.find(".singleChoiceWordFillDiv");
        var currentAtPosition = allSingleChoiceWordFills.index(parentElem);
        parentElem.remove();

        self.setupListenersAndIndexesFromPosition(currentAtPosition);
    }

    self.keyCombos = (e) =>{
        let evtobj = window.event ? event : e;
        //key combo for ctrl+b
        if (evtobj.keyCode == 66 && evtobj.ctrlKey) {
            e.preventDefault();
            var parentElem = $(e.target).closest(".form-group");

            self.addNewWordForChoiceWordFill("{[", "][", "]}", parentElem);
        }
    }

    self.addNewWordForChoiceWordFill = (leftTag_, middleTag_, rightTag_, forElement) => {

        var yourTextarea = forElement.find("." + self.taskName+ "FTextArea")[0];
        var insertAtCursor = (myField, leftTag, rightTag) => {

            var selText = window.getSelection().toString();
            selText = selText.replaceAll(leftTag,"").replaceAll(rightTag,"");

            //IE support
            if (document.selection) {
                myField.focus();
                sel = document.selection.createRange();
                sel.text = (leftTag + selText +middleTag_+ rightTag);
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
                        forElement.find(".invalid" + self.taskName+ "FAddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "FAddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        forElement.find(".invalid" + self.taskName+ "FAddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "FAddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + selText + middleTag_+ rightTag) + rightSide;
                myField.setSelectionRange(startPos+2+selText.length,startPos+2+selText.length);
            } else {
                myField.value += (leftTag + selText + middleTag_ + rightTag);
                myField.setSelectionRange(2+selText.length,2+selText.length);
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