/*TODO*/
const ManyPicturesManyWords_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "ManyPicturesManyWords";
    self.taskContent.words = [];
    self.taskContent.pictures = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var ManyPicturesManyWordsCreatorInit  = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        self.checkAndClickOnAddButt();

        /*
            "words": ["wda","dawdawd ad"],
            "pictures": ["img1", "img2"],
        */

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
        if ($("#customRangeManyPicturesManyWords").length > 0) {
            self.taskContent.difficulty = $("#customRangeManyPicturesManyWords").val();
        }

        //czy można pograc tagi
        if ($("#ManyPicturesManyWordsDivTaskTags").length) {
            var tagsString = $("#ManyPicturesManyWordsDivTaskTags").val();
            var tags = tagsString.split(",")
                .map(t=> t.trim())
                .filter(t => t!="");
            self.taskContent.tags = [];
            for (let i = 0; i < tags.length; i++) {
                self.taskContent.tags.push(tags[i]);
            }
        }
        //czy można pobrać instrukcje
        if ( $("#ManyPicturesManyWordsDivTaskInstruction").length ) {
            self.taskContent.instruction = $("#ManyPicturesManyWordsDivTaskInstruction").val().trim();
        }   
        
        //czy można pobrać zdania
        if ( $("#ManyPicturesManyWordsConnections").length ) {
            self.taskContent.words = [];
            self.taskContent.pictures = [];
            self.taskContent.correctMapping = {}
            var sentenceTextareas = $("#ManyPicturesManyWordsConnections").find(".taskTextTextarea");
            var cmCounter = 0;
            for (let i = 0; i < sentenceTextareas.length; i+=2) {

                var leftTextarea = $(sentenceTextareas[i]);
                var rightTextarea = $(sentenceTextareas[i+1]);

                var word = leftTextarea.val().trim()
                var picture = rightTextarea.val().trim()

                if (word == "" || picture == "") //Jakoś poinformować o tym?
                    continue;

                self.taskContent.words.push(word);
                self.taskContent.pictures.push(picture);

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
        self.checkAndClickOnAddButt();
    }

    self.prepareLoadedTask = () => {
        
        /*ustawiam tagi*/
        var tagsElem = $("#ManyPicturesManyWordsDivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        /*ustawiam insmtrukcje*/
        $("#ManyPicturesManyWordsDivTaskInstruction").val(self.taskContent.instruction);

        /*wstawiam dla każdego zdania textarea i przyciski*/
        var connectionDiv = $("#ManyPicturesManyWordsConnections");
        connectionDiv.empty();

        for (let i = 0; i < self.taskContent.words.length; i++) {
            var word = self.taskContent.words[i];
            var picture = self.taskContent.pictures[i];

            self.createConnection(i+1, word, picture);

        }

        /*ustawiam difficulty*/
        $("#customRangeManyPicturesManyWords").val(self.taskContent.difficulty);
        $("#customRangeManyPicturesManyWords").trigger("change");
        $("#customRangeLabelManyPicturesManyWords").html(`Difficulty: (` + self.taskContent.difficulty + `)`);

        /*TODO ustawiam czcionkę*/
    }

    self.checkAndClickOnAddButt = () => { 
        var btnToClick = $("#manyPicturesManyWordsAddConnection");
        if ( btnToClick.length && !$("#ManyPicturesManyWordsConnections").children().length)
            btnToClick.click();
    }

    self.addNewConnection = () => {
        
        /*czy są warunki do zrobienia nowego połączenia*/
        var doMakeNewConnection = () => {
            
            /* sprawdź czy nie ma już connection, jak jest to czy ma oba pola puste  */
            var connections = $("#ManyPicturesManyWordsConnections").children();
            if (connections.length > 0) {
                for (let i = 0; i < connections.length; i++) {
                    //var connection = connections[i];

                    //zakładam że w kolejności po id
                    var connectionWordTextarea = $("#ManyPicturesManyWordsDivTaskWord" + (i+1)); 
                    var connectionWordTextareaText = connectionWordTextarea.val(); 
                    var connectionDefinitionTextarea = $("#ManyPicturesManyWordsDivTaskPicture" + (i+1));
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
            var connectionDivindex = $("#ManyPicturesManyWordsConnections").children().length + 1;
            
            var sentendeElement = self.createConnection(connectionDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            //display a message about awaiting empty connections
            $("#invalidManyPicturesManyWordsAddConnection").show();
            setTimeout(function(){$("#invalidManyPicturesManyWordsAddConnection").fadeOut()},5000);

        }
        //scroll to bottom of the div after adding new
        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createConnection = (i=1, word="", picture="") => {
        var htmlString = `<div class="form-group blue-border-focus" id="ManyPicturesManyWordsConnection`+i+`">
            <hr class="border border-primary">
            <label for="ManyPicturesManyWordsConnectionDiv`+i+`">`+i+`</label>
            <div class="w-75 COC d-inline-block" id="ManyPicturesManyWordsConnectionDiv`+i+`">
                <label for="ManyPicturesManyWordsDivTaskWord`+i+`">Słowo:</label>
                <textarea class="form-control taskTextTextarea"  id="ManyPicturesManyWordsDivTaskWord`+i+`" rows="2" placeholder="Wstaw lewą stronę nr `+i+`:">`+word+`</textarea>

                <label for="ManyPicturesManyWordsDivTaskPicture`+i+`">Obrazek URL:</label>
                <textarea class="form-control taskTextTextarea"  id="ManyPicturesManyWordsDivTaskPicture`+i+`" rows="2" placeholder="Wstaw prawą stronę nr `+i+`:">`+picture+`</textarea>
            </div>
            <button class="d-inline-block btn btn-danger btn-sm" id="btnManyPicturesManyWordsRemoveConnection`+i+`">-</button>
        </div>`;

        var connectionDiv = $("#ManyPicturesManyWordsConnections");
        var element = $(htmlString);
        connectionDiv.append(element);
        
        $("#btnManyPicturesManyWordsRemoveConnection"+i).on('click', (e) => {
            self.removeConnection(i);
        });

        return element;
    }

    self.removeConnection = (index) => {
        /*teraz problem bo indeksy nie są poustawiane

            ALBO ustawiać wszystkim nowe indeksy, podmieniać listenery na buttonach (1)
            ALBO zrobić jeden statyczny rosnący index dla listenerów, indeksy w label ustawiac zaleznie od kolejności (2)
            ALBO wszystkie dane z textarea z niżej przemieszczać o jeden do góry w pętli i usunąć ostatni element (3)
        */

        //(3)
        var connections = $("#ManyPicturesManyWordsConnections").children();
        for ( let i = index; i < connections.length; i++) {
            
            var currentWord = $("#ManyPicturesManyWordsDivTaskWord"+(i));
            var currentDef = $("#ManyPicturesManyWordsDivTaskPicture"+(i));

            var nextWord = $("#ManyPicturesManyWordsDivTaskWord"+(i+1));
            var nextDef = $("#ManyPicturesManyWordsDivTaskPicture"+(i+1));

            currentWord.val(nextWord.val())
            currentDef.val(nextDef.val())
        }
        $(`#ManyPicturesManyWordsConnection`+connections.length).remove();
    }
    /*       event listeners          */
    if ( $("#ManyPicturesManyWordsAddConnection").length > 0) {

        var buttonAddConnection = $("#ManyPicturesManyWordsAddConnection"),
        buttonAddConnectionClone = buttonAddConnection.clone();
        buttonAddConnection.replaceWith( buttonAddConnectionClone );
        buttonAddConnectionClone.on('click', (e) => {
            
            self.addNewConnection();
        });
    }

    /* listeners */
    if( $("#customRangeManyPicturesManyWords").length > 0 ) {
        $("#customRangeManyPicturesManyWords").on("input",() => {

            self.taskContent.difficulty = $("#customRangeManyPicturesManyWords").val();
            $("#customRangeLabelManyPicturesManyWords").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    ManyPicturesManyWordsCreatorInit();
    return self;
}