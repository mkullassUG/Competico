/*TODO*/
const WordDrag_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "WordDrag";
    self.taskContent.leftWords = [];
    self.taskContent.rightWords = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var WordDragCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        $("#" + self.taskName + "AddConnection").click();
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
        if ($("#customRange" + self.taskName + "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
        }

        //czy można pograc tagi
        if ($("#" + self.taskName + "DivTaskTags").length) {
            var tagsString = $("#" + self.taskName + "DivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#" + self.taskName + "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName + "DivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#" + self.taskName + "Connections").length ) {
            self.taskContent.leftWords = [];
            self.taskContent.rightWords = [];
            self.taskContent.correctMapping = {}
            var sentenceTextareas = $("#" + self.taskName + "Connections").find(".taskTextTextarea");
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

    var sendEditedTaskVariantSuper = self.sendEditedTaskVariant;
    self.sendEditedTaskVariant = (ajaxCallback, onSuccess, taskID, preparedTask = self.prepareTaskJsonFile()) => {
        
        sendEditedTaskVariantSuper(ajaxCallback, onSuccess, taskID, preparedTask);
    }

    var loadTaskFromSuper = self.loadTaskFrom;
    self.loadTaskFrom = (taskObject) => {
        loadTaskFromSuper(taskObject);
        self.prepareLoadedTask();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#" + self.taskName + "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam insmtrukcje*/
        $("#" + self.taskName + "DivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var connectionDiv = $("#" + self.taskName + "Connections");
        connectionDiv.empty();

        for (let i = 0; i < self.taskContent.leftWords.length; i++) {
            var leftWord = self.taskContent.leftWords[i];
            var rightWord = self.taskContent.rightWords[i];

            self.createConnection(i+1, leftWord, rightWord);

        }

        /*ustawiam difficulty*/
        $("#customRange" + self.taskName + "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName + "").trigger("change");
        $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

        /*TODO ustawiam czcionkę*/
    }

    self.addNewConnection = () => {
        
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewConnection = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var connections = $("#" + self.taskName + "Connections").children();
            if (connections.length > 0) {
                for (let i = 0; i < connections.length; i++) {
                    //var connection = connections[i];

                    //zakładam że w kolejności po id
                    var connectionWordTextarea = $("#" + self.taskName + "DivTaskWord" + (i+1)); 
                    var connectionWordTextareaText = connectionWordTextarea.val(); 
                    var connectionDefinitionTextarea = $("#" + self.taskName + "DivTaskDefinition" + (i+1));
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
            //add new and focus
            var connectionDivindex = $("#" + self.taskName + "Connections").children().length + 1;
            
            var sentendeElement = self.createConnection(connectionDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            //display a message about awaiting empty connections
            $("#invalid" + self.taskName + "AddConnection").show();
            setTimeout(function(){$("#invalid" + self.taskName + "AddConnection").fadeOut()},5000);

        }
        //scroll to bottom of the div after adding new
        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createConnection = (i=1, left="", right="") => {
        var htmlString = `<div class="form-group blue-border-focus" id="` + self.taskName + `Connection`+i+`">
            <hr class="border border-primary">
            <label for="` + self.taskName + `ConnectionDiv`+i+`">`+i+`</label>
            <div class="w-75 COC d-inline-block" id="` + self.taskName + `ConnectionDiv`+i+`">
                <label for="` + self.taskName + `DivTaskWord`+i+`">Słowo:</label>
                <textarea class="form-control taskTextTextarea"  id="` + self.taskName + `DivTaskWord`+i+`" rows="2" placeholder="Wstaw lewą stronę nr `+i+`:">`+left+`</textarea>

                <label for="` + self.taskName + `DivTaskDefinition`+i+`">Definicja:</label>
                <textarea class="form-control taskTextTextarea"  id="` + self.taskName + `DivTaskDefinition`+i+`" rows="2" placeholder="Wstaw prawą stronę nr `+i+`:">`+right+`</textarea>
            </div>
            <button class="d-inline-block btn btn-danger btn-sm" id="btn` + self.taskName + `RemoveConnection`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń wiersz.">-</button>
        </div>`;

        var connectionDiv = $("#" + self.taskName + "Connections");
        var element = $(htmlString);
        connectionDiv.append(element);
        
        $("#btn" + self.taskName + "RemoveConnection"+i).on('click', (e) => {
            $('.tooltip').tooltip('dispose');
            self.removeConnection(i);
        });

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip();
        return element;
    }

    self.removeConnection = (index) => {
        /*teraz problem bo indeksy nie są poustawiane

            ALBO ustawiać wszystkim nowe indeksy, podmieniać listenery na buttonach (1)
            ALBO zrobić jeden statyczny rosnący index dla listenerów, indeksy w label ustawiac zaleznie od kolejności (2)
            ALBO wszystkie dane z textarea z niżej przemieszczać o jeden do góry w pętli i usunąć ostatni element (3)
        */

        //(3)
        var connections = $("#" + self.taskName + "Connections").children();
        for ( let i = index; i < connections.length; i++) {
            
            var currentWord = $("#" + self.taskName + "DivTaskWord"+(i));
            var currentDef = $("#" + self.taskName + "DivTaskDefinition"+(i));

            var nextWord = $("#" + self.taskName + "DivTaskWord"+(i+1));
            var nextDef = $("#" + self.taskName + "DivTaskDefinition"+(i+1));

            currentWord.val(nextWord.val())
            currentDef.val(nextDef.val())
        }
        $(`#` + self.taskName + `Connection`+connections.length).remove();
    }
    /*       event listeners          */
    if ( $("#" + self.taskName + "AddConnection").length > 0) {

        var buttonAddConnection = $("#" + self.taskName + "AddConnection"),
        buttonAddConnectionClone = buttonAddConnection.clone();
        buttonAddConnection.replaceWith( buttonAddConnectionClone );
        buttonAddConnectionClone.on('click', (e) => {
            
            self.addNewConnection();
        });
    }

    /* listeners */
    if( $("#customRange" + self.taskName + "").length > 0 ) {
        $("#customRange" + self.taskName + "").on("input",() => {

            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
            $("#customRangeLabel" + self.taskName + "").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    WordDragCreatorInit();
    return self;
}