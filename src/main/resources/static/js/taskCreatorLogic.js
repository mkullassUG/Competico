const TaskCreatorLogic = (playerInfo_, debug) => {
    var self = {};
    /*       logic variables          */
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.focusedTaskID; //wybrane z tablicy zadań dla usuń / edytuj
    self.lastEditedTaskID; // wybrane po potwierdzeniu edycji danego zadania
    self.tablicaPolskichNazwTaskow = {
        'WordFill': 'Jeden wielozdaniowy tekst, jedna pula odpowiedzi',
        'WordConnect': 'Łączenie słów i zwrotów z dwóch kolumn',
        'ChronologicalOrder': 'Układanie zdań w porządek chronologiczny'
    };
    self.currentTaskVariant; // ustawianie dema
    self.currentVariant; //edytowanie

    /*       logic functions          */
    self.taskCreatorInit = () => {
        /* TODO
        przygotuj start strony
        */
        var urlVariant =  window.location.hash.substr(1);
        if ( urlVariant.length != "")
            self.changeVariant(urlVariant);
        else
            self.changeVariant("WordFill");

            
        otherFrontendLogic();
        self.setupImportedTasksTable();
    }

    self.changeVariant = (variantString) => {
        /*TODO:
        -sprawdzanie czy wpisano coś w pola obecnego wariantu
            -jesli tak to zapytanie czy na pewno zmienić wariant na inny i stracić dane...    
        -zmiana wariantu do edytowania na przycisk
        */

        /*warianty:
            1 - Wypełnianie luk w tekście   (WordFill)
                1.2 - Jeden wielozdaniowy tekst, jedna pula odpowiedzi
            2 - Łączenie słów i zwrotów z dwóch kolumn (WordConnect)
            3 - Układanie zdań w porządek chronologiczny (ChronologicalOrder)
        */

        /*ukryj przucisk edycji, pokaż przycisk zapisu*/
        $("#btnSaveEditedTask").hide();
        $("#btnSaveTask").show();

        //sprawdzanie
        switch (variantString) {
            case "WordFill":
                self.currentVariant = WordFillCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "WordFill",
                    "taskContent" : {
                      "instruction" : "Complete the text with the missing words:",
                      "tags" : [ ],
                      "content" : {
                        "text" : [ "I’m sorry to have to tell you that there has been some ", " in the project and we won’t be able to ", " our original ", " on July 30th for completing the ", " of the new software. Pedro’s absence for three weeks caused a bit of a ", ", and there were more delays when we realised that there was still some ", " in the databases that needed cleaning up. Still, I am confident that we can complete the project by the end of next month." ],
                        "emptySpaces" : [ {
                          "answer" : "slippage"
                        }, {
                          "answer" : "stick to"
                        }, {
                          "answer" : "deadline"
                        }, {
                          "answer" : "rollout"
                        }, {
                          "answer" : "bottleneck"
                        }, {
                          "answer" : "dirty data"
                        } ],
                        "startWithText" : true,
                        "possibleAnswers" : [ "bottleneck", "deadline", "dirty data", "migrate", "rollout", "slippage", "stick to", "within", "scope" ]
                      },
                      "difficulty" : 100.0
                    }
                  });
                break;
            case "ChronologicalOrder":
                self.currentVariant = ChronologicalOrderCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "ChronologicalOrder",
                    "taskContent" : {
                      "instruction" : "Put the phrases in order:",
                      "tags" : [ ],
                      "sentences" : [ "Try to understand the problem and define the purpose of the program.", "Once you have analysed the problem, define the successive logical steps of the program.", "Write the instructions in a high-level language of your choice.", "Once the code is written, test it to detect bugs or errors.", "Debug and fix errors in your code.", "Finally, review the program’s documentation." ],
                      "difficulty" : 100.0
                    }
                  });
                break;
            case "WordConnect":
                self.currentVariant = WordConnectCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "WordConnect",
                    "taskContent" : {
                      "instruction" : "Match the words with their translations:",
                      "tags" : [ ],
                      "leftWords" : [ "data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier" ],
                      "rightWords" : [ "eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna" ],
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
                  });
                break;
           default:
               console.warn("TODO, to nie powinno się wydarzyć!")
               break;
        }

    }

    self.downloadImportedTasks = () => {
        self.ajaxGetImportedTasksFile();
    }

    self.sendTask = () => {
        
        if (!self.currentVariant) 
            return false;
        
        self.currentVariant.sendTaskVariant(
            self.sendAjaxTask,
            self.setupImportedTasksTable);
    }
    /*prepare task to edit*/
    self.editTask = (taskID) => {
        self.ajaxGetImportedTaskByID( 
            (data)=>{

                /*swap variant to the one from data .taskName*/
                self.changeVariant(data.taskName);
                self.currentVariant.loadTaskFrom(data);
                /*ustawić przycisk zapisz edycja zamiast zapisz zadanie*/
                $("#btnSaveEditedTask").show();
                $("#btnSaveTask").hide();
                /*zapamiętać id obecnie edytowanego taska*/
                self.lastEditedTaskID = taskID;
            },
            taskID)
    }
    /*send edited task*/
    self.saveEditTask = (taskID) => {
        if (!self.currentVariant) 
            return false;

        self.currentVariant.sendEditedTaskVariant(
            self.sendAjaxEditTask,
            self.setupImportedTasksTable,
            taskID);
    };

    /*delete task*/
    self.deleteTask = (taskID) => {
        self.sendAjaxDeleteTask(taskID,self.setupImportedTasksTable);
    }

    self.setupImportedTasksTable = () => {
        self.ajaxGetImpotedTasksArray((importedTasksArray)=>{

            var tableElem = $("#importedTasksElem");
            tableElem.html("")
            for (let i = 0; i < importedTasksArray.length; i++) {
                var task = importedTasksArray[i];
                tableElem.append(`
                    <tr>
                        <th scope="row">`+(i+1)+`</th>
                        <td>` + self.tablicaPolskichNazwTaskow[task.taskName] + " (" + task.taskName + `)</td>
                        <td>
                            <button type="button" class="btn btn-success editButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#editTaskModalCenter">Edytuj</button>
                        </td>
                        <td>
                            <button type="button" class="btn btn-danger delButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#deleteTaskModalCenter">Usuń</button>
                        </td>
                    </tr> `);
            }

            $("#openNavButton").html("&#9776; Zapisane zadania ("+importedTasksArray.length+")");
            $("#infoNavDiv").html("Zapisane zadania ("+importedTasksArray.length+")");
            

            $(".editButton").on("click",(e)=>{
                if (self.debug)
                    console.log("editButton");
                var taskID = $(e.target).data("taskid");
                console.log(e)
                console.log($(e.target))
                console.log(taskID)

                //ustawiam przycisk z modala edycji pod dane id
                //TODO czy to wgl działa??
                $("#btnSendEditTask").data("taskid",taskID);

                //zrobie to zapamiętując id w pamięci...
                self.focusedTaskID = taskID;
                //self.editTask(taskID);
            });

            $(".delButton").on("click",(e)=>{
                if (self.debug)
                    console.log("delButton");
                var taskID = $(e.target).data("taskid");
                console.log(e)
                console.log($(e.target))
                console.log(taskID)

                //ustawiam przycisk z modala edycji pod dane id
                $("#btnSendDeleteTask").data("taskid",taskID);
                self.focusedTaskID = taskID;
                //self.editTask(taskID);
            });
        })
    }

    self.setupDemo = () => {
        /*TODO
        z obecnego wariantu, użyć tego co mam obecnie w nim i stworzyć z tego zadanie*/
        if (!self.currentVariant)
            return;

        /*
            1. przerobić to co mam w edycji na format json
            1.2 (opcjonalne) randomizować odpowiedzi
            2. ustawić widocznosc odpowiedniego elementu wariantu z gry
            3. powstawiać dane do elementu gry
        */
        $("#gameDemoDiv").show();
        $("#taskEditHolder").hide();
        $("#btnDemoTask").hide();
        $("#btnDemoTaskEnd").show();

        //Do taskToSetup zapisać json zadania 
        //BUG 2021-02-07 używałem tego samego obiektu do dema co wysyłania na serwer, FIX 2021-02-07: parsowanie obiektu na string JSON i spowrotem, żeby powtsał nowy obiekt dla dema
        var taskToSetup = JSON.parse(JSON.stringify(self.currentVariant.prepareTaskJsonFile()));

        //w gameLogic jest setupNewTask, czy chce zrobić grugie takie tutaj?
        //start setupNewTask gameLogic
        //czy otrzymany task jest pusty
        if (taskToSetup == null) {
            if (self.debug)
            console.warn("task was empty");
            return;
        }
        
        taskToSetup.task = taskToSetup.taskContent; // bo tak jest w gameLogic

        if (!taskToSetup.taskName)
            console.warn("Could not read task name!");

        //czyszczenie porpzedniego taska jeśli jakiś był
        //przygotowanie miejsca na następnego taska
        if ($("#GameDiv").length)
            $("#GameDiv").html("");

        //mok task1
        if (self.debug) 
            console.log(taskToSetup);
        
        //wybieranie odpowiedniej logiki dla konkretnego template'a
        switch (taskToSetup.taskName) {
            case "WordFill":
                taskToSetup.task = taskToSetup.taskContent.content; // bo takie wysyłam :v

                taskToSetup.task.emptySpaceCount = taskToSetup.task.emptySpaces.length; //bo tego nie wysyłam serwerowi a potrzebuje sam do stworzenia zadania

                self.currentTaskVariant = TaskVariant0(taskToSetup.task);
                break;
            case "WordConnect":
                self.currentTaskVariant = TaskVariant1(taskToSetup.task);
                break;
            case "ChronologicalOrder":
                self.currentTaskVariant = TaskVariant2(taskToSetup.task);
                break;
            case "template3":
                self.currentTaskVariant = TaskVariant3(taskToSetup.task);//GameLogicVariants.logicVariant3(task);
                break;
            case "template4":
                self.currentTaskVariant = TaskVariant4(taskToSetup.task);//GameLogicVariants.logicVariant4(task);
                break;
            case "template5":
                self.currentTaskVariant = TaskVariant5(taskToSetup.task);//GameLogicVariants.logicVariant5(task);
                break;
            default:
                console.warn("To pole jest tylko dla jeszcze nie zaimplementowancyh tasków, w produkcji nie powinno się nigdy wykonać!");
                self.currentTaskVariant = {};
                //ListWordFill answers
                self.currentTaskVariant.getAnswers = () => { 
                    console.log("hello ListWordFill");
                    return {answers: [["test"]]} 
                }
                break;
        }
        //end setupNewTask gameLogic


        //self.currentVariant
    }

    self.endDemo = () => {

        $("#gameDemoDiv").hide();
        $("#taskEditHolder").show();
        $("#btnDemoTask").show();
        $("#btnDemoTaskEnd").hide();

    }

    /*       event listeners          */
    if ($("#btnChronologicalOrder").length)
        $("#btnChronologicalOrder").on("click",()=>{
            if (self.debug)
                console.log("btnChronologicalOrder");
            self.changeVariant("ChronologicalOrder");
        });
    if ($("#btnWordFill").length)
        $("#btnWordFill").on("click",()=>{
            if (self.debug)
                console.log("btnWordFill");
            self.changeVariant("WordFill");
        });
    if ($("#btnWordConnect").length)
        $("#btnWordConnect").on("click",()=>{
            if (self.debug)
                console.log("btnWordConnect");
            self.changeVariant("WordConnect");
        });
    if ($("#btnDownloadJsonImportedTasks").length)
        $("#btnDownloadJsonImportedTasks").on("click",()=>{
            if (self.debug)
                console.log("btnDownloadJsonImportedTasks");
            self.downloadImportedTasks();
        });   
    if ($("#btnSendSaveTask").length)
        $("#btnSendSaveTask").on("click",()=>{
            if (self.debug)
                console.log("btnSendSaveTask");
            self.sendTask();
        });
    if ($("#btnSendEditTask").length)
        $("#btnSendEditTask").on("click",(e)=>{
            if (self.debug)
                console.log("btnSendEditTask");
            // var taskID = $(e.target).data("taskid");
            self.editTask(self.focusedTaskID);
        });
    if ($("#btnSendSaveEditTask").length)
        $("#btnSendSaveEditTask").on("click",(e)=>{
            if (self.debug)
                console.log("btnSendSaveEditTask");

            //var taskID = $(e.target).data("taskid");
            self.saveEditTask(self.lastEditedTaskID);
        });
    if ($("#btnSendDeleteTask").length)
        $("#btnSendDeleteTask").on("click",(e)=>{
            if (self.debug)
                console.log("btnSendDeleteTask");
            // var taskID = $(e.target).data("taskid");
            self.deleteTask(self.focusedTaskID);
        });
    if ($("#btnDemoTask").length)
        $("#btnDemoTask").on("click",(e)=>{
            if (self.debug)
                console.log("btnDemoTask");
            self.setupDemo();
        });
    if ($("#btnDemoTaskEnd").length)
        $("#btnDemoTaskEnd").on("click",(e)=>{
            if (self.debug)
                console.log("btnDemoTaskEnd");
            self.endDemo();
        });
    /* Ajax requests*/
    

    self.ajaxGetImportedTasksFile = () => {
        /*/api/v1/tasks/imported/json GET - lista wszystkich wprowadzonych zadań*/

        fetch('/api/v1/tasks/imported/json/file')
            .then(resp => resp.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                // the filename you want
                a.download = 'importedTasksFile.json';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                alert('your file has downloaded!'); // or you know, something with better UX...
            })
            .catch(() => alert('oh no!'));
    }
    
    self.ajaxGetNumberOfTasks = (callback) =>{
        /*/api/v1/tasks/imported/count GET - liczba wszystkich wprowadzonych zadań*/
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/tasks/imported/count",
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("ajaxGetNumberOfTasks success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("ajaxGetNumberOfTasks error");
                console.warn(jqXHR);
                console.warn(status);
                console.warn(err);
                }
            }
        });
    }

    self.ajaxGetImpotedTasksArray = (callback) => {
        
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/tasks/imported/info",
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.warn("ajaxGetImpotedTasksArray success");
                    console.log(data);
                }
                callback(data);
            },
            error: function(jqXHR, status, err) {   
                if (self.debug) {
                    console.warn("ajaxGetImpotedTasksArray error");
                    console.warn(jqXHR);
                    console.warn(status);
                    console.warn(err);    
                }  
            }
        });
    }

    self.ajaxGetImportedTaskByID = (callback, taskID) => {
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/tasks/imported/" + taskID,
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.warn("ajaxGetImportedTaskByID success");
                    console.log(data);
                }
                callback(data);
            },
            error: function(jqXHR, status, err) {     
                if (self.debug) {
                    console.warn("ajaxGetImportedTaskByID error");
                    console.warn(jqXHR);
                    console.warn(status);
                    console.warn(err);    
                }  
            }
        });
    }
    /*     ajax http actions       */
    self.sendAjaxTask = (task, callback) => {
        /* /api/v1/tasks/imported POST - dodanie nowego zadania przez JSON */
        var send = task;
        console.log(send);
        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/tasks/imported",
            contentType: "application/json",
            data     : JSON.stringify(send),
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxTask success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("sendAjaxTask error");
                console.warn(jqXHR);
                console.warn(status);
                console.warn(err);
                }
            }
        });
    }

    self.sendAjaxEditTask = (task, taskID, callback) => {

        var send = task;
        console.log(send);
        $.ajax({
            type     : "PUT",
            cache    : false,
            url      : "/api/v1/tasks/imported/" + taskID,
            contentType: "application/json",
            data     : JSON.stringify(send),
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxEditTask success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("sendAjaxEditTask error");
                console.warn(jqXHR);
                console.warn(status);
                console.warn(err);
                }
            }
        });
    }

    self.sendAjaxDeleteTask = (taskID, callback) => {

        $.ajax({
            type     : "DELETE",
            cache    : false,
            url      : "/api/v1/tasks/imported/" + taskID,
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxDeleteTask success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxDeleteTask error");
                    console.warn(jqXHR);
                    console.warn(status);
                    console.warn(err);
                }
            }
        });
    }

    /* Other: */
    var otherFrontendLogic = () => {
        /* textarea wtf it is blurry (no scroll) fix
        nie działa w WordFill bo są inne textarea

        WGL to tekst na całej stronei robi się wtedy blurry
        */
        var observe;
        if (window.attachEvent) {
            observe = function (element, event, handler) {
                element.attachEvent('on'+event, handler(element));
            };
        }
        else {
            observe = function (element, event, handler) {
                element.addEventListener(event, handler(element), false);
            };
        }
        function textareaAutoscroll () {
            //var text = document.getElementById('WordFillDivTaskText');
            var allText = $(document).find(".taskTextTextarea");
            
            //currying concept https://en.wikipedia.org/wiki/Currying
            var resize = function(text) {
                return function curried_func(e) {
                    text.style.height = 'auto';
                    text.style.height = text.scrollHeight+'px';
                }
            }

            var delayedresize = function(text) {
                return function curried_func(e) {
                    window.setTimeout(resize(text), 0);
                }
            }
            for ( let i = 0; i < allText.length; i++) {
                var text = allText[i];
                
                /* 0-timeout to get the already changed text */
                
                observe(text, 'change',  resize);
                observe(text, 'focus',  resize);
                observe(text, 'cut',     delayedresize);
                observe(text, 'paste',   delayedresize);
                observe(text, 'drop',    delayedresize);
                observe(text, 'keydown', delayedresize);
            
                text.focus();
                text.select();
                resize(text);
            }
        }
        $(document).ready(function(){
            textareaAutoscroll();
        });


        /*  dropdown menu   */
        /*TODO 
        przerobić tak żeby działało jak ja chce przy dynamicznie zmieniającym się ekranie (chyba moge usunąć TODO, bo zrobiłem wysuwanie na połowe ekranu i konetant teżsię ładnie kurczy)*/
        // Prevent closing from click inside dropdown
        $(document).on('click', '.dropdown-menu', function (e) {
            e.stopPropagation();
        });
        
        // make it as accordion for smaller screens
        $('.dropdown-menu a').click(function(e){

            if ($(window).width() < 930) { //jak okno jest mniejsze to rozwiń wewnątrz
                e.preventDefault();

                if($(this).next('.submenu').length){
                    $(this).next('.submenu').toggle();
                }
                //.one żeby nie zapętlało się niepotrzebnie
                $('.dropdown').one('hide.bs.dropdown', function (e) {
                    $(this).find('.submenu').hide();
                })
            } else { //jak okno jest większe to rozwiń na zewnątrz
                if($(this).next('.submenu').length){
                    $(this).next('.submenu').toggle();
                }

                $('.dropdown').one('hide.bs.dropdown', function (e) {
                    $(this).find('.submenu').hide();
                })
            }
        });


        /* collapse side-panel*/
        /* Set the width of the sidebar to 250px (show it) */
        function openNav() {
            document.getElementById("mySidepanel").style.width = "50%";
            
            /*to wtedy zmieniam ustawienie elementów od edycji zadań*/

            /*#gametimer > h2*/
            var h2GameTimer = $("#gameTimer > h2");
            if (h2GameTimer.length > 0) {
                if (!h2GameTimer.hasClass("sidepanelClassForGameTimerH2")) {
                    h2GameTimer.addClass("sidepanelClassForGameTimerH2");
                }
            }
            /*.taskDiv WSZYSTKIE */
            var taskDivs = $(".taskDiv");
            if (taskDivs.length > 0) {
                if (!taskDivs.hasClass("sidepanelClassForBottonAndTasks")) {
                    taskDivs.addClass("sidepanelClassForBottonAndTasks");
                }
            }

            /*#Bottom	*/
            var bottomDiv = $("#Bottom");
            if (bottomDiv.length > 0) {
                if (!bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                    bottomDiv.addClass("sidepanelClassForBottonAndTasks");
                }
            }
        }
        
        /* Set the width of the sidebar to 0 (hide it) */
        function closeNav() {
            document.getElementById("mySidepanel").style.width = "0";
            
            /*#gametimer > h2*/
            var h2GameTimer = $("#gameTimer > h2");
            if (h2GameTimer.length > 0) {
                if (h2GameTimer.hasClass("sidepanelClassForGameTimerH2")) {
                    h2GameTimer.removeClass("sidepanelClassForGameTimerH2");
                }
            }
            /*.taskDiv WSZYSTKIE */
            var taskDivs = $(".taskDiv");
            if (taskDivs.length > 0) {
                if (taskDivs.hasClass("sidepanelClassForBottonAndTasks")) {
                    taskDivs.removeClass("sidepanelClassForBottonAndTasks");
                }
            }

            /*#Bottom	*/
            var bottomDiv = $("#Bottom");
            if (bottomDiv.length > 0) {
                if (bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                    bottomDiv.removeClass("sidepanelClassForBottonAndTasks");
                }
            }
        }

        $("#closeNavButton").on("click", ()=> {
            closeNav();
        })

        $("#openNavButton").on("click", ()=> {
            openNav();
        })
    }

    /*  initalization  */
    self.taskCreatorInit();
     
    return self;
}

TaskCreatorLogic.getInstance = (debug) => {

    if (TaskCreatorLogic.singleton)
        return TaskCreatorLogic.singleton;

    var ajaxReceiveWhoAmI = ( ) => {
        
        $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(playerInfo, textStatus, jqXHR) {
            if (debug){
            console.log("ajaxReceiveWhoAmI success");
            console.log(playerInfo);
            console.log(textStatus);
            console.log(jqXHR);
            }
            TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug);
            console.log("TaskCreatorLogic");
        },
        error: function(jqXHR, status, err) {
            if (debug){
            console.warn("ajaxReceiveWhoAmI error");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            }
        }
        });
    }
    
    ajaxReceiveWhoAmI();
    return TaskCreatorLogic.singleton;
}
