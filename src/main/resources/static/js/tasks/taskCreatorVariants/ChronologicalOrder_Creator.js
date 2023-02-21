const ChronologicalOrder_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ChronologicalOrder";
    self.taskContent.sentences = [];

    /*  Logic functions */
    var ChronologicalOrderCreatorInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);

        var buttonAddSentence = $("#chronologicalOrderAddSentence"),
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

        if ($("#customRangeChronologicalOrder").length > 0) {
            self.taskContent.difficulty = $("#customRangeChronologicalOrder").val();
        }

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
        
        if ( $("#chronologicalOrderDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#chronologicalOrderDivTaskInstruction").val().trim();
        }   
        
        if ( $("#chronologicalOrderSentences").length ) {
            self.taskContent.sentences = [];
            var sentenceTextareas = $("#chronologicalOrderSentences").find(".taskTextTextarea");
            for (let i = 0; i < sentenceTextareas.length; i++) {
                var sentenceTextarea = $(sentenceTextareas[i]);
                var sentence = sentenceTextarea.val().trim()
                if (sentence == "") //could inform user?
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
        
        var tagsElem = $("#chronologicalOrderDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#chronologicalOrderDivTaskInstruction").val(self.taskContent.instruction);

        var sentenceDiv = $("#chronologicalOrderSentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.sentences.length; i++) {
            var sentence = self.taskContent.sentences[i];

            self.createSentence((i+1),sentence)
        }

        $("#customRangeChronologicalOrder").val(self.taskContent.difficulty);
        $("#customRangeChronologicalOrder").trigger("change");
        $("#customRangeLabelCO").text(`Difficulty: (` + self.taskContent.difficulty + `)`);
    }

    self.checkAndClickOnAddButt = () => {
        var buttonAddSentence = $("#chronologicalOrderAddSentence");
        if ( buttonAddSentence.length && !$("#chronologicalOrderSentences").children().length)
            buttonAddSentence.click();
    }

    self.addNewSentence = () => {
        
        var doMakeNewSentence = () => {
            
            var sentences = $("#chronologicalOrderSentences").children();
            if (sentences.length > 0) {
                for (let i = 0; i < sentences.length; i++) {

                    var sentenceTextarea = $("#chronologicalOrderDivTaskText" + (i+1)); 
                    var sentenceTextareaText = sentenceTextarea.val(); 

                    if ( sentenceTextareaText == "") {
                        return false;
                    }
                }
            } 
            return true;
        }
        
        if (doMakeNewSentence()) {
            var sentenceDivindex = $("#chronologicalOrderSentences").children().length + 1;
            var sentendeElement = self.createSentence(sentenceDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            $("#invalidConnectAddSentence").show();
            setTimeout(function(){$("#invalidConnectAddSentence").fadeOut()},5000);
        }
        
        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createSentence = (i=1, sentence="") => {
        
        var chronologicalOrderSentence = $(`<div class="form-group blue-border-focus" id="chronologicalOrderSentence`+i+`">`);
        var chronologicalOrderDivTaskTextlabel = $(`<label for="chronologicalOrderDivTaskText`+i+`">`+i+`</label>`);
        var taskTextTextarea = $(`<textarea class="w-75 d-inline-block form-control taskTextTextarea" id="chronologicalOrderDivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`"></textarea>`);
        var button = $(`<button class="d-inline-block btn btn-danger btn-sm" id="btnChronologicalOrderRemoveSentence`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń wiersz.">-</button>`);

        taskTextTextarea.append(window.document.createTextNode(sentence));
        chronologicalOrderSentence.append(chronologicalOrderDivTaskTextlabel).append(taskTextTextarea).append(button);
        var sentenceDiv = $("#chronologicalOrderSentences");
        sentenceDiv.append(chronologicalOrderSentence);
        
        $("#btnChronologicalOrderRemoveSentence"+i).on('click', (e) => {
            $('.tooltip').tooltip('dispose');
            self.removeSentence(i);
        });
        
        tooltipsUpdate();

        return chronologicalOrderSentence;
    }

    var tooltipsUpdate = () => {

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }

    self.removeSentence = (index) => {

        var sentences = $("#chronologicalOrderSentences").children();
        for ( let i = index; i < sentences.length; i++) {
            
            var currentSentence = $("#chronologicalOrderDivTaskText"+(i));

            var nextSentence = $("#chronologicalOrderDivTaskText"+(i+1));

            currentSentence.val(nextSentence.val())
        }

        $(`#chronologicalOrderSentence`+sentences.length).remove();
    }
    /* listeners */
    if( $("#customRangeChronologicalOrder").length > 0 ) {
        $("#customRangeChronologicalOrder").on("input",() => {

            self.taskContent.difficulty = $("#customRangeChronologicalOrder").val();
            $("#customRangeLabelCO").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    ChronologicalOrderCreatorInit();
    return self;
}