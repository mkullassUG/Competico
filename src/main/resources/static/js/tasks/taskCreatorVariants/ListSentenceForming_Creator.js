/*TODO*/
const ListSentenceForming_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListSentenceForming";
    self.taskContent.rows = [];

    /*  Logic functions */
    var ListSentenceFormingInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);
        
        /*pozbywam się even listenerów z przycisków przez klonowanie*/

        var buttonAddSentence = $("#" + self.taskName+ "AddSentence"),
        buttonAddSentenceClone = buttonAddSentence.clone();
        buttonAddSentence.replaceWith( buttonAddSentenceClone );

        if ( buttonAddSentenceClone.length )
            buttonAddSentenceClone.on('click', (e) => {
                self.addNewSentence();
            });
        
        self.checkAndClickOnAddButt();
    }

    var checkIfTaskReadySuper = self.checkIfTaskReady;
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
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
        
        if ( $("#" + self.taskName+ "Sentences").length ) {
            self.taskContent.rows = [];
            var sentenceTextareas = $("#" + self.taskName+ "Sentences").find(".taskTextTextarea");
            for (let i = 0; i < sentenceTextareas.length; i++) {
                var sentenceTextarea = $(sentenceTextareas[i]);

                var multiWordStart = "";
                var multiWordEnd = null;
                var sentenceArrOut = []

                if (sentenceTextarea.val().split("]}").length !== sentenceTextarea.val().split("{[").length)
                    console.warn("nie równa ilośc tagów {[ i ]}");

                var sentenceTextareaValueWithReplacedTags = sentenceTextarea.val().replaceAll("{[]}"," ").replaceAll("]}"," ]} ").replaceAll("{["," {[ ");
                var sentenceArr = sentenceTextareaValueWithReplacedTags.split(" ");
                for (let i = 0; i < sentenceArr.length; i++) {
                    var word = sentenceArr[i]; 

                    if (word.includes("{[") && multiWordStart === "")
                        multiWordStart = word.replaceAll("{["," ").replaceAll("]}"," ");
                    else if (word.includes("]}") && multiWordStart !== "")
                        multiWordEnd = word.replaceAll("]}"," ").replaceAll("{["," ");
                    else if (multiWordStart !== "" && !word.includes("]}") && !word.includes("{["))
                        multiWordStart += " " + word;
                    else if (word.includes("{[") || word.includes("]}")) {
                        word = word.replaceAll("{[","").replaceAll("]}","");
                        console.warn("usunąłem jedno ]} lub {[");
                    } 
                    
                    if (i === sentenceArr.length-1 && multiWordStart !== "" && multiWordEnd === null){
                        word = multiWordStart;
                        multiWordStart = "";
                        console.warn("samemu zakończyłem ]}");
                    }
                    
                    if ( multiWordStart === "")
                        sentenceArrOut.push(word);
                    else if ( multiWordEnd !== null) {
                        sentenceArrOut.push(" " + (multiWordStart + " " + multiWordEnd).trim() + " ");
                        multiWordStart="";
                        multiWordEnd=null;
                    }
                }
                sentenceArrOut = sentenceArrOut.filter(s=> s.trim() != "");
                if (sentenceArrOut.length == 0) //Jakoś poinformować o tym?
                    continue;

                self.taskContent.rows.push({"words":sentenceArrOut});
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

    self.prepareLoadedTask = () => {
        
        var tagsElem = $("#" + self.taskName+ "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#" + self.taskName+ "DivTaskInstruction").val(self.taskContent.instruction);

        var sentenceDiv = $("#" + self.taskName+ "Sentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];

            for ( let j = 0; j < row.words.length; j++) {
                var word = row.words[j];
                row.words[j] = (word.includes(" ")? "{["+word+"]}":word);
            }

            var sentence = row.words.join(" ");
            self.createSentence((i+1),sentence);
        }

        $("#customRange" + self.taskName+ "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName+ "").trigger("change");
        $("#customRangeLabel" + self.taskName+ "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
    }

    self.checkAndClickOnAddButt = () => {
        var buttonAddSentence = $("#" + self.taskName+ "AddSentence");
        if ( buttonAddSentence.length && !$("#" + self.taskName+ "Sentences").children().length)
            buttonAddSentence.click();
    }

    self.addNewSentence = () => {

        var doMakeNewSentence = () => {
            
            var sentences = $("#" + self.taskName+ "Sentences").children();
            if (sentences.length > 0) {
                for (let i = 0; i < sentences.length; i++) {

                    var sentenceTextarea = $("#" + self.taskName+ "DivTaskText" + (i+1)); 
                    var sentenceTextareaText = sentenceTextarea.val(); 

                    if ( sentenceTextareaText == "") {
                        return false;
                    }
                }
            } 
            return true;
        }
        
        if (doMakeNewSentence()) {
            
            var sentenceDivindex = $("#" + self.taskName+ "Sentences").children().length + 1;
            var sentendeElement = self.createSentence(sentenceDivindex);
            sentendeElement.find("textarea").focus();
        } else {

            $("#invalid" + self.taskName+ "AddSentence").show();
            setTimeout(function(){$("#invalid" + self.taskName+ "AddSentence").fadeOut()},5000);
        }

        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createSentence = (i=1, sentence="") => {

        var listSentenceFormingSentence = $(`<div class="form-group blue-border-focus" id="` + self.taskName+ `Sentence`+i+`">`);
        var DivTaskTextLabel = $(`<label for="` + self.taskName+ `DivTaskText`+i+`">`+i+`</label>`);
        var taskTextTextarea = $(`<textarea class="w-75 d-inline-block form-control taskTextTextarea" id="` + self.taskName+ `DivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`" data-toggle="tooltip" data-placement="top" title="Aby wstawić kilka słów w jeden element: {[słowo słowo ...]} lub użyj skrótu CRTL + B">`);
        var button = $(`<button class="d-inline-block btn btn-danger btn-sm" id="btn` + self.taskName+ `RemoveSentence`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń zdanie.">-</button>`);

        var textNodeSentence = window.document.createTextNode(sentence);
        taskTextTextarea.append(textNodeSentence);
        taskTextTextarea.keydown( self.keyCombos );
        listSentenceFormingSentence.append(DivTaskTextLabel).append(taskTextTextarea).append(button);
        var sentenceDiv = $("#" + self.taskName+ "Sentences");
        sentenceDiv.append(listSentenceFormingSentence);
        
        $("#btn" + self.taskName+ "RemoveSentence"+i).on('click', (e) => {
            $('.tooltip').tooltip('dispose');
            self.removeSentence(i);
        });

        tooltipsUpdate();
            
        return listSentenceFormingSentence;
    }

    var tooltipsUpdate = () => {

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }

    self.keyCombos = (e) =>{
        let evtobj = window.event ? event : e;
        //key combo for ctrl+b
        if (evtobj.keyCode == 66 && evtobj.ctrlKey) {
            e.preventDefault();
            var parentElem = $(e.target).closest(".form-group");

            self.addNewMultiWord("{[", "]}", parentElem);
        }
    }

    self.addNewMultiWord = (leftTag_, rightTag_, forElement) => {

        var yourTextarea = forElement.find(".taskTextTextarea")[0];
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
                        console.log("1");

                        forElement.find(".invalid" + self.taskName+ "FAddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "FAddWord").fadeOut()},5000);
                        return;
                    }
                }
                var partsR = rightSide.split(rightTag);
                for ( let i = 0 ; i < partsR.length-1; i++) {
                    var part = partsR[i];

                    if ( !part.includes(leftTag)) {
                        console.log("2");
                        forElement.find(".invalid" + self.taskName+ "FAddWord").show();
                        setTimeout(function(){forElement.find(".invalid" + self.taskName+ "FAddWord").fadeOut()},5000);
                        return;
                    }
                }

                myField.value = leftSide + (leftTag + selText + rightTag) + rightSide;
                myField.setSelectionRange(startPos+2+selText.length,startPos+2+selText.length);
            } else {
                console.log("awd");
                myField.value += (leftTag + selText  + rightTag);
                myField.setSelectionRange(2+selText.length,2+selText.length);
            }
        }

        insertAtCursor(yourTextarea, leftTag_, rightTag_);
    }
    
    self.removeSentence = (index) => {

        var sentences = $("#" + self.taskName+ "Sentences").children();
        for ( let i = index; i < sentences.length; i++) {
            
            var currentSentence = $("#" + self.taskName+ "DivTaskText"+(i));
            var nextSentence = $("#" + self.taskName+ "DivTaskText"+(i+1));
            currentSentence.val(nextSentence.val())
        }

        $(`#` + self.taskName+ `Sentence`+sentences.length).remove();
    }

    /* listeners */
    if( $("#customRange" + self.taskName+ "").length > 0 ) {
        $("#customRange" + self.taskName+ "").on("input",() => {

            self.taskContent.difficulty = $("#customRange" + self.taskName+ "").val();
            $("#customRangeLabel" + self.taskName+ "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    
    /*  Initialization */
    ListSentenceFormingInit();
    return self;
}