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
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    /*TODO: */
    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile;
    self.prepareTaskJsonFile = () => {
        var task = prepareTaskJsonFileSuper();

        //czy można pobrać difficulty
        //slider
        if ($("#customRange" + self.taskName+ "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName+ "").val();
        }

        //czy można pograc tagi
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
        //czy można pobrać instrukcje
        if ( $("#" + self.taskName+ "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName+ "DivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#" + self.taskName+ "Sentences").length ) {
            self.taskContent.rows = [];
            var sentenceTextareas = $("#" + self.taskName+ "Sentences").find(".taskTextTextarea");
            for (let i = 0; i < sentenceTextareas.length; i++) {
                var sentenceTextarea = $(sentenceTextareas[i]);
                var sentence = sentenceTextarea.val().split(" ").filter(s=> s != "");
                if (sentence.length == 0) //Jakoś poinformować o tym?
                    continue;

                self.taskContent.rows.push({"words":sentence});
            }
        }

        task.taskName = self.taskName;
        task.taskContent = self.taskContent;
        //again... id fix (like in other tasks?)
        if (task.taskContent.id)
            delete task.taskContent.id;
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

    /*TODO: */
    self.prepareLoadedTask = () => {
        /*ustawiam tagi*/
        var tagsElem = $("#" + self.taskName+ "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam insmtrukcje*/
        $("#" + self.taskName+ "DivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var sentenceDiv = $("#" + self.taskName+ "Sentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];
            console.log(row);
            var sentence = row.words.join(" ");
            self.createSentence((i+1),sentence)
        }

        /*ustawiam difficulty*/
        $("#customRange" + self.taskName+ "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName+ "").trigger("change");
        $("#customRangeLabel" + self.taskName+ "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("#" + self.taskName+ "Dificulty").val(self.taskContent.difficulty);
    }

    self.checkAndClickOnAddButt = () => {
        var buttonAddSentence = $("#" + self.taskName+ "AddSentence");
        if ( buttonAddSentence.length && !$("#" + self.taskName+ "Sentences").children().length)
            buttonAddSentence.click();
    }

    /*TODO: */
    self.addNewSentence = () => {
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewSentence = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var sentences = $("#" + self.taskName+ "Sentences").children();
            if (sentences.length > 0) {
                for (let i = 0; i < sentences.length; i++) {

                    //zakładam że w kolejności po id
                    var sentenceTextarea = $("#" + self.taskName+ "DivTaskText" + (i+1)); 
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
            var sentenceDivindex = $("#" + self.taskName+ "Sentences").children().length + 1;
            var sentendeElement = self.createSentence(sentenceDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            //display a message about awaiting empty sentences
            $("#invalid" + self.taskName+ "AddSentence").show();
            setTimeout(function(){$("#invalid" + self.taskName+ "AddSentence").fadeOut()},5000);
        }
        //scroll to bottom of the div after adding new
        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    /*TODO: */
    self.createSentence = (i=1, sentence="") => {
        //console.log(sentence);

        
        var htmlString = `<div class="form-group blue-border-focus" id="` + self.taskName+ `Sentence`+i+`">
                <label for="` + self.taskName+ `DivTaskText`+i+`">`+i+`</label>
                <textarea class="w-75 d-inline-block form-control taskTextTextarea"  id="` + self.taskName+ `DivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`">`+sentence+`</textarea>
                <button class="d-inline-block btn btn-danger btn-sm" id="btn` + self.taskName+ `RemoveSentence`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń zdanie.">-</button>
            </div>`;

        var sentenceDiv = $("#" + self.taskName+ "Sentences");
        var element = $(htmlString);
        sentenceDiv.append(element);
        
        $("#btn" + self.taskName+ "RemoveSentence"+i).on('click', (e) => {
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

    /*TODO: */
    self.removeSentence = (index) => {

        /*robione w podobne sposób jak w wordConnectCreator*/
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