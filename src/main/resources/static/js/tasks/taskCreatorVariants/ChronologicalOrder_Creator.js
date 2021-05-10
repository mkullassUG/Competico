/*TODO*/
const ChronologicalOrder_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ChronologicalOrder";
    self.taskContent.sentences = [];

    /*  Logic functions */
    var ChronologicalOrderCreatorInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);
        
        /*pozbywam się even listenerów z przycisków przez klonowanie*/

        var buttonAddSentence = $("#chronologicalOrderAddSentence"),
        buttonAddSentenceClone = buttonAddSentence.clone();
        buttonAddSentence.replaceWith( buttonAddSentenceClone );

        if ( buttonAddSentenceClone.length )
            buttonAddSentenceClone.on('click', (e) => {
                self.addNewSentence();
            });
        
        self.checkAndClickOnAddButt();
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

        //czy można pobrać difficulty
        //slider
        if ($("#customRangeChronologicalOrder").length > 0) {
            self.taskContent.difficulty = $("#customRangeChronologicalOrder").val();
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
        
        /*ustawiam tagi*/
        var tagsElem = $("#chronologicalOrderDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam instrukcje*/
        $("#chronologicalOrderDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var sentenceDiv = $("#chronologicalOrderSentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.sentences.length; i++) {
            var sentence = self.taskContent.sentences[i];

            self.createSentence((i+1),sentence)
        }

        /*ustawiam difficulty*/
        $("#customRangeChronologicalOrder").val(self.taskContent.difficulty);
        $("#customRangeChronologicalOrder").trigger("change");
        $("#customRangeLabelCO").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("#chronologicalOrderDificulty").val(self.taskContent.difficulty);


        /*TODO ustawiam czcionkę*/
    }

    self.checkAndClickOnAddButt = () => {
        var buttonAddSentence = $("#chronologicalOrderAddSentence");
        if ( buttonAddSentence.length && !$("#chronologicalOrderSentences").children().length)
            buttonAddSentence.click();
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
            //add new and focus
            var sentenceDivindex = $("#chronologicalOrderSentences").children().length + 1;
            var sentendeElement = self.createSentence(sentenceDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            //display a message about awaiting empty sentences
            $("#invalidConnectAddSentence").show();
            setTimeout(function(){$("#invalidConnectAddSentence").fadeOut()},5000);
        }
        
        //scroll to bottom of the div after adding new
        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createSentence = (i=1, sentence="") => {
        
        var htmlString = `<div class="form-group blue-border-focus" id="chronologicalOrderSentence`+i+`">
                <label for="chronologicalOrderDivTaskText`+i+`">`+i+`</label>
                <textarea class="w-75 d-inline-block form-control taskTextTextarea" id="chronologicalOrderDivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`">`+sentence+`</textarea>
                <button class="d-inline-block btn btn-danger btn-sm" id="btnChronologicalOrderRemoveSentence`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń wiersz.">-</button>
            </div>`;

        var sentenceDiv = $("#chronologicalOrderSentences");
        var element = $(htmlString);
        sentenceDiv.append(element);
        
        $("#btnChronologicalOrderRemoveSentence"+i).on('click', (e) => {
            $('.tooltip').tooltip('dispose');
            self.removeSentence(i);
        });
        
        tooltipsUpdate();

        return element;
    }

    var tooltipsUpdate = () => {

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
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