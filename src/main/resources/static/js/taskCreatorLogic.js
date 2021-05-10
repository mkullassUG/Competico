const TaskCreatorLogic = (playerInfo_, debug = false, $jq, myWindow, deps = {}) => {

    /* singleton */
    if (TaskCreatorLogic.singleton)
        return TaskCreatorLogic.singleton;
    var self = {};
    if (!TaskCreatorLogic.singleton && playerInfo_)
        TaskCreatorLogic.singleton = self;
    else if (!TaskCreatorLogic.singleton) {
        TaskCreatorLogic.getInstance(debug);
        return TaskCreatorLogic.singleton;
    }

    /* environment preparation */
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;
    if ( deps.TaskCreatorCore && typeof TaskCreatorCore == "undefined")
        TaskCreatorCore = deps.TaskCreatorCore;
    if ( deps.NavbarLogic && typeof NavbarLogic == "undefined")
        NavbarLogic = deps.NavbarLogic;

    /*       logic variables          */
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.focusedTaskID; //wybrane z tablicy zadań dla usuń / edytuj
    self.lastEditedTaskID; // wybrane po potwierdzeniu edycji danego zadania
    self.currentTaskVariant; // ustawianie dema
    self.currentVariant; //edytowanie
    self.CreatorCore;
    self.tablicaPolskichNazwTaskow;
    self.allTaskIds = [];
    self.oldTaskIds = [];
    self.newTaskIds = [];
    self.editedTaskIds = [];
    self.recentlyAddedTaskId;
    self.currentTaskNameString;
    self.currentlyEditingThisTaskObejct;
    self.isFirstTableSetup = true;

    /*       logic functions          */
    var TaskCreatorLogicInit = (playerInfo) => {
        /* TODO
            przygotuj start strony
        */
        if ( typeof TaskCreatorCore != "undefined" ) {
            self.CreatorCore = TaskCreatorCore(debug, deps);
            self.tablicaPolskichNazwTaskow = self.CreatorCore.GameCore.tablicaPolskichNazwTaskow;
        }

        var urlVariant =  window.location.hash.substr(1);
        if ( urlVariant.length != "")
            self.changeVariant(urlVariant);
        // else
        //     //TODO: wybór wariantu a nie ten dafaultowo
        //     self.changeVariant("WordFill");

        //setup wariant button listeners from dropdown menu
        prepareWariantButtonListeners(self.tablicaPolskichNazwTaskow);
            
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

            $("#"+key+"Div .taskEditTitle").html(self.tablicaPolskichNazwTaskow[key] + ( self.debug? " ("+key+")":""));
        })
        
        tooltipsUpdate();  

        if ( self.debug ) {
            $("#main_nav").find(".disabled").addClass("bg-danger").removeClass("disabled");
        }

        
        var setupSlider = (id) => {
            if ( self.debug )
                console.log(id);
            const slider = $("#" +id)[0];
            const min = slider.min;
            const max = slider.max;
            const value = slider.value;

            slider.style.background = `linear-gradient(to right, #007bff 0%, #007bff ${(value-min)/(max-min)*100}%, #DEE2E6 ${(value-min)/(max-min)*100}%, #DEE2E6 100%)`;

            function sliderChange() {
                this.style.background = `linear-gradient(to right, #007bff 0%, #007bff ${(this.value-this.min)/(this.max-this.min)*100}%, #DEE2E6 ${(this.value-this.min)/(this.max-this.min)*100}%, #DEE2E6 100%)`;
            };

            $(slider).on("input change", sliderChange);
        }

        for ( key in self.tablicaPolskichNazwTaskow) {
            //SETUP SLIDERS for taskDivs:
            setupSlider("customRange" + key);
        }

        //navbar preparation
        
        if (typeof NavbarLogic != "undefined")
            NavbarLogic.singleton = NavbarLogic(playerInfo, debug);

        //NEW!!!!!!
        if (typeof PageLanguageChanger != "undefined")
            PageLanguageChanger();
    }

    var tooltipsUpdate = () => {

        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }

    self.changeVariant = (variantString) => {

        if (debug)
            console.log("self.changeVariant: " + variantString);
        self.currentTaskNameString = variantString;
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

                if ( data.taskExists && data.taskExists == "false") {
                    displayInfoAnimation("Nie znaleziono obecnie edytowanego zadania.",false)
                    return;
                }
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
        $("#main_nav").find(`[href='/tasks/import/global/#${variantString}']`).closest("li").addClass("bg-success");
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

            //musiał bym ddoać więcej warunków jeśli chce, żeby edytowało jedno z listy importowanych zadań
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
        $("body").addClass('gameDemoBody');

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
        $("#gameInstruction").html("<h2>Tworzenie zadań</h2>");
        $("body").removeClass('gameDemoBody');

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
            $("html").height($(window.document).height());
            
            //hideBeforeLoadModal na telefonach chyba wychodzi poza bo ma 100% height
            // $(".hideBeforeLoadModal").height($(document).height());
        } else {
            //console.log("szerokośc okna 100%");
            $("html").height("100%");

            //hideBeforeLoadModal na telefonach chyba wychodzi poza bo ma 100% height
            // $(".hideBeforeLoadModal").height("100%");
        }
    }

    var displayInfoAnimation = (text, success = true) => {
        var failInfoDiv = $(`<div class="failSuccessInfo alert alert-`+(success?"success":"danger")+`">` + text + `</div>`)
        $("#bigChangeDiv").append(failInfoDiv)
    
        failInfoDiv.animate({
          top: "1%",
          opacity: 1
        }, 2000, function() {
          // Animation complete.
          setTimeout(function(){
            failInfoDiv.animate({
              top: "4%",
              opacity: 0
            }, 1000, function() {
              // Second Animation complete.
              failInfoDiv.remove();
            });
          },2000)
          
        });
    }

    /*       event listeners          */
    var prepareWariantButtonListeners = (tablicaPolskichNazwTaskow) => {

        var makeBtnListener = (wariantName) => {
            if ($("#btn" + wariantName).length)
                $("#btn" + wariantName).on("click",() => {
                    var wariantNameInner = wariantName;
                    if (self.debug)
                        console.log("btn" + wariantNameInner);
                    self.changeVariant(wariantNameInner);

                    // switch ( wariantNameInner ) {
                    //     case 'WordFill':
                    //         $("#wordFillDivTaskText").focus();
                    //         $(".dropdown-menu").removeClass("show");
                    //     break;
                    //     case "OptionSelect":
                    //         $("#OptionSelectDivTaskText").focus();
                    //         $(".dropdown-menu").removeClass("show");
                    //     break;
                    // }
                });

            if ($("."+ wariantName +"BtnClass").length)
                $("."+ wariantName +"BtnClass").on("click",() => {
                    var wariantNameInner = wariantName;
                    if (self.debug)
                        console.log(wariantNameInner + "BtnClass");
                    self.changeVariant(wariantNameInner);

                    // switch ( wariantNameInner ) {
                    //     case 'WordFill':
                    //         $("#wordFillDivTaskText").focus();
                    //         $(".dropdown-menu").removeClass("show");
                    //     break;
                    //     case "OptionSelect":
                    //         $("#OptionSelectDivTaskText").focus();
                    //         $(".dropdown-menu").removeClass("show");
                    //     break;
                    // }
                });
        }
        for ( wariantName in tablicaPolskichNazwTaskow)
            makeBtnListener(wariantName);
    }

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
        $("#btnDemoTaskEnd").on("click", (e) => {
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

            //nie wspierane przez esprima.js
            //async pojawił się dopwiro w ECMAScript 2017
            //2021-05-09 usunąłem async i await
            function readFile (evt) {	
                    
                var Input = evt.target;
                var fileTypes = ['json'];  //acceptable file types

                return new Promise((resolve) => {
                
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

            var Input = evt.target;
            var fileTypes = ['json'];  //acceptable file types


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
    }

    if ($("#btnSendImportTasks").length) {

        $("#btnSendImportTasks").on('click',(e)=>{

            $("#TaskJsonFileForm").submit();
        })

        $('#TaskJsonFileForm').submit(function(e) {

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
                    if (self.debug)
                        console.log(data);
                    //zrobićżeby tobył callbackiem
                    self.setupImportedTasksTable();
                },
                error: function (xhr, desc, err)
                {
                    if (self.debug)
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
                // const a = document.createElement('a');
                const a = $('<a>');
                a[0].style.display = 'none';
                a[0].href = url;
                // the filename you want
                a[0].download = 'importedTasksFile.json';
                window.document.body.appendChild(a[0]);
                a[0].click();
                window.URL.revokeObjectURL(url);
                //TODO 2021-03-27 poprawić, żeby nie był alert tylko tekst nad przyciskiem albo popout
                // alert('your file has downloaded!'); // or you know, something with better UX...
                displayInfoAnimation("Pomyślnie pobrano plik.", true);
            })
            .catch(() => displayInfoAnimation("Nie pobrano pliku.", false));
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
                    console.log("ajaxGetImpotedTasksArray success");
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
                    console.warn(data);
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
                displayInfoAnimation("Pomyślnie zapisano.", true);
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxTask error");
                }
                displayInfoAnimation("Nie zapisano.", false);
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
                displayInfoAnimation("Pomyślnie zaimportowano zadania.", true);
                callback(data);
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxTask error");
                }
                displayInfoAnimation("Nie zaimportowano zadań.", false);
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
                displayInfoAnimation("Pomyślnie nadpisano.", true);
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("sendAjaxEditTask error");
                }
                displayInfoAnimation("Nie nadpisano, spróbuj zapisać nowe.", false);
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
                    if( self.debug) {
                        console.log(taskID)
                        console.log(self.lastEditedTaskID)
                    }

                    self.focusedTaskID = undefined;
                    self.lastEditedTaskID = undefined;
                    if($("#btnSaveEditedTask").length > 0)
                        $("#btnSaveEditedTask").hide();
                }
                displayInfoAnimation("Pomyślnie usunięto.", true);

                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("sendAjaxDeleteTask error");
                }
                displayInfoAnimation("Nie usunięto.", false);
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

                //usuwam tego taska z tablic jeśli jest tam nadal
                self.allTaskIds = [];
                self.oldTaskIds = [];
                self.newTaskIds = [];
                self.editedTaskIds = [];

                //jeśli usuwam tego co edytuje to chowam przycisk
                self.focusedTaskID = undefined;
                self.lastEditedTaskID = undefined;
                if($("#btnSaveEditedTask").length > 0)
                    $("#btnSaveEditedTask").hide();

                displayInfoAnimation("Pomyślnie usunięto.", true);
                self.deleteAllTasksFromTableVisually();
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                    console.warn("ajaxDeleteAllTasks error");
                }
                displayInfoAnimation("Nie usunięto.", false);
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
            var allText = $(window.document).find(".taskTextTextarea");
            
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
        $(window.document).ready(function(){
            textareaAutoscroll();
        });

        /* collapse side-panel*/
        /* Set the width of the sidebar to 250px (show it) */
        self.isNavOpened = false;

        function openNav() {

            window.document.getElementById("mySidepanel").style.width = "";
           //document.getElementById("mySidepanel").removeProperty('style');
            
            /*to wtedy zmieniam ustawienie elementów od edycji zadań*/

            /*#gameInstruction > h2*/
            var gameInstruction = $("#gameInstruction");
            if (gameInstruction.length > 0) {
                if (!gameInstruction.hasClass("sidepanelClassForGameInstructionH2")) {
                    gameInstruction.addClass("sidepanelClassForGameInstructionH2");
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

            self.isNavOpened = true;
            $(".dropdown-menu").addClass("SideNavOpenClass");
            $(".nav-item .submenu").addClass("SideNavOpenClass");
            $(".nav-item .submenu-left").addClass("SideNavOpenClass");
            $(".taskDiv").addClass("SideNavOpenClass");
        }
        
        /* Set the width of the sidebar to 0 (hide it) */
        function closeNav() {

            window.document.getElementById("mySidepanel").style.width = "0";
            
            /*#gameInstruction > h2*/
            var gameInstruction = $("#gameInstruction");
            if (gameInstruction.length > 0) {
                if (gameInstruction.hasClass("sidepanelClassForGameInstructionH2")) {
                    gameInstruction.removeClass("sidepanelClassForGameInstructionH2");
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

            self.isNavOpened = false;
            $(".dropdown-menu").removeClass("SideNavOpenClass");
            $(".nav-item .submenu").removeClass("SideNavOpenClass");
            $(".nav-item .submenu-left").removeClass("SideNavOpenClass");
            $(".taskDiv").removeClass("SideNavOpenClass");
            //naprawianie blurra tekstu
        }

        $("#closeNavButton").on("click", ()=> {
            closeNav();
        })

        $("#openNavButton").on("click", ()=> {
            openNav();
        })
    }
    /*  initalization  */
    TaskCreatorLogicInit(playerInfo_);
     
    return self;
}

TaskCreatorLogic.getInstance = (debug = false, $jq, myWindow, deps = {}, cbTest) => {

    //for testing
    if ( $jq && typeof $ == "undefined")
        $ = $jq;
    if ( myWindow && typeof window == "undefined")
        window = myWindow;

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

              if ( cbTest )
                  cbTest("success");

              TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug, $, window, deps);
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

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskCreatorLogic};