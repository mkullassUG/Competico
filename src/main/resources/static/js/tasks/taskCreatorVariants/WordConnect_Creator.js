/*TODO*/
const WordConnect_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "WordConnect";
    self.taskContent.leftWords = [];
    self.taskContent.rightWords = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var wordConnectCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);
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
            "taskName" : "WordConnect",
            "taskContent" : {
                "id" : "2d210a77-329e-41c9-9fe2-ef7b0dd6918a",
                "instruction" : "Match the words with their translations:",
                "tags" : [ ],
                "leftWords" : [ "keynote", "to convey (information)", "to unveil (a theme)", "consistent", "stiff", "a knack (for sth)", "a flair", "intricate", "dazzling", "to rehearse" ],
                "rightWords" : [ "myśl przewodnia, główny motyw", "przekazywać/dostarczać (informacje)", "odkryć, ujawnić, odsłonić", "spójny, zgodny, konsekwentny", "sztywny, zdrętwiały", "talent, zręczność", "klasa, dar", "zawiły, misterny", "olśniewający", "próbować, przygotowywać się" ],
                "correctMapping" : {
                "0" : 0,
                "1" : 1,
                "2" : 2,
                "3" : 3,
                "4" : 4,
                "5" : 5,
                "6" : 6,
                "7" : 7,
                "8" : 8,
                "9" : 9
                },
                "difficulty" : 100.0
            }
        }*/
        
        //czy można pobrać difficulty
        if ($("#wordConnectDificulty").length) {
            self.taskContent.difficulty = $("#wordConnectDificulty").val();
        }
        //czy można pograc tagi
        if ($("#wordConnectDivTaskTags").length) {
            var tagsString = $("#wordConnectDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#wordConnectDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#wordConnectDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#wordConnectConnections").length ) {
            self.taskContent.leftWords = [];
            self.taskContent.rightWords = [];
            self.taskContent.correctMapping = {}
            var sentenceTextareas = $("#wordConnectConnections").find(".taskTextTextarea");
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
        var tagsElem = $("#wordConnectDivTaskTags");
        tagsElem.val("");
        var previousTags;
        for (let i = 0; i < self.taskContent.tags.length; i++) {
            var tag = self.taskContent.tags[i];

            previousTags = tagsElem.val();
            tagsElem.val(previousTags + ", " + tag);
        }

        /*ustawiam insmtrukcje*/
        $("#wordConnectDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var connectionDiv = $("#wordConnectConnections");
        connectionDiv.empty();

        for (let i = 0; i < self.taskContent.leftWords.length; i++) {
            var leftWord = self.taskContent.leftWords[i];
            var rightWord = self.taskContent.rightWords[i];

            self.createConnection(i+1, leftWord, rightWord);

        }

        /*ustawiam difficulty*/
        $("#wordConnectDificulty").val(self.taskContent.difficulty);


        /*TODO ustawiam czcionkę*/
    }

    self.addNewConnection = () => {
        
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewConnection = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var connections = $("#wordConnectConnections").children();
            if (connections.length > 0) {
                for (let i = 0; i < connections.length; i++) {
                    //var connection = connections[i];

                    //zakładam że w kolejności po id
                    var connectionWordTextarea = $("#wordConnectDivTaskWord" + (i+1)); 
                    var connectionWordTextareaText = connectionWordTextarea.val(); 
                    var connectionDefinitionTextarea = $("#wordConnectDivTaskDefinition" + (i+1));
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
            //add new
            var connectionDivindex = $("#wordConnectConnections").children().length + 1;
            self.createConnection(connectionDivindex);
        } else {
            //display a message about awaiting empty connections
            $("#invalidConnectAddConnection").show();
            setTimeout(function(){$("#invalidConnectAddConnection").fadeOut()},5000);

        }
    }

    self.createConnection = (i=1, left="", right="") => {
        var htmlString = `<div class="form-group blue-border-focus" id="wordConnectConnection`+i+`">
            <hr class="border border-primary">
            <label for="wordConnectConnectionDiv`+i+`">`+i+`</label>
            <div class="w-75 COC d-inline-block" id="wordConnectConnectionDiv`+i+`">
                <label for="wordConnectDivTaskWord`+i+`">Słowo:</label>
                <textarea class="form-control taskTextTextarea"  id="wordConnectDivTaskWord`+i+`" rows="2" placeholder="Wstaw słowo nr `+i+`:">`+left+`</textarea>

                <label for="wordConnectDivTaskDefinition`+i+`">Definicja:</label>
                <textarea class="form-control taskTextTextarea"  id="wordConnectDivTaskDefinition`+i+`" rows="2" placeholder="Wstaw definicje nr `+i+`:">`+right+`</textarea>
            </div>
            <button class="d-inline-block btn btn-danger btn-sm" id="btnWordConnectRemoveConnection`+i+`">-</button>
        </div>`;

        var connectionDiv = $("#wordConnectConnections");
        var element = $(htmlString);
        connectionDiv.append(element);
        
        $("#btnWordConnectRemoveConnection"+i).on('click', (e) => {
            self.removeConnection(i);
        });
    }

    self.removeConnection = (index) => {
        /*teraz problem bo indeksy nie są poustawiane

            ALBO ustawiać wszystkim nowe indeksy, podmieniać listenery na buttonach (1)
            ALBO zrobić jeden statyczny rosnący index dla listenerów, indeksy w label ustawiac zaleznie od kolejności (2)
            ALBO wszystkie dane z textarea z niżej przemieszczać o jeden do góry w pętli i usunąć ostatni element (3)
        */

        //(3)
        var connections = $("#wordConnectConnections").children();
        for ( let i = index; i < connections.length; i++) {
            
            var currentWord = $("#wordConnectDivTaskWord"+(i));
            var currentDef = $("#wordConnectDivTaskDefinition"+(i));

            var nextWord = $("#wordConnectDivTaskWord"+(i+1));
            var nextDef = $("#wordConnectDivTaskDefinition"+(i+1));

            currentWord.val(nextWord.val())
            currentDef.val(nextDef.val())
        }
        $(`#wordConnectConnection`+connections.length).remove();
    }
    /*       event listeners          */
    if ( $("#wordConnectAddConnection").length > 0) {

        var buttonAddConnection = $("#wordConnectAddConnection"),
        buttonAddConnectionClone = buttonAddConnection.clone();
        buttonAddConnection.replaceWith( buttonAddConnectionClone );
        buttonAddConnectionClone.on('click', (e) => {
            
            self.addNewConnection();
        });
    }


    /*  Initialization */
    wordConnectCreatorInit();
    return self;
}