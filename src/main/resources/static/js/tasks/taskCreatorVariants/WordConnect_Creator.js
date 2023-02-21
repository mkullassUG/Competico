const WordConnect_Creator = (data_ = {}) => {

    /*  Extends TaskCreator */
    var self = TaskCreatorVariant(data_);
    /*  Variables */
    self.taskName = "WordConnect";
    self.taskContent.leftWords = [];
    self.taskContent.rightWords = [];
    self.taskContent.correctMapping = {};

    /*  Logic functions */
    var WordConnectCreatorInit = () => {

        self.hideAllTaskDivsExceptGiven(self.taskName);

        self.checkAndClickOnAddButt();
    }

    var checkIfTaskReadySuper = self.checkIfTaskReady;
    self.checkIfTaskReady = () => {
        checkIfTaskReadySuper();
    }

    var prepareTaskJsonFileSuper = self.prepareTaskJsonFile;
    self.prepareTaskJsonFile = () => {
        var task = prepareTaskJsonFileSuper();
        
        if ($("#customRange" + self.taskName + "").length > 0) {
            self.taskContent.difficulty = $("#customRange" + self.taskName + "").val();
        }

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

        if ( $("#" + self.taskName + "DivTaskInstruction").length ) {
            self.taskContent.instruction = $("#" + self.taskName + "DivTaskInstruction").val().trim();
        }   
        
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

                if (leftWord == "" || rightWord == "")//could inform user?
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
        
        var tagsElem = $("#" + self.taskName + "DivTaskTags");
        tagsElem.val(self.taskContent.tags.join(", "));

        $("#" + self.taskName + "DivTaskInstruction").val(self.taskContent.instruction);

        var connectionDiv = $("#" + self.taskName + "Connections");
        connectionDiv.empty();

        for (let i = 0; i < self.taskContent.leftWords.length; i++) {
            var leftWord = self.taskContent.leftWords[i];
            var rightWord = self.taskContent.rightWords[i];

            self.createConnection(i+1, leftWord, rightWord);
        }

        $("#customRange" + self.taskName + "").val(self.taskContent.difficulty);
        $("#customRange" + self.taskName + "").trigger("change");
        $("#customRangeLabelWC").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
    }

    self.checkAndClickOnAddButt = () => { 
        var btnToClick = $("#" + self.taskName + "AddConnection");
        if ( btnToClick.length && !$("#" + self.taskName + "Connections").children().length)
            btnToClick.click();
    }

    self.addNewConnection = () => {
        
        var doMakeNewConnection = () => {
            
            var connections = $("#" + self.taskName + "Connections").children();
            if (connections.length > 0) {
                for (let i = 0; i < connections.length; i++) {

                    var connectionWordTextarea = $("#" + self.taskName + "DivTaskWord" + (i+1)); 
                    var connectionWordTextareaText = connectionWordTextarea.val(); 
                    var connectionDefinitionTextarea = $("#" + self.taskName + "DivTaskDefinition" + (i+1));
                    var connectionDefinitionTextareaText = connectionDefinitionTextarea.val(); 
                    
                    if ( connectionWordTextareaText == "" || connectionDefinitionTextareaText == "") {
                        return false;
                    }
                }
            } 
            return true;
        }
        
        if (doMakeNewConnection()) {

            var connectionDivindex = $("#" + self.taskName + "Connections").children().length + 1;
            
            var sentendeElement = self.createConnection(connectionDivindex);
            sentendeElement.find("textarea").focus();
        } else {
            $("#invalidConnectAddConnection").show();
            setTimeout(function(){$("#invalidConnectAddConnection").fadeOut()},5000);

        }

        function gotoBottom(id){
            var element = document.getElementById(id);
            element.scrollTop = element.scrollHeight - element.clientHeight;
        }
        gotoBottom(self.taskName+"Div");
    }

    self.createConnection = (i=1, left="", right="") => {

        var ConnectionDiv = $(`<div class="form-group blue-border-focus" id="` + self.taskName + `Connection`+i+`">`);
        ConnectionDiv.html(`<hr class="border border-primary">`);

        var ConnectionDivLabel = $(`<label for="` + self.taskName + `ConnectionDiv`+i+`">`+i+`</label>`);
        ConnectionDiv.append(ConnectionDivLabel);
        var ConnectionDivInner = $(`<div class="w-75 COC d-inline-block" id="` + self.taskName + `ConnectionDiv`+i+`">`);
        ConnectionDiv.append(ConnectionDivInner);

        var DivTaskWordLabel = $(`<label for="` + self.taskName + `DivTaskWord`+i+`">Słowo:</label>`);
        ConnectionDivInner.append(DivTaskWordLabel);
        var taskTextTextareaLeft = $(`<textarea class="form-control taskTextTextarea"  id="` + self.taskName + `DivTaskWord`+i+`" rows="2" placeholder="Wstaw lewą stronę nr `+i+`:">`);
        taskTextTextareaLeft.append(window.document.createTextNode(left));
        ConnectionDivInner.append(taskTextTextareaLeft);
        var DivTaskDefinitionLabel = $(`<label for="` + self.taskName + `DivTaskDefinition`+i+`">Definicja:</label>`);
        ConnectionDivInner.append(DivTaskDefinitionLabel);
        var taskTextTextareaRight = $(`<textarea class="form-control taskTextTextarea"  id="` + self.taskName + `DivTaskDefinition`+i+`" rows="2" placeholder="Wstaw prawą stronę nr `+i+`:">`);
        taskTextTextareaRight.append(window.document.createTextNode(right));
        ConnectionDivInner.append(taskTextTextareaRight);

        var button = $(`<button class="d-inline-block btn btn-danger btn-sm" id="btn` + self.taskName + `RemoveConnection`+i+`" data-toggle="tooltip" data-placement="top" title="Usuń połączenie.">-</button>`);
        ConnectionDiv.append(button);

        var connectionDiv = $("#" + self.taskName + "Connections");
        connectionDiv.append(ConnectionDiv);
        
        $("#btn" + self.taskName + "RemoveConnection"+i).on('click', (e) => {
            $('.tooltip').tooltip('dispose');
            self.removeConnection(i);
        });

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip();

        return ConnectionDiv;
    }

    self.removeConnection = (index) => {
        
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
            $("#customRangeLabelWC").html(`Difficulty: (` + self.taskContent.difficulty + `)`);
        })
    }
    /*  Initialization */
    WordConnectCreatorInit();
    return self;
}