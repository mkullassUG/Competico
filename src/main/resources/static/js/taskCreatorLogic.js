const TaskCreatorLogic = (playerInfo_, debug) => {
    var self = {};
    /*       logic variables          */
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.focusedTaskID; //wybrane z tablicy zadań dla usuń / edytuj
    self.lastEditedTaskID; // wybrane po potwierdzeniu edycji danego zadania
    self.tablicaPolskichNazwTaskow = {
        'WordFill': 
        'Wypełnianie luk w tekście\n <br> <small>Jeden wielozdaniowy tekst, jedna pula odpowiedzi</small>',
        'WordConnect': 
        'Łączenie słów i zwrotów z dwóch kolumn',
        'ChronologicalOrder': 
        'Układanie zdań w porządek chronologiczny',
        'ListWordFill':
        "Wypełnianie luk w tekście\n <br> <small>Oddzielne zdania ułożone w wierszach, jedna pula odpowiedzi na wiersz</small>",
        "ListChoiceWordFill":
        "Wypełnianie luk w tekście\n <br> <small>Oddzielne zdania ułożone w wierszach, wybór słowa dla każdej luki</small>",
        'ListSentenceForming' : "Układanie zdań z podanych wyrazów."
    };
    self.currentTaskVariant; // ustawianie dema
    self.currentVariant; //edytowanie

    self.CreatorCore = TaskCreatorCore();

    self.allTaskIds = [];
    self.oldTaskIds = [];
    self.newTaskIds = [];
    self.editedTaskIds = [];
    self.recentlyAddedTaskId;
    self.currentlyEditingThisTaskObejct;
    self.isFirstTableSetup = true;
    /*       logic functions          */
    self.taskCreatorInit = (playerInfo) => {
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

        //modal button bug fixes
        $('#saveTaskModalCenter').on('shown.bs.modal', function (e) {
            $('#btnSaveTask').one('focus', function (e) {
                $(this).blur();
            });
        });
        $('#saveEditTaskModalCenter').on('shown.bs.modal', function (e) {
            $('#btnSaveEditedTask').one('focus', function (e) {
                $(this).blur();
            });
        });
        $('#editTaskModalCenter').on('shown.bs.modal', function (e) {
            $('[data-taskid="'+self.focusedTaskID+'"]').one('focus', function (e) {
                $(this).blur();
            });
        });
        $('#deleteTaskModalCenter').on('shown.bs.modal', function (e) {
            $('[data-taskid="'+self.focusedTaskID+'"]').one('focus', function (e) {
                $(this).blur();
            });
        });
        
        Object.keys(self.tablicaPolskichNazwTaskow).map(key=>{
            
            $("#"+key+"Div .taskEditTitle").html(self.tablicaPolskichNazwTaskow[key]);
        })
            
        //navbar preparation
        NavbarLogic.singleton = NavbarLogic(playerInfo, debug);
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
        //$("#btnSaveTask").show();

        //2021-04-05 fix zmaian wariantu podczas dema
        self.endDemo();
        /*
            Przestac podświetlać edycje taska z listy jeśli jakaś jest
        */

        self.currentVariant = self.CreatorCore.getVariant(variantString);

        self.changeVisualsForCreatingNewVariant(variantString);
        
        self.lastEditedTaskID = undefined;
        self.focusedTaskID = undefined;
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
                //$("#btnSaveTask").hide();
                /*zapamiętać id obecnie edytowanego taska*/
                self.lastEditedTaskID = taskID;

                self.changeVisualsForEditing();
            },
            taskID)
    }

    self.changeVisualsForEditing = () => {

        //remove
        var previousFromVariantList = $("#main_nav").find("li.bg-success");
        if ( previousFromVariantList.length > 0) {
            previousFromVariantList.removeClass("bg-success");
        }

        if ( self.lastEditedTaskID ) {
            var previousFromTable = $("#importedTasksTable").find("tr.bg-info");
            
            if ( previousFromTable.length > 0)
            previousFromTable.removeClass("bg-info")
        }

        //add
        if ( self.lastEditedTaskID )
            $(`[data-taskid='${self.lastEditedTaskID}']`)
            .closest("tr").addClass("bg-info");
    }

    self.changeVisualsForCreatingNewVariant = (variantString) => {
        
        //remove
        if ( self.lastEditedTaskID ) {
            var previousFromTable = $("#importedTasksTable").find("tr.bg-info");
            
            if ( previousFromTable.length > 0)
            previousFromTable.removeClass("bg-info")
        }

        var previousFromVariantList = $("#main_nav").find("li.bg-success");
        if ( previousFromVariantList.length > 0) {
            previousFromVariantList.removeClass("bg-success");
        }

        //add
        $("#main_nav").find(`[href='#${variantString}']`).closest("li").addClass("bg-success");
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

    self.prepareTaskTableAfterRequest = (importedTasksArray)=>{

        var tableElem = $("#importedTasksElem");
        tableElem.html("")

        var foundTaskIds = [];
        for (let i = 0; i < importedTasksArray.length; i++) {
            var task = importedTasksArray[i];
            
            foundTaskIds.push(task.taskID);
            var editedTaskTrSubstring = '';
            if (self.focusedTaskID && task.taskID === self.focusedTaskID) 
                editedTaskTrSubstring = 'class="bg-info"';
            tableElem.append(`
                <tr `+editedTaskTrSubstring+` data-thisTaskId="`+task.taskID+`">
                    <th scope="row">`+(i+1)+`</th>
                    <td>` + self.tablicaPolskichNazwTaskow[task.taskName] + (self.debug?" (" + task.taskName + `)`:"") +`</td>
                    <td>
                        <button type="button" class="btn btn-success editButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#editTaskModalCenter">Edytuj</button>
                    </td>
                    <td>
                        <button type="button" class="btn btn-danger delButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#deleteTaskModalCenter">Usuń</button>
                    </td>
                </tr> `);
        }
        
        /*zakładam, że musze znaleźć nowy task jaki się pojawi, i on jest obecnie wysłanym , pobieram jego id*/
        if (!self.isFirstTableSetup) {
            
            self.newTaskIds = [...foundTaskIds.filter(t=>!self.allTaskIds.includes(t))];
            self.allTaskIds = [...self.allTaskIds, ...self.newTaskIds];
            
            var tasksToHighlit = self.allTaskIds.filter(t=>!self.oldTaskIds.includes(t));
            tasksToHighlit = [...tasksToHighlit, self.editedTaskIds.filter(t=>!tasksToHighlit.includes(t))]
            for (let taskID of tasksToHighlit) {
                self.findTaskInTableById_AndChangeBGColor(taskID);
            }

            if ( self.newTaskIds.length == 1) {
                self.recentlyAddedTaskId = self.newTaskIds[0];
                self.editTask(self.recentlyAddedTaskId);
            } else {
                if ( self.lastEditedTaskID ) {
                    //fix, bo jak usuwałem edytowanego to error
                    if ( self.allTaskIds.includes(self.lastEditedTaskID))
                        self.editTask(self.lastEditedTaskID);
                }
                self.recentlyAddedTaskId = undefined;
            }

        } else {
            self.allTaskIds = [...foundTaskIds];
            self.oldTaskIds = [...foundTaskIds];
            self.isFirstTableSetup = false;
        }

        $("#openNavButton").html("&#9776; Zapisane zadania ("+importedTasksArray.length+")");
        $("#infoNavDiv").html("Zapisane zadania ("+importedTasksArray.length+")");
        

        $(".editButton").on("click",(e)=>{
            if (self.debug)
                console.log("editButton");
            var taskID = $(e.target).data("taskid");

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

            //ustawiam przycisk z modala edycji pod dane id
            $("#btnSendDeleteTask").data("taskid",taskID);
            self.focusedTaskID = taskID;
        });
    }

    self.setupImportedTasksTable = () => {
        self.ajaxGetImpotedTasksArray(self.prepareTaskTableAfterRequest);
    }

    self.findTaskInTableById_AndChangeBGColor = (id, color) => {

        var newTrTask = $("#importedTasksElem").find('[data-thisTaskId="'+id+'"]');
        newTrTask.addClass("newTask");
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
        //czyszczenie porpzedniego taska jeśli jakiś był
        //przygotowanie miejsca na następnego taska
        if ($("#GameDiv").length)
            $("#GameDiv").html("");

        var taskToSetup = self.currentVariant.prepareTaskJsonFile();
        
        if (self.debug) 
            console.log(taskToSetup);

        self.currentTaskVariant = self.CreatorCore.getVariant_GameCore(taskToSetup.taskName, self.currentVariant.prepareTaskJsonFile());

        
        self.resizeWindow();
    }

    self.endDemo = () => {

        //2021-04-05 fix zmaian wariantu podczas dema
        if( self.currentTaskVariant ) 
            self.currentTaskVariant.isTaskDone = true;
        $("#gameDemoDiv").hide();
        $("#taskEditHolder").show();
        $("#btnDemoTask").show();
        $("#btnDemoTaskEnd").hide();

        self.resizeWindow();
    }

    self.deleteAllTasksFromTableVisually = () => {
        $("#importedTasksElem").html("");
        $("#infoNavDiv").html("Zapisane zadania (0)");
    }

    self.resizeWindow = () => {
        if ( self.currentTaskVariant && !self.currentTaskVariant.isTaskDone) {
            //console.log("szerokośc okna pod gre");
            $("html").height("100%");
            $("html").height($(document).height());
        } else {
            //console.log("szerokośc okna 100%");
            $("html").height("100%");
        }
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

    if ($("#btnListWordFill").length)
        $("#btnListWordFill").on("click",()=>{
            if (self.debug)
                console.log("btnListWordFill");
            self.changeVariant("ListWordFill");
        });

    if ($("#btnListChoiceWordFill").length)
        $("#btnListChoiceWordFill").on("click",()=>{
            if (self.debug)
                console.log("btnListChoiceWordFill");
            self.changeVariant("ListChoiceWordFill");
        });
    
    if ($("#btnListSentenceForming").length)
        $("#btnListSentenceForming").on("click",()=>{
            if (self.debug)
                console.log("btnListSentenceForming");
            self.changeVariant("ListSentenceForming");
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
    if ($("#btnSendDeleteAllTasks").length)
        $("#btnSendDeleteAllTasks").on("click",(e)=>{
            if (self.debug)
                console.log("btnSendDeleteAllTasks");
            // var taskID = $(e.target).data("taskid");
            self.ajaxDeleteAllTasks();
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

    if ($("#customImportFile").length) {
        $("#customImportFile").change( (evt) => {

            var input = $("#customImportFile")[0];
            if ( !input.files[0] ||  !input.files[0].name)
                return;

            var fileName = input.files[0].name;

            var nextSibling = input.nextElementSibling
            nextSibling.innerText = fileName

            readFile(evt)
            .then(jsonStr => {
                
                $("#importFileInvalidBadFile").hide();
                $("#importFileInvalidEmptyFile").hide();
                $("#importedTaskTableTBody").html("");
                $("#importedTaskTable").addClass('collapse');
                $("#btnSendImportTasks").addClass('disabled');
                $("#btnSendImportTasks").prop("disabled", true);
                $("#customImportFileLabel").removeClass("invalid-file");
                $("#customImportFileLabel").removeClass("valid-file");

                if (jsonStr) {

                    jsonStr = JSON.parse(jsonStr);

                    if ( !jsonStr.length) {
                        $("#importFileInvalidEmptyFile").show();
                        $("#customImportFileLabel").addClass("invalid-file");
                        return
                    }

                    $("#btnSendImportTasks").removeClass('disabled');
                    $("#btnSendImportTasks").prop("disabled", false);
                    $("#importedTaskTable").removeClass('collapse');
                    $("#customImportFileLabel").addClass("valid-file");

                    showTaskImportTable(jsonStr);
                } else {
                    $("#importFileInvalidBadFile").show();
                    $("#customImportFileLabel").addClass("invalid-file");
                }
                
            });
        });

        if($("#btnUploadJsonImportedTasks").length) {
            $("#btnUploadJsonImportedTasks").on('click', () => {
                
                $("#importFileInvalidBadFile").hide();
                $("#importFileInvalidEmptyFile").hide();
                $("#importedTaskTableTBody").html("");
                $("#importedTaskTable").addClass('collapse');
                $("#btnSendImportTasks").addClass('disabled');
                $("#btnSendImportTasks").prop("disabled", true);
                $("#customImportFileLabel").removeClass("invalid-file");
                $("#customImportFileLabel").removeClass("valid-file");

                var input = $("#customImportFile")[0];
                input.value = '';
                var nextSibling = input.nextElementSibling
                nextSibling.innerText = "Wybierz plik";
            })
        }

        async function readFile (evt) {	
				
            var Input = evt.target;
            var fileTypes = ['json'];  //acceptable file types

            let correctFileFormat = await new Promise((resolve) => {
            
                if (Input.files && Input.files[0]) {
                    var extension = Input.files[0].name.split('.').pop().toLowerCase(),  //file extension from Input file
                    isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types
                    if(isSuccess) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            var str = JSON.stringify(e.target.result);
                            if (isJson(str)) {
                                resolve( JSON.parse(str) );
                            } else {
                                resolve( false );
                            }
                        }
                        reader.readAsText(Input.files[0]);
                    } else {
                        resolve( false );
                    }
                    
                }
            });
            return correctFileFormat;
        }

        let isJson = (str) => {
			try {
				JSON.parse(str);
			} catch (e) {
				return false;
			}
			return true;
		}

        var showTaskImportTable = (jsonStr) => {

            for ( let i = 0; i < jsonStr.length; i++) {
                var task = jsonStr[i];
                $("#importedTaskTableTBody").append(`
                <tr>
                    <td>
                        `+(i+1)+`
                    </td>
                    <td>
                        `+task.taskName+`
                    </td>
                </tr>`)
            }
        }
    }

    if ($("#btnSendImportTasks").length) {

        $("#btnSendImportTasks").on('click',(e)=>{
            console.log("submit btn");

            $("#TaskJsonFileForm").submit();
        })

        $('#TaskJsonFileForm').submit(function(e) {
            console.log("submit");

            e.preventDefault();
            $.ajax({
                url: $(this).attr("action"),
                type: $(this).attr("method"),
                contentType: 'json',
                data: new FormData(this),
                processData: false,
                contentType: false,
                success: function (data, status)
                {
                    console.log(data);
                    //zrobićżeby tobył callbackiem
                    self.setupImportedTasksTable();
                },
                error: function (xhr, desc, err)
                {
                    console.log(xhr);

                }
            });     
        }); 
    }

    window.onresize = self.resizeWindow;

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
                //TODO 2021-03-27 poprawić, żeby nie byłalert tylko tekst nad przyciskiem albo popout
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
                }
                callback(data);
            },
            error: function(jqXHR, status, err) {   
                if (self.debug) {
                    console.warn("ajaxGetImpotedTasksArray error");    
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
                }
                callback(data);
            },
            error: function(jqXHR, status, err) {     
                if (self.debug) {
                    console.warn("ajaxGetImportedTaskByID error"); 
                }  
            }
        });
    }
    /*     ajax http actions       */
    self.sendAjaxTask = (task, callback) => {
        /* /api/v1/tasks/imported POST - dodanie nowego zadania przez JSON */
        var send = task;
        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/tasks/imported",
            contentType: "application/json",
            data     : JSON.stringify(send),
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxTask success");
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxTask error");
                }
            }
        });
    }

    self.sendAjaxTaskFile = (tasks, callback) => {
        /* /api/v1/tasks/imported/json/file POST - dodanie nowego zadania przez JSON */
        var send = tasks;

        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/tasks/imported/json/file",
            contentType: "application/json",
            data     : JSON.stringify(send),
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxTask success");
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxTask error");
                }
            }
        });
    }

    self.sendAjaxEditTask = (task, taskID, callback) => {

        var send = task;
        $.ajax({
            type     : "PUT",
            cache    : false,
            url      : "/api/v1/tasks/imported/" + taskID,
            contentType: "application/json",
            data     : JSON.stringify(send),
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxEditTask success");
                }
                self.editedTaskIds.push(taskID); //dziwny wymysł żeby to du dawać
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("sendAjaxEditTask error");
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
                }
                //usuwam tego taska z tablic jeśli jest tam nadal
                self.allTaskIds = self.allTaskIds.filter(t => t !== taskID); // will return ['A', 'C']
                self.oldTaskIds = self.oldTaskIds.filter(t => t !== taskID);
                self.newTaskIds = self.newTaskIds.filter(t => t !== taskID);
                self.editedTaskIds = self.editedTaskIds.filter(t => t !== taskID);

                //jeśli usuwam tego co edytuje to chowam przycisk
                if ( self.lastEditedTaskID === taskID) {
                    console.log(taskID)
                    console.log(self.lastEditedTaskID)
                    self.focusedTaskID = undefined;
                    self.lastEditedTaskID = undefined;
                    if($("#btnSaveEditedTask").length > 0)
                        $("#btnSaveEditedTask").hide();
                }

                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxDeleteTask error");
                }
            }
        });
    }

    self.ajaxDeleteAllTasks = () => {
        
        $.ajax({
            type     : "DELETE",
            cache    : false,
            url      : "/api/v1/tasks/imported",
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("ajaxDeleteAllTasks success");
                }
                self.deleteAllTasksFromTableVisually();
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("ajaxDeleteAllTasks error");
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

            /*#gameBottom	*/
            var bottomDiv = $("#gameBottom");
            if (bottomDiv.length > 0) {
                if (!bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                    bottomDiv.addClass("sidepanelClassForBottonAndTasks");
                }
            }

            //nie zawija się wybór wariantu
            // var variantNavbar = $("#variantNavbar");
            // if ( variantNavbar.length > 0) {
            //     variantNavbar.addClass("variantNavbarFlexEnd");
            // }
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

            /*#gameBottom	*/
            var bottomDiv = $("#gameBottom");
            if (bottomDiv.length > 0) {
                if (bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                    bottomDiv.removeClass("sidepanelClassForBottonAndTasks");
                }
            }

            //nie zawija się wybór wariantu
            // var variantNavbar = $("#variantNavbar");
            // if ( variantNavbar.length > 0) {
            //     variantNavbar.removeClass("variantNavbarFlexEnd");
            // }
        }

        $("#closeNavButton").on("click", ()=> {
            closeNav();
        })

        $("#openNavButton").on("click", ()=> {
            openNav();
        })
    }
    /*  initalization  */
    self.taskCreatorInit(playerInfo_);
     
    return self;
}

//new 2021-04-05
/*moduł pozwalający na eksportowanie pliku z zadaniami*/
TaskCreatorLogic.exportTask = (fileData) => {
    
    //to musi być wykonane przed dodaniem pliku....

    
    // Add the following code if you want the name of the file appear on select
    $(".custom-file-input").on("change", function() {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });

    let isJson = (str) => {
        try {
            JSON.parse(str);
        } catch (e) {
            
            divOutput.innerHTML = "Niepoprawne dane: odczyt danych nie powiódł się.";
            graph.ClearAndDrawAxis();
            return false;
        }
        return true;
    }

    //dostarczanie pliku
    async function readFile (evt) {	
            
        var input = evt.target;
        var fileTypes = ['json', 'txt'];  //acceptable file types

        let array = await new Promise((resolve) => {
        
            if (input.files && input.files[0]) {
                var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
                isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types
                    
                if (isSuccess) { //yes
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        console.log('Prawidłowo odczytano plik');
                        switch (extension) {
                        case "json": resolve( JSON.parse(e.target.result) )
                            break;
                        case "txt": resolve( e.target.result)
                            break;
                        default: console.warn("cos poszło nie tak"); resolve( e.target.result);
                            break; 
                        }
                        
                    }
                    
                    reader.readAsText(input.files[0]);
                } else { //no
                
                }
            }
        });
        return array;
    }

    if (window.File && window.FileReader && window.FileList && window.Blob) {
        console.log("Obsługiwane są wszystkie interfejsy API plików.");

        document.getElementById('customExportFile').addEventListener('change', (evt) => {

            readFile(evt)
            .then(result => {
                console.log(result);
                //dataPlaceholder = result;
                
                /*TODO:
                    jakoś musze odczytać

                    wszystkie zadania po kolei importować które odczytam

                    //dodac nowe do tabeli
                */
                //start(result);
            });

        }, false);
    } else {
        alert('Interfejsy API plików nie są w pełni obsługiwane w tej przeglądarce.');
    }
    
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
            }
            ajaxReceiveAccountInfo(playerInfo)
            //TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug);
        },
        error: function(jqXHR, status, err) {
            if (debug){
                console.warn("ajaxReceiveWhoAmI error");
            }
        }
        });
    }

    var ajaxReceiveAccountInfo = ( playerInfo ) => {

        $.ajax({
          type     : "GET",
          cache    : false,
          url      : "/api/v1/account/info",
          contentType: "application/json",
          success: function(accountInfo, textStatus, jqXHR) {
              if (debug) {
                  console.log("ajaxReceiveAccountInfo success");
                  console.log(accountInfo);
              }
              playerInfo.roles = accountInfo.roles;
              TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug);
          },
          error: function(data, status, err) {
              if (debug) {
                console.warn("ajaxReceiveAccountInfo error");
                console.warn(data);
              }

          }
        });
    }
    
    ajaxReceiveWhoAmI();
    return TaskCreatorLogic.singleton;
}
