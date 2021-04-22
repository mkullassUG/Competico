const ListWordFill_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListWordFill";
    self.taskContent = {};
    self.taskContent.rows = [];

    /*  Logic functions */
    var wordFillCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        var btnAddSingleWordFill = $("#ListWordFillAddWordFill");
        btnAddSingleWordFill.on('click',(e)=> {
            
            var createdElement = self.addSingleWordFill();
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

        btnAddSingleWordFill.click();
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
            "taskName" : "ListWordFill",
            "taskContent" : {
                "id" : "0be75481-5a7d-4703-979f-70afcdae5846",
                "instruction" : "Complete the sentences with the best word:",
                "tags" : [ ],
                "rows" : [  {
                "id" : "2438f4e5-9e2a-4493-9de3-b072b786fcb4",
                "text" : [ "Do you mind if we deal ", " it later?" ],
                "emptySpaces" : [ {
                    "answer" : "WITH"
                } ],
                "startWithText" : true,
                "possibleAnswers" : [ "ON", "WITHOUT", "WITH", "FROM" ]
                },  {
                "id" : "34d0cb83-fbb3-4ae5-b712-362d0a0803c6",
                "text" : [ "I don’t want to go into too much ", " at this stage." ],
                "emptySpaces" : [ {
                    "answer" : "DETAIL"
                } ],
                "startWithText" : true,
                "possibleAnswers" : [ "DISTRUCTIONS", "DETAIL", "TIME", "DISCUSSIONS" ]
                } ],
                "difficulty" : 100.0
            }
        }*/


        //czy można pobrać difficulty
        //slider
        if ($("#customRangeLWF").length > 0) {
            self.taskContent.difficulty = $("#customRangeLWF").val();
        }
        // if ($("#ListWordFillDificulty").length) {
        //     self.taskContent.difficulty = $("#ListWordFillDificulty").val();
        // }

        //czy można pograc tagi
        if ($("#ListWordFillDivTaskTags").length) {
            var tagsString = $("#ListWordFillDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#ListWordFillDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#ListWordFillDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        var singleWordFills = $("#WordFills").find(".singleWordFillDiv");
        if ( singleWordFills.length ) {
            
            self.taskContent.rows = [];
            
            for ( let i = 0; i < singleWordFills.length; i++) {
                //dla pojedynczego WordFilla poberać, potem do row wsadzać
                var currentRow = {};
                currentRow.emptySpaces = [];
                currentRow.possibleAnswers = [];

                var singleWordFill = $(singleWordFills[i]);

                //LWFTextArea
                var textString = singleWordFill.find(".LWFTextArea").val();
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

                        currentRow.emptySpaces.push({
                            'answer':word
                        });
                }

                //get possibleAnswers
                //LWFTDivIncorrectWords
                var inCorrectWordsString = singleWordFill.find(".LWFTDivIncorrectWords").val();
                var inCorrectWords = inCorrectWordsString.split(",")
                    .map(t=> t.trim())
                    .filter(t => t!="");
                    currentRow.possibleAnswers = [...correctWords, ...inCorrectWords];

                //get text
                //build regex 
                var regexString = `\\{\\[`+correctWords[0]+ "\\]\\}";
                for ( let i = 1; i < correctWords.length; i++) {
                    regexString += "|\\{\\[" +correctWords[i] + "\\]\\}";
                }
                //regexString += "";
                regexString = new RegExp(regexString, "g");
                var text = textString.split(regexString);
                
                //get starts with text
                currentRow.startWithText = text[0] != "";
                currentRow.text = [...(text).filter((t,index)=>!(t==""&&index==0))]

                self.taskContent.rows[i] = currentRow;
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;
        //BUG fixed, przez id nie przyjmowało, ale nei wiem skąd id O.o
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
        loadTaskFromSuper(taskObject); /*WordFill ma ten "content" jeszcze*/
        
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        //WordFillDiv

        /*ustawiam tagi*/
        var tagsElem = $("#ListWordFillDivTaskTags");
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
        $("#ListWordFillDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/

        /*TODO: msuze zrobić żeby po klasach znajdowało w pętli a nie po id*/
        
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

            var WordFillElement = $(`
            <div class="form-group blue-border-focus singleWordFillDiv">
            
                <hr class="border border-primary">
                <div class="form-inline">
                    <button class="m-auto btn btn-primary btn-sm LWFAddWord" id="ListWordFillAddWord`+i+`">Wstaw brakujące słowo</button>
                    <div class="invalid-feedback invalidLWFAddWord" id="invalidListWordFillAddWord`+i+`">
                    Nie zgadza się umiejscowienie tagów {[]}.
                    </div>
                </div>
                <label class="LWFDivTaskText">`+(i+1)+`</label>
                <div class="align-middle w-75 d-inline-block LWFTextBlock">
                    <div class="form-label-group">
                        <textarea class="form-control taskTextTextarea LWFTextArea"  id="ListWordFillDivTaskText`+i+`" placeholder="cos" rows="4" >`+textWithAnswers+`</textarea>
                        <label for="ListWordFillDivTaskText">Treść</label>
                    </div>
                    <div class="form-inline">
                        <button class="m-auto btn btn-primary btn-sm LWFAddIncorrectWord" id="ListWordFillAddIncorrectWord`+i+`">Wstaw dodatkowe słowa</button> 
                    </div>
                    <div class="form-label-group">
                        <textarea class="form-control incorrectWords LWFTDivIncorrectWords"  id="ListWordFillDivIncorrectWords`+i+`" placeholder="cos" rows="1" >`+possibleAnswersString+`</textarea>
                        <label for="ListWordFillDivIncorrectWords">Niepoprawne odpowiedzi</label>
                    </div>
                </div>
                <button class="align-middle d-inline-block btn btn-danger btn-sm LWFDeleteButton" id="btnLWFRemoveWordFill`+i+`">-</button>
            </div>`);
            
            $("#WordFills").append(WordFillElement);
        }

        self.setupListenersAndIndexesFromPosition(0);

        /*ustawiam difficulty*/
        $("#customRangeLWF").val(self.taskContent.difficulty);
        $("#customRangeLabelLWF").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("ListWordFillDificulty").val(self.taskContent.difficulty);
        
        

        /*TODO ustawiam czcionkę*/
    }

    self.addSingleWordFill = () => {

        //check if treść of other singleWordFills are empty
        var foundEmpty = false;
        var singleWordFills = $("#WordFills").find(".singleWordFillDiv");
        for ( let i = 0; i < singleWordFills.length; i++) {
            var singleWordFill = $(singleWordFills[i]);
            //.trim()
            //.replaceAll(/\s/g,'')
            if ( singleWordFill.find(".taskTextTextarea").val().trim() === ""){
                foundEmpty = true;
                break;
            }
        }
        if (foundEmpty) {
            //wyświetl info i zakończ
            $("#invalidFillAddFill").show();
            setTimeout(function(){$("#invalidFillAddFill").fadeOut()},5000);
            return
        }

        
        var index = singleWordFills.length;
        var WordFillElement = $(`
            <div class="form-group blue-border-focus singleWordFillDiv">
            
                <hr class="border border-primary">
                <div class="form-inline">
                    <button class="m-auto btn btn-primary btn-sm LWFAddWord" id="ListWordFillAddWord`+index+`">Wstaw brakujące słowo</button>
                    <div class="invalid-feedback invalidLWFAddWord" id="invalidListWordFillAddWord`+index+`">
                    Nie zgadza się umiejscowienie tagów {[]}.
                    </div>
                </div>
                <label class="LWFDivTaskText">`+(index+1)+`</label>
                <div class="align-middle w-75 d-inline-block LWFTextBlock">
                    <div class="form-label-group">
                        <textarea class="form-control taskTextTextarea LWFTextArea"  id="ListWordFillDivTaskText`+index+`" placeholder="cos" rows="4" > </textarea>
                        <label for="ListWordFillDivTaskText">Treść</label>
                    </div>
                    <div class="form-inline">
                        <button class="m-auto btn btn-primary btn-sm LWFAddIncorrectWord" id="ListWordFillAddIncorrectWord`+index+`">Wstaw dodatkowe słowa</button> 
                    </div>
                    <div class="form-label-group">
                        <textarea class="form-control incorrectWords LWFTDivIncorrectWords"  id="ListWordFillDivIncorrectWords`+index+`" placeholder="cos" rows="1" > </textarea>
                        <label for="ListWordFillDivIncorrectWords">Niepoprawne odpowiedzi</label>
                    </div>
                </div>
                <button class="align-middle d-inline-block btn btn-danger btn-sm LWFDeleteButton" id="btnLWFRemoveWordFill`+index+`">-</button>
            </div>`);
        $("#WordFills").append(WordFillElement)
        self.setupListenersAndIndexesFromPosition(index);

        return WordFillElement;
    }

    self.setupListenersAndIndexesFromPosition = (elemPosition) => {
        
        
        var ChildrenAddWord = $(".LWFAddWord");
        var ChildrenTextArea = $(".LWFTextArea");
        var ChildrenAddIncorrectWord = $(".LWFAddIncorrectWord");

        //ListWordFillAddWordFill   
        for (let i = elemPosition; i < ChildrenAddWord.length; i ++) {
            var indexLabel = $($(".LWFDivTaskText")[i]);
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
                        parentElem.find(".LWFTDivIncorrectWords").focus();
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
            
            //.LWFDeleteButton
            //#btnLWFRemoveWordFill  +  i
            //klonuje przycisk od usuwania
            //#btnLWFRemoveWordFill  +  i
            var btnRemove = $("#btnLWFRemoveWordFill"+i);
            if ( btnRemove.length > 0 ) {
                var button = btnRemove,
                buttonClone = button.clone();
                button.replaceWith( buttonClone );

                if ( buttonClone.length ) {
                    buttonClone.on('click', (e) => {
                        var parentElem = $(e.target).closest(".singleWordFillDiv");
                        self.deleteSingleWordFillParent(parentElem);
                    });
                }
            }
        }  
    }

    self.deleteSingleWordFillParent = (parentElem) => {
        //ogarnąc który z kolei jest to element i delete, wszystkie wyżej elementy przesunać i pozmieniać listenery.
        //array.index(parentElem)
        var WordFillsDiv = parentElem.closest("#WordFills");
        var allSingleWordFills = WordFillsDiv.find(".singleWordFillDiv");

        var currentAtPosition = allSingleWordFills.index(parentElem);

        parentElem.remove();

        self.setupListenersAndIndexesFromPosition(currentAtPosition);
    }

    self.addNewWordForWordFill = (leftTag_, rightTag_, forElement ) => {

        var yourTextarea = forElement.find(".LWFTextArea")[0];
        
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
                        forElement.find(".invalidLWFAddWord").show();
                        setTimeout(function(){forElement.find(".invalidLWFAddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        //oof zatrzymaj i wyświetl info że tagi się nie zgadzają
                        forElement.find(".invalidLWFAddWord").show();
                        setTimeout(function(){forElement.find(".invalidLWFAddWord").fadeOut()},5000);
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
    if( $("#customRangeLWF").length > 0 ) {
        $("#customRangeLWF").on("input",() => {

            self.taskContent.difficulty = $("#customRangeLWF").val();
            $("#customRangeLabelLWF").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }

    /*  Initialization */
    wordFillCreatorInit();
    return self;
}