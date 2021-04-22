/*TODO*/
const ListSentenceForming_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ListSentenceForming";
    self.taskContent.rows = [];

    /*  Logic functions */
    var listSentenceFormingInit = () => {
        self.hideAllTaskDivsExceptGiven(self.taskName);
        
        /*pozbywam się even listenerów z przycisków przez klonowanie*/

        var buttonAddSentence = $("#LSFAddSentence"),
        buttonAddSentenceClone = buttonAddSentence.clone();
        buttonAddSentence.replaceWith( buttonAddSentenceClone );

        if ( buttonAddSentenceClone.length )
            buttonAddSentenceClone.on('click', (e) => {
                self.addNewSentence();
            });
        
        buttonAddSentenceClone.click();
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
        if ($("#customRangeLSF").length > 0) {
            self.taskContent.difficulty = $("#customRangeLSF").val();
        }
        // if ($("#LSFDificulty").length) {
        //     self.taskContent.difficulty = $("#LSFDificulty").val();
        // }
        //czy można pograc tagi
        if ($("#LSFDivTaskTags").length) {
            var tagsString = $("#LSFDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#LSFDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#LSFDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#LSFSentences").length ) {
            self.taskContent.rows = [];
            var sentenceTextareas = $("#LSFSentences").find(".taskTextTextarea");
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
    }

    /*TODO: */
    self.prepareLoadedTask = () => {
        /*ustawiam tagi*/
        var tagsElem = $("#LSFDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + ", " + tag);
        }

        /*ustawiam insmtrukcje*/
        $("#LSFDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var sentenceDiv = $("#LSFSentences");
        sentenceDiv.empty();
        for (let i = 0; i < self.taskContent.rows.length; i++) {
            var row = self.taskContent.rows[i];
            console.log(row);
            var sentence = row.words.join(" ");
            self.createSentence((i+1),sentence)
        }

        /*ustawiam difficulty*/
        $("#customRangeLSF").val(self.taskContent.difficulty);
        $("#customRangeLabelLSF").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        // $("#LSFDificulty").val(self.taskContent.difficulty);
    }

    /*TODO: */
    self.addNewSentence = () => {
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewSentence = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var sentences = $("#LSFSentences").children();
            if (sentences.length > 0) {
                for (let i = 0; i < sentences.length; i++) {

                    //zakładam że w kolejności po id
                    var sentenceTextarea = $("#LSFDivTaskText" + (i+1)); 
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
            var sentenceDivindex = $("#LSFSentences").children().length + 1;
            var sentendeElement = self.createSentence(sentenceDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            //display a message about awaiting empty sentences
            $("#invalidLSFAddSentence").show();
            setTimeout(function(){$("#invalidLSFAddSentence").fadeOut()},5000);
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

        
        var htmlString = `<div class="form-group blue-border-focus" id="LSFSentence`+i+`">
                <label for="LSFDivTaskText`+i+`">`+i+`</label>
                <textarea class="w-75 d-inline-block form-control taskTextTextarea"  id="LSFDivTaskText`+i+`" rows="2" placeholder="Wstaw zdanie nr `+i+`">`+sentence+`</textarea>
                <button class="d-inline-block btn btn-danger btn-sm" id="btnLSFRemoveSentence`+i+`">-</button>
            </div>`;

        var sentenceDiv = $("#LSFSentences");
        var element = $(htmlString);
        sentenceDiv.append(element);
        
        $("#btnLSFRemoveSentence"+i).on('click', (e) => {
            self.removeSentence(i);
        });

        return element;
    }
    /*TODO: */
    self.removeSentence = (index) => {

        /*robione w podobne sposób jak w wordConnectCreator*/
        var sentences = $("#LSFSentences").children();
        for ( let i = index; i < sentences.length; i++) {
            
            var currentSentence = $("#LSFDivTaskText"+(i));

            var nextSentence = $("#LSFDivTaskText"+(i+1));

            currentSentence.val(nextSentence.val())
        }

        $(`#LSFSentence`+sentences.length).remove();
    }
    /* listeners */
    if( $("#customRangeLSF").length > 0 ) {
        $("#customRangeLSF").on("input",() => {

            self.taskContent.difficulty = $("#customRangeLSF").val();
            $("#customRangeLabelLSF").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    listSentenceFormingInit();
    return self;
}